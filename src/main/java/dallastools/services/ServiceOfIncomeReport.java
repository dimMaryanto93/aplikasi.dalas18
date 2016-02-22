package dallastools.services;

import dallastools.models.income.Sales;
import dallastools.models.income.SalesDetails;
import dallastools.models.masterdata.Item;
import dallastools.models.other.*;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by dimmaryanto on 08/11/15.
 */
@Repository
@Transactional(readOnly = true)
public class ServiceOfIncomeReport {

    private final Logger log = LoggerFactory.getLogger(ServiceOfIncomeReport.class);

    private SessionFactory sessionFactory;

    @Autowired
    public ServiceOfIncomeReport(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session getSessionFactory() {
        return sessionFactory.getCurrentSession();
    }

    public List<Sales> findSalesTransaction() throws Exception {
        Criteria aCriteria = getSessionFactory().createCriteria(Sales.class);
        return aCriteria.list();
    }

    public List<SalesDetails> findItemsAnySalesTransaction() throws Exception {
        Criteria aCriteria = getSessionFactory().createCriteria(SalesDetails.class);
        return aCriteria.list();
    }

    public List<SalesSumOfDate> findSalesGroupByDate(LocalDate in, LocalDate out) throws Exception {
        Date before = Date.valueOf(in);
        Date after = Date.valueOf(out);

        Criteria aCriteria = getSessionFactory().createCriteria(Sales.class);

        aCriteria.add(Restrictions.between("dateTransaction", before, after));

        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.groupProperty("dateTransaction"));
        projectionList.add(Projections.sum("grantTotal"));
        projectionList.add(Projections.sum("ammount"));

        aCriteria.setProjection(projectionList);

        aCriteria.addOrder(Order.asc("dateTransaction"));

        List<SalesSumOfDate> emptyList = new ArrayList<>();
        Iterator index = aCriteria.list().iterator();
        while (index.hasNext()) {
            Object[] rows = (Object[]) index.next();
            LocalDate date = ((Date) rows[0]).toLocalDate();
            Double grantTotal = (Double) rows[1];
            Double ammount = (Double) rows[2];
            emptyList.add(new SalesSumOfDate(date, grantTotal, ammount));
        }

        return emptyList;
    }

    public List<SalesSumOfMonth> findSalesGroupByMonth(LocalDate in, LocalDate out) throws Exception {
        Date before = Date.valueOf(in);
        Date after = Date.valueOf(out);
        log.info("between {} and {}", before, after);

        Criteria aCriteria = getSessionFactory().createCriteria(Sales.class);

        aCriteria.add(Restrictions.between("dateTransaction", before, after));

        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.groupProperty("month"));
        projectionList.add(Projections.sum("grantTotal"));
        projectionList.add(Projections.sum("ammount"));

        aCriteria.setProjection(projectionList);

        aCriteria.addOrder(Order.asc("month"));

        List<SalesSumOfMonth> emptyList = new ArrayList<>();
        Iterator index = aCriteria.list().iterator();
        log.info("Data Transaksi Penjualan diterima {} data", emptyList.size());
        while (index.hasNext()) {
            Object[] rows = (Object[]) index.next();
            String date = (rows[0]).toString();
            Double grantTotal = (Double) rows[1];
            Double ammount = (Double) rows[2];
            emptyList.add(new SalesSumOfMonth(date, grantTotal, ammount));
        }

        return emptyList;
    }

    /**
     * menggabungkan data produk pada detail penjualan dan kemudian menjumlahkan data yang dijual
     *
     * @return
     * @throws Exception
     */
    public List<ItemSum> findItemGrupBySalesTransaction(LocalDate in, LocalDate out) throws Exception {
        Date before = Date.valueOf(in);
        Date after = Date.valueOf(out);

        Criteria aCriteria = getSessionFactory().createCriteria(SalesDetails.class);
        /*income as s , item as i*/
        aCriteria.createAlias("sales", "s");
        aCriteria.createAlias("item", "i");

        /*betwean now()-10 and now() */
        aCriteria.add(Restrictions.between("s.dateTransaction", before, after));

        /*select item.id , sum(qty) from salesDetails order by item.name group by item.itemGenerator*/
        ProjectionList projections = Projections.projectionList();
        projections.add(Projections.groupProperty("i.itemGenerator"));
        projections.add(Projections.sum("qty"));

        aCriteria.setProjection(projections);

        /*order by item.name*/
        aCriteria.addOrder(Order.asc("i.name"));

        List<ItemSum> newList = new ArrayList<>();
        Iterator iterator = aCriteria.list().iterator();
        while (iterator.hasNext()) {
            Object[] rows = (Object[]) iterator.next();

            /*select item.* from item where itemGenerator = :item_id*/
            Item anItem = getSessionFactory().get(Item.class, (Integer) rows[0]);

            Long qty = (Long) rows[1];
            newList.add(new ItemSum(anItem, qty));
        }
        return newList;
    }

    /**
     * menggambungkan tanggal berdasarkan penjualan dan kemudian menjumlahkan produk yang dijual
     *
     * @return
     * @throws Exception
     */
    public List<ItemSumOfDate> findItemGrupByDateOfSalesTransaction() throws Exception {
        Date before = Date.valueOf(LocalDate.now().minusYears(2));
        Date after = Date.valueOf(LocalDate.now());
        Criteria aCriteria = getSessionFactory().createCriteria(SalesDetails.class);

        aCriteria.createAlias("sales", "s");
        aCriteria.createAlias("item", "i");

        //aCriteria.add(Restrictions.between("s.dateTransaction", before, after));

        ProjectionList projections = Projections.projectionList();
        projections.add(Projections.groupProperty("s.dateTransaction"));
        projections.add(Projections.sum("qty"));

        aCriteria.setProjection(projections);

        aCriteria.addOrder(Order.asc("s.dateTransaction"));

        List<ItemSumOfDate> emptyList = new ArrayList<>();
        Iterator iterator = aCriteria.list().iterator();
        while (iterator.hasNext()) {
            Object[] rows = (Object[]) iterator.next();

            Date date = (Date) rows[0];
            Long qty = (Long) rows[1];
            emptyList.add(new ItemSumOfDate(date.toLocalDate(), qty.intValue()));
        }

        return emptyList;
    }

    /**
     * menggambungkan bulan-tahun berdasarkan penjualan dan kemudian menjumlahkan produk yang dijual
     *
     * @return
     * @throws Exception
     */
    public List<ItemSumOfMonth> findItemGrupByMonthOfSalesTransaction() throws Exception {
        Date before = Date.valueOf(LocalDate.now().minusYears(2));
        Date after = Date.valueOf(LocalDate.now());
        Criteria aCriteria = getSessionFactory().createCriteria(SalesDetails.class);

        aCriteria.createAlias("sales", "s");
        aCriteria.createAlias("item", "i");

        //aCriteria.add(Restrictions.between("s.month", before, after));

        ProjectionList projections = Projections.projectionList();
        projections.add(Projections.groupProperty("s.month"));
        projections.add(Projections.sum("qty"));

        aCriteria.setProjection(projections);

        aCriteria.addOrder(Order.asc("s.month"));

        List<ItemSumOfMonth> emptyList = new ArrayList<>();
        Iterator iterator = aCriteria.list().iterator();
        while (iterator.hasNext()) {
            Object[] rows = (Object[]) iterator.next();

            YearMonth date = (YearMonth) rows[0];
            Long qty = (Long) rows[1];
            emptyList.add(new ItemSumOfMonth(date, qty.intValue()));
        }

        return emptyList;
    }

    /**
     * menggambungkan tahun berdasarkan penjualan dan kemudian menjumlahkan produk yang dijual
     *
     * @return
     * @throws Exception
     */
    public List<ItemSumOfYear> findItemGrupByYearOfSalesTransaction() throws Exception {
        Date before = Date.valueOf(LocalDate.now().minusYears(2));
        Date after = Date.valueOf(LocalDate.now());
        Criteria aCriteria = getSessionFactory().createCriteria(SalesDetails.class);

        aCriteria.createAlias("sales", "s");
        aCriteria.createAlias("item", "i");

        //aCriteria.add(Restrictions.between("s.year", before, after));

        ProjectionList projections = Projections.projectionList();
        projections.add(Projections.groupProperty("s.year"));
        projections.add(Projections.sum("qty"));

        aCriteria.setProjection(projections);

        aCriteria.addOrder(Order.asc("s.year"));

        List<ItemSumOfYear> emptyList = new ArrayList<>();
        Iterator iterator = aCriteria.list().iterator();
        while (iterator.hasNext()) {
            Object[] rows = (Object[]) iterator.next();

            Year date = (Year) rows[0];
            Long qty = (Long) rows[1];
            emptyList.add(new ItemSumOfYear(qty.intValue(), date));
        }

        return emptyList;
    }
}
