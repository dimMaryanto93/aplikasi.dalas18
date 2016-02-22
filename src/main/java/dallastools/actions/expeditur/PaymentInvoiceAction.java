package dallastools.actions.expeditur;

import dallastools.actions.HomeAction;
import dallastools.controllers.FxInitializable;
import dallastools.controllers.NumberFormatter;
import dallastools.controllers.TableViewColumnAction;
import dallastools.controllers.dataselections.CategoryPaymentChooser;
import dallastools.controllers.notifications.DialogBalloon;
import dallastools.controllers.notifications.DialogWindows;
import dallastools.controllers.notifications.LangProperties;
import dallastools.controllers.notifications.LangSource;
import dallastools.controllers.stages.InnerScene;
import dallastools.models.expenditur.PaymentInvoice;
import dallastools.models.masterdata.CategoryOfPayment;
import dallastools.services.ServiceOfPaymentInvoice;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
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
import java.sql.Date;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by dimmaryanto on 18/10/15.
 */
public class PaymentInvoiceAction implements FxInitializable {

    @FXML
    private TableView<PaymentInvoice> tableView;
    @FXML
    private TableColumn<PaymentInvoice, Integer> columnId;
    @FXML
    private TableColumn<PaymentInvoice, Date> columnDate;
    @FXML
    private TableColumn<PaymentInvoice, String> columnCategory;
    @FXML
    private TableColumn<PaymentInvoice, String> columnAction;

    private CategoryPaymentChooser chooser;
    private TableViewColumnAction actionColumn;
    private DialogBalloon ballon;
    private DialogWindows windows;
    private ServiceOfPaymentInvoice service;
    private InnerScene innerScene;
    private HomeAction homeAction;
    private ApplicationContext springContext;
    private MessageSource messageSource;
    private LangSource lang;
    private NumberFormatter numberFormatter;

    @Override
    public void doClose() {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.springContext = applicationContext;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        columnId.setCellValueFactory(new PropertyValueFactory<PaymentInvoice, Integer>("id"));
        columnDate.setCellValueFactory(new PropertyValueFactory<PaymentInvoice, Date>("transDate"));
        columnCategory.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PaymentInvoice, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<PaymentInvoice, String> param) {
                if (param != null) {
                    CategoryOfPayment category = param.getValue().getCategory();
                    if (category != null) {
                        return new SimpleStringProperty(chooser.getKey(category));
                    } else return new SimpleStringProperty("");
                } else
                    return null;
            }
        });
        columnAction.setCellFactory(new Callback<TableColumn<PaymentInvoice, String>, TableCell<PaymentInvoice, String>>() {
            @Override
            public TableCell<PaymentInvoice, String> call(TableColumn<PaymentInvoice, String> param) {
                return new TableColumnAction(tableView.getItems());
            }
        });
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void loadData() {
        try {
            tableView.getItems().clear();
            List<PaymentInvoice> list = service.findAll();
            windows.loading(tableView.getItems(), list, lang.getSources(LangProperties.LIST_OF_OTHER_PAYMENTS));
        } catch (Exception e) {
            windows.errorLoading(lang.getSources(LangProperties.LIST_OF_OTHER_PAYMENTS), e);
            e.printStackTrace();
        }
    }

    @FXML
    public void newPayment() {
        PaymentInvoiceDataAction action = springContext.getBean(PaymentInvoiceDataAction.class);
        homeAction.updateContent();
        action.newData();
    }

    @Autowired
    public void setChooser(CategoryPaymentChooser chooser) {
        this.chooser = chooser;
    }

    @Autowired
    public void setActionColumn(TableViewColumnAction actionColumn) {
        this.actionColumn = actionColumn;
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
    public void setService(ServiceOfPaymentInvoice service) {
        this.service = service;
    }

    @Autowired
    public void setHomeAction(HomeAction homeAction) {
        this.homeAction = homeAction;
    }

    @Autowired
    public void setLang(LangSource lang) {
        this.lang = lang;
    }

    @Autowired
    public void setNumberFormatter(NumberFormatter numberFormatter) {
        this.numberFormatter = numberFormatter;
    }

    public void tableViewClearSelection() {
        tableView.getSelectionModel().clearSelection();
    }

    private class TableColumnAction extends TableCell<PaymentInvoice, String> {
        ObservableList<PaymentInvoice> list;

        public TableColumnAction(ObservableList<PaymentInvoice> list) {
            this.list = list;
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) setGraphic(null);
            else {
                PaymentInvoice invoice = list.get(getIndex());
                setGraphic(actionColumn.getDefautlTableModel());
                actionColumn.getUpdateLink().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        PaymentInvoiceDataAction action = springContext.getBean(PaymentInvoiceDataAction.class);
                        homeAction.updateContent();
                        action.exitsData(invoice);
                    }
                });
                actionColumn.getDeleteLink().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if (windows.confirmDelete(lang.getSources(LangProperties.DATA_AN_OTHER_PAYMENT),
                                invoice.getCategory().getPaymentFor(), lang.getSources(LangProperties.ID), invoice.getId()).get() == ButtonType.OK) {
                            try {
                                service.delete(invoice);
                                ballon.sucessedRemoved(lang.getSources(LangProperties.DATA_AN_OTHER_PAYMENT));
                                loadData();
                            } catch (Exception e) {
                                windows.errorRemoved(lang.getSources(LangProperties.DATA_AN_OTHER_PAYMENT), lang.getSources(LangProperties.ID), invoice.getId(), e);
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }
    }
}
