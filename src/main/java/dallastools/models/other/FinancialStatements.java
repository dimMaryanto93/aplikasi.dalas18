package dallastools.models.other;

/**
 * Created by dimmaryanto on 28/11/15.
 */
public class FinancialStatements {
    private String id;
    private Double debit;
    private Double credit;
    private Double total;


    public Double getCredit() {
        return credit;
    }

    public void setCredit(Double credit) {
        this.credit = credit;
    }

    public Double getDebit() {
        return debit;
    }

    public void setDebit(Double debit) {
        this.debit = debit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}
