package dallastools.services;

import dallastools.models.masterdata.Unit;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by dimmaryanto on 9/27/15.
 */
@Repository
@Transactional(readOnly = true)
public class ServiceOfUnit {

    private SessionFactory sessionFactory;

    @Autowired
    public ServiceOfUnit(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Session getSessionFactory() {
        return sessionFactory.getCurrentSession();
    }

    @Transactional(readOnly = false)
    public void save(Unit anUnit) throws Exception {
        getSessionFactory().save(anUnit);
    }

    @Transactional(readOnly = false)
    public void udpate(Unit anUnit) throws Exception {
        getSessionFactory().update(anUnit);
    }

    @Transactional(readOnly = false)
    public void delete(Unit anUnit) throws Exception {
        getSessionFactory().delete(anUnit);
    }

    public List<Unit> findAll() throws Exception {
        return getSessionFactory().createCriteria(Unit.class).list();
    }
}
