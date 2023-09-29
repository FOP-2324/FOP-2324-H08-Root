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
        Bank FOPBank = new Bank("FOPBank",1234,10);
        Bank algoBank = new Bank("AlgoBank",69,1);

        Account mike = new Account("Mike", "Oxlong",187,50000,FOPBank);
        Account diana = new Account("Diana", "Oxlong",188,50000,algoBank);
        Account marlon = new Account("Marlon", "Oxlong",189,50000,FOPBank);
        Account paul = new Account("Paul", "Oxlong",190,50000,FOPBank);
        Account max = new Account("Max", "Oxlong",191,50000,FOPBank);
        Bank[] banks = new Bank[2];
        banks[0] = FOPBank;
        banks[1] = algoBank;

        FOPBank.printAccounts();
        System.out.println("---------------");
        algoBank.printAccounts();
        System.out.println();
        try{
            FOPBank.transfer(189,1088,69,banks,10000);

        }catch (Exception ign){
            System.out.println(ign.getMessage());
        }
        FOPBank.printAccounts();
        System.out.println("---------------");
        algoBank.printAccounts();








    }
}
