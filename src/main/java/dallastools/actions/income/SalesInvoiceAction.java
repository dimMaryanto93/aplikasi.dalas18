package dallastools.actions.income;

import dallastools.actions.HomeAction;
import dallastools.controllers.FxInitializable;
import dallastools.controllers.TableViewColumnAction;
import dallastools.controllers.notifications.*;
import dallastools.models.income.Sales;
import dallastools.models.masterdata.Customer;
import dallastools.services.ServiceOfSalesInvoice;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by dimmaryanto on 27/10/15.
 */
public class SalesInvoiceAction implements FxInitializable {

    private final Logger log = LoggerFactory.getLogger(SalesInvoiceAction.class);
    @FXML
    public TableColumn<Sales, Boolean> columnRange;
    @FXML
    private TableView<Sales> tableView;
    @FXML
    private TableColumn<Sales, Integer> columnId;
    @FXML
    private TableColumn<Sales, Date> columnDate;
    @FXML
    private TableColumn<Sales, String> columnCustomerPhone;
    @FXML
    private TableColumn<Sales, String> columnCustomerName;
    @FXML
    private TableColumn<Sales, Boolean> columnSent;
    @FXML
    private TableColumn<Sales, Boolean> columnRecieved;
    @FXML
    private TableColumn<Sales, Boolean> columnPaid;
    @FXML
    private TableColumn<Sales, String> columnAction;
    private HomeAction homeAction;
    private DialogBalloon ballon;
    private DialogWindows windows;
    private ApplicationContext springContext;
    private MessageSource messageSource;
    private ServiceOfSalesInvoice serviceOfSales;
    private TableViewColumnAction actionColumn;
    private ActionMessages actionMessages;
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
        columnId.setCellValueFactory(new PropertyValueFactory<Sales, Integer>("id"));
        columnId.setCellFactory(param1 -> new TableCell<Sales, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setAlignment(Pos.CENTER);
                if (empty) setText(null);
                else setText(item.toString());
            }
        });
        columnDate.setCellValueFactory(new PropertyValueFactory<Sales, Date>("dateTransaction"));
        columnCustomerName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Sales, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Sales, String> param) {
                if (param != null) {
                    Customer aCustomer = param.getValue().getCustomer();
                    if (aCustomer != null) {
                        return new SimpleStringProperty(aCustomer.getCustomerName());
                    } else return new SimpleStringProperty("");
                } else
                    return null;
            }
        });
        columnCustomerPhone.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Sales, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Sales, String> param) {
                if (param != null) {
                    Customer aCustomer = param.getValue().getCustomer();
                    if (aCustomer != null) {
                        return new SimpleStringProperty(aCustomer.getPhone());
                    } else return new SimpleStringProperty("");
                } else
                    return null;
            }
        });
        columnSent.setCellValueFactory(new PropertyValueFactory<Sales, Boolean>("sent"));
        columnSent.setCellFactory(param -> new TableColumnCheckboxRendered());
        columnRecieved.setCellValueFactory(new PropertyValueFactory<Sales, Boolean>("recieved"));
        columnRecieved.setCellFactory(param -> new TableColumnCheckboxRendered());
        columnPaid.setCellValueFactory(new PropertyValueFactory<Sales, Boolean>("paid"));
        columnPaid.setCellFactory(param -> new TableColumnCheckboxRendered());
        columnAction.setCellFactory(param -> new TableColumnAction(tableView.getItems()));
        columnRange.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Sales, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Sales, Boolean> param) {
                if (param != null) {
                    Sales aSales = param.getValue();
                    return new SimpleObjectProperty<Boolean>(isExpired(1, aSales));
                } else
                    return null;
            }
        });
        columnRange.setCellFactory(new Callback<TableColumn<Sales, Boolean>, TableCell<Sales, Boolean>>() {
            @Override
            public TableCell<Sales, Boolean> call(TableColumn<Sales, Boolean> param) {
                return new TableColumnExpired(tableView.getItems());
            }
        });
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @FXML
    public void tableViewClearSelection() {
        tableView.getSelectionModel().clearSelection();
    }

    @FXML
    public void newSales() {
        SalesInvoiceDataAction action = springContext.getBean(SalesInvoiceDataAction.class);
        homeAction.updateContent();
        action.newData();
    }

    public boolean isExpired(Integer plusMonth, Sales sales) {
        Boolean value = LocalDate.now().isBefore(sales.getDateTransaction().toLocalDate().plusMonths(plusMonth)) || sales.getPaid();
        log.info("sales id ({}) date value {} is expired {}", new Object[]{sales.getTransId(), sales.getDateTransaction(), value});
        return value;
    }


    private Task<Integer> getWorker() {
        return new Task<Integer>() {

            private final Integer INDICATOR_SUCCEEDED = 250;
            private final Integer INDICATOR_PROGRESSED = 50;

            private Integer workDone;
            private Integer workMax;
            private Integer data = 0;

            public void setData(Integer data) {
                this.data = data;
            }

            private void setWorkDone(Integer workDone) {
                this.workDone = workDone;
            }

            private void setWorkMax(Integer workMax) {
                this.workMax = workMax;
            }

            @Override
            protected void succeeded() {
                try {
                    setWorkMax(100);
                    for (int i = 0; i < workMax; i++) {
                        setWorkDone(i);
                        updateProgress(workDone, workMax - 1);
                        updateMessage(messageSource.getMessage(lang.getSources(LangProperties.PROGRESS_FINISHED_WITH_PARAM),
                                new Object[]{workDone}, Locale.getDefault()));
                        Thread.sleep(10);
                    }
                    Thread.sleep(INDICATOR_SUCCEEDED);
                    super.succeeded();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            private Integer loadData() {
                try {
                    tableView.getItems().clear();
                    List<Sales> list = serviceOfSales.findAllSales();
                    setWorkMax(list.size());
                    Integer expiredDateFaund = 0;
                    for (int i = 0; i < workMax; i++) {
                        setWorkDone(i);
                        updateProgress(workDone, workMax - 1);
                        updateMessage(messageSource.getMessage(lang.getSources(LangProperties.PROGRESS_GETTING_WITH_PARAMS),
                                new Object[]{workDone, workMax}, Locale.getDefault()));

                        Sales sales = list.get(workDone);
                        tableView.getItems().add(sales);

                        LocalDate expiredDate = sales.getDateTransaction().toLocalDate().plusMonths(1);
                        Double paid = sales.getGrantTotal() - sales.getAmmount();
                        if (paid >= 1 && LocalDate.now().isAfter(expiredDate)) {
                            expiredDateFaund += 1;
                            setData(expiredDateFaund);
                        }
                        Thread.sleep(INDICATOR_PROGRESSED);
                    }

                    return expiredDateFaund;
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            }

            @Override
            protected Integer call() throws Exception {
                loadData();
                succeeded();
                return data;
            }
        };
    }

    @FXML
    public void loadData() {
        try {
            tableView.getItems().clear();
            Task<Integer> task = getWorker();
            windows.loading(task, lang.getSources(LangProperties.LIST_OF_SALES));
            task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    Integer value = task.getValue();
                    if (value >= 1) {
                        ballon.warningExpiredMessage(lang.getSources(LangProperties.LIST_OF_SALES), value);
                    }
                }
            });
        } catch (Exception e) {
            windows.errorLoading(lang.getSources(LangProperties.LIST_OF_SALES), e);
            e.printStackTrace();
        }
    }

    @Autowired
    public void setServiceOfSales(ServiceOfSalesInvoice serviceOfSales) {
        this.serviceOfSales = serviceOfSales;
    }

    @Autowired
    public void setActionColumn(TableViewColumnAction actionColumn) {
        this.actionColumn = actionColumn;
    }

    @Autowired
    public void setHomeAction(HomeAction homeAction) {
        this.homeAction = homeAction;
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
    public void setActionMessages(ActionMessages actionMessages) {
        this.actionMessages = actionMessages;
    }

    @Autowired
    public void setLang(LangSource lang) {
        this.lang = lang;
    }

    private class TableColumnCheckboxRendered extends TableCell<Sales, Boolean> {
        @Override
        protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) setGraphic(null);
            else {
                CheckBox box = new CheckBox(actionMessages.unchecked());
                box.selectedProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        if (newValue) {
                            box.setText(messageSource.getMessage(lang.getSources(LangProperties.YES), null, Locale.getDefault()));
                        } else {
                            box.setText(messageSource.getMessage(lang.getSources(LangProperties.NOT_YET), null, Locale.getDefault()));
                        }
                    }
                });
                box.setSelected(item);
                box.setDisable(true);
                box.setOpacity(0.9);
                setGraphic(box);
            }
        }
    }

    private class TableColumnAction extends TableCell<Sales, String> {
        ObservableList<Sales> list;

        public TableColumnAction(ObservableList<Sales> list) {
            this.list = list;
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) setGraphic(null);
            else {
                Sales aSales = list.get(getIndex());
                setGraphic(actionColumn.getMasterDetailTableModel());
                actionColumn.getUpdateLink().setText(actionMessages.payoff());
                actionColumn.getUpdateLink().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if (aSales.getPaid()) {
                            ballon.warningPaidMessage(lang.getSources(LangProperties.DATA_A_SALES), aSales.getTransId());
                        } else {
                            SalesInvoiceDataAction action = springContext.getBean(SalesInvoiceDataAction.class);
                            homeAction.updateContent();
                            action.exitsData(aSales);
                        }
                    }
                });
                actionColumn.getDeleteLink().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        try {
                            if (windows.confirmDelete(lang.getSources(LangProperties.DATA_A_SALES), aSales.getDateTransaction().toString(),
                                    lang.getSources(LangProperties.ID), aSales.getTransId()).get() == ButtonType.OK) {
                                if (!aSales.getSent()) {
                                    serviceOfSales.updateItemBeforeUpdatedOfDelete(aSales, true);
                                    serviceOfSales.delete(aSales);
                                    loadData();
                                    ballon.sucessedRemoved(lang.getSources(LangProperties.DATA_A_SALES), aSales.getTransId());
                                } else {
                                    ballon.warningCantRemovedSendMessage(lang.getSources(LangProperties.DATA_A_SALES), aSales.getTransId());
                                }
                            }
                        } catch (Exception e) {
                            windows.errorRemoved(lang.getSources(LangProperties.DATA_A_SALES), lang.getSources(LangProperties.ID), aSales.getTransId(), e);
                            e.printStackTrace();
                        }
                    }
                });
                actionColumn.getDetailLink().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        SalesInvoiceDataAction action = springContext.getBean(SalesInvoiceDataAction.class);
                        homeAction.updateContent();
                        action.readOnly(aSales);
                    }
                });
            }
        }
    }

    private class TableColumnExpired extends TableCell<Sales, Boolean> {
        List<Sales> list;

        public TableColumnExpired(List<Sales> list) {
            this.list = list;
        }

        @Override
        protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) setGraphic(null);
            else {
                Label text = new Label();
                FontAwesomeIconView icon;
                Sales aSales = list.get(getIndex());
                if (!item) {
                    icon = new FontAwesomeIconView(FontAwesomeIcon.REMOVE);
                    text.setText(messageSource.getMessage(lang.getSources(LangProperties.YES), null, Locale.getDefault()));
                    text.setGraphic(icon);
                } else {
                    if (aSales.getPaid()) {
                        icon = new FontAwesomeIconView(FontAwesomeIcon.BELL_SLASH_ALT);
                        text.setText(actionMessages.not());
                        text.setGraphic(icon);
                    } else {
                        icon = new FontAwesomeIconView(FontAwesomeIcon.BELL_ALT);
                        text.setText(actionMessages.unchecked());
                        text.setGraphic(icon);
                    }
                }
                setGraphic(text);
            }
        }
    }
}
