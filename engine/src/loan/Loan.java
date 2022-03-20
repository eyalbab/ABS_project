package loan;

import customer.type.Borrower;
import customer.type.Lender;

import java.util.List;

public class Loan {

    public Loan(Borrower borrower) {
        this.borrower = borrower;
    }

    public enum LoanStatus {
        PENDING, ACTIVE, RISK, FINISHED
    }

    private LoanStatus status;
    private List<Lender> lenders;
    private final Borrower borrower;

}
