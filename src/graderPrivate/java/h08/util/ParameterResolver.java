package h08.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.util.concurrent.AtomicDouble;
import h08.*;
import h08.implementations.TestBank;
import h08.implementations.TestTransactionHistory;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ParameterResolver {

    public static final Supplier<TestBank> FOP_BANK = () -> new TestBank("FOP Bank", 1, 5);

    public static final Supplier<TestBank> FOP_BANK_2 = () -> new TestBank("FOP Bank 2", 2, 10);

    public static final Map<String, Supplier<TestBank>> idToBank = Map.of(
        "FOPBank", FOP_BANK,
        "FOPBank2", FOP_BANK_2
    );

    public static TestBank getBank(String id) {
        return idToBank.get(id).get();
    }

    public static TestBank getBank(String id, JsonNode overrides) {

        TestBank defaultBank = idToBank.get(id).get();

        return new TestBank(
            getOrDefault(overrides, "name", JsonNode::textValue, defaultBank.getName()),
            getOrDefault(overrides, "bic", JsonNode::intValue, defaultBank.getBic()),
            getOrDefault(overrides, "capacity", JsonNode::intValue, defaultBank.capacity())
        );
    }

    public static final Supplier<Customer> CUSTOMER_A = () -> createCustomer("Person", "A", "Street A",
        LocalDate.of(2000, 1, 1));

    public static final Supplier<Customer> CUSTOMER_B = () -> createCustomer("Person", "B", "Street B",
        LocalDate.of(1999, 1, 1));

    public static final Supplier<Customer> CUSTOMER_C = () -> createCustomer("Person", "C", "Street C",
        LocalDate.of(1998, 1, 1));

    public static final Supplier<Customer> CUSTOMER_D = () -> createCustomer("Person", "D", "Street D",
        LocalDate.of(1997, 1, 1));

    public static Customer createCustomer(String firstName, String lastName, String address, LocalDate dateOfBirth) {
        try {
            return new Customer(firstName, lastName, address, dateOfBirth);
        } catch (Throwable e) {
            Customer customer = mock(Customer.class);
            when(customer.firstName()).thenReturn(firstName);
            when(customer.lastName()).thenReturn(lastName);
            when(customer.address()).thenReturn(address);
            when(customer.dateOfBirth()).thenReturn(dateOfBirth);

            return customer;
        }

    }

    public static final Map<String, Supplier<Customer>> idToCustomer = Map.of(
        "customerA", CUSTOMER_A,
        "customerB", CUSTOMER_B,
        "customerC", CUSTOMER_C,
        "customerD", CUSTOMER_D
    );

    public static Customer getCustomer(String id) {
        return idToCustomer.get(id).get();
    }

    public static Customer getCustomer(String id, JsonNode overrides) {

        Customer defaultCustomer = idToCustomer.get(id).get();

        return createCustomer(
            getOrDefault(overrides, "firstName", JsonNode::textValue, defaultCustomer.firstName()),
            getOrDefault(overrides, "lastName", JsonNode::textValue, defaultCustomer.lastName()),
            getOrDefault(overrides, "address", JsonNode::textValue, defaultCustomer.address()),
            getOrDefault(overrides, "dateOfBirth", JsonConverters::toDate, defaultCustomer.dateOfBirth())
        );
    }

    public static final Supplier<Account> ACCOUNT_A = () -> createAccount(CUSTOMER_A.get(), 1, 10, FOP_BANK.get(), TestTransactionHistory.newInstance());

    public static final Supplier<Account> ACCOUNT_B = () -> createAccount(CUSTOMER_B.get(), 2, 20, FOP_BANK.get(), TestTransactionHistory.newInstance());

    public static final Supplier<Account> ACCOUNT_C = () -> createAccount(CUSTOMER_C.get(), 4, 30, FOP_BANK.get(), TestTransactionHistory.newInstance());

    public static final Supplier<Account> ACCOUNT_D = () -> createAccount(CUSTOMER_D.get(), 10000, 40, FOP_BANK.get(), TestTransactionHistory.newInstance());

    public static Account createAccount(Customer customer, long iban, double balance, Bank bank, TransactionHistory history) {
        try {
            return new Account(customer, iban, balance, bank, history);
        } catch (Throwable e) {
            Account account = mock(Account.class);

            AtomicReference<TransactionHistory> transactionHistory = new AtomicReference<>(history);
            AtomicDouble balanceRef = new AtomicDouble(balance);

            when(account.getCustomer()).thenReturn(customer);
            when(account.getIban()).thenReturn(iban);
            when(account.getBalance()).thenAnswer(invocation -> balanceRef.get());
            when(account.getBank()).thenReturn(bank);
            when(account.getHistory()).thenAnswer(invocation -> transactionHistory.get());

            doAnswer(invocation -> {
                balanceRef.set(invocation.getArgument(0));
                return null;
            }).when(account).setBalance(anyDouble());

            doAnswer(invocation -> {
                transactionHistory.set(invocation.getArgument(0));
                return null;
            }).when(account).setHistory(any());


            return account;
        }
    }

    public static final Map<String, Supplier<Account>> idToAccount = Map.of(
        "accountA", ACCOUNT_A,
        "accountB", ACCOUNT_B,
        "accountC", ACCOUNT_C,
        "accountD", ACCOUNT_D
    );

    public static Account getAccount(String id) {
        return idToAccount.get(id).get();
    }

    public static Account getAccount(String id, JsonNode overrides) {

        Account defaultAccount = idToAccount.get(id).get();

        return createAccount(
            getOrDefault(overrides, "customer", JsonConverters::toCustomer, defaultAccount.getCustomer()),
            getOrDefault(overrides, "iban", JsonNode::longValue, defaultAccount.getIban()),
            getOrDefault(overrides, "balance", JsonNode::doubleValue, defaultAccount.getBalance()),
            getOrDefault(overrides, "bank", JsonConverters::toBank, defaultAccount.getBank()),
            getOrDefault(overrides, "history", JsonConverters::toHistory, defaultAccount.getHistory())
        );
    }

    public static List<Account> getAllAccounts() {
        return List.of(ACCOUNT_A.get(), ACCOUNT_B.get(), ACCOUNT_C.get(), ACCOUNT_D.get());
    }

    public static final Supplier<Transaction> TRANSACTION_1 = () -> createTransaction(
        ACCOUNT_A.get(),
        ACCOUNT_C.get(),
        2,
        1,
        "transaction 1",
        LocalDate.of(2020, 1, 1),
        Status.OPEN
    );

    public static final Supplier<Transaction> TRANSACTION_2 = () -> createTransaction(
        ACCOUNT_A.get(),
        ACCOUNT_C.get(),
        3,
        2,
        "transaction 2",
        LocalDate.of(2020, 1, 2),
        Status.CLOSED
    );

    public static final Supplier<Transaction> TRANSACTION_3 = () -> createTransaction(
        ACCOUNT_B.get(),
        ACCOUNT_C.get(),
        4,
        3,
        "transaction 3",
        LocalDate.of(2020, 1, 3),
        Status.CANCELLED
    );

    public static final Supplier<Transaction> TRANSACTION_4 = () -> createTransaction(
        ACCOUNT_A.get(),
        ACCOUNT_C.get(),
        5,
        4,
        "transaction 4",
        LocalDate.of(2020, 1, 4),
        Status.OPEN
    );

    public static final Supplier<Transaction> TRANSACTION_5 = () -> createTransaction(
        ACCOUNT_D.get(),
        ACCOUNT_B.get(),
        6,
        5,
        "transaction 5",
        LocalDate.of(2020, 1, 5),
        Status.CLOSED
    );

    public static Transaction createTransaction(Account sourceAccount,
                                                Account targetAccount,
                                                double amount,
                                                long transactionNumber,
                                                String description,
                                                LocalDate date,
                                                Status status) {
        try {
            return new Transaction(sourceAccount, targetAccount, amount, transactionNumber, description, date, status);
        } catch (Throwable e) {
            Transaction transaction = mock(Transaction.class);

            when(transaction.sourceAccount()).thenReturn(sourceAccount);
            when(transaction.targetAccount()).thenReturn(targetAccount);
            when(transaction.amount()).thenReturn(amount);
            when(transaction.transactionNumber()).thenReturn(transactionNumber);
            when(transaction.description()).thenReturn(description);
            when(transaction.date()).thenReturn(date);
            when(transaction.status()).thenReturn(status);

            return transaction;
        }
    }

    public static final Map<String, Supplier<Transaction>> idToTransaction = Map.of(
        "transaction1", TRANSACTION_1,
        "transaction2", TRANSACTION_2,
        "transaction3", TRANSACTION_3,
        "transaction4", TRANSACTION_4,
        "transaction5", TRANSACTION_5
    );

    public static Transaction getTransaction(String id) {
        return idToTransaction.get(id).get();
    }

    public static Transaction getTransaction(String id, JsonNode overrides) {

        Transaction defaultTransaction = idToTransaction.get(id).get();

        return createTransaction(
            getOrDefault(overrides, "sourceAccount", JsonConverters::toAccount, defaultTransaction.sourceAccount()),
            getOrDefault(overrides, "targetAccount", JsonConverters::toAccount, defaultTransaction.targetAccount()),
            getOrDefault(overrides, "amount", JsonNode::doubleValue, defaultTransaction.amount()),
            getOrDefault(overrides, "transactionNumber", JsonNode::longValue, defaultTransaction.transactionNumber()),
            getOrDefault(overrides, "description", JsonNode::textValue, defaultTransaction.description()),
            getOrDefault(overrides, "date", JsonConverters::toDate, defaultTransaction.date()),
            getOrDefault(overrides, "status", JsonConverters::toStatus, defaultTransaction.status())
        );
    }

    public static List<Transaction> getAllTransactions() {
        return List.of(TRANSACTION_1.get(), TRANSACTION_2.get(), TRANSACTION_3.get(), TRANSACTION_4.get(), TRANSACTION_5.get());
    }

    private static <T> T getOrDefault(JsonNode node,
                                       String key,
                                       Function<JsonNode,T> keyMapper,
                                       T defaultValue) {
        if (node.has(key)) {
            return keyMapper.apply(node.get(key));
        }
        return defaultValue;
    }
}
