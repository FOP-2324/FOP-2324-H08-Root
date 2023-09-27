package h08.exceptions;

public class TooManyAccountsException extends AccountException{
    /**
     * Constructs a new account exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public TooManyAccountsException(String message) {
        super(message);
    }
}
