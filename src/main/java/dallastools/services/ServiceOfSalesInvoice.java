package dallastools.services;

import dallastools.models.income.Sales;
import dallastools.models.income.SalesDetails;
import dallastools.models.masterdata.Item;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;

/**
 * Created by dimmaryanto on 9/27/15.
 */
@Repository
@Transactional(readOnly = true)
public class ServiceOfSalesInvoice {

    private Logger log = LoggerFactory.getLogger(ServiceOfSalesInvoice.class);

    private SessionFactory sessionFactory;

    @Autowired
    public ServiceOfSalesInvoice(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session getSessionFactory() {
        return sessionFactory.getCurrentSession();
    }

    @Transactional(readOnly = false)
    public void updateItemBeforeUpdatedOfDelete(Sales sales, Boolean plus) throws Exception {
        List<SalesDetails> list = findSalesDetailPerSales(sales);
        for (SalesDetails detail : list) {
            Item anItem = detail.getItem();
            Integer qtyNow = anItem.getQty();
            Integer qtyUpdated = detail.getQty();
            Integer result = 0;
            if (plus)
                result = qtyNow + qtyUpdated;
            else result = qtyNow - qtyUpdated;
            anItem.setQty(result);
            getSessionFactory().update(anItem);
            log.info(anItem.getId() + " updated stok from {} to {}", qtyNow, result);
        }

    }

    @Transactional(readOnly = false)
    public Integer save(Sales aSales, List<SalesDetails> aSalesDetailses) throws Exception {
        LocalDate date = aSales.getDateTransaction().toLocalDate();
        aSales.setMonth(YearMonth.of(date.getYear(), date.getMonth()).toString());
        aSales.setYear(Year.now().getValue());
        aSales.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));

        Integer value = (Integer) getSessionFactory().save(aSales);
        aSales.setId(value);
        for (SalesDetails aSalesDetails : aSalesDetailses) {
            getSessionFactory().save(aSalesDetails);
            aSalesDetails.setSales(aSales);
        }
        return value;
    }

    @Transactional(readOnly = false)
    public void update(Sales aSales, List<SalesDetails> aSalesDetailses) throws Exception {
        getSessionFactory().save(aSales);
        aSales.setLastUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
        for (SalesDetails aSalesDetails : aSalesDetailses) {
            getSessionFactory().update(aSalesDetails);
            aSalesDetails.setSales(aSales);
        }
    }

    @Transactional(readOnly = false)
    public void delete(Sales aSales) throws Exception {
        Query aQuery = getSessionFactory().createQuery("delete from SalesDetails where sales.id = :kode_sales");
        aQuery.setInteger("kode_sales", aSales.getId());
        aQuery.executeUpdate();
        getSessionFactory().delete(aSales);
    }

    public List<Sales> findAllSales() throws Exception {
        return getSessionFactory().createCriteria(Sales.class).list();
    }

    public List<SalesDetails> findSalesDetailPerSales(Sales aSales) throws Exception {
        Criteria aCriteria = getSessionFactory().createCriteria(SalesDetails.class);
        aCriteria.add(Restrictions.eq("sales.id", aSales.getId()));
        return aCriteria.list();
    }

    public List<Sales> findAllSalesForDelivery(Boolean sent) throws Exception {
        Criteria aCriteria = getSessionFactory().createCriteria(Sales.class);
        aCriteria.add(Restrictions.eq("sent", sent));
        return aCriteria.list();
    }

    @Transactional(readOnly = false)
    public void updateSales(Sales sales) throws Exception {
        getSessionFactory().update(sales);
    }


}
