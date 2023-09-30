package h08;

import h08.exceptions.IllegalNameException;

public class Account {

    private String firstName;
    private String lastName;
    private final long iban;
    private double balance;
    private Bank bank;

    public Account(String firstName, String lastName, long iban, double balance, Bank bank){
        assert firstName != null;
        assert lastName != null;

        this.firstName = firstName;
        this.lastName = lastName;
        this.iban = iban;
        this.bank = bank;
        bank.addAccount(this);
        bank.depositWithAssert(iban, balance);



    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if(firstName == null)
            throw new IllegalNameException();
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if(firstName == null)
            throw new IllegalNameException();
        this.lastName = lastName;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public long getIban() {
        return iban;
    }



    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
