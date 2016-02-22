package dallastools.actions.expeditur;

import dallastools.actions.HomeAction;
import dallastools.controllers.FxInitializable;
import dallastools.controllers.TableViewColumnAction;
import dallastools.controllers.notifications.DialogBalloon;
import dallastools.controllers.notifications.DialogWindows;
import dallastools.controllers.notifications.LangProperties;
import dallastools.controllers.notifications.LangSource;
import dallastools.models.expenditur.DeliveryOfSales;
import dallastools.models.income.Sales;
import dallastools.models.masterdata.Employee;
import dallastools.services.ServiceOfSalesDelivery;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by dimmaryanto on 14/11/15.
 */
public class DeliverySalesAction implements FxInitializable {
    private final Logger log = LoggerFactory.getLogger(DeliverySalesAction.class);
    @FXML
    private TableView<DeliveryOfSales> tableViewDelivery;
    @FXML
    private TableColumn<DeliveryOfSales, Boolean> columnDeliveryProses;
    @FXML
    private TableColumn<DeliveryOfSales, String> columnDriver;
    @FXML
    private TableColumn<DeliveryOfSales, String> columnDeliveryId;
    @FXML
    private TableColumn columnDeliveryAction;
    @FXML
    private TableView<Sales> tableViewSales;
    @FXML
    private TableColumn<Sales, String> columnSalesId;
    @FXML
    private TableColumn<Sales, String> columnSalesCustomerPhone;
    @FXML
    private TableColumn<Sales, String> columnSalesCustomerName;
    @FXML
    private TableColumn<Sales, Boolean> columnSalesProses;
    private ApplicationContext springContext;
    private MessageSource messageSource;
    private ServiceOfSalesDelivery service;
    private DialogWindows windows;
    private DialogBalloon ballon;
    private HomeAction homeAction;
    private TableViewColumnAction actionColumn;
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
        tableViewSales.setSelectionModel(null);
        columnSalesId.setCellValueFactory(new PropertyValueFactory<Sales, String>("transId"));
        columnSalesCustomerPhone.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getCustomer().getPhone()));
        columnSalesCustomerName.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getCustomer().getCustomerName()));
        columnSalesProses.setCellValueFactory(new PropertyValueFactory<Sales, Boolean>("sent"));
        columnSalesProses.setCellFactory(new Callback<TableColumn<Sales, Boolean>, TableCell<Sales, Boolean>>() {
            @Override
            public TableCell<Sales, Boolean> call(TableColumn<Sales, Boolean> param) {
                return new CheckBoxTableCell<Sales, Boolean>() {
                    private CheckBox box;

                    @Override
                    public void updateItem(Boolean item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) setGraphic(null);
                        else {
                            box = new CheckBox(messageSource.getMessage(lang.getSources(LangProperties.NOT_YET), null, Locale.getDefault()));
                            box.setDisable(true);
                            box.setOpacity(0.9);
                            box.selectedProperty().addListener(new ChangeListener<Boolean>() {
                                @Override
                                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                                    if (newValue)
                                        box.setText(messageSource.getMessage(lang.getSources(LangProperties.BEING_PROCESSED), null, Locale.getDefault()));
                                    else
                                        box.setText(messageSource.getMessage(lang.getSources(LangProperties.NOT_YET), null, Locale.getDefault()));
                                }
                            });
                            box.setSelected(item);
                            setGraphic(box);
                        }
                    }
                };
            }
        });


        columnDeliveryAction.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn param) {
                return new TableCell() {
                    List<DeliveryOfSales> list = tableViewDelivery.getItems();

                    @Override
                    protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) setGraphic(null);
                        else {
                            DeliveryOfSales delivery = list.get(getIndex());
                            setAlignment(Pos.CENTER);
                            setGraphic(actionColumn.getDefautlTableModel());
                            actionColumn.getDeleteLink().setText(messageSource.getMessage(lang.getSources(LangProperties.DELETE), null, Locale.getDefault()));
                            actionColumn.getDeleteLink().setTextFill(Color.RED);
                            actionColumn.getDeleteLink().setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    if (windows.confirmDelete(lang.getSources(LangProperties.DATA_SALES_DELIVERY),
                                            delivery.getDeliveryId(),
                                            lang.getSources(LangProperties.DATE), delivery.getDateSent()
                                    ).get() == ButtonType.OK) {
                                        try {
                                            if (!delivery.getStatus()) {
                                                service.updateSalesFromDelivery(delivery);
                                                service.delete(delivery);
                                                service.deleteDeliveryOfSales(delivery);
                                                ballon.sucessedRemoved(lang.getSources(LangProperties.DATA_SALES_DELIVERY), delivery.getDeliveryId());
                                                loadData();
                                            } else {
                                                ballon.warningCantRemovedSendMessage(lang.getSources(LangProperties.DATA_SALES_DELIVERY), delivery.getDeliveryId());
                                            }
                                        } catch (Exception e) {
                                            windows.errorRemoved(lang.getSources(LangProperties.DATA_SALES_DELIVERY), lang.getSources(LangProperties.ID), delivery.getDeliveryId(), e);
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                            actionColumn.getUpdateLink().setText(messageSource.getMessage(lang.getSources(LangProperties.PROCESS), null, Locale.getDefault()));
                            actionColumn.getUpdateLink().setTextFill(Color.YELLOWGREEN);
                            actionColumn.getUpdateLink().setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    if (!delivery.getStatus()) {
                                        DeliverySalesDataAction action = springContext.getBean(DeliverySalesDataAction.class);
                                        action.forPayment(delivery);
                                        homeAction.updateContent();
                                    } else {
                                        ballon.warningHasBeenProcessed(lang.getSources(LangProperties.DATA_SALES_DELIVERY), delivery.getDeliveryId());
                                    }
                                }
                            });
                        }
                    }
                };
            }
        });
        columnDriver.setCellValueFactory(param -> {
            if (param != null) {
                Employee anEmployee = param.getValue().getEmployee();
                if (anEmployee != null) {
                    return new SimpleStringProperty(anEmployee.getEmployeeName());
                } else return new SimpleStringProperty();
            } else return null;
        });
        columnDeliveryId.setCellValueFactory(new PropertyValueFactory<DeliveryOfSales, String>("deliveryId"));
        columnDeliveryProses.setCellValueFactory(new PropertyValueFactory<DeliveryOfSales, Boolean>("status"));
        columnDeliveryProses.setCellFactory(new Callback<TableColumn<DeliveryOfSales, Boolean>, TableCell<DeliveryOfSales, Boolean>>() {
            @Override
            public TableCell<DeliveryOfSales, Boolean> call(TableColumn<DeliveryOfSales, Boolean> param) {
                return new TableCell<DeliveryOfSales, Boolean>() {
                    @Override
                    protected void updateItem(Boolean item, boolean empty) {

                        super.updateItem(item, empty);
                        if (empty) setGraphic(null);
                        else {
                            Label text = new Label();
                            FontAwesomeIconView icon = new FontAwesomeIconView();
                            icon.setFont(new Font("FontAwesome", 14));
                            if (item) {
                                icon.setIcon(FontAwesomeIcon.CHECK);
                                text.setText(messageSource.getMessage(lang.getSources(LangProperties.HAS_BEEN_PROCESSED), null, Locale.getDefault()));
                            } else {
                                text.setText(messageSource.getMessage(lang.getSources(LangProperties.ON_PROGRESS), null, Locale.getDefault()));
                                icon.setIcon(FontAwesomeIcon.HOURGLASS_ALT);
                            }
                            text.setGraphic(icon);
                            setGraphic(text);
                        }
                    }
                };
            }
        });
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private Task<Object> getWorker() {
        return new Task<Object>() {

            private final Integer PROGRESS_INDICATOR = 10;
            private final Integer PROGRESS_SUCCESSED = 500;

            private Integer workDone;
            private Integer workMax;

            public void setWorkMax(Integer workMax) {
                this.workMax = workMax;
            }

            public void setWorkDone(Integer workDone) {
                this.workDone = workDone;
            }

            private void loadSales() throws Exception {
                tableViewSales.getItems().clear();
                //log.info("Bersihkan data table income");
                List<Sales> list = service.findSalesReadyForDelivery();
                setWorkMax(list.size());
                //log.info("Data income ditemukan sebanyak {} data", list.size());
                for (int i = 0; i < workMax; i++) {
                    setWorkDone(i);
                    updateProgress(workDone, workMax - 1);
                    updateMessage(messageSource.getMessage(lang.getSources(LangProperties.PROGRESS_GETTING_WITH_PARAMS),
                            new Object[]{workDone, workMax}, Locale.getDefault()));
                    Sales sales = list.get(workDone);
                    tableViewSales.getItems().add(sales);
                    Thread.sleep(PROGRESS_INDICATOR);
                }
                succeeded();
            }

            private void loadDelivery() throws Exception {
                tableViewDelivery.getItems().clear();
                //log.info("Bersihkan data table delivery");
                List<DeliveryOfSales> list = service.findAllDelivery();
                //log.info("Data Pengiriman Penjualan ditemukan sebanyak {} data", list.size());
                setWorkMax(list.size());
                for (int i = 0; i < workMax; i++) {
                    setWorkDone(i);
                    updateProgress(workDone, workMax - 1);
                    updateMessage(messageSource.getMessage(lang.getSources(LangProperties.PROGRESS_GETTING_WITH_PARAMS),
                            new Object[]{workDone, workMax}, Locale.getDefault()));
                    DeliveryOfSales delivery = list.get(workDone);
                    tableViewDelivery.getItems().add(delivery);
                    Thread.sleep(PROGRESS_INDICATOR);
                }
                succeeded();
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
                    Thread.sleep(PROGRESS_SUCCESSED);
                    super.succeeded();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected Object call() throws Exception {
                loadSales();
                loadDelivery();
                return null;
            }
        };
    }

    public void loadData() {
        try {
            windows.loading(getWorker(), lang.getSources(LangProperties.LIST_OF_SALES_DELIVERIES));
        } catch (Exception e) {
            windows.errorLoading(lang.getSources(LangProperties.LIST_OF_SALES_DELIVERIES), e);
            e.printStackTrace();
        }
    }

    @Autowired
    public void setService(ServiceOfSalesDelivery service) {
        this.service = service;
    }

    @Autowired
    public void setWindows(DialogWindows windows) {
        this.windows = windows;
    }

    @Autowired
    public void setBallon(DialogBalloon ballon) {
        this.ballon = ballon;
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

    @FXML
    public void newDelivery() {
        DeliverySalesDataAction action = springContext.getBean(DeliverySalesDataAction.class);
        homeAction.updateContent();
        ObservableList<Sales> list = FXCollections.observableArrayList();
        for (Sales sales : tableViewSales.getItems()) {
            if (!sales.getSent())
                list.add(sales);
            //else log.info("transaksi {} tidak akan ditambahkan ke pengiriman", sales.getTransId());
        }
        action.newData(list);
    }

    @FXML
    private void tableDeliveryClearSelection() {
        tableViewDelivery.getSelectionModel().clearSelection();
    }
}
