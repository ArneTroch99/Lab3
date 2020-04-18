class MainClient{
	
	public static void main(String[] args) throws Exception{
		
		HttpClient client = new HttpClient();
		
		Account account = client.getAccount("http://10.0.13.14:8080", "Siemen");
		System.out.println(account.toString);
	}
}
