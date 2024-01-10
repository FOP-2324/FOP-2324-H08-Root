package h08;

import h08.util.ParameterResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSet;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSetTest;

import java.time.LocalDate;
import java.time.Period;

import static h08.util.StudentLinks.BAD_TIME_STAMP_EXCEPTION_LINK;
import static h08.util.StudentLinks.getClassOfTypeLink;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.call;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.contextBuilder;

@TestForSubmission
public class H4_1_Test extends H08_TestBase {

    @ParameterizedTest
    @JsonParameterSetTest(value = "H4_1.json", customConverters = "customConverters")
    public void testAgeRestriction(JsonParameterSet params) throws ClassNotFoundException {

        Customer customer = params.get("customer", Customer.class);
        int iban = params.get("iban", Integer.class);
        int balance = params.get("balance", Integer.class);
        Bank bank = params.get("bank", Bank.class);

        int customerAge = Period.between(customer.dateOfBirth(), LocalDate.now()).getYears();

        Context context = contextBuilder()
            .subject("Account#Account()")
            .add("customer", customer)
            .add("customer age", customerAge)
            .add("iban", iban)
            .add("balance", balance)
            .add("bank", bank)
            .add("transactionHistory", "[]")
            .add("current date", LocalDate.now())
            .build();

        if (customerAge < 18) {
            assertExceptionThrown(() -> new Account(customer, iban, balance, bank, new TransactionHistory()),
                context, getClassOfTypeLink(BAD_TIME_STAMP_EXCEPTION_LINK.get()), getExpectedBadTimeStepMessage(customer.dateOfBirth().toString()));
        } else {
            call(() -> new Account(customer, iban, balance, bank, new TransactionHistory()), context,
                TR -> "The constructor should not throw an exception for a customer with age " + customerAge + ".");
        }
    }

    @Test
    public void testAsserts() {
        Bank bank = new Bank("FOPBank", 1, 5);
        int customerAge = 20;
        Customer customer = ParameterResolver.createCustomer("Person", "A", "StreetA", LocalDate.now().minusYears(customerAge));
        long iban = 1;
        double balance = 10;
        TransactionHistory transactionHistory = new TransactionHistory();

        assertExceptionThrown(() -> new Account(null, iban, balance, bank, transactionHistory),
            contextBuilder()
                .subject("Account#Account()")
                .add("customer", null)
                .add("customer age", customerAge)
                .add("iban", iban)
                .add("balance", balance)
                .add("bank", bank)
                .add("transactionHistory", "[]")
                .build(), AssertionError.class);

        assertExceptionThrown(() -> new Account(customer, iban, balance, null, transactionHistory),
            contextBuilder()
                .subject("Account#Account()")
                .add("customer", customer)
                .add("customer age", customerAge)
                .add("iban", iban)
                .add("balance", balance)
                .add("bank", null)
                .add("transactionHistory", "[]")
                .build(), AssertionError.class);

        assertExceptionThrown(() -> new Account(customer, iban, balance, bank, null),
            contextBuilder()
                .subject("Account#Account()")
                .add("customer", customer)
                .add("customer age", customerAge)
                .add("iban", iban)
                .add("balance", balance)
                .add("bank", bank)
                .add("transactionHistory", null)
                .build(), AssertionError.class);

        assertExceptionThrown(() -> new Account(customer, -1, balance, bank, transactionHistory),
            contextBuilder()
                .subject("Account#Account()")
                .add("customer", customer)
                .add("customer age", customerAge)
                .add("iban", -1)
                .add("balance", balance)
                .add("bank", bank)
                .add("transactionHistory", "[]")
                .build(), AssertionError.class);
    }

}
