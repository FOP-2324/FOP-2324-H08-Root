package h08;


import h08.exceptions.TransactionException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import static h08.Status.OPEN;

/**
 * Main entry point in executing the program.
 */
public class Main {

    /**
     * Main entry point in executing the program.
     *
     * @param args program arguments, currently ignored
     */
    public static void main(String[] args) throws TransactionException {
        Transaction[] transactions = new Transaction[5];
        for (int i = 0; i < transactions.length; i++) {
            Customer c = new Customer("test","testo","Fe",LocalDate.now());
            Bank b = new Bank("FOPBank",187,100);
            Account s = new Account(c, "Max","MusterMann",12000.00,b,new TransactionHistory(10));
            Account t = new Account(c, "Michelle","MusterMann",12000.00,b,new TransactionHistory(10));
            transactions[i] = new Transaction(s,t,10,System.nanoTime(),"Bla",LocalDate.now(),OPEN);
        }
        throw new TransactionException(transactions);
    }
}
