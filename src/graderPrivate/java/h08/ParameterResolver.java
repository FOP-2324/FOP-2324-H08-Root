package h08;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class ParameterResolver {

    public static final Supplier<Bank> FOP_BANK = () -> new Bank("FOP Bank", 1, 5);

    public static final Map<String, Supplier<Bank>> idToBank = Map.of(
        "FOPBank", FOP_BANK
    );

    public static Bank getBank(String id) {
        return idToBank.get(id).get();
    }

    public static Bank getBank(String id, JsonNode overrides) {

        Bank defaultBank = idToBank.get(id).get();

        return new Bank(
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

    public static final Supplier<Account> ACCOUNT_A = () -> new Account(CUSTOMER_A.get(), 1, 10, FOP_BANK.get(), new TransactionHistory());

    public static final Supplier<Account> ACCOUNT_B = () -> new Account(CUSTOMER_B.get(), 2, 20, FOP_BANK.get(), new TransactionHistory());

    public static final Supplier<Account> ACCOUNT_C = () -> new Account(CUSTOMER_C.get(), 0, 30, FOP_BANK.get(), new TransactionHistory());

    public static final Supplier<Account> ACCOUNT_D = () -> new Account(CUSTOMER_D.get(), 10000, 40, FOP_BANK.get(), new TransactionHistory());

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
        Object[] args = new Object[5];

        Account defaultAccount = idToAccount.get(id).get();
        args[0] = getOrDefault(overrides, "customer", JsonConverters::toCustomer, defaultAccount.getCustomer());
        args[1] = getOrDefault(overrides, "iban", JsonNode::longValue, defaultAccount.getIban());
        args[2] = getOrDefault(overrides, "balance", JsonNode::doubleValue, defaultAccount.getBalance());
        args[3] = getOrDefault(overrides, "bank", JsonConverters::toBank, defaultAccount.getBank());
        args[4] = getOrDefault(overrides, "history", JsonConverters::toHistory, defaultAccount.getHistory());

        return new Account((Customer) args[0], (long) args[1], (double) args[2], (Bank) args[3], (TransactionHistory) args[4]);
    }

    public static List<Account> getAllAccounts() {
        return List.of(ACCOUNT_A.get(), ACCOUNT_B.get(), ACCOUNT_C.get(), ACCOUNT_D.get());
    }

    public static final Supplier<Transaction> TRANSACTION_1 = () -> new Transaction(
        ACCOUNT_A.get(),
        ACCOUNT_B.get(),
        2,
        1,
        "transaction 1",
        LocalDate.of(2020, 1, 1),
        Status.OPEN);

    public static final Supplier<Transaction> TRANSACTION_2 = () -> new Transaction(
        ACCOUNT_A.get(),
        ACCOUNT_C.get(),
        3,
        2,
        "transaction 2",
        LocalDate.of(2020, 1, 2),
        Status.CLOSED);

    public static final Supplier<Transaction> TRANSACTION_3 = () -> new Transaction(
        ACCOUNT_C.get(),
        ACCOUNT_B.get(),
        4,
        3,
        "transaction 3",
        LocalDate.of(2020, 1, 3),
        Status.CANCELLED);

    public static final Map<String, Supplier<Transaction>> idToTransaction = Map.of(
        "transaction1", TRANSACTION_1,
        "transaction2", TRANSACTION_2,
        "transaction3", TRANSACTION_3
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
        return List.of(TRANSACTION_1.get(), TRANSACTION_2.get(), TRANSACTION_3.get());
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
