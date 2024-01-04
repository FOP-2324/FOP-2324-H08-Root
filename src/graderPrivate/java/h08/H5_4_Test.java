package h08;

import org.junit.jupiter.params.ParameterizedTest;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSet;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSetTest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.*;

@TestForSubmission
public class H5_4_Test extends H08_TestBase {

    private void setupTransactions(List<Account> accounts) throws Exception {

        Map<Account, Transaction[]> originalTransactions = accounts.stream()
            .collect(Collectors.toMap(account -> account, account -> account.getHistory().getTransactions()));

        for (Account sourceAccount : accounts) {
            for (Transaction transaction : originalTransactions.get(sourceAccount)) {
                Account targetAccount = accounts.stream()
                    .filter(acc -> acc.getIban() == transaction.targetAccount().getIban())
                    .findFirst()
                    .get();

                Transaction newTransaction = new Transaction(
                    sourceAccount,
                    targetAccount,
                    transaction.amount(),
                    transaction.transactionNumber(),
                    transaction.description(),
                    transaction.date(),
                    transaction.status()
                );

                sourceAccount.getHistory().update(newTransaction);
                targetAccount.getHistory().add(newTransaction);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @JsonParameterSetTest(value = "H5_4.json", customConverters = "customConverters")
    public void testReturnArraySize(JsonParameterSet params) throws Exception {

        TestBank bank = params.get("bank", TestBank.class);
        List<Account> accounts = params.get("accounts", List.class);

        setBankAccounts(bank, accounts);
        setupTransactions(accounts);

        Context context = contextBuilder()
            .subject("Bank#checkOpenTransactions")
            .add("bank", bank)
            .add("accounts", accounts)
            .build();

        List<Transaction> expectedOpenTransactions = getOpenTransactions(accounts);

        Transaction[] actualOpenTransactions = callObject(bank::checkOpenTransactions, context,
            TR -> "bank#checkOpenTransactions threw an unexpected exception");

        assertEquals(expectedOpenTransactions.size(), actualOpenTransactions.length, context,
            TR -> "The returned array has the wrong size");
    }

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @JsonParameterSetTest(value = "H5_4.json", customConverters = "customConverters")
    public void testOldTransactionsCancelled(JsonParameterSet params) throws Exception {

        TestBank bank = params.get("bank", TestBank.class);
        List<Account> accounts = params.get("accounts", List.class);

        setBankAccounts(bank, accounts);
        setupTransactions(accounts);

        Context context = contextBuilder()
            .subject("Bank#checkOpenTransactions")
            .add("bank", bank)
            .add("accounts", accounts)
            .build();

        Map<Account, List<Transaction>> originalTransactions = getOriginalTransactions(accounts);

        bank.checkOpenTransactions();
        call(bank::checkOpenTransactions, context, TR -> "bank#checkOpenTransactions threw an unexpected exception");

        for (Account account : accounts) {
            List<Transaction> actualTransactions = Arrays.asList(account.getHistory().getTransactions());

            for (int i = 0; i < originalTransactions.get(account).size(); i++) {

                Transaction originalTransaction = originalTransactions.get(account).get(i);
                Transaction actualTransaction = actualTransactions.get(i);

                if (originalTransaction.status() == Status.OPEN) {

                    boolean olderThanTwoWeeks = originalTransaction.date().plusWeeks(2).isBefore(LocalDate.now());

                    if (olderThanTwoWeeks) {
                        assertNotSame(originalTransaction, actualTransaction, context,
                            TR -> "The transaction with transactionNumber " + originalTransaction.transactionNumber() + " was not changed");

                        Transaction expectedTransaction = new Transaction(
                            originalTransaction.sourceAccount(),
                            originalTransaction.targetAccount(),
                            originalTransaction.amount(),
                            originalTransaction.transactionNumber(),
                            originalTransaction.description(),
                            originalTransaction.date(),
                            Status.CANCELLED
                        );

                        assertEquals(expectedTransaction, actualTransaction, context,
                            TR -> "The transaction with transactionNumber " + originalTransaction.transactionNumber() + " was not changed correctly");
                    }

                } else {
                    assertSame(originalTransaction, actualTransaction, context,
                        TR -> "The transaction with transactionNumber " + originalTransaction.transactionNumber() + " was changed");
                }

            }
        }
    }

    private Map<Account, List<Transaction>> getOriginalTransactions(List<Account> accounts) {
        return accounts.stream()
            .collect(Collectors.toMap(account -> account, account -> Arrays.asList(account.getHistory().getTransactions())));
    }

    private List<Transaction> getOpenTransactions(List<Account> accounts) {
        return accounts.stream()
            .flatMap(account -> Arrays.stream(account.getHistory().getTransactions(Status.OPEN)))
            .collect(Collectors.toList());
    }

}
