package dallastools.models.expenditur;

import dallastools.models.BasedTableEntity;
import dallastools.models.masterdata.Employee;

import javax.persistence.*;
import java.sql.Date;

/**
 * Created by dimmaryanto on 9/24/15.
 */
@Entity
@Table(name = "trans_sales_delivery")
@SequenceGenerator(name = "trans_delivery_sales_sq", sequenceName = "sq_trans_sales_delivery",
        allocationSize = 1, initialValue = 1)
public class DeliveryOfSales extends BasedTableEntity {

    @Id
    @GeneratedValue(generator = "trans_delivery_sales_sq", strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, unique = true)
    private Integer id;
    @Column(name = "delivery_id", nullable = false, unique = true)
    private String deliveryId;
    @OneToOne
    @JoinColumns(@JoinColumn(name = "employee_id"))
    private Employee employee;
    @Column(name = "date_sent", nullable = false)
    private Date dateSent;
    @Column(name = "in_process", nullable = false)
    private Boolean status;
    @Column(name = "grant_total", nullable = false)
    private Double grantTotal;


    public String getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(String deliveryId) {
        this.deliveryId = deliveryId;
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

    public Date getDateSent() {
        return dateSent;
    }

    public void setDateSent(Date dateSent) {
        this.dateSent = dateSent;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Double getGrantTotal() {
        return grantTotal;
    }

    public void setGrantTotal(Double grantTotal) {
        this.grantTotal = grantTotal;
    }
}
