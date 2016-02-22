package dallastools.actions.income;

import dallastools.actions.HomeAction;
import dallastools.controllers.FxInitializable;
import dallastools.controllers.NumberFormatter;
import dallastools.controllers.PrintController;
import dallastools.controllers.TableViewColumnAction;
import dallastools.controllers.dataselections.CustomerChooser;
import dallastools.controllers.notifications.*;
import dallastools.models.income.Sales;
import dallastools.models.income.SalesDetails;
import dallastools.models.income.SalesOrder;
import dallastools.models.income.SalesOrderDetails;
import dallastools.models.masterdata.Customer;
import dallastools.models.masterdata.Item;
import dallastools.services.ServiceOfCustomer;
import dallastools.services.ServiceOfItem;
import dallastools.services.ServiceOfSalesInvoice;
import dallastools.services.ServiceOfSalesOrder;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by dimmaryanto on 13/10/15.
 */
public class SalesInvoiceDataAction implements FxInitializable {

    private final Logger log = LoggerFactory.getLogger(SalesInvoiceDataAction.class);
    private final ObservableList<Item> listDetails = FXCollections.observableArrayList();
    @FXML
    private RadioButton txtDeliverySelling;
    @FXML
    private ToggleGroup sent;
    @FXML
    private RadioButton txtDirectSelling;
    @FXML
    private TextArea txtShipTo;
    @FXML
    private Button btnSave;
    @FXML
    private DatePicker txtTransDate;
    @FXML
    private ComboBox<String> txtCustomer;
    @FXML
    private Button btnAddCustomer;
    @FXML
    private Button btnAddItem;
    @FXML
    private TableView<SalesDetails> tableView;
    @FXML
    private TableColumn<SalesDetails, String> columnAction;
    @FXML
    private TableColumn<SalesDetails, String> columnProductId;
    @FXML
    private TableColumn<SalesDetails, String> columnProductName;
    @FXML
    private TableColumn<SalesDetails, Integer> columnQty;
    @FXML
    private TableColumn<SalesDetails, Double> columnPrice;
    @FXML
    private TableColumn<SalesDetails, Double> columnSubtotal;
    @FXML
    private TextField txtTotal;
    @FXML
    private Spinner<Double> txtPayment;
    @FXML
    private TextField txtDownPayment;
    @FXML
    private TextField txtCashBack;

    private PrintController print;
    private ServiceOfCustomer customerService;
    private ServiceOfSalesInvoice service;
    private ServiceOfSalesOrder salesOrderService;
    private ServiceOfItem itemService;
    private HomeAction homeAction;
    private DialogWindows windows;
    private DialogBalloon ballon;
    private TableViewColumnAction actionColumn;
    private HashMap<String, Customer> customerMap;
    private CustomerChooser chooser;
    private SpinnerValueFactory.DoubleSpinnerValueFactory paymentValueFactory;
    private ValidationSupport validator;
    private MessageSource messageSource;
    private ApplicationContext springContext;
    private Sales sales;
    private Boolean isUpdate;
    private Boolean fromOrder;
    private Boolean readOnly;
    private SalesOrder salesOrder;
    private ValidatorMessages validatorMessages;
    private LangSource lang;
    private NumberFormatter numberFormatter;

    private void initValidator() {
        this.validator = new ValidationSupport();
        this.validator.registerValidator(txtCustomer, true, Validator.createEmptyValidator(
                validatorMessages.validatorNotSelected(lang.getSources(LangProperties.CUSTOMER)), Severity.ERROR));
        this.validator.registerValidator(txtTransDate,
                (Control date, LocalDate value) -> ValidationResult.fromWarningIf(date,
                        validatorMessages.validatorDateNotEqualsNow(), !value.equals(LocalDate.now())));
        this.validator.registerValidator(txtShipTo, true, Validator.createEmptyValidator(
                validatorMessages.validatorEmpty(lang.getSources(LangProperties.SHIP_TO)), Severity.ERROR));
        this.validator.invalidProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                btnSave.setDisable(newValue);
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        tableView.setSelectionModel(null);

        paymentValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 0, 0, 500);

        txtTransDate.setValue(LocalDate.now());

        txtTotal.setAlignment(Pos.CENTER_RIGHT);

        txtPayment.setValueFactory(paymentValueFactory);
        txtPayment.getEditor().setAlignment(Pos.CENTER_RIGHT);
        txtPayment.setEditable(true);

        txtCashBack.setAlignment(Pos.CENTER_RIGHT);


        txtCustomer.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue != null) {
                    txtShipTo.setText(customerMap.get(newValue).getAddress().getStreetAddress());
                } else {
                    txtShipTo.clear();
                }
            }
        });

        columnAction.setCellFactory(new Callback<TableColumn<SalesDetails, String>, TableCell<SalesDetails, String>>() {
            @Override
            public TableCell<SalesDetails, String> call(TableColumn<SalesDetails, String> param) {
                return new TableColumnAction(tableView.getItems());
            }
        });
        columnProductId.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SalesDetails, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<SalesDetails, String> param) {
                if (param != null && param.getValue().getItem() != null) {
                    Item anItem = param.getValue().getItem();
                    return new SimpleStringProperty(anItem.getId());
                } else
                    return null;
            }
        });
        columnProductName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SalesDetails, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<SalesDetails, String> param) {
                if (param != null && param.getValue().getItem() != null) {
                    Item anItem = param.getValue().getItem();
                    return new SimpleStringProperty(anItem.getName());
                } else
                    return null;
            }
        });
        columnQty.setCellValueFactory(new PropertyValueFactory<SalesDetails, Integer>("qty"));
        columnQty.setCellFactory(param -> new TableCell<SalesDetails, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setText(null);
                else setText(item.toString());
                setAlignment(Pos.CENTER);
            }
        });
        columnPrice.setCellValueFactory(new PropertyValueFactory<SalesDetails, Double>("priceSell"));
        columnPrice.setCellFactory(param -> new TableCell<SalesDetails, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(new Label(numberFormatter.getCurrency(item)));
                setAlignment(Pos.CENTER_RIGHT);
            }
        });
        columnSubtotal.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SalesDetails, Double>, ObservableValue<Double>>() {
            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<SalesDetails, Double> param) {
                if (param.getValue() != null && param.getValue().getItem() != null) {
                    SalesDetails details = param.getValue();
                    Double price = details.getPriceSell();
                    Double qty = Double.valueOf(details.getQty());
                    return new SimpleObjectProperty<Double>(price * qty);
                } else
                    return null;
            }
        });
        columnSubtotal.setCellFactory(param -> new TableCell<SalesDetails, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(new Label(numberFormatter.getCurrency(item)));
                setAlignment(Pos.CENTER_RIGHT);
            }
        });
        tableView.getItems().addListener(new ListChangeListener<SalesDetails>() {
            @Override
            public void onChanged(Change<? extends SalesDetails> c) {
                Double result = getTotal();
                Double grantTotal = result - txtPayment.getValueFactory().getValue();
                txtTotal.setText(numberFormatter.getCurrency(result));
                paymentValueFactory.setMin(0);
                paymentValueFactory.setMax(result - sales.getAmmount());
                txtCashBack.setText(numberFormatter.getCurrency(grantTotal));
            }
        });
        txtPayment.getValueFactory().valueProperty().addListener(new ChangeListener<Double>() {
            @Override
            public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
                Double total = getTotal();
                Double grantTotal = total - newValue;
                txtCashBack.setText(numberFormatter.getCurrency(grantTotal - sales.getAmmount()));
            }
        });
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.springContext = applicationContext;
    }

    private void isDirectSelling(Boolean benar) {
        sales.setRecieved(benar);
        sales.setSent(benar);
    }


    /**
     * simpan data untuk transaksi baru
     */
    private void todoNewData() {
        try {
            isDirectSelling(txtDirectSelling.isSelected());
            sales.setCustomer(customerMap.get(txtCustomer.getValue()));
            sales.setAmmount(txtPayment.getValueFactory().getValue() + sales.getAmmount());
            sales.setDateTransaction(Date.valueOf(txtTransDate.getValue()));
            sales.setShipTo(txtShipTo.getText());
            sales.setGrantTotal(getTotal());
            Double result = sales.getGrantTotal() - sales.getAmmount();
            sales.setPaid(result <= 0);
            sales.setTransId("-");
            service.save(sales, tableView.getItems());
            sales.setTransId(getTransID(sales.getId().longValue()));
            service.updateSales(sales);
            service.updateItemBeforeUpdatedOfDelete(sales, false);
            log.info("transaksi {} telah lunas : {} , telah dikirim : {} , telah diterima : {}",
                    new Object[]{sales.getTransId(), sales.getPaid(), sales.getSent(), sales.getRecieved()});
            print.showSalesInvoice(messageSource.getMessage(lang.getSources(LangProperties.LIST_OF_SALES), null, Locale.getDefault()), sales, tableView.getItems());
            ballon.sucessedSave(lang.getSources(LangProperties.DATA_A_SALES), sales.getTransId());
            newData();
        } catch (Exception e) {
            e.printStackTrace();
            windows.errorSave(lang.getSources(LangProperties.DATA_A_SALES), e);
        }
    }

    /**
     * simpan data untuk transaksi berdasarkan pesanan
     */
    private void todoFromSalesOrderData() {
        try {
            sales.setTransId("-");
            isDirectSelling(txtDirectSelling.isSelected());
            sales.setCustomer(customerMap.get(txtCustomer.getValue()));
            sales.setAmmount(txtPayment.getValueFactory().getValue() + sales.getAmmount());
            sales.setDateTransaction(Date.valueOf(txtTransDate.getValue()));
            sales.setShipTo(txtShipTo.getText());
            sales.setGrantTotal(getTotal());
            Double result = sales.getGrantTotal() - sales.getAmmount();
            sales.setPaid(result <= 0);
            //do save data
            service.save(sales, tableView.getItems());
            sales.setTransId(getTransID(sales.getId().longValue()));
            service.updateSales(sales);
            service.updateItemBeforeUpdatedOfDelete(sales, false);
            salesOrderService.updateSetTransaction(true, salesOrder);

            log.info("transaksi {} telah lunas : {} , telah dikirim : {} , telah diterima : {}",
                    new Object[]{sales.getTransId(), sales.getPaid(), sales.getSent(), sales.getRecieved()});

            print.showSalesInvoice(messageSource.getMessage(lang.getSources(LangProperties.LIST_OF_SALES), null, Locale.getDefault()), sales, tableView.getItems());
            ballon.sucessedSave(lang.getSources(LangProperties.DATA_A_SALES), sales.getTransId());
            newData();
        } catch (Exception e) {
            e.printStackTrace();
            windows.errorSave(lang.getSources(LangProperties.DATA_A_SALES), e);
        }
    }

    private void todoPaymentCustomerReciveable() {
        try {
            sales.setCustomer(customerMap.get(txtCustomer.getValue()));
            sales.setAmmount(txtPayment.getValueFactory().getValue() + sales.getAmmount());
            sales.setDateTransaction(Date.valueOf(txtTransDate.getValue()));
            sales.setShipTo(txtShipTo.getText());
            sales.setGrantTotal(getTotal());

            Double result = sales.getGrantTotal() - sales.getAmmount();
            sales.setPaid(result <= 0);

            service.updateSales(sales);
            print.showSalesInvoice(messageSource.getMessage(lang.getSources(LangProperties.LIST_OF_SALES), null, Locale.getDefault()), sales, tableView.getItems());

            log.info("transaksi {} telah lunas : {}", sales.getTransId(), sales.getPaid());
            homeAction.showSales();
            ballon.sucessedUpdated(lang.getSources(LangProperties.DATA_A_SALES), lang.getSources(LangProperties.ID), sales.getTransId());
        } catch (Exception e) {
            windows.errorUpdate(lang.getSources(LangProperties.DATA_A_SALES), lang.getSources(LangProperties.ID), sales.getTransId(), e);
            e.printStackTrace();
        }
    }

    private boolean isQtyMinus() {
        Boolean isValid = true;

        for (SalesDetails sales : tableView.getItems()) {
            for (Item anItem : listDetails) {
                if (anItem == sales.getItem()) {
                    log.info("nama barang {} : ({} <= {}) -> {} ", new Object[]{anItem.getName(), anItem.getQty(), 0, anItem.getQty() >= 0});
                    if (anItem.getQty() <= -1)
                        isValid = false;
                    break;
                }
            }
        }
        return isValid;
    }

    @FXML
    private void doSave() {
        if (tableView.getItems().size() > 0) {
            if (isUpdate && !fromOrder && !readOnly) {
                todoPaymentCustomerReciveable();
            } else if (!isUpdate && !fromOrder && !readOnly) {
                if (isQtyMinus())
                    todoNewData();
                else
                    ballon.warningNotEnough(lang.getSources(LangProperties.DATA_A_SALES), lang.getSources(LangProperties.DATA_AN_ITEM));
            } else if (!isUpdate && fromOrder) {
                if (isQtyMinus()) {
                    todoFromSalesOrderData();
                } else {
                    ballon.warningNotEnough(lang.getSources(LangProperties.DATA_A_SALES), lang.getSources(LangProperties.DATA_AN_ITEM));
                }
            }
        } else {
            ballon.warningEmptyMessage(lang.getSources(LangProperties.DATA_A_SALES), lang.getSources(LangProperties.LIST_OF_ITEMS));
        }
    }

    @FXML
    private void doAddCustomer() {
        QuickNewCustomerAction action = springContext.getBean(QuickNewCustomerAction.class);
        action.fromSalesInvoice(this);
    }

    @FXML
    private void doAddItem() {
        AddItemToSalesInvoiceAction action = springContext.getBean(AddItemToSalesInvoiceAction.class);
        action.newData(listDetails, tableView.getItems());
    }

    @Autowired
    public void setCustomerService(ServiceOfCustomer customerService) {
        this.customerService = customerService;
    }

    @Autowired
    public void setService(ServiceOfSalesInvoice service) {
        this.service = service;
    }

    @Autowired
    public void setSalesOrderService(ServiceOfSalesOrder salesOrderService) {
        this.salesOrderService = salesOrderService;
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
    public void setWindows(DialogWindows windows) {
        this.windows = windows;
    }

    @Autowired
    public void setBallon(DialogBalloon ballon) {
        this.ballon = ballon;
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
    public void setItemService(ServiceOfItem itemService) {
        this.itemService = itemService;
    }

    @Autowired
    public void setNumberFormatter(NumberFormatter numberFormatter) {
        this.numberFormatter = numberFormatter;
    }

    @Autowired
    public void setLang(LangSource lang) {
        this.lang = lang;
    }

    public void newData() {
        initValidator();
        tableView.setSelectionModel(null);

        setReadOnly(false);
        setFromOrder(false);
        setUpdate(false);

        this.sales = new Sales();
        this.sales.setAmmount(0.0);
        this.sales.setGrantTotal(0.0);

        loadAllComponentNeeded();
        clearFields();
        enableComponents(true);
        this.columnQty.setCellFactory(param -> new TableColumnQTYMessage(tableView.getItems()));
        this.columnAction.setVisible(true);
        this.btnAddCustomer.setVisible(true);
        this.btnAddItem.setVisible(true);
        this.btnSave.setVisible(true);
        this.txtDirectSelling.setDisable(false);
        this.txtDeliverySelling.setDisable(false);

        this.txtDownPayment.setText(numberFormatter.getCurrency(0));
        this.txtCashBack.setText(numberFormatter.getCurrency(0));
        this.txtTotal.setText(numberFormatter.getCurrency(0));

        this.validator.redecorate();
    }

    public void convertFromSalesOrder(SalesOrder anOrder) {
        initValidator();
        setReadOnly(false);
        setFromOrder(true);
        setUpdate(false);
        this.salesOrder = anOrder;

        this.sales = new Sales();
        this.sales.setAmmount(salesOrder.getDownPayment());

        loadAllComponentNeeded();
        showToTableForSalesOrder(anOrder);

        Double result = 0.0;
        for (SalesDetails details : tableView.getItems()) {
            result += details.getPriceSell() * details.getQty();
        }

        this.txtPayment.getValueFactory().setValue(0.0);
        this.txtCustomer.setValue(chooser.getKey(anOrder.getCustomer()));
        this.txtCashBack.setText(numberFormatter.getCurrency(result - sales.getAmmount()));
        this.txtDownPayment.setText(numberFormatter.getCurrency(sales.getAmmount()));
        this.txtPayment.getValueFactory().setValue(0.0);

        enableComponents(false);
        this.columnQty.setCellFactory(param -> new TableColumnQTYMessage(tableView.getItems()));

        this.txtPayment.setDisable(false);
        this.btnAddItem.setDisable(false);
        this.txtCustomer.setDisable(true);
        this.txtShipTo.setDisable(false);

        this.columnAction.setVisible(true);
        this.btnAddCustomer.setVisible(false);
        this.btnAddItem.setVisible(true);
        this.btnSave.setVisible(true);

        this.txtDirectSelling.setDisable(false);
        this.txtDeliverySelling.setDisable(false);

        this.validator.redecorate();
    }

    public void doClose() {
        if (fromOrder)
            homeAction.showSalesOrder();
        else
            homeAction.showSales();
    }

    private Double getTotal() {
        Double result = 0.0;
        for (SalesDetails details : tableView.getItems()) {
            Double priceOfItem = details.getPriceSell();
            Double qty = Double.valueOf(details.getQty());
            result += priceOfItem * qty;
        }
        return result;
    }

    public void exitsData(Sales aSales) {
        initValidator();

        tableView.setSelectionModel(null);
        setReadOnly(false);
        setFromOrder(false);
        setUpdate(true);

        this.sales = aSales;

        loadAllComponentNeeded();
        showToTableForSales(aSales);

        this.txtTransDate.setValue(aSales.getDateTransaction().toLocalDate());
        this.txtCustomer.setValue(chooser.getKey(aSales.getCustomer()));
        this.txtPayment.getValueFactory().setValue(0.0);
        this.txtCashBack.setText(numberFormatter.getCurrency(sales.getGrantTotal() - sales.getAmmount()));

        this.txtDownPayment.setText(numberFormatter.getCurrency(sales.getAmmount()));
        this.txtShipTo.setText(aSales.getShipTo());

        enableComponents(false);
        this.txtDirectSelling.setDisable(true);
        this.txtDeliverySelling.setDisable(true);
        this.txtPayment.setDisable(false);

        this.columnAction.setVisible(false);
        this.btnAddItem.setVisible(false);
        this.btnAddCustomer.setVisible(false);
        this.btnSave.setVisible(true);


        this.validator.redecorate();
    }

    public void readOnly(Sales aSales) {
        tableView.setSelectionModel(null);
        setReadOnly(true);
        setFromOrder(false);
        setUpdate(true);
        this.sales = aSales;
        loadAllComponentNeeded();
        showToTableForSales(aSales);

        this.paymentValueFactory.setMin(0.0);
        this.paymentValueFactory.setMax(sales.getGrantTotal());
        this.paymentValueFactory.setValue(sales.getAmmount());

        this.txtCustomer.setValue(chooser.getKey(aSales.getCustomer()));
        this.txtDownPayment.setText(numberFormatter.getCurrency(0));
        this.txtCashBack.setText(numberFormatter.getCurrency(sales.getGrantTotal() - sales.getAmmount()));

        enableComponents(false);

        this.txtDirectSelling.setDisable(true);
        this.txtDeliverySelling.setDisable(true);

        this.columnAction.setVisible(false);
        this.btnAddItem.setVisible(false);
        this.btnAddCustomer.setVisible(false);
        this.btnSave.setVisible(false);

        this.txtPayment.setOpacity(0.9);
        this.txtPayment.getEditor().setOpacity(0.9);
    }

    private void opacityComponents(Double value) {
        this.txtPayment.setOpacity(value);
        this.txtCustomer.setOpacity(value);
        this.txtTransDate.setOpacity(value);
        this.txtShipTo.setOpacity(value);
    }

    private void enableComponents(Boolean active) {
        txtPayment.setDisable(!active);
        txtCustomer.setDisable(!active);
        txtTransDate.setDisable(!active);
        txtShipTo.setDisable(!active);
        btnAddCustomer.setDisable(!active);
        btnAddItem.setDisable(!active);
        opacityComponents(0.9);
    }

    private void showToTableForSales(Sales aSales) {
        try {
            tableView.getItems().clear();
            List<SalesDetails> list = service.findSalesDetailPerSales(aSales);
            for (SalesDetails sales : list) {
                tableView.getItems().add(sales);
            }
        } catch (Exception e) {
            windows.errorLoading(lang.getSources(LangProperties.LIST_OF_ITEMS), e);
            e.printStackTrace();
        }
    }

    private void adjusmentItem(SalesDetails salesDetails) {
        Item anItem = salesDetails.getItem();
        for (int i = 0; i < listDetails.size(); i++) {
            Item item = listDetails.get(i);
            if (item.getItemGenerator() == anItem.getItemGenerator()) {
                log.info("Barang {} berkurang {} - {} = {}",
                        new Object[]{item.getName(), item.getQty(), salesDetails.getQty(),
                                (item.getQty() - salesDetails.getQty())});
                item.setQty(item.getQty() - salesDetails.getQty());
                salesDetails.setId(i);
                salesDetails.setItem(item);
            }
        }
    }

    private void showToTableForSalesOrder(SalesOrder anOrder) {
        try {
            tableView.getItems().clear();
            SalesDetails aDetails;
            List<SalesOrderDetails> list = salesOrderService.findAllSalesOrderDetails(anOrder);
            for (SalesOrderDetails orderDetails : list) {
                aDetails = new SalesDetails();
                aDetails.setItem(orderDetails.getItem());
                aDetails.setQty(orderDetails.getQty());
                aDetails.setPriceSell(orderDetails.getItem().getPriceSell());
                tableView.getItems().add(aDetails);
                adjusmentItem(aDetails);
            }
        } catch (Exception e) {
            windows.errorLoading(lang.getSources(LangProperties.LIST_OF_ITEMS), e);
            e.printStackTrace();
        }
    }

    public void loadAllComponentNeeded() {
        txtCustomer.getItems().clear();
        customerMap.clear();
        listDetails.clear();
        try {
            List<Customer> listCustomer = customerService.findAll();
            for (Customer aCustomer : listCustomer) {
                customerMap.put(chooser.getKey(aCustomer), aCustomer);
                txtCustomer.getItems().add(chooser.getKey(aCustomer));
            }
            List<Item> listItems = itemService.findByItemIsSell(true);
            for (Item item : listItems) {
                listDetails.add(item);
            }
            /*log.info("Jumlah Barang ditemukan {}", listItems.size());*/
        } catch (Exception e) {
            windows.errorLoading(lang.getSources(LangProperties.LIST_OF_CUSTOMERS), e);
            e.printStackTrace();
        }
    }

    private void clearFields() {
        tableView.getItems().clear();
        txtShipTo.clear();
        txtCustomer.getSelectionModel().clearSelection();
        txtTransDate.setValue(LocalDate.now());
        txtTotal.clear();
        txtPayment.getValueFactory().setValue(0.0);
        txtCashBack.clear();
    }

    private String getTransID(Long nextval) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SALES_INV");
        stringBuilder.append("-");
        stringBuilder.append(this.customerMap.get(txtCustomer.getValue()).getId());
        stringBuilder.append("-");
        stringBuilder.append(new SimpleDateFormat("yyyyMMdd").format(new java.util.Date()));
        stringBuilder.append("-");
        stringBuilder.append(nextval);
        return stringBuilder.toString();
    }


    private void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }

    private void setUpdate(Boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    private void setFromOrder(Boolean fromOrder) {
        this.fromOrder = fromOrder;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private class TableColumnAction extends TableCell<SalesDetails, String> {
        ObservableList<SalesDetails> list;

        public TableColumnAction(ObservableList<SalesDetails> list) {
            this.list = list;
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty)
                setGraphic(null);
            else {
                setAlignment(Pos.CENTER);
                SalesDetails sales = list.get(getIndex());
                FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.TRASH_ALT);
                icon.setFont(new Font("FontAwesome", 18));
                setGraphic(actionColumn.getSingleHyperlinkTableModel(lang.getSources(LangProperties.DELETE)));
                actionColumn.getDeleteLink().setGraphic(icon);
                actionColumn.getDeleteLink().setTextFill(Color.RED);
                actionColumn.getDeleteLink().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        Item anItem = sales.getItem();
                        listDetails.set(sales.getId(), anItem);
                        anItem.setQty(anItem.getQty() + sales.getQty());
                        list.remove(getIndex());
                    }
                });
            }
        }
    }

    private class TableColumnQTYMessage extends TableCell<SalesDetails, Integer> {
        ObservableList<SalesDetails> list;

        public TableColumnQTYMessage(ObservableList<SalesDetails> list) {
            this.list = list;
        }

        @Override
        protected void updateItem(Integer item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) setGraphic(null);
            else {
                setAlignment(Pos.CENTER);
                HBox box = new HBox(5);
                box.setAlignment(Pos.CENTER);

                SalesDetails details = list.get(getIndex());
                for (Item anItem : listDetails) {
                    if (details.getItem().getItemGenerator() == anItem.getItemGenerator()) {
                        Integer qtyOfItem = anItem.getQty();
                        //log.info("QTY {} < {} : {}", new Object[]{qtyOfItem, 0, (qtyOfItem < 0)});
                        if (qtyOfItem < 0) {
                            setGraphic(new Label(numberFormatter.getNumber(item), new FontAwesomeIconView(FontAwesomeIcon.EXCLAMATION)));
                            setTooltip(new Tooltip(messageSource.getMessage(lang.getSources(LangProperties.NOT_ENOUGH_WITH_PARAM), new Object[]{
                                    messageSource.getMessage(lang.getSources(LangProperties.QTY), null, Locale.getDefault())
                            }, Locale.getDefault())));
                        } else {
                            setGraphic(new Label(numberFormatter.getNumber(item), new FontAwesomeIconView(FontAwesomeIcon.CHECK)));
                        }
                    }
                }
            }
        }

    }
}
