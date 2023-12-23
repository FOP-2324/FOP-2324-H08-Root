package h08;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDate;
import java.util.List;

public class JsonConverters extends org.tudalgo.algoutils.tutor.general.json.JsonConverters {

    public static List<Account> toAccountList(JsonNode node) {
        if (node.isTextual() && node.asText().equals("all")) {
            return ParameterResolver.getAllAccounts();
        }
        return toList(node, JsonConverters::toAccount);
    }

    public static Account toAccount(JsonNode node) {
        if (node.isTextual()) {
            return ParameterResolver.getAccount(node.asText());
        }

        return ParameterResolver.getAccount(node.get("id").asText(), node);
    }

    public static Customer toCustomer(JsonNode node) {
        if (node.isTextual()) {
            return ParameterResolver.getCustomer(node.asText());
        }

        return ParameterResolver.getCustomer(node.get("id").asText(), node);
    }

    public static Bank toBank(JsonNode node) {
        if (node.isTextual()) {
            return ParameterResolver.getBank(node.asText());
        }

        return ParameterResolver.getBank(node.get("id").asText(), node);
    }

    public static List<Transaction> toTransactionList(JsonNode node) {
        if (node.isTextual() && node.asText().equals("all")) {
            return ParameterResolver.getAllTransactions();
        }
        return toList(node, JsonConverters::toTransaction);
    }

    public static Transaction toTransaction(JsonNode node) {
        if (node.isTextual()) {
            return ParameterResolver.getTransaction(node.asText());
        }

        return ParameterResolver.getTransaction(node.get("id").asText(), node);
    }

    public static TransactionHistory toHistory(JsonNode node) {
        throw new UnsupportedOperationException();
    }

    public static LocalDate toDate(JsonNode node) {

        if (node.isNumber()) {
            throw new IllegalArgumentException();
        }

        LocalDate date = LocalDate.now();

        if (node.has("year")) {
            date = date.plusYears(node.get("year").asLong());
        }
        if (node.has("month")) {
            date = date.plusMonths(node.get("month").asLong());
        }
        if (node.has("day")) {
            date = date.plusDays(node.get("day").asLong());
        }

        return date;
    }

    public static Status toStatus(JsonNode node) {
        return Status.valueOf(node.asText());
    }

}
