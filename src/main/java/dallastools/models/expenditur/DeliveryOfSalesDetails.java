package dallastools.models.expenditur;

import dallastools.models.income.Sales;

import javax.persistence.*;

/**
 * Created by dimmaryanto on 9/24/15.
 */
@Entity
@Table(name = "trans_sales_delivery_details")
@SequenceGenerator(name = "trans_sales_delivery_details_sq", sequenceName = "sq_trans_sales_delivery_details")
public class DeliveryOfSalesDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trans_sales_delivery_details_sq")
    private Integer id;
    @ManyToOne
    @JoinColumns(@JoinColumn(name = "sales_id"))
    private Sales sales;
    @Column(name = "wight_per_kg", nullable = false)
    private Integer wightPerKg;
    @ManyToOne
    @JoinColumns(@JoinColumn(name = "delivery_id"))
    private DeliveryOfSales deliveryOfSales;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Sales getSales() {
        return sales;
    }

    public void setSales(Sales sales) {
        this.sales = sales;
    }

    public Integer getWightPerKg() {
        return wightPerKg;
    }

    public void setWightPerKg(Integer wightPerKg) {
        this.wightPerKg = wightPerKg;
    }

    public DeliveryOfSales getDeliveryOfSales() {
        return deliveryOfSales;
    }

    public void setDeliveryOfSales(DeliveryOfSales deliveryOfSales) {
        this.deliveryOfSales = deliveryOfSales;
    }
}
