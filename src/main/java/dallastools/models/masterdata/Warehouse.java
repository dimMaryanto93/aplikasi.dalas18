package dallastools.models.masterdata;

import dallastools.models.Address;
import dallastools.models.BasedTableEntity;

import javax.persistence.*;

/**
 * Created by dimmaryanto on 18/10/15.
 */
@Entity
@Table(name = "mst_warehouse")
@SequenceGenerator(name = "mst_warehouse_sq", sequenceName = "sq_mst_warehouse",
        initialValue = 1, allocationSize = 1)
public class Warehouse extends BasedTableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mst_warehouse_sq")
    private Integer id;

    @Column(length = 100, nullable = false, unique = true)
    private String name;

    @Column(name = "phone_number", nullable = false, unique = true, length = 25)
    private String phoneNumber;

    @Embedded
    private Address address;

    public Warehouse() {
    }

    public Warehouse(Address address, String name, String phoneNumber) {
        this.address = address;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
