package h08;

import h08.util.comment.TransactionCommentFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSet;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSetTest;

import java.util.List;

import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.*;

@TestForSubmission
public class H5_1_Test extends H08_TestBase {

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @JsonParameterSetTest(value = "H5_1_Exception.json", customConverters = "customConverters")
    public void testException(JsonParameterSet params) throws ReflectiveOperationException {
        List<Transaction> transactions = params.get("transactions", List.class);
        Transaction doubledTransaction = params.get("doubledTransaction", Transaction.class);
        int capacity = params.get("capacity", Integer.class);

        TransactionHistory history = new TransactionHistory(capacity);
        Transaction[] transactionsArray = transactions.toArray(new Transaction[capacity]);

        setHistorySize(history, transactions.size());
        setHistoryNextIndex(history, transactions.size());
        setHistoryTransactions(history, transactionsArray);

        Context context = contextBuilder()
            .subject("TransactionHistory#add")
            .add("transactions", TransactionCommentFactory.NUMBER_ONLY.build(transactions))
            .add("size", transactions.size())
            .add("nextIndex", transactions.size())
            .add("capacity", capacity)
            .add("transactionToAdd", TransactionCommentFactory.NUMBER_ONLY.build(doubledTransaction))
            .build();

        checkExceptionThrown(() -> history.add(doubledTransaction),
            context,
            IllegalArgumentException.class,
            "This transaction already exists!");
    }

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @JsonParameterSetTest(value = "H5_1_Size.json", customConverters = "customConverters")
    public void testSize(JsonParameterSet params) throws ReflectiveOperationException {
        List<Transaction> transactions = params.get("transactions", List.class);
        int capacity = params.get("capacity", Integer.class);

        TransactionHistory history = new TransactionHistory(capacity);

        int expectedSize = 0;

        for (Transaction transaction : transactions) {

            Context context = contextBuilder()
                .subject("TransactionHistory#add")
                .add("transactions", TransactionCommentFactory.NUMBER_ONLY.build(history.getTransactions()))
                .add("size", history.size())
                .add("nextIndex", getHistoryNextIndex(history))
                .add("capacity", history.capacity())
                .add("transactionToAdd", TransactionCommentFactory.NUMBER_ONLY.build(transaction))
                .build();

            call(() -> history.add(transaction), context,
                TR -> "TransactionHistory#add threw an unexpected exception.");


            expectedSize = Math.min(expectedSize + 1, capacity);

            assertEquals(expectedSize, history.size(), context, TR -> "The size of the history is not correct.");
        }
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "H5_1_Simple.json", customConverters = "customConverters")
    public void testSimple(JsonParameterSet params) throws ReflectiveOperationException {
        testAdd(params);
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "H5_1_Complex.json", customConverters = "customConverters")
    public void testComplex(JsonParameterSet params) throws ReflectiveOperationException {
        testAdd(params);
    }

    @SuppressWarnings("unchecked")
    private void testAdd(JsonParameterSet params) throws ReflectiveOperationException {
        List<Transaction> transactions = params.get("transactions", List.class);
        int capacity = params.get("capacity", Integer.class);

        TransactionHistory history = new TransactionHistory(capacity);

        Transaction[] expectedTransactions = new Transaction[capacity];
        int nextIndex = 0;
        int expectedSize = 0;

        for (Transaction transaction : transactions) {

            expectedTransactions[nextIndex] = transaction;
            nextIndex = (nextIndex + 1) % capacity;
            expectedSize = Math.min(expectedSize + 1, capacity);

            Context context = contextBuilder()
                .subject("TransactionHistory#add")
                .add("transactions", TransactionCommentFactory.NUMBER_ONLY.build(history.getTransactions()))
                .add("size", history.size())
                .add("nextIndex", getHistoryNextIndex(history))
                .add("capacity", history.capacity())
                .add("transactionToAdd", TransactionCommentFactory.NUMBER_ONLY.build(transaction))
                .add("expectedTransactions", TransactionCommentFactory.NUMBER_ONLY.build(expectedTransactions))
                .build();

            call(() -> history.add(transaction), context,
                TR -> "TransactionHistory#add threw an unexpected exception.");

            for (int i = 0; i < capacity; i++) {
                int finalI = i;
                assertEquals(expectedTransactions[i], getHistoryTransactions(history)[i], context,
                    TR -> "The transaction at index %d is not correct.".formatted(finalI));
            }
        }
    }

}
