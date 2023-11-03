package h08;

import java.time.LocalDate;

public record Transaction(
    Account sourceAccount,
    Account targetAccount,
    double amount,
    long transactionNumber,
    String description,
    LocalDate date,
    Status status

) {
}
