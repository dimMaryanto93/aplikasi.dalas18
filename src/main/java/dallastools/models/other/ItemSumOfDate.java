package dallastools.models.other;

import java.time.LocalDate;

/**
 * Created by dimmaryanto on 08/11/15.
 */
public class ItemSumOfDate {
    private LocalDate date;
    private Integer qty;

    public ItemSumOfDate(LocalDate date, Integer qty) {
        this.date = date;
        this.qty = qty;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }
}
