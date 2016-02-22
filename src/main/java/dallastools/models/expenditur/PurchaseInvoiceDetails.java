package dallastools.models.expenditur;

import dallastools.models.masterdata.Item;

import javax.persistence.*;

/**
 * Created by dimmaryanto on 9/25/15.
 */
@Entity
@Table(name = "trans_purchase_invoice_details")
@SequenceGenerator(name = "trans_purchase_details_sq", sequenceName = "sq_trans_purchase_details",
        allocationSize = 1, initialValue = 1)
public class PurchaseInvoiceDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trans_purchase_details_sq")
    private Integer id;
    @OneToOne
    @JoinColumns(@JoinColumn(name = "item_id"))
    private Item item;
    @Column(nullable = false)
    private Integer qty;
    @Column(name = "price_buy", nullable = false, scale = 2)
    private Double priceBuy;
    @ManyToOne
    @JoinColumns(@JoinColumn(name = "purchase_id"))
    private PurchaseInvoice invoice;

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

    public Double getPriceBuy() {
        return priceBuy;
    }

    public void setPriceBuy(Double priceBuy) {
        this.priceBuy = priceBuy;
    }

    public PurchaseInvoice getInvoice() {
        return invoice;
    }

    public void setInvoice(PurchaseInvoice invoice) {
        this.invoice = invoice;
    }
}
