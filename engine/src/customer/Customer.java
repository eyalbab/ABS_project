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
    private List<Loan> BorrowLoans;
    private List<Loan> LendingLoans;

    public Customer(String name, int balance) {
        this.name = name;
        this.balance = balance;
        this.transactions = new ArrayList<>();
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
        String res = "*** " + name + " ***\n" + " Transactions:\n";
        for (Transaction tran : transactions
        ) {
            res += tran.toString() + "\n";
        }
        res += "Loans as Lender:\n";
        res += "Loans as Borrower:\n";
        return res;
    }

    public String customerLoansToString(Loan loan) {
        String res = "name:" + loan.getId() + "\n" +
                "category:" + loan.getCategory() + "\n" +
                "capital:" + loan.getCapital() + "\n" +
                "rate:" + loan.getPaymentRatio() + "\n" +
                "interest:" + loan.getInterest() + "\n" +
                "total sum:" + loan.getTotalPay() + "\n";
        return res;

    }

    public void addTransaction(int yaz,int sum,Boolean income){
        if(income){
            transactions.add(new Transaction(yaz,sum,Boolean.TRUE,getBalance()));
            setBalance(getBalance()+sum);
        }
        else {
            transactions.add(new Transaction(yaz,sum,Boolean.FALSE,getBalance()));
            setBalance(getBalance()-sum);
        }
    }
}
