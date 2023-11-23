package h08;


import h08.exceptions.AccountException;
import h08.exceptions.BankException;
import h08.exceptions.TransactionException;

import java.time.LocalDate;

import static h08.Status.*;
import static org.tudalgo.algoutils.student.Student.crash;

public class Bank {

    private String name;
    private final int bic;
    private final Account[] accounts;
    private final int capacity;
    private int numberOfAddedAccounts = 0;

    public Bank(String name, int bic, int capacity) {
        assert name != null;

        this.name = name;
        this.bic = bic;
        this.accounts = new Account[capacity];
        this.capacity = capacity;
    }


    public void depositWithAssert(long iban, double amount) {

        assert amount > 0;

        int index = getAccountIndex(iban);

        assert index >= 0;

        double newBalance = accounts[index].getBalance() + amount;

        accounts[index].setBalance(newBalance);
    }

    public void depositWithExc(long iban, double amount){

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

    public void withdrawWithExc(long iban, double amount) throws BankException {
        //TODO: test
        if(amount <= 0)
            throw new IllegalArgumentException("amount can't be zero or negative!");

        //indirect exception
        int index = getAccountIndex(iban);

        Account account = accounts[index];
        if(account.getBalance() - amount < 0)
            throw new BankException("Can't withdraw money, because " + account + "has insufficient funds!");

        double newBalance = account.getBalance() - amount;

        account.setBalance(newBalance);

    }


    public void addAccount(Account account) {
        //TODO: test
        if(account == null)
            throw new NullPointerException("Account can't be null!");

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

    protected boolean ibanIsAlreadyUsed(long iban){
        for (Account account : accounts) {
            if (account != null && account.getIban() == iban)
                return true;
        }
        return false;
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
    public Status transfer(long senderIBAN, long receiverIBAN,int receiverBIC, Bank[] banks, double amount, String description) throws TransactionException {
        //TODO: rework transaction exception catch block
        if(banks == null){
            System.err.println("banks cannot be null!");
            return CANCELLED;
        }
        long transactionNumber = generateTransactionNumber();
        int senderIndex = 0;
        int receiverIndex = 0;

        //will never be null after the try block.
        Bank receiverBank = null;

        //if you can't find the receiver or sender,print the message and return CANCELLED.
        try{
             senderIndex = getAccountIndex(senderIBAN);
             receiverIndex = getAccountIndex(receiverIBAN);
            receiverBank = banks[getBankIndex(receiverBIC,banks)];

        }catch (IllegalArgumentException | BankException exception){
            System.err.println(exception.getMessage());
            return CANCELLED;
        }
        catch (Exception ignored){
            return CANCELLED;
        }

        Account senderAccount = accounts[senderIndex];
        Account receiverAccount = receiverBank.accounts[receiverIndex];

        //declare and initialize open Transaction
        Transaction transaction = new Transaction(senderAccount,receiverAccount,amount,transactionNumber,description, LocalDate.now(), OPEN);

        try {
            withdrawWithExc(senderIBAN,amount);
            depositWithExc(receiverIBAN,amount);
            transaction = new Transaction(senderAccount,receiverAccount,amount,transactionNumber,description, LocalDate.now(),CLOSED);
            senderAccount.getHistory().add(transaction);
            receiverAccount.getHistory().add(transaction);
        }catch(IllegalArgumentException argumentException){
            System.out.println(argumentException.getMessage());
            return CANCELLED;
        }
        catch(TransactionException transactionException){
            System.out.println(transactionException.getMessage());
            transaction = new Transaction(senderAccount,receiverAccount,amount,transactionNumber,description, LocalDate.now(), CANCELLED);
            senderAccount.getHistory().add(transaction);
            receiverAccount.getHistory().add(transaction);
            return CANCELLED;
        }catch(BankException bankException){
            System.out.println(bankException.getMessage());
            senderAccount.getHistory().add(transaction);
            receiverAccount.getHistory().add(transaction);
            return OPEN;
        } catch (Exception ignored){
            return CANCELLED;
        }

        return CLOSED;
    }

    public Status transfer(long senderIBAN, long receiverIBAN,Bank receiverBank, double amount, String description) throws TransactionException {
        //TODO: rework transaction exception catch block

        long transactionNumber = generateTransactionNumber();
        int senderIndex = 0;
        int receiverIndex = 0;




        //if you can't find the receiver or sender,print the message and return CANCELLED.
        try{
            senderIndex = getAccountIndex(senderIBAN);
            receiverIndex = getAccountIndex(receiverIBAN);

        }catch (IllegalArgumentException exception){
            System.err.println(exception.getMessage());
            return CANCELLED;
        }
        catch (Exception ignored){
            return CANCELLED;
        }

        Account senderAccount = accounts[senderIndex];
        Account receiverAccount = receiverBank.accounts[receiverIndex];

        //declare and initialize open Transaction
        Transaction transaction = new Transaction(senderAccount,receiverAccount,amount,transactionNumber,description, LocalDate.now(), OPEN);

        try {
            withdrawWithExc(senderIBAN,amount);
            depositWithExc(receiverIBAN,amount);
            transaction = new Transaction(senderAccount,receiverAccount,amount,transactionNumber,description, LocalDate.now(),CLOSED);
            senderAccount.getHistory().add(transaction);
            receiverAccount.getHistory().add(transaction);
        }catch(IllegalArgumentException argumentException){
            System.out.println(argumentException.getMessage());
            return CANCELLED;
        }
        catch(TransactionException transactionException){
            System.out.println(transactionException.getMessage());
            transaction = new Transaction(senderAccount,receiverAccount,amount,transactionNumber,description, LocalDate.now(), CANCELLED);
            senderAccount.getHistory().add(transaction);
            receiverAccount.getHistory().add(transaction);
            return CANCELLED;
        }catch(BankException bankException){
            System.out.println(bankException.getMessage());
            senderAccount.getHistory().add(transaction);
            receiverAccount.getHistory().add(transaction);
            return OPEN;
        } catch (Exception ignored){
            return CANCELLED;
        }

        return CLOSED;
    }

    private int getBankIndex(int bic, Bank[] banks) throws BankException {
        if(banks == null)
            throw new NullPointerException("Banks cannot be null!");
        for (int i = 0; i < banks.length; i++) {
            if(bic == banks[i].getBic())
                return i;
        }
        throw new BankException(bic);
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

}
