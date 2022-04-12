package loan;

public class Payment {

    private final Integer yazOfPay;
    private final Integer capital;
    private final Integer interest;
    private final Boolean paid;

    public Payment(Integer yazOfPay, Integer capital, Integer interest, Boolean paid) {
        this.yazOfPay = yazOfPay;
        this.capital = capital;
        this.interest = interest;
        this.paid = paid;
    }

    public Integer getYazOfPay() {
        return yazOfPay;
    }

    public Integer getCapital() {
        return capital;
    }

    public Integer getInterest() {
        return interest;
    }

    public Boolean getPaid() {
        return paid;
    }

    @Override
    public String toString() {
        String res = "Yaz of pay: " + yazOfPay +
                ", capital: " + capital +
                ", interest: " + interest +
                ", total pay: " + (capital + interest) +
                '\n';
        if(!getPaid()){
            res = "# DIDN'T PAY! "+res;
        }
        return res;
    }
}
