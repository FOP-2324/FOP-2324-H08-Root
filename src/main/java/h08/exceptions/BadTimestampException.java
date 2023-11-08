package h08.exceptions;

import java.time.LocalDate;

public class BadTimestampException extends RuntimeException{
    public BadTimestampException(LocalDate date) {
        super("The entered date is invalid!: " + date);
    }
}
