package h08.exceptions;

public class BankException extends Exception{

    /**
     * Constructs a new BankException with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public BankException(String message) {
        super(message);
    }

    public BankException(int bic) {
        super("Cannot find Bank with BIC: " + bic);

    }
}