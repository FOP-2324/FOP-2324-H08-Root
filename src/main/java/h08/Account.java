package h08;

import h08.exceptions.IllegalNameException;

public class Account {

    private String firstName;
    private String lastName;

    private final long iban;
    private double balance;

    private Bank bank;
    private final Customer customer;

    private final TransactionHistory history;

    public Account(String firstName, String lastName, long iban, double balance, Bank bank, Customer customer, TransactionHistory history){
        this.customer = customer;
        this.history = history;
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
            throw new IllegalNameException(this);
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if(firstName == null)
            throw new IllegalNameException(this);
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

    public Customer getCustomer() {
        return customer;
    }

    public TransactionHistory getHistory() {
        return history;
    }

    @Override
    public String toString() {
        return "Account{" +
            "firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", iban=" + iban +
            ", balance=" + balance +
            ", bank=" + bank +
            '}';
    }
}
