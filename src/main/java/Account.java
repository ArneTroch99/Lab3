public class Account {

    private String name;
    private int balance;
    private String adress;
    private int children;
    private String partner;

    public Account(String name, int balance, String adress, int children, String partner) {
        this.name = name;
        this.balance = balance;
        this.adress = adress;
        this.children = children;
        this.partner = partner;
    }

    public String getName() {
        return name;
    }

    public int getBalance() {
        return balance;
    }

    public String getAdress() {
        return adress;
    }

    public int getChildren() {
        return children;
    }

    public String getPartner() {
        return partner;
    }
}
