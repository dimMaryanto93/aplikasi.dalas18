package dallastools.models.productions;

import dallastools.models.masterdata.Item;

import javax.persistence.*;

/**
 * Created by dimmaryanto on 16/11/15.
 */
@Entity
@Table(name = "trans_item_used_details")
@SequenceGenerator(name = "trans_item_used_details_seq", sequenceName = "sq_trans_item_used_details",
        initialValue = 1, allocationSize = 1)
public class ItemUsedDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trans_item_used_details_seq")
    private Integer id;

    @ManyToOne
    @JoinColumns(@JoinColumn(name = "item_id"))
    private Item item;

    @Column(nullable = false)
    private Integer qty;

    @ManyToOne
    @JoinColumns(@JoinColumn(name = "item_used"))
    private ItemUsed itemUsed;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public ItemUsed getItemUsed() {
        return itemUsed;
    }

    public void setItemUsed(ItemUsed itemUsed) {
        this.itemUsed = itemUsed;
    }
}
