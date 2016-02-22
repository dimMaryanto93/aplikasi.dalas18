package dallastools.models.masterdata;

import dallastools.models.Address;
import dallastools.models.BasedTableEntity;

import javax.persistence.*;

/**
 * Created by dimmaryanto on 9/24/15.
 */
@Entity
@Table(name = "mst_employees")
@SequenceGenerator(name = "mst_employees_sq", sequenceName = "sq_mst_employees",
        initialValue = 1, allocationSize = 1)
public class Employee extends BasedTableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mst_employees_sq")
    private Integer id;
    @Column(name = "employee_name", length = 100, nullable = false)
    private String employeeName;
    @Embedded
    private Address address;
    @OneToOne
    @JoinColumns(@JoinColumn(name = "job_id"))
    private Department jobdesc;
    @Column(name = "is_working", nullable = false)
    private Boolean work;

    public Employee() {
    }

    public Employee(String employeeName, Address address, Department jobdesc, Double salary) {
        this.employeeName = employeeName;
        this.address = address;
        this.jobdesc = jobdesc;
    }

    public Boolean getWork() {
        return work;
    }

    public void setWork(Boolean work) {
        this.work = work;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Department getJobdesc() {
        return jobdesc;
    }

    public void setJobdesc(Department jobdesc) {
        this.jobdesc = jobdesc;
    }


}
