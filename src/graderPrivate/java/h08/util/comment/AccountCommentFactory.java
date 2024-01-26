package h08.util.comment;

import h08.Account;
import h08.Transaction;

public class AccountCommentFactory extends CommentFactory<Account> {

    private CustomerCommentFactory customerCommentFactory;
    private boolean iban;
    private boolean balance;
    private BankCommentFactory bankCommentFactory;
    private TransactionCommentFactory transactionCommentFactory;

    private boolean newLine;

    public static final AccountCommentFactory IBAN_ONLY = new AccountCommentFactory().iban();
    public static final AccountCommentFactory NAME_ONLY = new NameOnlyAccountCommentFactory();

    @Override
    public String build(Account account) {

        StringBuilder builder = new StringBuilder("Account{");
        boolean first = true;

        if (customerCommentFactory != null) {
            builder.append("customer=").append(customerCommentFactory.build(account.getCustomer()));
            first = false;
        }
        if (iban) {
            if (!first) {
                builder.append(", ");
            }
            builder.append("iban=").append(account.getIban());
            first = false;
        }
        if (balance) {
            if (!first) {
                builder.append(", ");
            }
            builder.append("balance=").append(account.getBalance());
            first = false;
        }
        if (bankCommentFactory != null) {
            if (!first) {
                builder.append(", ");
            }
            builder.append("bank=").append(bankCommentFactory.build(account.getBank()));
            first = false;
        }
        if (transactionCommentFactory != null) {
            if (!first) {
                builder.append(", ");
            }
            builder.append("history=[");

            boolean firstTransaction = true;

            for (Transaction transaction : account.getHistory().getTransactions()) {
                if (!firstTransaction) {
                    builder.append(", ");
                    if (newLine) {
                        builder.append("\n    ");
                    }
                }
                builder.append(transactionCommentFactory.build(transaction));
                firstTransaction = false;
            }

            builder.append(']');

        }
        builder.append('}');

        return builder.toString();
    }

    public AccountCommentFactory customer(CustomerCommentFactory customerCommentFactory) {
        this.customerCommentFactory = customerCommentFactory;
        return this;
    }

    public AccountCommentFactory iban() {
        this.iban = true;
        return this;
    }

    public AccountCommentFactory balance() {
        this.balance = true;
        return this;
    }

    public AccountCommentFactory bank(BankCommentFactory bankCommentFactory) {
        this.bankCommentFactory = bankCommentFactory;
        return this;
    }

    public AccountCommentFactory transaction(TransactionCommentFactory transactionCommentFactory) {
        this.transactionCommentFactory = transactionCommentFactory;
        return this;
    }

    public AccountCommentFactory newLine() {
        this.newLine = true;
        return this;
    }

    private static class NameOnlyAccountCommentFactory extends AccountCommentFactory {
        @Override
        public String build(Account account) {

            if (account.getCustomer() == null) {
                return account.toString();
            }

            return "Account{name='" + account.getCustomer().firstName() + " " + account.getCustomer().lastName() + "'}";
        }
    }
}
