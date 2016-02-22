package dallastools.actions.masterdata;

import dallastools.actions.HomeAction;
import dallastools.controllers.FxInitializable;
import dallastools.controllers.notifications.*;
import dallastools.models.Address;
import dallastools.models.masterdata.Suplayer;
import dallastools.services.ServiceOfSuplayer;
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
 * Created by dimmaryanto on 10/10/15.
 */
public class SuplayerDataAction implements FxInitializable {

    @FXML
    private Button btnSave;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtPhone;
    @FXML
    private TextField txtCity;
    @FXML
    private TextField txtDistrict;
    @FXML
    private Spinner<Integer> txtRt;
    @FXML
    private Spinner<Integer> txtRw;
    @FXML
    private Spinner<Integer> txtPinCode;
    @FXML
    private TextArea txtStreetAddress;

    private DialogBalloon ballon;
    private DialogWindows windows;
    private ServiceOfSuplayer service;
    private ValidationSupport validator;
    private ApplicationContext springContext;
    private MessageSource messageSource;
    private Suplayer aSuplayer;
    private Boolean isUpdate;
    private ValidatorMessages validatorMessages;
    private LangSource lang;

    private void initValidator() {
        validator = new ValidationSupport();
        validator.invalidProperty().addListener((observable, oldValue, newValue) -> btnSave.setDisable(newValue));
        validator.registerValidator(txtName, true, Validator.createEmptyValidator(
                validatorMessages.validatorNotNull(lang.getSources(LangProperties.NAME)), Severity.ERROR));
        validator.registerValidator(txtPhone, true, Validator.createEmptyValidator(
                validatorMessages.validatorNotNull(lang.getSources(LangProperties.CONTACT_PERSON)), Severity.ERROR));
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        txtRt.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0));
        txtRt.setEditable(true);
        txtRt.getEditor().setAlignment(Pos.CENTER_RIGHT);

        txtRw.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0));
        txtRw.setEditable(true);
        txtRw.getEditor().setAlignment(Pos.CENTER_RIGHT);

        txtPinCode.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0, 50));
        txtPinCode.setEditable(true);
        txtPinCode.getEditor().setAlignment(Pos.CENTER_RIGHT);
    }

    @FXML
    private void doSave() {
        aSuplayer.setName(txtName.getText());
        aSuplayer.setPhone(txtPhone.getText());
        aSuplayer.setAddress(createAddress());
        if (isUpdate) {
            try {
                service.update(aSuplayer);
                ballon.sucessedUpdated(lang.getSources(LangProperties.DATA_A_SUPLAYER), lang.getSources(LangProperties.ID), aSuplayer.getSuplayerId());
                doClose();
            } catch (Exception e) {
                windows.errorUpdate(lang.getSources(LangProperties.DATA_A_SUPLAYER), lang.getSources(LangProperties.ID), aSuplayer.getSuplayerId(), e);
                e.printStackTrace();
            }
        } else {
            try {
                service.save(aSuplayer);
                ballon.sucessedSave(lang.getSources(LangProperties.DATA_A_SUPLAYER), aSuplayer.getName());
                newData();
            } catch (Exception e) {
                e.printStackTrace();
                windows.errorSave(lang.getSources(LangProperties.DATA_A_SUPLAYER), aSuplayer.getName(), e);
            }
        }
    }


    @Override
    public void doClose() {
        HomeAction homeAction = springContext.getBean(HomeAction.class);
        homeAction.showSuplayers();
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
    public void setService(ServiceOfSuplayer service) {
        this.service = service;
    }

    @Autowired
    public void setValidatorMessages(ValidatorMessages validatorMessages) {
        this.validatorMessages = validatorMessages;
    }

    @Autowired
    public void setLang(LangSource lang) {
        this.lang = lang;
    }

    private void setUpdate(Boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    private void clearFields() {
        txtName.clear();
        txtPhone.clear();
        txtCity.clear();
        txtDistrict.clear();
        txtRt.getValueFactory().setValue(0);
        txtRw.getValueFactory().setValue(0);
        txtPinCode.getValueFactory().setValue(0);
        txtStreetAddress.clear();
    }

    private void showToFields(Suplayer aSuplayer) {
        txtName.setText(aSuplayer.getName());
        txtPhone.setText(aSuplayer.getPhone());
        txtCity.setText(aSuplayer.getAddress().getCity());
        txtDistrict.setText(aSuplayer.getAddress().getDistrict());
        txtRt.getValueFactory().setValue(aSuplayer.getAddress().getRt());
        txtRw.getValueFactory().setValue(aSuplayer.getAddress().getRw());
        txtPinCode.getValueFactory().setValue(aSuplayer.getAddress().getPinCode());
        txtStreetAddress.setText(aSuplayer.getAddress().getStreetAddress());
    }

    public void newData() {
        initValidator();
        aSuplayer = new Suplayer();
        setUpdate(false);
        clearFields();
        this.validator.redecorate();
    }

    public void exitsData(Suplayer aSuplayer) {
        initValidator();
        setUpdate(true);
        this.aSuplayer = aSuplayer;
        showToFields(aSuplayer);
        this.validator.redecorate();
    }

    private Address createAddress() {
        return new Address(
                txtStreetAddress.getText(),
                txtCity.getText(),
                txtPinCode.getValueFactory().getValue(),
                txtRt.getValueFactory().getValue(),
                txtRw.getValueFactory().getValue(),
                txtDistrict.getText());
    }
}
