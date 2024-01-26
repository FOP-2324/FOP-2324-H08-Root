package h08;

import h08.implementations.TestBank;
import h08.transformer.SystemNanoTimeTransformer;
import h08.util.ParameterResolver;
import h08.util.comment.AccountCommentFactory;
import h08.util.comment.BankCommentFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.MockedConstruction;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.sourcegrade.jagr.api.testing.extension.JagrExecutionCondition;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSet;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSetTest;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.*;

@SuppressWarnings("DuplicatedCode")
@TestForSubmission
public class H2_2_Test extends H08_TestBase {


    @AfterEach
    public void resetSystemNanoTime() {
        SystemNanoTimeTransformer.systemNanoTime = -1;
    }

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @JsonParameterSetTest(value = "H2_2_generateIban.json", customConverters = "customConverters")
    public void testGenerateIbanUnusedIban(JsonParameterSet params) throws ReflectiveOperationException {

        TestBank bank = params.get("bank", TestBank.class);
        List<Account> accounts = params.get("accounts", List.class);
        Customer customer = params.get("customerToAdd", Customer.class);

        setBankAccounts(bank, accounts);

        long seed = 2;

        List<Long> usedIbans = accounts.stream().map(Account::getIban).toList();

        long expectedIban = generateExpectedIban(customer, seed, usedIbans);

        Context context = contextBuilder()
            .subject("Bank#generateIban(Customer, long)")
            .add("accounts", AccountCommentFactory.IBAN_ONLY.build(accounts))
            .add("customer.hashcode()", customer.hashCode())
            .add("seed", seed)
            .add("expectedIban", expectedIban)
            .build();

        long actualIban = callObject(() -> bank.generateIban(customer, seed), context,
            TR -> "Bank#generateIban(Customer, long) threw an unexpected exception.");

        assertEquals(expectedIban, actualIban, context, TR -> "Wrong iban generated when the first generated iban is not used.");

        context = contextBuilder()
            .subject("Bank#generateIban(Customer, long)")
            .add("accounts", AccountCommentFactory.IBAN_ONLY.build(accounts))
            .add("customer.hashcode()", customer.hashCode())
            .add("seed", -seed)
            .add("expectedIban", expectedIban)
            .build();

        actualIban = callObject(() -> bank.generateIban(customer, -seed), context,
            TR -> "Bank#generateIban(Customer, long) threw an unexpected exception.");

        assertEquals(expectedIban, actualIban, context, TR -> "Wrong iban generated when the first generated iban is not used.");

    }

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @JsonParameterSetTest(value = "H2_2_generateIban.json", customConverters = "customConverters")
    public void testGenerateIbanUsedIban(JsonParameterSet params) throws ReflectiveOperationException {

        TestBank bank = params.get("bank", TestBank.class);
        List<Account> accounts = params.get("accounts", List.class);
        Customer customer = params.get("customerToAdd", Customer.class);

        setBankAccounts(bank, accounts);

        long seed = 2;

        long nextIban = Math.abs(customer.hashCode() * seed);
        for (Account account : accounts) {
            setAccountIban(account, nextIban);
            nextIban = Math.abs(customer.hashCode() * nextIban);
        }

        List<Long> usedIbans = accounts.stream().map(Account::getIban).toList();

        long expectedIban = generateExpectedIban(customer, seed, usedIbans);

        Context context = contextBuilder()
            .subject("Bank#generateIban(Customer, long)")
            .add("accounts", AccountCommentFactory.IBAN_ONLY.build(accounts))
            .add("customer.hashcode()", customer.hashCode())
            .add("seed", seed)
            .add("expectedIban", expectedIban)
            .build();

        long actualIban = callObject(() -> bank.generateIban(customer, seed), context,
            TR -> "Bank#generateIban(Customer, long) threw an unexpected exception.");

        assertEquals(expectedIban, actualIban, context, TR -> "Wrong iban generated when the iban has to be created multiple times.");
    }

    private long generateExpectedIban(Customer customer, long seed, List<Long> usedIbans) {
        long iban = Math.abs(customer.hashCode() * seed);
        if (usedIbans.contains(iban)) {
            iban = generateExpectedIban(customer, iban, usedIbans);
        }
        return iban;
    }

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @ExtendWith(JagrExecutionCondition.class)
    @JsonParameterSetTest(value = "H2_2_add_Normal.json", customConverters = "customConverters")
    public void testAccountCreation(JsonParameterSet params) throws ReflectiveOperationException {

        TestBank bank = params.get("bank", TestBank.class);
        List<Account> accounts = params.get("accounts", List.class);
        Customer customer = params.get("customerToAdd", Customer.class);
        int ibanToGenerate = params.get("ibanToGenerate", int.class);
        int systemNanoTime = params.get("systemNanoTime", int.class);
        int transactionHistoryCapacity = params.get("transactionHistoryCapacity", int.class);

        setBankAccounts(bank, accounts);

        bank.setTransactionHistoryCapacity(transactionHistoryCapacity);

        SystemNanoTimeTransformer.systemNanoTime = systemNanoTime;
        bank.ibanToGenerate = ibanToGenerate;
        bank.generateIbanCallsActual = false;

        List<Account> createdAccounts = new ArrayList<>();
        List<Account> createdMocks = new ArrayList<>();

        Context context = contextBuilder()
            .subject("Bank#add()")
            .add("customerToAdd", customer)
            .add("bank", new BankCommentFactory().size().capacity().build(bank))
            .add("System.nanoTime()", systemNanoTime)
            .add("generateIban(...)", ibanToGenerate)
            .add("transactionHistoryCapacity", transactionHistoryCapacity)
            .build();

        try (MockedConstruction<Account> mockedConstruction = mockConstruction(Account.class,
            withSettings().defaultAnswer(CALLS_REAL_METHODS), (mock, mockContext) -> {
                Account account = ParameterResolver.createAccount(
                    (Customer) mockContext.arguments().get(0),
                    (long) mockContext.arguments().get(1),
                    (double) mockContext.arguments().get(2),
                    (Bank) mockContext.arguments().get(3),
                    (TransactionHistory) mockContext.arguments().get(4));

                createdAccounts.add(account);
                createdMocks.add(mock);
            }
        )) {

            call(() -> bank.add(customer), context, TR -> "Bank#add() threw and unexpected exception.");

        }

        assertEquals(1, createdAccounts.size(), context, TR -> "Wrong number of accounts created.");

        Account createdAccount = createdAccounts.get(0);
        Account[] actualAccounts = getBankAccounts(bank);

        context = contextBuilder()
            .subject("Bank#add()")
            .add("bank", new BankCommentFactory().size().capacity().build(bank))
            .add("customer", customer)
            .add("previous accounts", AccountCommentFactory.NAME_ONLY.build(accounts))
            .add("System.nanoTime()", systemNanoTime)
            .add("generateIban(...) return value", ibanToGenerate)
            .add("transactionHistoryCapacity", transactionHistoryCapacity)
            .add("created account", createdMocks.get(0))
            .add("actual accounts", AccountCommentFactory.NAME_ONLY.build(actualAccounts))
            .build();

        assertEquals(customer, createdAccount.getCustomer(), context, TR -> "Customer of the created account is not the given customer.");
        assertEquals(1, bank.generateIbanCallCount, context, TR -> "generateIban(...) was not called exactly once.");
        assertEquals((long) systemNanoTime, bank.generateIbanlastSeed, context, TR -> "Wrong seed passed to generateIban(...).");
        assertEquals(customer, bank.generateIbanLastCustomer, context, TR -> "Customer passed to generateIban(...) is not the given customer.");
        assertEquals((long) ibanToGenerate, createdAccount.getIban(), context, TR -> "The created account does not have the correct iban.");
        assertEquals(0.0, createdAccount.getBalance(), context, TR -> "The created account does not have a balance of 0.0.");
        assertEquals(bank, createdAccount.getBank(), context, TR -> "The created account does not have the correct bank.");
        assertEquals(transactionHistoryCapacity, createdAccount.getHistory().capacity(), context, TR -> "The TransactionHistory of created account does not have the correct capacity.");
        assertEquals(accounts.size() + 1, getBankSize(bank), context, TR -> "The bank size was not increased by 1.");
        assertEquals(createdMocks.get(0), actualAccounts[accounts.size()], context, TR -> "The created account was not added to the bank at account[size]. Expected: " + createdAccount + ", Actual: " + actualAccounts[accounts.size()]);

        for (int i = 0; i < accounts.size(); i++) {
            int finalI = i;
            assertSame(accounts.get(i), actualAccounts[i], context, TR -> "The account at index " + finalI + " was changed.");
        }
    }

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @JsonParameterSetTest(value = "H2_2_add_Exception.json", customConverters = "customConverters")
    public void testException(JsonParameterSet params) throws ReflectiveOperationException {

        TestBank bank = params.get("bank", TestBank.class);
        List<Account> accounts = params.get("accounts", List.class);
        Customer customer = params.get("customerToAdd", Customer.class);
        int ibanToGenerate = params.get("ibanToGenerate", int.class);
        int systemNanoTime = params.get("systemNanoTime", int.class);
        int transactionHistoryCapacity = params.get("transactionHistoryCapacity", int.class);

        setBankAccounts(bank, accounts);

        bank.setTransactionHistoryCapacity(transactionHistoryCapacity);

        SystemNanoTimeTransformer.systemNanoTime = systemNanoTime;
        bank.ibanToGenerate = ibanToGenerate;
        bank.generateIbanCallsActual = false;

        Context context = contextBuilder()
            .subject("Bank#add()")
            .add("bank", new BankCommentFactory().size().capacity().build(bank))
            .add("customer", customer)
            .add("System.nanoTime()", systemNanoTime)
            .add("generateIban(...)", ibanToGenerate)
            .add("transactionHistoryCapacity", transactionHistoryCapacity)
            .build();

        assertExceptionThrown(() -> bank.add(customer), context, IllegalStateException.class, "Bank is full");
        checkBankSizeAndAccountsUnchanged(bank, accounts, context);
    }

}
