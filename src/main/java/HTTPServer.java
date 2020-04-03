import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class HTTPServer implements Runnable {

    private static final String ACCOUNTFOLDER = "Accounts";
    private static final ObjectMapper mapper = new ObjectMapper();
    // port to listen connection
    private static final int PORT = 8080;

    // Keeping track of total amount of threads
    private static int threads = 0;

    // Client Connection via Socket Class
    private Socket connect;

    private List<String> usedFiles = new LinkedList<>();

    private HTTPServer(Socket c) {
        connect = c;
    }

    public static void main(String[] args) {
        try {
            // Server Setup
            System.out.println("Performing setup of server... ");
            ServerSocket serverConnect = serverSetup();

            // Main loop of main thread
            while (true) {
                HTTPServer myServer = new HTTPServer(serverConnect.accept());

                System.out.println("\n----------------------------------------------------");
                System.out.println("New connection opened. (" + new Date() + ")");

                // Creating new thread to handle incoming connection
                Thread thread = new Thread(myServer);
                thread.start();
            }

        } catch (Exception e) {
            System.err.println("Server Connection error : " + e.getMessage());
        }
    }

    static private ServerSocket serverSetup() throws IOException {
        System.out.println("Checking existence of accounts folder");
        File directory = new File(ACCOUNTFOLDER);
        if (!directory.exists()) {
            directory.mkdir();
            System.out.println("No accounts folder found! Creating new folder and files...");
            Account account1 = new Account("Siemen", 0, "Oosterveldaan 3", 17, "Lies");
            Account account2 = new Account("Arne", 15000, "Korte kopstraat 70", 2, "Cassandra");
            Account account3 = new Account("Gliesje", 15, "Oosterveldlaan 3", 50, "Yelena");

            mapper.writeValue(new File(ACCOUNTFOLDER, "/Siemen.json"), account1);
            mapper.writeValue(new File(ACCOUNTFOLDER, "/Arne.json"), account2);
            mapper.writeValue(new File(ACCOUNTFOLDER, "/Gliesje.json"), account3);
        } else {
            System.out.println("Accounts folder found!");
        }
        System.out.println("Setup completed! Server started.\nListening for connections on port: " + PORT + "...\n");
        return new ServerSocket(PORT);
    }

    public void run() {
        // Managing of new connection by dedicated thread
        System.out.println("Starting new thread for connection from " + connect.getInetAddress() + ". Total running threads: " + ++threads);
        BufferedReader in;
        PrintWriter out;
        BufferedOutputStream dataOut;

        try {
            in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
            out = new PrintWriter(connect.getOutputStream());
            dataOut = new BufferedOutputStream(connect.getOutputStream());

            // Reading incoming request and parsing input to get the command
            String input = in.readLine();
            System.out.println("Input: " + input);
            String[] split1 = input.split(" ");
            String[] split2 = split1[1].split("/");

            switch (split2[1].trim()) {
                case ("info"):
                    infoInput(out, dataOut, split2[2] + ".json");
                    break;
                case ("changeBalance"):
                    infoChangeBalance(out, dataOut, split2[2] + ".json", split2[3]);
                    break;
                case ("add"):

                    if (split1[0].equals("POST")) {
                        new InputStreamReader(connect.getInputStream());
                        String inputLine;
                        StringBuffer content = new StringBuffer();

                        while ((inputLine = in.readLine()) != null) {
                            content.append(inputLine);
                        }

                        System.out.println(content.toString().split("\\{")[1]);

                        try {
                            Account account = mapper.readValue(content.toString().split("\\{")[1], Account.class);
                            infoAdd(out, account);
                        } catch (JsonParseException e ){
                            errorMessage(out, "400", "Bad Request");
                        }
                    }

                    break;
                default:
                    errorMessage(out, "400", "Bad Request");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        threads--;
    }

    private void errorMessage(PrintWriter out, String errorCode, String errorMessage) {
        System.out.println("Error while processing request! Error code and message: " + errorCode + " " + errorMessage);
        out.println("HTTP/1.1 " + errorCode + errorMessage);
        out.println("Server: Java HTTP Server from Arne");
        out.println("Date: " + new Date());
        out.println("Content-type: json");
        out.println();
        out.flush();
    }

    private void infoInput(PrintWriter out, OutputStream dataOut, String account) {
        File accountFile = new File(ACCOUNTFOLDER, account);
        if (validAccount(out, accountFile)) {
            try {
                while (usedFiles.contains(account)){
                    System.out.println("Account " + account + " is already in use by another thread!");
                    Thread.sleep(20);
                }
                usedFiles.add(account);
                System.out.println("Sending account information for account: " + account);
                ObjectMapper objMapper = new ObjectMapper();
                Account accountClass;
                accountClass = objMapper.readValue(accountFile, Account.class);
                out.println("HTTP/1.1 200 OK");
                out.println("Server: Java HTTP Server from Arne");
                out.println("Date: " + new Date());
                out.println("Content-type: json");
                out.println();
                out.flush();
                mapper.writeValue(dataOut, accountClass);
                dataOut.flush();
                usedFiles.remove(account);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void infoChangeBalance(PrintWriter out, OutputStream dataOut, String account, String amount) {
        File accountFile = new File(ACCOUNTFOLDER, account);
        if (validAccount(out, accountFile)) {
            try {
                while (usedFiles.contains(account)){
                    System.out.println("Account " + account + " is already in use by another thread!");
                    Thread.sleep(20);
                }
                usedFiles.add(account);
                System.out.println("Changing balance of account " + account + "with " + amount);
                Account accountClass;
                accountClass = mapper.readValue(accountFile, Account.class);
                if (amount.charAt(0) == '-') {
                    accountClass.setBalance(accountClass.getBalance() - Integer.parseInt(amount.replaceAll("\\D+", "")));
                } else if (amount.charAt(0) == '+') {
                    accountClass.setBalance(accountClass.getBalance() + Integer.parseInt(amount.replaceAll("\\D+", "")));
                } else {
                    errorMessage(out,"400", "Bad Request");
                }
                mapper.writeValue(accountFile, accountClass);
                out.println("HTTP/1.1 200 OK");
                out.println("Server: Java HTTP Server from Arne");
                out.println("Date: " + new Date());
                out.println("Content-type: json");
                out.println();
                out.flush();
                usedFiles.remove(account);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void infoAdd(PrintWriter out, Account account) {
        File accountFile = new File(ACCOUNTFOLDER, account.getName() + ".json");
        if (!accountFile.isFile()) {
            try {
                mapper.writeValue(accountFile, account);
                out.println("HTTP/1.1 200 OK");
                out.println("Server: Java HTTP Server from Arne");
                out.println("Date: " + new Date());
                out.println("Content-type: json");
                out.println();
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            errorMessage(out,"409", "Conflict");
        }
    }

    private boolean validAccount(PrintWriter out, File accountFile) {
        if (!accountFile.isFile()) {
            System.out.println("Account " + accountFile.getName() + " does not exist");
            errorMessage(out,"404", "Not Found");
            return false;
        } else {
            return true;
        }
    }
}
