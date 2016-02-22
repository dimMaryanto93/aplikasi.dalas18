package dallastools.services;

import dallastools.models.masterdata.Item;
import dallastools.models.productions.ItemUsed;
import dallastools.models.productions.ItemUsedDetails;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by dimmaryanto on 16/11/15.
 */
@Repository
@Transactional(readOnly = true)
public class ServiceOfItemUsed {

    private final Logger log = LoggerFactory.getLogger(ServiceOfItemUsed.class);

    private SessionFactory sessionFactory;

    @Autowired
    public ServiceOfItemUsed(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session getSessionFactory() {
        return sessionFactory.getCurrentSession();
    }

    public List<ItemUsed> findAllTransaction() throws Exception {
        return getSessionFactory().createCriteria(ItemUsed.class).list();
    }

    public List<Item> findItemForUsed() throws Exception {
        Criteria aCriteria = getSessionFactory().createCriteria(Item.class);
        aCriteria.add(Restrictions.and(
                Restrictions.eq("sell", false),
                Restrictions.and(Restrictions.gt("priceBuy", 0.0),
                        Restrictions.le("priceSell", 0.0))
        ));
        return aCriteria.list();
    }

    public List<ItemUsedDetails> findItemPerTransaction(ItemUsed value) throws Exception {
        Criteria aCriteria = getSessionFactory().createCriteria(ItemUsedDetails.class);
        aCriteria.add(Restrictions.eq("itemUsed.id", value.getId()));
        return aCriteria.list();
    }

    @Transactional(readOnly = false)
    public void save(ItemUsed item, List<ItemUsedDetails> details) throws Exception {
        item.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        getSessionFactory().save(item);
        log.info("Transaksi pada tanggal {} menggunakan barang sebanyak {} data",
                item.getDate(), details.size());
        for (ItemUsedDetails detail : details) {
            getSessionFactory().save(detail);
            detail.setItemUsed(item);

            Item anItem = detail.getItem();
            Integer qtyNow = anItem.getQty();
            Integer qtyUsed = detail.getQty();
            anItem.setQty(qtyNow - qtyUsed);
            getSessionFactory().update(anItem);
            log.info("Bahan {} berkurang dari {} menjadi ({} - {}) = {}",
                    new Object[]{anItem.getName(), qtyNow, qtyNow, qtyUsed, anItem.getQty()});

        }
    }

    @Transactional(readOnly = false)
    public void delete(ItemUsed value) throws Exception {
        List<ItemUsedDetails> list = findItemPerTransaction(value);
        for (ItemUsedDetails item : list) {
            Item anItem = item.getItem();
            Integer qtyUsed = item.getQty();
            Integer qtyNow = anItem.getQty();
            anItem.setQty(qtyNow + qtyUsed);
            log.info("Barang {} bertambah dari ({} + {}) = {}",
                    new Object[]{anItem.getName(), qtyNow, qtyUsed, anItem.getQty()});
            getSessionFactory().update(anItem);
            getSessionFactory().delete(item);
        }
    }

    @Transactional(readOnly = false)
    public void deleteMaster(ItemUsed value) throws Exception {
        getSessionFactory().delete(value);
    }
}
