package dallastools.actions.masterdata;

import dallastools.actions.HomeAction;
import dallastools.controllers.FxInitializable;
import dallastools.controllers.dataselections.WarehouseChooser;
import dallastools.controllers.notifications.*;
import dallastools.models.masterdata.CategoryOfItem;
import dallastools.models.masterdata.Item;
import dallastools.models.masterdata.Unit;
import dallastools.models.masterdata.Warehouse;
import dallastools.services.ServiceOfItem;
import dallastools.services.ServiceOfItemCategory;
import dallastools.services.ServiceOfUnit;
import dallastools.services.ServiceOfWarehouse;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ResourceBundle;

/**
 * Created by dimmaryanto on 08/10/15.
 */
public class ItemDataAction implements FxInitializable {

    @FXML
    private RadioButton txtOnSale;
    @FXML
    private RadioButton txtNotForSale;
    @FXML
    private ToggleGroup isSale;
    @FXML
    private Button btnSave;
    @FXML
    private ChoiceBox<String> txtCategory;
    @FXML
    private ComboBox<String> txtUnit;
    @FXML
    private TextField txtName;
    @FXML
    private Spinner<Double> txtPriceSell;
    @FXML
    private ComboBox txtWarehouse;
    @FXML
    private Spinner<Double> txtPriceBuy;

    private HashMap<String, CategoryOfItem> categoryMap;
    private HashMap<String, Unit> unitMap;
    private HashMap<String, Warehouse> warehouseMap;
    private WarehouseChooser warehouseChooser;
    private DialogBalloon ballon;
    private DialogWindows windows;
    private ServiceOfItem serviceOfItem;
    private ServiceOfItemCategory serviceOfItemCategory;
    private ServiceOfUnit serviceOfUnit;
    private ServiceOfWarehouse serviceOfWarehouse;
    private ValidationSupport validator;
    private ApplicationContext springContext;
    private MessageSource messageSource;
    private Boolean isUpdate;
    private Item anItem;
    private ValidatorMessages validatorMessages;
    private LangSource lang;

    private void initValidator() {
        this.validator = new ValidationSupport();
        this.validator.registerValidator(txtName, (Control c, String value) -> ValidationResult.fromErrorIf(c,
                validatorMessages.validatorMinMax(lang.getSources(LangProperties.NAME), 3, 25),
                value.trim().isEmpty() || value.trim().length() < 3 || value.trim().length() > 25));
        this.validator.registerValidator(txtCategory, true, Validator.createEmptyValidator(
                validatorMessages.validatorNotSelected(lang.getSources(LangProperties.CATEGORY)), Severity.ERROR));
        this.validator.registerValidator(txtUnit, true,
                Validator.createEmptyValidator(
                        validatorMessages.validatorNotSelected(lang.getSources(LangProperties.UNIT)), Severity.ERROR));
        this.validator.registerValidator(txtWarehouse, true, Validator.createEmptyValidator(
                validatorMessages.validatorNotSelected(lang.getSources(LangProperties.WAREHOUSE)), Severity.ERROR));
        this.validator.invalidProperty().addListener((observable, oldValue, newValue) -> btnSave.setDisable(newValue));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtPriceSell.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, Double.MAX_VALUE, 0, 500));
        txtPriceSell.setEditable(true);
        txtPriceSell.getEditor().setAlignment(Pos.CENTER_RIGHT);

        txtPriceBuy.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, Double.MAX_VALUE, 0, 500));
        txtPriceBuy.setEditable(true);
        txtPriceBuy.getEditor().setAlignment(Pos.CENTER_RIGHT);
    }

    private void clearField() {
        txtCategory.getSelectionModel().clearSelection();
        txtUnit.getSelectionModel().clearSelection();
        txtWarehouse.getSelectionModel().clearSelection();
        txtName.clear();
        txtPriceSell.getValueFactory().setValue(0.0);
        txtPriceBuy.getValueFactory().setValue(0.0);
        txtOnSale.setSelected(false);
        txtNotForSale.setSelected(true);
    }

    private void showToFields(Item anItem) {
        Unit anUnit = anItem.getUnit();
        CategoryOfItem anCategoryOfItem = anItem.getCategory();
        Warehouse aWarehouse = anItem.getWarehouse();
        if (anUnit != null) txtUnit.setValue(anItem.getUnit().getId());
        else txtUnit.getSelectionModel().clearSelection();
        if (anCategoryOfItem != null) txtCategory.setValue(anItem.getCategory().getId());
        else txtCategory.getSelectionModel().clearSelection();
        if (aWarehouse != null) txtWarehouse.setValue(warehouseChooser.getKey(aWarehouse));
        else txtWarehouse.getSelectionModel().clearSelection();
        if (anItem.getSell()) txtOnSale.setSelected(true);
        else txtNotForSale.setSelected(true);
        txtName.setText(anItem.getName());
        txtPriceSell.getValueFactory().setValue(anItem.getPriceSell());
        txtPriceBuy.getValueFactory().setValue(anItem.getPriceBuy());
    }

    public void loadData() {
        warehouseMap.clear();
        txtWarehouse.getItems().clear();
        categoryMap.clear();
        unitMap.clear();
        txtUnit.getItems().clear();
        txtCategory.getItems().clear();
        try {
            Iterator<Unit> listUnit = serviceOfUnit.findAll().iterator();
            while (listUnit.hasNext()) {
                Unit anUnit = listUnit.next();
                unitMap.put(anUnit.getId(), anUnit);
                txtUnit.getItems().add(anUnit.getId());
            }
            Iterator<CategoryOfItem> listCategory = serviceOfItemCategory.findAll().iterator();
            while (listCategory.hasNext()) {
                CategoryOfItem aCategory = listCategory.next();
                categoryMap.put(aCategory.getId(), aCategory);
                txtCategory.getItems().add(aCategory.getId());
            }
            Iterator<Warehouse> listWarehouse = serviceOfWarehouse.findAll().iterator();
            while (listWarehouse.hasNext()) {
                Warehouse aWarehouse = listWarehouse.next();
                String key = warehouseChooser.getKey(aWarehouse);
                warehouseMap.put(key, aWarehouse);
                txtWarehouse.getItems().add(key);
            }
        } catch (Exception e) {
            windows.errorLoading(lang.getSources(LangProperties.LIST_OF_ITEM_CATEGORIES), e);
            e.printStackTrace();
        }

    }

    public void newData() {
        initValidator();
        loadData();
        clearField();
        setUpdate(false);
        anItem = new Item();
        this.validator.redecorate();
    }

    public void exitsData(Item anItem) {
        initValidator();
        loadData();
        setUpdate(true);
        this.anItem = anItem;
        showToFields(anItem);
        this.validator.redecorate();
    }

    private String getIdOfItem(String value) {
        StringBuilder builder = new StringBuilder();
        builder.append(warehouseMap.get(txtWarehouse.getValue()).getId());
        builder.append("-");
        builder.append(categoryMap.get(txtCategory.getValue()).getId().toUpperCase());
        builder.append("-");
        builder.append(value);
        return builder.toString();
    }

    private void setUpdate(Boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    @FXML
    public void doSave() {
        anItem.setId(getIdOfItem(txtName.getText().toUpperCase()));
        anItem.setName(txtName.getText());
        anItem.setCategory(categoryMap.get(txtCategory.getValue()));
        anItem.setUnit(unitMap.get(txtUnit.getValue()));
        anItem.setPriceBuy(txtPriceBuy.getValueFactory().getValue());
        anItem.setPriceSell(txtPriceSell.getValueFactory().getValue());
        anItem.setWarehouse(warehouseMap.get(txtWarehouse.getValue()));
        anItem.setSell(txtOnSale.isSelected());
        if (isUpdate) {
            try {
                serviceOfItem.update(anItem);
                ballon.sucessedUpdated(lang.getSources(LangProperties.DATA_AN_ITEM), lang.getSources(LangProperties.ID), anItem.getId());
                doClose();
            } catch (Exception e) {
                windows.errorUpdate(lang.getSources(LangProperties.DATA_AN_ITEM), lang.getSources(LangProperties.ID), anItem.getId(), e);
                e.printStackTrace();
            }
        } else {
            try {
                anItem.setQty(0);
                serviceOfItem.save(anItem);
                ballon.sucessedSave(lang.getSources(LangProperties.DATA_AN_ITEM), anItem.getName());
                newData();
            } catch (Exception e) {
                windows.errorSave(lang.getSources(LangProperties.DATA_AN_ITEM), anItem.getName(), e);
                e.printStackTrace();
            }
        }
    }

    @Autowired
    public void setBallon(DialogBalloon ballon) {
        this.ballon = ballon;
    }

    @Autowired
    public void setCategoryMap(HashMap<String, CategoryOfItem> categoryMap) {
        this.categoryMap = categoryMap;
    }

    @Autowired
    public void setServiceOfItem(ServiceOfItem serviceOfItem) {
        this.serviceOfItem = serviceOfItem;
    }

    @Autowired
    public void setServiceOfItemCategory(ServiceOfItemCategory serviceOfItemCategory) {
        this.serviceOfItemCategory = serviceOfItemCategory;
    }

    @Autowired
    public void setServiceOfUnit(ServiceOfUnit serviceOfUnit) {
        this.serviceOfUnit = serviceOfUnit;
    }

    @Autowired
    public void setServiceOfWarehouse(ServiceOfWarehouse serviceOfWarehouse) {
        this.serviceOfWarehouse = serviceOfWarehouse;
    }

    @Autowired
    public void setValidatorMessages(ValidatorMessages validatorMessages) {
        this.validatorMessages = validatorMessages;
    }

    @Autowired
    public void setWarehouseChooser(WarehouseChooser warehouseChooser) {
        this.warehouseChooser = warehouseChooser;
    }

    @Autowired
    public void setWarehouseMap(HashMap<String, Warehouse> warehouseMap) {
        this.warehouseMap = warehouseMap;
    }

    @Autowired
    public void setWindows(DialogWindows windows) {
        this.windows = windows;
    }

    @Autowired
    public void setUnitMap(HashMap<String, Unit> unitMap) {
        this.unitMap = unitMap;
    }

    @Autowired
    public void setLang(LangSource lang) {
        this.lang = lang;
    }

    @Override
    public void doClose() {
        HomeAction homeAction = springContext.getBean(HomeAction.class);
        homeAction.showItems();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.springContext = applicationContext;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
