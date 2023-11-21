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
        if (dateOfBirth.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date of birth cannot be in the future!");
        }
    }
}
