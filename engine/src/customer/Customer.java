package customer;

import exception.AbsException;
import jaxb.generated.AbsCustomer;
import loan.Loan;
import utils.ABSUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class Customer implements Serializable {
    private final String name;
    private Double balance;
    private List<Transaction> transactions;
    private List<Loan> borrowLoans;
    private List<Loan> lendingLoans;

    public Customer(String name, Double balance) {
        this.name = name;
        this.balance = balance;
        this.transactions = new ArrayList<>();
        this.borrowLoans = new ArrayList<>();
        this.lendingLoans = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public static Customer convertRawAbsToCustomer(AbsCustomer cust) throws AbsException {
        String custName = ABSUtils.sanitizeStr(cust.getName());
        if (cust.getAbsBalance() > 0) {
            return new Customer(custName, (double) cust.getAbsBalance());
        } else
            throw new AbsException("the Customer " + custName + " has negative balance, it's invalid");
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder("*** " + name + " ***\n" + "Transactions:\n");
        for (Transaction tran : transactions) {
            res.append(tran.toString()).append("\n");
        }
        res.append(customerLoansToString(Boolean.TRUE));
        res.append(customerLoansToString(Boolean.FALSE));
        return res.toString();
    }

    public String customerLoansToString(Boolean isBorrowLoans) {
        StringBuilder res = new StringBuilder(isBorrowLoans ? "Borrow loans: \n" : "Lending loans: \n");
        List<Loan> toIter = isBorrowLoans ? borrowLoans : lendingLoans;
        for (Loan loan : toIter) {
            res.append(loanInfoForCustomer(loan));
        }

        return res.toString();

    }

    public static String loanInfoForCustomer(Loan loan) {
        String res =
                "name:" + loan.getId() + "\n" +
                        "category:" + loan.getCategory() + "\n" +
                        "capital:" + loan.getCapital() + "\n" +
                        "rate:" + loan.getPaymentRatio() + "\n" +
                        "interest:" + loan.getInterest() + "\n" +
                        "total sum:" + loan.getTotalPay() + "\n";
        switch (loan.getStatus()) {
            case NEW:
                break;
            case PENDING:
                res += "Left for active: " + (loan.getCapital() - loan.getRecruited()) + "\n";
                break;
            case ACTIVE:
                res += "Next payment yaz: " + loan.getNextPaymentYaz() + ", next payment sum: "
                        + (loan.getTotalPay() / loan.getTotalPayCount()) + "\n";
                break;
            case RISK:
                res = loan.getRiskedPaymentInfo(res);
                break;
            case FINISHED:
                res += "Activated yaz: " + loan.getActivatedYaz() + ", finished yaz: " +
                        loan.getFinishedYaz() + "\n";
                break;
        }
        return res;
    }

    public void addTransaction(int yaz, Double sum, Boolean income) {
        if (income) {
            transactions.add(new Transaction(yaz, sum, Boolean.TRUE, getBalance()));
            setBalance(getBalance() + sum);
        } else {
            transactions.add(new Transaction(yaz, sum, Boolean.FALSE, getBalance()));
            setBalance(getBalance() - sum);
        }
    }

    public void addNewLending(Loan addToLending, Integer investment, Integer currentYaz) {
        transactions.add(new Transaction(currentYaz, (double) investment, false, balance));
        setBalance(balance - investment);
        lendingLoans.add(addToLending);
    }

    public void addNewBorrowing(Loan toAdd) {
        borrowLoans.add(toAdd);
    }

    public Map<Loan, Double> handleContinue(Integer yaz) {
        Map<Loan, Double> isPaid = new HashMap<>();
        List<Loan.LoanStatus> activatedStatuses = Arrays.asList(Loan.LoanStatus.ACTIVE, Loan.LoanStatus.RISK);
        List<Loan> activatedSortedLoans = borrowLoans.stream()
                .filter(loan -> activatedStatuses.contains(loan.getStatus()))
                .filter(loan -> loan.getNextPaymentYaz() == yaz)
                .sorted(Loan::compareTo)
                .collect(Collectors.toList());
        for (Loan loan : activatedSortedLoans) {
            if (loan.getNextPaymentSum() <= balance) {                 //Case of still ACTIVE
                addTransaction(yaz, loan.getNextPaymentSum(), false);
                loan.handlePayment(loan.getNextCapitalPaymentSum(), loan.getNextInterestPaymentSum(), yaz, true);
                isPaid.put(loan, loan.getNextPaymentSum());
                if (loan.getTotalPaid() >= loan.getTotalPay()) {           //Case of FINISHED
                    loan.setStatus(Loan.LoanStatus.FINISHED);
                    loan.setFinishedYaz(yaz);
                } else {
                    loan.setStatus(Loan.LoanStatus.ACTIVE);            //case it was RISK and became ACTIVE again
                    loan.setNextCapitalPaymentSum(loan.getNormalNextCapitalPaySum());
                    loan.setNextInterestPaymentSum(loan.getNormalNextInterestPaySum());
                    loan.setNextPaymentSum(loan.getNextPaymentSum());
                }
            } else {                                                  //Case of becoming RISK
                loan.handlePayment(loan.getNextCapitalPaymentSum(), loan.getNextInterestPaymentSum(), yaz, false);
                loan.setStatus(Loan.LoanStatus.RISK);
                loan.setNextCapitalPaymentSum(loan.getNextCapitalPaymentSum() + loan.getNormalNextCapitalPaySum());
                loan.setNextInterestPaymentSum(loan.getNextInterestPaymentSum() + loan.getNormalNextInterestPaySum());
                loan.setNextPaymentSum(loan.getNextPaymentSum());
            }
            loan.setNextPaymentYaz(loan.getNextPaymentYaz() + loan.getPaymentRatio());
        }
        return isPaid;
    }
}
