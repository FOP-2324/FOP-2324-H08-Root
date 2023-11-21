package h08;

import java.time.LocalDate;

/**
 * Represents a transaction between two accounts.
 *
 * @param sourceAccount     the source account of the transaction
 * @param targetAccount     the target account of the transaction
 * @param amount            the amount of money that is transferred
 * @param transactionNumber the transaction number of the transaction
 * @param description       the description of the transaction
 * @param date              the date of the transaction
 * @param status            the status of the transaction
 */
public record Transaction(
    Account sourceAccount,
    Account targetAccount,
    double amount,
    long transactionNumber,
    String description,
    LocalDate date,
    Status status

) {
    public Transaction {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount cannot be zero or negative!");
        }

        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date cannot be in the future!");
        }
    }
}
