package h08;

public class TransactionException extends Exception {
    public TransactionException(String message, long transactionNumber) {
        super(message + " " + transactionNumber);
    }

    public TransactionException(Transaction[] transactions) {
        super(getTransactionNumbers(transactions));
    }

    private static String getTransactionNumbers(Transaction[] transactions) {
        StringBuilder sb = new StringBuilder("Transaction numbers: [");
        for (int i = 0; i < transactions.length; i++) {
            sb.append(transactions[i].transactionNumber());
            if (i < transactions.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

}
