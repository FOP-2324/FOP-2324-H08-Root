package h08;

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
        Bank FOPBank = new Bank("Bank",1234,new Account[10]);
        Account mike = new Account("Mike", "Oxlong",187,50000,FOPBank);
        Account marlon = new Account("Marlon", "Oxlong",188,50000,FOPBank);
        Account sandra = new Account("Sandra", "Oxlong",189,50000,FOPBank);
        Account elo = new Account("Elo", "Oxlong",190,50000,FOPBank);










    }
}
