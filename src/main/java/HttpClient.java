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

	private Object getResponse() throws Exception{
		
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
		Object object = mapper.readValue(content.toString(), Object.class);
		return object;
	}

	private void closeConnection() throws Exception{
		con.disconnect();
	}

	public Object get(String address) throws Exception{
		openConnection(address, "GET");
		Account account = (Account)getResponse();
		closeConnection();
		return account;
	}
}
	


