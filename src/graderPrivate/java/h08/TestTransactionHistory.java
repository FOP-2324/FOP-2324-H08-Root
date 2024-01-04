package h08;


import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TestTransactionHistory extends TransactionHistory {

    List<Transaction> transactions;

    int addCalls;

    private TestTransactionHistory(List<Transaction> transactions) {
        this.transactions = new ArrayList<>(transactions);
    }

    public static TestTransactionHistory newInstance(List<Transaction> transactions) {
        // We can't override the update method, because it throws a TransactionException, which is added by the students.
        // If a student misspells the TransactionException, the test would not compile.

        TestTransactionHistory instance = spy(new TestTransactionHistory(transactions));

        try {
            doAnswer(invocation -> {
                Transaction transaction = invocation.getArgument(0);
                instance.update0(transaction);
                return null;
            }).when(instance).update(any(Transaction.class));
        } catch (Exception e) {
            throw new RuntimeException("failed to mock update", e);
        }
        return instance;
    }

    public static TestTransactionHistory newInstance() {
        return newInstance(new ArrayList<>());
    }

    @Override
    public void add(Transaction transaction) {
        if (transactions.stream().anyMatch(t -> t.transactionNumber() == transaction.transactionNumber()))
            throw new IllegalArgumentException("This transaction already exists!");
        transactions.add(transaction);
        addCalls++;
    }

    private void update0(Transaction transaction) throws Throwable {
        Optional<Transaction> t = transactions.stream()
            .filter(tr -> tr.transactionNumber() == transaction.transactionNumber())
            .findFirst();

        if (t.isPresent()) {
            transactions.set(transactions.indexOf(t.get()), transaction);
        } else {
            throw (Throwable) StudentLinks.TRANSACTION_EXCEPTION_TRANSACTION_CONSTRUCTOR_LINK.get().invoke(
                "Transaction does not exist!", transaction.transactionNumber());
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
