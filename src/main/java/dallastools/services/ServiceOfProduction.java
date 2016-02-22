package dallastools.services;

import dallastools.models.income.SalesOrderDetails;
import dallastools.models.masterdata.Item;
import dallastools.models.other.ItemSum;
import dallastools.models.productions.ProductionOfSales;
import dallastools.models.productions.ProductionOfSalesDetails;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by dimmaryanto on 25/10/15.
 */
@Repository
@Transactional(readOnly = true)
public class ServiceOfProduction {

    private final Logger log = LoggerFactory.getLogger(ServiceOfProduction.class);

    private SessionFactory sessionFactory;

    @Autowired
    public ServiceOfProduction(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Session getSessionFactory() {
        return sessionFactory.getCurrentSession();
    }

    public List<ProductionOfSales> findAllTransaction() throws Exception {
        return getSessionFactory().createCriteria(ProductionOfSales.class).list();
    }

    public List<ProductionOfSalesDetails> findTransactionPerProduction(ProductionOfSales production) throws Exception {
        return getSessionFactory().createCriteria(ProductionOfSalesDetails.class)
                .add(Restrictions.eq("production.id", production.getId())).list();
    }

    public List<Item> findItemForSalesProduction() throws Exception {
        Criteria aCriteria = getSessionFactory().createCriteria(Item.class);
        aCriteria.add(Restrictions.and(
                Restrictions.eq("sell", true),
                Restrictions.and(
                        Restrictions.le("priceBuy", Double.valueOf(1)),
                        Restrictions.gt("priceSell", Double.valueOf(0)))
        ));
        return aCriteria.list();
    }

    @Transactional(readOnly = false)
    public void save(ProductionOfSales master, List<ProductionOfSalesDetails> details) throws Exception {
        master.setUsed(false);
        getSessionFactory().save(master);
        log.info("Menyimpan data Produksi Barang pada {}", master.getDate());
        for (ProductionOfSalesDetails production : details) {
            getSessionFactory().save(production);

            Item anItem = production.getItem();
            Integer qtyNow = anItem.getQty();
            Integer qtyUsed = production.getItemUsed();

            anItem.setQty(qtyNow + qtyUsed);
            getSessionFactory().update(anItem);
            log.info("Merubah Stok Barang {} dari {} menjadi ({} + {}) = {}",
                    new Object[]{anItem.getId(), qtyNow, qtyNow, qtyUsed, anItem.getQty()});
            production.setProduction(master);

        }
    }

    @Transactional(readOnly = false)
    public void updateItemBeforeUpdateOrDelete(ProductionOfSales production, Boolean plus) throws Exception {
        List<ProductionOfSalesDetails> detailses = getSessionFactory()
                .createCriteria(ProductionOfSalesDetails.class)
                .add(Restrictions.eq("production.id", production.getId())).list();
        for (ProductionOfSalesDetails details : detailses) {
            Item anItem = details.getItem();
            Integer qtyUpdateable = details.getItemUsed();
            Integer qtynow = anItem.getQty();
            if (plus) anItem.setQty(qtynow + qtyUpdateable);
            else anItem.setQty(qtynow - qtyUpdateable);
            getSessionFactory().update(anItem);
        }
    }

    @Transactional(readOnly = false)
    public void update(ProductionOfSales master, List<ProductionOfSalesDetails> details) throws Exception {
        getSessionFactory().update(master);
        for (ProductionOfSalesDetails production : details) {
            getSessionFactory().save(production);
            production.setProduction(master);
        }
    }

    @Transactional(readOnly = false)
    public void deleteTransaction(ProductionOfSales production) throws Exception {
        Query aQuery = getSessionFactory()
                .createQuery("DELETE FROM ProductionOfSalesDetails WHERE production.id = :production_id");
        aQuery.setInteger("production_id", production.getId());
        aQuery.executeUpdate();
        getSessionFactory().delete(production);
    }

    public List findItemDeadline() throws Exception {
        Criteria aCriteria = getSessionFactory().createCriteria(SalesOrderDetails.class);
        aCriteria.createAlias("salesOrder", "order");

        LocalDate now = LocalDate.now();
        aCriteria.add(Restrictions.and(
                Restrictions.between("order.orderDate", Date.valueOf(now), Date.valueOf(now.plusWeeks(1))),
                Restrictions.eq("order.checklist", false)
        ));

        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.groupProperty("item"));
        projectionList.add(Projections.sum("qty"));

        aCriteria.setProjection(projectionList);

        List<ItemSum> emptyList = new ArrayList<>();
        Iterator index = aCriteria.list().iterator();
        while (index.hasNext()) {
            Object[] rows = (Object[]) index.next();
            Item anItem = (Item) rows[0];
            Long qty = (Long) rows[1];
            ItemSum items = new ItemSum(anItem, qty);
            emptyList.add(items);
        }

        return emptyList;
    }

}
