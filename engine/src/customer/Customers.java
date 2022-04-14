package customer;

import exception.AbsException;
import jaxb.generated.AbsCustomer;
import jaxb.generated.AbsCustomers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Customers implements Serializable {

    private final List<Customer> allCustomers;

    private Customers(List<Customer> allCustomers) {
        this.allCustomers = allCustomers;
    }

    public static Customers ConvertRawAbsToCustomers(AbsCustomers rawVer) throws AbsException {
        List<Customer> resList = new ArrayList<>();
        for (AbsCustomer cust : rawVer.getAbsCustomer()) {
            Customer toAdd = Customer.convertRawAbsToCustomer(cust);
            if (!resList.isEmpty()) {
                for (Customer customer : resList) {
                    if (customer.getName().equals(toAdd.getName())) {
                        throw new AbsException("We can't have two customers with same name");
                    }
                }
            }
            resList.add(toAdd);
        }

        return new Customers(resList);
    }

    public List<Customer> getAllCustomers() {
        return allCustomers;
    }

    public Optional<Customer> getCustomerByName(String name) {
        for (Customer customer : allCustomers) {
            if (customer.getName().equals(name)) {
                return Optional.of(customer);
            }
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder("Customers in the system:\n");
        int i = 1;
        for (Customer cust : this.getAllCustomers()) {
            res.append(i++).append(". ").append(cust).append("\n");
        }
        return res.toString();
    }

    public String showAllCustomersNameByOrder() {
        StringBuilder res = new StringBuilder("Customers in the system:\n");
        int i = 1;
        for (Customer cust : this.getAllCustomers()) {
            res.append(i++).append(". ").append(cust.getName()).append(" Balance - ").append(cust.getBalance()).append("\n");
        }
        return res.toString();
    }
}
