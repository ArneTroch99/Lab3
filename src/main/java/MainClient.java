class MainClient{
	
	public static void main(String[] args) throws Exception{
		HttpClient client = new HttpClient();

		client.openConnection("http://dummy.restapiexample.com/api/v1/employees");	
		System.out.println(client.getResponse());
		client.closeConnection();
	}
}
