package h08;


import org.tudalgo.algoutils.student.test.StudentTestUtils;

import java.time.LocalDate;
import java.time.Month;

/**
 * Main entry point in executing the program.
 */
public class Main {

    /**
     * Epsilon for comparing double values.
     */
    private static final double EPSILON = 0.0001;

    /**
     * Main entry point in executing the program.
     *
     * @param args program arguments, currently ignored
     */
    public static void main(String[] args) {
        // TODO H6
        // 1
        Customer linus = new Customer(
            "Linus ", "Torvalds",
            "548 Market St, San Francisco, CA 94104, United States",
            LocalDate.of(1969, Month.DECEMBER, 28)
        );
        Customer bill = new Customer(
            "Bill", "Gates",
            "One Microsoft Way, Redmond, WA 98052, United States",
            LocalDate.of(1955, Month.OCTOBER, 28)
        );
        // 2
        Bank linusBank = new Bank("Goldman Sachs", 42);
        Bank billsBank = new Bank("JPMorgan Chase & Co.", 123);
        // 3
        linusBank.add(linus);
        linusBank.add(billsBank);
        billsBank.add(bill);
        billsBank.add(linusBank);
        // 4
        Account linusAccount = linusBank.getAccounts()[0];
        Account billsAccount = billsBank.getAccounts()[0];
        // 5
        linusBank.deposit(linusAccount.getIban(), 20000);
        billsBank.deposit(billsAccount.getIban(), 20000);
        // 6
        linusBank.transfer(linusAccount.getIban(), billsAccount.getIban(), billsBank.getBic(), 1000, "For Windux");
        // 7
        StudentTestUtils.testWithinRange(19000 - EPSILON, 19000 + EPSILON, linusAccount.getBalance());
        StudentTestUtils.testWithinRange(21000 - EPSILON, 21000 + EPSILON, billsAccount.getBalance());
        StudentTestUtils.testEquals(1, linusAccount.getHistory().size());
        StudentTestUtils.testEquals(1, billsAccount.getHistory().size());
        // 8
        StudentTestUtils.testEquals(
            Status.CANCELLED,
            billsBank.transfer(billsAccount.getIban(), linusAccount.getIban(), 24, 4000000.00, "For Lindows")
        );
        StudentTestUtils.testEquals(
            Status.CANCELLED,
            billsBank.transfer(billsAccount.getIban(), linusAccount.getIban(), 24, 4000000.00, "For Lindows")
        );
        StudentTestUtils.testEquals(1, linusAccount.getHistory().size());
        StudentTestUtils.testEquals(1, billsAccount.getHistory().size());
        // 9
        StudentTestUtils.testThrows(
            IllegalArgumentException.class,
            () -> linusBank.withdraw(linusAccount.getIban(), 20000)
        );
        StudentTestUtils.printTestResults();
    }

}
