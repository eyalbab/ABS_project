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
import java.util.*;
import java.util.stream.Collectors;

public class MySystem {

    private Integer yaz = 1;
    private Categories categories;
    private Loans loans;
    private Customers allCustomers;
    private final Boolean isLoaded;

    public MySystem() {
        isLoaded = false;
    }

    public MySystem(Categories categories, Loans loans, Customers allCustomers) {
        this.isLoaded = true;
        this.categories = categories;
        this.loans = loans;
        this.allCustomers = allCustomers;
    }

    public static MySystem ConvertDescriptorToSystem(AbsDescriptor rawDescriptor) throws AbsException {
        Customers tmpAllCustomers = Customers.ConvertRawAbsToCustomers(rawDescriptor.getAbsCustomers());
        Categories tmpAllCategories = Categories.ConvertRawAbsToCategories(rawDescriptor.getAbsCategories());
        Loans tmpAllLoans = Loans.ConvertRawAbsToLoans(rawDescriptor.getAbsLoans());
        for (Loan loan : tmpAllLoans.getAllLoans()) {
            if (tmpAllCustomers.getCustomerByName(loan.getOwner()).equals(Optional.empty())) {
                throw new AbsException("Loan " + loan.getId() + " owner doesn't exist");
            }

            if (!tmpAllCategories.getAllCategories().contains(loan.getCategory())) {
                throw new AbsException("Loan " + loan.getCategory() + " category doesn't exist");
            }
            tmpAllCustomers.getCustomerByName(loan.getOwner()).get().addNewBorrowing(loan);
        }
        return new MySystem(tmpAllCategories, tmpAllLoans, tmpAllCustomers);
    }

    public static MySystem LoadFile(String filePath) throws JAXBException, AbsException {
        InputStream fileStream;
        if (!filePath.endsWith(".xml")) {
            throw new AbsException("invalid file type");
        }
        try {
            fileStream = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            throw new AbsException("File not found");
        }
        AbsDescriptor rawDescriptor = ABSUtils.deserializeFrom(fileStream);
        return ConvertDescriptorToSystem(rawDescriptor);
    }

    public String handleDeposit(String userPick, String sum) {
        Double sanitizedSum = (double) ABSUtils.tryParseIntAndValidateRange(sum, 0, Integer.MAX_VALUE);
        if (sanitizedSum < 1) {
            return "Invalid sum to deposit";
        }
        Integer userPickInt = ABSUtils.tryParseIntAndValidateRange(userPick, 1, allCustomers.getAllCustomers().size());
        if (userPickInt == -1) {
            return "Invalid customer index";
        }
        Customer toDeposit = allCustomers.getAllCustomers().get(userPickInt - 1);
        if (toDeposit != null) {
            toDeposit.addTransaction(getYaz(), sanitizedSum, Boolean.TRUE);
            return "Deposit was Successful";
        } else {
            return "Couldn't find the customer you asked for";
        }
    }

    public String handleWithdraw(String userPick, String sum) {
        Integer userPickInt = ABSUtils.tryParseIntAndValidateRange(userPick, 1, allCustomers.getAllCustomers().size());
        if (userPickInt == -1) {
            return "Invalid customer index";
        }
        Customer toWithdraw = allCustomers.getAllCustomers().get(userPickInt - 1);
        if (toWithdraw == null) {
            return "Couldn't find the customer you asked for";
        }
        Double sanitizedSum = ABSUtils.tryParseDoubleAndValidateRange(sum, 0.0, toWithdraw.getBalance());
        if (sanitizedSum < 1) {
            return "Invalid sum to Withdraw";
        } else {
            toWithdraw.addTransaction(getYaz(), sanitizedSum, Boolean.FALSE);
            return "Withdraw was Successful";
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


    public void handleLoanAssignmentPicks(String customerIndex, String sumToInvest,
                                          String minInterest, String minYazTime) throws AbsException {
        Integer customerIndexInt =
                ABSUtils.tryParseIntAndValidateRange(customerIndex, 1, allCustomers.getAllCustomers().size());
        if (customerIndexInt == -1) {
            throw new AbsException("invalid customer pick");
        } else {
            if (!sumToInvest.equals("") && ABSUtils.tryParseDoubleAndValidateRange
                    (sumToInvest, 1.0, allCustomers.getAllCustomers().get(customerIndexInt - 1).getBalance()) == -1.0) {
                throw new AbsException("invalid investment sum");
            }
        }
        if (!minInterest.equals("")) {
            if (ABSUtils.tryParseIntAndValidateRange(minInterest, 1, Integer.MAX_VALUE) == -1) {
                throw new AbsException("invalid Interest Percent");
            }
        }
        if (!minYazTime.equals("")) {
            if (ABSUtils.tryParseIntAndValidateRange(minYazTime, 1, Integer.MAX_VALUE) == -1) {
                throw new AbsException("invalid  total yaz time");
            }
        }
    }

    public List<Loan> showSuggestedLoans(String customerIndex, String sumToInvest, String categoriesPick,
                                         String minInterest, String minYazTime) throws AbsException {
        handleLoanAssignmentPicks(customerIndex, sumToInvest, minInterest, minYazTime);
        List<String> pickedCategories = validateCategoriesPick(categoriesPick);
        if (pickedCategories == null) {
            throw new AbsException("invalid  categories pick");
        }
        if (minInterest.equals("")) minInterest = "0";
        Integer finalInterest = Integer.parseInt(minInterest);
        if (minYazTime.equals("")) minYazTime = "0";
        Integer finalYazTime = Integer.parseInt(minYazTime);
        List<Loan.LoanStatus> investableStatuses = Arrays.asList(Loan.LoanStatus.NEW, Loan.LoanStatus.PENDING);
        return loans.getAllLoans().stream()
                .filter(loan -> !loan.getOwner().equals(allCustomers.getAllCustomers().get(Integer.parseInt(customerIndex) - 1).getName()))
                .filter(loan -> pickedCategories.contains(loan.getCategory()))
                .filter(loan -> investableStatuses.contains(loan.getStatus()))
                .filter(loan -> loan.getInterest() >= finalInterest)
                .filter(loan -> loan.getTotalYazTime() >= finalYazTime)
                .collect(Collectors.toList());
    }

    private List<String> validateCategoriesPick(String categoriesPick) {
        if ("".equals(categoriesPick)) {
            return getCategories().getAllCategories();
        }
        Integer categSize = getCategories().getAllCategories().size();
        List<String> res = new ArrayList<>();
        String[] tmp = categoriesPick.split(",");
        for (String categ : tmp) {
            Integer toAddIndex = ABSUtils.tryParseIntAndValidateRange(categ, 1, categSize);
            if (toAddIndex.equals(-1)) {
                return null;
            } else {
                res.add(getCategories().getAllCategories().get(toAddIndex - 1));
            }
        }
        return res;
    }

    public List<Loan> handleLoansPick(String loansPick, List<Loan> optionalLoans) {
        if ("".equals(loansPick)) {
            return null;
        }
        List<Loan> res = new ArrayList<>();
        String[] tmp = loansPick.split(",");
        for (String loanPick : tmp) {
            Integer toAddIndex = ABSUtils.tryParseIntAndValidateRange(loanPick, 1, optionalLoans.size());
            if (toAddIndex == -1) {
                return null;
            } else {
                res.add(optionalLoans.get(toAddIndex - 1));
            }
        }
        return res;
    }

    public void assignLoans(Customer assignTo, List<Loan> loansToAssign, Integer sumToInvest) {
        List<Integer> sumToInvestEachLoan = assignmentsAlgorithm(loansToAssign, sumToInvest);
        for (int i = 0; i < loansToAssign.size(); i++) {
            Loan loanToAssign = loansToAssign.get(i);
            Integer sumToInvestPerLoan = sumToInvestEachLoan.get(i);
            loanToAssign.newInvestment(assignTo.getName(), sumToInvestPerLoan, yaz);
            assignTo.addNewLending(loanToAssign, sumToInvestPerLoan, yaz);
        }

    }

    private List<Integer> assignmentsAlgorithm(List<Loan> loansToAssign, Integer sumToInvest) {
        if (loansToAssign == null) return null;
        List<Integer> leftToInvest = new ArrayList<>();
        List<Integer> sumToInvestEachLoan = new ArrayList<>(Collections.nCopies(loansToAssign.size(), 0));
        loansToAssign.forEach(loan -> leftToInvest.add(loan.getCapital() - loan.getRecruited()));
        while (sumToInvest > 0 && countLeftToInvest(leftToInvest) > 0) {
            Integer minIndex = getCurrentMinToInvest(leftToInvest);
            Integer minSumToInvest = leftToInvest.get(minIndex);
            Integer equalSplit = sumToInvest / countLeftToInvest(leftToInvest);
            if (sumToInvest < countLeftToInvest(leftToInvest)) {            //it can't split between all loans
                int i = 0;
                while (sumToInvest > 0 && countLeftToInvest(leftToInvest) > 0) {
                    if (leftToInvest.get(i) > 0) {
                        sumToInvestEachLoan.set(i, sumToInvestEachLoan.get(i) + 1);
                        leftToInvest.set(i, leftToInvest.get(i) - 1);
                        sumToInvest -= 1;
                    }
                    i++;
                }
            } else {
                sumToInvest = (equalSplit <= minSumToInvest) ?
                        splitAmountForAllLoans(sumToInvest, leftToInvest, sumToInvestEachLoan, equalSplit) :
                        splitAmountForAllLoans(sumToInvest, leftToInvest, sumToInvestEachLoan, minSumToInvest);
            }
        }
        return sumToInvestEachLoan;
    }

    private Integer splitAmountForAllLoans(Integer sumToInvest, List<Integer> leftToInvest,
                                           List<Integer> sumToInvestEachLoan, Integer amountToSplit) {
        for (int i = 0; i < leftToInvest.size(); i++) {
            if (leftToInvest.get(i) > 0) {
                sumToInvestEachLoan.set(i, sumToInvestEachLoan.get(i) + amountToSplit);
                leftToInvest.set(i, leftToInvest.get(i) - amountToSplit);
                sumToInvest -= amountToSplit;
            }
        }
        return sumToInvest;
    }

    private Integer countLeftToInvest(List<Integer> leftToInvest) {
        Integer count = 0;
        for (Integer left : leftToInvest) {
            if (left != 0)
                count++;
        }
        return count;
    }

    private Integer getCurrentMinToInvest(List<Integer> leftToInvest) {
        int minIndex = 0;
        while (leftToInvest.get(minIndex).equals(0)) {
            minIndex++;
        }
        for (int i = 1; i < leftToInvest.size(); i++) {
            if (leftToInvest.get(i) < leftToInvest.get(minIndex) && !leftToInvest.get(i).equals(0)) {
                minIndex = i;
            }
        }
        return minIndex;
    }

    public void continueTimeline() {
        yaz++;
        for (Customer customer : allCustomers.getAllCustomers()) {
            Map<Loan, Double> loansThatPaid = customer.handleContinue(yaz);    //returns all loans that customer could pay
            for (Loan loan : loansThatPaid.keySet()) {
                Set<String> lendersToPay = loan.getLenders().keySet();
                for (String customerName : lendersToPay) {
                    Double percentToPay = loan.getLoanPercentForEachLender().get(customerName);
                    allCustomers.getCustomerByName(customerName).get()
                            .addTransaction(yaz, (percentToPay * loansThatPaid.get(loan)) / 100.0, true);
                }
            }
        }

    }
}
