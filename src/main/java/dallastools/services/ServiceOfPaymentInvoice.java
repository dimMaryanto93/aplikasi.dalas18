package dallastools.services;

import dallastools.models.expenditur.PaymentInvoice;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by dimmaryanto on 18/10/15.
 */
@Repository
@Transactional(readOnly = true)
public class ServiceOfPaymentInvoice {

    private SessionFactory sessionFactory;

    @Autowired
    public ServiceOfPaymentInvoice(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session getSessionFactory() {
        return sessionFactory.getCurrentSession();
    }

    @Transactional(readOnly = false)
    public void save(PaymentInvoice invoice) throws Exception {
        getSessionFactory().save(invoice);
    }

    @Transactional(readOnly = false)
    public void update(PaymentInvoice invoice) throws Exception {
        getSessionFactory().update(invoice);
    }

    @Transactional(readOnly = false)
    public void delete(PaymentInvoice invoice) throws Exception {
        getSessionFactory().delete(invoice);
    }

    public List<PaymentInvoice> findAll() throws Exception {
        return getSessionFactory().createCriteria(PaymentInvoice.class).list();
    }
}
