package h08;

import org.junit.jupiter.params.ParameterizedTest;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSet;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSetTest;

import java.util.List;

import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.*;

@TestForSubmission
public class H5_2_Test extends H08_TestBase {

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @JsonParameterSetTest(value = "H5_2_Exception.json", customConverters = "customConverters")
    public void testException(JsonParameterSet params) throws ReflectiveOperationException {

        List<Transaction> transactions = params.get("transactions", List.class);
        Transaction missingTransaction = params.get("transactionToUpdate", Transaction.class);
        int capacity = params.get("capacity", Integer.class);

        TransactionHistory history = new TransactionHistory(capacity);
        Transaction[] transactionsArray = transactions.toArray(new Transaction[capacity]);

        setHistorySize(history, transactions.size());
        setHistoryNextIndex(history, transactions.size());
        setHistoryTransactions(history, transactionsArray);

        Context context = contextBuilder()
            .subject("TransactionHistory#add")
            .add("transactions", transactions)
            .add("size", transactions.size())
            .add("capacity", capacity)
            .add("transactionToUpdate", missingTransaction)
            .build();

        checkExceptionThrown(() -> history.update(missingTransaction),
            context,
            StudentLinks.getClassOfTypeLink(StudentLinks.TRANSACTION_EXCEPTION_LINK.get()),
            "Transaction does not exist! " + missingTransaction.transactionNumber());

        Transaction[] expectedTransactionsArray = transactions.toArray(new Transaction[capacity]);
        for (int i = 0; i < capacity; i++) {
            int finalI = i;
            assertSame(expectedTransactionsArray[i], getHistoryTransactions(history)[i], context,
                TR -> "The transaction at index %d was modified.".formatted(finalI));
        }

    }

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @JsonParameterSetTest(value = "H5_2.json", customConverters = "customConverters")
    public void testNormal(JsonParameterSet params) throws ReflectiveOperationException {

        List<Transaction> transactions = params.get("transactions", List.class);
        Transaction transactionToUpdate = params.get("transactionToUpdate", Transaction.class);
        int capacity = params.get("capacity", Integer.class);

        TransactionHistory history = new TransactionHistory(capacity);
        Transaction[] transactionsArray = transactions.toArray(new Transaction[capacity]);

        setHistorySize(history, transactions.size());
        setHistoryNextIndex(history, transactions.size());
        setHistoryTransactions(history, transactionsArray);

        Context context = contextBuilder()
            .subject("TransactionHistory#add")
            .add("transactions", transactions)
            .add("size", transactions.size())
            .add("capacity", capacity)
            .add("transactionToUpdate", transactionToUpdate)
            .build();

        call(() -> history.update(transactionToUpdate), context,
            TR -> "TransactionHistory#update threw an unexpected exception.");

        Transaction[] expectedTransactions = transactions.toArray(new Transaction[capacity]);
        int indexToUpdate = transactions.indexOf(transactions.stream()
            .filter(t -> t.transactionNumber() == transactionToUpdate.transactionNumber())
            .findFirst()
            .get());
        expectedTransactions[indexToUpdate] = transactionToUpdate;

        for (int i = 0; i < capacity; i++) {
            int finalI = i;
            assertSame(expectedTransactions[i], getHistoryTransactions(history)[i], context,
                TR -> finalI == indexToUpdate ? "The transaction has not been replaced with the given transaction" :
                    "The transaction at index %d was modified.".formatted(finalI));
        }

    }

}
