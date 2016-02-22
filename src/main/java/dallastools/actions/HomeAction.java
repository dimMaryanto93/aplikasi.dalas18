package dallastools.actions;

import dallastools.actions.application.RegisterAccountAction;
import dallastools.actions.expeditur.DeliverySalesAction;
import dallastools.actions.expeditur.EmployeePayrollAction;
import dallastools.actions.expeditur.PaymentInvoiceAction;
import dallastools.actions.expeditur.PurchaseInvoiceAction;
import dallastools.actions.income.SalesInvoiceAction;
import dallastools.actions.income.SalesOrderAction;
import dallastools.actions.masterdata.*;
import dallastools.actions.productions.ItemUsedAction;
import dallastools.actions.productions.ProductionSalesAction;
import dallastools.actions.reports.FinancialStatementsAction;
import dallastools.actions.reports.IncomeReportAction;
import dallastools.actions.reports.ReportProductionOfItemAction;
import dallastools.actions.reports.SalesTurnoverAction;
import dallastools.actions.settings.AccountAction;
import dallastools.controllers.FxInitializable;
import dallastools.controllers.stages.InnerScene;
import dallastools.models.masterdata.Account;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by dimmaryanto on 9/24/15.
 */
public class HomeAction implements FxInitializable {

    private InnerScene innerScene;
    private ApplicationContext springContext;
    private MessageSource messageSource;
    private Account account;

    @FXML
    private MenuItem mniChangeAccount;
    @FXML
    private MenuItem mniAccount;
    @FXML
    private Menu menuSettings;
    @FXML
    private MenuItem mnuRegisterAccount;
    @FXML
    private TitledPane menuReports;
    @FXML
    private TitledPane menuExpenditur;
    @FXML
    private TitledPane menuIncome;
    @FXML
    private TitledPane menuProductions;
    @FXML
    private TitledPane menuMasterData;
    @FXML
    private Button btnReportProductionOfItem;
    @FXML
    private BorderPane content;
    @FXML
    private Label txtUserId;
    @FXML
    private Button btnUnitOfItem;
    @FXML
    private Button btnEmployee;
    @FXML
    private Button btnCategoriOfItem;
    @FXML
    private Button btnDepartment;
    @FXML
    private Button btnWarehouse;
    @FXML
    private Button btnItem;
    @FXML
    private Button btnCustomer;
    @FXML
    private Button btnSuplayer;
    @FXML
    private Button btnCategoryPayment;
    @FXML
    private Button btnSalesProduction;
    @FXML
    private Button btnProduction;
    @FXML
    private Button btnSalesOrder;
    @FXML
    private Button btnSales;
    @FXML
    private Button btnSalesDelivery;
    @FXML
    private Button btnPurchase;
    @FXML
    private Button btnPayment;
    @FXML
    private Button btnPayroll;
    @FXML
    private Button btnReportFinancial;
    @FXML
    private Button btnReportIncome;
    @FXML
    private Button btnReportSalesTurnOver;
    @FXML
    private Hyperlink logoutAction;
    @FXML
    private Label txtName;
    @FXML
    private Label txtLevel;
    @FXML
    private MenuItem mniLogin;
    @FXML
    private MenuItem mniLogout;
    @FXML
    private Text statusLeftUser;
    @FXML
    private Text statusRightUser;

    @Override
    public void doClose() {
        try {
            Platform.exit();
        } catch (Exception e) {
            System.exit(0);
            e.printStackTrace();
        }
    }

    @Autowired
    public void setInnerScene(InnerScene innerScene) {
        this.innerScene = innerScene;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.springContext = applicationContext;
    }

    public void updateContent() {
        this.content.setCenter(innerScene.getNode());
        this.content.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
    }

    public void initialize(URL location, ResourceBundle resources) {
        expandedMenu(false);
        logoutAction.setVisible(false);
        statusAccount(null);
    }

    public Account getAccount() {
        return account;
    }

    @FXML
    public void doLogout() {
        enableMenuMasterData(false);
        enableMenuProduction(false);
        enableMenuIncome(false);
        enableMenuExpenditur(false);
        enableMenuReports(false);
        statusAccount(null);
        expandedMenu(false);
        this.content.setCenter(null);
    }

    @FXML
    public void showCustomers() {
        CustomerAction action = springContext.getBean(CustomerAction.class);
        updateContent();
        action.loadData();
    }

    @FXML
    public void showEmployees() {
        EmployeeAction action = springContext.getBean(EmployeeAction.class);
        updateContent();
        action.loadData();
    }

    @FXML
    public void showJobs() {
        DepartmentAction action = springContext.getBean(DepartmentAction.class);
        updateContent();
        action.loadData();
    }

    @FXML
    public void showCategoriesOfItem() {
        CategoryOfItemAction action = springContext.getBean(CategoryOfItemAction.class);
        updateContent();
        action.loadData();
    }

    @FXML
    public void showUnitOfItem() {
        UnitOfItemAction action = springContext.getBean(UnitOfItemAction.class);
        updateContent();
        action.loadData();
    }

    @FXML
    public void showItems() {
        ItemAction action = springContext.getBean(ItemAction.class);
        updateContent();
        action.loadData();
    }

    @FXML
    public void showSuplayers() {
        SuplayerAction action = springContext.getBean(SuplayerAction.class);
        updateContent();
        action.loadData();
    }

    @FXML
    public void showAccounts() {
        AccountAction account = springContext.getBean(AccountAction.class);
        updateContent();
        account.loadData();
    }

    @FXML
    public void showSales() {
        SalesInvoiceAction action = springContext.getBean(SalesInvoiceAction.class);
        updateContent();
        action.loadData();
    }

    @FXML
    public void showSalesDelivery() {
        DeliverySalesAction action = springContext.getBean(DeliverySalesAction.class);
        updateContent();
        action.loadData();
    }

    @FXML
    public void showPurchasing() {
        PurchaseInvoiceAction action = springContext.getBean(PurchaseInvoiceAction.class);
        updateContent();
        action.loadData();
    }

    @FXML
    public void showCategoryPayment() {
        PaymentCategoryAction action = springContext.getBean(PaymentCategoryAction.class);
        updateContent();
        action.loadData();
    }

    @FXML
    public void showPaymentInvoice() {
        PaymentInvoiceAction action = springContext.getBean(PaymentInvoiceAction.class);
        updateContent();
        action.loadData();
    }

    @FXML
    public void showWarehouse() {
        WarehouseAction action = springContext.getBean(WarehouseAction.class);
        updateContent();
        action.loadData();
    }

    @FXML
    public void showPayrolls() {
        EmployeePayrollAction action = springContext.getBean(EmployeePayrollAction.class);
        updateContent();
        action.loadData();
    }

    @FXML
    public void showProductions() {
        ItemUsedAction action = springContext.getBean(ItemUsedAction.class);
        updateContent();
        action.loadData();
    }

    @FXML
    public void showSalesOrder() {
        SalesOrderAction action = springContext.getBean(SalesOrderAction.class);
        updateContent();
        action.loadData();
    }

    @FXML
    public void showFinancialStatement() {
        springContext.getBean(FinancialStatementsAction.class);
        updateContent();
    }

    @FXML
    public void showReportIncome() {
        springContext.getBean(IncomeReportAction.class);
        updateContent();
    }

    @FXML
    public void showSalesTurnOver() {
        SalesTurnoverAction action = springContext.getBean(SalesTurnoverAction.class);
        updateContent();
        action.loadData();
    }

    @FXML
    public void showSalesProductions() {
        ProductionSalesAction action = springContext.getBean(ProductionSalesAction.class);
        updateContent();
        action.loadData();
    }

    @FXML
    public void showReportProductionOfItem() {
        ReportProductionOfItemAction action = springContext.getBean(ReportProductionOfItemAction.class);
        updateContent();
        action.initComponent();
    }

    private void statusAccount(Account anAccount) {
        txtUserId.setVisible(anAccount != null);
        txtName.setVisible(anAccount != null);
        txtLevel.setVisible(anAccount != null);

        statusLeftUser.setVisible(anAccount != null);
        statusRightUser.setVisible(anAccount != null);

        logoutAction.setVisible(anAccount != null);

        mniLogout.setDisable(anAccount == null);
        mniLogin.setDisable(anAccount != null);

        if (anAccount != null) {
            txtUserId.setText(anAccount.getUsername());
            txtName.setText(anAccount.getFullname());
            txtLevel.setText(anAccount.getLevel());
        } else {
            txtUserId.setText("");
            txtName.setText("");
            txtLevel.setText("");
        }
    }

    private void expandedMenu(Boolean active) {
        enableMenuSettings(active);
        menuMasterData.setExpanded(active);
        menuProductions.setExpanded(active);
        menuIncome.setExpanded(active);
        menuExpenditur.setExpanded(active);
        menuReports.setExpanded(active);
        setOpacityMenu(active);
    }

    private void opacityOfMenu(Double value) {
        menuMasterData.setOpacity(value);
        menuProductions.setOpacity(value);
        menuIncome.setOpacity(value);
        menuExpenditur.setOpacity(value);
        menuReports.setOpacity(value);
    }

    private void setOpacityMenu(Boolean active) {
        if (active) {
            opacityOfMenu(1.0);
        } else {
            opacityOfMenu(0.3);
        }
    }

    private void enableMenuSettings(Boolean active) {
        mniAccount.setDisable(!active);
    }

    public void setLogin(Account anAccount) {
        this.account = anAccount;
        statusAccount(anAccount);
        enableMenuSettings(false);
        if (anAccount != null && anAccount.getActive() && anAccount.getLevel().equals("ADMIN")) {
            enableMenuSettings(true);
            expandedMenu(true);
            enableMenuMasterData(true);
            enableMenuProduction(true);
            enableMenuIncome(true);
            enableMenuExpenditur(true);
            enableMenuReports(true);
        } else if (anAccount != null && anAccount.getLevel().equals("BENDAHARA")) {
            expandedMenu(false);
            //enable transacation
            btnPayment.setDisable(false);
            btnPurchase.setDisable(false);
            //enable masterdata
            btnCategoryPayment.setDisable(false);
            btnSuplayer.setDisable(false);
            btnItem.setDisable(false);
            btnUnitOfItem.setDisable(false);
            btnCategoriOfItem.setDisable(false);
            btnWarehouse.setDisable(false);

            menuExpenditur.setExpanded(true);

            menuExpenditur.setOpacity(1.0);
            menuMasterData.setOpacity(1.0);
        } else if (anAccount != null && anAccount.getLevel().equals("PRODUKSI")) {
            expandedMenu(false);
            enableMenuMasterData(true);
            enableMenuProduction(true);
            enableMenuIncome(true);
            //enable transaction
            btnSalesDelivery.setDisable(false);
            btnPayroll.setDisable(false);
            //disable master data
            btnSuplayer.setDisable(true);
            btnCategoryPayment.setDisable(true);

            menuMasterData.setOpacity(1.0);
            menuProductions.setOpacity(1.0);
            menuIncome.setOpacity(1.0);
            menuExpenditur.setOpacity(1.0);

            menuProductions.setExpanded(true);
            menuIncome.setExpanded(true);
            menuExpenditur.setExpanded(true);
        } else if (anAccount != null && anAccount.getLevel().equals("PEMILIK")) {
            expandedMenu(false);
            enableMenuMasterData(true);
            enableMenuReports(true);

            menuMasterData.setOpacity(1.0);
            menuReports.setOpacity(1.0);

            menuReports.setExpanded(true);
        }
    }

    private void enableMenuMasterData(Boolean active) {
        btnEmployee.setDisable(!active);
        btnUnitOfItem.setDisable(!active);
        btnDepartment.setDisable(!active);
        btnCategoriOfItem.setDisable(!active);
        btnWarehouse.setDisable(!active);
        btnItem.setDisable(!active);
        btnCustomer.setDisable(!active);
        btnSuplayer.setDisable(!active);
        btnCategoryPayment.setDisable(!active);
    }

    private void enableMenuProduction(Boolean active) {
        btnSalesProduction.setDisable(!active);
        btnProduction.setDisable(!active);
    }

    private void enableMenuIncome(Boolean active) {
        btnSalesOrder.setDisable(!active);
        btnSales.setDisable(!active);
    }

    private void enableMenuExpenditur(Boolean active) {
        btnSalesDelivery.setDisable(!active);
        btnPurchase.setDisable(!active);
        btnPayment.setDisable(!active);
        btnPayroll.setDisable(!active);
    }

    private void enableMenuReports(Boolean active) {
        btnReportFinancial.setDisable(!active);
        btnReportIncome.setDisable(!active);
        btnReportSalesTurnOver.setDisable(!active);
        btnReportProductionOfItem.setDisable(!active);
    }

    @FXML
    public void doLogin() {
        LoginAction action = springContext.getBean(LoginAction.class);
        this.content.setCenter(null);
        action.initValidator();
    }


    @FXML
    public void doRegisterAccount() {
        RegisterAccountAction action = springContext.getBean(RegisterAccountAction.class);
        action.initValidation();
    }

    @FXML
    public void showChangedAccount() {

    }


}
