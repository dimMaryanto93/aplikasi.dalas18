package dallastools.actions.masterdata;

import dallastools.actions.HomeAction;
import dallastools.controllers.FxInitializable;
import dallastools.controllers.notifications.*;
import dallastools.models.masterdata.CategoryOfPayment;
import dallastools.services.ServiceOfPaymentCategory;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
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
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

/**
 * Created by dimmaryanto on 18/10/15.
 */
public class PaymentCategoryDataAction implements FxInitializable {
    @FXML
    private TextField txtId;
    @FXML
    private TextField txtName;
    @FXML
    private TextArea txtDescription;
    @FXML
    private Button btnSave;

    private DialogBalloon ballon;
    private DialogWindows windows;
    private ServiceOfPaymentCategory service;
    private Boolean isUpdate;
    private CategoryOfPayment category;
    private ApplicationContext springContext;
    private MessageSource messageSource;
    private ValidationSupport validator;
    private ValidatorMessages validatorMessages;
    private LangSource lang;

    @Override
    public void doClose() {
        HomeAction action = springContext.getBean(HomeAction.class);
        action.showCategoryPayment();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.springContext = applicationContext;
    }

    private void initValidator() {
        this.validator = new ValidationSupport();
        this.validator.registerValidator(txtId, (Control kode, String value) -> ValidationResult.fromErrorIf(kode,
                validatorMessages.validatorMinMax(lang.getSources(LangProperties.ID), 1, 25),
                value.trim().isEmpty() || value.length() > 25));
        this.validator.registerValidator(txtName, true, Validator.createEmptyValidator(
                validatorMessages.validatorNotNull(lang.getSources(LangProperties.NAME)), Severity.ERROR));
        this.validator.registerValidator(txtDescription, false, Validator.createEmptyValidator(
                validatorMessages.validatorEmpty(lang.getSources(LangProperties.DESCRIPTION)), Severity.WARNING));
        this.validator.invalidProperty().addListener((observable, oldValue, newValue) -> btnSave.setDisable(newValue));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @FXML
    private void doSave() {
        if (isUpdate) {
            try {
                service.update(getValue(this.category));
                ballon.sucessedUpdated(lang.getSources(LangProperties.DATA_CATEGORY_OF_PAYMENT), lang.getSources(LangProperties.ID), category.getId());
                doClose();
            } catch (Exception e) {
                windows.errorUpdate(lang.getSources(LangProperties.DATA_CATEGORY_OF_PAYMENT), lang.getSources(LangProperties.ID), category.getId(), e);
                e.printStackTrace();
            }
        } else {
            try {
                this.category = getValue(new CategoryOfPayment());
                this.category.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
                service.save(this.category);
                clearFields();
                ballon.sucessedSave(lang.getSources(LangProperties.DATA_CATEGORY_OF_PAYMENT), category.getPaymentFor());
            } catch (Exception e) {
                windows.errorSave(lang.getSources(LangProperties.DATA_CATEGORY_OF_PAYMENT), category.getPaymentFor(), e);
                e.printStackTrace();
            }
        }
    }

    @Autowired
    public void setLang(LangSource lang) {
        this.lang = lang;
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
    public void setService(ServiceOfPaymentCategory service) {
        this.service = service;
    }

    @Autowired
    public void setValidatorMessages(ValidatorMessages validatorMessages) {
        this.validatorMessages = validatorMessages;
    }

    private void clearFields() {
        txtId.clear();
        txtName.clear();
        txtDescription.clear();
    }

    private CategoryOfPayment getValue(CategoryOfPayment category) {
        category.setId(txtId.getText());
        category.setPaymentFor(txtName.getText());
        category.setDescription(txtDescription.getText());
        return category;
    }

    private void setUpdate(Boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    public void newData() {
        initValidator();
        setUpdate(false);
        this.txtId.setEditable(true);
        this.validator.redecorate();
    }

    public void exitsData(CategoryOfPayment category) {
        initValidator();
        setUpdate(true);
        this.txtId.setEditable(false);
        this.category = category;
        txtId.setText(category.getId());
        txtName.setText(category.getPaymentFor());
        txtDescription.setText(category.getDescription());
        this.validator.redecorate();
    }
}
