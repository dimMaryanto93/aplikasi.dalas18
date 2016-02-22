package dallastools.actions.income;

import dallastools.actions.HomeAction;
import dallastools.controllers.FxInitializable;
import dallastools.controllers.TableViewColumnAction;
import dallastools.controllers.notifications.DialogBalloon;
import dallastools.controllers.notifications.DialogWindows;
import dallastools.controllers.notifications.LangProperties;
import dallastools.controllers.notifications.LangSource;
import dallastools.models.income.SalesOrder;
import dallastools.models.income.SalesOrderDetails;
import dallastools.models.masterdata.Customer;
import dallastools.models.masterdata.Item;
import dallastools.services.ServiceOfItem;
import dallastools.services.ServiceOfSalesOrder;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Callback;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import java.net.URL;
import java.sql.Date;
import java.util.*;

/**
 * Created by dimmaryanto on 12/10/15.
 */
public class SalesOrderAction implements FxInitializable {

    public List<Item> listItems;
    @FXML
    private TableView<SalesOrder> tableView;
    @FXML
    private TableColumn<SalesOrder, String> columnDoSell;
    @FXML
    private TableColumn<SalesOrder, String> columnCustomerContact;
    @FXML
    private TableColumn<SalesOrder, Integer> columnId;
    @FXML
    private TableColumn<SalesOrder, Date> columnOrder;
    @FXML
    private TableColumn<SalesOrder, String> columnCustomerName;
    @FXML
    private TableColumn columnAction;
    private TableViewColumnAction actionColumn;
    private DialogWindows windows;
    private DialogBalloon ballon;
    private ServiceOfSalesOrder service;
    private ServiceOfItem serviceOfItem;
    private HomeAction homeAction;
    private ApplicationContext springContext;
    private MessageSource messageSource;
    private LangSource lang;
    private HashMap<SalesOrder, List<SalesOrderDetails>> salesOrderHashMap;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.listItems = new ArrayList<>();
        this.salesOrderHashMap = new HashMap<>();

        columnId.setCellValueFactory(new PropertyValueFactory<SalesOrder, Integer>("id"));
        columnId.setCellFactory(param -> new TableCell<SalesOrder, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setAlignment(Pos.CENTER);
                if (empty) setText(null);
                else setText(item.toString());
            }
        });
        columnOrder.setCellValueFactory(new PropertyValueFactory<SalesOrder, Date>("orderDate"));
        columnCustomerName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SalesOrder, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<SalesOrder, String> param) {
                if (param != null) {
                    // untuk menampilkan data customer hanya customer namenya saja tidak termasuk class loadernya
                    if (param.getValue().getCustomer() != null)
                        return new SimpleStringProperty(param.getValue().getCustomer().getCustomerName());
                    else return new SimpleStringProperty("-");

                } else
                    return null;
            }
        });
        columnCustomerContact.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SalesOrder, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<SalesOrder, String> param) {
                if (param != null) {
                    if (param.getValue().getCustomer() != null) {
                        Customer aCustomer = param.getValue().getCustomer();
                        return new SimpleStringProperty(aCustomer.getPhone());
                    } else return new SimpleStringProperty("-");
                } else
                    return null;
            }
        });
        columnDoSell.setCellFactory(new Callback<TableColumn<SalesOrder, String>, TableCell<SalesOrder, String>>() {
            @Override
            public TableCell<SalesOrder, String> call(TableColumn<SalesOrder, String> param) {
                return new TableColumnActionToSell(tableView.getItems());
            }
        });
        columnAction.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn param) {
                return new TableColumnAction(tableView.getItems());
            }
        });
    }

    private Task<Object> getWorker() {
        return new Task<Object>() {


            private Integer workDone;
            private Integer workMax;
            private List<Item> items = new ArrayList<>();
            private List<SalesOrderDetails> salesOrderDetails = new ArrayList<>();
            private List<SalesOrder> salerOrders = new ArrayList<>();

            public void setWorkMax(Integer workMax) {
                this.workMax = workMax;
            }

            public void setWorkDone(Integer workDone) {
                this.workDone = workDone;
            }

            private void loadItems() throws Exception {
                this.items.clear();
                items = serviceOfItem.findAll();
                setWorkMax(items.size());
                for (int i = 0; i < workMax; i++) {
                    setWorkDone(i);
                    listItems.add(items.get(workDone));
                    updateProgress(workDone, workDone - 1);
                    updateMessage(messageSource.getMessage(lang.getSources(LangProperties.PROGRESS_GETTING_WITH_PARAMS),
                            new Object[]{workDone, workMax}, Locale.getDefault()));
                    Thread.sleep(10);
                }
                succeeded();
            }

            private void loadSalesOrder() throws Exception {
                salesOrderHashMap.clear();
                this.salerOrders.clear();
                salerOrders = service.findAllSelesOrder();
                setWorkMax(salerOrders.size());
                for (int i = 0; i < workMax; i++) {
                    setWorkDone(i);
                    loadSalesOrderDetails(salerOrders.get(workDone));
                    tableView.getItems().add(salerOrders.get(workDone));

                    updateProgress(workDone, workMax - 1);
                    updateMessage(messageSource.getMessage(lang.getSources(LangProperties.PROGRESS_GETTING_WITH_PARAMS),
                            new Object[]{workDone, workMax}, Locale.getDefault()));
                    Thread.sleep(10);
                }
                succeeded();
            }

            private void loadSalesOrderDetails(SalesOrder salesOrder) throws Exception {
                List<SalesOrderDetails> list = service.findAllSalesOrderDetails(salesOrder);
                salesOrderHashMap.put(salesOrder, list);
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
                        Thread.sleep(15);
                    }
                    super.succeeded();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected Object call() throws Exception {
                loadItems();
                loadSalesOrder();
                return null;
            }
        };
    }

    @FXML
    public void loadData() {
        try {
            tableView.getItems().clear();
            windows.loading(getWorker(), lang.getSources(LangProperties.LIST_OF_SALES_ORDER));
        } catch (Exception e) {
            windows.errorLoading(lang.getSources(LangProperties.LIST_OF_SALES_ORDER), e);
            e.printStackTrace();
        }
    }

    @FXML
    public void newSalesOrder() {
        SalesOrderDataAction action = springContext.getBean(SalesOrderDataAction.class);
        homeAction.updateContent();
        action.newData();
    }

    @FXML
    public void newSales() {
        SalesInvoiceDataAction action = springContext.getBean(SalesInvoiceDataAction.class);
        homeAction.updateContent();
        action.newData();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.springContext = applicationContext;
    }

    @Override
    public void doClose() {

    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Autowired
    public void setActionColumn(TableViewColumnAction actionColumn) {
        this.actionColumn = actionColumn;
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
    public void setService(ServiceOfSalesOrder service) {
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
    public void setServiceOfItem(ServiceOfItem serviceOfItem) {
        this.serviceOfItem = serviceOfItem;
    }

    @FXML
    public void tableViewClearSelection() {
        tableView.getSelectionModel().clearSelection();
    }

    private class TableColumnAction extends TableCell<SalesOrder, String> {
        private ObservableList<SalesOrder> list;

        public TableColumnAction(ObservableList<SalesOrder> list) {
            this.list = list;
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                SalesOrder order = list.get(getIndex());
                setGraphic(actionColumn.getMasterDetailTableModel());
                actionColumn.getDeleteLink().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if (windows.confirmDelete(lang.getSources(LangProperties.DATA_A_SALES_ORDER), order.getOrderDate(),
                                lang.getSources(LangProperties.ID), order.getTransId())
                                .get() == ButtonType.OK) {
                            try {
                                service.delete(order);
                                loadData();
                                ballon.sucessedRemoved(lang.getSources(LangProperties.DATA_A_SALES_ORDER), order.getTransId());
                            } catch (Exception e) {
                                windows.errorRemoved(lang.getSources(LangProperties.DATA_A_SALES_ORDER), lang.getSources(LangProperties.ID), order.getTransId(), e);
                                e.printStackTrace();
                            }
                        }
                    }
                });
                actionColumn.getDetailLink().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        SalesOrderDataAction action = springContext.getBean(SalesOrderDataAction.class);
                        homeAction.updateContent();
                        action.exitsData(order, false);
                    }
                });
                actionColumn.getUpdateLink().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        SalesOrderDataAction action = springContext.getBean(SalesOrderDataAction.class);
                        homeAction.updateContent();
                        action.exitsData(order, true);
                    }
                });

            }
        }
    }

    private class TableColumnActionToSell extends TableCell<SalesOrder, String> {
        ObservableList<SalesOrder> list;

        public TableColumnActionToSell(ObservableList<SalesOrder> list) {
            this.list = list;
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            setAlignment(Pos.CENTER_LEFT);
            if (empty)
                setGraphic(null);
            else {
                SalesOrder anOrder = list.get(getIndex());
                List<SalesOrderDetails> list = salesOrderHashMap.get(anOrder);

                Boolean enough = true;
                for (int i = 0; i < list.size(); i++) {
                    SalesOrderDetails salesOrderDetails = list.get(i);
                    for (int j = 0; j < listItems.size(); j++) {
                        Item itemAvailable = listItems.get(j);
                        Item itemCompare = salesOrderDetails.getItem();
                        if (itemAvailable.getItemGenerator() == itemCompare.getItemGenerator()) {
                            if (itemAvailable.getQty() < salesOrderDetails.getQty())
                                enough = false;
                            break;
                        }
                    }
                }
                FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.CHECK);
                icon.setFont(new Font("FontAwesome", 18));
                if (enough) {
                    setGraphic(actionColumn.getSingleHyperlinkTableModel(lang.getSources(LangProperties.READY_FOR_SALE)));
                    actionColumn.getDeleteLink().setTextFill(Color.BLACK);
                    actionColumn.getDeleteLink().setGraphic(icon);
                    actionColumn.getDeleteLink().setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            SalesInvoiceDataAction action = springContext.getBean(SalesInvoiceDataAction.class);
                            homeAction.updateContent();
                            action.convertFromSalesOrder(anOrder);
                        }
                    });
                } else {
                    Label label = new Label(messageSource.getMessage(lang.getSources(LangProperties.NOT_READY), null, Locale.getDefault()));
                    icon.setIcon(FontAwesomeIcon.HOURGLASS_ALT);
                    label.setTextFill(Color.RED);
                    label.setGraphic(icon);
                    setGraphic(label);
                }
            }
        }
    }
}
