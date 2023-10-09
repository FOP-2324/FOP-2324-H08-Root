package h08;

import h08.exceptions.*;

import java.time.LocalDate;
import java.util.Arrays;


public class Bank {

    private String name;
    private final int bic;
    private Account[] accounts;
    private final int capacity;
    private int numberOfAddedAccounts  = 0;
    private Branch[] branches = new Branch[0];




    public Bank(String name, int bic, int capacity) {
        this.name = name;
        this.bic = bic;
        this.capacity = capacity;
        this.accounts = new Account[capacity];
    }

    public void withdrawWithAssert(long IBAN, double amount) throws BankException {

        assert amount > 0;

        int index = getAccountIndex(IBAN);

        assert index >= 0;

        Account account = accounts[index];
        if(account.getBalance() - amount < 0)
            throw new BankException("Can't withdraw money, because " + account + "has insufficient funds!", account.getBank());
        double newBalance = account.getBalance() - amount;

        account.setBalance(newBalance);

    }

    public void withdrawWithExc(long IBAN, double amount) throws BankException {

        if(amount <= 0)
            throw new IllegalArgumentException("amount can't be zero or negative!");

        int index = getAccountIndex(IBAN);

        if(index < 0)
            throw new AccountException("Cannot find account!");

        Account account = accounts[index];
        if(account.getBalance() - amount < 0)
            throw new BankException("Can't withdraw money, because " + account + "has insufficient funds!", account.getBank());

        double newBalance = account.getBalance() - amount;

        account.setBalance(newBalance);
    }

    public void depositWithAssert(long IBAN, double amount){

        assert amount > 0;

        int index = getAccountIndex(IBAN);

        assert index >= 0;

        double newBalance = accounts[index].getBalance() + amount;

        accounts[index].setBalance(newBalance);
    }

    public void depositWithExc(long IBAN, double amount){
        if(amount <= 0)
            throw new IllegalArgumentException("amount can't be zero or negative!");

        int index = getAccountIndex(IBAN);

        if(index < 0)
            throw new AccountException("Cannot find IBAN!", null);

        double newBalance = accounts[index].getBalance() + amount;
        accounts[index].setBalance(newBalance);
    }

    /*public void transfer(long senderIBAN, long receiverIBAN,int receiverBIC, Bank[] banks, double amount) throws BankException {
        if(banks == null)
            throw new BankException("Banks cannot be null!",null);

        int index;

        try {
           index  = getBankIndex(receiverBIC,banks);
        }catch (NoSuchBankException b){
            System.out.println("Cannot find Bank with BIC: " + receiverBIC);
            return;
        }

        Bank receiverBank = banks[index];

        try{
            withdrawWithExc(senderIBAN,amount);
        }catch (AccountException a){
            System.out.println("Cannot find sender account");
            return;
        }catch(RuntimeException r){
            System.out.println(r.getMessage());
            return;
        }catch (Exception ignored){
            return;
        }

        try{
            receiverBank.depositWithExc(receiverIBAN,amount);
        }catch (AccountException a){
            depositWithExc(senderIBAN,amount);
            System.out.println("Cannot find receiver account");
        }catch (RuntimeException r){
            System.out.println(r.getMessage());
            depositWithExc(senderIBAN,amount);
        }catch (Exception ignored){
        }

    }*/
    public void transfer(long senderIBAN, long receiverIBAN,int receiverBIC, Bank[] banks, double amount) throws BankException, TransactionException {
        if(banks == null)
            throw new BankException("Banks cannot be null!",null);

        int senderIndex = getAccountIndex(senderIBAN);
        int receiverIndex = getAccountIndex(receiverIBAN);
        long transactionNumber = generateTransactionNumber();

        if(senderIndex < 0)
            throw new AccountException("Can't find sender account with IBAN: " + senderIBAN);
        if(receiverIndex < 0)
            throw new AccountException("Can't find receiver account with IBAN: " + receiverIBAN);

        Account senderAccount = accounts[senderIndex];
        Bank receiverBank = banks[getBankIndex(receiverBIC,banks)];
        Account receiverAccount = receiverBank.accounts[receiverIndex];

        if(senderAccount.getBalance() < amount)
            throw new AccountException("Account has insufficient funds", senderAccount);

        // create transaction and handle exceptions
        try{
            Transaction transaction = new Transaction(senderAccount,receiverAccount,amount,transactionNumber,"(senderAccount + receiverAccount.toString())", LocalDate.now(),Status.OPEN);
            senderAccount.getHistory().add(transaction);
            receiverAccount.getHistory().add(transaction);
        } catch (TransactionException transactionException) {
            System.out.println(transactionException.getMessage());
            return;
        }catch (Exception ignored){
        }

        try {
            withdrawWithExc(senderIBAN,amount);
            depositWithExc(receiverIBAN,amount);
        }catch(IllegalArgumentException | BankException argumentException){
            System.out.println(argumentException.getMessage());
        }catch(AccountException accountException){
            System.out.println(senderAccount + "Account error!");
            throw new BankException("Account at index " + senderIndex + " is invalid!",this);
        }catch (Exception ignored){

        }


    }


    private int getBankIndex(int bic, Bank[] banks) throws BankException {
        for (int i = 0; i < banks.length; i++) {
            if(bic == banks[i].getBic())
                return i;
        }
        throw new NoSuchBankException(bic);
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

    public int getNumberOfAddedAccounts() {
        return numberOfAddedAccounts;
    }

    public Branch[] getBranches() {
        return branches;
    }

    public Account[] getAccounts() {
        return accounts;
    }

    private void setAccounts(Account[] accounts){
        this.accounts = accounts;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getCurrentAccounts() {
        return numberOfAddedAccounts ;
    }

    private int getAccountIndex(long IBAN){
        for (int i = 0; i < accounts.length; i++) {
            if(accounts[i] != null && accounts[i].getIban() == IBAN)
                return i;
        }

        throw new AccountException("Cannot find account!",null);
    }

    public void addAccount(Account account){
        if(account == null)
            throw new AccountException("Account can't be null!",null);

        if(numberOfAddedAccounts  == capacity)
            throw new TooManyAccountsException("Maximum amount of accounts is reached!",account);

        for (int i = 0; i < accounts.length; i++) {

            if(accounts[i] == null) {
                accounts[i] = account;
                accounts[i].setBank(this);
                numberOfAddedAccounts ++;
                return;
            }
        }

    }

    public void removeAccount(Account account){
        if(account == null)
            throw new AccountException("Account can't be null!",null);
        for (int i = 0; i < accounts.length; i++) {
            if(accounts[i] != null && account.getIban() == accounts[i].getIban()) {
                accounts[i] = null;
                if(i < capacity - 1)
                    System.arraycopy(accounts, i + 1, accounts, i, accounts.length - i - 1);
                numberOfAddedAccounts --;
                return;
            }
        }
    }

    private long generateTransactionNumber(){
        return System.currentTimeMillis();
    }

    public void printAccounts(){
        for (Account a :
            accounts) {
            if(a !=null)
                System.out.println(a.getFirstName() + " | " + a.getLastName() + " | " + a.getBank().getName() + " | " + a.getBalance());

        }
    }


    @Override
    public String toString() {
        return "Bank{" +
            "name='" + name + '\'' +
            ", bic=" + bic +
            ", capacity=" + capacity +
            ", numberOfAddedAccounts=" + numberOfAddedAccounts +
            '}';
    }
}
