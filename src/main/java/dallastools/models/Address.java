package dallastools.models;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;

/**
 * Created by dimmaryanto on 9/24/15.
 */
@Embeddable
public class Address {

    @Column(name = "street_address", nullable = false)
    @Lob
    private String streetAddress;
    @Column(nullable = false, name = "city", length = 100)
    private String city;
    @Column(name = "pin_code", length = 10)
    private Integer pinCode;
    @Column(length = 3, nullable = false)
    private Integer rt;
    @Column(length = 3, nullable = false)
    private Integer rw;
    @Column(name = "district", nullable = false, length = 100)
    private String district;

    public Address() {
    }

    public Address(String streetAddress, String city, Integer pinCode, Integer rt, Integer rw, String district) {
        this.streetAddress = streetAddress;
        this.city = city;
        this.pinCode = pinCode;
        this.rt = rt;
        this.rw = rw;
        this.district = district;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getPinCode() {
        return pinCode;
    }

    public void setPinCode(Integer pinCode) {
        this.pinCode = pinCode;
    }

    public Integer getRt() {
        return rt;
    }

    public void setRt(Integer rt) {
        this.rt = rt;
    }

    public Integer getRw() {
        return rw;
    }

    public void setRw(Integer rw) {
        this.rw = rw;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }
}
