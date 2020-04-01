class MainClient{
	
	public static void main(String[] args) throws Exception{
		
		HttpClient client = new HttpClient();
		
		Account account = (Account) client.get("http://10.0.13.14:8080/Siemen");
		System.out.println(account.getName() + "  " + account.getBalance());
	}
}
