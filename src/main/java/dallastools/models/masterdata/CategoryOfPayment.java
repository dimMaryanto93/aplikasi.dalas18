package dallastools.models.masterdata;

import dallastools.models.BasedTableEntity;

import javax.persistence.*;

/**
 * Created by dimmaryanto on 9/25/15.
 */
@Entity
@Table(name = "mst_payment_category")
public class CategoryOfPayment extends BasedTableEntity {

    @Id
    @Column(name = "payment_id", nullable = false, unique = true, length = 25)
    private String id;
    @Column(name = "payment_for", nullable = false)
    private String paymentFor;
    @Lob
    private String description;
    public CategoryOfPayment() {
    }
    public CategoryOfPayment(String description, String paymentFor, String id) {
        this.description = description;
        this.paymentFor = paymentFor;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String paymentId) {
        this.id = paymentId;
    }

    public String getPaymentFor() {
        return paymentFor;
    }

    public void setPaymentFor(String paymentFor) {
        this.paymentFor = paymentFor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
