package customer;

public class Transaction {

    private Integer yazDate;
    private Double sum;
    private Boolean income;
    private Double prevBalance;
    private Double afterBalance;

    public Transaction(int yazDate, Double sum, Boolean income, Double prevBalance) {
        this.yazDate = yazDate;
        this.sum = sum;
        this.income = income;
        this.prevBalance = prevBalance;
        this.afterBalance = income ? sum + prevBalance : prevBalance - sum;
    }

    @Override
    public String toString() {
        String res = income ? "+" : "-";
        res += "Yaz:" + yazDate + ", Sum:" + sum + ", Previous Balance:" + prevBalance + ", Current Balance:" + afterBalance;
        return res;
    }
}
