package dallastools.services;

import dallastools.models.expenditur.*;
import dallastools.models.income.Sales;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by dimmaryanto on 07/11/15.
 */
@Repository
@Transactional(readOnly = true)
public class ServiceOfFinancialStatements {

    private SessionFactory sessionFactory;

    private Session getSessionFactory() {
        return sessionFactory.getCurrentSession();
    }

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<Sales> findSales(LocalDate in, LocalDate out) throws Exception {
        Criteria aCriteria = getSessionFactory().createCriteria(Sales.class);
        aCriteria.add(Restrictions.between("dateTransaction", Date.valueOf(in), Date.valueOf(out)));
        return aCriteria.list();
    }

    public List<PurchaseInvoice> findPurchase(LocalDate in, LocalDate out) throws Exception {
        Criteria aCriteria = getSessionFactory().createCriteria(PurchaseInvoice.class);
        aCriteria.add(Restrictions.between("transDate", Date.valueOf(in), Date.valueOf(out)));
        return aCriteria.list();
    }

    public List<DeliveryOfSales> findTransportations(LocalDate in, LocalDate out) throws Exception {
        Criteria aCriteria = getSessionFactory().createCriteria(DeliveryOfSales.class);
        aCriteria.add(Restrictions.between("dateSent", Date.valueOf(in), Date.valueOf(out)));
        return aCriteria.list();
    }

    public List<PayrollAnEmployee> findPayrollEmployeesForDivProduction(LocalDate in, LocalDate out) throws Exception {
        Criteria aCriteria = getSessionFactory().createCriteria(PayrollAnEmployee.class);
        aCriteria.add(Restrictions.between("date", Date.valueOf(in), Date.valueOf(out)));
        return aCriteria.list();
    }

    public List<PaymentInvoice> findPayment(LocalDate in, LocalDate out) throws Exception {
        Criteria aCriteria = getSessionFactory().createCriteria(PaymentInvoice.class);
        aCriteria.add(Restrictions.between("transDate", Date.valueOf(in), Date.valueOf(out)));
        return aCriteria.list();
    }


    public List<CashRecieptForEmployee> findCashRecieptForEmployee(LocalDate in, LocalDate out) throws Exception {
        Criteria aCriteria = getSessionFactory().createCriteria(CashRecieptForEmployee.class);
        aCriteria.add(Restrictions.between("date", Date.valueOf(in), Date.valueOf(out)));
        return aCriteria.list();
    }
}
