package customer;

import exception.AbsException;
import jaxb.generated.AbsCustomer;
import loan.Loan;
import utils.ABSUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Customer implements Serializable {
    private final String name;
    private int balance;
    private List<Transaction> transactions;
    private List<Loan> borrowLoans;
    private List<Loan> lendingLoans;

    public Customer(String name, int balance) {
        this.name = name;
        this.balance = balance;
        this.transactions = new ArrayList<>();
        this.borrowLoans = new ArrayList<>();
        this.lendingLoans = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public static Customer convertRawAbsToCustomer(AbsCustomer cust) throws AbsException {
        String custName = ABSUtils.sanitizeStr(cust.getName());
        if (cust.getAbsBalance() > 0) {
            return new Customer(custName, cust.getAbsBalance());
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
        String res = isBorrowLoans ? "Borrow loans: \n" : "Lending loans: \n";
        List<Loan> toIter = isBorrowLoans ? borrowLoans : lendingLoans;
        for (Loan loan : toIter
        ) {
            res += "name:" + loan.getId() + "\n" +
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
        }

        return res;

    }

    public void addTransaction(int yaz, int sum, Boolean income) {
        if (income) {
            transactions.add(new Transaction(yaz, sum, Boolean.TRUE, getBalance()));
            setBalance(getBalance() + sum);
        } else {
            transactions.add(new Transaction(yaz, sum, Boolean.FALSE, getBalance()));
            setBalance(getBalance() - sum);
        }
    }
}
