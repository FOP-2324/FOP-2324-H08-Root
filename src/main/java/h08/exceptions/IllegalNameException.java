package h08.exceptions;

import h08.Account;

public class IllegalNameException extends AccountException{

    /**
     * Constructs a new runtime exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this runtime exception's detail message.
     *
     *                permitted, and indicates that the cause is nonexistent or
     *                unknown.)
     * @since 1.4
     */
    public IllegalNameException(Account account) {
        super("name for account is invalid!",account);
    }
}
