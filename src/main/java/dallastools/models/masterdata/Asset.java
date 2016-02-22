package dallastools.models.masterdata;

import dallastools.models.BasedTableEntity;

import javax.persistence.*;

/**
 * Created by dimmaryanto on 18/10/15.
 */
@Entity
@Table(name = "mst_assets")
@SequenceGenerator(name = "mst_asset_sq", allocationSize = 1, initialValue = 1, sequenceName = "sq_mst_asset")
public class Asset extends BasedTableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mst_asset_sq")
    private Integer id;

    @Column(name = "asset_name", length = 50, nullable = false, unique = true)
    private String name;

    @Column(name = "ammount", nullable = false)
    private Double ammount;

    @Column(name = "qty", nullable = false)
    private Integer qty;

    @Lob
    @Column(name = "asset_description")
    private String description;

    public Asset() {
    }

    public Asset(Double ammount, String description, String name, Integer qty) {
        this.ammount = ammount;
        this.description = description;
        this.name = name;
        this.qty = qty;
    }

    public Double getAmmount() {
        return ammount;
    }

    public void setAmmount(Double ammount) {
        this.ammount = ammount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }
}
