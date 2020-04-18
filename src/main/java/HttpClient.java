import java.net.*;
import java.io.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ObjectMapper;

class HttpClient{
	
	private HttpURLConnection con;
	private String address;

	public HttpClient(String address){
		this.address = address;
	}
	
	private void openConnection(String request, String method) throws Exception{

		URL url = new URL(request);
		con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod(method);
		con.setConnectTimeout(5000);
		con.setReadTimeout(5000);
	}

	public Account getAccount(String name) throws Exception{
		
		String request = address + "/getAccount?account="+name;
		openConnection(request, "GET");

		BufferedReader in = new BufferedReader(
  		new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer content = new StringBuffer();
		
		while ((inputLine = in.readLine()) != null) {
    		content.append(inputLine);
		}
		
		in.close();
		con.disconnect();

		// create object mapper instance
		ObjectMapper mapper = new ObjectMapper();

		// convert JSON string to object
		Account object = mapper.readValue(content.toString(), Account.class);
		return object;
	}

	public int addAccount(Account account) throws Exception{
		
		String request = address + "/addAccount?name="+account.getName()+"&balance="+account.getBalance();
		openConnection(request, "POST");
		con.disconnect();
		return con.getResponseCode();
	}	
	
	public int changeBalance(String name, String type, int amount) throws Exception{

		String request = address + "/changeBalance?account="+name+"&type="+type+"&amount="+String.valueOf(amount);
		openConnection(request, "PUT");
		con.disconnect();
		return con.getResponseCode();
	}
	
}

