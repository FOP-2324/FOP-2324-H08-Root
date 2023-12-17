package h08;

import org.junit.jupiter.params.ParameterizedTest;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSet;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSetTest;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.tudalgo.algoutils.tutor.general.assertions.Assertions2.*;

@TestForSubmission
public class H2_4_Test extends H08_TestBase {

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @JsonParameterSetTest(value = "H2_4_Deposit.json", customConverters = "customConverters")
    public void testDepositNormal(JsonParameterSet params) throws ReflectiveOperationException {

        Bank bank = params.get("bank", Bank.class);
        List<Account> accounts = params.get("accounts", List.class);
        int amountToDeposit = params.get("amountToDeposit", int.class);

        Map<Account, Double> currentBalances = accounts.stream().collect(Collectors.toMap(account -> account, Account::getBalance));

        setBankAccounts(bank, accounts);

        for (Account account : accounts) {

            double resultingBalance = account.getBalance() + amountToDeposit;

            Context context = contextBuilder()
                .subject("Bank#deposit()")
                .add("iban", account.getIban())
                .add("accounts", accounts)
                .add("previousBalance", account.getBalance())
                .add("amountToDeposit", amountToDeposit)
                .add("resultingBalance", resultingBalance)
                .build();

            call(() -> bank.deposit(account.getIban(), amountToDeposit), context, TR -> "bank#deposit() threw an unexpected exception.");

            currentBalances.put(account, resultingBalance);

            assertEquals(resultingBalance, account.getBalance(), context, TR -> "The account with the given iban does not have the correct balance after calling deposit.");
            checkBankSizeAndAccountsUnchanged(bank, accounts, context);

            for (Account otherAccount : accounts) {
                if (otherAccount != account) {
                    assertEquals(currentBalances.get(otherAccount), otherAccount.getBalance(), context, TR -> "The balance of an account that was not deposited to has changed.");
                }
            }

        }
    }

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @JsonParameterSetTest(value = "H2_4_Deposit_Exception.json", customConverters = "customConverters")
    public void testDepositException(JsonParameterSet params) throws ReflectiveOperationException {

        Bank bank = params.get("bank", Bank.class);
        List<Account> accounts = params.get("accounts", List.class);
        List<Long> unusedIbans = params.get("unusedIbans", List.class);
        int amountToDeposit = params.get("amountToDeposit", int.class);

        setBankAccounts(bank, accounts);

        for (long unusedIban : unusedIbans) {

            Context context = contextBuilder()
                .subject("Bank#deposit()")
                .add("iban", unusedIban)
                .add("accounts", accounts)
                .add("amountToDeposit", amountToDeposit)
                .build();

            checkExceptionThrown(() -> bank.deposit(unusedIban, amountToDeposit), context, NoSuchElementException.class, Long.toString(unusedIban));
        }

        checkExceptionThrown(() -> bank.deposit(accounts.get(0).getIban(), -1), contextBuilder()
            .subject("Bank#deposit()")
            .add("iban", accounts.get(0).getIban())
            .add("accounts", accounts)
            .add("amountToDeposit", -1)
            .build(), IllegalArgumentException.class, "-1.0");
    }

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @JsonParameterSetTest(value = "H2_4_Withdraw.json", customConverters = "customConverters")
    public void testWithdrawNormal(JsonParameterSet params) throws ReflectiveOperationException {

        Bank bank = params.get("bank", Bank.class);
        List<Account> accounts = params.get("accounts", List.class);
        int amountToWithdraw = params.get("amountToWithdraw", int.class);

        Map<Account, Double> currentBalances = accounts.stream().collect(Collectors.toMap(account -> account, Account::getBalance));

        setBankAccounts(bank, accounts);

        for (Account account : accounts) {

            double resultingBalance = account.getBalance() - amountToWithdraw;

            Context context = contextBuilder()
                .subject("Bank#withdraw()")
                .add("iban", account.getIban())
                .add("accounts", accounts)
                .add("previousBalance", account.getBalance())
                .add("amountToWithdraw", amountToWithdraw)
                .add("resultingBalance", resultingBalance)
                .build();

            call(() -> bank.withdraw(account.getIban(), amountToWithdraw), context, TR -> "bank#withdraw() threw an unexpected exception.");

            currentBalances.put(account, resultingBalance);

            assertEquals(resultingBalance, account.getBalance(), context, TR -> "The account with the given iban does not have the correct balance after calling withdraw.");
            checkBankSizeAndAccountsUnchanged(bank, accounts, context);

            for (Account otherAccount : accounts) {
                if (otherAccount != account) {
                    assertEquals(currentBalances.get(otherAccount), otherAccount.getBalance(), context, TR -> "The balance of an account that was not withdrawn from has changed.");
                }
            }

        }

    }

    @SuppressWarnings("unchecked")
    @ParameterizedTest
    @JsonParameterSetTest(value = "H2_4_Withdraw_Exception.json", customConverters = "customConverters")
    public void testWithdrawException(JsonParameterSet params) throws ReflectiveOperationException {

        Bank bank = params.get("bank", Bank.class);
        List<Account> accounts = params.get("accounts", List.class);
        List<Long> unusedIbans = params.get("unusedIbans", List.class);
        int negativeBalanceIban = params.get("negativeBalanceIban", int.class);
        int amountToWithdraw = params.get("amountToWithdraw", int.class);

        setBankAccounts(bank, accounts);

        for (long unusedIban : unusedIbans) {

            Context context = contextBuilder()
                .subject("Bank#withdraw()")
                .add("iban", unusedIban)
                .add("accounts", accounts)
                .add("amountToWithdraw", amountToWithdraw)
                .build();

            checkExceptionThrown(() -> bank.withdraw(unusedIban, amountToWithdraw), context, NoSuchElementException.class, Long.toString(unusedIban));
        }

        double resultingNegativeBalance = accounts.stream()
            .filter(account -> account.getIban() == negativeBalanceIban)
            .findFirst()
            .get()
            .getBalance() - amountToWithdraw;

        Context context = contextBuilder()
            .subject("Bank#withdraw()")
            .add("iban", negativeBalanceIban)
            .add("accounts", accounts)
            .add("amountToWithdraw", amountToWithdraw)
            .add("resultingBalance", resultingNegativeBalance)
            .build();

        checkExceptionThrown(() -> bank.withdraw(negativeBalanceIban, amountToWithdraw), context, IllegalArgumentException.class, Double.toString(resultingNegativeBalance));

        checkExceptionThrown(() -> bank.withdraw(accounts.get(0).getIban(), -1), contextBuilder()
            .subject("Bank#withdraw()")
            .add("iban", accounts.get(0).getIban())
            .add("accounts", accounts)
            .add("amountToWithdraw", -1)
            .build(), IllegalArgumentException.class, "-1.0");
    }

}
