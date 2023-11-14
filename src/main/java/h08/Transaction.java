package h08;

import h08.exceptions.BadTimestampException;
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
    public Transaction {
        if (amount <= 0)
            throw new IllegalArgumentException("Amount cannot be zero or negative!");
        if(date.isAfter(LocalDate.now()))
            throw new BadTimestampException(date);
    }

    @Override
    public String toString() {
        return "Transaction{" +
            "sourceAccount=" + sourceAccount +
            ", targetAccount=" + targetAccount +
            ", amount=" + amount +
            ", transactionNumber=" + transactionNumber +
            ", description='" + description + '\'' +
            ", date=" + date +
            ", status=" + status +
            '}';
    }
}
