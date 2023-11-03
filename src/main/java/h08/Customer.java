package h08;

import java.time.LocalDate;

public record Customer(
    String firstName,
    String lastName,
    String address,
    LocalDate dateOfBirth
) {


}
