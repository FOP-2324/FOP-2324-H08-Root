package h08;

import java.time.LocalDate;

/**
 * Signals that a timestamp is invalid.
 */
public class BadTimestampException extends RuntimeException {

    /**
     * Constructs a new bad timestamp exception with the specified date.
     *
     * @param date the date that is invalid
     */
    public BadTimestampException(LocalDate date) {
        super("Bad timestamp: " + date);
    }

}
