package h08;

import h08.implementations.TestBank;
import h08.util.ParameterResolver;
import h08.util.StudentLinks;
import h08.util.comment.AccountCommentFactory;
import h08.util.comment.BankCommentFactory;
import h08.util.comment.TransactionCommentFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.MockedConstruction;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.student.CrashException;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSet;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSetTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.*;

@TestForSubmission
public class H5_4_Test extends H08_TestBase {

    private static MockedConstruction<Transaction> transactionMockedConstruction;

    @BeforeAll
    public static void mockTransaction() {
        if (ParameterResolver.createTransactionFailed) {
            transactionMockedConstruction = mockConstruction(Transaction.class,
                withSettings().defaultAnswer(CALLS_REAL_METHODS), (mock, context) -> {

                    when(mock.sourceAccount()).thenReturn((Account) context.arguments().get(0));
                    when(mock.targetAccount()).thenReturn((Account) context.arguments().get(1));
                    when(mock.amount()).thenReturn((double) context.arguments().get(2));
                    when(mock.transactionNumber()).thenReturn((long) context.arguments().get(3));
                    when(mock.description()).thenReturn((String) context.arguments().get(4));
                    when(mock.date()).thenReturn((LocalDate) context.arguments().get(5));
                    when(mock.status()).thenReturn((Status) context.arguments().get(6));
                });
        }
    }

    @AfterAll
    public static void closeTransactionMock() {
        if (transactionMockedConstruction != null) {
            transactionMockedConstruction.close();
        }
    }

    private void setupTransactions(List<Account> accounts) throws Exception {

        Map<Account, Transaction[]> originalTransactions = accounts.stream()
            .collect(Collectors.toMap(account -> account, account -> account.getHistory().getTransactions()));

        for (Account sourceAccount : accounts) {
            for (Transaction transaction : originalTransactions.get(sourceAccount)) {
                Account targetAccount = accounts.stream()
                    .filter(acc -> acc.getIban() == transaction.targetAccount().getIban())
                    .findFirst()
                    .get();

                Transaction newTransaction = ParameterResolver.createTransaction(
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

    private void setupBanks(Bank sourceBank, Bank targetBank, List<Account> accounts) throws ReflectiveOperationException {

        List<Account> sourceBankAccounts = new ArrayList<>();
        List<Account> targetBankAccounts = new ArrayList<>();

        for (Account account : accounts) {

            if (account.getBank().getBic() == sourceBank.getBic()) {
                sourceBankAccounts.add(account);
            } else if (account.getBank().getBic() == targetBank.getBic()) {
                targetBankAccounts.add(account);
            } else {
                throw new IllegalArgumentException("Internal Error: The account " + account.getIban() + " does not belong to the source or target bank");
            }

        }

        setBankAccounts(sourceBank, sourceBankAccounts);
        setBankAccounts(targetBank, targetBankAccounts);

        long highestTransactionNumber = accounts.stream()
            .map(Account::getHistory)
            .flatMap(history -> Arrays.stream(history.getTransactions()))
            .mapToLong(Transaction::transactionNumber)
            .max()
            .orElse(0);

        TestBank.transactionNumberToGenerate = highestTransactionNumber + 1;
    }

    private void makeAllOneWeekOld(List<Account> accounts) throws Exception {
        for (Account account : accounts) {
            for (Transaction transaction : account.getHistory().getTransactions()) {
                account.getHistory().update(new Transaction(
                    transaction.sourceAccount(),
                    transaction.targetAccount(),
                    transaction.amount(),
                    transaction.transactionNumber(),
                    transaction.description(),
                    LocalDate.now().minusWeeks(1),
                    transaction.status()
                ));
            }
        }
    }

    private static Context createContext(Bank sourceBank, Bank targetBank, List<Account> accounts) {

        AccountCommentFactory acf = new AccountCommentFactory()
            .iban()
            .transaction(new TransactionCommentFactory()
                .targetAccount(AccountCommentFactory.IBAN_ONLY)
                .sourceAccount(AccountCommentFactory.IBAN_ONLY)
                .transactionNumber()
                .status()
                .daysOld()
            )
            .newLine();

        BankCommentFactory bcf = new BankCommentFactory()
            .bic()
            .size()
            .capacity();

        return contextBuilder()
            .subject("Bank#checkOpenTransactions")
            .add("sourceBank", bcf.build(sourceBank))
            .add("targetBank", bcf.build(targetBank))
            .add("sourceBank accounts", AccountCommentFactory.IBAN_ONLY.build(Arrays.asList(sourceBank.getAccounts())))
            .add("targetBank accounts", AccountCommentFactory.IBAN_ONLY.build(Arrays.asList(targetBank.getAccounts())))
            .add("all accounts", acf.build(accounts, "\n\n"))
            .build();
    }

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @JsonParameterSetTest(value = "H5_4.json", customConverters = "customConverters")
    public void testReturnArraySize(JsonParameterSet params) throws Exception {

        TestBank sourceBank = params.get("sourceBank", TestBank.class);
        TestBank targetBank = params.get("targetBank", TestBank.class);
        List<Account> accounts = params.get("accounts", List.class);

        setupBanks(sourceBank, targetBank, accounts);
        setupTransactions(accounts);
        makeAllOneWeekOld(accounts);

        sourceBank.transferCallsActual = false;

        Context context = createContext(sourceBank, targetBank, accounts);

        List<Transaction> expectedOpenTransactions = getOpenTransactions(sourceBank);

        Transaction[] actualOpenTransactions = callObject(sourceBank::checkOpenTransactions, context,
            TR -> "bank#checkOpenTransactions threw an unexpected exception");

        assertEquals(expectedOpenTransactions.size(), actualOpenTransactions.length, context,
            TR -> "The returned array has the wrong size");
    }

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @JsonParameterSetTest(value = "H5_4.json", customConverters = "customConverters")
    public void testOldTransactionsCancelled(JsonParameterSet params) throws Exception {

        TestBank sourceBank = params.get("sourceBank", TestBank.class);
        TestBank targetBank = params.get("targetBank", TestBank.class);
        List<Account> accounts = params.get("accounts", List.class);

        setupBanks(sourceBank, targetBank, accounts);
        setupTransactions(accounts);

        sourceBank.transferCallsActual = false;

        Context context = createContext(sourceBank, targetBank, accounts);

        Map<Account, List<Transaction>> originalTransactions = getOriginalTransactions(accounts);

        try {
            sourceBank.checkOpenTransactions();
        } catch (CrashException e) {
            call(() -> {throw e;});
        } catch (Exception e) {
            if (!e.getClass().equals(StudentLinks.TRANSACTION_EXCEPTION_LINK.get().reflection())) {
                fail(context, TR -> "The method bank#checkOpenTransactions threw an unexpected exception of type " +
                    e.getClass().getName() + ".\n" + getStackTrace(e));
            }
        }

        for (Account account : accounts) {
            List<Transaction> actualTransactions = Arrays.asList(account.getHistory().getTransactions());

            for (int i = 0; i < originalTransactions.get(account).size(); i++) {

                Transaction originalTransaction = originalTransactions.get(account).get(i);
                Transaction actualTransaction = actualTransactions.get(i);

                if (originalTransaction.status() == Status.OPEN) {

                    if (!originalTransaction.sourceAccount().equals(account)) {
                        continue;
                    }

                    boolean olderThanTwoWeeks = originalTransaction.date().plusWeeks(2).isBefore(LocalDate.now());

                    if (olderThanTwoWeeks) {
                        assertNotSame(originalTransaction, actualTransaction, context,
                            TR -> "The transaction with transactionNumber " + originalTransaction.transactionNumber() + " was not changed for Account{iban=" + account.getIban() + "}.");

                        Transaction expectedTransaction = new Transaction(
                            originalTransaction.sourceAccount(),
                            originalTransaction.targetAccount(),
                            originalTransaction.amount(),
                            originalTransaction.transactionNumber(),
                            originalTransaction.description(),
                            originalTransaction.date(),
                            Status.CANCELLED
                        );

                        assertTransactionEquals(expectedTransaction, actualTransaction, context,
                            "The transaction with transactionNumber " + originalTransaction.transactionNumber() + " was not changed correctly for Account{iban=" + account.getIban() + "}");
                    }

                } else {
                    assertTransactionEquals(originalTransaction, actualTransaction, context,
                        "The transaction with transactionNumber " + originalTransaction.transactionNumber() + " was changed for Account{iban=" + account.getIban() + "} even though it was not open");
                }

            }
        }
    }

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @JsonParameterSetTest(value = "H5_4.json", customConverters = "customConverters")
    public void testOldTransactionsTransferredAgain(JsonParameterSet params) throws Exception {

        TestBank sourceBank = params.get("sourceBank", TestBank.class);
        TestBank targetBank = params.get("targetBank", TestBank.class);
        List<Account> accounts = params.get("accounts", List.class);

        setupBanks(sourceBank, targetBank, accounts);
        setupTransactions(accounts);

        sourceBank.transferCallsActual = false;

        Context context = createContext(sourceBank, targetBank, accounts);

        List<Transaction> transactionsToTransfer = getOpenTransactions(sourceBank).stream()
            .filter(transaction -> transaction.date().plusWeeks(2).isBefore(LocalDate.now()))
            .filter(transaction -> transaction.date().plusWeeks(4).isAfter(LocalDate.now()))
            .toList();

        try {
            sourceBank.checkOpenTransactions();
        } catch (CrashException e) {
            call(() -> {throw e;});
        } catch (Exception e) {
            if (!e.getClass().equals(StudentLinks.TRANSACTION_EXCEPTION_LINK.get().reflection())) {
                fail(context, TR -> "The method bank#checkOpenTransactions threw an unexpected exception of type " +
                    e.getClass().getName() + ".\n" + getStackTrace(e));
            }
        }

        assertEquals(transactionsToTransfer.size(), sourceBank.transferCalls.size(), context,
            TR -> "The method bank#checkOpenTransactions did not call Bank#transfer the correct number of times.");

        for (Transaction transaction : transactionsToTransfer) {

            TestBank.TransferCall expectedTransferCall = new TestBank.TransferCall(
                transaction.sourceAccount().getIban(),
                transaction.targetAccount().getIban(),
                transaction.targetAccount().getBank().getBic(),
                transaction.amount(),
                transaction.description()
            );

            assertTrue(sourceBank.transferCalls.contains(expectedTransferCall), context,
                TR -> "Did not find a call to Bank#transfer that matches the transaction with transactionNumber " + transaction.transactionNumber() + ".");
        }
    }

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @JsonParameterSetTest(value = "H5_4.json", customConverters = "customConverters")
    public void testReturnArrayContent(JsonParameterSet params) throws Exception {

        TestBank sourceBank = params.get("sourceBank", TestBank.class);
        TestBank targetBank = params.get("targetBank", TestBank.class);
        List<Account> accounts = params.get("accounts", List.class);

        setupBanks(sourceBank, targetBank, accounts);
        setupTransactions(accounts);
        makeAllOneWeekOld(accounts);

        sourceBank.transferCallsActual = false;

        Context context = createContext(sourceBank, targetBank, accounts);

        List<Transaction> expectedOpenTransactions = getOpenTransactions(sourceBank);

        Transaction[] actualOpenTransactions = callObject(sourceBank::checkOpenTransactions, context,
            TR -> "bank#checkOpenTransactions threw an unexpected exception");

        assertEquals(expectedOpenTransactions.size(), actualOpenTransactions.length, context,
            TR -> "The returned array has the wrong size");

        List<Transaction> actualOpenTransactionsList = Arrays.asList(actualOpenTransactions);

        for (Transaction expectedTransaction : expectedOpenTransactions) {
            assertTrue(actualOpenTransactionsList.contains(expectedTransaction), context,
                TR -> "The returned array does not contain the transaction with transactionNumber " + expectedTransaction.transactionNumber() + ".");
        }

    }

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @JsonParameterSetTest(value = "H5_4.json", customConverters = "customConverters")
    public void testOldTransactionsExceptionThrown(JsonParameterSet params) throws Exception {

        TestBank sourceBank = params.get("sourceBank", TestBank.class);
        TestBank targetBank = params.get("targetBank", TestBank.class);
        List<Account> accounts = params.get("accounts", List.class);

        setupBanks(sourceBank, targetBank, accounts);
        setupTransactions(accounts);

        sourceBank.transferCallsActual = false;

        Context context = createContext(sourceBank, targetBank, accounts);

        List<Transaction> olderThanFourWeeks = getOpenTransactions(sourceBank).stream()
            .filter(transaction -> transaction.date().plusWeeks(4).isBefore(LocalDate.now()))
            .toList();

        List<Transaction[]> constructorCalls = new ArrayList<>();

        try (MockedConstruction<?> mockedConstruction = mockConstruction(StudentLinks.TRANSACTION_EXCEPTION_LINK.get().reflection(),
            withSettings().defaultAnswer(CALLS_REAL_METHODS), (mock, mockContext) -> {

            if (mockContext.arguments().size() == 1 && mockContext.arguments().get(0) instanceof Transaction[]) {
                constructorCalls.add((Transaction[]) mockContext.arguments().get(0));
            }

        })) {
            try {
                sourceBank.checkOpenTransactions();
                if (!olderThanFourWeeks.isEmpty()) {
                    fail(context, TR -> "The method bank#checkOpenTransactions did not throw an exception when transactions that are older than 4 weeks exist.");
                }
            } catch (CrashException e) {
                call(() -> {throw e;});
            } catch (Exception e) {
                if (e.getClass().equals(StudentLinks.TRANSACTION_EXCEPTION_LINK.get().reflection())) {

                    assertEquals(1, constructorCalls.size(), context,
                        TR -> "The constructor TransactionException(Transaction[]) wasn't called exactly once.");

                    Transaction[] actualTransactions = constructorCalls.get(0);

                    assertEquals(olderThanFourWeeks.size(), actualTransactions.length, context,
                        TR -> "The constructor TransactionException(Transaction[]) was called with the wrong number of transactions.");

                    for (Transaction actualTransaction : actualTransactions) {

                        Optional<Transaction> originalTransaction = olderThanFourWeeks.stream()
                            .filter(transaction -> transaction.transactionNumber() == actualTransaction.transactionNumber())
                            .findFirst();

                        assertTrue(originalTransaction.isPresent(), context,
                            TR -> "The constructor TransactionException(Transaction[]) was called with a transaction with transactionNumber %d which wasn't expected."
                                .formatted(actualTransaction.transactionNumber()));


                        Transaction expectedTransaction = new Transaction(
                            originalTransaction.get().sourceAccount(),
                            originalTransaction.get().targetAccount(),
                            originalTransaction.get().amount(),
                            originalTransaction.get().transactionNumber(),
                            originalTransaction.get().description(),
                            originalTransaction.get().date(),
                            Status.CANCELLED
                        );

                        assertTransactionEquals(expectedTransaction, actualTransaction, context,
                            "The constructor TransactionException(Transaction[]) was called with a incorrect transaction with transactionNumber %d"
                                .formatted(actualTransaction.transactionNumber()));

                    }

                } else {

                    fail(context, TR -> "The method bank#checkOpenTransactions threw an unexpected exception of type " +
                        e.getClass().getName() + ".\n" + getStackTrace(e));
                }
            }
        }

    }

    private String getStackTrace(Throwable throwable) {
        StringBuilder sb = new StringBuilder();

        sb.append(throwable.getClass().getName()).append(": ").append(throwable.getMessage()).append("\n");

        StackTraceElement[] stackTrace = throwable.getStackTrace();

        for (int i = 0; i < 7 && i < stackTrace.length; i++) {

            sb.append("    at: ").append(stackTrace[i]).append("\n");
        }

        return sb.toString();
    }

    private Map<Account, List<Transaction>> getOriginalTransactions(List<Account> accounts) {
        return accounts.stream()
            .collect(Collectors.toMap(account -> account, account -> Arrays.asList(account.getHistory().getTransactions())));
    }

    private List<Transaction> getOpenTransactions(Bank sourceBank) {
        return Arrays.stream(sourceBank.getAccounts())
            .flatMap(account -> Arrays.stream(account.getHistory().getTransactions(Status.OPEN)))
            .collect(Collectors.toList());
    }

}
