package dallastools.models.income;

import dallastools.models.masterdata.Item;

import javax.persistence.*;

/**
 * Created by dimmaryanto on 9/24/15.
 */
@Entity
@Table(name = "trans_sales_details")
@SequenceGenerator(name = "mst_trans_sales_details_sq", sequenceName = "sq_trans_sales_details",
        initialValue = 1, allocationSize = 1)
public class SalesDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mst_trans_sales_details_sq")
    private Integer id;
    @OneToOne
    @JoinColumns(@JoinColumn(name = "item_id"))
    private Item item;
    @Column(name = "sell_qty", nullable = false)
    private Integer qty;
    @Column(name = "sell_price", nullable = false)
    private Double priceSell;
    @ManyToOne
    @JoinColumns(@JoinColumn(name = "sales_id"))
    private Sales sales;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Double getPriceSell() {
        return priceSell;
    }

    public void setPriceSell(Double priceSell) {
        this.priceSell = priceSell;
    }

    public Sales getSales() {
        return sales;
    }

    public void setSales(Sales sales) {
        this.sales = sales;
    }
}
