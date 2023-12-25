package h08;


import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class TestTransactionHistory extends TransactionHistory {

    List<Transaction> transactions = new ArrayList<>();

    int addCalls;

    @Override
    public void add(Transaction transaction) {
        if (transactions.stream().anyMatch(t -> t.transactionNumber() == transaction.transactionNumber()))
            throw new IllegalArgumentException("This transaction already exists!");
        transactions.add(transaction);
        addCalls++;
    }

    @Override
    public void update(Transaction transaction) throws TransactionException {
        Optional<Transaction> t = transactions.stream()
            .filter(tr -> tr.transactionNumber() == transaction.transactionNumber())
            .findFirst();

        if (t.isPresent()) {
            transactions.set(transactions.indexOf(t.get()), transaction);
        } else {
            throw new TransactionException("Transaction does not exist!", transaction.transactionNumber());
        }
    }

    @Override
    public Transaction get(long transactionNumber) {
        return transactions.stream()
            .filter(t -> t.transactionNumber() == transactionNumber)
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException(String.valueOf(transactionNumber)));
    }

    @Override
    public Transaction get(int index) {
        return transactions.get(index);
    }

    @Override
    public int size() {
        return transactions.size();
    }

    @Override
    public int capacity() {
        return Integer.MAX_VALUE;
    }

    @Override
    public Transaction getLatestTransaction() {
        return transactions.get(transactions.size() - 1);
    }

    @Override
    public Transaction[] getTransactions() {
        return transactions.toArray(new Transaction[0]);
    }

    @Override
    public Transaction[] getTransactions(Status status) {
        return transactions.stream()
            .filter(t -> t.status() == status)
            .toArray(Transaction[]::new);
    }
}
