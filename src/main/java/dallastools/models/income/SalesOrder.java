package dallastools.models.income;

import dallastools.models.BasedTableEntity;
import dallastools.models.masterdata.Customer;

import javax.persistence.*;
import java.sql.Date;

/**
 * Created by dimmaryanto on 9/24/15.
 */
@Entity
@Table(name = "trans_sales_order")
@SequenceGenerator(name = "trans_sales_order_sq", sequenceName = "sq_trans_sales_order",
        initialValue = 1, allocationSize = 1)
public class SalesOrder extends BasedTableEntity {

    @Id
    @GeneratedValue(generator = "trans_sales_order_sq", strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, unique = true)
    private Integer id;
    @Column(name = "trans_id", nullable = false, unique = true)
    private String transId;
    @Column(name = "order_date", nullable = false)
    private Date orderDate;
    @Column(name = "todo_checklist", nullable = false)
    private Boolean checklist;
    @OneToOne
    @JoinColumns(@JoinColumn(name = "customer_id"))
    private Customer customer;
    @Column(name = "down_payment", scale = 2, nullable = false)
    private Double downPayment;


    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public Double getDownPayment() {
        return downPayment;
    }

    public void setDownPayment(Double downPayment) {
        this.downPayment = downPayment;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Boolean getChecklist() {
        return checklist;
    }

    public void setChecklist(Boolean checklist) {
        this.checklist = checklist;
    }
}
