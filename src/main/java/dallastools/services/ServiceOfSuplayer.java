package dallastools.services;

import dallastools.models.masterdata.Suplayer;
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
public class ServiceOfSuplayer {

    private SessionFactory sessionFactory;

    @Autowired
    public ServiceOfSuplayer(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Session getSessionFactory() {
        return sessionFactory.getCurrentSession();
    }

    @Transactional(readOnly = false)
    public void save(Suplayer aSuplayer) throws Exception {
        getSessionFactory().save(aSuplayer);
        aSuplayer.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
    }

    @Transactional(readOnly = false)
    public void update(Suplayer aSuplayer) throws Exception {
        getSessionFactory().update(aSuplayer);
        aSuplayer.setLastUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
    }

    @Transactional(readOnly = false)
    public void delete(Suplayer aSuplayer) throws Exception {
        getSessionFactory().delete(aSuplayer);
    }


    public List<Suplayer> findAll() throws Exception {
        return getSessionFactory().createQuery("from Suplayer ").list();
    }

}
