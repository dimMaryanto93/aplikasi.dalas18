package dallastools.services;

import dallastools.models.income.SalesOrder;
import dallastools.models.income.SalesOrderDetails;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by dimmaryanto on 9/27/15.
 */
@Repository
@Transactional(readOnly = true)
public class ServiceOfSalesOrder {

    private SessionFactory sessionFactory;

    @Autowired
    public ServiceOfSalesOrder(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Session getSessionFactory() {
        return sessionFactory.getCurrentSession();
    }

    @Transactional(readOnly = false)
    public Integer save(SalesOrder anOrder, List<SalesOrderDetails> anOrderDetailses) throws Exception {
        Integer value = (Integer) getSessionFactory().save(anOrder);
        for (SalesOrderDetails anOrderDetails : anOrderDetailses) {
            getSessionFactory().save(anOrderDetails);
            anOrderDetails.setSalesOrder(anOrder);
        }
        return value;
    }

    @Transactional(readOnly = false)
    public void update(SalesOrder anOrder, List<SalesOrderDetails> anOrderDetailses) throws Exception {
        getSessionFactory().update(anOrder);
        Query aQuery = getSessionFactory().createQuery("DELETE FROM SalesOrderDetails WHERE salesOrder.id = :sales_order_id");
        aQuery.setInteger("sales_order_id", anOrder.getId());
        aQuery.executeUpdate();
        for (SalesOrderDetails anOrderDetails : anOrderDetailses) {
            getSessionFactory().save(anOrderDetails);
            anOrderDetails.setSalesOrder(anOrder);
        }
    }

    @Transactional(readOnly = false)
    public void update(SalesOrder anOrder) {
        getSessionFactory().update(anOrder);
    }

    @Transactional(readOnly = false)
    public void delete(SalesOrder anOrder) throws Exception {
        Query aQuery = getSessionFactory().createQuery("delete from SalesOrderDetails where salesOrder.id = :kode_order");
        aQuery.setInteger("kode_order", anOrder.getId());
        aQuery.executeUpdate();
        getSessionFactory().delete(anOrder);
    }

    public List<SalesOrder> findAllSelesOrder() throws Exception {
        Criteria aCriteria = getSessionFactory().createCriteria(SalesOrder.class);
        aCriteria.add(Restrictions.eq("checklist", false));
        return aCriteria.list();
    }

    public List<SalesOrderDetails> findAllSalesOrderDetails(SalesOrder order) throws Exception {
        Criteria aCriteria = getSessionFactory().createCriteria(SalesOrderDetails.class);
        aCriteria.add(Restrictions.eq("salesOrder.id", order.getId()));
        return aCriteria.list();
    }

    @Transactional(readOnly = false)
    public void updateSetTransaction(Boolean active, SalesOrder anOrder) throws Exception {
        anOrder.setChecklist(active);
        getSessionFactory().update(anOrder);
    }
}
