package dallastools.models.masterdata;

import dallastools.models.Address;
import dallastools.models.BasedTableEntity;

import javax.persistence.*;

/**
 * Created by dimmaryanto on 9/25/15.
 */
@Entity
@Table(name = "mst_suplayers")
@SequenceGenerator(name = "mst_suplayer_sq", sequenceName = "sq_mst_suplayer",
        initialValue = 1, allocationSize = 1)
public class Suplayer extends BasedTableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mst_suplayer_sq")
    @Column(name = "suplayer_id", nullable = false, unique = true)
    private Integer suplayerId;
    @Column(name = "suplayer_name", nullable = false)
    private String name;
    @Column(length = 25, name = "contact_person")
    private String phone;
    @Embedded
    private Address address;

    public Suplayer() {
    }

    public Suplayer(String name, String phone, Address address) {
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    public Integer getSuplayerId() {
        return suplayerId;
    }

    public void setSuplayerId(Integer suplayerId) {
        this.suplayerId = suplayerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
