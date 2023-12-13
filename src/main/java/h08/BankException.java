package h08;

// TODO H3
/**
 * Signals that a bank action has failed.
 */
public class BankException extends Exception {
    /**
     * Constructs a new bank exception with the specified detail message.
     *
     * @param message the detail message
     */
    public BankException(String message) {
        super(message);
    }

    /**
     * Constructs a new bank exception with the specified BIC.
     *
     * @param bic the BIC that is invalid
     */
    public BankException(long bic) {
        super("Cannot find Bank with BIC: " + bic);
    }

}
