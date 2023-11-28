package h08;

import java.time.LocalDate;

/**
 * Represents a customer.
 *
 * @param firstName   the first name of the customer
 * @param lastName    the last name of the customer
 * @param address     the address of the customer
 * @param dateOfBirth the date of birth of the customer
 */
public record Customer(
    String firstName,
    String lastName,
    String address,
    LocalDate dateOfBirth
) {
    static final int MIN_AGE = 18;

    /**
     * Constructs a new customer.
     *
     * @param firstName   the first name of the customer
     * @param lastName    the last name of the customer
     * @param address     the address of the customer
     * @param dateOfBirth the date of birth of the customer
     */
    public Customer {
        assert firstName != null;
        assert lastName != null;
        assert address != null;
        assert dateOfBirth != null;
    }
}
