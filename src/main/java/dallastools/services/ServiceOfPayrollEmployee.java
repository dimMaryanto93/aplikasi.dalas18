package dallastools.services;

import dallastools.models.expenditur.PayrollAnEmployee;
import dallastools.models.masterdata.Employee;
import dallastools.models.masterdata.Item;
import dallastools.models.other.ItemSumForProduction;
import dallastools.models.productions.ProductionOfSales;
import dallastools.models.productions.ProductionOfSalesDetails;
import javafx.collections.ObservableList;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by dimmaryanto on 23/11/15.
 */
@Repository
@Transactional(readOnly = true)
public class ServiceOfPayrollEmployee {
    private final Logger log = LoggerFactory.getLogger(ServiceOfPayrollEmployee.class);
    private SessionFactory sessionFactory;

    public Session getSessionFactory() {
        return sessionFactory.getCurrentSession();
    }

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional(readOnly = false)
    public Integer save(PayrollAnEmployee payroll) throws Exception {
        for (ProductionOfSales sales : payroll.getDetails()) {
            sales.setUsed(true);
            getSessionFactory().update(sales);
        }
        return (Integer) getSessionFactory().save(payroll);
    }

    @Transactional(readOnly = false)
    public void delete(PayrollAnEmployee payroll) throws Exception {
        PayrollAnEmployee payment = getSessionFactory().get(PayrollAnEmployee.class, payroll.getId());
        for (ProductionOfSales production : payment.getDetails()) {
            log.info("Update production id = {} to false", production.getId());
            production.setUsed(false);
            getSessionFactory().update(production);
        }
        getSessionFactory().delete(payment);
    }

    public List<PayrollAnEmployee> findTransactionByEmployee(Employee anEmployee) throws Exception {
        return getSessionFactory().
                createCriteria(PayrollAnEmployee.class).
                add(Restrictions.eq("employee.id", anEmployee.getId())).
                list();
    }

    public List<ProductionOfSales> findProductionByEmployee(Employee anEmployee) throws Exception {
        Criteria aCriteria = getSessionFactory().createCriteria(ProductionOfSales.class);
        aCriteria.add(Restrictions.and(Restrictions.eq("employee.id", anEmployee.getId()), Restrictions.eq("used", false)));
        return aCriteria.list();
    }

    public List<ItemSumForProduction> findItemGroupByProductions(ObservableList<ProductionOfSales> list) throws Exception {
        Criteria aCriteria = getSessionFactory().createCriteria(ProductionOfSalesDetails.class);

        aCriteria.add(Restrictions.in("production", list));

        ProjectionList projections = Projections.projectionList();
        projections.add(Projections.groupProperty("item"));
        projections.add(Projections.sum("itemUsed"));

        aCriteria.setProjection(projections);

        Iterator values = aCriteria.list().iterator();
        List<ItemSumForProduction> emptyList = new ArrayList<>();
        while (values.hasNext()) {
            Object[] rows = (Object[]) values.next();
            Item anItem = (Item) rows[0];
            Long qty = (Long) rows[1];
            ItemSumForProduction value = new ItemSumForProduction(anItem, qty.intValue(), 0.0);
            emptyList.add(value);
        }
        return emptyList;
    }
}
