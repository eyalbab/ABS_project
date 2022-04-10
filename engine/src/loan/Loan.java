package loan;

import customer.type.Lender;
import exception.AbsException;
import jaxb.generated.AbsLoan;
import utils.ABSUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Loan implements Serializable {

    private LoanStatus status;
    private final String category;
    private final int capital;            //sum of loan, without interest
    private final int totalYazTime;
    private final int paymentRatio;       //pay every yaz
    private final int interest;
    private List<Lender> lenders;
    private final String owner;
    private final String Id;

    public Loan(String Id, String owner, String category, int capital, int totalYaz, int payRate, int interest) {
        this.Id = Id;
        this.status = LoanStatus.NEW;
        this.category = category;
        this.capital = capital;
        this.totalYazTime = totalYaz;
        this.paymentRatio = payRate;
        this.interest = interest;
        this.owner = owner;
        this.lenders = new ArrayList<Lender>();

    }

    public static Loan ConvertRawAbsToLoan(AbsLoan loan) throws AbsException {
        if (loan.getAbsCapital() < 1) {
            throw new AbsException("Abs capital must be positive");
        }
        if (loan.getAbsIntristPerPayment() < 0) {
            throw new AbsException("Abs interest must be positive");
        }
        if (loan.getAbsTotalYazTime() < 1) {
            throw new AbsException("Abs total yaz must be positive");
        }
        if (loan.getAbsPaysEveryYaz() < 1) {
            throw new AbsException("Abs payment rate must be positive");
        }
        if (loan.getAbsTotalYazTime() % loan.getAbsPaysEveryYaz() != 0) {
            throw new AbsException("total yaz must divide with rate without remainder");
        }
        String sanityID = ABSUtils.sanitizeStr(loan.getId());
        String sanityCategory = ABSUtils.sanitizeStr(loan.getAbsCategory());
        Loan res = new Loan(sanityID, loan.getAbsOwner(), sanityCategory, loan.getAbsCapital(), loan.getAbsTotalYazTime(), loan.getAbsPaysEveryYaz(), loan.getAbsIntristPerPayment());
        return res;
    }

    public enum LoanStatus {
        NEW, PENDING, ACTIVE, RISK, FINISHED
    }

    public String getId() {
        return Id;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public String getCategory() {
        return category;
    }

    public int getCapital() {
        return capital;
    }

    public int getTotalYazTime() {
        return totalYazTime;
    }

    public int getPaymentRatio() {
        return paymentRatio;
    }

    public int getInterest() {
        return interest;
    }

    public List<Lender> getLenders() {
        return lenders;
    }

    public String getOwner() {
        return owner;
    }

    public int getTotalPay() {
        return capital * ((100 + interest) / 100);
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        String res = "_________________________\n" +
                "ID: " + getId() + "\n" +
                "Owner: " + getOwner() + "\n" +
                "Category: " + getCategory() + "\n" +
                "Capital: " + getCapital() + ", Total period: " + getTotalYazTime() + " Yaz\n" +
                "Interest: " + getInterest() + "%, Pay rate:" + getPaymentRatio() + "Yaz\n" +
                "Status: " + getStatus();
        switch (status){
            case PENDING:
                //Show all lenders and their investment, total raised sum, amount left for active
                break;
            case ACTIVE:
                //show lenders and their investment, which yaz became active, when is next payment
                //and all payments so far: yaz of payment, capital, interest, total
                break;
            case RISK:
                //show all active
                case
        }

    }
}
