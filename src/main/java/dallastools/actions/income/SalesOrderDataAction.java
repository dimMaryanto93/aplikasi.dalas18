package dallastools.actions.income;

import dallastools.actions.HomeAction;
import dallastools.controllers.FxInitializable;
import dallastools.controllers.NumberFormatter;
import dallastools.controllers.PrintController;
import dallastools.controllers.TableViewColumnAction;
import dallastools.controllers.dataselections.CustomerChooser;
import dallastools.controllers.notifications.*;
import dallastools.models.income.SalesOrder;
import dallastools.models.income.SalesOrderDetails;
import dallastools.models.masterdata.Customer;
import dallastools.models.masterdata.Item;
import dallastools.models.masterdata.Unit;
import dallastools.services.ServiceOfCustomer;
import dallastools.services.ServiceOfSalesOrder;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import java.net.URL;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by dimmaryanto on 12/10/15.
 */
public class SalesOrderDataAction implements FxInitializable {
    @FXML
    private Button btnAction;
    @FXML
    private Button btnAddCustomer;
    @FXML
    private Button btnAddItem;
    @FXML
    private DatePicker txtOrderDate;
    @FXML
    private ComboBox<String> txtCostomer;
    @FXML
    private TableView<SalesOrderDetails> tableView;
    @FXML
    private TableColumn<SalesOrderDetails, String> columnItemId;
    @FXML
    private TableColumn<SalesOrderDetails, String> columnAction;
    @FXML
    private TableColumn<SalesOrderDetails, String> columnProductName;
    @FXML
    private TableColumn<SalesOrderDetails, String> columnItemUnit;
    @FXML
    private TableColumn<SalesOrderDetails, Integer> columnQty;
    @FXML
    private TableColumn<SalesOrderDetails, Double> columnPrice;
    @FXML
    private TableColumn<SalesOrderDetails, Double> columnSubtotal;
    @FXML
    private TextField txtTotal;
    @FXML
    private Spinner<Double> txtDownPayment;
    @FXML
    private TextField txtGrantTotal;

    private ServiceOfSalesOrder service;
    private ServiceOfCustomer customerService;
    private DialogBalloon ballon;
    private DialogWindows windows;
    private HomeAction homeAction;
    private TableViewColumnAction actionColumn;
    private HashMap<String, Customer> customerMap;
    private CustomerChooser chooser;
    private PrintController print;
    private ApplicationContext springContext;
    private MessageSource messageSource;
    private SalesOrder order;
    private Boolean isUpdate;
    private ValidationSupport validator;
    private SpinnerValueFactory.DoubleSpinnerValueFactory doubleSpinnerValueFactory;
    private Logger log;
    private ValidatorMessages validatorMessages;
    private LangSource lang;
    private NumberFormatter numberFormatter;

    private void setUpdate(Boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    private void initValidator() {
        this.validator = new ValidationSupport();
        this.validator.registerValidator(txtCostomer, true,
                Validator.createEmptyValidator(validatorMessages.validatorNotSelected(lang.getSources(LangProperties.CUSTOMER)), Severity.ERROR));
        this.validator.registerValidator(txtOrderDate, (Control order, LocalDate date) ->
                ValidationResult.fromWarningIf(order,
                        validatorMessages.validatorDateNotEqualsNow(), !LocalDate.now().equals(date)));
        this.validator.invalidProperty().addListener((observable, oldValue, newValue) -> btnAction.setDisable(newValue));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tableView.setSelectionModel(null);

        this.log = LoggerFactory.getLogger(SalesOrderAction.class);

        this.txtGrantTotal.setAlignment(Pos.CENTER_RIGHT);
        this.txtTotal.setAlignment(Pos.CENTER_RIGHT);
        this.doubleSpinnerValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 0, 0, 500);
        this.btnAction.setDisable(true);

        this.doubleSpinnerValueFactory.valueProperty().addListener(new ChangeListener<Double>() {
            @Override
            public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
                Double ammout = getAmmount() - newValue;
                txtGrantTotal.setText(ammout.toString());
            }
        });
        tableView.getItems().addListener(new ListChangeListener<SalesOrderDetails>() {
            @Override
            public void onChanged(Change<? extends SalesOrderDetails> c) {
                Double ammount = getAmmount();
                Double downpayment = txtDownPayment.getValueFactory().getValue();
                doubleSpinnerValueFactory.setMin(0.0);
                doubleSpinnerValueFactory.setMax(ammount);
                txtTotal.setText(numberFormatter.getCurrency(ammount));
                Double result = ammount - downpayment;
                txtGrantTotal.setText(numberFormatter.getCurrency(result));
            }
        });
        columnItemId.setCellValueFactory(param1 -> {
            if (param1 != null) {
                Item anItem = param1.getValue().getItem();
                if (anItem != null) {
                    return new SimpleStringProperty(anItem.getId());
                } else return new SimpleStringProperty();
            } else return null;
        });
        txtOrderDate.setValue(LocalDate.now());
        txtDownPayment.getEditor().setAlignment(Pos.CENTER_RIGHT);
        txtDownPayment.setValueFactory(doubleSpinnerValueFactory);
        txtDownPayment.setEditable(true);
        columnAction.setCellFactory(new Callback<TableColumn<SalesOrderDetails, String>, TableCell<SalesOrderDetails, String>>() {
            @Override
            public TableCell<SalesOrderDetails, String> call(TableColumn<SalesOrderDetails, String> param) {
                return new TableColumnAction(tableView.getItems());
            }
        });
        columnProductName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SalesOrderDetails, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<SalesOrderDetails, String> param) {
                if (param != null) {
                    return new SimpleStringProperty(param.getValue().getItem().getName());
                } else
                    return null;
            }
        });
        columnItemUnit.setCellValueFactory(param -> {
            if (param != null) {
                Item anItem = param.getValue().getItem();
                if (anItem != null) {
                    Unit unit = anItem.getUnit();
                    if (unit != null) {
                        return new SimpleStringProperty(unit.getId());
                    } else return new SimpleStringProperty();
                } else return null;
            }
            return null;
        });
        columnItemUnit.setCellFactory(param -> new TableCell<SalesOrderDetails, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setAlignment(Pos.CENTER);
                if (empty) setText(null);
                else setText(item);
            }
        });
        columnQty.setCellValueFactory(new PropertyValueFactory<SalesOrderDetails, Integer>("qty"));
        columnQty.setCellFactory(param -> new TableCell<SalesOrderDetails, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setAlignment(Pos.CENTER);
                if (empty) setText("");
                else setText(item.toString());
            }
        });
        columnPrice.setCellValueFactory(param -> {
            if (param != null) {
                Item anItem = param.getValue().getItem();
                if (anItem != null) {
                    return new SimpleObjectProperty<Double>(anItem.getPriceSell());
                } else return new SimpleObjectProperty<Double>(0.0);
            } else return null;
        });
        columnPrice.setCellFactory(param -> new TableCell<SalesOrderDetails, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setAlignment(Pos.CENTER_RIGHT);
                if (empty) setText("");
                else setText(numberFormatter.getCurrency(item));
            }
        });
        columnSubtotal.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SalesOrderDetails, Double>, ObservableValue<Double>>() {
            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<SalesOrderDetails, Double> param) {
                if (param != null) {
                    Double price = param.getValue().getItem().getPriceSell();
                    Double qty = Double.valueOf(param.getValue().getQty());
                    return new SimpleObjectProperty<Double>(price * qty);
                } else
                    return null;
            }
        });
        columnSubtotal.setCellFactory(param -> new TableCell<SalesOrderDetails, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setAlignment(Pos.CENTER_RIGHT);
                if (empty) setText(null);
                else setText(numberFormatter.getCurrency(item));
            }
        });
    }


    @FXML
    public void doSave() {
        if (tableView.getItems().size() > 0) {
            order.setOrderDate(Date.valueOf(txtOrderDate.getValue()));
            order.setCustomer(customerMap.get(txtCostomer.getValue()));
            order.setDownPayment(txtDownPayment.getValueFactory().getValue());
            order.setChecklist(false);
            if (isUpdate) {
                try {
                    order.setLastUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
                    service.update(order, tableView.getItems());
                    print.showSalesOrder(messageSource.getMessage(lang.getSources(LangProperties.DATA_A_SALES_ORDER), null, Locale.getDefault()),
                            order, tableView.getItems(), homeAction.getAccount());
                    doClose();
                    ballon.sucessedUpdated(lang.getSources(LangProperties.DATA_A_SALES_ORDER), lang.getSources(LangProperties.ID), order.getTransId());
                } catch (Exception e) {
                    windows.errorUpdate(lang.getSources(LangProperties.DATA_A_SALES_ORDER), lang.getSources(LangProperties.ID), order.getTransId(), e);
                    e.printStackTrace();
                }
            } else {
                try {
                    order.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
                    order.setTransId("-");
                    Integer sequanceValue = service.save(order, tableView.getItems());
                    order.setTransId(getTransId(sequanceValue, order.getCustomer().getId()));
                    service.update(order);
                    print.showSalesOrder(messageSource.getMessage(lang.getSources(LangProperties.DATA_A_SALES_ORDER), null, Locale.getDefault()),
                            order, tableView.getItems(), homeAction.getAccount());
                    ballon.sucessedSave(lang.getSources(LangProperties.DATA_A_SALES_ORDER), order.getTransId());
                    newData();
                } catch (Exception e) {
                    windows.errorSave(lang.getSources(LangProperties.DATA_A_SALES_ORDER), order.getTransId(), e);
                    e.printStackTrace();
                }
            }
        } else {
            ballon.warningEmptyMessage(lang.getSources(LangProperties.DATA_A_SALES_ORDER), lang.getSources(LangProperties.LIST_OF_ITEMS));
        }
    }

    @FXML
    public void doClose() {
        HomeAction homeAction = springContext.getBean(HomeAction.class);
        homeAction.showSalesOrder();
    }

    @FXML
    public void doAddCustomer() {
        QuickNewCustomerAction action = springContext.getBean(QuickNewCustomerAction.class);
        action.fromSalesOrder(this);
    }

    @FXML
    public void doAddItem() {
        AddItemToSalesOrderAction action = springContext.getBean(AddItemToSalesOrderAction.class);
        action.newData(tableView.getItems());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.springContext = applicationContext;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Autowired
    public void setService(ServiceOfSalesOrder service) {
        this.service = service;
    }

    @Autowired
    public void setCustomerService(ServiceOfCustomer customerService) {
        this.customerService = customerService;
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
    public void setActionColumn(TableViewColumnAction actionColumn) {
        this.actionColumn = actionColumn;
    }

    @Autowired
    public void setCustomerMap(HashMap<String, Customer> customerMap) {
        this.customerMap = customerMap;
    }

    @Autowired
    public void setChooser(CustomerChooser chooser) {
        this.chooser = chooser;
    }

    @Autowired
    public void setPrint(PrintController print) {
        this.print = print;
    }

    @Autowired
    public void setValidatorMessages(ValidatorMessages validatorMessages) {
        this.validatorMessages = validatorMessages;
    }

    @Autowired
    public void setHomeAction(HomeAction homeAction) {
        this.homeAction = homeAction;
    }

    @Autowired
    public void setNumberFormatter(NumberFormatter numberFormatter) {
        this.numberFormatter = numberFormatter;
    }

    @Autowired
    public void setLang(LangSource lang) {
        this.lang = lang;
    }

    private String getTransId(Integer value, Integer customerId) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SALES_ORD");
        stringBuilder.append("-");
        stringBuilder.append(customerId);
        stringBuilder.append("-");
        stringBuilder.append(new SimpleDateFormat("yyyyMMdd").format(new java.util.Date()));
        stringBuilder.append("-");
        stringBuilder.append(value);
        return stringBuilder.toString();
    }

    private Double getAmmount() {
        Double ammount = 0.0;
        for (SalesOrderDetails order : tableView.getItems()) {
            Item anItem = order.getItem();
            Double price = 0.0;
            if (anItem != null) {
                price = anItem.getPriceSell();
            } else {
                price = 0.0;
            }
            ammount += order.getQty() * price;
        }
        return ammount;
    }

    public void loadAllComponent() {
        txtCostomer.getItems().clear();
        customerMap.clear();
        try {
            List<Customer> list = customerService.findAll();
            for (Customer aCustomer : list) {
                customerMap.put(chooser.getKey(aCustomer), aCustomer);
                txtCostomer.getItems().add(chooser.getKey(aCustomer));
            }
        } catch (Exception e) {
            windows.errorLoading(lang.getSources(LangProperties.LIST_OF_CUSTOMERS), e);
            e.printStackTrace();
        }
    }

    private void clearFields() {
        txtOrderDate.setValue(LocalDate.now());
        txtCostomer.getSelectionModel().clearSelection();
        txtDownPayment.getValueFactory().setValue(0.0);
        txtGrantTotal.clear();
        txtTotal.clear();
        tableView.getItems().clear();
    }

    public void newData() {
        initValidator();
        setUpdate(false);
        loadAllComponent();
        order = new SalesOrder();
        clearFields();
        this.validator.redecorate();
    }

    private void showToField(SalesOrder order) {
        Customer aCustomer = order.getCustomer();
        txtCostomer.setValue(chooser.getKey(aCustomer));
        txtOrderDate.setValue(order.getOrderDate().toLocalDate());
        System.out.println(order.getDownPayment());
        txtDownPayment.getValueFactory().setValue(order.getDownPayment());
    }

    public void exitsData(SalesOrder order, boolean isUpdate) {
        try {
            initValidator();
            enabledComponents(isUpdate);
            setUpdate(isUpdate);
            loadAllComponent();
            tableView.getItems().clear();
            List<SalesOrderDetails> list = service.findAllSalesOrderDetails(order);
            for (SalesOrderDetails anOrderDetails : list) {
                tableView.getItems().add(anOrderDetails);
            }
            this.order = order;
            showToField(order);
            this.validator.redecorate();
        } catch (Exception e) {
            windows.errorLoading(lang.getSources(LangProperties.LIST_OF_SALES_ORDER), e);
            e.printStackTrace();
        }
    }

    private void enabledComponents(Boolean enabled) {
        txtCostomer.setDisable(!enabled);
        txtDownPayment.setDisable(!enabled);
        txtOrderDate.setDisable(!enabled);
        btnAction.setDisable(!enabled);
        btnAddCustomer.setDisable(!enabled);
        btnAddItem.setDisable(!enabled);
        columnAction.setVisible(enabled);
        btnAddCustomer.setVisible(enabled);
        btnAddItem.setVisible(enabled);
        btnAction.setVisible(enabled);
    }

    private class TableColumnAction extends TableCell<SalesOrderDetails, String> {
        private ObservableList<SalesOrderDetails> listSalesOrder;


        public TableColumnAction(ObservableList<SalesOrderDetails> listSalesOrder) {
            this.listSalesOrder = listSalesOrder;
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                SalesOrderDetails detail = listSalesOrder.get(getIndex());
                setGraphic(actionColumn.getDefautlTableModel());
                actionColumn.getDeleteLink().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        listSalesOrder.remove(getIndex());
                    }
                });
                actionColumn.getUpdateLink().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        AddItemToSalesOrderAction action = springContext.getBean(AddItemToSalesOrderAction.class);
                        action.exitsData(getIndex(), detail, listSalesOrder);
                    }
                });
            }
        }
    }


}
