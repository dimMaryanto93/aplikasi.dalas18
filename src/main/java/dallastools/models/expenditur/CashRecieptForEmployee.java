package dallastools.models.expenditur;

import dallastools.models.BasedTableEntity;
import dallastools.models.masterdata.Employee;

import javax.persistence.*;
import java.sql.Date;

/**
 * Created by dimmaryanto on 23/11/15.
 */
@Entity
@Table(name = "trans_employee_cash_reciept")
@SequenceGenerator(name = "trans_employee_cash_reciept_sq",
        allocationSize = 1, initialValue = 1,
        sequenceName = "sq_trans_employee_cash_reciept")
public class CashRecieptForEmployee extends BasedTableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "trans_employee_cash_reciept_sq")
    private Integer id;
    @Column(name = "date", nullable = false)
    private Date date;
    @OneToOne
    @JoinColumns(@JoinColumn(name = "employee_id"))
    private Employee employee;
    @Column(name = "amount", nullable = false, scale = 2)
    private Double amount;
    @Column(name = "payment", nullable = false, scale = 2)
    private Double payment;
    @Column(name = "is_paid", nullable = false)
    private Boolean paid;

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

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public Double getPayment() {
        return payment;
    }

    public void setPayment(Double payment) {
        this.payment = payment;
    }
}
