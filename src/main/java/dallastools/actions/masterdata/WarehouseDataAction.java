package dallastools.actions.masterdata;

import dallastools.actions.HomeAction;
import dallastools.controllers.FxInitializable;
import dallastools.controllers.notifications.*;
import dallastools.models.Address;
import dallastools.models.masterdata.Warehouse;
import dallastools.services.ServiceOfWarehouse;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by dimmaryanto on 20/10/15.
 */
public class WarehouseDataAction implements FxInitializable {

    @FXML
    private TextField txtName;
    @FXML
    private TextField txtPhone;
    @FXML
    private TextArea txtStreetAddress;
    @FXML
    private TextField txtCity;
    @FXML
    private TextField txtDistrict;
    @FXML
    private Spinner<Integer> txtRT;
    @FXML
    private Spinner<Integer> txtRW;
    @FXML
    private Spinner<Integer> txtPos;
    @FXML
    private Button btnSave;

    private ServiceOfWarehouse service;
    private DialogWindows windows;
    private DialogBalloon ballon;
    private ValidationSupport validator;
    private Warehouse aWarehouse;
    private Boolean isUpdate;
    private ApplicationContext springContext;
    private MessageSource messageSource;
    private ValidatorMessages validatorMessages;
    private LangSource lang;


    private void clearFields() {
        txtName.clear();
        txtPhone.clear();
        txtStreetAddress.clear();
        txtCity.clear();
        txtDistrict.clear();
        txtRT.getValueFactory().setValue(0);
        txtRW.getValueFactory().setValue(0);
        txtPos.getValueFactory().setValue(0);
    }

    private void showToFields(Warehouse aWarehouse) {
        txtName.setText(aWarehouse.getName());
        txtPhone.setText(aWarehouse.getPhoneNumber());
        Address anAddress = aWarehouse.getAddress();
        if (anAddress != null) {
            txtStreetAddress.setText(anAddress.getStreetAddress());
            txtCity.setText(anAddress.getCity());
            txtDistrict.setText(anAddress.getDistrict());
            txtRT.getValueFactory().setValue(anAddress.getRt());
            txtRW.getValueFactory().setValue(anAddress.getRw());
            txtPos.getValueFactory().setValue(anAddress.getPinCode());
        } else {
            txtStreetAddress.clear();
            txtCity.clear();
            txtDistrict.clear();
            txtRT.getValueFactory().setValue(0);
            txtRW.getValueFactory().setValue(0);
            txtPos.getValueFactory().setValue(0);
        }
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.springContext = applicationContext;
    }

    private void initValidator() {
        this.validator = new ValidationSupport();
        this.validator.invalidProperty().addListener((observable, oldValue, newValue) -> btnSave.setDisable(newValue));
        this.validator.registerValidator(txtName, true, Validator.createEmptyValidator(
                validatorMessages.validatorNotNull(lang.getSources(LangProperties.NAME)), Severity.ERROR));
        this.validator.registerValidator(txtPhone, true, Validator.createEmptyValidator(
                validatorMessages.validatorNotNull(lang.getSources(LangProperties.CONTACT_PERSON)), Severity.ERROR));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        txtRT.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0));
        txtRT.setEditable(true);
        txtRT.getEditor().setAlignment(Pos.CENTER_RIGHT);

        txtRW.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0));
        txtRW.setEditable(true);
        txtRW.getEditor().setAlignment(Pos.CENTER_RIGHT);

        txtPos.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0, 50));
        txtPos.setEditable(true);
        txtPos.getEditor().setAlignment(Pos.CENTER_RIGHT);
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void newData() {
        initValidator();
        setUpdate(false);
        clearFields();
        this.validator.redecorate();
    }

    @FXML
    public void doSave() {
        if (isUpdate) {
            try {
                aWarehouse.setAddress(getAddressValue());
                aWarehouse.setName(txtName.getText());
                aWarehouse.setPhoneNumber(txtPhone.getText());
                service.update(aWarehouse);
                doClose();
                ballon.sucessedUpdated(lang.getSources(LangProperties.DATA_A_WAREHOUSE), lang.getSources(LangProperties.ID), aWarehouse.getId());
            } catch (Exception e) {
                windows.errorUpdate(lang.getSources(LangProperties.DATA_A_WAREHOUSE), lang.getSources(LangProperties.ID), aWarehouse.getId(), e);
                e.printStackTrace();
            }
        } else {
            try {
                this.aWarehouse = new Warehouse(getAddressValue(), txtName.getText(), txtPhone.getText());
                service.save(this.aWarehouse);
                newData();
                ballon.sucessedSave(lang.getSources(LangProperties.DATA_A_WAREHOUSE), aWarehouse.getName());
            } catch (Exception e) {
                windows.errorSave(lang.getSources(LangProperties.DATA_A_WAREHOUSE), aWarehouse.getName(), e);
                e.printStackTrace();
            }
        }
    }

    private Address getAddressValue() {
        return new Address(
                txtStreetAddress.getText(),
                txtCity.getText(),
                txtPos.getValueFactory().getValue(),
                txtRT.getValueFactory().getValue(),
                txtRW.getValueFactory().getValue(),
                txtDistrict.getText());
    }

    public void setUpdate(Boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    @Override
    public void doClose() {
        HomeAction homeAction = springContext.getBean(HomeAction.class);
        homeAction.showWarehouse();
    }

    public void exitsData(Warehouse value) {
        initValidator();
        this.aWarehouse = value;
        showToFields(value);
        setUpdate(true);
        this.validator.redecorate();
    }

    @Autowired
    public void setService(ServiceOfWarehouse service) {
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
    public void setValidatorMessages(ValidatorMessages validatorMessages) {
        this.validatorMessages = validatorMessages;
    }

    @Autowired
    public void setLang(LangSource lang) {
        this.lang = lang;
    }
}
