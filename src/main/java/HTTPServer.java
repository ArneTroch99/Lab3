import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class HTTPServer implements Runnable {

    private static final String ACCOUNTFOLDER = "Accounts";
    private static final ObjectMapper mapper = new ObjectMapper();
    // port to listen connection
    private static final int PORT = 8080;

    // verbose mode
    private static final boolean verbose = true;

    private static int threads = 0;

    // Client Connection via Socket Class
    private Socket connect;

    private HTTPServer(Socket c) {
        connect = c;
    }

    public static void main(String[] args) {
        try {

            File directory = new File(ACCOUNTFOLDER);
            if (!directory.exists()) {
                directory.mkdir();
            }

            if (directory.createNewFile()) {
                System.out.println("Creating accounts file...");
                Account account1 = new Account("Siemen", 0, "Oosterveldaan 3", 17, "Lies");
                Account account2 = new Account("Arne", 15000, "Korte kopstraat 70", 2, "Cassandra");
                Account account3 = new Account("Gliesje", 15, "Oosterveldlaan 3", 50, "Yelena");

                mapper.writeValue(new File(ACCOUNTFOLDER, "Siemen.json"), account1);
                mapper.writeValue(new File(ACCOUNTFOLDER, "Arne.json"), account2);
                mapper.writeValue(new File(ACCOUNTFOLDER, "Gliesje.json"), account3);
            } else {
                System.out.println("Accounts file already exists");
            }

            ServerSocket serverConnect = new ServerSocket(PORT);
            System.out.println("Server started.\nListening for connections on port : " + PORT + " ...\n");

            // we listen until user halts server execution
            while (true) {
                HTTPServer myServer = new HTTPServer(serverConnect.accept());

                if (verbose) {
                    System.out.println("----------------------------------------------------");
                    System.out.println("Connection opened. (" + new Date() + ")");
                }

                // create dedicated thread to manage the client connection
                Thread thread = new Thread(myServer);
                thread.start();
            }

        } catch (Exception e) {
            System.err.println("Server Connection error : " + e.getMessage());
        }
    }

    public void run() {
        System.out.println("New thread, running threads: " + ++threads);
        // we manage our particular client connection
        BufferedReader in = null;
        PrintWriter out = null;
        BufferedOutputStream dataOut = null;

        try {
            // we read characters from the client via input stream on the socket
            in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
            // we get character output stream to client (for headers)
            out = new PrintWriter(connect.getOutputStream());
            // get binary output stream to client (for requested data)
            dataOut = new BufferedOutputStream(connect.getOutputStream());

            boolean balance = true;

            // get first line of the request from the client
            String input = in.readLine();
            String[] splittedInput = input.split("/");
            String account = "";
            String amount = "";
            if (splittedInput.length == 2) {
                account = splittedInput[1].split(" ")[0];
                System.out.println("Sending information for account " + account);
            } else if (splittedInput.length == 3) {
                balance = false;
                account = splittedInput[1].split(" ")[0];
                amount = splittedInput[2].split(" ")[0];
                System.out.println("Changing balance for account " + account + "with " + amount);
            } else {
                System.out.println("Wrong input: " + input);
                errorMessage(out, dataOut, "400", "Bad Request");
            }

            File accountFile = new File(ACCOUNTFOLDER, account);
            // File -and account- doesn't exist
            if (accountFile.createNewFile()) {
                System.out.println("Account does not exist");
                errorMessage(out, dataOut, "404", "Not Found");
            } else {

                // Returnen van de balance van een account
                if (balance) {
                    Account accountClass = mapper.readValue(accountFile, Account.class);
                    out.println("HTTP/1.1 200 OK");
                    out.println("Server: Java HTTP Server from Arne");
                    out.println("Date: " + new Date());
                    out.println("Content-type: json");
                    // out.println("Content-length: ");
                    out.println(); // blank line between headers and content, very important !
                    out.flush(); // flush character output stream buffer
                    mapper.writeValue(dataOut, accountClass);
                    dataOut.flush();
                } else {
                    Account accountClass = mapper.readValue(accountFile, Account.class);
                    if (amount.charAt(0) == '-') {
                        accountClass.setBalance(accountClass.getBalance() - Integer.parseInt(amount.replaceAll("\\D+", "")));
                    } else if (amount.charAt(0) == '+') {
                        accountClass.setBalance(accountClass.getBalance() + Integer.parseInt(amount.replaceAll("\\D+", "")));
                    } else {
                        errorMessage(out, dataOut, "400", "Bad Request");
                    }
                    mapper.writeValue(accountFile, accountClass);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        threads--;
    }

    private byte[] readFileData(File file, int fileLength) throws IOException {
        FileInputStream fileIn = null;
        byte[] fileData = new byte[fileLength];

        try {
            fileIn = new FileInputStream(file);
            fileIn.read(fileData);
        } finally {
            if (fileIn != null)
                fileIn.close();
        }

        return fileData;
    }


    private void errorMessage(PrintWriter out, OutputStream dataOut, String errorCode, String errorMessage) throws IOException {
        System.out.println("Error while processing request! Error code and message: " + errorCode + " " + errorMessage);
        out.println("HTTP/1.1 " + errorCode + errorMessage);
        out.println("Server: Java HTTP Server from Arne");
        out.println("Date: " + new Date());
        out.println("Content-type: json");
        out.println();
        out.flush();

        String json = "{ \"Message\" : \"" + errorMessage + "\"}";


        dataOut.write(json.getBytes(), 0, json.getBytes().length);
        dataOut.flush();
    }
}