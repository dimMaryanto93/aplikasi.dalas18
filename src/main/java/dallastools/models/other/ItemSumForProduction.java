package dallastools.models.other;

import dallastools.models.masterdata.Item;

/**
 * Created by dimmaryanto on 23/11/15.
 */
public class ItemSumForProduction {
    private Item item;
    private Integer qty;
    private Double price;
    private Double subTotal;

    public ItemSumForProduction() {
    }

    public ItemSumForProduction(Item item, Integer qty, Double price) {
        this.item = item;
        this.price = price;
        this.qty = qty;
        this.setSubTotal(price, qty);
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(Double price, Integer qty) {
        this.subTotal = price * qty;
    }
}
