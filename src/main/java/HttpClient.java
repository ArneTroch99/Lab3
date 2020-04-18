import java.net.*;
import java.io.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ObjectMapper;

class HttpClient{
	
	private HttpURLConnection con;

	private void openConnection(String request, String method) throws Exception{

		URL url = new URL(request);
		con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod(method);
		con.setConnectTimeout(5000);
		con.setReadTimeout(5000);
	}

	public Account getAccount(String address, String name) throws Exception{
		
		String request = address + "/getAccount?account="+name;
		openConnection(address, "GET");
		BufferedReader in = new BufferedReader(
  		new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer content = new StringBuffer();
		
		while ((inputLine = in.readLine()) != null) {
    		content.append(inputLine);
		}
		
		in.close();
		con.close();
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
		//con.setRequestProperty("Accept", "text/plain");
		con.setDoOutput(true);
			
		try(OutputStream os = con.getOutputStream()) {
 		byte[] input = jsonInputString.getBytes("utf-8");
    		os.write(input, 0, input.length);           
		}
		//con.getInputStream().close();
		//return con.getResponseCode();
		return con.getResponseCode();
	}	
}
	


