package h08;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Represents a bank. A bank offers accounts to its customers and allows them to transfer money to other accounts.
 */
public class Bank {
    /**
     * The default capacity of a bank.
     */
    private static final int DEFAULT_CAPACITY = 10;

    /**
     * The default capacity of a transaction history.
     */
    private static final int DEFAULT_TRANSACTION_CAPACITY = 10;

    /**
     * The name of the bank.
     */
    private final String name;

    /**
     * The BIC of the bank.
     */
    private final int bic;


    /**
     * The banks to which this bank can transfer money.
     */
    private Bank[] transferableBanks;

    /**
     * The accounts of the bank.
     */
    private final Account[] accounts;

    /**
     * The capacity of the bank.
     */
    private final int capacity;

    /**
     * The size (number of accounts) of the bank.
     */
    private int size = 0;

    /**
     * The capacity of the transaction history.
     */
    private int transactionHistoryCapacity = DEFAULT_TRANSACTION_CAPACITY;


    /**
     * Constructs a new bank with the specified name, BIC, capacity and transferable banks.
     *
     * @param name              the name of the bank
     * @param bic               the BIC of the bank
     * @param capacity          the capacity of the bank
     * @param transferableBanks the banks to which this bank can transfer money
     */
    public Bank(String name, int bic, int capacity, Bank[] transferableBanks) {
        this.name = name;
        this.bic = bic;
        this.accounts = new Account[capacity];
        this.capacity = capacity;
        this.transferableBanks = transferableBanks;
    }

    /**
     * Constructs a new bank with the specified name, BIC, default capacity of {@value DEFAULT_CAPACITY} and
     * transferable banks.
     *
     * @param name              the name of the bank
     * @param bic               the BIC of the bank
     * @param transferableBanks the banks to which this bank can transfer money
     */
    public Bank(String name, int bic, Bank[] transferableBanks) {
        this(name, bic, DEFAULT_CAPACITY, transferableBanks);
    }

    /**
     * Constructs a new bank with the specified name, BIC and default capacity of {@value DEFAULT_CAPACITY}.
     *
     * @param name the name of the bank
     * @param bic  the BIC of the bank
     */
    public Bank(String name, int bic) {
        this(name, bic, DEFAULT_CAPACITY, new Bank[0]);
    }

    /**
     * Returns the name of the bank.
     *
     * @return the name of the bank
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the BIC of the bank.
     *
     * @return the BIC of the bank
     */
    public int getBic() {
        return bic;
    }

    /**
     * Returns the available accounts of the bank.
     *
     * @return the available accounts of the bank
     */
    public Account[] getAccounts() {
        Account[] availableAccounts = new Account[size];
        System.arraycopy(accounts, 0, availableAccounts, 0, size);
        return availableAccounts;
    }

    /**
     * Returns the transferable banks of the bank.
     *
     * @return the transferable banks of the bank
     */
    public Bank[] getTransferableBanks() {
        return transferableBanks;
    }

    /**
     * Returns the capacity of the bank.
     *
     * @return the capacity of the bank
     */
    public int capacity() {
        return capacity;
    }

    /**
     * Returns the size (number of accounts) of the bank.
     *
     * @return the size (number of accounts) of the bank
     */
    public int size() {
        return size;
    }

    /**
     * Returns the capacity of the transaction history.
     *
     * @return the capacity of the transaction history
     */
    public int transactionCapacity() {
        return DEFAULT_TRANSACTION_CAPACITY;
    }

    /**
     * Sets the capacity of the transaction history.
     *
     * @param transactionHistoryCapacity the capacity of the transaction history
     */
    public void setTransactionHistoryCapacity(int transactionHistoryCapacity) {
        this.transactionHistoryCapacity = transactionHistoryCapacity;
        for (int i = 0; i < size; i++) {
            Account account = accounts[i];
            account.setHistory(new TransactionHistory(account.getHistory(), transactionHistoryCapacity));
        }
    }

    /**
     * Returns {@code true} if the specified IBAN is already used by an account of the bank.
     *
     * @param iban the IBAN to check
     * @return {@code true} if the specified IBAN is already used by an account of the bank
     */
    private boolean isIbanAlreadyUsed(long iban) {
        for (int i = 0; i < size; i++) {
            if (accounts[i].getIban() == iban) {
                return true;
            }
        }
        return false;
    }

    /**
     * Generates an IBAN.
     *
     * @param seed the seed to generate the IBAN
     * @return the generated IBAN
     */
    private long generateIban(Customer customer, long seed) {
        long iban = Math.abs(customer.hashCode() * seed);
        if (isIbanAlreadyUsed(iban)) {
            iban = generateIban(customer, iban) << 33;
        }
        return Math.abs(iban);
    }

    /**
     * Adds the specified account to the bank.
     *
     * @throws IllegalStateException if the bank is full
     */
    public void add(Customer customer) {
        if (size == capacity) {
            throw new IllegalStateException("Bank is full!");
        }
        for (int i = 0; i < accounts.length; i++) {
            if (accounts[i] == null) {
                accounts[i] = new Account(customer, generateIban(customer, 1), 0, this,
                    new TransactionHistory(transactionHistoryCapacity));
                size++;
                return;
            }
        }
    }

    /**
     * Adds the specified bank to the transferable banks of the bank.
     *
     * @param bank the bank to add
     * @throws IllegalArgumentException if the bank is already in the transferable banks
     */
    public void add(Bank bank) {
        for (Bank transferableBank : transferableBanks) {
            if (transferableBank.getBic() == bank.getBic()) {
                throw new IllegalArgumentException("Cannot add duplicates!");
            }
        }
        Bank[] newTransferableBanks = new Bank[transferableBanks.length + 1];
        System.arraycopy(transferableBanks, 0, newTransferableBanks, 0, transferableBanks.length);
        newTransferableBanks[transferableBanks.length] = bank;
        transferableBanks = newTransferableBanks;
    }

    /**
     * Returns the account with the specified IBAN.
     *
     * @param iban the IBAN of the account
     * @return the account with the specified IBAN
     * @throws NoSuchElementException if the account with the specified IBAN does not exist
     */
    public int getAccountIndex(long iban) {
        for (int i = 0; i < accounts.length; i++) {
            if (accounts[i] != null && accounts[i].getIban() == iban)
                return i;
        }
        throw new NoSuchElementException(String.valueOf(iban));
    }

    /**
     * Removes the account with the specified IBAN from the bank.
     *
     * @param iban the IBAN of the account to remove
     * @return the removed account
     * @throws IllegalArgumentException if the IBAN is negative
     * @throws NoSuchElementException   if the account with the specified IBAN does not exist
     */
    public Account remove(long iban) {
        if (iban < 0) {
            throw new IllegalArgumentException("IBAN cannot be negative!");
        }
        int index = getAccountIndex(iban);
        Account removedAccount = accounts[index];
        accounts[index] = null;
        size--;
        return removedAccount;
    }

    /**
     * Returns the index of the bank with the specified BIC.
     *
     * @param bic the BIC of the bank
     * @return the index of the bank with the specified BIC
     * @throws NoSuchElementException if the bank with the specified BIC does not exist
     */
    private int getBankIndex(int bic) {
        for (int i = 0; i < transferableBanks.length; i++) {
            if (transferableBanks[i] != null && transferableBanks[i].getBic() == bic)
                return i;
        }
        throw new NoSuchElementException(String.valueOf(bic));
    }

    /**
     * Returns the bank with the specified BIC.
     *
     * @param bic the BIC of the bank
     * @return the bank with the specified BIC
     * @throws NoSuchElementException if the bank with the specified BIC does not exist
     */
    private Bank getBank(int bic) {
        return transferableBanks[getBankIndex(bic)];
    }

    /**
     * Removes the bank with the specified BIC from the transferable banks of the bank.
     *
     * @param bic the BIC of the bank to remove
     * @return the removed bank
     */
    public Bank remove(int bic) {
        if (bic < 0) {
            throw new IllegalArgumentException("BIC cannot be negative!");
        }
        int index = getBankIndex(bic);
        Bank removedBank = transferableBanks[index];
        transferableBanks[index] = null;
        return removedBank;
    }

    /**
     * Deposits the specified amount to the account with the specified IBAN.
     *
     * @param iban   the IBAN of the account
     * @param amount the amount to deposit
     * @throws IllegalArgumentException if the amount is zero or negative
     * @throws NoSuchElementException   if the account with the specified IBAN does not exist
     */
    public void deposit(long iban, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount cannot be zero or negative!");
        }
        int index = getAccountIndex(iban);
        Account account = accounts[index];
        double newBalance = account.getBalance() + amount;
        account.setBalance(newBalance);
    }

    /**
     * Withdraws the specified amount from the account with the specified IBAN.
     *
     * @param iban   the IBAN of the account
     * @param amount the amount to withdraw
     * @throws IllegalArgumentException if the amount is zero or negative or if the new balance would be negative
     * @throws NoSuchElementException   if the account with the specified IBAN does not exist
     */
    public void withdraw(long iban, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount cannot be zero or negative!");
        }
        int index = getAccountIndex(iban);
        Account account = accounts[index];
        double newBalance = account.getBalance() - amount;
        if (newBalance < 0) {
            throw new IllegalArgumentException("Amount cannot be negative!");
        }
        account.setBalance(newBalance);
    }

    /**
     * Generates a transaction number.
     *
     * @return the generated transaction number
     */
    private long generateTransactionNumber() {
        return System.currentTimeMillis();
    }

    /**
     * Transfers the specified amount from the account with the specified IBAN to the account with the specified IBAN.
     *
     * @param senderIBAN   the IBAN of the sender account
     * @param receiverIBAN the IBAN of the receiver account
     * @param receiverBIC  the BIC of the receiver bank
     * @param amount       the amount to transfer
     * @param description  the description of the transaction
     * @return the status of the transaction
     */
    public Status transfer(long senderIBAN, long receiverIBAN, int receiverBIC, double amount, String description) {
        long transactionNumber = generateTransactionNumber();
        int senderIndex;
        Bank receiverBank;
        int receiverIndex;
        try {
            senderIndex = getAccountIndex(senderIBAN);
            receiverBank = getBank(receiverBIC);
            receiverIndex = receiverBank.getAccountIndex(receiverIBAN);
        } catch (NoSuchElementException | IllegalArgumentException e) {
            return Status.CANCELLED;
        }
        Account senderAccount = accounts[senderIndex];
        Account receiverAccount = receiverBank.accounts[receiverIndex];
        Transaction transaction = new Transaction(
            senderAccount, receiverAccount,
            amount, transactionNumber,
            description, LocalDate.now(), Status.OPEN
        );

        TransactionHistory senderHistory = senderAccount.getHistory();
        TransactionHistory receiverHistory = receiverAccount.getHistory();
        senderHistory.add(transaction);
        receiverHistory.add(transaction);
        try {
            withdraw(senderIBAN, amount);
            receiverBank.deposit(receiverIBAN, amount);
            transaction = new Transaction(
                senderAccount, receiverAccount,
                amount, transactionNumber,
                description, LocalDate.now(), Status.CLOSED
            );
            senderHistory.update(transaction);
            receiverHistory.update(transaction);
        } catch (IllegalArgumentException e) {
            transaction = new Transaction(
                senderAccount, receiverAccount,
                amount, transactionNumber,
                description, LocalDate.now(), Status.CANCELLED
            );
            senderHistory.update(transaction);
            receiverHistory.update(transaction);
            return Status.CANCELLED;
        }
        return transaction.status();
    }

    /**
     * Checks for opened transactions and cancels them if they are older than 2 or 4 weeks. If the transaction is older
     * than 2 weeks, the user is notified by transferring the amount to the target account again.
     *
     * @return the open transactions
     */
    public Transaction[] checkOpenTransactions() {
        int length = 0;
        for (int i = 0; i < size; i++) {
            length += accounts[i].getHistory().getTransactions(Status.OPEN).length;
        }
        Transaction[] openTransactions = new Transaction[length];
        int index = 0;
        for (int i = 0; i < size; i++) {
            Account account = accounts[i];
            Transaction[] transactions = account.getHistory().getTransactions(Status.OPEN);
            for (Transaction transaction : transactions) {
                Account sourceAccount = transaction.sourceAccount();
                Account targetAccount = transaction.targetAccount();
                LocalDate today = LocalDate.now();
                boolean olderThanTwoWeeks = transaction.date().plusWeeks(2).isAfter(today);
                boolean olderThanFourWeeks = transaction.date().plusWeeks(4).isAfter(today);

                // Cancel transaction if it is older than 2 or 4 weeks.
                if (olderThanTwoWeeks || olderThanFourWeeks) {
                    Transaction cancelledTransaction = new Transaction(
                        sourceAccount, targetAccount,
                        transaction.amount(), transaction.transactionNumber(),
                        transaction.description(), transaction.date(), Status.CANCELLED
                    );
                    sourceAccount.getHistory().update(cancelledTransaction);
                    targetAccount.getHistory().update(cancelledTransaction);
                }
                // If transaction is older than 2 weeks, notify user.
                if (transaction.date().plusWeeks(2).isAfter(today)) {
                    transfer(
                        sourceAccount.getIban(),
                        targetAccount.getIban(),
                        targetAccount.getBank().getBic(),
                        transaction.amount(),
                        transaction.description()
                    );
                }
                openTransactions[index++] = transaction;
            }
        }
        return openTransactions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bank bank = (Bank) o;
        return getBic() == bank.getBic()
            && capacity() == bank.capacity()
            && size() == bank.size()
            && Objects.equals(getName(), bank.getName())
            && Arrays.equals(getAccounts(), bank.getAccounts());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getBic(), capacity(), size(), Arrays.hashCode(getAccounts()));
    }

    @Override
    public String toString() {
        return "Bank{" +
            "name='" + name + '\'' +
            ", bic=" + bic +
            ", capacity=" + capacity +
            ", size=" + size +
            '}';
    }
}
