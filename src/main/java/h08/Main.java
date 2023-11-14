package h08;


import h08.exceptions.AccountException;

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
        Bank bank = new Bank("FOPBank",1908,10021);
        Account acc1 = new Account(null,"Max","Mustermann",100000,bank,new TransactionHistory(100));
        Account acc2 = new Account(null,"Max","Mustermann",100000,bank,new TransactionHistory(100));

    }
}
