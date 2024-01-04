package h08;

import java.util.ArrayList;

public class TestBank extends Bank {

    public long ibanToGenerate = 0L;
    public int generateIbanCallCount = 0;
    public boolean generateIbanCallsActual = true;

    public long generateIbanlastSeed = -1L;
    public Customer generateIbanLastCustomer = null;

    public long transactionNumberToGenerate = 0L;

    public boolean withdrawThrowsException = false;
    public boolean depositThrowsException = false;

    public boolean withdrawCallsActual = true;
    public boolean depositCallsActual = true;

    public int withdrawCallCount = 0;
    public int depositCallCount = 0;

    public long withdrawIban = 0;
    public long depositIban = 0;

    public double withdrawAmount = 0;
    public double depositAmount = 0;

    public boolean transferCallsActual = true;
    ArrayList<TransferCall> transferCalls = new ArrayList<>();

    public TestBank(Bank bank) {
        super(bank.getName(), bank.getBic(), bank.capacity());
    }

    public TestBank(String name, int bic, int capacity) {
        super(name, bic, capacity);
    }

    @Override
    protected long generateIban(Customer customer, long seed) {
        generateIbanCallCount++;
        generateIbanlastSeed = seed;
        generateIbanLastCustomer = customer;

        if (generateIbanCallsActual) {
            return super.generateIban(customer, seed);
        }
        return ibanToGenerate;
    }

    @Override
    protected long generateTransactionNumber() {
        return transactionNumberToGenerate++;
    }

    @Override
    public void deposit(long iban, double amount) {

        depositIban = iban;
        depositAmount = amount;
        depositCallCount++;

        if (depositThrowsException) {
            throw new IllegalArgumentException();
        } else if (depositCallsActual) {
            super.deposit(iban, amount);
        } else {
            int index = getAccountIndex(iban);
            Account account = getAccounts()[index];
            double newBalance = account.getBalance() + amount;
            account.setBalance(newBalance);
        }
    }

    @Override
    public void withdraw(long iban, double amount) {

        withdrawIban = iban;
        withdrawAmount = amount;
        withdrawCallCount++;

        if (withdrawThrowsException) {
            throw new IllegalArgumentException();
        } else if (withdrawCallsActual) {
            super.withdraw(iban, amount);
        } else {
            int index = getAccountIndex(iban);
            Account account = getAccounts()[index];
            double newBalance = account.getBalance() - amount;
            account.setBalance(newBalance);
        }
    }

    @Override
    public Status transfer(long senderIBAN, long receiverIBAN, int receiverBIC, double amount, String description) {
        transferCalls.add(new TransferCall(senderIBAN, receiverIBAN, receiverBIC, amount, description));

        if (!transferCallsActual) {
            return Status.CLOSED;
        }

        return super.transfer(senderIBAN, receiverIBAN, receiverBIC, amount, description);
    }
}

record TransferCall(long senderIBAN, long receiverIBAN, int receiverBIC, double amount, String description) {
}
