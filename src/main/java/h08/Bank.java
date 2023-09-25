package h08;

import h08.exceptions.AccountException;


public class Bank {

    private String name;
    private int BIC;
    private Account[] accounts;

    public Bank(String name, int BIC, Account[] accounts) {
        this.name = name;
        this.BIC = BIC;
        this.accounts = accounts;
    }

    public void withdrawWithAssert(long IBAN, double amount){

        assert amount > 0;

        int index = getAccountIndex(IBAN);

        assert index >= 0;

        double newBalance = accounts[index].getBalance() - amount;

        accounts[index].setBalance(newBalance);

    }

    public void withdrawWithRTE(long IBAN, double amount){

        if(amount <= 0)
            throw new RuntimeException("amount can't be zero or negative!");

        int index = getAccountIndex(IBAN);

        if(index < 0)
            throw new RuntimeException("Cannot find IBAN!");

        double newBalance = accounts[index].getBalance() - amount;

        accounts[index].setBalance(newBalance);
    }

    public void depositWithAssert(long IBAN, double amount){

        assert amount > 0;

        int index = getAccountIndex(IBAN);

        assert index >= 0;

        double newBalance = accounts[index].getBalance() + amount;

        accounts[index].setBalance(newBalance);
    }

    public void depositWithRTE(long IBAN, double amount){
        if(amount <= 0)
            throw new RuntimeException("amount can't be zero or negative!");

        int index = getAccountIndex(IBAN);

        if(index < 0)
            throw new RuntimeException("Cannot find IBAN!");

        double newBalance = accounts[index].getBalance() + amount;
        accounts[index].setBalance(newBalance);

    }

    public void transfer(Account sender, Account receiver){

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBIC() {
        return BIC;
    }

    public void setBIC(int BIC) {
        this.BIC = BIC;
    }

    public void setAccounts(Account[] accounts) {
        this.accounts = accounts;
    }

    public Account[] getAccounts() {
        return accounts;
    }

    private int getAccountIndex(long IBAN){
        for (int i = 0; i < accounts.length; i++) {
            if(accounts[i].getIBAN() == IBAN)
                return i;
        }

        return -1;
    }

    public void addAccount(Account account){
        if(account == null)
            throw new AccountException("Account can't be null!");
        for (int i = 0; i < accounts.length; i++) {
            if(accounts[i] == null) {
                accounts[i] = account;
                return;
            }
        }
        throw new AccountException("Too many accounts!");
    }

    public void printAccounts(){
        for (Account a :
            accounts) {
            if(a !=null)
                System.out.println(a.getFirstName() + " | " + a.getLastName() + " | " + a.getBank().getName() + " | " + a.getBalance());

        }
    }


}
