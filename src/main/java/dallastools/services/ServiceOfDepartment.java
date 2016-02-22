package dallastools.services;

import dallastools.models.masterdata.Department;
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
public class ServiceOfDepartment {

    private SessionFactory sessionFactory;

    @Autowired
    public ServiceOfDepartment(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Session getSessionFactory() {
        return sessionFactory.getCurrentSession();
    }

    @Transactional(readOnly = false)
    public void save(Department aDepartment) throws Exception {
        getSessionFactory().save(aDepartment);
    }

    @Transactional(readOnly = false)
    public void update(Department aDepartment) throws Exception {
        getSessionFactory().update(aDepartment);
    }

    @Transactional(readOnly = false)
    public void delete(Department aDepartment) throws Exception {
        getSessionFactory().delete(aDepartment);
    }

    public List<Department> findAll() throws Exception {
        return getSessionFactory().createQuery("from Department ").list();
    }
}
