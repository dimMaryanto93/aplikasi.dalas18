package dallastools.models.other;

import java.time.YearMonth;

/**
 * Created by dimmaryanto on 08/11/15.
 */
public class ItemSumOfMonth {
    private YearMonth month;
    private Integer qty;

    public ItemSumOfMonth(YearMonth month, Integer qty) {
        this.month = month;
        this.qty = qty;
    }

    public YearMonth getMonth() {
        return month;
    }

    public void setMonth(YearMonth month) {
        this.month = month;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }
}
