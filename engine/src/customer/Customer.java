package customer;

import exception.AbsException;
import jaxb.generated.AbsCustomer;

import java.io.Serializable;

public class Customer implements Serializable {
    private final String name;
    private int balance;

    public Customer(String name, int balance) {
        this.name = name;
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public int getBalance() {
        return balance;
    }

    public static Customer ConvertRawAbsToCustomer(AbsCustomer cust) throws AbsException {
        if (cust.getAbsBalance() > 0) {
            return new Customer(cust.getName(), cust.getAbsBalance());
        } else
            throw new AbsException("the Customer " + cust.getName() + " has negative balance, it's invalid");
    }
}
