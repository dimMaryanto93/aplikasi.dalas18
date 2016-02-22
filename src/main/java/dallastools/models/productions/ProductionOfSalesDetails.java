package dallastools.models.productions;

import dallastools.models.masterdata.Item;

import javax.persistence.*;

/**
 * Created by dimmaryanto on 18/10/15.
 */
@Entity
@Table(name = "trans_production_of_sales_details")
@SequenceGenerator(name = "trans_production_of_sales_details_sq",
        sequenceName = "sq_trans_sales_production_details", allocationSize = 1, initialValue = 1)
public class ProductionOfSalesDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trans_production_of_sales_details_sq")
    private Integer id;

    @ManyToOne
    @JoinColumns(@JoinColumn(name = "item_id"))
    private Item item;

    @Column(name = "item_count", nullable = false)
    private Integer itemUsed;

    @ManyToOne
    @JoinColumns(@JoinColumn(name = "production_id"))
    private ProductionOfSales production;

    public Integer getItemUsed() {
        return itemUsed;
    }

    public void setItemUsed(Integer itemUsed) {
        this.itemUsed = itemUsed;
    }

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

    public ProductionOfSales getProduction() {
        return production;
    }

    public void setProduction(ProductionOfSales production) {
        this.production = production;
    }

}
