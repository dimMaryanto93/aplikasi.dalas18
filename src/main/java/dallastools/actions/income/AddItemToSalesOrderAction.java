package dallastools.actions.income;

import dallastools.controllers.FxInitializable;
import dallastools.controllers.NumberFormatter;
import dallastools.controllers.notifications.*;
import dallastools.controllers.stages.SecondStageController;
import dallastools.models.income.SalesOrderDetails;
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
 * Created by dimmaryanto on 13/10/15.
 */
public class AddItemToSalesOrderAction implements FxInitializable {

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

    private ServiceOfItem service;
    private DialogBalloon ballon;
    private DialogWindows windows;
    private SpinnerValueFactory.IntegerSpinnerValueFactory qty;
    private ValidationSupport validator;
    private ApplicationContext springContext;
    private MessageSource messageSource;
    private Item anItem;
    private Boolean isUpdate;
    private SalesOrderDetails salesOrderDetails;
    private ObservableList<SalesOrderDetails> listOrder;
    private Integer indexOfItems;
    private ValidatorMessages validatorMessages;
    private LangSource lang;
    private Integer oldValueQTY;
    private NumberFormatter numberFormatter;

    private void initValidator() {
        this.validator = new ValidationSupport();
        this.validator.registerValidator(txtId, false, Validator.createEmptyValidator(
                validatorMessages.validatorNotSelected(lang.getSources(LangProperties.DATA_AN_ITEM)), Severity.ERROR));
        this.validator.registerValidator(txtQty.getEditor(), (Control control, String value) ->
                ValidationResult.fromErrorIf(control,
                        validatorMessages.validatorMin(1), value.equals("0")));
        this.validator.invalidProperty().addListener((observable, oldValue, newValue) -> btnAdd.setDisable(newValue));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtStok.setAlignment(Pos.CENTER_RIGHT);

        this.qty = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 0, 0);


        this.txtQty.setValueFactory(qty);
        txtQty.getEditor().setAlignment(Pos.CENTER_RIGHT);
        txtQty.setEditable(true);

        txtPrice.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, Double.MAX_VALUE, 0));
        txtPrice.getEditor().setAlignment(Pos.CENTER_RIGHT);
        txtPrice.setEditable(true);

        tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Item>() {
            @Override
            public void changed(ObservableValue<? extends Item> observable, Item oldValue, Item newValue) {
                if (newValue != null) {
                    anItem = newValue;
                    qty.setMin(0);
                    qty.setMax(Integer.MAX_VALUE);
                    showToFields(newValue);
                } else {
                    anItem = null;
                    clearSelection();
                    qty.setValue(0);
                    qty.setMin(0);
                    qty.setMax(0);
                }
            }
        });
        columnId.setCellValueFactory(new PropertyValueFactory<Item, String>("id"));
        columnName.setCellValueFactory(new PropertyValueFactory<Item, String>("name"));
    }


    @Override
    public void doClose() {
        SecondStageController theStage = springContext.getBean(SecondStageController.class);
        theStage.closeSecondStage();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.springContext = applicationContext;
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
    public void doAdd() {
        if (isUpdate) {
            salesOrderDetails.setItem(anItem);
            salesOrderDetails.setQty(txtQty.getValueFactory().getValue());
            this.listOrder.set(indexOfItems, salesOrderDetails);
            ballon.sucessedUpdated(lang.getSources(LangProperties.DATA_AN_ITEM), lang.getSources(LangProperties.NAME),
                    anItem.getName(), oldValueQTY, salesOrderDetails.getQty());
            doClose();
        } else {
            salesOrderDetails = new SalesOrderDetails();
            salesOrderDetails.setItem(anItem);
            salesOrderDetails.setQty(txtQty.getValueFactory().getValue());
            this.listOrder.add(salesOrderDetails);
            ballon.sucessedSave(lang.getSources(LangProperties.DATA_AN_ITEM), anItem.getName());
            clearSelection();
        }
    }

    @Autowired
    public void setService(ServiceOfItem service) {
        this.service = service;
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

    public void loadItemToSell() {
        try {
            tableView.getItems().clear();
            List<Item> list = service.findByItemIsSell(true);
            for (Item anItem : list) {
                tableView.getItems().add(anItem);
            }
        } catch (Exception e) {
            windows.errorLoading(lang.getSources(LangProperties.LIST_OF_ITEMS), e);
            e.printStackTrace();
        }
    }

    private void setUpdate(Boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    private void clearSelection() {
        txtQty.getValueFactory().setValue(0);
        txtWarehouse.clear();
        txtId.clear();
        txtName.clear();
        txtCategory.clear();
        txtUnit.clear();
        txtStok.clear();
        txtPrice.getValueFactory().setValue(0.0);
        tableView.getSelectionModel().clearSelection();
    }

    private void showToFields(Item anItem) {
        Unit anUnit = anItem.getUnit();
        CategoryOfItem aCategory = anItem.getCategory();
        Warehouse aWarehouse = anItem.getWarehouse();
        txtId.setText(anItem.getId());
        txtName.setText(anItem.getName());

        if (aCategory != null) txtCategory.setText(aCategory.getName());
        else txtCategory.clear();

        if (anUnit != null) txtUnit.setText(anUnit.getId());
        else txtUnit.clear();

        if (aWarehouse != null) txtWarehouse.setText(aWarehouse.getName());
        else txtWarehouse.clear();

        txtStok.setText(numberFormatter.getNumber(anItem.getQty()));
        txtPrice.getValueFactory().setValue(anItem.getPriceSell());
    }

    public void newData(ObservableList<SalesOrderDetails> listOrder) {
        initValidator();
        loadItemToSell();
        setUpdate(false);
        this.listOrder = listOrder;
        this.validator.redecorate();
        this.tableView.setDisable(false);
    }

    public void exitsData(int index, SalesOrderDetails anOrder, ObservableList<SalesOrderDetails> list) {
        initValidator();
        loadItemToSell();
        this.indexOfItems = index;
        setUpdate(true);
        this.listOrder = list;
        this.salesOrderDetails = anOrder;
        this.tableView.setDisable(true);
        tableView.getSelectionModel().select(anOrder.getItem());
        txtQty.getValueFactory().setValue(anOrder.getQty());
        this.oldValueQTY = txtQty.getValueFactory().getValue();
        this.validator.redecorate();
    }

}
