package h08;

import h08.exceptions.BankException;
import h08.exceptions.TransactionException;

/**
 * Main entry point in executing the program.
 */
public class Main {

    /**
     * Main entry point in executing the program.
     *
     * @param args program arguments, currently ignored
     */
    public static void main(String[] args) throws BankException, TransactionException {
        Bank fopBank = new Bank("FOPBank",789,10);
        Bank[] banks = {fopBank};
        Account alex = new Account("alex","lastname",1234,10000,fopBank,null,new TransactionHistory(100));
        Account morty = new Account("morty", "smith",321,10000,fopBank,null,new TransactionHistory(100));
        fopBank.transfer(1234,321, fopBank.getBic(),banks,100);
        System.out.println(alex.getBalance());
        System.out.println(morty.getBalance());

    }
}
