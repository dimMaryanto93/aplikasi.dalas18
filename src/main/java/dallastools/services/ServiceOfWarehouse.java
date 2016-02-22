package dallastools.services;

import dallastools.models.masterdata.Warehouse;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by dimmaryanto on 20/10/15.
 */
@Repository
@Transactional(readOnly = true)
public class ServiceOfWarehouse {

    private SessionFactory sessionFactory;

    @Autowired
    public ServiceOfWarehouse(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Session getSessionFactory() {
        return sessionFactory.getCurrentSession();
    }

    public List<Warehouse> findAll() throws Exception {
        return getSessionFactory().createCriteria(Warehouse.class).list();
    }

    @Transactional(readOnly = false)
    public void save(Warehouse aWarehouse) throws Exception {
        getSessionFactory().save(aWarehouse);
    }

    @Transactional(readOnly = false)
    public void update(Warehouse aWarehouse) throws Exception {
        getSessionFactory().update(aWarehouse);
    }

    @Transactional(readOnly = false)
    public void delete(Warehouse aWarehouse) throws Exception {
        getSessionFactory().delete(aWarehouse);
    }
}
