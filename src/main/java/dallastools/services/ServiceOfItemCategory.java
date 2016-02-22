package dallastools.services;

import dallastools.models.masterdata.CategoryOfItem;
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
public class ServiceOfItemCategory {

    private SessionFactory sessionFactory;

    @Autowired
    public ServiceOfItemCategory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Session getSessionFactory() {
        return sessionFactory.getCurrentSession();
    }

    @Transactional(readOnly = false)
    public void save(CategoryOfItem aCategoryOfItem) throws Exception {
        getSessionFactory().save(aCategoryOfItem);
    }

    @Transactional(readOnly = false)
    public void update(CategoryOfItem aCategoryOfItem) throws Exception {
        getSessionFactory().update(aCategoryOfItem);
    }

    @Transactional(readOnly = false)
    public void delete(CategoryOfItem aCategoryOfItem) throws Exception {
        getSessionFactory().delete(aCategoryOfItem);
    }

    public List<CategoryOfItem> findAll() throws Exception {
        return getSessionFactory().createQuery("from CategoryOfItem").list();
    }
}
