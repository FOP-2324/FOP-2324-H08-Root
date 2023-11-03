package h08;

import java.time.LocalDate;

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

            Bank bank = new Bank("FOPBank",190,99910, null);
            for (int i = 1; i < 1000; i++) {
                Customer c = new Customer(Integer.toString(i,i*2),Integer.toString(i*3,i +2),"TestStraße", LocalDate.now());
                Account testAccount = new Account(c,c.firstName(),c.lastName(),10000,bank, null);
                System.out.println(testAccount.getIban());
            }


    }
}
