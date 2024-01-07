package h08;

import h08.implementations.TestBank;
import h08.implementations.TestTransactionHistory;
import h08.util.StudentLinks;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.MockedConstruction;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSet;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSetTest;
import org.tudalgo.algoutils.tutor.general.reflections.MethodLink;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.*;

@TestForSubmission
public class H5_3_Test extends H08_TestBase {

    public static final long TRANSACTION_NUMBER = 100L;

    private static MockedConstruction<Transaction> transactionMockedConstruction;

    @BeforeAll
    public static void mockTransaction() {
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

    @AfterAll
    public static void closeTransactionMock() {
        transactionMockedConstruction.close();
    }

    private void setup(JsonParameterSet params) throws ReflectiveOperationException {

        Account sender = params.get("sender", Account.class);
        Account receiver = params.get("receiver", Account.class);

        TestBank senderBank = (TestBank) sender.getBank();

        senderBank.transactionNumberToGenerate = TRANSACTION_NUMBER;

        setBankAccounts(senderBank, List.of(sender));
        senderBank.add(receiver.getBank());
        setBank(sender, senderBank);

        TestBank receiverBank = (TestBank) receiver.getBank();
        setBankAccounts(receiverBank, List.of(receiver));
        setBank(receiver, receiverBank);

        setTransferableBanks(senderBank, new Bank[]{receiverBank});
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "H5_3.json", customConverters = "customConverters")
    public void testInvalidSender(JsonParameterSet params) throws ReflectiveOperationException {
        Account sender = params.get("sender", Account.class);
        Account receiver = params.get("receiver", Account.class);
        double amount = params.get("amount", Integer.class);
        String description = params.get("description", String.class);
        long senderIban = params.get("invalidSenderIban", Long.class);

        setup(params);
        TestBank senderBank = (TestBank) sender.getBank();

        Context context = contextBuilder()
            .subject("Bank#transfer")
            .add("senderIban (invalid)", senderIban)
            .add("receiverIban", receiver.getIban())
            .add("receiverBic", receiver.getBank().getBic())
            .add("amount", amount)
            .add("description", description)
            .build();

        checkNoEffect(senderIban, receiver.getIban(), receiver.getBank().getBic(), sender.getBalance(), receiver.getBalance(), params, senderBank, context);
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "H5_3.json", customConverters = "customConverters")
    public void testInvalidBic(JsonParameterSet params) throws ReflectiveOperationException {
        Account sender = params.get("sender", Account.class);
        Account receiver = params.get("receiver", Account.class);
        double amount = params.get("amount", Integer.class);
        String description = params.get("description", String.class);
        int receiverBic = params.get("invalidReceiverBic", Integer.class);

        setup(params);

        TestBank senderBank = (TestBank) sender.getBank();

        Context context = contextBuilder()
            .subject("Bank#transfer")
            .add("senderIban", sender.getIban())
            .add("receiverIban", receiver.getIban())
            .add("receiverBic (invalid)", receiverBic)
            .add("amount", amount)
            .add("description", description)
            .build();

        checkNoEffect(sender.getIban(), receiver.getIban(), receiverBic, sender.getBalance(), receiver.getBalance(), params, senderBank, context);
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "H5_3.json", customConverters = "customConverters")
    public void testInvalidReceiver(JsonParameterSet params) throws ReflectiveOperationException {
        Account sender = params.get("sender", Account.class);
        Account receiver = params.get("receiver", Account.class);
        double amount = params.get("amount", Integer.class);
        String description = params.get("description", String.class);
        long receiverIban = params.get("invalidReceiverIban", Long.class);

        setup(params);

        TestBank senderBank = (TestBank) sender.getBank();

        Context context = contextBuilder()
            .subject("Bank#transfer")
            .add("senderIban", sender.getIban())
            .add("receiverIban (invalid)", receiverIban)
            .add("receiverBic", receiver.getBank().getBic())
            .add("amount", amount)
            .add("description", description)
            .build();

        checkNoEffect(sender.getIban(), receiverIban, receiver.getBank().getBic(), sender.getBalance(), receiver.getBalance(), params, senderBank, context);
    }

    private void checkNoEffect(long senderIban, long receiverIban, int receiverBic, double expectedSenderAmount, double expectedReceiverAmount,
                               JsonParameterSet params, TestBank bank, Context context) {
        double amount = params.getInt("amount");
        String description = params.getString("description");
        Account receiver = params.get("receiver", Account.class);
        Account sender = params.get("sender", Account.class);

        Status actual = callObject(
            () -> bank.transfer(senderIban, receiverIban, receiverBic, amount, description),
            context,
            TR -> "Bank#transfer threw an unexpected exception.");

        assertEquals(Status.CANCELLED, actual, context, TR -> "Bank#transfer did not return the correct status.");

        assertEquals(expectedReceiverAmount, receiver.getBalance(), context, TR -> "The receiver's balance changed.");

        assertEquals(expectedSenderAmount, sender.getBalance(), context, TR -> "The sender's balance changed.");

        assertEquals(0, ((TestTransactionHistory) receiver.getHistory()).addCalls, context,
            TR -> "The add method of the receiver's history has been called.");
    }


    @ParameterizedTest
    @JsonParameterSetTest(value = "H5_3.json", customConverters = "customConverters")
    public void testWithdrawExceptionReturnStatus(JsonParameterSet params) throws ReflectiveOperationException {
        testException(params, true, true);
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "H5_3.json", customConverters = "customConverters")
    public void testDepositExceptionReturnStatus(JsonParameterSet params) throws ReflectiveOperationException {
        testException(params, true, false);
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "H5_3.json", customConverters = "customConverters")
    public void testWithdrawExceptionHistory(JsonParameterSet params) throws ReflectiveOperationException {
        testException(params, false, true);
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "H5_3.json", customConverters = "customConverters")
    public void testDepositExceptionHistory(JsonParameterSet params) throws ReflectiveOperationException {
        testException(params, false, false);
    }

    private void testException(JsonParameterSet params, boolean checkReturnStatus, boolean withdraw) throws ReflectiveOperationException {
        Account sender = params.get("sender", Account.class);
        Account receiver = params.get("receiver", Account.class);
        double amount = params.get("amount", Integer.class);
        String description = params.get("description", String.class);

        setup(params);

        TestBank senderBank = (TestBank) sender.getBank();
        TestBank receiverBank = (TestBank) receiver.getBank();

        String methodName = withdraw ? "withdraw" : "deposit";

        if (withdraw) {
            senderBank.withdrawThrowsException = true;
            receiverBank.depositCallsActual = false;
        } else {
            receiverBank.depositThrowsException = true;
            senderBank.withdrawCallsActual = false;
        }

        Context context = contextBuilder()
            .subject("Bank#transfer")
            .add("senderIban", sender.getIban())
            .add("receiverIban", receiver.getIban())
            .add("receiverBic", receiver.getBank().getBic())
            .add("amount", amount)
            .add("description", description)
            .build();

        Status actual = callObject(
            () -> senderBank.transfer(sender.getIban(), receiver.getIban(), receiver.getBank().getBic(), amount, description),
            context,
            TR -> "Bank#transfer threw an unexpected exception when %s throws an exception.".formatted(methodName));

        assertEquals(1, ((TestTransactionHistory) sender.getHistory()).addCalls, context,
            TR -> "TransactionHistory#add was not called exactly once for the sender");
        assertEquals(1, ((TestTransactionHistory) receiver.getHistory()).addCalls, context,
            TR -> "TransactionHistory#add was not called exactly once for the receiver");

        if (withdraw) {
            assertEquals(1, senderBank.withdrawCallCount, context, TR -> "senderBank.withdraw has not been called exactly once");
            assertEquals(sender.getIban(), senderBank.withdrawIban, context, TR -> "senderBank.withdraw has not been called with the correct iban");
            assertEquals(amount, senderBank.withdrawAmount, context, TR -> "senderBank.withdraw has not been called with the correct amount to withdraw");
        } else {
            assertEquals(1, receiverBank.depositCallCount, context, TR -> "receiverBank.deposit has not been called exactly once");
            assertEquals(receiver.getIban(), receiverBank.depositIban, context, TR -> "receiverBank.deposit has not been called with the correct iban");
            assertEquals(amount, receiverBank.depositAmount, context, TR -> "receiverBank.deposit has not been called with the correct amount to deposit in the receiver's account");
        }

        if (checkReturnStatus) {
            assertEquals(Status.CANCELLED, actual, context,
                TR -> "Bank#transfer did not return the correct status when %s throws an exception.".formatted(methodName));
        } else {
            assertEquals(Status.CANCELLED, sender.getHistory().get(0).status(), context,
                TR -> "The status of the transaction added to the sender is not correct when %s throws an exception".formatted(methodName));
            assertEquals(Status.CANCELLED, receiver.getHistory().get(0).status(), context,
                TR -> "The status of the transaction added to the receiver is not correct when %s throws an exception".formatted(methodName));

            Transaction expected = new Transaction(sender, receiver, amount, TRANSACTION_NUMBER, description, LocalDate.now(), Status.CANCELLED);

            assertTransactionEquals(expected, sender.getHistory().get(0), context,
                "The transaction added to the sender is not correct when %s throws an exception".formatted(methodName));
            assertTransactionEquals(expected, receiver.getHistory().get(0), context,
                "The transaction added to the receiver is not correct when %s throws an exception".formatted(methodName));
        }
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "H5_3.json", customConverters = "customConverters")
    public void testSuccessReturnStatus(JsonParameterSet params) throws ReflectiveOperationException {
        testSuccess(params, true);
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "H5_3.json", customConverters = "customConverters")
    public void testSuccessHistory(JsonParameterSet params) throws ReflectiveOperationException {
        testSuccess(params, false);
    }

    private void testSuccess(JsonParameterSet params, boolean checkReturnStatus) throws ReflectiveOperationException {
        Account sender = params.get("sender", Account.class);
        Account receiver = params.get("receiver", Account.class);
        double amount = params.get("amount", Integer.class);
        String description = params.get("description", String.class);

        setup(params);

        TestBank senderBank = (TestBank) sender.getBank();
        TestBank receiverBank = (TestBank) receiver.getBank();

        senderBank.withdrawCallsActual = false;
        receiverBank.depositCallsActual = false;

        Context context = contextBuilder()
            .subject("Bank#transfer")
            .add("senderIban", sender.getIban())
            .add("receiverIban", receiver.getIban())
            .add("receiverBic", receiver.getBank().getBic())
            .add("amount", amount)
            .add("description", description)
            .build();

        Status actual = callObject(
            () -> senderBank.transfer(sender.getIban(), receiver.getIban(), receiver.getBank().getBic(), amount, description),
            context,
            TR -> "Bank#transfer threw an unexpected exception when withdraw and deposit is successful");

        assertEquals(1, senderBank.withdrawCallCount, context, TR -> "senderBank.withdraw has not been called exactly once");
        assertEquals(sender.getIban(), senderBank.withdrawIban, context, TR -> "senderBank.withdraw has not been called with the correct iban");
        assertEquals(amount, senderBank.withdrawAmount, context, TR -> "senderBank.withdraw has not been called with the correct amount to withdraw");

        assertEquals(1, receiverBank.depositCallCount, context, TR -> "receiverBank.deposit has not been called exactly once");
        assertEquals(receiver.getIban(), receiverBank.depositIban, context, TR -> "receiverBank.deposit has not been called with the correct iban");
        assertEquals(amount, receiverBank.depositAmount, context, TR -> "receiverBank.deposit has not been called with the correct amount to deposit in the receiver's account");

        if (checkReturnStatus) {
            assertEquals(Status.CLOSED, actual, context, TR -> "Bank#transfer did not return the correct status when withdraw and deposit is successful.");
        } else {
            assertEquals(Status.CLOSED, sender.getHistory().get(0).status(), context,
                TR -> "The status of the transaction added to the sender is not correct when withdraw and deposit is successful");
            assertEquals(Status.CLOSED, receiver.getHistory().get(0).status(), context,
                TR -> "The status of the transaction added to the receiver is not correct when withdraw and deposit is successful");

            Transaction expected = new Transaction(sender, receiver, amount, TRANSACTION_NUMBER, description, LocalDate.now(), Status.CLOSED);

            assertTransactionEquals(expected, sender.getHistory().get(0), context,
                "The transaction added to the sender is not correct when withdraw and deposit is successful");
            assertTransactionEquals(expected, receiver.getHistory().get(0), context,
                "The transaction added to the receiver is not correct when withdraw and deposit is successful");
        }
    }

    @Test
    public void testThrowsClause() {

        MethodLink transferLink = StudentLinks.createMethodLink(StudentLinks.createTypeLink("Bank"), "transfer");

        List<Class<?>> unexpectedExceptions = Arrays.stream(transferLink.reflection().getExceptionTypes())
            .filter(cls -> !RuntimeException.class.isAssignableFrom(cls))
            .toList();

        if (!unexpectedExceptions.isEmpty()) {
            Assertions.fail("The throws clause of Bank#transfer should not declare any non-runtime, but declares %s".formatted(unexpectedExceptions));
        }
    }

}
