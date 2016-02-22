package dallastools.services;

import dallastools.models.masterdata.Employee;
import dallastools.models.masterdata.Item;
import dallastools.models.other.ItemSum;
import dallastools.models.productions.ProductionOfSalesDetails;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by dimMaryanto on 1/11/2016.
 */
@Repository
@Transactional(readOnly = true)
public class ServiceOfReportProduction {

    private SessionFactory sessionFactory;

    @Autowired
    public ServiceOfReportProduction(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Session getSessionFactory() {
        return sessionFactory.getCurrentSession();
    }

    public List<ItemSum> findProductionByEmployee(
            Employee anEmployee,
            LocalDate dateBefore,
            LocalDate dateAfter) throws Exception {
        Criteria aCriteria = getSessionFactory().createCriteria(ProductionOfSalesDetails.class);
        aCriteria.createAlias("production", "p");
        aCriteria.createAlias("p.employee", "e");
        aCriteria.add(Restrictions.and(
                Restrictions.eq("e.id", anEmployee.getId()),
                Restrictions.between("p.date", Date.valueOf(dateBefore), Date.valueOf(dateAfter))
        ));

        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.groupProperty("item"));
        projectionList.add(Projections.sum("itemUsed"));
        aCriteria.setProjection(projectionList);

        List<ItemSum> emptyList = new ArrayList<>();
        Iterator rows = aCriteria.list().iterator();
        while (rows.hasNext()) {
            Object[] values = (Object[]) rows.next();
            Item anItem = (Item) values[0];
            Long qty = (Long) values[1];
            emptyList.add(new ItemSum(anItem, qty));
        }
        return emptyList;
    }
}
