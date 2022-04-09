package loan;

import customer.type.Borrower;
import customer.type.Lender;

import java.io.Serializable;
import java.util.List;

public class Loan implements Serializable {

    public Loan(Borrower borrower) {
        this.borrower = borrower;
    }

    public enum LoanStatus {
        PENDING, ACTIVE, RISK, FINISHED
    }

    private LoanStatus status;
    private String category;
    private int capital;            //sum of loan, without interest
    private int totalYazTime;
    private int paymentRatio;       //pay every yaz
    private int interest;
    private List<Lender> lenders;
    private final Borrower borrower;

}
