package system;

import customer.Customer;
import exception.AbsException;
import jaxb.generated.AbsDescriptor;
import loan.Loan;
import utils.ABSUtils;

import javax.xml.bind.JAXBException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class System {
    public System() {
        categories = new ArrayList<String>();
        loans = new ArrayList<Loan>();
        customers = new ArrayList<Customer>();
        isLoaded = false;
    }

    private static Integer yaz = 1;
    private List<String> categories;
    private List<Loan> loans;
    private List<Customer> customers;
    private Boolean isLoaded;

    public static void LoadFile(String filePath) throws JAXBException, AbsException {
        InputStream fileStream = null;
        try {
            fileStream = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            throw new AbsException("File not found");
        }
        AbsDescriptor rawDescriptor = ABSUtils.deserializeFrom(fileStream);

    }

    public static void ConvertDescriptorToSystem(AbsDescriptor rawDescriptor){

    }
}
