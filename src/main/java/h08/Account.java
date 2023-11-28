package h08;

import java.time.LocalDate;

/**
 * Represents a bank account.
 */
public class Account {

    /**
     * The minimum age of a customer.
     */
    private static final int MIN_AGE = 18;

    /**
     * The customer that owns this account.
     */
    private final Customer customer;

    /**
     * The IBAN of this account.
     */
    private final long iban;

    /**
     * The balance of this account.
     */
    private double balance;

    /**
     * The transaction history of this account.
     */
    private TransactionHistory history;

    /**
     * The bank this account belongs to.
     */
    private final Bank bank;

    /**
     * Constructs a new account with the specified customer, IBAN, balance, bank and transaction history.
     *
     * @param customer the customer that owns this account
     * @param iban     the IBAN of this account
     * @param balance  the balance of this account
     * @param bank     the bank this account belongs to
     * @param history  the transaction history of this account
     */
    public Account(Customer customer, long iban, double balance, Bank bank, TransactionHistory history) {
        assert customer != null;
        assert iban >= 0;
        assert bank != null;
        assert history != null;
        if (customer.dateOfBirth().isAfter(LocalDate.now().minusYears(MIN_AGE))) {
            throw new BadTimestampException(customer.dateOfBirth());
        }
        this.customer = customer;
        this.iban = iban;
        this.balance = balance;
        this.history = history;
        this.bank = bank;
    }

    /**
     * Returns the customer that owns this account.
     *
     * @return the customer that owns this account
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Returns the IBAN of this account.
     *
     * @return the IBAN of this account
     */
    public long getIban() {
        return iban;
    }

    /**
     * Returns the balance of this account.
     *
     * @return the balance of this account
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Sets the balance of this account.
     *
     * @param balance the new balance of this account
     */
    public void setBalance(double balance) {
        this.balance = balance;
    }

    /**
     * Returns the bank this account belongs to.
     *
     * @return the bank this account belongs to
     */
    public Bank getBank() {
        return bank;
    }

    /**
     * Returns the transaction history of this account.
     *
     * @return the transaction history of this account
     */
    public TransactionHistory getHistory() {
        return history;
    }

    /**
     * Sets the transaction history of this account.
     *
     * @param history the new transaction history of this account
     */
    public void setHistory(TransactionHistory history) {
        this.history = history;
    }

    @Override
    public String toString() {
        return "Account{" +
            "customer=" + customer +
            ", iban=" + iban +
            ", balance=" + balance +
            '}';
    }
}
