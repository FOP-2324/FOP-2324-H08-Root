package h08.util.comment;

import h08.Transaction;

import java.time.Period;
import java.time.temporal.ChronoUnit;

@SuppressWarnings("unused")
public class TransactionCommentFactory extends CommentFactory<Transaction> {

    AccountCommentFactory sourceAccountCommentFactory;
    AccountCommentFactory targetAccountCommentFactory;
    private boolean amount;
    private boolean transactionNumber;
    private boolean description;
    private boolean date;
    private boolean status;

    private boolean daysOld;

    public static final TransactionCommentFactory NUMBER_ONLY = new TransactionCommentFactory().transactionNumber();

    @Override
    public String build(Transaction transaction) {

        StringBuilder builder = new StringBuilder("Transaction{");
        boolean first = true;

        if (sourceAccountCommentFactory != null) {
            builder.append("sourceAccount=").append(sourceAccountCommentFactory.build(transaction.sourceAccount()));
            first = false;
        }
        if (targetAccountCommentFactory != null) {
            if (!first) {
                builder.append(", ");
            }
            builder.append("targetAccount=").append(targetAccountCommentFactory.build(transaction.targetAccount()));
            first = false;
        }
        if (amount) {
            if (!first) {
                builder.append(", ");
            }
            builder.append("amount=").append(transaction.amount());
            first = false;
        }
        if (transactionNumber) {
            if (!first) {
                builder.append(", ");
            }
            builder.append("transactionNumber=").append(transaction.transactionNumber());
            first = false;
        }
        if (description) {
            if (!first) {
                builder.append(", ");
            }
            builder.append("description='").append(transaction.description()).append('\'');
            first = false;
        }
        if (date) {
            if (!first) {
                builder.append(", ");
            }
            builder.append("date=").append(transaction.date());
            first = false;
        }
        if (status) {
            if (!first) {
                builder.append(", ");
            }
            builder.append("status=").append(transaction.status());
        }
        if (daysOld) {
            if (!first) {
                builder.append(", ");
            }
            builder.append("daysOld=").append(ChronoUnit.DAYS.between(transaction.date(), java.time.LocalDate.now()));
        }

        builder.append('}');

        return builder.toString();
    }

    public TransactionCommentFactory sourceAccount(AccountCommentFactory sourceAccountCommentFactory) {
        this.sourceAccountCommentFactory = sourceAccountCommentFactory;
        return this;
    }

    public TransactionCommentFactory targetAccount(AccountCommentFactory targetAccountCommentFactory) {
        this.targetAccountCommentFactory = targetAccountCommentFactory;
        return this;
    }

    public TransactionCommentFactory amount() {
        this.amount = true;
        return this;
    }

    public TransactionCommentFactory transactionNumber() {
        this.transactionNumber = true;
        return this;
    }

    public TransactionCommentFactory description() {
        this.description = true;
        return this;
    }

    public TransactionCommentFactory date() {
        this.date = true;
        return this;
    }

    public TransactionCommentFactory status() {
        this.status = true;
        return this;
    }

    public TransactionCommentFactory daysOld() {
        this.daysOld = true;
        return this;
    }

}
