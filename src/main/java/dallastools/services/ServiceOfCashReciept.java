package dallastools.services;

import dallastools.models.expenditur.CashRecieptForEmployee;
import dallastools.models.masterdata.Employee;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by dimmaryanto on 24/11/15.
 */
@Repository
@Transactional(readOnly = true)
public class ServiceOfCashReciept {

    private SessionFactory sessionFactory;

    private Session getSessionFactory() {
        return sessionFactory.getCurrentSession();
    }

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional(readOnly = false)
    public void save(CashRecieptForEmployee cash) throws Exception {
        cash.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        getSessionFactory().save(cash);
    }

    @Transactional(readOnly = false)
    public void delete(CashRecieptForEmployee cash) throws Exception {
        getSessionFactory().delete(cash);
    }

    @Transactional(readOnly = false)
    public void update(CashRecieptForEmployee cash) throws Exception {
        cash.setLastUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
        getSessionFactory().update(cash);
    }

    public List<CashRecieptForEmployee> findCashRecieptByEmployee(Employee anEmployee) throws Exception {
        Criteria aCriteria = getSessionFactory().createCriteria(CashRecieptForEmployee.class);
        aCriteria.add(Restrictions.and(
                Restrictions.eq("employee.id", anEmployee.getId()),
                Restrictions.eq("paid", false)
        ));
        return aCriteria.list();
    }
}
