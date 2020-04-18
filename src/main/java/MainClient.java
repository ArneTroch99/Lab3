class MainClient{
	
	public static void main(String[] args) throws Exception{
		
		HttpClient client = new HttpClient("http://10.0.13.14:8081");
		
		Account account = client.getAccount("Siemen");
		System.out.println(account.toString());

		account.setName("Benny");
		account.setBalance(2147483647);
		client.addAccount(account);
	
		client.changeBalance("Siemen", "plus", 20000);
	}
}
