package h08;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.tudalgo.algoutils.tutor.general.annotation.SkipAfterFirstFailedTest;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import org.tudalgo.algoutils.tutor.general.callable.Callable;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static h08.StudentLinks.BAD_TIME_STAMP_EXCEPTION_CONSTRUCTOR_LINK;
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
        Map.entry("transactions", n -> JsonConverters.toList(n, JsonConverters::toTransaction)),
        Map.entry("customer", JsonConverters::toCustomer),
        Map.entry("date", JsonConverters::toDate),
        Map.entry("status", JsonConverters::toStatus),
        Map.entry("description", JsonNode::asText)
    ));

    @BeforeEach
    public void resetConverters() {
        JsonConverters.reset();
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
        } catch (Throwable t) {
            exception = t;
        }

        assertNotNull(exception, context, TR -> "No exception was thrown. Expected exception of type " + expectedExceptionClass.getName() + ".");
        assertEquals(expectedExceptionClass, exception.getClass(), context, TR -> "The thrown exception is not of the expected type.");
        if (expectedExceptionMessage !=  null) {
            assertEquals(expectedExceptionMessage, exception.getMessage(), context, TR -> "The thrown exception has the wrong message.");
        }
    }

    public static void setBankAccounts(Bank bank, List<Account> accounts) throws ReflectiveOperationException{

        if (bank.capacity() < accounts.size()) {
            throw new IllegalArgumentException("Internal error: Tried to set more accounts than the bank can hold.");
        }

        setBankSize(bank, accounts.size());

        Field accountsField = Bank.class.getDeclaredField("accounts");
        accountsField.setAccessible(true);
        accountsField.set(bank, accounts.toArray(i -> new Account[bank.capacity()]));
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
