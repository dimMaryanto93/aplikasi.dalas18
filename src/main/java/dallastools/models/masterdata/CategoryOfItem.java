package dallastools.models.masterdata;

import dallastools.models.BasedTableEntity;

import javax.persistence.*;

/**
 * Created by dimmaryanto on 9/24/15.
 */
@Entity
@Table(name = "mst_category_of_items")
public class CategoryOfItem extends BasedTableEntity {

    @Id
    @Column(name = "category_id", length = 50, nullable = false, unique = true)
    private String id;
    @Column(name = "category_name", length = 100, nullable = false)
    private String name;
    @Lob
    @Column(name = "category_description")
    private String description;

    public CategoryOfItem() {
    }

    public CategoryOfItem(String id, String categoryName, String description) {
        this.id = id;
        this.name = categoryName;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String categoryId) {
        this.id = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String categoryName) {
        this.name = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
