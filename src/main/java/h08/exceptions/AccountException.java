package h08.exceptions;

import h08.Account;

public class AccountException extends RuntimeException{
    public AccountException(String s) {
        super(s);
    }

    public AccountException(long iban) {
        super("Cannot find Account with IBAN: " + iban);
    }

    public AccountException(String message, Account account){
        super(message + account.toString());
    }
}
