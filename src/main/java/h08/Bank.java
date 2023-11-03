package h08;

import h08.exceptions.AccountException;

import static org.tudalgo.algoutils.student.Student.crash;

public class Bank {

    private String name;
    private final int bic;
    private final Account[] accounts;
    private final int capacity;
    private int numberOfAddedAccounts = 0;
    private final Branch[] branches;

    public Bank(String name, int bic, int capacity, Branch[] branches) {
        assert name != null;

        this.name = name;
        this.bic = bic;
        this.accounts = new Account[capacity];
        this.capacity = capacity;
        this.branches = branches;
    }


    public void depositWithAssert(long iban, double amount) {

        assert amount > 0;

        int index = getAccountIndex(iban);

        assert index >= 0;

        double newBalance = accounts[index].getBalance() + amount;

        accounts[index].setBalance(newBalance);
    }

    public void depositWithExc(long iban, double amount){
        //TODO: implement with Java Standard Exception
        if(amount <= 0)
            throw new IllegalArgumentException("amount can't be zero or negative!");

        int index = getAccountIndex(iban);

        if(index < 0)
            throw new IllegalArgumentException("Cannot find IBAN!");

        double newBalance = accounts[index].getBalance() + amount;
        accounts[index].setBalance(newBalance);

    }

    public void withdrawWithAssert(long iban, double amount){

        assert amount > 0;

        int index = getAccountIndex(iban);
        assert index >= 0;

        Account account = accounts[index];

        assert (account.getBalance() - amount > 0);

        double newBalance = account.getBalance() - amount;

        account.setBalance(newBalance);
    }

    public void withdrawWithExc(long iban, double amount){
        //TODO: implement with Java standard Exception
        crash("not implemented");
    }


    public void addAccount(Account account) {
        //TODO: change AccountException
        //crash("not implemented");
        if(account == null)
            throw new AccountException("Account can't be null!");

        if(numberOfAddedAccounts  == capacity)
            throw new IllegalArgumentException("Maximum amount of accounts is reached!");

        for (int i = 0; i < accounts.length; i++) {

            if(accounts[i] == null) {
                accounts[i] = account;
                accounts[i].setBank(this);
                numberOfAddedAccounts ++;
                return;
            }
        }
    }

    public Account removeAccount(long iban){
        //TODO: may add one AccountException
        crash("not implemented");
        if(iban < 0)
            throw new IllegalArgumentException(iban + " is  an invalid IBAN!");

        //indirect exception
        int index = getAccountIndex(iban);
        Account removedAccount = accounts[index];

        accounts[index] = null;
        if(index < capacity - 1)
            System.arraycopy(accounts, index + 1, accounts, index, accounts.length - index - 1);
        numberOfAddedAccounts --;
        return removedAccount;
    }

    private int getAccountIndex(long iban) {
        for (int i = 0; i < accounts.length; i++) {
            if (accounts[i] != null && accounts[i].getIban() == iban)
                return i;
        }
        throw new IllegalArgumentException("Cannot find account with IBAN: " + iban);
    }

    /**
     * This method creates a new Transaction and executes the actual money transfer. It ensures that the money transfer is an atomic operation.
     * @param senderIBAN the sender account's IBAN.
     * @param receiverIBAN the receiver account's IBAN.
     * @param receiverBIC the receiver account's BIC.
     * @param banks all banks in the system.
     * @param amount how much money should be transferred.
     * @return the last status of the {@link Transaction}
     */
    public Status transfer(long senderIBAN, long receiverIBAN,int receiverBIC, Bank[] banks, double amount){
        //TODO: implement with custom Exceptions
        crash("not implemented");
        return null;
    }

    private long generateTransactionNumber(){
        return System.currentTimeMillis();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBic() {
        return bic;
    }

    public Account[] getAccounts() {
        return accounts;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getNumberOfAddedAccounts() {
        return numberOfAddedAccounts;
    }

    public void setNumberOfAddedAccounts(int numberOfAddedAccounts) {
        this.numberOfAddedAccounts = numberOfAddedAccounts;
    }

    public Branch[] getBranches() {
        return branches;
    }
}
