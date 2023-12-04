package h08;


import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;

/**
 * Main entry point in executing the program.
 */
public class Main {

    /**
     * Main entry point in executing the program.
     *
     * @param args program arguments, currently ignored
     */
    public static void main(String[] args) {
        //create customers
        Customer linus = new Customer("Linus ","Torvalds","548 Market St, San Francisco, CA 94104, United States", LocalDate.of(1969, Month.DECEMBER,28));
        Customer bill = new Customer("Bill", "Gates","One Microsoft Way, Redmond, WA 98052, United States",LocalDate.of(1955,Month.OCTOBER,28));
        //create banks
        Bank linusBank = new Bank("Goldman Sachs", 42);
        Bank billsBank = new Bank("JPMorgan Chase & Co.",123);
        //add customer and transferable bank
        linusBank.add(linus);
        linusBank.add(billsBank);
        //add customer and transferable bank
        billsBank.add(bill);
        billsBank.add(linusBank);
        //get account reference
        Account linusAccount = linusBank.getAccounts()[0];
        Account billsAccount = billsBank.getAccounts()[0];
        //deposit
        linusBank.deposit(linusAccount.getIban(),2000000.00);
        billsBank.deposit(billsAccount.getIban(),4000000.00);
        //print accounts before first transaction
        System.out.println(linusAccount);
        System.out.println(billsAccount);
        //test transfer
        System.out.println(linusBank.transfer(linusAccount.getIban(), billsAccount.getIban(), 123, 100000.00, "For Windux") + "\n");
        //print accounts
        System.out.println(linusAccount);
        System.out.println(billsAccount);
        //wrong transfer
        System.out.println(billsBank.transfer(billsAccount.getIban(), linusAccount.getIban(), 24, 4000000.00, "For Lindows")+ "\n");
        //print accounts and transfers
        System.out.println(linusAccount);
        System.out.println(Arrays.toString(linusAccount.getHistory().getTransactions()));
        System.out.println(billsAccount);
        System.out.println(Arrays.toString(billsAccount.getHistory().getTransactions()));


    }

}
