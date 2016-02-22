package dallastools.services;

import dallastools.models.masterdata.Customer;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by dimmaryanto on 9/26/15.
 */
@Repository
@Transactional(readOnly = true)
public class ServiceOfCustomer {

    private final Logger log = LoggerFactory.getLogger(ServiceOfCustomer.class);

    private SessionFactory sessionFactory;

    @Autowired
    public ServiceOfCustomer(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Session getSessionFactory() {
        return sessionFactory.getCurrentSession();
    }

    @Transactional(readOnly = false)
    public void save(Customer aCustomer) throws Exception {
        getSessionFactory().save(aCustomer);
        aCustomer.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
    }


    @Transactional(readOnly = false)
    public void update(Customer aCustomer) throws Exception {
        getSessionFactory().update(aCustomer);
        aCustomer.setLastUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
    }

    @Transactional(readOnly = false)
    public void delete(Customer aCustomer) throws Exception {
        getSessionFactory().delete(aCustomer);
    }

    public List<Customer> findAll() throws Exception {
        return getSessionFactory().createQuery("from Customer").list();
    }

}
