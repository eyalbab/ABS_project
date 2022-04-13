package customer;

public class Transaction {

    private Integer yazDate;
    private Integer sum;
    private Boolean income;
    private Integer prevBalance;
    private Integer afterBalance;

    public Transaction(int yazDate, int sum, Boolean income, int prevBalance) {
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
