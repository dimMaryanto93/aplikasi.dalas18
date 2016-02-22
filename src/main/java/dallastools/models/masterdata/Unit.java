package dallastools.models.masterdata;

import dallastools.models.BasedTableEntity;

import javax.persistence.*;

/**
 * Created by dimmaryanto on 9/24/15.
 */
@Entity
@Table(name = "mst_unit_of_items")
public class Unit extends BasedTableEntity {

    @Id
    @Column(name = "unit_id", length = 25, nullable = false, unique = true)
    private String id;
    @Column(name = "unit_name", length = 100, nullable = false)
    private String name;
    @Lob
    @Column(name = "unit_description")
    private String description;

    public Unit() {
    }

    public Unit(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String unitId) {
        this.id = unitId;
    }

    public String getName() {
        return name;
    }

    public void setName(String unitName) {
        this.name = unitName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
