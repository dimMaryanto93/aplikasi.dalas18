package dallastools.services;

import dallastools.models.masterdata.CategoryOfPayment;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
public class ServiceOfPaymentCategory {

    private SessionFactory sessionFactory;

    @Autowired
    public ServiceOfPaymentCategory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Session getSessionFactory() {
        return sessionFactory.getCurrentSession();
    }


    @Transactional(readOnly = false)
    public void save(CategoryOfPayment aPayment) throws Exception {
        aPayment.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        getSessionFactory().save(aPayment);
    }

    @Transactional(readOnly = false)
    public void update(CategoryOfPayment aPayment) throws Exception {
        aPayment.setLastUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
        getSessionFactory().update(aPayment);
    }

    @Transactional(readOnly = false)
    public void delete(CategoryOfPayment aPayment) throws Exception {
        getSessionFactory().delete(aPayment);
    }

    public List<CategoryOfPayment> findAll() throws Exception {
        return getSessionFactory().createCriteria(CategoryOfPayment.class).list();
    }
}
