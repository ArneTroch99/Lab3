import java.net.*;
import java.io.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ObjectMapper;

class HttpClient{
	
	private HttpURLConnection con;

	private void openConnection(String address, String HttpMethod) throws Exception{

		URL url = new URL(address);
		con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod(HttpMethod);
		con.setConnectTimeout(5000);
		con.setReadTimeout(5000);
	}

	private Account getResponse() throws Exception{
		
		BufferedReader in = new BufferedReader(
  		new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer content = new StringBuffer();
		
		while ((inputLine = in.readLine()) != null) {
    		content.append(inputLine);
		}
		
		in.close();
		// create object mapper instance
		ObjectMapper mapper = new ObjectMapper();

		// convert JSON string to `JsonNode`
		Account object = mapper.readValue(content.toString(), Account.class);
		return object;
	}

	private int sendAccount(Account account) throws Exception{
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonInputString = objectMapper.writeValueAsString(account);

		con.setRequestProperty("Content-Type", "application/json; utf-8");
		con.setDoOutput(true);
		
		try(OutputStream os = con.getOutputStream()) {
 		byte[] input = jsonInputString.getBytes("utf-8");
    		os.write(input, 0, input.length);           
		}
		return con.getResponseCode();	
	} 

	private void closeConnection() throws Exception{
		con.disconnect();
	}

	public Account get(String address) throws Exception{
		openConnection(address, "GET");
		
		Account account = getResponse();
		closeConnection();
		return account;
	}

	public void post(String address, Account account)throws Exception{
		openConnection(address, "POST");
		int error = sendAccount(account);
		closeConnection();
		
		if (error == 200){System.out.println("File sucesfully added");return;}
		System.out.println("File not added! Code: "+error);
	}	
}
	


