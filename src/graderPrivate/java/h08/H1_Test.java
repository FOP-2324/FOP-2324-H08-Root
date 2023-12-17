package h08;

import org.junit.jupiter.api.Test;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;

import java.time.LocalDate;

import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.call;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.contextBuilder;

@TestForSubmission
public class H1_Test extends H08_TestBase {

    @Test
    public void test() {

        String firstName = "Max";
        String lastName = "Mustermann";
        String address = "Musterstraße 1, 12345 Musterstadt";
        LocalDate date = LocalDate.now().minusYears(18);

        checkExceptionThrown(() -> new Customer(null, lastName, address, date), contextBuilder()
                .subject("Customer#Customer()")
                .add("firstName", "null")
                .add("lastName", lastName)
                .add("address", address)
                .add("date", date)
                .build(), AssertionError.class);

        checkExceptionThrown(() -> new Customer(firstName, null, address, date), contextBuilder()
            .subject("Customer#Customer()")
            .add("firstName", firstName)
            .add("lastName", "null")
            .add("address", address)
            .add("date", date)
            .build(), AssertionError.class);

        checkExceptionThrown(() -> new Customer(firstName, lastName, null, date), contextBuilder()
            .subject("Customer#Customer()")
            .add("firstName", firstName)
            .add("lastName", lastName)
            .add("address", "null")
            .add("date", date)
            .build(), AssertionError.class);

        checkExceptionThrown(() -> new Customer(firstName, lastName, address, null), contextBuilder()
            .subject("Customer#Customer()")
            .add("firstName", firstName)
            .add("lastName", lastName)
            .add("address", address)
            .add("date", "null")
            .build(), AssertionError.class);

        call(() -> new Customer(firstName, lastName, address, date), contextBuilder()
            .subject("Customer#Customer()")
            .add("firstName", firstName)
            .add("lastName", lastName)
            .add("address", address)
            .add("date", date)
            .build(), TR -> "Customer#Customer() threw an unexpected exception.");

    }

}
