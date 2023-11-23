package h08;

import org.junit.jupiter.api.Test;


import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


public class AccountTests {


    @Test
    public void testGenerateIban(){
        Customer testCustomer = new Customer("TestFirstName","TestLastName","TestStreet", LocalDate.now());
        Bank testBank = new Bank("TestBank",1234,300);
        TransactionHistory history = new TransactionHistory(1000);
        Set<Long> set = new HashSet<>();
        Account testAccount;
        for (int i = 0; i < testBank.getCapacity(); i++) {
            testAccount = new Account(testCustomer, "Max","Mstrmnn",120000D,testBank,history);
            assertFalse(set.contains(testAccount.getIban()));
            set.add(testAccount.getIban());

        }

    }
}
