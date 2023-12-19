package h08;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonConverters extends org.tudalgo.algoutils.tutor.general.json.JsonConverters {

    public static final Map<String, Bank> nameToBank = new HashMap<>();
    public static int nextBic = 0;

    public static void reset() {
        nameToBank.clear();
        nextBic = 0;
    }

    public static List<Account> toAccountList(JsonNode node) {
        return toList(node, JsonConverters::toAccount);
    }

    public static Account toAccount(JsonNode node) {
        return new Account(toCustomer(node.get("customer")),
            node.get("iban").asLong(),
            node.get("balance").asDouble(),
            toBank(node.get("bank")),
            new TransactionHistory());
    }

    public static Customer toCustomer(JsonNode node) {
        return new Customer(
            node.get("firstName").asText(),
            node.get("lastName").asText(),
            node.get("address").asText(),
            LocalDate.now().minusYears(node.get("age").asInt()).minusDays(1));
    }

    public static Bank toBank(JsonNode node) {
        return nameToBank.computeIfAbsent(node.get("name").asText(), name -> new Bank(name, nextBic++, node.get("capacity").asInt()));
    }

    public static Transaction toTransaction(JsonNode node) {
        return new Transaction(
            toAccount(node.get("sourceAccount")),
            toAccount(node.get("targetAccount")),
            node.get("amount").asDouble(),
            node.get("transactionNumber").asLong(),
            node.get("description").asText(),
            toDate(node.get("date")),
            toStatus(node.get("status"))
            );
    }

    public static LocalDate toDate(JsonNode node) {
        return LocalDate.now().minusYears(node.asInt()).minusDays(1);
    }

    public static Status toStatus(JsonNode node) {
        return Status.valueOf(node.asText());
    }

}
