package loan;

import exception.AbsException;
import jaxb.generated.AbsLoan;
import jaxb.generated.AbsLoans;

import java.util.HashMap;
import java.util.Map;

public class Loans {
    private Map<String, Loan> allLoans;

    private Loans(Map<String, Loan> allLoans) {
        this.allLoans = allLoans;
    }

    public static Loans ConvertRawAbsToLoans(AbsLoans rawVer) throws AbsException {
        Map<String, Loan> resMap = new HashMap<>();
        for (AbsLoan loan : rawVer.getAbsLoan()
        ) {
            Loan toAdd = Loan.ConvertRawAbsToLoan(loan);
            if (resMap.containsKey(toAdd.getId())) {
                throw new AbsException("We can't have two loans with same ID");
            } else {
                resMap.put(toAdd.getId(), toAdd);
            }
        }
        Loans res = new Loans(resMap);
        return res;
    }

    public Map<String, Loan> getAllLoans() {
        return allLoans;
    }


    @Override
    public String toString() {
        String res = "Loans in the system:\n";
        for (Loan loan : this.getAllLoans().values()
        ) {
            res += loan;
        }
        return res;
    }
}
