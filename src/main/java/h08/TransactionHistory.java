package h08;

import java.util.NoSuchElementException;

/**
 * Represents a history of transactions with a fixed capacity. If the capacity is reached, the oldest transaction is
 * removed.
 */
public class TransactionHistory {

    /**
     * The default capacity of the transaction history.
     */
    private static final int DEFAULT_CAPACITY = 10;

    /**
     * The capacity of the transaction history.
     */
    private final int capacity;

    /**
     * The transactions in this history.
     */
    private final Transaction[] transactions;

    /**
     * Index of the next transaction to be added.
     */
    private int nextIndex = 0;

    /**
     * The number of transactions in this history.
     */
    private int size = 0;

    /**
     * Constructs a new transaction history with the specified capacity.
     *
     * @param capacity the capacity of the transaction history.
     */
    public TransactionHistory(int capacity) {
        this.capacity = capacity;
        this.transactions = new Transaction[capacity];
    }

    /**
     * Constructs a new transaction history with the specified capacity and the transactions of the specified history.
     *
     * @param history  the history to copy
     * @param capacity the capacity of the transaction history
     */
    TransactionHistory(TransactionHistory history, int capacity) {
        this.capacity = capacity;
        this.transactions = new Transaction[capacity];
        System.arraycopy(history.transactions, 0, this.transactions, 0, Math.min(capacity, history.size));
    }

    /**
     * Constructs a new transaction history with the default capacity of {@value DEFAULT_CAPACITY}.
     */
    public TransactionHistory() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Adds the specified transaction to this history. If the capacity is reached, the oldest transaction is removed.
     *
     * @param transaction the transaction to add
     * @throws IllegalArgumentException if the transaction number already exists in this history.
     */
    public void add(Transaction transaction) {
        // TODO H5.1
        for (int i = 0; i < size; i++) {
            if (transactions[i].transactionNumber() == transaction.transactionNumber()) {
                throw new IllegalArgumentException("This transaction already exists!");
            }
        }
        transactions[nextIndex++] = transaction;
        if (nextIndex == capacity) {
            nextIndex = 0;
        }
        size = Math.min(size + 1, capacity);
    }

    /**
     * Updates the specified transaction in this history.
     *
     * @param transaction the transaction to update
     * @throws TransactionException if the transaction does not exist in this history.
     */
    public void update(Transaction transaction) throws TransactionException {
        // TODO H5.2
        for (int i = 0; i < size; i++) {
            if (transactions[i].transactionNumber() == transaction.transactionNumber()) {
                transactions[i] = transaction;
                return;
            }
        }
        throw new TransactionException("Transaction does not exist!", transaction.transactionNumber());
    }

    /**
     * Returns the transaction with the specified transaction number.
     *
     * @param transactionNumber the transaction number of the transaction to return
     * @return the transaction with the specified transaction number
     * @throws NoSuchElementException if the transaction does not exist in this history.
     */
    public Transaction get(long transactionNumber) {
        for (int i = 0; i < size; i++) {
            Transaction transaction = transactions[i];
            if (transaction.transactionNumber() == transactionNumber) {
                return transaction;
            }
        }
        throw new NoSuchElementException(String.valueOf(transactionNumber));
    }

    /**
     * Returns the transaction at the specified index. The index must be between 0 and the last index of the history.
     *
     * @param index the index of the transaction to return
     * @return the transaction at the specified index
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    public Transaction get(int index) {
        Transaction transaction = transactions[index];
        if (transaction == null) {
            throw new IndexOutOfBoundsException(index);
        }
        return transaction;
    }

    /**
     * Returns the number of transactions in this history.
     *
     * @return the number of transactions in this history
     */
    public int size() {
        return size;
    }

    /**
     * Returns the capacity of this history.
     *
     * @return the capacity of this history
     */
    public int capacity() {
        return capacity;
    }

    /**
     * Returns the latest transaction in this history.
     *
     * @return the latest transaction in this history
     */
    public Transaction getLatestTransaction() {
        if (nextIndex == 0 && size != capacity) {
            throw new IllegalStateException("No transactions yet!");
        }
        return transactions[Math.floorMod(nextIndex - 1, capacity)];
    }

    public static void main(String[] args) {
        int nextIndex = 0;
        int capacity = 10;
        System.out.println(Math.floorMod(nextIndex - 1, capacity));
    }

    /**
     * Returns the available transactions in this history. The order of the transactions is from oldest to newest.
     *
     * @return the available transactions in this history
     */
    public Transaction[] getTransactions() {
        Transaction[] availableTransactions = new Transaction[size];
        for (int i = availableTransactions.length - 1; i >= 0; i--) {
            availableTransactions[i] = transactions[Math.floorMod(nextIndex - i - 1, capacity)];
        }
        return availableTransactions;
    }

    /**
     * Returns the available transactions in this history with the specified status. The order of the transactions is
     *
     * @param status the status of the transactions to return
     * @return the available transactions in this history with the specified status
     */
    public Transaction[] getTransactions(Status status) {
        int length = 0;
        for (int i = 0; i < size; i++) {
            if (transactions[i].status() == status) {
                length++;
            }
        }
        Transaction[] availableTransactions = new Transaction[length];
        int index = 0;
        for (int i = 0; i < size; i++) {
            Transaction transaction = transactions[i];
            if (transaction.status() == status) {
                availableTransactions[index++] = transaction;
            }
        }
        return availableTransactions;
    }

}
