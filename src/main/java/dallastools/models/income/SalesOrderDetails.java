package dallastools.models.income;

import dallastools.models.masterdata.Item;

import javax.persistence.*;

/**
 * Created by dimmaryanto on 9/24/15.
 */
@Entity
@Table(name = "trans_sales_order_details")
@SequenceGenerator(name = "trans_sales_order_details_sq", sequenceName = "sq_trans_sales_order_details",
        initialValue = 1, allocationSize = 1)
public class SalesOrderDetails {

    @ManyToOne
    @JoinColumns(@JoinColumn(name = "order_id"))
    public SalesOrder salesOrder;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trans_sales_order_details_sq")
    private Integer id;
    @OneToOne
    @JoinColumns(@JoinColumn(name = "item_id"))
    private Item item;
    @Column(nullable = false)
    private Integer qty;

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

    public SalesOrder getSalesOrder() {
        return salesOrder;
    }

    public void setSalesOrder(SalesOrder salesOrder) {
        this.salesOrder = salesOrder;
    }
}
