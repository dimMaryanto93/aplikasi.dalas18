package dallastools.controllers.stages;

import dallastools.actions.LoginAction;
import dallastools.actions.application.RegisterAccountAction;
import dallastools.actions.expeditur.AddItemToPurchaseInvoiceAction;
import dallastools.actions.expeditur.DeliverySalesDataItemAction;
import dallastools.actions.expeditur.QuickNewSuplayerAction;
import dallastools.actions.income.AddItemToSalesInvoiceAction;
import dallastools.actions.income.AddItemToSalesOrderAction;
import dallastools.actions.income.QuickNewCustomerAction;
import dallastools.actions.reports.FinancialStatementsDetailAction;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by dimmaryanto on 12/10/15.
 */
@Component
public class SecondStageController {

    private StageRunner loader;
    private Stage primaryStage;
    private Stage secondStage;

    @Autowired
    public SecondStageController(StageRunner loader, Stage primaryStage, Stage secondStage) {
        this.loader = loader;
        this.primaryStage = primaryStage;
        this.secondStage = secondStage;
        secondStage.initModality(Modality.APPLICATION_MODAL);
        secondStage.initOwner(this.primaryStage);
        secondStage.initStyle(StageStyle.DECORATED);
    }

    public Stage getSecondStage() {
        return secondStage;
    }

    public void showStage() {
        this.secondStage.requestFocus();
        this.secondStage.setAlwaysOnTop(false);
        this.secondStage.setResizable(false);
        this.secondStage.centerOnScreen();
        this.secondStage.show();
    }

    public void closeSecondStage() {
        this.secondStage.close();
    }

    @Bean
    @Scope("prototype")
    public LoginAction showLogin(@Value("/stage/scene/login.fxml") String fxml) {
        LoginAction action = (LoginAction) loader.getController(getClass(), secondStage, fxml);
        secondStage.requestFocus();
        secondStage.setAlwaysOnTop(true);
        showStage();
        return action;
    }

    @Bean
    @Scope("prototype")
    public AddItemToPurchaseInvoiceAction addItemToPurchaseInvoice(
            @Value("/stage/scene/transactions/expenditur/add_an_item_to_purchase_invoice.fxml") String fxml) {
        AddItemToPurchaseInvoiceAction action = (AddItemToPurchaseInvoiceAction) loader.getController(getClass(),
                secondStage, fxml);
        showStage();
        return action;
    }

    @Bean
    @Scope("prototype")
    public AddItemToSalesOrderAction addItemToSalesOrder(
            @Value("/stage/scene/transactions/incomes/add_an_item_to_sales_order.fxml") String fxml) {
        showStage();
        return (AddItemToSalesOrderAction) loader.getController(getClass(), secondStage, fxml);
    }

    @Bean
    @Scope("prototype")
    public AddItemToSalesInvoiceAction addItemToSalesInvoice(
            @Value("/stage/scene/transactions/incomes/add_an_item_to_sales_invoice.fxml") String fxml) {
        showStage();
        return (AddItemToSalesInvoiceAction) loader.getController(getClass(), secondStage, fxml);
    }

    @Bean
    @Scope("prototype")
    public QuickNewCustomerAction quickNewCustomer(
            @Value("/stage/scene/transactions/incomes/quick_new_customer.fxml") String fxml) {
        QuickNewCustomerAction action = (QuickNewCustomerAction) loader.getController(getClass(), secondStage, fxml);
        showStage();
        return action;
    }

    @Bean
    @Scope("prototype")
    public QuickNewSuplayerAction quickNewSuplayer(
            @Value("/stage/scene/transactions/expenditur/quick_new_suplayer.fxml") String fxml) {
        QuickNewSuplayerAction action = (QuickNewSuplayerAction) loader.getController(getClass(), secondStage, fxml);
        showStage();
        return action;
    }

    @Bean
    @Scope("prototype")
    public DeliverySalesDataItemAction deliverySalesDataItem(
            @Value("/stage/scene/transactions/expenditur/delivery_of_sales_data_item.fxml") String fxml) {
        DeliverySalesDataItemAction action = (DeliverySalesDataItemAction) loader.getController(getClass(), secondStage,
                fxml);
        showStage();
        return action;
    }

    @Bean
    @Scope("prototype")
    public FinancialStatementsDetailAction financialStatementsDetail(
            @Value("/stage/scene/reports/financial_statements_details.fxml") String fxml) {
        FinancialStatementsDetailAction action = (FinancialStatementsDetailAction) loader.getController(getClass(),
                secondStage, fxml);
        showStage();
        return action;
    }

    @Bean
    @Scope("prototype")
    public RegisterAccountAction registerAccount(
            @Value("/stage/scene/applications/register_account.fxml") String fxml) {
        RegisterAccountAction accountAction = (RegisterAccountAction) loader.getController(getClass(),
                secondStage, fxml);
        showStage();
        return accountAction;
    }

}
