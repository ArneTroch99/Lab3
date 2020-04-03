class MainClient{
	
	public static void main(String[] args) throws Exception{
		
		HttpClient client = new HttpClient();
		
		Account account = client.get("http://10.0.13.14:8080/info/siemen");
		System.out.println(account.getName() + "  " + account.getBalance());
		
		account = new Account("Ronny", 20, "TestStraat 1", 3, "Benny");
		client.post("http://10.0.13.14:8080/add", account);
	}
}
