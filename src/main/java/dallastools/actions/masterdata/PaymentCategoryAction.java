package dallastools.actions.masterdata;

import dallastools.actions.HomeAction;
import dallastools.controllers.FxInitializable;
import dallastools.controllers.TableViewColumnAction;
import dallastools.controllers.notifications.DialogBalloon;
import dallastools.controllers.notifications.DialogWindows;
import dallastools.controllers.notifications.LangProperties;
import dallastools.controllers.notifications.LangSource;
import dallastools.models.masterdata.CategoryOfPayment;
import dallastools.services.ServiceOfPaymentCategory;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by dimmaryanto on 18/10/15.
 */
public class PaymentCategoryAction implements FxInitializable {

    @FXML
    private TableView<CategoryOfPayment> tableView;
    @FXML
    private TableColumn<CategoryOfPayment, String> columnId;
    @FXML
    private TableColumn<CategoryOfPayment, String> columnPayment;
    @FXML
    private TableColumn<CategoryOfPayment, String> columnDescription;
    @FXML
    private TableColumn<CategoryOfPayment, String> columnAction;

    private DialogBalloon ballon;
    private DialogWindows windows;
    private ServiceOfPaymentCategory service;
    private HomeAction homeAction;
    private TableViewColumnAction actionColumn;
    private ApplicationContext springContext;
    private MessageSource messageSource;
    private LangSource lang;

    @Override
    public void doClose() {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.springContext = applicationContext;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        columnId.setCellValueFactory(new PropertyValueFactory<CategoryOfPayment, String>("id"));
        columnPayment.setCellValueFactory(new PropertyValueFactory<CategoryOfPayment, String>("paymentFor"));
        columnDescription.setCellValueFactory(new PropertyValueFactory<CategoryOfPayment, String>("description"));
        columnAction.setCellFactory(new Callback<TableColumn<CategoryOfPayment, String>, TableCell<CategoryOfPayment, String>>() {
            @Override
            public TableCell<CategoryOfPayment, String> call(TableColumn<CategoryOfPayment, String> param) {
                return new TableColumnAction(tableView.getItems());
            }
        });
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @FXML
    public void newCategory() {
        PaymentCategoryDataAction action = springContext.getBean(PaymentCategoryDataAction.class);
        homeAction.updateContent();
        action.newData();
    }

    @FXML
    public void loadData() {
        try {
            tableView.getItems().clear();
            windows.loading(tableView.getItems(), service.findAll(), lang.getSources(LangProperties.LIST_OF_PAYMENT_CATEGORY));
            tableView.requestFocus();
        } catch (Exception e) {
            windows.errorLoading(lang.getSources(LangProperties.LIST_OF_PAYMENT_CATEGORY), e);
            e.printStackTrace();
        }
    }

    @FXML
    private void tableViewClearSelection() {
        tableView.getSelectionModel().clearSelection();
    }

    @Autowired
    public void setBallon(DialogBalloon ballon) {
        this.ballon = ballon;
    }

    @Autowired
    public void setWindows(DialogWindows windows) {
        this.windows = windows;
    }

    @Autowired
    public void setService(ServiceOfPaymentCategory service) {
        this.service = service;
    }

    @Autowired
    public void setHomeAction(HomeAction homeAction) {
        this.homeAction = homeAction;
    }

    @Autowired
    public void setActionColumn(TableViewColumnAction actionColumn) {
        this.actionColumn = actionColumn;
    }

    @Autowired
    public void setLang(LangSource lang) {
        this.lang = lang;
    }

    private class TableColumnAction extends TableCell<CategoryOfPayment, String> {
        ObservableList<CategoryOfPayment> list;

        public TableColumnAction(ObservableList<CategoryOfPayment> list) {
            this.list = list;
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) setGraphic(null);
            else {
                CategoryOfPayment category = list.get(getIndex());
                setGraphic(actionColumn.getDefautlTableModel());
                actionColumn.getDeleteLink().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if (windows.confirmDelete(
                                lang.getSources(LangProperties.DATA_CATEGORY_OF_PAYMENT), category.getPaymentFor(),
                                lang.getSources(LangProperties.ID), category.getId()
                        ).get() == ButtonType.OK) {
                            try {
                                service.delete(category);
                                loadData();
                                ballon.sucessedRemoved(lang.getSources(LangProperties.DATA_CATEGORY_OF_PAYMENT), category.getPaymentFor());
                            } catch (Exception e) {
                                windows.errorRemoved(lang.getSources(LangProperties.DATA_CATEGORY_OF_PAYMENT), lang.getSources(LangProperties.NAME),
                                        category.getPaymentFor(), e);
                                e.printStackTrace();
                            }
                        }
                    }
                });
                actionColumn.getUpdateLink().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        PaymentCategoryDataAction action = springContext.getBean(PaymentCategoryDataAction.class);
                        homeAction.updateContent();
                        action.exitsData(category);
                    }
                });
            }
        }
    }
}
