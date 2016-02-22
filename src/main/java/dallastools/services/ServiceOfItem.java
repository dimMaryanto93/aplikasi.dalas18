package dallastools.services;

import dallastools.models.masterdata.Item;
import org.hibernate.Criteria;
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
@Repository()
@Transactional(readOnly = true)
public class ServiceOfItem {

    private SessionFactory sessionFactory;

    @Autowired
    public ServiceOfItem(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Session getSessionFactory() {
        return sessionFactory.getCurrentSession();
    }

    @Transactional(readOnly = false)
    public void save(Item anItem) throws Exception {
        getSessionFactory().save(anItem);
    }

    @Transactional(readOnly = false)
    public void update(Item anItem) throws Exception {
        getSessionFactory().update(anItem);
    }

    @Transactional(readOnly = false)
    public void delete(Item anItem) throws Exception {
        getSessionFactory().delete(anItem);
    }

    public List<Item> findAll() throws Exception {
        return getSessionFactory().createQuery("from Item").list();
    }

    public List<Item> findByItemIsSell(Boolean isSell) throws Exception {
        Criteria aCriteria = getSessionFactory().createCriteria(Item.class);
        aCriteria.add(Restrictions.eq("sell", isSell));
        return aCriteria.list();
    }

    public List<Item> findByItemForPurchase() throws Exception {
        Criteria aCriteria = getSessionFactory().createCriteria(Item.class);
        aCriteria.add(Restrictions.gt("priceBuy", 0.0));
        return aCriteria.list();
    }
}

