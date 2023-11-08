package h08;

import static org.tudalgo.algoutils.student.Student.crash;

public class Account {

    private final Customer customer;

    private String firstName;
    private String lastName;

    private final long iban;
    private double balance;

    private Bank bank;

    private final TransactionHistory history;


    public Account(Customer customer, String firstName, String lastName, long iban, double balance, Bank bank, TransactionHistory history) {

        assert firstName != null;
        assert lastName != null;
        assert bank != null;

        this.customer = customer;
        this.firstName = firstName;
        this.lastName = lastName;
        this.iban = iban;
        this.balance = balance;
        this.bank = bank;
        this.history = history;

        bank.addAccount(this);
        bank.depositWithAssert(iban,balance);
    }
    public Account(Customer customer, String firstName, String lastName, double balance, Bank bank, TransactionHistory history) {

        assert firstName != null;
        assert lastName != null;
        assert bank != null;

        this.bank = bank;
        this.customer = customer;
        this.firstName = firstName;
        this.lastName = lastName;
        this.iban = generateIban(System.currentTimeMillis());
        this.balance = balance;
        this.history = history;

        bank.addAccount(this);
        bank.depositWithAssert(iban,balance);
    }




    private long generateIban(long seed){
        //TODO: implement
        //crash("not implement");

        long nameHash = String.join("",getFirstName(),getLastName()).hashCode();
        long iban = nameHash * seed;
        if(bank.ibanIsAlreadyUsed(iban)) {
            iban = generateIban(iban) << 33;
        }
        return Math.abs(iban);
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public TransactionHistory getHistory() {
        return history;
    }
}
