package dallastools.models.other;

import dallastools.models.masterdata.Item;

/**
 * Created by dimmaryanto on 08/11/15.
 */
public class ItemSum {
    private Item item;
    private Long qty;

    public ItemSum() {
    }

    public ItemSum(Item item, Long qty) {
        this.item = item;
        this.qty = qty;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Long getQty() {
        return qty;
    }

    public void setQty(Long qty) {
        this.qty = qty;
    }
}
