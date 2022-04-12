package loan;

import exception.AbsException;
import jaxb.generated.AbsLoan;
import utils.ABSUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Loan implements Serializable {

    private LoanStatus status;
    private final String category;
    private final Integer capital;            //sum of loan, without interest
    private final Integer totalYazTime;
    private final Integer paymentRatio;       //pay every yaz
    private final Integer interest;
    private Map<String, Integer> lenders;     //key is lender name and the value is the amount of money
    private Integer recruited;
    private final String owner;
    private final String Id;
    private Integer activatedYaz;
    private final Integer finishedYaz;
    private Integer nextPaymentYaz;
    private List<Payment> loanPayments;

    public Loan(String Id, String owner, String category, Integer capital, Integer totalYaz, Integer payRate, Integer interest) {
        this.Id = Id;
        this.status = LoanStatus.NEW;
        this.category = category;
        this.capital = capital;
        this.totalYazTime = totalYaz;
        this.paymentRatio = payRate;
        this.interest = interest;
        this.owner = owner;
        this.recruited = 0;
        this.lenders = new HashMap<>();
        this.activatedYaz = null;
        this.finishedYaz = null;
        this.nextPaymentYaz = null;
        this.loanPayments = new ArrayList<>();
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

    public Integer getCapital() {
        return capital;
    }

    public Integer getTotalYazTime() {
        return totalYazTime;
    }

    public Integer getPaymentRatio() {
        return paymentRatio;
    }

    public Integer getInterest() {
        return interest;
    }

    public Map<String, Integer> getLenders() {
        return lenders;
    }

    public String getOwner() {
        return owner;
    }

    public Integer getTotalPayCount() {
        return totalYazTime/paymentRatio;
    }

    public Integer getRecruited() {
        return recruited;
    }

    public void setRecruited(Integer recruited) {
        this.recruited = recruited;
    }

    public void setActivatedYaz(Integer activatedYaz) {
        this.activatedYaz = activatedYaz;
    }

    public void setNextPaymentYaz(Integer nextPaymentYaz) {
        this.nextPaymentYaz = nextPaymentYaz;
    }

    public Integer getFinishedYaz() {
        return finishedYaz;
    }

    public List<Payment> getLoanPayments() {
        return loanPayments;
    }

    public Integer getTotalPay() {
        return capital * ((100 + interest) / 100);
    }

    public Integer getActivatedYaz() {
        return activatedYaz;
    }

    public Integer getNextPaymentYaz() {
        return nextPaymentYaz;
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
                "Interest: " + getInterest() + "%, Pay rate: " + getPaymentRatio() + " Yaz\n" +
                "Status: " + getStatus() + "\n";
        switch (status) {
            case NEW:
                break;
            case PENDING:
                //Show all lenders and their investment, total raised sum, amount left for active
                res += "Lenders: \n";
                for (Map.Entry<String, Integer> entry : lenders.entrySet()) {
                    res += entry.getKey() + " payed " + entry.getValue() + " dollars \n";
                }
                res += "Amount that has been recruited: " + getRecruited() + " dollars\n " +
                        "Amount that left to recruit: " + (getCapital() - getRecruited()) + "\n";
                break;
            case ACTIVE:
                res = getActiveInfo(res);
                break;
            case RISK:
                res = getActiveInfo(res);
                res = getRiskedPaymentInfo(res);
                break;
            case FINISHED:
                res += "Lenders: \n";
                for (Map.Entry<String, Integer> entry : lenders.entrySet()) {
                    res += entry.getKey() + " payed " + entry.getValue() + " dollars \n";
                }
                res += "Active yaz: " + activatedYaz + ", Finished yaz: " + finishedYaz + "\n";
                res += showPaymentInfo(Boolean.TRUE);
                break;
        }
        return res;
    }

    private String getActiveInfo(String res) {
        res += "Lenders: \n";
        for (Map.Entry<String, Integer> entry : lenders.entrySet()) {
            res += entry.getKey() + " payed " + entry.getValue() + "\n";
        }
        res += "Activated on: " + getActivatedYaz() + " yaz, Next payment on: " + getNextPaymentYaz() + " Yaz\n";
        res += showPaymentInfo(Boolean.FALSE);
        Integer capitalPaid = getCurrentTotalPaidCapital();
        Integer interestPaid = getCurrentTotalPaidInterest();
        res += "Paid Capital: " + capitalPaid + " Paid Interest: " + interestPaid + " Left Capital: "
                + (capital - capitalPaid) + " left interest: " + ((getTotalPay() - capital) - interestPaid + "\n");
        return res;
    }

    public String getRiskedPaymentInfo(String res) {
        Integer numOfUnpaid = 0;
        Integer sumOfUnpaid = 0;
        for (Payment payment : loanPayments
        ) {
            if (!payment.getPaid()) {
                numOfUnpaid++;
                sumOfUnpaid += payment.getCapital() + payment.getInterest();
            }
        }
        res += "Number of unpaid: " + numOfUnpaid + ", total sum unpad: " + sumOfUnpaid + "\n";
        return res;

    }

    public Integer getCurrentTotalPaidCapital() {
        Integer res = 0;
        for (Payment payment : loanPayments
        ) {
            if (payment.getPaid()) {
                res += payment.getCapital();
            }
        }
        return res;
    }

    public Integer getCurrentTotalPaidInterest() {
        Integer res = 0;
        for (Payment payment : loanPayments
        ) {
            if (payment.getPaid()) {
                res += payment.getInterest();
            }
        }
        return res;
    }

    public String showPaymentInfo(Boolean onlyPaid) {
        String res = "Loan Payment:\n";
        for (Payment payment : loanPayments
        ) {
            if (onlyPaid) {
                if (payment.getPaid()) {
                    res += payment.toString();
                }
            } else {
                res += payment.toString();
            }
        }
        return res;
    }
}
