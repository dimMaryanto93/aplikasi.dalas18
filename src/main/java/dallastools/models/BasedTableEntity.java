package dallastools.models;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.sql.Timestamp;

/**
 * Created by dimmaryanto on 9/24/15.
 */
@MappedSuperclass
public class BasedTableEntity {
    @Column(name = "created_date")
    private Timestamp createdDate;
    @Column(name = "last_updated_date")
    private Timestamp lastUpdatedDate;
    @Column(name = "created_by")
    private String createdBy;

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public Timestamp getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Timestamp lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
