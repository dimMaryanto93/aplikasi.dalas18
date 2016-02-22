package dallastools.actions.income;

import dallastools.controllers.FxInitializable;
import dallastools.controllers.NumberFormatter;
import dallastools.controllers.notifications.*;
import dallastools.controllers.stages.SecondStageController;
import dallastools.models.income.SalesDetails;
import dallastools.models.masterdata.CategoryOfItem;
import dallastools.models.masterdata.Item;
import dallastools.models.masterdata.Unit;
import dallastools.models.masterdata.Warehouse;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by dimmaryanto on 14/10/15.
 */
public class AddItemToSalesInvoiceAction implements FxInitializable {

    private final Logger log = LoggerFactory.getLogger(AddItemToSalesInvoiceAction.class);

    @FXML
    private TextField txtWarehouse;
    @FXML
    private Button btnAdd;
    @FXML
    private TableView<Item> tableView;
    @FXML
    private TableColumn<Item, String> columnId;
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

    private ApplicationContext springContext;
    private MessageSource messageSource;
    private DialogBalloon ballon;
    private DialogWindows windows;
    private Boolean isUpdate;
    private ObservableList<SalesDetails> emptyList;
    private SpinnerValueFactory.DoubleSpinnerValueFactory priceValueFactory;
    private SpinnerValueFactory.IntegerSpinnerValueFactory qtyValueFactory;
    private SalesDetails salesDetails;
    private int indexOfList;
    private SecondStageController secondStage;
    private ValidationSupport validator;
    private ValidatorMessages validatorMessages;
    private LangSource lang;
    private Integer oldQtyValue;
    private NumberFormatter numberFormatter;

    private void initValidator() {
        validator = new ValidationSupport();
        validator.registerValidator(txtId, false, Validator.createEmptyValidator(
                validatorMessages.validatorNotSelected(lang.getSources(LangProperties.DATA_AN_ITEM)), Severity.ERROR));
        validator.registerValidator(txtQty.getEditor(), (Control c, String value) ->
                ValidationResult.fromErrorIf(c, validatorMessages.validatorMin(1), value.equals("0")));
        validator.invalidProperty().addListener((observable, oldValue, newValue) -> btnAdd.setDisable(newValue));
    }

    private void clearFields() {
        txtId.clear();
        txtName.clear();
        txtCategory.clear();
        txtUnit.clear();
        txtStok.clear();
    }

    private void showToFields(Item anItem) {
        txtId.setText(anItem.getId());
        txtName.setText(anItem.getName());

        CategoryOfItem category = anItem.getCategory();
        if (category != null) txtCategory.setText(category.getName());
        else txtCategory.clear();

        Unit anUnit = anItem.getUnit();
        if (anUnit != null) txtUnit.setText(anUnit.getId());
        else txtUnit.clear();

        Warehouse warehouse = anItem.getWarehouse();
        if (warehouse != null) txtWarehouse.setText(warehouse.getName());
        else txtWarehouse.clear();

        txtStok.setText(numberFormatter.getNumber(anItem.getQty()));
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        priceValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 0, 0, 500);
        txtPrice.getEditor().setAlignment(Pos.CENTER_RIGHT);
        txtPrice.setEditable(true);
        txtPrice.setValueFactory(priceValueFactory);
        txtPrice.setDisable(true);

        this.qtyValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 0, 0, 5);
        this.txtQty.getEditor().setAlignment(Pos.CENTER_RIGHT);
        this.txtQty.setEditable(true);
        this.txtQty.setValueFactory(qtyValueFactory);
        txtQty.setDisable(true);

        txtStok.setAlignment(Pos.CENTER_RIGHT);

        columnId.setCellValueFactory(new PropertyValueFactory<Item, String>("id"));
        columnName.setCellValueFactory(new PropertyValueFactory<Item, String>("name"));


        tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Item>() {
            @Override
            public void changed(ObservableValue<? extends Item> observable, Item oldValue, Item newValue) {
                Boolean isNotNull = newValue != null;
                txtQty.setDisable(!isNotNull);
                txtPrice.setDisable(!isNotNull);
                if (isNotNull) {
                    showToFields(newValue);
                    setQtyValueFactory(0, newValue.getQty(), 0);
                    setPriceValueFactory(newValue.getPriceSell(), Double.MAX_VALUE, 0.0);
                } else {
                    setQtyValueFactory(0, 0, 0);
                    setPriceValueFactory(.0, .0, .0);
                    clearFields();
                }
            }
        });

        txtQty.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                Item anItem = tableView.getSelectionModel().getSelectedItem();
                if (anItem != null) {
                    Integer qty = anItem.getQty() - newValue;
                    txtStok.setText(numberFormatter.getNumber(qty));
                }
            }
        });

    }

    private void setPriceValueFactory(Double value, Double max, Double min) {
        priceValueFactory.setMax(max);
        priceValueFactory.setMin(min);
        priceValueFactory.setValue(value);
    }

    private void setQtyValueFactory(Integer value, Integer max, Integer min) {
        qtyValueFactory.setValue(value);
        qtyValueFactory.setMax(max);
        qtyValueFactory.setMin(min);
    }


    @FXML
    private void doAdd() {
        Item anItem = tableView.getSelectionModel().getSelectedItem();
        if (isUpdate) {
            this.salesDetails.setQty(txtQty.getValueFactory().getValue());
            this.salesDetails.setPriceSell(txtPrice.getValueFactory().getValue());
            /*log.info("Barang {} stok awal {} - {} = {}", new Object[]{anItem.getName(),
                    anItem.getQty(), salesDetails.getQty(), (anItem.getQty() - salesDetails.getQty())});*/
            anItem.setQty(anItem.getQty() - salesDetails.getQty());

            emptyList.set(indexOfList, salesDetails);
            ballon.sucessedUpdated(lang.getSources(LangProperties.DATA_AN_ITEM),
                    lang.getSources(LangProperties.QTY), anItem.getName(),
                    oldQtyValue, salesDetails.getQty());
            doClose();
        } else {
            this.salesDetails = new SalesDetails();
            salesDetails.setId(tableView.getSelectionModel().getFocusedIndex());
            salesDetails.setItem(anItem);
            salesDetails.setQty(txtQty.getValueFactory().getValue());
            salesDetails.setPriceSell(txtPrice.getValueFactory().getValue());
            /*log.info("Barang {} stok awal {} - {} = {}", new Object[]{anItem.getName(),
                    anItem.getQty(), salesDetails.getQty(), (anItem.getQty() - salesDetails.getQty())});*/
            anItem.setQty(anItem.getQty() - salesDetails.getQty());

            emptyList.add(salesDetails);
            ballon.sucessedSave(lang.getSources(LangProperties.DATA_AN_ITEM), anItem.getName());
            tableViewClearSelection();
        }
    }

    @FXML
    private void tableViewClearSelection() {
        tableView.getSelectionModel().clearSelection();
    }

    @Override
    public void doClose() {
        secondStage.closeSecondStage();
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
    public void setBallon(DialogBalloon ballon) {
        this.ballon = ballon;
    }

    @Autowired
    public void setWindows(DialogWindows windows) {
        this.windows = windows;
    }

    @Autowired
    public void setSecondStage(SecondStageController secondStage) {
        this.secondStage = secondStage;
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

    private void setUpdate(Boolean isUpdate) {
        this.isUpdate = isUpdate;
    }


    public void newData(ObservableList<Item> from, ObservableList<SalesDetails> emptyList) {
        initValidator();
        tableView.getItems().clear();
        this.emptyList = emptyList;

        tableView.setItems(from);
        setUpdate(false);
        this.validator.redecorate();
    }

    public void exitsData(ObservableList<Item> from, ObservableList<SalesDetails> emptyList, SalesDetails salesDetails, int index) {
        initValidator();
        tableView.getItems().clear();
        this.salesDetails = salesDetails;
        this.emptyList = emptyList;
        this.indexOfList = index;

        tableView.setItems(from);
        setUpdate(true);
        tableView.getSelectionModel().select(salesDetails.getItem());
        txtQty.getValueFactory().setValue(salesDetails.getQty());
        this.oldQtyValue = txtQty.getValueFactory().getValue();
        this.validator.redecorate();

    }
}
