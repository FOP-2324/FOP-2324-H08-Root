package h08;

import h08.implementations.TestBank;
import org.junit.jupiter.params.ParameterizedTest;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSet;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSetTest;

import java.util.*;

import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.*;

@TestForSubmission
@SuppressWarnings("DuplicatedCode")
public class H2_3_Test extends H08_TestBase {

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @JsonParameterSetTest(value = "H2_3.json", customConverters = "customConverters")
    public void testNormal(JsonParameterSet params) throws ReflectiveOperationException {
        TestBank bank = params.get("bank", TestBank.class);
        List<Account> accounts = new ArrayList<Account>(params.get("accounts", List.class));
        List<Long> ibansToRemove = params.get("ibansToRemove", List.class);

        setBankAccounts(bank, accounts);

        for (long ibanToRemove : ibansToRemove) {

            Account expectedRemoved = accounts.stream()
                .filter(account -> account.getIban() == ibanToRemove)
                .findFirst()
                .get();
            accounts.remove(expectedRemoved);

            Account actualRemoved = callObject(() -> bank.remove(ibanToRemove), contextBuilder()
                .subject("Bank#remove()")
                .add("ibanToRemove", ibanToRemove)
                .add("expectedAccounts", accounts)
                .add("expectedRemoved", expectedRemoved)
                .build(),
                TR -> "bank.remove() threw an unexpected exception.");

            List<Account> actualAccounts = Arrays.asList(getBankAccounts(bank));

            Context context = contextBuilder()
                .subject("Bank#remove()")
                .add("ibanToRemove", ibanToRemove)
                .add("expectedAccounts", accounts)
                .add("actualAccounts", actualAccounts)
                .add("expectedRemoved", expectedRemoved)
                .add("actualRemoved", actualRemoved)
                .build();

            assertEquals(expectedRemoved, actualRemoved, context, TR -> "bank.remove() did not return the correct Account.");
            assertEquals(accounts.size(), getBankSize(bank), context, TR -> "bank.remove() did not decrease the bank's size by 1.");

            for (int i = 0; i < accounts.size(); i++) {
                int index = i;
                assertNotNull(actualAccounts.get(index), context, TR -> "bank.remove() caused an unexpected null value in the accounts array at index." + index);
            }

            for (Account account : accounts) {
                assertTrue(actualAccounts.contains(account), context, TR -> "the accounts array does not contain an account that has not been removed.");
            }

            assertFalse(actualAccounts.contains(expectedRemoved), context, TR -> "the accounts array contains the removed account.");
        }
    }

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @JsonParameterSetTest(value = "H2_3_Exception.json", customConverters = "customConverters")
    public void testException(JsonParameterSet params) throws ReflectiveOperationException {
        TestBank bank = params.get("bank", TestBank.class);
        List<Account> accounts = params.get("accounts", List.class);
        List<Long> unusedIbans = params.get("unusedIbans", List.class);

        setBankAccounts(bank, accounts);

        for (long unusedIban : unusedIbans) {

            Context context = contextBuilder()
                .subject("Bank#remove()")
                .add("ibanToRemove", unusedIban)
                .add("accounts", accounts)
                .build();

            if (unusedIban >= 0) {
                checkExceptionThrown(() -> bank.remove(unusedIban), context, NoSuchElementException.class, Long.toString(unusedIban));
            } else {
                checkExceptionThrown(() -> bank.remove(unusedIban), context, AssertionError.class);
            }

            checkBankSizeAndAccountsUnchanged(bank, accounts, context);
        }
    }
}
