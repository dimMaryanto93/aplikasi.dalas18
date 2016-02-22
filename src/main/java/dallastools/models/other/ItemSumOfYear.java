package dallastools.models.other;

import java.time.Year;

/**
 * Created by dimmaryanto on 08/11/15.
 */
public class ItemSumOfYear {
    private Year year;
    private Integer qty;

    public ItemSumOfYear(Integer qty, Year year) {
        this.qty = qty;
        this.year = year;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Year getYear() {
        return year;
    }

    public void setYear(Year year) {
        this.year = year;
    }
}
