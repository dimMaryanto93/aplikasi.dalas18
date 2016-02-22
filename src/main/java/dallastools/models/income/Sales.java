package dallastools.models.income;

import dallastools.models.BasedTableEntity;
import dallastools.models.masterdata.Customer;

import javax.persistence.*;
import java.sql.Date;

/**
 * Created by dimmaryanto on 9/24/15.
 */
@Entity
@Table(name = "trans_sales")
@SequenceGenerator(name = "trans_sales_sq", sequenceName = "sq_trans_sales",
        initialValue = 1, allocationSize = 1)
public class Sales extends BasedTableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trans_sales_sq")
    @Column(name = "id", nullable = false, unique = true)
    private Integer id;
    /*SALES_INV-CUST_ID-TRANS_DATE-COUNT --> ex SALES_INV-10-20151103-1*/
    @Column(name = "trans_id", nullable = false, unique = true)
    private String transId;
    @OneToOne
    @JoinColumns(@JoinColumn(name = "customer_id"))
    private Customer customer;
    @Column(name = "transaction_date", nullable = false)
    private Date dateTransaction;
    @Column(name = "transaction_month", nullable = false)
    private String month;
    @Column(name = "transaction_year", nullable = false)
    private Integer year;
    @Lob
    @Column(name = "ship_to", nullable = false)
    private String shipTo;
    @Column(name = "ammount", nullable = false)
    private Double ammount;
    @Column(name = "grant_total", nullable = false)
    private Double grantTotal;
    @Column(name = "is_paid", nullable = false)
    private Boolean paid;
    @Column(name = "is_sent", nullable = false)
    private Boolean sent;
    @Column(nullable = false, name = "is_recieved")
    private Boolean recieved;

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public Boolean getRecieved() {
        return recieved;
    }

    public void setRecieved(Boolean recieved) {
        this.recieved = recieved;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public Boolean getSent() {
        return sent;
    }

    public void setSent(Boolean sent) {
        this.sent = sent;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Date getDateTransaction() {
        return dateTransaction;
    }

    public void setDateTransaction(Date dateTransaction) {
        this.dateTransaction = dateTransaction;
    }

    public Double getGrantTotal() {
        return grantTotal;
    }

    public void setGrantTotal(Double grantTotal) {
        this.grantTotal = grantTotal;
    }

    public Double getAmmount() {
        return ammount;
    }

    public void setAmmount(Double ammount) {
        this.ammount = ammount;
    }

    public String getShipTo() {
        return shipTo;
    }

    public void setShipTo(String shipTo) {
        this.shipTo = shipTo;
    }
}
