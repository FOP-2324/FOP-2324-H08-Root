package h08;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.LocalDate;

public class AccountTests {


    @Test
    public void testGenerateIban(){
        Customer testCustomer = new Customer("TestFirstName","TestLastName","TestStreet", LocalDate.now());
    }
}
