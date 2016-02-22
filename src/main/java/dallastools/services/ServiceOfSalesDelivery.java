package dallastools.services;

import dallastools.models.expenditur.DeliveryOfSales;
import dallastools.models.expenditur.DeliveryOfSalesDetails;
import dallastools.models.income.Sales;
import dallastools.models.income.SalesDetails;
import dallastools.models.masterdata.Item;
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
 * Created by dimmaryanto on 9/27/15.
 */
@Repository
@Transactional(readOnly = true)
public class ServiceOfSalesDelivery {

    private SessionFactory sessionFactory;

    private Logger log = LoggerFactory.getLogger(ServiceOfSalesDelivery.class);

    @Autowired
    public ServiceOfSalesDelivery(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Session getSessionFactory() {
        return sessionFactory.getCurrentSession();
    }

    @Transactional(readOnly = false)
    public Integer save(DeliveryOfSales aDelivery, List<DeliveryOfSalesDetails> aDeliveryOfSalesDetailses) throws Exception {
        Integer value = (Integer) getSessionFactory().save(aDelivery);
        aDelivery.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        for (DeliveryOfSalesDetails aDetails : aDeliveryOfSalesDetailses) {
            getSessionFactory().save(aDetails);
            log.info("Data Delivery detail disimpan");
            aDetails.setDeliveryOfSales(aDelivery);

            Sales sales = aDetails.getSales();
            sales.setSent(true);
            getSessionFactory().update(sales);
        }
        return value;
    }

    @Transactional(readOnly = false)
    public void update(DeliveryOfSales aDelivery, List<DeliveryOfSalesDetails> aDeliveryOfSalesDetailses) throws Exception {
        getSessionFactory().update(aDelivery);
        for (DeliveryOfSalesDetails aDetails : aDeliveryOfSalesDetailses) {
            Sales aSales = aDetails.getSales();
            aSales.setRecieved(true);
            getSessionFactory().update(aSales);

            aDetails.setDeliveryOfSales(aDelivery);
            getSessionFactory().update(aDetails);
        }
    }

    @Transactional(readOnly = false)
    public void delete(DeliveryOfSales aDelivery) throws Exception {
        log.info("Data Details Pengiriman Barang dihapus semua!");
        getSessionFactory()
                .createQuery("DELETE FROM DeliveryOfSalesDetails WHERE deliveryOfSales.id = :sales")
                .setInteger("sales", aDelivery.getId())
                .executeUpdate();
    }

    @Transactional(readOnly = false)
    public void deleteDeliveryOfSales(DeliveryOfSales sales) {
        //log.info("Data Master Pengiriman Barang dihapus");
        getSessionFactory().delete(sales);
    }

    public List<DeliveryOfSales> findAllDelivery() throws Exception {
        return getSessionFactory().createQuery("from DeliveryOfSales").list();
    }

    public List<DeliveryOfSalesDetails> findAllDeliverySalesDetails() {
        return getSessionFactory().createQuery("from DeliveryOfSalesDetails").list();
    }

    @Transactional(readOnly = false)
    public void updateSalesFromDelivery(DeliveryOfSales deliveryOfSales) throws Exception {
        List<DeliveryOfSalesDetails> list = findDeliveryDetailForDelivery(deliveryOfSales);
        for (DeliveryOfSalesDetails aDetails : list) {
            Sales aSales = aDetails.getSales();

            aSales.setRecieved(false);
            aSales.setSent(false);
            getSessionFactory().update(aSales);

            log.info("Transaksi penjualan {} ubah kirim {} terima {} ",
                    new Object[]{aSales.getTransId(), aSales.getSent(), aSales.getRecieved()});
            Criteria aCriteria = getSessionFactory().createCriteria(SalesDetails.class);
            aCriteria.add(Restrictions.eq("sales.id", aSales.getId()));
            List<SalesDetails> detailses = aCriteria.list();

            log.info("Barang ditemukan {} data pada transaksi penjualan {}", list.size(), aSales.getTransId());
            for (SalesDetails details : detailses) {
                Item anItem = details.getItem();
                Integer qtyNow = anItem.getQty();
                Integer qtySell = details.getQty();
                anItem.setQty(qtyNow + qtySell);
                log.info("Barang {} bertambah dari {} menjadi {}", new Object[]{anItem.getName(), qtyNow, anItem.getQty()});
                getSessionFactory().update(anItem);
            }

        }
    }

    @Transactional(readOnly = false)
    public void updateSalesRecivedFromDelivery(DeliveryOfSalesDetails details) {
        getSessionFactory().update(details);
    }

    public List<DeliveryOfSalesDetails> findDeliveryDetailForDelivery(DeliveryOfSales aDelivery) throws Exception {
        return getSessionFactory().createCriteria(DeliveryOfSalesDetails.class)
                .add(Restrictions.eq("deliveryOfSales.id", aDelivery.getId()))
                .list();
    }

    @Transactional(readOnly = false)
    public void update(DeliveryOfSales delivery) {
        getSessionFactory().update(delivery);
    }

    public List<Sales> findSalesReadyForDelivery() throws Exception {
        Criteria aCriteria = getSessionFactory().createCriteria(Sales.class);
        aCriteria.add(Restrictions.or(Restrictions.eq("sent", false), Restrictions.eq("recieved", false)));
        return aCriteria.list();
    }
}
