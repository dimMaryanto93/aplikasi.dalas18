package dallastools.actions.expeditur;

import dallastools.actions.HomeAction;
import dallastools.controllers.FxInitializable;
import dallastools.controllers.NumberFormatter;
import dallastools.controllers.TableViewColumnAction;
import dallastools.controllers.dataselections.SuplayerChooser;
import dallastools.controllers.notifications.*;
import dallastools.models.expenditur.PurchaseInvoice;
import dallastools.models.expenditur.PurchaseInvoiceDetails;
import dallastools.models.masterdata.Item;
import dallastools.models.masterdata.Suplayer;
import dallastools.models.masterdata.Unit;
import dallastools.services.ServiceOfPurchaseInvoice;
import dallastools.services.ServiceOfSuplayer;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
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
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import java.net.URL;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by dimmaryanto on 17/10/15.
 */
public class PurchaseInvoiceDataAction implements FxInitializable {

    @FXML
    private Button btnSave;
    @FXML
    private DatePicker txtTransDate;
    @FXML
    private TextArea txtShipTo;
    @FXML
    private ComboBox<String> txtSuplayer;
    @FXML
    private Button btnAddSuplayer;
    @FXML
    private Button btnAddItem;
    @FXML
    private TableView<PurchaseInvoiceDetails> tableView;
    @FXML
    private TableColumn<PurchaseInvoiceDetails, String> columnAction;
    @FXML
    private TableColumn<PurchaseInvoiceDetails, String> columnProductId;
    @FXML
    private TableColumn<PurchaseInvoiceDetails, String> columnProductName;
    @FXML
    private TableColumn<PurchaseInvoiceDetails, Integer> columnQty;
    @FXML
    private TableColumn<PurchaseInvoiceDetails, String> columnProductUnit;
    @FXML
    private TableColumn<PurchaseInvoiceDetails, Double> columnPrice;
    @FXML
    private TableColumn<PurchaseInvoiceDetails, Double> columnSubtotal;
    @FXML
    private TextField txtTotal;
    @FXML
    private Spinner<Double> txtAmmount;

    private DialogBalloon ballon;
    private DialogWindows windows;
    private ServiceOfSuplayer suplayerService;
    private ServiceOfPurchaseInvoice service;
    private HashMap<String, Suplayer> suplayerMap;
    private SuplayerChooser chooser;
    private TableViewColumnAction actionColumn;

    private Boolean isUpdate;
    private PurchaseInvoice invoice;
    private ApplicationContext springContext;
    private MessageSource messageSource;
    private ValidationSupport validator;
    private SpinnerValueFactory.DoubleSpinnerValueFactory ammount;
    private ValidatorMessages validatorMessages;
    private LangSource lang;
    private NumberFormatter numberFormatter;

    private void initValidator() {
        this.validator = new ValidationSupport();
        this.validator.registerValidator(txtTransDate, (Control date, LocalDate value) ->
                ValidationResult.fromWarningIf(date, validatorMessages.validatorDateNotEqualsNow(), !value.equals(LocalDate.now())));
        this.validator.registerValidator(txtSuplayer, true, Validator.createEmptyValidator(
                validatorMessages.validatorNotSelected(lang.getSources(LangProperties.DATA_A_SUPLAYER)), Severity.ERROR));
        this.validator.invalidProperty().addListener((observable, oldValue, newValue) -> btnSave.setDisable(newValue));
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtTransDate.setValue(LocalDate.now());

        this.ammount = new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 0, 0, 500);
        txtAmmount.setValueFactory(ammount);
        txtAmmount.getEditor().setAlignment(Pos.CENTER_RIGHT);
        txtAmmount.setEditable(true);

        txtTotal.setAlignment(Pos.CENTER_RIGHT);


        columnProductId.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PurchaseInvoiceDetails, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<PurchaseInvoiceDetails, String> param) {
                if (param != null) {
                    Item anItem = param.getValue().getItem();
                    if (anItem != null) {
                        return new SimpleStringProperty(anItem.getId());
                    } else return new SimpleStringProperty("");
                } else
                    return null;
            }
        });
        columnProductName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PurchaseInvoiceDetails, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<PurchaseInvoiceDetails, String> param) {
                if (param != null) {
                    Item anItem = param.getValue().getItem();
                    if (anItem != null) {
                        return new SimpleStringProperty(anItem.getName());
                    } else return new SimpleStringProperty("");
                } else
                    return null;
            }
        });
        columnProductUnit.setCellValueFactory(param -> {
            if (param != null) {
                Item anItem = param.getValue().getItem();
                if (anItem != null) {
                    Unit anUnit = anItem.getUnit();
                    if (anUnit != null) {
                        return new SimpleStringProperty(anUnit.getId());
                    } else return new SimpleStringProperty();
                } else return null;
            } else return null;
        });
        columnQty.setCellValueFactory(new PropertyValueFactory<PurchaseInvoiceDetails, Integer>("qty"));
        columnQty.setCellFactory(param -> new TableCell<PurchaseInvoiceDetails, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setAlignment(Pos.CENTER);
                if (empty) setText(null);
                else setText(numberFormatter.getNumber(item));
            }
        });
        columnPrice.setCellValueFactory(new PropertyValueFactory<PurchaseInvoiceDetails, Double>("priceBuy"));
        columnPrice.setCellFactory(param -> new TableCell<PurchaseInvoiceDetails, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                setAlignment(Pos.CENTER_RIGHT);
                super.updateItem(item, empty);
                if (empty) setText(null);
                else setText(numberFormatter.getCurrency(item));
            }
        });
        columnSubtotal.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PurchaseInvoiceDetails, Double>, ObservableValue<Double>>() {
            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<PurchaseInvoiceDetails, Double> param) {
                if (param != null) {
                    Double qty = Double.valueOf(param.getValue().getQty());
                    Double price = param.getValue().getPriceBuy();
                    return new SimpleObjectProperty<Double>(qty * price);
                } else
                    return null;
            }
        });
        columnSubtotal.setCellFactory(param -> new TableCell<PurchaseInvoiceDetails, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                setAlignment(Pos.CENTER_RIGHT);
                super.updateItem(item, empty);
                if (empty) setText(null);
                else setText(numberFormatter.getCurrency(item));
            }
        });
        columnAction.setCellFactory(new Callback<TableColumn<PurchaseInvoiceDetails, String>, TableCell<PurchaseInvoiceDetails, String>>() {
            @Override
            public TableCell<PurchaseInvoiceDetails, String> call(TableColumn<PurchaseInvoiceDetails, String> param) {
                return new TableColumnAction(tableView.getItems());
            }
        });

        tableView.getItems().addListener(new ListChangeListener<PurchaseInvoiceDetails>() {
            @Override
            public void onChanged(Change<? extends PurchaseInvoiceDetails> c) {
                Double total = getTotal();
                txtTotal.setText(numberFormatter.getCurrency(total));
                ammount.setMax(total);
                ammount.setMin(0);
            }
        });
    }


    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @FXML
    public void doSave() {
        if (tableView.getItems().size() > 0) {
            if (isUpdate) {
                try {
                    this.invoice.setLastUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
                    this.invoice.setAmmount(txtAmmount.getValueFactory().getValue());
                    this.invoice.setDescription(txtShipTo.getText());
                    this.invoice.setGrantTotal(getTotal());
                    this.invoice.setTransDate(Date.valueOf(txtTransDate.getValue()));
                    this.invoice.setSuplayer(suplayerMap.get(txtSuplayer.getValue()));
                    service.updateItemBeforeUpdateOrDelete(this.invoice, false);
                    service.deletePurchaseDetailsByPurchaseInvoice(invoice);
                    service.update(invoice, tableView.getItems());
                    service.updateItemBeforeUpdateOrDelete(this.invoice, true);
                    doClose();
                    clearFields();
                    ballon.sucessedUpdated(lang.getSources(LangProperties.DATA_PURCHASE), lang.getSources(LangProperties.ID), invoice.getId());
                } catch (Exception e) {
                    windows.errorUpdate(lang.getSources(LangProperties.DATA_PURCHASE), lang.getSources(LangProperties.ID), invoice.getId(), e);
                    e.printStackTrace();
                }
            } else {
                try {
                    this.invoice = new PurchaseInvoice(
                            txtAmmount.getValueFactory().getValue(),
                            txtShipTo.getText(),
                            getTotal(),
                            suplayerMap.get(txtSuplayer.getValue()),
                            Date.valueOf(txtTransDate.getValue()));
                    this.invoice.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
                    service.save(this.invoice, tableView.getItems());
                    service.updateItemBeforeUpdateOrDelete(this.invoice, true);
                    clearFields();
                    ballon.sucessedSave(lang.getSources(LangProperties.DATA_PURCHASE));
                } catch (Exception e) {

                    windows.errorSave(lang.getSources(LangProperties.DATA_PURCHASE), e);
                    e.printStackTrace();
                }
            }
        } else {
            ballon.warningEmptyMessage(lang.getSources(LangProperties.DATA_PURCHASE), lang.getSources(LangProperties.LIST_OF_ITEMS));
        }
    }


    @FXML
    public void doAddSuplayer() {
        QuickNewSuplayerAction action = springContext.getBean(QuickNewSuplayerAction.class);
        action.fromPurchaseInvoice(this);
    }

    @FXML
    public void doAddItem() {
        AddItemToPurchaseInvoiceAction action = springContext.getBean(AddItemToPurchaseInvoiceAction.class);
        action.newData(tableView.getItems());
    }


    @Override
    public void doClose() {
        HomeAction homeAction = springContext.getBean(HomeAction.class);
        homeAction.showPurchasing();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.springContext = applicationContext;
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
    public void setSuplayerService(ServiceOfSuplayer suplayerService) {
        this.suplayerService = suplayerService;
    }

    @Autowired
    public void setService(ServiceOfPurchaseInvoice service) {
        this.service = service;
    }

    @Autowired
    public void setSuplayerMap(HashMap<String, Suplayer> suplayerMap) {
        this.suplayerMap = suplayerMap;
    }

    @Autowired
    public void setChooser(SuplayerChooser chooser) {
        this.chooser = chooser;
    }

    @Autowired
    public void setActionColumn(TableViewColumnAction actionColumn) {
        this.actionColumn = actionColumn;
    }

    @Autowired
    public void setLang(LangSource lang) {
        this.lang = lang;
    }

    @Autowired
    public void setValidatorMessages(ValidatorMessages validatorMessages) {
        this.validatorMessages = validatorMessages;
    }

    @Autowired
    public void setNumberFormatter(NumberFormatter numberFormatter) {
        this.numberFormatter = numberFormatter;
    }

    private void showToFields(PurchaseInvoice invoice) {
        try {
            List<PurchaseInvoiceDetails> list = service.findPurchaseDetailByInvoice(invoice);
            for (PurchaseInvoiceDetails details : list) {
                tableView.getItems().add(details);
            }
            txtSuplayer.getSelectionModel().select(chooser.getKey(invoice.getSuplayer()));
            txtAmmount.getValueFactory().setValue(invoice.getAmmount());
            txtShipTo.setText(invoice.getDescription());
            txtTransDate.setValue(invoice.getTransDate().toLocalDate());
        } catch (Exception e) {
            windows.errorLoading(lang.getSources(LangProperties.DATA_PURCHASE), e);
            e.printStackTrace();
        }
    }

    public void initComponents() {
        try {
            txtSuplayer.getItems().clear();
            suplayerMap.clear();
            List<Suplayer> list = suplayerService.findAll();
            for (Suplayer aSuplayer : list) {
                txtSuplayer.getItems().add(chooser.getKey(aSuplayer));
                suplayerMap.put(chooser.getKey(aSuplayer), aSuplayer);
            }
        } catch (Exception e) {
            windows.errorLoading(lang.getSources(LangProperties.LIST_OF_SUPLAYERS), e);
            e.printStackTrace();
        }
    }

    public void newData() {
        initValidator();
        initComponents();
        setUpdate(false);
        this.validator.redecorate();
    }


    public void exitsData(PurchaseInvoice invoice) {
        initValidator();
        this.invoice = invoice;
        initComponents();
        initComponentsVisible(true);
        initComponentsEnabled(true);
        setUpdate(true);
        showToFields(invoice);
        this.validator.redecorate();
    }

    public void readOnly(PurchaseInvoice invoice) {
        initComponents();
        initComponentsEnabled(false);
        initComponentsVisible(false);
        setUpdate(false);
        showToFields(invoice);
    }

    private Double getTotal() {
        Double result = 0.0;
        for (PurchaseInvoiceDetails details : tableView.getItems()) {
            Double priceOfBuy = details.getPriceBuy();
            Double qty = Double.valueOf(details.getQty());
            result += priceOfBuy * qty;
        }
        return result;
    }


    private void setUpdate(Boolean isUpdate) {
        this.isUpdate = isUpdate;
    }


    private void initComponentsEnabled(Boolean enable) {
        txtTransDate.setDisable(!enable);
        txtShipTo.setDisable(!enable);
        txtSuplayer.setDisable(!enable);
        txtTotal.setDisable(!enable);
        txtAmmount.setDisable(!enable);
        tableView.getSelectionModel().setCellSelectionEnabled(!enable);
    }

    private void initComponentsVisible(Boolean show) {
        btnSave.setVisible(show);
        btnAddItem.setVisible(show);
        btnAddSuplayer.setVisible(show);
        columnAction.setVisible(show);
    }

    private void clearFields() {
        txtTransDate.setValue(LocalDate.now());
        txtShipTo.clear();
        txtSuplayer.getSelectionModel().clearSelection();
        txtTotal.clear();
        txtAmmount.getValueFactory().setValue(0.0);
        tableView.getItems().clear();
    }

    private class TableColumnAction extends TableCell<PurchaseInvoiceDetails, String> {
        ObservableList<PurchaseInvoiceDetails> list;

        public TableColumnAction(ObservableList<PurchaseInvoiceDetails> list) {
            this.list = list;
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) setGraphic(null);
            else {
                setGraphic(actionColumn.getDefautlTableModel());
                actionColumn.getDeleteLink().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        list.remove(getIndex());
                    }
                });
                actionColumn.getUpdateLink().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        AddItemToPurchaseInvoiceAction action = springContext.getBean(AddItemToPurchaseInvoiceAction.class);
                        action.exitsData(getIndex(), list.get(getIndex()), list);
                    }
                });
            }
        }
    }
}
