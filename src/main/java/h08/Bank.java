package h08;

import h08.exceptions.AccountException;
import h08.exceptions.BankException;
import h08.exceptions.NoSuchBankException;
import h08.exceptions.TooManyAccountsException;


public class Bank {

    private String name;
    private final int bic;
    private Account[] accounts;
    private final int maxAccounts;
    private int numberOfAddedAccounts  = 0;




    public Bank(String name, int bic, int maxAccounts) {
        this.name = name;
        this.bic = bic;
        this.maxAccounts = maxAccounts;
        this.accounts = new Account[maxAccounts];
    }

    public void withdrawWithAssert(long IBAN, double amount){

        assert amount > 0;

        int index = getAccountIndex(IBAN);

        assert index >= 0;

        double newBalance = accounts[index].getBalance() - amount;

        accounts[index].setBalance(newBalance);

    }

    public void withdrawWithExc(long IBAN, double amount){

        if(amount <= 0)
            throw new IllegalArgumentException("amount can't be zero or negative!");

        int index = getAccountIndex(IBAN);

        if(index < 0)
            throw new AccountException("Cannot find IBAN!");

        double newBalance = accounts[index].getBalance() - amount;

        accounts[index].setBalance(newBalance);
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
            throw new AccountException("Cannot find IBAN!");

        double newBalance = accounts[index].getBalance() + amount;
        accounts[index].setBalance(newBalance);
    }

    public void transfer(long senderIBAN, long receiverIBAN,int receiverBIC, Bank[] banks, double amount) throws BankException {
        if(banks == null)
            throw new BankException("Banks cannot be null!");
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

    }

    private int getBankIndex(int BIC, Bank[] banks) throws BankException {
        for (int i = 0; i < banks.length; i++) {
            if(BIC == banks[i].getBic())
                return i;
        }
        throw new NoSuchBankException();
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

    private void setAccounts(Account[] accounts){
        this.accounts = accounts;
    }

    public int getMaxAccounts() {
        return maxAccounts;
    }

    public int getCurrentAccounts() {
        return numberOfAddedAccounts ;
    }

    private int getAccountIndex(long IBAN){
        for (int i = 0; i < accounts.length; i++) {
            if(accounts[i] != null && accounts[i].getIban() == IBAN)
                return i;
        }

        throw new AccountException("Cannot find account!");
    }

    public void addAccount(Account account){
        if(account == null)
            throw new AccountException("Account can't be null!");

        if(numberOfAddedAccounts  == maxAccounts)
            throw new TooManyAccountsException("Maximum amount of accounts is reached!");

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
            throw new AccountException("Account can't be null!");
        for (int i = 0; i < accounts.length; i++) {
            if(accounts[i] != null && account.getIban() == accounts[i].getIban()) {
                accounts[i] = null;
                if(i < maxAccounts - 1)
                    System.arraycopy(accounts, i + 1, accounts, i, accounts.length - i - 1);
                numberOfAddedAccounts --;
                return;
            }
        }
    }

    public void printAccounts(){
        for (Account a :
            accounts) {
            if(a !=null)
                System.out.println(a.getFirstName() + " | " + a.getLastName() + " | " + a.getBank().getName() + " | " + a.getBalance());

        }
    }


}
