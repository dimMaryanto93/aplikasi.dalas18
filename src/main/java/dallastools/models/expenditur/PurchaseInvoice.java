package dallastools.models.expenditur;

import dallastools.models.BasedTableEntity;
import dallastools.models.masterdata.Suplayer;

import javax.persistence.*;
import java.sql.Date;

/**
 * Created by dimmaryanto on 9/25/15.
 */
@Entity
@Table(name = "trans_purchase_invoice")
@SequenceGenerator(name = "trans_purchase_sq", sequenceName = "sq_trans_purchase",
        initialValue = 1, allocationSize = 1)
public class PurchaseInvoice extends BasedTableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trans_purchase_sq")
    private Integer id;
    @ManyToOne
    @JoinColumns(@JoinColumn(name = "suplayer_id"))
    private Suplayer suplayer;
    @Column(name = "trans_date", nullable = false)
    private Date transDate;
    @Column(name = "ammount", nullable = false)
    private Double ammount;
    @Column(name = "grant_total", nullable = false)
    private Double grantTotal;
    @Lob
    private String description;

    public PurchaseInvoice() {
    }

    public PurchaseInvoice(Double ammount, String description, Double grantTotal, Suplayer suplayer, Date transDate) {
        this.ammount = ammount;
        this.description = description;
        this.grantTotal = grantTotal;
        this.suplayer = suplayer;
        this.transDate = transDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Suplayer getSuplayer() {
        return suplayer;
    }

    public void setSuplayer(Suplayer suplayer) {
        this.suplayer = suplayer;
    }

    public Date getTransDate() {
        return transDate;
    }

    public void setTransDate(Date transDate) {
        this.transDate = transDate;
    }

    public Double getAmmount() {
        return ammount;
    }

    public void setAmmount(Double ammount) {
        this.ammount = ammount;
    }

    public Double getGrantTotal() {
        return grantTotal;
    }

    public void setGrantTotal(Double grantTotal) {
        this.grantTotal = grantTotal;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
