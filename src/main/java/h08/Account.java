package h08;

import h08.exceptions.BankException;
import h08.exceptions.IllegalNameException;

public class Account {

    private String firstName;
    private String lastName;
    private long IBAN;
    private double balance;
    private final Bank bank;

    public Account(String firstName, String lastName, long IBAN, double balance, Bank bank){
        assert firstName != null;
        assert lastName != null;

        this.firstName = firstName;
        this.lastName = lastName;
        this.IBAN = IBAN;
        this.bank = bank;
        bank.addAccount(this);
        this.balance = balance;
        //bank.depositWithAssert(IBAN, balance);



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

    public long getIBAN() {
        return IBAN;
    }

    public void setIBAN(long IBAN) {
        if(IBAN <= 0)
            throw new RuntimeException("Invalid IBAN!");
        this.IBAN = IBAN;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
