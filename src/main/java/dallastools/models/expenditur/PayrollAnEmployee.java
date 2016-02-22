package dallastools.models.expenditur;

import dallastools.models.BasedTableEntity;
import dallastools.models.masterdata.Employee;
import dallastools.models.productions.ProductionOfSales;

import javax.persistence.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dimmaryanto on 23/11/15.
 */
@Entity
@Table(name = "trans_employee_payroll",
        uniqueConstraints =
                {@UniqueConstraint(
                        name = "uq_payroll_employee",
                        columnNames = {"date", "employee_id"})})
@SequenceGenerator(name = "trans_employee_payroll_sq",
        sequenceName = "sq_trans_employee_payroll",
        allocationSize = 1, initialValue = 1)
public class PayrollAnEmployee extends BasedTableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trans_employee_payroll_sq")
    private Integer id;
    @Column(name = "date", nullable = false)
    private Date date;
    @OneToOne
    @JoinColumns(@JoinColumn(name = "employee_id"))
    private Employee employee;
    @Column(name = "amount", nullable = false, scale = 2)
    private Double amount;
    @Column(name = "other_amount", nullable = false, scale = 2)
    private Double otherAmount;
    @OneToMany
    @JoinTable(name = "trans_employee_payroll_details",
            joinColumns = @JoinColumn(name = "trans_id"),
            inverseJoinColumns = @JoinColumn(name = "production_id"))
    private List<ProductionOfSales> details = new ArrayList<ProductionOfSales>();

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<ProductionOfSales> getDetails() {
        return details;
    }

    public void setDetails(List<ProductionOfSales> details) {
        this.details = details;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getOtherAmount() {
        return otherAmount;
    }

    public void setOtherAmount(Double otherAmount) {
        this.otherAmount = otherAmount;
    }
}
