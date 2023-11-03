package h08;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * An example JUnit test class.
 */
public class ExampleJUnitTest {

    @Test
    public void testAddition() {
        assertEquals(2, 1 + 1);
    }

    public void testIBANGeneration(){
        for (int i = 1; i < 1000; i++) {
            Customer c = new Customer(Integer.toString(i,i*2),Integer.toString(i*3,i +2),"TestStraße", LocalDate.now());
            Bank bank = new Bank("FOPBank",190,10, null);
            Account testAccount = new Account(c,c.firstName(),c.lastName(),10000,bank, null);
            System.out.println(testAccount.getIban());
            assertTrue(testAccount.getIban() > 0);
        }
    }
}
