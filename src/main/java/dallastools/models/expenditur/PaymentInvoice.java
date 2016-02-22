package dallastools.models.expenditur;

import dallastools.models.BasedTableEntity;
import dallastools.models.masterdata.CategoryOfPayment;

import javax.persistence.*;
import java.sql.Date;

/**
 * Created by dimmaryanto on 9/25/15.
 */
@Entity
@Table(name = "trans_payment_invoice")
@SequenceGenerator(name = "trans_payment_invoice_sq", sequenceName = "sq_trans_payment_invoice",
        initialValue = 1, allocationSize = 1)
public class PaymentInvoice extends BasedTableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trans_payment_invoice_sq")
    private Integer id;
    @Column(name = "trans_date", nullable = false)
    private Date transDate;
    @OneToOne
    @JoinColumns(@JoinColumn(name = "category_id"))
    private CategoryOfPayment category;
    @Column(name = "ammount", nullable = false)
    private Double ammount;
    @Lob
    private String description;

    public PaymentInvoice() {
    }

    public PaymentInvoice(Double ammount, CategoryOfPayment category, String description, Date transDate) {
        this.ammount = ammount;
        this.category = category;
        this.description = description;
        this.transDate = transDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getTransDate() {
        return transDate;
    }

    public void setTransDate(Date transDate) {
        this.transDate = transDate;
    }

    public CategoryOfPayment getCategory() {
        return category;
    }

    public void setCategory(CategoryOfPayment category) {
        this.category = category;
    }

    public Double getAmmount() {
        return ammount;
    }

    public void setAmmount(Double ammount) {
        this.ammount = ammount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
