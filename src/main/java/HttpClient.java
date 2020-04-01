import java.net.*;
import java.io.*;

class HttpClient{
	
	private HttpURLConnection con;

	public void openConnection(String adress) throws Exception{
	
		URL url = new URL(adress);
		con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		con.setConnectTimeout(5000);
		con.setReadTimeout(5000);
	}

	public String getResponse() throws Exception{
		BufferedReader in = new BufferedReader(
  		new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer content = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
    		content.append(inputLine);
		}
		in.close();
		return content.toString();
	}

	public void closeConnection() throws Exception{
		con.disconnect();
	}
}
	


