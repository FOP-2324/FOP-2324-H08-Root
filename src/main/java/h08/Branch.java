package h08;

import h08.exceptions.AccountException;
import h08.exceptions.TooManyAccountsException;


public class Branch {

    private final String name;
    private final Bank bank;

    private final String bic;

    private final int capacity;
    private int currentNumberOfAccounts;
    private final Account[] accounts;


    public Branch(String name, Bank bank, String bic, int capacity) {
        this.name = name;
        this.bank = bank;
        this.bic = bic;
        this.capacity = capacity;
        this.accounts = new Account[capacity];
        currentNumberOfAccounts = 0;
    }

    public void add(Account account) {
        if(account == null)
            throw new AccountException("Account can't be null!",null);

        if(currentNumberOfAccounts  == capacity)
            throw new TooManyAccountsException("Maximum amount of accounts is reached!",account);

        for (int i = 0; i < accounts.length; i++) {

            if(accounts[i] == null) {
                accounts[i] = account;
                accounts[i].setBank(this.bank);
                currentNumberOfAccounts++;
                return;
            }
        }
    }

    public void remove(Account account) {
        if(account == null)
            throw new AccountException("Account can't be null!",null);
        for (int i = 0; i < accounts.length; i++) {
            if(accounts[i] != null && account.getIban() == accounts[i].getIban()) {
                accounts[i] = null;
                if(i < capacity - 1)
                    System.arraycopy(accounts, i + 1, accounts, i, accounts.length - i - 1);
                currentNumberOfAccounts --;
                return;
            }
        }
    }

    public String getName() {
        return name;
    }

    public Bank getBank() {
        return bank;
    }

    public String getBic() {
        return bic;
    }

    public int getCapacity() {
        return capacity;
    }

    public Account[] getAccounts() {
        Account[] notNullAccounts = new Account[currentNumberOfAccounts];
        System.arraycopy(accounts,0,notNullAccounts,0,currentNumberOfAccounts);
        return notNullAccounts;
    }

}
