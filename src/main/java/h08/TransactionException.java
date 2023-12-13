package h08;

// TODO H3
/**
 * Signals that a transaction action has failed.
 */
public class TransactionException extends Exception {

    /**
     * Constructs a new transaction exception with the specified detail message.
     *
     * @param message           the detail message
     * @param transactionNumber the transaction number that is invalid
     */
    public TransactionException(String message, long transactionNumber) {
        super(message + " " + transactionNumber);
    }

    /**
     * Constructs a new transaction exception with the specified transaction numbers of the transactions that are
     * invalid.
     *
     * @param transactions the transactions that are invalid
     */
    public TransactionException(Transaction[] transactions) {
        super(getTransactionNumbers(transactions));
    }

    /**
     * Maps the specified transactions to their transaction numbers and returns a string representation of the mapping.
     *
     * @param transactions the transactions to map
     * @return a string representation of the mapping of the specified transactions to their transaction numbers
     */
    private static String getTransactionNumbers(Transaction[] transactions) {
        // Alternatively via String concatenation
        StringBuilder sb = new StringBuilder("Transaction numbers: [");
        for (int i = 0; i < transactions.length; i++) {
            sb.append(transactions[i].transactionNumber());
            if (i < transactions.length - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

}
