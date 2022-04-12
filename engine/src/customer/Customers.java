package customer;

import exception.AbsException;
import jaxb.generated.AbsCustomer;
import jaxb.generated.AbsCustomers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Customers {

    private List<Customer> allCustomers ;

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

        Customers res = new Customers(resList);
        return res;
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
        String res = "Customers in the system:\n";
        int i = 1;
        for (Customer cust : this.getAllCustomers()) {
            res += i++ + ". " + cust + "\n";
        }
        return res;
    }

    public String showAllCustomersNameByOrder() {
        String res = "Customers in the system:\n";
        int i = 1;
        for (Customer cust : this.getAllCustomers()
        ) {
            res += i++ + ". " + cust.getName() + " Balance - " + cust.getBalance() + "\n";
        }
        return res;
    }
}
