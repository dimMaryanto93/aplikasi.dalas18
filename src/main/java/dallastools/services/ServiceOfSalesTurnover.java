package dallastools.services;

import dallastools.models.income.Sales;
import dallastools.models.income.SalesDetails;
import dallastools.models.masterdata.Customer;
import dallastools.models.masterdata.Item;
import dallastools.models.other.ItemSum;
import dallastools.models.other.SalesSumOfDate;
import dallastools.models.other.SalesSumOfMonth;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
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
 * Created by dimmaryanto on 11/11/15.
 */
@Repository
@Transactional(readOnly = true)
public class ServiceOfSalesTurnover {

    private SessionFactory sessionFactory;

    @Autowired
    public ServiceOfSalesTurnover(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session getSessionFactory() {
        return sessionFactory.getCurrentSession();
    }

    public List<Customer> findAllCustomer() throws Exception {
        return getSessionFactory().createCriteria(Customer.class).list();
    }

    public List<SalesSumOfDate> findIncomeByDate(LocalDate dateIn, LocalDate dateOut, Customer customer) throws Exception {
        Criteria aCriteria = getSessionFactory().createCriteria(Sales.class);
        aCriteria.createAlias("customer", "c");

        aCriteria.add(Restrictions.and(
                        Restrictions.between("dateTransaction", Date.valueOf(dateIn), Date.valueOf(dateOut)),
                        Restrictions.eq("c.id", customer.getId()))
        );

        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.groupProperty("dateTransaction"));
        projectionList.add(Projections.sum("grantTotal"));
        projectionList.add(Projections.sum("ammount"));

        aCriteria.setProjection(projectionList);

        aCriteria.addOrder(Order.asc("dateTransaction"));

        Iterator index = aCriteria.list().iterator();
        List<SalesSumOfDate> emptyList = new ArrayList<>();

        while (index.hasNext()) {
            Object[] rows = (Object[]) index.next();
            LocalDate date = ((Date) rows[0]).toLocalDate();
            Double grantTotal = (Double) rows[1];
            Double ammount = (Double) rows[2];
            emptyList.add(new SalesSumOfDate(date, grantTotal, ammount));
        }
        return emptyList;
    }

    public List<SalesSumOfMonth> findIncomeByMonth(LocalDate dateIn, LocalDate dateOut, Customer customer) throws Exception {
        Criteria aCriteria = getSessionFactory().createCriteria(Sales.class);
        aCriteria.createAlias("customer", "c");

        aCriteria.add(Restrictions.and(
                        Restrictions.between("dateTransaction", Date.valueOf(dateIn), Date.valueOf(dateOut)),
                        Restrictions.eq("c.id", customer.getId()))
        );

        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.groupProperty("month"));
        projectionList.add(Projections.sum("grantTotal"));
        projectionList.add(Projections.sum("ammount"));

        aCriteria.setProjection(projectionList);

        aCriteria.addOrder(Order.asc("month"));

        Iterator index = aCriteria.list().iterator();
        List<SalesSumOfMonth> emptyList = new ArrayList<>();

        while (index.hasNext()) {
            Object[] rows = (Object[]) index.next();
            String date = (String) rows[0];
            Double grantTotal = (Double) rows[1];
            Double ammount = (Double) rows[2];
            emptyList.add(new SalesSumOfMonth(date, grantTotal, ammount));
        }
        return emptyList;
    }

    public List findItemsByCustomer(LocalDate dateIn, LocalDate dateOut, Customer customer) throws Exception {

        Criteria aCriteria = getSessionFactory().createCriteria(SalesDetails.class);
        aCriteria.createAlias("sales", "s");
        aCriteria.createAlias("s.customer", "c");
        aCriteria.createAlias("item", "i");

        aCriteria.add(Restrictions.and(
                Restrictions.between("s.dateTransaction", Date.valueOf(dateIn), Date.valueOf(dateOut)),
                Restrictions.eq("c.id", customer.getId())));

        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.groupProperty("i.itemGenerator"));
        projectionList.add(Projections.sum("qty"));

        aCriteria.setProjection(projectionList);

        aCriteria.addOrder(Order.asc("i.itemGenerator"));

        List<ItemSum> emptyList = new ArrayList<>();
        Iterator index = aCriteria.list().iterator();
        while (index.hasNext()) {
            Object[] rows = (Object[]) index.next();
            Item anItem = getSessionFactory().get(Item.class, (Integer) rows[0]);
            Long qty = (Long) rows[1];
            emptyList.add(new ItemSum(anItem, qty));
        }

        return emptyList;
    }
}
