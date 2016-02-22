package dallastools.models.masterdata;

import dallastools.models.BasedTableEntity;

import javax.persistence.*;

/**
 * Created by dimmaryanto on 9/24/15.
 */
@Entity
@Table(name = "mst_departments")
public class Department extends BasedTableEntity {

    @Id
    @Column(name = "job_id", length = 10, nullable = false, unique = true)
    private String id;
    @Column(name = "job_name", nullable = false, length = 100)
    private String name;
    @Lob
    @Column(name = "job_description")
    private String description;

    public Department() {
    }

    public Department(String description, String id, String name) {
        this.description = description;
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String jobId) {
        this.id = jobId;
    }

    public String getName() {
        return name;
    }

    public void setName(String jobName) {
        this.name = jobName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String jobDescription) {
        this.description = jobDescription;
    }


}
