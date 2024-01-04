package h08;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSet;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSetTest;
import org.tudalgo.algoutils.tutor.general.match.Matcher;
import org.tudalgo.algoutils.tutor.general.reflections.ConstructorLink;
import org.tudalgo.algoutils.tutor.general.reflections.Modifier;
import org.tudalgo.algoutils.tutor.general.reflections.TypeLink;

import java.time.LocalDate;
import java.util.List;

import static h08.util.StudentLinks.*;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.*;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions3.assertCorrectModifiers;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions3.assertCorrectSuperType;

@TestForSubmission
public class H3_Test extends H08_TestBase {

    @Test
    public void testBadTimestampExceptionDeclaration() {

        TypeLink badTimeStampExceptionLink = createTypeLink("BadTimestampException", true).get();

        assertCorrectModifiers(badTimeStampExceptionLink, Modifier.CLASS, Modifier.NON_ABSTRACT);
        assertCorrectSuperType(badTimeStampExceptionLink, Matcher.of(type -> type.identifier().equals(RuntimeException.class.getSimpleName())));

        BAD_TIME_STAMP_EXCEPTION_CONSTRUCTOR_LINK.get();
    }


    @Test
    public void testBadTimestampConstructor() {

        ConstructorLink constructorLink = BAD_TIME_STAMP_EXCEPTION_CONSTRUCTOR_LINK.get();

        LocalDate localDate = LocalDate.now();

        Context context = contextBuilder()
            .subject("BadTimestampException#BadTimestampException(LocalDate)")
            .add("localDate", localDate)
            .build();

        Exception exception = callObject(() -> constructorLink.invoke(localDate), context, TR ->
            "Constructor of BadTimestampException threw an exception");

        assertEquals("Bad timestamp: " + localDate, exception.getMessage(), context, TR ->
            "Constructor of BadTimestampException did not set the message correctly");

    }

    @Test
    public void testBankExceptionDeclaration() {

        TypeLink bankExceptionLink = createTypeLink("BankException", true).get();

        assertCorrectModifiers(bankExceptionLink, Modifier.CLASS, Modifier.NON_ABSTRACT);
        assertCorrectSuperType(bankExceptionLink, Matcher.of(type -> type.identifier().equals(Exception.class.getSimpleName())));

        BANK_EXCEPTION_STRING_CONSTRUCTOR_LINK.get();
        BANK_EXCEPTION_LONG_CONSTRUCTOR_LINK.get();
    }

    @Test
    public void testBankExceptionStringConstructor() {

        ConstructorLink constructorLink = BANK_EXCEPTION_STRING_CONSTRUCTOR_LINK.get();

        String message = "Test message";

        Context context = contextBuilder()
            .subject("BankException#BankException(String)")
            .add("message", message)
            .build();

        Exception exception = callObject(() -> constructorLink.invoke(message), context, TR ->
            "Constructor of BankException threw an exception");

        assertEquals(message, exception.getMessage(), context, TR ->
            "Constructor of BankException did not set the message correctly");
    }

    @Test
    public void testBankExceptionLongConstructor() {

        ConstructorLink constructorLink = BANK_EXCEPTION_LONG_CONSTRUCTOR_LINK.get();

        long bic = 123456789;

        Context context = contextBuilder()
            .subject("BankException#BankException(long)")
            .add("bic", bic)
            .build();

        Exception exception = callObject(() -> constructorLink.invoke(bic), context, TR ->
            "Constructor of BankException threw an exception");

        assertEquals("Cannot find Bank with BIC: " + bic, exception.getMessage(), context, TR ->
            "Constructor of BankException did not set the message correctly");
    }

    @Test
    public void testTransactionExceptionStringConstructorDeclaration() {

        TypeLink transactionExceptionLink = createTypeLink("TransactionException", true).get();

        assertCorrectModifiers(transactionExceptionLink, Modifier.CLASS, Modifier.NON_ABSTRACT);
        assertCorrectSuperType(transactionExceptionLink, Matcher.of(type -> type.identifier().equals(Exception.class.getSimpleName())));

        TRANSACTION_EXCEPTION_STRING_CONSTRUCTOR_LINK.get();
    }

    @Test
    public void testTransactionExceptionTransactionConstructorDeclaration() {

        TypeLink transactionExceptionLink = TRANSACTION_EXCEPTION_LINK.get(); // don't check exact match again

        assertCorrectModifiers(transactionExceptionLink, Modifier.CLASS, Modifier.NON_ABSTRACT);
        assertCorrectSuperType(transactionExceptionLink, Matcher.of(type -> type.identifier().equals(Exception.class.getSimpleName())));

        TRANSACTION_EXCEPTION_TRANSACTION_CONSTRUCTOR_LINK.get();
    }

    @Test
    public void testTransactionExceptionStringConstructor() {

        ConstructorLink constructorLink = TRANSACTION_EXCEPTION_STRING_CONSTRUCTOR_LINK.get();

        String message = "Test message";
        long transactionNumber = 123456789;

        Context context = contextBuilder()
            .subject("TransactionException#TransactionException(String, long)")
            .add("message", message)
            .add("transactionNumber", transactionNumber)
            .build();

        Exception exception = callObject(() -> constructorLink.invoke(message, transactionNumber), context, TR ->
            "Constructor of TransactionException threw an exception");

        assertEquals(message + " " + transactionNumber, exception.getMessage(), context, TR ->
            "Constructor of TransactionException did not set the message correctly");
    }

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @JsonParameterSetTest(value = "H3_TransactionException.json", customConverters = "customConverters")
    public void testTransactionExceptionTransactionConstructor(JsonParameterSet params) {

        ConstructorLink constructorLink = TRANSACTION_EXCEPTION_TRANSACTION_CONSTRUCTOR_LINK.get();

        List<Transaction> transactions = params.get("transactions", List.class);

        Context context = contextBuilder()
            .subject("TransactionException#TransactionException(Transaction[])")
            .add("transactions", transactions)
            .build();

        Exception exception = callObject(
            () -> constructorLink.invoke(new Object[]{transactions.toArray(Transaction[]::new)}),
            context, TR -> "Constructor of TransactionException threw an exception");

        assertEquals(getExpectedTransactionExceptionMessage(transactions), exception.getMessage(), context, TR ->
            "Constructor of TransactionException did not set the message correctly");
    }

    private String getExpectedTransactionExceptionMessage(List<Transaction> transactions) {
        StringBuilder sb = new StringBuilder("Transaction numbers: [");
        for (int i = 0; i < transactions.size(); i++) {
            sb.append(transactions.get(i).transactionNumber());
            if (i < transactions.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

}
