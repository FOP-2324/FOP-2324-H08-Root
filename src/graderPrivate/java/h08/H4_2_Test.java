package h08;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSet;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSetTest;

import java.time.LocalDate;
import java.util.List;

import static h08.util.StudentLinks.BAD_TIME_STAMP_EXCEPTION_LINK;
import static h08.util.StudentLinks.getClassOfTypeLink;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.call;
import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.contextBuilder;

@TestForSubmission
public class H4_2_Test extends H08_TestBase {

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @JsonParameterSetTest(value = "H4_2.json", customConverters = "customConverters")
    public void testDateRestriction(JsonParameterSet params) throws ClassNotFoundException {

        List<Account> accounts = (List<Account>) params.get("accounts", List.class);
        int amount = params.get("amount", Integer.class);
        int transactionNumber = params.get("transactionNumber", Integer.class);
        String description = params.get("description", String.class);
        LocalDate date = params.get("date", LocalDate.class);
        Status status = params.get("status", Status.class);

        Account sourceAccount = accounts.get(0);
        Account targetAccount = accounts.get(1);

        boolean dateInFuture = date.isAfter(LocalDate.now());

        Context context = contextBuilder()
            .subject("Transaction#Transaction()")
            .add("sourceAccount", sourceAccount)
            .add("targetAccount", targetAccount)
            .add("amount", amount)
            .add("transactionNumber", transactionNumber)
            .add("description", description)
            .add("date", date)
            .add("status", status)
            .add("currentTime", LocalDate.now())
            .add("date in future", dateInFuture)
            .build();

        if (dateInFuture) {

            checkExceptionThrown(() -> new Transaction(sourceAccount, targetAccount, amount, transactionNumber, description, date, status),
                context, getClassOfTypeLink(BAD_TIME_STAMP_EXCEPTION_LINK.get()), getExpectedBadTimeStepMessage(date.toString()));
        } else {
            call(() -> new Transaction(sourceAccount, targetAccount, amount, transactionNumber, description, date, status), context,
                TR -> "The constructor should not throw an exception for a date that is not in the future.");
        }
    }

    @Test
    public void testAsserts() {
        Bank bank = new Bank("FOPBank", 1, 5);
        Account sourceAccount = new Account(
            new Customer("Person", "A", "StreetA", LocalDate.now().minusYears(20)),
            1,
            10,
            bank,
            new TransactionHistory());
        Account targetAccount = new Account(
            new Customer("Person", "B", "StreetB", LocalDate.now().minusYears(20)),
            2,
            10,
            bank,
            new TransactionHistory());
        int amount = 10;
        int transactionNumber = 1;
        String description = "description";
        LocalDate date = LocalDate.now().minusDays(1);
        Status status = Status.OPEN;

        checkExceptionThrown(() -> new Transaction(null, targetAccount, amount, transactionNumber, description, date, status),
            contextBuilder()
                .subject("Transaction#Transaction()")
                .add("sourceAccount", null)
                .add("targetAccount", targetAccount)
                .add("amount", amount)
                .add("transactionNumber", transactionNumber)
                .add("description", description)
                .add("date", date)
                .add("status", status)
                .add("currentTime", LocalDate.now())
                .add("date in future", false)
                .build(), AssertionError.class);

        checkExceptionThrown(() -> new Transaction(sourceAccount, null, amount, transactionNumber, description, date, status),
            contextBuilder()
                .subject("Transaction#Transaction()")
                .add("sourceAccount", sourceAccount)
                .add("targetAccount", null)
                .add("amount", amount)
                .add("transactionNumber", transactionNumber)
                .add("description", description)
                .add("date", date)
                .add("status", status)
                .add("currentTime", LocalDate.now())
                .add("date in future", false)
                .build(), AssertionError.class);

        checkExceptionThrown(() -> new Transaction(sourceAccount, targetAccount, amount, transactionNumber, null, date, status),
            contextBuilder()
                .subject("Transaction#Transaction()")
                .add("sourceAccount", sourceAccount)
                .add("targetAccount", targetAccount)
                .add("amount", amount)
                .add("transactionNumber", transactionNumber)
                .add("description", null)
                .add("date", date)
                .add("status", status)
                .add("currentTime", LocalDate.now())
                .add("date in future", false)
                .build(), AssertionError.class);

        checkExceptionThrown(() -> new Transaction(sourceAccount, targetAccount, amount, transactionNumber, description, null, status),
            contextBuilder()
                .subject("Transaction#Transaction()")
                .add("sourceAccount", sourceAccount)
                .add("targetAccount", targetAccount)
                .add("amount", amount)
                .add("transactionNumber", transactionNumber)
                .add("description", description)
                .add("date", null)
                .add("status", status)
                .add("currentTime", LocalDate.now())
                .add("date in future", false)
                .build(), AssertionError.class);

        checkExceptionThrown(() -> new Transaction(sourceAccount, targetAccount, amount, transactionNumber, description, date, null),
            contextBuilder()
                .subject("Transaction#Transaction()")
                .add("sourceAccount", sourceAccount)
                .add("targetAccount", targetAccount)
                .add("amount", amount)
                .add("transactionNumber", transactionNumber)
                .add("description", description)
                .add("date", date)
                .add("status", null)
                .add("currentTime", LocalDate.now())
                .add("date in future", false)
                .build(), AssertionError.class);
    }

}
