package dallastools.models.productions;

import dallastools.models.BasedTableEntity;

import javax.persistence.*;
import java.sql.Date;

/**
 * Created by dimmaryanto on 16/11/15.
 */
@Entity
@Table(name = "trans_item_used")
@SequenceGenerator(name = "trans_item_used_seq", allocationSize = 1, initialValue = 1, sequenceName = "sq_trans_item_used")
public class ItemUsed extends BasedTableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trans_item_used_seq")
    private Integer id;

    @Column(nullable = false)
    private Date date;


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
