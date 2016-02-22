package dallastools.actions.masterdata;

import dallastools.actions.HomeAction;
import dallastools.controllers.FxInitializable;
import dallastools.controllers.notifications.*;
import dallastools.models.masterdata.CategoryOfItem;
import dallastools.services.ServiceOfItemCategory;
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
public class CategoryOfItemDataAction implements FxInitializable {


    @FXML
    private Button btnSave;
    @FXML
    private TextField txtId;
    @FXML
    private TextField txtName;
    @FXML
    private TextArea txtDesc;

    private ServiceOfItemCategory service;
    private Boolean isUpdate;
    private CategoryOfItem item;
    private MessageSource messageSource;
    private ApplicationContext springContext;
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
                validatorMessages.validatorMinMax(lang.getSources(LangProperties.ID), 1, 50),
                o.toString().isEmpty() || o.toString().length() >= 50));
        this.validator.registerValidator(txtName, (control, o) -> ValidationResult.fromErrorIf(control,
                validatorMessages.validatorMinMax(lang.getSources(LangProperties.NAME), 1, 100),
                o.toString().isEmpty() || o.toString().length() >= 100));
        this.validator.registerValidator(txtDesc, false,
                Validator.createEmptyValidator(
                        validatorMessages.validatorNotNull(lang.getSources(LangProperties.DESCRIPTION)), Severity.WARNING));
        this.validator.invalidProperty().addListener((observable, oldValue, newValue) -> btnSave.setDisable(newValue));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void showToField(CategoryOfItem item) {
        txtId.setText(item.getId());
        txtName.setText(item.getName());
        txtDesc.setText(item.getDescription());
    }

    private void clearFields() {
        txtId.clear();
        txtName.clear();
        txtDesc.clear();
    }

    public void newData() {
        initValidator();
        item = new CategoryOfItem();
        clearFields();
        setUpdate(false);
        this.validator.redecorate();
    }

    public void existData(CategoryOfItem item) {
        initValidator();
        this.item = item;
        showToField(item);
        setUpdate(true);
        this.validator.redecorate();
    }

    @FXML
    private void doSave() {
        item.setId(txtId.getText().toUpperCase());
        item.setName(txtName.getText());
        item.setDescription(txtDesc.getText());
        if (isUpdate) {
            try {
                service.update(item);
                ballon.sucessedUpdated(lang.getSources(LangProperties.DATA_AN_ACCOUNT), lang.getSources(LangProperties.ID), item.getId());
                doClose();
            } catch (Exception e) {
                windows.errorUpdate(lang.getSources(LangProperties.DATA_AN_ACCOUNT), lang.getSources(LangProperties.ID), item.getId(), e);
                e.printStackTrace();
            }
        } else {
            try {
                service.save(item);
                ballon.sucessedSave(lang.getSources(LangProperties.DATA_AN_ACCOUNT), item.getName());
                newData();
            } catch (Exception e) {
                windows.errorSave(lang.getSources(LangProperties.DATA_AN_ACCOUNT), item.getName(), e);
                e.printStackTrace();
            }
        }
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
    public void setService(ServiceOfItemCategory service) {
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

    @Override
    public void doClose() {
        HomeAction home = springContext.getBean(HomeAction.class);
        home.showCategoriesOfItem();
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
