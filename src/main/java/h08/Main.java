package h08;

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
        Bank FOPBank = new Bank("Bank",1234,10);

        Account mike = new Account("Mike", "Oxlong",187,50000,FOPBank);
        Account diana = new Account("Diana", "Oxlong",188,50000,FOPBank);
        Account marlon = new Account("Marlon", "Oxlong",189,50000,FOPBank);
        Account paul = new Account("Paul", "Oxlong",190,50000,FOPBank);
        Account max = new Account("Max", "Oxlong",191,50000,FOPBank);

        FOPBank.printAccounts();








    }
}
