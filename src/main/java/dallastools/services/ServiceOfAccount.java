package dallastools.services;

import dallastools.models.masterdata.Account;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by dimmaryanto on 9/24/15.
 */
@Repository
@Transactional(readOnly = true)
public class ServiceOfAccount {

    private SessionFactory sessionFactory;

    @Autowired
    public ServiceOfAccount(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Session getSessionFactory() {
        return sessionFactory.getCurrentSession();
    }

    @Transactional(readOnly = false)
    public void save(Account anAccount) throws Exception {
        getSessionFactory().save(anAccount);
        anAccount.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
    }

    public Account findByUsername(String username) throws Exception {
        return getSessionFactory().get(Account.class, username);
    }

    @Transactional(readOnly = false)
    public void update(Account anAccount) throws Exception {
        getSessionFactory().update(anAccount);
        anAccount.setLastUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
    }

    @Transactional(readOnly = false)
    public void delete(Account anAccount) throws Exception {
        getSessionFactory().delete(anAccount);
    }

    public List<Account> findAll() throws Exception {
        return getSessionFactory().createCriteria(Account.class).addOrder(Order.asc("username")).list();
    }

    public Account findByUsernameAndPassword(String username, String password) throws Exception {
        Criteria aCriteria = getSessionFactory().createCriteria(Account.class)
                .add(Restrictions.and(Restrictions.eq("username", username), Restrictions.eq("passwd", password)));
        return (Account) aCriteria.uniqueResult();
    }

}
