package dallastools.actions.expeditur;

import dallastools.controllers.FxInitializable;
import dallastools.controllers.NumberFormatter;
import dallastools.controllers.notifications.*;
import dallastools.controllers.stages.SecondStageController;
import dallastools.models.expenditur.PurchaseInvoiceDetails;
import dallastools.models.masterdata.CategoryOfItem;
import dallastools.models.masterdata.Item;
import dallastools.models.masterdata.Unit;
import dallastools.models.masterdata.Warehouse;
import dallastools.services.ServiceOfItem;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by dimmaryanto on 17/10/15.
 */
public class AddItemToPurchaseInvoiceAction implements FxInitializable {

    @FXML
    private TextField txtWarehouse;
    @FXML
    private Button btnAdd;
    @FXML
    private TableView<Item> tableView;
    @FXML
    private TableColumn<Item, Integer> columnId;
    @FXML
    private TableColumn<Item, String> columnName;
    @FXML
    private Spinner<Integer> txtQty;
    @FXML
    private TextField txtId;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtCategory;
    @FXML
    private TextField txtUnit;
    @FXML
    private Spinner<Double> txtPrice;
    @FXML
    private TextField txtStok;

    private ServiceOfItem itemService;
    private DialogBalloon ballon;
    private DialogWindows windows;
    private Integer indexOfList;
    private ApplicationContext springContext;
    private MessageSource messageSource;
    private Item anItem;
    private SpinnerValueFactory.IntegerSpinnerValueFactory integerSpinnerValue;
    private SpinnerValueFactory.DoubleSpinnerValueFactory doubleSpinnerValue;
    private PurchaseInvoiceDetails details;
    private ObservableList<PurchaseInvoiceDetails> listDetails;
    private Boolean isUpdate;
    private ValidationSupport validator;
    private ValidatorMessages validatorMessages;
    private Integer oldValue;
    private LangSource lang;
    private NumberFormatter numberFormatter;


    @Override
    public void doClose() {
        SecondStageController theStage = springContext.getBean(SecondStageController.class);
        theStage.closeSecondStage();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.springContext = applicationContext;
    }

    private void initValidator() {
        this.validator = new ValidationSupport();
        this.validator.registerValidator(txtId, false, Validator.createEmptyValidator(
                validatorMessages.validatorNotSelected(lang.getSources(LangProperties.ID)), Severity.ERROR));
        this.validator.registerValidator(txtQty.getEditor(), (Control qty, String value) ->
                ValidationResult.fromErrorIf(qty, validatorMessages.validatorMin(1), value.equals("0")));
        this.validator.invalidProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                btnAdd.setDisable(newValue);
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.integerSpinnerValue = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 0, 0, 5);
        this.doubleSpinnerValue = new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 0, 0, 500);

        txtQty.setValueFactory(integerSpinnerValue);
        txtQty.getEditor().setAlignment(Pos.CENTER_RIGHT);
        txtQty.setEditable(true);

        txtPrice.getEditor().setAlignment(Pos.CENTER_RIGHT);
        txtPrice.setValueFactory(doubleSpinnerValue);
        txtPrice.setEditable(true);

        txtStok.setAlignment(Pos.CENTER_RIGHT);


        columnId.setCellValueFactory(new PropertyValueFactory<Item, Integer>("id"));
        columnName.setCellValueFactory(new PropertyValueFactory<Item, String>("name"));
        tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Item>() {
            @Override
            public void changed(ObservableValue<? extends Item> observable, Item oldValue, Item newValue) {
                anItem = newValue;
                if (newValue != null) {
                    showToFields(newValue);
                } else {
                    clearFields();
                }
            }
        });
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private PurchaseInvoiceDetails getValue(PurchaseInvoiceDetails detail) {
        detail.setPriceBuy(txtPrice.getValueFactory().getValue());
        detail.setQty(txtQty.getValueFactory().getValue());
        detail.setItem(anItem);
        return detail;
    }

    @FXML
    public void doAdd() {
        if (isUpdate) {
            listDetails.set(indexOfList, getValue(this.details));
            doClose();
            ballon.sucessedUpdated(lang.getSources(LangProperties.DATA_AN_ITEM), lang.getSources(LangProperties.QTY), anItem.getName(), oldValue, details.getQty());
        } else {
            this.details = new PurchaseInvoiceDetails();
            listDetails.add(getValue(this.details));
            tableView.getSelectionModel().clearSelection();
            ballon.sucessedSave(lang.getSources(LangProperties.DATA_AN_ITEM), lang.getSources(LangProperties.NAME), details.getItem().getName());
        }
    }

    @FXML
    private void loadData() {
        try {
            tableView.getItems().clear();
            List<Item> list = itemService.findByItemForPurchase();
            for (Item anItem : list)
                tableView.getItems().add(anItem);
        } catch (Exception e) {
            windows.errorLoading(lang.getSources(LangProperties.LIST_OF_ITEMS), e);
            e.printStackTrace();
        }
    }

    @FXML
    public void tableViewClearSelection() {
        tableView.getSelectionModel().clearSelection();
    }

    @Autowired
    public void setItemService(ServiceOfItem itemService) {
        this.itemService = itemService;
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
    public void setValidatorMessages(ValidatorMessages validatorMessages) {
        this.validatorMessages = validatorMessages;
    }

    @Autowired
    public void setNumberFormatter(NumberFormatter numberFormatter) {
        this.numberFormatter = numberFormatter;
    }

    @Autowired
    public void setLang(LangSource lang) {
        this.lang = lang;
    }

    private void clearFields() {
        this.integerSpinnerValue.setMax(0);
        this.integerSpinnerValue.setMin(0);
        this.integerSpinnerValue.setValue(0);
        this.doubleSpinnerValue.setMax(0);
        this.doubleSpinnerValue.setMin(0);
        this.doubleSpinnerValue.setValue(0.0);
        txtWarehouse.clear();
        txtId.clear();
        txtQty.getValueFactory().setValue(0);
        txtName.clear();
        txtCategory.clear();
        txtUnit.clear();
        txtPrice.getValueFactory().setValue(0.0);
        txtStok.clear();
    }

    private void showToFields(Item anItem) {
        this.integerSpinnerValue.setMax(Integer.MAX_VALUE);
        this.integerSpinnerValue.setMin(0);
        this.doubleSpinnerValue.setMax(Double.MAX_VALUE);
        this.doubleSpinnerValue.setMin(0);
        txtId.setText(anItem.getId());
        txtName.setText(anItem.getName());

        CategoryOfItem aCategory = anItem.getCategory();
        if (aCategory != null) txtCategory.setText(aCategory.getName());
        else txtCategory.clear();

        Unit anUnit = anItem.getUnit();
        if (anUnit != null) txtUnit.setText(anUnit.getId());
        else txtUnit.clear();

        Warehouse aWarehouse = anItem.getWarehouse();
        if (aWarehouse != null) txtWarehouse.setText(aWarehouse.getName());
        else txtWarehouse.clear();

        txtPrice.getValueFactory().setValue(anItem.getPriceBuy());
        txtStok.setText(numberFormatter.getNumber(anItem.getQty()));
    }

    private void setUpdate(Boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    public void newData(ObservableList<PurchaseInvoiceDetails> items) {
        initValidator();
        this.listDetails = items;
        setUpdate(false);
        loadData();
        this.tableView.setDisable(false);
        this.validator.redecorate();
    }

    public void exitsData(int index, PurchaseInvoiceDetails invoiceDetails, ObservableList<PurchaseInvoiceDetails> listDetails) {
        initValidator();
        this.indexOfList = index;
        this.listDetails = listDetails;
        this.details = invoiceDetails;
        loadData();
        setUpdate(true);
        tableView.getSelectionModel().select(invoiceDetails.getItem());
        txtQty.getValueFactory().setValue(invoiceDetails.getQty());
        this.oldValue = txtQty.getValueFactory().getValue();
        txtPrice.getValueFactory().setValue(invoiceDetails.getPriceBuy());
        this.tableView.setDisable(true);
        this.validator.redecorate();
    }


}
