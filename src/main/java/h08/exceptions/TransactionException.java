package h08.exceptions;

import h08.Transaction;

public class TransactionException extends Exception{

    /**
     * Constructs a new transaction exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public TransactionException(String message, long transactionNumber) {
        super(message + transactionNumber);
    }

    public TransactionException(Transaction[] transactions) {
        super(getTransactionNumbers(transactions));
    }

    public static String getTransactionNumbers(Transaction[] transactions){
        StringBuilder sb = new StringBuilder("Transaction numbers:");
        for (int i = 0; i < transactions.length;i++) {
            sb.append(transactions[i].transactionNumber());
            if(i < transactions.length -1)
                sb.append("|");
        }
        return sb.toString();
    }
}
