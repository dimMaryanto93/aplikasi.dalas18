package dallastools.models.masterdata;

import dallastools.models.BasedTableEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * Created by dimmaryanto on 9/24/15.
 */
@Entity
@Table(name = "mst_accounts")
public class Account extends BasedTableEntity {

    @Id
    @Column(name = "unique_name", nullable = false, length = 25, unique = true)
    private String username;
    @Column(name = "name", nullable = false)
    private String fullname;
    @Column(name = "identified_passwd", nullable = false, length = 150)
    private String passwd;
    @Column(name = "security_accessed", nullable = false, length = 15)
    private String level;
    @Column(name = "is_active", nullable = false)
    private Boolean active;
    @Column(name = "last_login")
    private Timestamp lastLogin;

    public Account() {
    }

    public Account(String username, String fullname, String passwd, String level, Boolean active, Timestamp lastLogin) {
        this.username = username;
        this.fullname = fullname;
        this.passwd = passwd;
        this.level = level;
        this.active = active;
        this.lastLogin = lastLogin;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
