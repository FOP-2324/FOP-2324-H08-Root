package h08;

import h08.exceptions.TransactionException;

public class TransactionHistory {

    private final int capacity;
    private final Transaction[] transactions;

    // Index of the next transaction to be added
    private int nextIndex = 0;

    public TransactionHistory(int capacity) {
        this.capacity = capacity;
        this.transactions = new Transaction[capacity];
    }

    public void add(Transaction transaction) throws TransactionException {
        // If transaction number already exists, throw exception
        // Remove the oldest transaction if capacity is reached
        for (Transaction t:
             transactions) {
            if(t.transactionNumber() == transaction.transactionNumber())
                throw new TransactionException("This transaction already exists!", transaction.transactionNumber());
        }
        if(nextIndex == capacity)
            nextIndex = 0;
        transactions[nextIndex] = transaction;
        nextIndex++;
    }

    public void update(Transaction transaction) {
        // remove old transaction (search transaction number) and replace with new transaction
        for (int i = 0; i < transactions.length;i++){
            if(transactions[i].transactionNumber() == transaction.transactionNumber())
                transactions[i] = transaction;
        }
    }

    public Transaction get(long transactionNumber) throws TransactionException {
        for (Transaction transaction : transactions) {
            if (transaction.transactionNumber() == transactionNumber)
                return transaction;
        }
        throw new TransactionException("cant find transaction number: ", transactionNumber);
    }

    public Transaction get(int index) {
        // index must be between 0 and capacity - 1. index depends on the nextIndex
        return transactions[index];
    }

    public Transaction[] getTransactions() {
        // Return a copy of the transactions array starting from the first element (depending on the nextIndex)
        Transaction[] copiedTransactions = new Transaction[capacity];
        System.arraycopy(this.transactions,0,copiedTransactions,0,this.transactions.length);
        return copiedTransactions;
    }
}
