package dallastools.models.masterdata;

import dallastools.models.Address;
import dallastools.models.BasedTableEntity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Created by dimmaryanto on 9/24/15.
 */
@Entity
@Table(name = "mst_customers")
@SequenceGenerator(name = "mst_customer_sq", sequenceName = "sq_mst_customer",
        initialValue = 1, allocationSize = 1)
public class Customer extends BasedTableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mst_customer_sq")
    private Integer id;
    @Column(name = "customer_name", length = 100, nullable = false)
    private String customerName;
    @Column(name = "phone_number", length = 50, nullable = false)
    private String phone;
    @Embedded
    private Address address;

    public Customer() {
    }

    public Customer(String customerName, String phone, Address address) {
        this.customerName = customerName;
        this.phone = phone;
        this.address = address;
        this.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        this.setLastUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
