package customer;

import exception.AbsException;
import jaxb.generated.AbsCustomer;
import jaxb.generated.AbsCustomers;

import java.util.HashMap;
import java.util.Map;

public class Customers {

    private Map<String, Customer> allCustomers = null;

    private Customers(Map<String, Customer> allCustomers) {
        this.allCustomers = allCustomers;
    }

    public static Customers ConvertRawAbsToCustomers(AbsCustomers rawVer) throws AbsException {
        Map<String, Customer> resMap = new HashMap<>();
        for (AbsCustomer cust : rawVer.getAbsCustomer()
        ) {
            Customer toAdd = Customer.ConvertRawAbsToCustomer(cust);
            if (resMap.containsKey(toAdd.getName())) {
                throw new AbsException("We can't have two customers with same name");
            } else {
                resMap.put(toAdd.getName(), toAdd);
            }
        }
        Customers res = new Customers(resMap);
        return res;
    }
}
