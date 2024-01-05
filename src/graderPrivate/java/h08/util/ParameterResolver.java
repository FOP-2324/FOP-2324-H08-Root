package h08.util;

import com.fasterxml.jackson.databind.JsonNode;
import h08.*;
import h08.implementations.TestBank;
import h08.implementations.TestTransactionHistory;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

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

    public static final Supplier<Customer> CUSTOMER_A = () -> new Customer("Person", "A", "Street A",
        LocalDate.of(2000, 1, 1));

    public static final Supplier<Customer> CUSTOMER_B = () -> new Customer("Person", "B", "Street B",
        LocalDate.of(1999, 1, 1));

    public static final Supplier<Customer> CUSTOMER_C = () -> new Customer("Person", "C", "Street C",
        LocalDate.of(1998, 1, 1));

    public static final Supplier<Customer> CUSTOMER_D = () -> new Customer("Person", "D", "Street D",
        LocalDate.of(1997, 1, 1));

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

        return new Customer(
            getOrDefault(overrides, "firstName", JsonNode::textValue, defaultCustomer.firstName()),
            getOrDefault(overrides, "lastName", JsonNode::textValue, defaultCustomer.lastName()),
            getOrDefault(overrides, "address", JsonNode::textValue, defaultCustomer.address()),
            getOrDefault(overrides, "dateOfBirth", JsonConverters::toDate, defaultCustomer.dateOfBirth())
        );
    }

    public static final Supplier<Account> ACCOUNT_A = () -> new Account(CUSTOMER_A.get(), 1, 10, FOP_BANK.get(), TestTransactionHistory.newInstance());

    public static final Supplier<Account> ACCOUNT_B = () -> new Account(CUSTOMER_B.get(), 2, 20, FOP_BANK.get(), TestTransactionHistory.newInstance());

    public static final Supplier<Account> ACCOUNT_C = () -> new Account(CUSTOMER_C.get(), 4, 30, FOP_BANK.get(), TestTransactionHistory.newInstance());

    public static final Supplier<Account> ACCOUNT_D = () -> new Account(CUSTOMER_D.get(), 10000, 40, FOP_BANK.get(), TestTransactionHistory.newInstance());

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

        return new Account(
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

    public static final Supplier<Transaction> TRANSACTION_1 = () -> new Transaction(
        ACCOUNT_A.get(),
        ACCOUNT_C.get(),
        2,
        1,
        "transaction 1",
        LocalDate.of(2020, 1, 1),
        Status.OPEN
    );

    public static final Supplier<Transaction> TRANSACTION_2 = () -> new Transaction(
        ACCOUNT_A.get(),
        ACCOUNT_C.get(),
        3,
        2,
        "transaction 2",
        LocalDate.of(2020, 1, 2),
        Status.CLOSED
    );

    public static final Supplier<Transaction> TRANSACTION_3 = () -> new Transaction(
        ACCOUNT_B.get(),
        ACCOUNT_C.get(),
        4,
        3,
        "transaction 3",
        LocalDate.of(2020, 1, 3),
        Status.CANCELLED
    );

    public static final Supplier<Transaction> TRANSACTION_4 = () -> new Transaction(
        ACCOUNT_A.get(),
        ACCOUNT_C.get(),
        5,
        4,
        "transaction 4",
        LocalDate.of(2020, 1, 4),
        Status.OPEN
    );

    public static final Supplier<Transaction> TRANSACTION_5 = () -> new Transaction(
        ACCOUNT_D.get(),
        ACCOUNT_B.get(),
        6,
        5,
        "transaction 5",
        LocalDate.of(2020, 1, 5),
        Status.CLOSED
    );

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

        return new Transaction(
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
