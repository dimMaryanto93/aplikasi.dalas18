package dallastools.actions.masterdata;

import dallastools.actions.HomeAction;
import dallastools.controllers.FxInitializable;
import dallastools.controllers.notifications.*;
import dallastools.controllers.stages.InnerScene;
import dallastools.models.Address;
import dallastools.models.masterdata.Customer;
import dallastools.services.ServiceOfCustomer;
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
 * Created by dimmaryanto on 10/1/15.
 */
public class CustomerDataAction implements FxInitializable {

    @FXML
    private TextField txtName;
    @FXML
    private TextField txtPhone;
    @FXML
    private TextField txtCity;
    @FXML
    private TextArea txtStreetAddress;
    @FXML
    private Spinner<Integer> txtPinCode;
    @FXML
    private Spinner<Integer> txtRt;
    @FXML
    private Spinner<Integer> txtRW;
    @FXML
    private TextField txtDistrict;
    @FXML
    private Button action;

    private DialogWindows windows;
    private DialogBalloon ballon;
    private ServiceOfCustomer service;
    private ValidationSupport validator;
    private ApplicationContext springContext;
    private MessageSource messageSource;
    private ValidatorMessages validatorMessages;
    private InnerScene innerScene;
    private Customer aCustomer;
    private boolean isUpdate;
    private LangSource lang;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtRt.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0));
        txtRt.getEditor().setAlignment(Pos.CENTER_RIGHT);
        txtRt.setEditable(true);

        txtRW.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0));
        txtRW.setEditable(true);
        txtRW.getEditor().setAlignment(Pos.CENTER_RIGHT);

        txtPinCode.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0, 50));
        txtPinCode.getEditor().setAlignment(Pos.CENTER_RIGHT);
        txtPinCode.setEditable(true);
    }

    private void initValidator() {
        validator = new ValidationSupport();
        validator.invalidProperty().addListener((observable, oldValue, newValue) -> action.setDisable(newValue));
        validator.registerValidator(txtName, true, Validator.createEmptyValidator(
                validatorMessages.validatorNotNull(lang.getSources(LangProperties.ID)), Severity.ERROR));
        validator.registerValidator(txtPhone, true, Validator.createEmptyValidator(
                validatorMessages.validatorNotNull(lang.getSources(LangProperties.CONTACT_PERSON)), Severity.ERROR));
    }


    public void clearFields() {
        txtName.clear();
        txtPhone.clear();
        txtCity.clear();
        txtStreetAddress.clear();
        txtPinCode.getValueFactory().setValue(0);
        txtRW.getValueFactory().setValue(0);
        txtRt.getValueFactory().setValue(0);
        txtDistrict.clear();
    }

    public void showFields(Customer aCustomer) {
        txtName.setText(aCustomer.getCustomerName());
        txtPhone.setText(aCustomer.getPhone());
        txtCity.setText(aCustomer.getAddress().getCity());
        txtStreetAddress.setText(aCustomer.getAddress().getStreetAddress());
        txtPinCode.getValueFactory().setValue(aCustomer.getAddress().getPinCode());
        txtRW.getValueFactory().setValue(aCustomer.getAddress().getRw());
        txtRt.getValueFactory().setValue(aCustomer.getAddress().getRt());
        txtDistrict.setText(aCustomer.getAddress().getDistrict());
    }


    @FXML
    private void doAction() {
        aCustomer.setAddress(new Address(
                txtStreetAddress.getText(),
                txtCity.getText(),
                txtPinCode.getValueFactory().getValue(),
                txtRt.getValueFactory().getValue(),
                txtRW.getValueFactory().getValue(),
                txtDistrict.getText()
        ));
        aCustomer.setCustomerName(txtName.getText());
        aCustomer.setPhone(txtPhone.getText());
        if (isUpdate) {
            try {
                service.update(aCustomer);
                ballon.sucessedUpdated(lang.getSources(LangProperties.DATA_A_CUSTOMER), lang.getSources(LangProperties.ID), aCustomer.getId());
                doClose();
            } catch (Exception e) {
                windows.errorUpdate(lang.getSources(LangProperties.DATA_A_CUSTOMER), lang.getSources(LangProperties.ID), aCustomer.getId(), e);
            }
        } else {
            try {
                service.save(aCustomer);
                ballon.sucessedSave(lang.getSources(LangProperties.DATA_A_CUSTOMER), lang.getSources(LangProperties.NAME), aCustomer.getCustomerName());
                newData();
            } catch (Exception e) {
                windows.errorSave(lang.getSources(LangProperties.DATA_A_CUSTOMER), aCustomer.getCustomerName(), e);
            }
        }
    }


    public void newData() {
        initValidator();
        setUpdate(false);
        aCustomer = new Customer();
        clearFields();
        this.validator.redecorate();
    }

    public void exitsData(Customer aCustomer) {
        initValidator();
        showFields(aCustomer);
        setUpdate(true);
        this.aCustomer = aCustomer;
        this.validator.redecorate();
    }

    private void setUpdate(boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    @Override
    public void doClose() {
        HomeAction homeAction = springContext.getBean(HomeAction.class);
        homeAction.showCustomers();
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
    public void setLang(LangSource lang) {
        this.lang = lang;
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
    public void setService(ServiceOfCustomer service) {
        this.service = service;
    }

    @Autowired
    public void setInnerScene(InnerScene innerScene) {
        this.innerScene = innerScene;
    }

    @Autowired
    public void setValidatorMessages(ValidatorMessages validatorMessages) {
        this.validatorMessages = validatorMessages;
    }
}
