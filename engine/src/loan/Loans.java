package loan;

import exception.AbsException;
import jaxb.generated.AbsLoan;
import jaxb.generated.AbsLoans;

import java.util.ArrayList;
import java.util.List;

public class Loans {
    private final List<Loan> allLoans;

    private Loans(List<Loan> allLoans) {
        this.allLoans = allLoans;
    }

    public static Loans ConvertRawAbsToLoans(AbsLoans rawVer) throws AbsException {
        List<Loan> resList = new ArrayList<>();
        for (AbsLoan loan : rawVer.getAbsLoan()) {
            Loan toAdd = Loan.ConvertRawAbsToLoan(loan);
            if (!resList.isEmpty()) {
                for (Loan loanTmp : resList) {
                    if (loanTmp.getId().equals(toAdd.getId())) {
                        throw new AbsException("We can't have two loans with same ID");
                    }
                }
            }
            resList.add(toAdd);
        }
        return new Loans(resList);
    }

    public List<Loan> getAllLoans() {
        return allLoans;
    }


    @Override
    public String toString() {
        StringBuilder res = new StringBuilder("Loans in the system:\n----------------------\n");
        int i = 1;
        for (Loan loan : this.getAllLoans()) {
            res.append(i++).append(". ").append(loan);
        }
        return res.toString();
    }
}
