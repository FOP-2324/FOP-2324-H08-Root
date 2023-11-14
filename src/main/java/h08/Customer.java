package h08;

import h08.exceptions.BadTimestampException;

import java.time.LocalDate;

public record Customer(
    String firstName,
    String lastName,
    String address,
    LocalDate dateOfBirth
) {

    public Customer {
        assert firstName != null;
        assert lastName != null;
        if (dateOfBirth().isAfter(LocalDate.now()))
            throw new BadTimestampException(dateOfBirth);
    }
}
