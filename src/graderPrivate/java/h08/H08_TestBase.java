package h08;

import com.fasterxml.jackson.databind.JsonNode;
import h08.util.JsonConverters;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.MockedStatic;
import org.tudalgo.algoutils.student.CrashException;
import org.tudalgo.algoutils.tutor.general.annotation.SkipAfterFirstFailedTest;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import org.tudalgo.algoutils.tutor.general.callable.Callable;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static h08.util.StudentLinks.BAD_TIME_STAMP_EXCEPTION_CONSTRUCTOR_LINK;
import static org.mockito.Answers.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mockStatic;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.*;

@SkipAfterFirstFailedTest(TestConstants.SKIP_AFTER_FIRST_FAILED_TEST)
public abstract class H08_TestBase {

    @SuppressWarnings("unused")
    public static final Map<String, Function<JsonNode, ?>> customConverters = new DefaultConvertersMap(Map.ofEntries(
        Map.entry("bank", JsonConverters::toBank),
        Map.entry("accounts", JsonConverters::toAccountList),
        Map.entry("unusedIbans", n -> JsonConverters.toList(n, JsonNode::asLong)),
        Map.entry("ibansToRemove", n -> JsonConverters.toList(n, JsonNode::asLong)),
        Map.entry("customerToAdd", JsonConverters::toCustomer),
        Map.entry("transactions", JsonConverters::toTransactionList),
        Map.entry("customer", JsonConverters::toCustomer),
        Map.entry("date", JsonConverters::toDate),
        Map.entry("status", JsonConverters::toStatus),
        Map.entry("description", JsonNode::asText),
        Map.entry("doubledTransaction", JsonConverters::toTransaction),
        Map.entry("transactionToUpdate", JsonConverters::toTransaction),
        Map.entry("sender", JsonConverters::toAccount),
        Map.entry("receiver", JsonConverters::toAccount),
        Map.entry("sourceBank", JsonConverters::toBank),
        Map.entry("targetBank", JsonConverters::toBank)
    ));

    private static MockedStatic<LocalDate> mockedLocalDate;

    @BeforeAll
    public static void setupLocalDate() {
        if (mockedLocalDate == null) {
            LocalDate christmas = LocalDate.of(2023, 12, 24);
            mockedLocalDate = mockStatic(LocalDate.class, CALLS_REAL_METHODS);
            mockedLocalDate.when(LocalDate::now).thenReturn(christmas);
        }
    }

    @AfterAll
    public static void tearDownLocalDate() {
        if (mockedLocalDate != null) {
            mockedLocalDate.close();
            mockedLocalDate = null;
        }
    }

    public static String getExpectedBadTimeStepMessage(String message) {

        try {
            return ((Exception) BAD_TIME_STAMP_EXCEPTION_CONSTRUCTOR_LINK.get().invoke(message)).getMessage();
        } catch (Throwable e) {
            return "Bad timestamp: " + message;
        }
    }

    public static void checkBankSizeAndAccountsUnchanged(Bank bank, List<Account> accounts, Context context) throws ReflectiveOperationException {


        assertEquals(accounts.size(), getBankSize(bank), context, TR -> "The size of the bank is not correct.");

        Account[] bankAccounts = bank.getAccounts();
        for (int i = 0; i < accounts.size(); i++) {
            int finalI = i;
            assertEquals(accounts.get(i), bankAccounts[i], context, TR -> "The account at index " + finalI + " is not correct.");
        }
    }

    public static void checkExceptionThrown(Callable callable, Context context, Class<?> expectedExceptionClass) {
        checkExceptionThrown(callable, context, expectedExceptionClass, null);
    }

    public static void checkExceptionThrown(Callable callable, Context context, Class<?> expectedExceptionClass, String expectedExceptionMessage) {

        Throwable exception = null;

        try {
            callable.call();
        } catch (CrashException crashException) {
            call(() -> {throw crashException;});
        } catch (Throwable t) {
            exception = t;
        }

        assertNotNull(exception, context, TR -> "No exception was thrown. Expected exception of type " + expectedExceptionClass.getName() + ".");

        if (exception instanceof CrashException crashException) {
            throw crashException;
        }

        assertEquals(expectedExceptionClass, exception.getClass(), context, TR -> "The thrown exception is not of the expected type.");
        if (expectedExceptionMessage !=  null) {
            assertEquals(expectedExceptionMessage, exception.getMessage(), context, TR -> "The thrown exception has the wrong message.");
        }
    }

    public static void assertTransactionEquals(Transaction expected, Transaction actual, Context context, String message) {
        assertEquals(expected.sourceAccount(), actual.sourceAccount(), context, TR -> message + ". The source account is not correct.");
        assertEquals(expected.targetAccount(), actual.targetAccount(), context, TR -> message + ". The target account is not correct.");
        assertEquals(expected.amount(), actual.amount(), context, TR -> message + ". The amount is not correct.");
        assertEquals(expected.transactionNumber(), actual.transactionNumber(), context, TR -> message + ". The transaction number is not correct.");
        assertEquals(expected.description(), actual.description(), context, TR -> message + ". The description is not correct.");
        assertEquals(expected.date(), actual.date(), context, TR -> message + ". The date is not correct.");
        assertEquals(expected.status(), actual.status(), context, TR -> message + ". The status is not correct.");
    }

    public static void setBankAccounts(Bank bank, List<Account> accounts) throws ReflectiveOperationException{

        if (bank.capacity() < accounts.size()) {
            throw new IllegalArgumentException("Internal error: Tried to set more accounts than the bank can hold.");
        }

        setBankSize(bank, accounts.size());

        Field accountsField = Bank.class.getDeclaredField("accounts");
        accountsField.setAccessible(true);
        accountsField.set(bank, accounts.toArray(i -> new Account[bank.capacity()]));

        for (Account account : accounts) {
            setBank(account, bank);
        }
    }

    public static void setBankSize(Bank bank, int size) throws ReflectiveOperationException {
        Field sizeField = Bank.class.getDeclaredField("size");
        sizeField.setAccessible(true);
        sizeField.set(bank, size);
    }

    public static void setBank(Account account, Bank bank) throws ReflectiveOperationException {
        Field bankField = Account.class.getDeclaredField("bank");
        bankField.setAccessible(true);
        bankField.set(account, bank);
    }

    public static int getBankSize(Bank bank) throws ReflectiveOperationException {
        Field sizeField = Bank.class.getDeclaredField("size");
        sizeField.setAccessible(true);
        return (int) sizeField.get(bank);
    }

    public static Account[] getBankAccounts(Bank bank) throws ReflectiveOperationException {
        Field accountsField = Bank.class.getDeclaredField("accounts");
        accountsField.setAccessible(true);
        return (Account[]) accountsField.get(bank);
    }

    public static void setAccountIban(Account account, long iban) throws ReflectiveOperationException {
        Field ibanField = Account.class.getDeclaredField("iban");
        ibanField.setAccessible(true);
        ibanField.set(account, iban);
    }

    public static void setHistorySize(TransactionHistory history, int size) throws ReflectiveOperationException {
        Field sizeField = TransactionHistory.class.getDeclaredField("size");
        sizeField.setAccessible(true);
        sizeField.set(history, size);
    }

    public static void setHistoryNextIndex(TransactionHistory history, int nextIndex) throws ReflectiveOperationException {
        Field nextIndexField = TransactionHistory.class.getDeclaredField("nextIndex");
        nextIndexField.setAccessible(true);
        nextIndexField.set(history, nextIndex);
    }

    public static void setHistoryTransactions(TransactionHistory history, Transaction[] transactions) throws ReflectiveOperationException {
        Field transactionsField = TransactionHistory.class.getDeclaredField("transactions");
        transactionsField.setAccessible(true);
        transactionsField.set(history, transactions);
    }

    public static int getHistoryNextIndex(TransactionHistory history) throws ReflectiveOperationException {
        Field nextIndexField = TransactionHistory.class.getDeclaredField("nextIndex");
        nextIndexField.setAccessible(true);
        return (int) nextIndexField.get(history);
    }

    public static Transaction[] getHistoryTransactions(TransactionHistory history) throws ReflectiveOperationException {
        Field transactionsField = TransactionHistory.class.getDeclaredField("transactions");
        transactionsField.setAccessible(true);
        return (Transaction[]) transactionsField.get(history);
    }

    public static void setTransferableBanks(Bank bank, Bank[] transferableBanks) throws ReflectiveOperationException {
        Field transferableBanksField = Bank.class.getDeclaredField("transferableBanks");
        transferableBanksField.setAccessible(true);
        transferableBanksField.set(bank, transferableBanks);
    }

    private static class DefaultConvertersMap extends HashMap<String, Function<JsonNode, ?>> {

        public DefaultConvertersMap(Map<? extends String, ? extends Function<JsonNode, ?>> m) {
            super(m);
        }

        @Override
        public Function<JsonNode, ?> get(Object key) {
            return super.getOrDefault(key, JsonNode::asInt);
        }
    }

}
