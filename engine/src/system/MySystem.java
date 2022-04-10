package system;

import customer.Customer;
import customer.Customers;
import exception.AbsException;
import jaxb.generated.AbsDescriptor;
import loan.Categories;
import loan.Loan;
import loan.Loans;
import utils.ABSUtils;

import javax.xml.bind.JAXBException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MySystem {
    public MySystem() {
        categories = null;
        loans = null;
        allCustomers = null;
        isLoaded = false;
        yaz = 1;
    }

    public MySystem(Categories categories, Loans loans, Customers allCustomers) {
        this.isLoaded = true;
        this.categories = categories;
        this.loans = loans;
        this.allCustomers = allCustomers;
        this.yaz = 1;
    }

    private Integer yaz;
    private Categories categories;
    private Loans loans;
    private Customers allCustomers;
    private Boolean isLoaded;

    public static MySystem ConvertDescriptorToSystem(AbsDescriptor rawDescriptor) throws AbsException {
        Customers tmpAllCustomers = Customers.ConvertRawAbsToCustomers(rawDescriptor.getAbsCustomers());
        Categories tmpAllCategories = Categories.ConvertRawAbsToCategories(rawDescriptor.getAbsCategories());
        Loans tmpAllLoans = Loans.ConvertRawAbsToLoans(rawDescriptor.getAbsLoans());
//        long afterFilterCount = tmpAllLoans.getAllLoans().entrySet().stream()
//                .filter(loan -> tmpAllCustomers.getAllCustomers().containsKey(loan.getValue().getOwner())).count();
//        int beforeFilterCount = tmpAllLoans.getAllLoans().size();
//        if (afterFilterCount != beforeFilterCount) {
//            throw new AbsException("Some loans owners doesn't exit in the system");
//        }
        for (Loan loan : tmpAllLoans.getAllLoans().values()
        ) {
            if (!tmpAllCustomers.getAllCustomers().containsKey(loan.getOwner())) {
                throw new AbsException("Loan " + loan.getId() + " owner doesn't exist");
            }
            if (!tmpAllCategories.getAllCategories().contains(loan.getCategory())) {
                throw new AbsException("Loan " + loan.getCategory() + " category doesn't exist");
            }
        }
        MySystem res = new MySystem(tmpAllCategories, tmpAllLoans, tmpAllCustomers);
        return res;


    }

    public static MySystem LoadFile(String filePath) throws JAXBException, AbsException {
        InputStream fileStream = null;
        if (!filePath.endsWith(".xml")) {
            throw new AbsException("invalid file type");
        }
        try {
            fileStream = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            throw new AbsException("File not found");
        }
        AbsDescriptor rawDescriptor = ABSUtils.deserializeFrom(fileStream);
        MySystem res = ConvertDescriptorToSystem(rawDescriptor);
        return res;

    }

    public String handleDeposit(String userPick, String sum) {
        int sanitizedSum = ABSUtils.tryParseIntAndValidateRange(sum,0,Integer.MAX_VALUE);
        if(sanitizedSum<1){
            return "Invalid sum to deposit";
        }
        ABSUtils.sanitizeStr(userPick);
        Customer toDeposit = allCustomers.getAllCustomers().get(userPick);
        if (toDeposit != null){
            toDeposit.addTransaction(getYaz(),sanitizedSum,Boolean.TRUE);
            return "Deposit was Successful";
        }
        else{
            return "Couldn't find the customer you asked for";
        }
    }

    public Integer getYaz() {
        return yaz;
    }

    public Categories getCategories() {
        return categories;
    }

    public Loans getLoans() {
        return loans;
    }

    public Customers getAllCustomers() {
        return allCustomers;
    }

    public Boolean getLoaded() {
        return isLoaded;
    }
}
