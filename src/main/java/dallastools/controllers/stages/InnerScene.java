package dallastools.controllers.stages;

import dallastools.actions.expeditur.*;
import dallastools.actions.income.SalesInvoiceAction;
import dallastools.actions.income.SalesInvoiceDataAction;
import dallastools.actions.income.SalesOrderAction;
import dallastools.actions.income.SalesOrderDataAction;
import dallastools.actions.masterdata.*;
import dallastools.actions.productions.ItemUsedAction;
import dallastools.actions.productions.ItemUsedDataAction;
import dallastools.actions.productions.ProductionSalesAction;
import dallastools.actions.productions.ProductionSalesDataAction;
import dallastools.actions.reports.FinancialStatementsAction;
import dallastools.actions.reports.IncomeReportAction;
import dallastools.actions.reports.ReportProductionOfItemAction;
import dallastools.actions.reports.SalesTurnoverAction;
import dallastools.actions.settings.AccountAction;
import dallastools.actions.settings.AccountDataAction;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Created by dimmaryanto on 10/1/15.
 */
@Component
public class InnerScene {

    private Initializable init;

    private Node node;

    private ResourceBundle resourceBundle;

    private Stage primaryStage;

    @Autowired
    public void setResourceBundle(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    @Autowired
    @Qualifier("primaryStage")
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Initializable getInit() {
        return init;
    }

    private void setInit(Initializable init) {
        this.init = init;
    }

    public Node getNode() {
        return node;
    }

    private void setNode(Node node) {
        this.node = node;
    }

    private void setContentInitialize(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        loader.setResources(resourceBundle);
        this.setNode(loader.load());
        setInit(loader.getController());
    }

    @Bean
    @Scope("prototype")
    public CustomerAction customers(@Value("/stage/scene/masterdata/customer.fxml") String fxml) throws IOException {
        setContentInitialize(fxml);
        return (CustomerAction) getInit();
    }

    @Bean
    @Scope("prototype")
    public EmployeeAction employees(@Value("/stage/scene/masterdata/employee.fxml") String fxml) throws IOException {
        setContentInitialize(fxml);
        return (EmployeeAction) getInit();
    }

    @Bean
    @Scope("prototype")
    public CustomerDataAction customerData(@Value("/stage/scene/masterdata/customer_data.fxml") String fxml)
            throws IOException {
        setContentInitialize(fxml);
        return (CustomerDataAction) getInit();
    }

    @Bean
    @Scope("prototype")
    public DepartmentAction jobDescriptions(@Value("/stage/scene/masterdata/department.fxml") String fxml)
            throws IOException {
        setContentInitialize(fxml);
        return (DepartmentAction) getInit();
    }

    @Bean
    @Scope("prototype")
    public DepartmentDataAction jobDescriptionData(@Value("/stage/scene/masterdata/department_data.fxml") String fxml)
            throws IOException {
        setContentInitialize(fxml);
        return (DepartmentDataAction) getInit();
    }

    @Bean
    @Scope("prototype")
    public EmployeeDataAction employeeData(@Value("/stage/scene/masterdata/employee_data.fxml") String fxml)
            throws IOException {
        setContentInitialize(fxml);
        return (EmployeeDataAction) getInit();
    }

    @Bean
    @Scope("prototype")
    public CategoryOfItemAction categoryOfItem(@Value("/stage/scene/masterdata/category_of_item.fxml") String fxml)
            throws IOException {
        setContentInitialize(fxml);
        return (CategoryOfItemAction) getInit();
    }

    @Bean
    @Scope("prototype")
    public CategoryOfItemDataAction categoryOfItemData(
            @Value("/stage/scene/masterdata/category_of_item_data.fxml") String fxml) throws IOException {
        setContentInitialize(fxml);
        return (CategoryOfItemDataAction) getInit();
    }

    @Bean
    @Scope("prototype")
    public UnitOfItemAction UnitOfItem(@Value("/stage/scene/masterdata/unit_of_item.fxml") String fxml)
            throws IOException {
        setContentInitialize(fxml);
        return (UnitOfItemAction) getInit();
    }

    @Bean
    @Scope("prototype")
    public UnitOfItemDataAction unitOfItemData(@Value("/stage/scene/masterdata/unit_of_item_data.fxml") String fxml)
            throws IOException {
        setContentInitialize(fxml);
        return (UnitOfItemDataAction) getInit();
    }

    @Bean
    @Scope("prototype")
    public ItemAction items(@Value("/stage/scene/masterdata/item.fxml") String fxml) throws IOException {
        setContentInitialize(fxml);
        return (ItemAction) getInit();
    }

    @Bean
    @Scope("prototype")
    public ItemDataAction itemData(@Value("/stage/scene/masterdata/item_data.fxml") String fxml) throws IOException {
        setContentInitialize(fxml);
        return (ItemDataAction) getInit();
    }

    @Bean
    @Scope("prototype")
    public SuplayerAction suplayers(@Value("/stage/scene/masterdata/suplayer.fxml") String fxml) throws IOException {
        setContentInitialize(fxml);
        return (SuplayerAction) getInit();
    }

    @Bean
    @Scope("prototype")
    public SuplayerDataAction suplayerData(@Value("/stage/scene/masterdata/suplayer_data.fxml") String fxml)
            throws IOException {
        setContentInitialize(fxml);
        return (SuplayerDataAction) getInit();
    }

    @Bean
    @Scope("prototype")
    public AccountAction accounts(@Value("/stage/scene/settings/account.fxml") String fxml) throws IOException {
        setContentInitialize(fxml);
        return (AccountAction) getInit();
    }

    @Bean
    @Scope("prototype")
    public AccountDataAction accountData(@Value("/stage/scene/settings/account_data.fxml") String fxml)
            throws IOException {
        setContentInitialize(fxml);
        return (AccountDataAction) getInit();
    }

    @Bean
    @Scope("prototype")
    public SalesOrderAction salesOrder(@Value("/stage/scene/transactions/incomes/sales_order.fxml") String fxml)
            throws IOException {
        setContentInitialize(fxml);
        return (SalesOrderAction) getInit();
    }

    @Bean
    @Scope("prototype")
    public DeliverySalesAction salesDelivery(
            @Value("/stage/scene/transactions/expenditur/delivery_of_sales.fxml") String fxml) throws IOException {
        setContentInitialize(fxml);
        return (DeliverySalesAction) getInit();
    }

    @Bean
    @Scope("prototype")
    public PurchaseInvoiceAction purchaseInvoice(
            @Value("/stage/scene/transactions/expenditur/purchase_invoice.fxml") String fxml) throws IOException {
        setContentInitialize(fxml);
        return (PurchaseInvoiceAction) getInit();
    }

    @Bean
    @Scope("prototype")
    public PurchaseInvoiceDataAction purchaseInvoiceData(
            @Value("/stage/scene/transactions/expenditur/purchase_invoice_data.fxml") String fxml) throws IOException {
        setContentInitialize(fxml);
        return (PurchaseInvoiceDataAction) getInit();
    }

    @Bean
    @Scope("prototype")
    public SalesOrderDataAction salesOrderData(
            @Value("/stage/scene/transactions/incomes/sales_order_data.fxml") String fxml) throws IOException {
        setContentInitialize(fxml);
        return (SalesOrderDataAction) getInit();
    }

    @Bean
    @Scope("prototype")
    public SalesInvoiceDataAction salesInvoiceData(
            @Value("/stage/scene/transactions/incomes/sales_invoice_data.fxml") String fxml) throws IOException {
        setContentInitialize(fxml);
        return (SalesInvoiceDataAction) getInit();
    }

    @Bean
    @Scope("prototype")
    public PaymentCategoryAction paymentCategory(
            @Value("/stage/scene/masterdata/payment_category.fxml") String fxml)
            throws IOException {
        setContentInitialize(fxml);
        return (PaymentCategoryAction) getInit();
    }

    @Bean
    @Scope("prototype")
    public PaymentCategoryDataAction paymentCategoryData(
            @Value("/stage/scene/masterdata/payment_category_data.fxml") String fxml) throws IOException {
        setContentInitialize(fxml);
        return (PaymentCategoryDataAction) getInit();
    }

    @Bean
    @Scope("prototype")
    public PaymentInvoiceAction paymentInvoice(
            @Value("/stage/scene/transactions/expenditur/payment_invoice.fxml") String fxml) throws IOException {
        setContentInitialize(fxml);
        return (PaymentInvoiceAction) getInit();
    }

    @Bean
    @Scope("prototype")
    public PaymentInvoiceDataAction paymentInvoiceData(
            @Value("/stage/scene/transactions/expenditur/payment_invoice_data.fxml") String fxml) throws IOException {
        setContentInitialize(fxml);
        return (PaymentInvoiceDataAction) getInit();
    }

    @Bean
    @Scope("prototype")
    public DeliverySalesDataAction SalesDeliveryData(
            @Value("/stage/scene/transactions/expenditur/delivery_of_sales_data.fxml") String fxml) throws IOException {
        setContentInitialize(fxml);
        return (DeliverySalesDataAction) getInit();
    }

    @Bean
    @Scope("prototype")
    public WarehouseAction warehouse(
            @Value("/stage/scene/masterdata/warehouse.fxml") String fxml) throws IOException {
        setContentInitialize(fxml);
        return (WarehouseAction) getInit();
    }

    @Bean
    @Scope("prototype")
    public WarehouseDataAction warehouseData(
            @Value("/stage/scene/masterdata/warehouse_data.fxml") String fxml)
            throws IOException {
        setContentInitialize(fxml);
        return (WarehouseDataAction) getInit();
    }

    @Bean
    @Scope("prototype")
    public SalesInvoiceAction salesInvoice(
            @Value("/stage/scene/transactions/incomes/sales_invoice.fxml") String fxml)
            throws IOException {
        setContentInitialize(fxml);
        return (SalesInvoiceAction) getInit();
    }

    @Bean
    @Scope("prototype")
    public IncomeReportAction incomeReport(
            @Value("/stage/scene/reports/income_report.fxml") String fxml)
            throws IOException {
        setContentInitialize(fxml);
        return (IncomeReportAction) getInit();
    }

    @Bean
    @Scope("prototype")
    public SalesTurnoverAction salesTurnover(
            @Value("/stage/scene/reports/sales_turnover.fxml") String fxml)
            throws IOException {
        setContentInitialize(fxml);
        return (SalesTurnoverAction) getInit();
    }

    @Bean
    @Scope("prototype")
    public ProductionSalesAction salesProduction(
            @Value("/stage/scene/transactions/productions/production_of_sales.fxml") String fxml) throws IOException {
        setContentInitialize(fxml);
        return (ProductionSalesAction) getInit();
    }

    @Bean
    @Scope("prototype")
    public ProductionSalesDataAction productionSalesData(
            @Value("/stage/scene/transactions/productions/production_of_sales_data.fxml") String fxml)
            throws IOException {
        setContentInitialize(fxml);
        return (ProductionSalesDataAction) getInit();
    }

    @Bean
    @Scope("prototype")
    public ItemUsedAction itemUsed(@Value("/stage/scene/transactions/productions/item_used.fxml") String fxml)
            throws Exception {
        setContentInitialize(fxml);
        return (ItemUsedAction) getInit();
    }

    @Bean
    @Scope("prototype")
    public ItemUsedDataAction itemUsedData(
            @Value("/stage/scene/transactions/productions/item_used_data.fxml") String fxml) throws Exception {
        setContentInitialize(fxml);
        return (ItemUsedDataAction) getInit();
    }

    @Bean
    @Scope("prototype")
    public EmployeePayrollAction employeePayroll(
            @Value("/stage/scene/transactions/expenditur/employee_payroll.fxml") String fxml) throws IOException {
        setContentInitialize(fxml);
        return (EmployeePayrollAction) getInit();
    }

    @Bean
    @Scope("prototype")
    public EmployeePayrollDataAction employeePayrollData(
            @Value("/stage/scene/transactions/expenditur/employee_payroll_data.fxml") String fxml) throws IOException {
        setContentInitialize(fxml);
        return (EmployeePayrollDataAction) getInit();
    }

    @Bean
    @Scope("prototype")
    public EmployeeCashRecieptAction employeeCashReciept(
            @Value("/stage/scene/transactions/expenditur/employee_cash_reciept_data.fxml") String fxml)
            throws IOException {
        setContentInitialize(fxml);
        return (EmployeeCashRecieptAction) getInit();
    }

    @Bean
    @Scope("prototype")
    public FinancialStatementsAction financialStatements(
            @Value("/stage/scene/reports/financial_statements.fxml") String fxml) throws IOException {
        setContentInitialize(fxml);
        return (FinancialStatementsAction) getInit();
    }

    @Bean
    @Scope("prototype")
    public ReportProductionOfItemAction reportProductionOfItem(
            @Value("/stage/scene/reports/item_production.fxml") String fxml) throws IOException {
        setContentInitialize(fxml);
        return (ReportProductionOfItemAction) getInit();
    }

}
