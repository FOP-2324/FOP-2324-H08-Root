package h08.exceptions;


public class NoSuchBankException extends BankException {
    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     */
    public NoSuchBankException() {
        super("Invalid BIC! Can't find receiver´s bank!");
    }
}
