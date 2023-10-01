package h08.exceptions;


import h08.Bank;

public class NoSuchBankException extends BankException {
    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     */
    public NoSuchBankException(int bic) {
        super(bic < 0 ? "invalid bic!" : bic + "can't be found!",null);
    }
}
