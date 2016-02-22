package dallastools.models.masterdata;

import dallastools.models.BasedTableEntity;

import javax.persistence.*;

/**
 * Created by dimmaryanto on 9/24/15.
 */
@Entity
@Table(name = "mst_items")
@SequenceGenerator(name = "mst_items_sq", sequenceName = "sq_mst_items",
        allocationSize = 1, initialValue = 1)
public class Item extends BasedTableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mst_items_sq")
    @Column(name = "item_generator")
    private Integer itemGenerator;
    @Column(name = "item_id", length = 25, nullable = false, unique = true)
    private String id;
    @Column(name = "item_name", length = 100, nullable = false)
    private String name;
    @OneToOne
    @JoinColumns(@JoinColumn(name = "category_id", nullable = false))
    private CategoryOfItem category;
    @OneToOne
    @JoinColumns(@JoinColumn(name = "unit_id", nullable = false))
    private Unit unit;
    @Column(name = "qty", nullable = false)
    private Integer qty;
    @Column(name = "price_buy", nullable = false)
    private Double priceBuy;
    @Column(name = "price_sell", nullable = false)
    private Double priceSell;
    @Column(name = "is_sell", nullable = false)
    private Boolean sell;
    @OneToOne
    @JoinColumns(@JoinColumn(name = "warehouse_id", nullable = false))
    private Warehouse warehouse;


    public Item() {
    }

    public Item(CategoryOfItem category, String id, String name, Double priceBuy, Double priceSell, Integer qty, Boolean sell, Unit unit, Warehouse warehouse) {
        this.category = category;
        this.id = id;
        this.name = name;
        this.priceBuy = priceBuy;
        this.priceSell = priceSell;
        this.qty = qty;
        this.sell = sell;
        this.unit = unit;
        this.warehouse = warehouse;
    }

    public Integer getItemGenerator() {
        return itemGenerator;
    }

    public void setItemGenerator(Integer itemGenerator) {
        this.itemGenerator = itemGenerator;
    }

    public Boolean getSell() {
        return sell;
    }

    public void setSell(Boolean sell) {
        this.sell = sell;
    }

    public String getId() {
        return id;
    }

    public void setId(String itemId) {
        this.id = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String itemName) {
        this.name = itemName;
    }

    public CategoryOfItem getCategory() {
        return category;
    }

    public void setCategory(CategoryOfItem category) {
        this.category = category;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
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

    public Double getPriceSell() {
        return priceSell;
    }

    public void setPriceSell(Double priceSell) {
        this.priceSell = priceSell;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }
}
