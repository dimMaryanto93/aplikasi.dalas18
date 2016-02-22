package dallastools.actions.masterdata;

import dallastools.actions.HomeAction;
import dallastools.controllers.FxInitializable;
import dallastools.controllers.notifications.*;
import dallastools.models.masterdata.Unit;
import dallastools.services.ServiceOfUnit;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by dimmaryanto on 08/10/15.
 */
public class UnitOfItemDataAction implements FxInitializable {
    @FXML
    private Button btnSave;
    @FXML
    private TextField txtId;
    @FXML
    private TextField txtName;
    @FXML
    private TextArea txtDesc;

    private ApplicationContext springContext;
    private MessageSource messageSource;
    private ServiceOfUnit service;
    private Unit anUnit;
    private Boolean isUpdate;
    private ValidationSupport validator;
    private DialogBalloon ballon;
    private DialogWindows windows;
    private ValidatorMessages validatorMessages;
    private LangSource lang;

    public void setUpdate(Boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    private void initValidator() {
        this.validator = new ValidationSupport();
        this.validator.registerValidator(txtId, (control, o) -> ValidationResult.fromErrorIf(control,
                validatorMessages.validatorMinMax(lang.getSources(LangProperties.ID), 1, 50), o.toString().isEmpty() || o.toString().length() >= 50));
        this.validator.registerValidator(txtName, (control, o) -> ValidationResult.fromErrorIf(control,
                validatorMessages.validatorMinMax(lang.getSources(LangProperties.NAME), 1, 100), o.toString().isEmpty() || o.toString().length() >= 100));
        this.validator.registerValidator(txtDesc, false, Validator.createEmptyValidator(
                validatorMessages.validatorEmpty(lang.getSources(LangProperties.DESCRIPTION)), Severity.WARNING));
        this.validator.invalidProperty().addListener((observable, oldValue, newValue) -> btnSave.setDisable(newValue));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    private void clearFields() {
        txtId.clear();
        txtName.clear();
        txtDesc.clear();
    }

    public void showToFields(Unit anUnit) {
        txtId.setText(anUnit.getId());
        txtName.setText(anUnit.getName());
        txtDesc.setText(anUnit.getDescription());
    }

    public void newData() {
        initValidator();
        setUpdate(false);
        this.anUnit = new Unit();
        clearFields();
        this.validator.redecorate();
    }

    public void exitsData(Unit anUnit) {
        initValidator();
        setUpdate(true);
        this.anUnit = anUnit;
        showToFields(anUnit);
        this.validator.redecorate();
    }

    @FXML
    private void doSave() {
        anUnit.setId(txtId.getText().toUpperCase());
        anUnit.setName(txtName.getText());
        anUnit.setDescription(txtDesc.getText());
        if (isUpdate) {
            try {
                service.udpate(anUnit);
                ballon.sucessedUpdated(lang.getSources(LangProperties.DATA_AN_UNIT_OF_ITEM), lang.getSources(LangProperties.NAME), anUnit.getName());
                doClose();
            } catch (Exception e) {
                windows.errorUpdate(lang.getSources(LangProperties.DATA_AN_UNIT_OF_ITEM), lang.getSources(LangProperties.NAME), anUnit.getName(), e);
                e.printStackTrace();
            }
        } else {
            try {
                service.save(anUnit);
                ballon.sucessedSave(lang.getSources(LangProperties.DATA_AN_UNIT_OF_ITEM), anUnit.getName());
                newData();
            } catch (Exception e) {
                windows.errorSave(lang.getSources(LangProperties.DATA_AN_UNIT_OF_ITEM), anUnit.getName(), e);
                e.printStackTrace();
            }
        }
    }

    @Autowired
    public void setWindows(DialogWindows windows) {
        this.windows = windows;
    }

    @Autowired
    public void setService(ServiceOfUnit service) {
        this.service = service;
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

    @Override
    public void doClose() {
        HomeAction homeAction = springContext.getBean(HomeAction.class);
        homeAction.showUnitOfItem();
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
