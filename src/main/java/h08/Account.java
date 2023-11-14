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

    //TODO: exercise for students

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

    //TODO: exercise for students
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



    //TODO: exercise for students

    private long generateIban(long seed){
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

    //TODO: exercise for students
    @Override
    public String toString() {
        return "Account{" +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", iban=" + iban +
            ", balance=" + balance +
            ", bank=" + bank.getName() +
            ", latest transaction=" + history.getLatestTransaction() +
            '}';
    }
}
