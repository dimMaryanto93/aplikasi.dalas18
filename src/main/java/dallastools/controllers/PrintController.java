package dallastools.controllers;

import dallastools.models.expenditur.DeliveryOfSales;
import dallastools.models.expenditur.DeliveryOfSalesDetails;
import dallastools.models.income.Sales;
import dallastools.models.income.SalesDetails;
import dallastools.models.income.SalesOrder;
import dallastools.models.income.SalesOrderDetails;
import dallastools.models.masterdata.Account;
import dallastools.models.masterdata.Employee;
import dallastools.models.masterdata.Item;
import dallastools.models.other.ItemSum;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dimmaryanto on 29/10/15.
 */
@Component
public class PrintController {

    private DataSource dataSource;
    private JasperDesign design;
    private JasperReport report;
    private JasperPrint print;
    private JasperViewer viewer;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private void setDesign(String jrxml) throws JRException {
        this.design = JRXmlLoader.load(getClass().getResourceAsStream(jrxml));
    }

    private void setReport(JasperDesign design) throws JRException {
        this.report = JasperCompileManager.compileReport(design);
    }

    private void setPrint(JasperReport report, HashMap map, JRBeanCollectionDataSource collection) throws JRException {
        this.print = JasperFillManager.fillReport(report, map, collection);
    }

    private void setPrint(JasperReport report, HashMap map, JREmptyDataSource collection) throws JRException {
        this.print = JasperFillManager.fillReport(report, map, collection);
    }

    private void setPrint(JasperReport report, HashMap map, DataSource dataSource) throws SQLException, JRException {
        this.print = JasperFillManager.fillReport(report, map, dataSource.getConnection());
    }

    private void setViewer(JasperPrint print, String title) {
        this.viewer = new JasperViewer(print, false);
        this.viewer.setSize(600, 400);
        this.viewer.setTitle(title);
        this.viewer.setFitWidthZoomRatio();
        this.viewer.setVisible(true);
        this.viewer.requestFocus();
    }

    public void showItemsReport(String title, List<Item> list) throws JRException {
        setDesign("/stage/reports/items.jrxml");
        setReport(this.design);
        setPrint(this.report, null, new JRBeanCollectionDataSource(list));
        setViewer(this.print, title);
    }

    public void showSalesInvoice(String title, Sales aSales, List<SalesDetails> list) throws JRException {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("nota", aSales.getTransId());
        map.put("tuan", aSales.getCustomer().getCustomerName());
        map.put("dp", aSales.getAmmount());
        map.put("total", aSales.getGrantTotal());
        map.put("alamat", aSales.getShipTo());
        setDesign("/stage/reports/sales.jrxml");
        setReport(this.design);
        setPrint(this.report, map, new JRBeanCollectionDataSource(list));
        setViewer(this.print, title);
    }

    public void showSalesOrder(String title, SalesOrder order, List<SalesOrderDetails> list, Account account) throws JRException {
        HashMap<String, Object> map = new HashMap<>();
        map.put("operator", account.getFullname());
        map.put("pelanggan", order.getCustomer().getCustomerName());
        map.put("tanggal", order.getOrderDate().toString());
        map.put("nota", order.getTransId());
        map.put("alamat", order.getCustomer().getAddress().getStreetAddress());
        setDesign("/stage/reports/sales_order.jrxml");
        setReport(this.design);
        setPrint(this.report, map, new JRBeanCollectionDataSource(list));
        setViewer(this.print, title);
    }

    @Deprecated
    public void showSalesDelivery(String title, DeliveryOfSales delivery, List<DeliveryOfSalesDetails> list)
            throws JRException {
        HashMap<String, Object> map = new HashMap<>();
        map.put("employee", delivery.getEmployee().getEmployeeName());
        map.put("trans_id", delivery.getDeliveryId());
        setDesign("/stage/reports/sales_delivery.jrxml");
        setReport(this.design);
        setPrint(report, map, new JRBeanCollectionDataSource(list));
        setViewer(print, title);
    }

    public void showSalesDeliveries(
            String title,
            DeliveryOfSales delivery,
            List<DeliveryOfSalesDetails> deliveryDetails,
            Account anAccount,
            HashMap<Sales, List<SalesDetails>> hashMapList) throws JRException {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        setDesign("/stage/reports/delivery_of_sales.jrxml");
        setReport(this.design);

        for (int i = 0; i < deliveryDetails.size(); i++) {
            DeliveryOfSalesDetails deliveryDetail = deliveryDetails.get(i);
            Sales sales = deliveryDetail.getSales();
            HashMap<String, Object> mapParams = new HashMap<>();
            mapParams.put("customer", deliveryDetail.getSales().getCustomer().getCustomerName());
            mapParams.put("streetAddress", sales.getShipTo());
            mapParams.put("phone", sales.getCustomer().getPhone());
            mapParams.put("salesId", sales.getTransId());
            mapParams.put("dateTrans", timeFormatter.format(sales.getDateTransaction().toLocalDate()));
            mapParams.put("supir", delivery.getEmployee().getEmployeeName());
            mapParams.put("operator", anAccount.getFullname());

            List<SalesDetails> items = hashMapList.get(sales);

            setPrint(report, mapParams, new JRBeanCollectionDataSource(items));
            setViewer(print, title);
        }
    }

    public void showItemForProduction(String title, List<ItemSum> list, Account account) throws JRException {
        setDesign("/stage/reports/production.jrxml");
        setReport(this.design);
        HashMap<String, Object> mapParams = new HashMap<>();
        mapParams.put("operator", account.getFullname());
        setPrint(this.report, mapParams, new JRBeanCollectionDataSource(list));
        setViewer(print, title);
    }

    public void showReportIncomeStatemenet(String title, HashMap<String, Object> valueMapped) throws JRException {
        setDesign("/stage/reports/financial_statement.jrxml");
        setReport(this.design);
        setPrint(this.report, valueMapped, new JREmptyDataSource());
        setViewer(print, title);
    }

    public void showReportProductionByEmployee(String title, List<ItemSum> listSum,
                                               LocalDate dateBefore, LocalDate dateAfter,
                                               Employee anEmployee) throws JRException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        StringBuilder sb = new StringBuilder();
        sb.append(formatter.format(dateBefore)).append(" ").append("s/d").append(" ").append(formatter.format(dateAfter));
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", anEmployee.getEmployeeName());
        hashMap.put("tanggal", sb.toString());
        setDesign("/stage/reports/item_production.jrxml");
        setReport(this.design);
        setPrint(this.report, hashMap, new JRBeanCollectionDataSource(listSum));
        setViewer(print, title);
    }


}
