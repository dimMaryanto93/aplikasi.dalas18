package dallastools.services;

import dallastools.models.expenditur.PurchaseInvoice;
import dallastools.models.expenditur.PurchaseInvoiceDetails;
import dallastools.models.masterdata.Item;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by dimmaryanto on 17/10/15.
 */
@Repository
@Transactional(readOnly = true)
public class ServiceOfPurchaseInvoice {
    private final Logger log = LoggerFactory.getLogger(ServiceOfSalesInvoice.class);
    private SessionFactory sessionFactory;

    @Autowired
    public ServiceOfPurchaseInvoice(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session getSessionFactory() {
        return sessionFactory.getCurrentSession();
    }

    public List<PurchaseInvoice> findAllPurchaseInvoice() throws Exception {
        return getSessionFactory().createCriteria(PurchaseInvoice.class).list();
    }

    @Transactional(readOnly = false)
    public void updateItemBeforeUpdateOrDelete(PurchaseInvoice invoice, Boolean plus) throws Exception {
        List<PurchaseInvoiceDetails> list = findPurchaseDetailByInvoice(invoice);
        for (PurchaseInvoiceDetails details : list) {
            Item anItem = details.getItem();
            Integer qtyNow = anItem.getQty();
            Integer qtyUpdated = details.getQty();
            Integer result = 0;
            if (plus) result = qtyNow + qtyUpdated;
            else result = qtyNow - qtyUpdated;
            anItem.setQty(result);
            getSessionFactory().update(anItem);
            log.info("Barang : " + anItem.getName() + " telah diubah stoknya dari {} ke {}", qtyNow, anItem.getQty());
        }
    }

    public List<PurchaseInvoiceDetails> findPurchaseDetailByInvoice(PurchaseInvoice invoice) throws Exception {
        return getSessionFactory().
                createCriteria(PurchaseInvoiceDetails.class).
                add(Restrictions.eq("invoice.id", invoice.getId())).
                list();
    }

    @Transactional(readOnly = false)
    public void save(PurchaseInvoice invoice, List<PurchaseInvoiceDetails> listDetailInvoice) throws Exception {
        getSessionFactory().save(invoice);
        log.info("transaksi pembelian telah disimpan pada tanggal {}", invoice.getTransDate().toString());
        for (PurchaseInvoiceDetails details : listDetailInvoice) {
            getSessionFactory().save(details);
            details.setInvoice(invoice);
            log.info("Nama Barang : {} dibeli sebanyak {}", details.getItem().getName(), details.getQty());
        }
    }

    @Transactional(readOnly = false)
    public void update(PurchaseInvoice invoice, List<PurchaseInvoiceDetails> listDetailInvoice) throws Exception {
        getSessionFactory().saveOrUpdate(invoice);
        for (PurchaseInvoiceDetails details : listDetailInvoice) {
            getSessionFactory().save(details);
            details.setInvoice(invoice);
        }
    }

    @Transactional(readOnly = false)
    public void deletePurchaseDetailsByPurchaseInvoice(PurchaseInvoice invoice) throws Exception {
        Query aQuery = getSessionFactory().createQuery("DELETE FROM PurchaseInvoiceDetails WHERE invoice.id = :invoice");
        aQuery.setInteger("invoice", invoice.getId());
        aQuery.executeUpdate();
    }

    @Transactional(readOnly = false)
    public void delete(PurchaseInvoice invoice) throws Exception {
        getSessionFactory().delete(invoice);
    }

}
