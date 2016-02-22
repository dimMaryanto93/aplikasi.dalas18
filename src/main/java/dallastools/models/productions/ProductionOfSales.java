package dallastools.models.productions;

import dallastools.models.BasedTableEntity;
import dallastools.models.masterdata.Employee;

import javax.persistence.*;
import java.sql.Date;

/**
 * Created by dimmaryanto on 18/10/15.
 */

@Entity
@Table(name = "trans_production_of_sales")
@SequenceGenerator(name = "trans_production_of_sales_sq", sequenceName = "sq_trans_sales_production",
        initialValue = 1, allocationSize = 1)
public class ProductionOfSales extends BasedTableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trans_production_of_sales_sq")
    private Integer id;

    @Column(name = "production_date", nullable = false)
    private Date date;

    @OneToOne
    @JoinColumns(@JoinColumn(name = "employee_id"))
    private Employee employee;

    @Column(nullable = false)
    private Boolean used;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Boolean getUsed() {
        return used;
    }

    public void setUsed(Boolean used) {
        this.used = used;
    }
}
