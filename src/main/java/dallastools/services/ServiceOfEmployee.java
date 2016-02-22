package dallastools.services;

import dallastools.models.masterdata.Employee;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by dimmaryanto on 9/26/15.
 */
@Repository
@Transactional(readOnly = true)
public class ServiceOfEmployee {

    private SessionFactory sessionFactory;

    @Autowired
    public ServiceOfEmployee(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Session getSessionFactory() {
        return sessionFactory.getCurrentSession();
    }

    @Transactional(readOnly = false)
    public void save(Employee anEmployee) throws Exception {
        anEmployee.setWork(false);
        getSessionFactory().save(anEmployee);
    }

    @Transactional(readOnly = false)
    public void update(Employee anEmployee) throws Exception {
        anEmployee.setWork(false);
        getSessionFactory().update(anEmployee);
    }

    @Transactional(readOnly = false)
    public void delete(Employee anEmployee) throws Exception {
        getSessionFactory().delete(anEmployee);
    }

    public List<Employee> findAll() throws Exception {
        return getSessionFactory().createCriteria(Employee.class).list();
    }
}
