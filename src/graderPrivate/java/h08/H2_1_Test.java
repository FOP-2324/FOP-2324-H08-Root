package h08;

import h08.implementations.TestBank;
import h08.util.comment.AccountCommentFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSet;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSetTest;

import java.util.List;

import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.*;

@TestForSubmission
public class H2_1_Test extends H08_TestBase {

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @JsonParameterSetTest(value = "H2_1.json", customConverters = "customConverters")
    public void testContains(JsonParameterSet params) throws ReflectiveOperationException {

        TestBank bank = params.get("bank", TestBank.class);
        List<Account> accounts = params.get("accounts", List.class);

        setBankAccounts(bank, accounts);

        for (Account account : accounts) {

            assertCallTrue(() -> bank.isIbanAlreadyUsed(account.getIban()), contextBuilder()
                .subject("Bank#isIbanAlreadyUsed()")
                .add("iban", account.getIban())
                .add("accounts", AccountCommentFactory.IBAN_ONLY.build(accounts))
                .build(),
                TR -> "bank.isIbanAlreadyUsed() returned false for an iban that belongs to an account in the accounts array.");
        }
    }

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @JsonParameterSetTest(value = "H2_1.json", customConverters = "customConverters")
    public void testContainsNot(JsonParameterSet params) throws ReflectiveOperationException {

        TestBank bank = params.get("bank", TestBank.class);
        List<Account> accounts = params.get("accounts", List.class);
        List<Long>  unusedIbans = params.get("unusedIbans", List.class);

        setBankAccounts(bank, accounts);

        for (Long unusedIban : unusedIbans) {

            assertCallFalse(() -> bank.isIbanAlreadyUsed(unusedIban), contextBuilder()
                    .subject("Bank#isIbanAlreadyUsed()")
                    .add("iban", unusedIban)
                    .add("accounts", AccountCommentFactory.IBAN_ONLY.build(accounts))
                    .build(),
                TR -> "bank.isIbanAlreadyUsed() returned true for an iban that does not belong to an account in the accounts array.");
        }
    }

}
