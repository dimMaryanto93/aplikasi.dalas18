package dallastools.actions.application;

import dallastools.controllers.AutheticationLevel;
import dallastools.controllers.FxInitializable;
import dallastools.controllers.notifications.*;
import dallastools.controllers.stages.SecondStageController;
import dallastools.models.masterdata.Account;
import dallastools.models.other.Level;
import dallastools.services.ServiceOfAccount;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

/**
 * Created by dimMaryanto on 1/15/2016.
 */
public class RegisterAccountAction implements FxInitializable {
    @FXML
    private ChoiceBox<Level> txtType;
    @FXML
    private TextField txtUsername;
    @FXML
    private TextField txtFullname;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Button btnSave;

    private ApplicationContext springContext;
    private MessageSource messageSource;
    private ServiceOfAccount service;
    private DialogBalloon balloon;
    private DialogWindows windows;
    private LangSource lang;
    private ValidatorMessages validatorMessages;
    private ValidationSupport validationSupport;

    public void initValidation() {
        validationSupport = new ValidationSupport();
        validationSupport.invalidProperty().addListener((observable, oldValue, newValue) -> btnSave.setDisable(newValue));
        validationSupport.registerValidator(txtType,
                Validator.createEmptyValidator(
                        validatorMessages.validatorNotSelected(lang.getSources(LangProperties.LEVEL))));
        validationSupport.redecorate();
        validationSupport.registerValidator(txtFullname, true, Validator.createEmptyValidator(
                validatorMessages.validatorNotNull(lang.getSources(LangProperties.ACCOUNT_FULLNAME)), Severity.ERROR
        ));
        validationSupport.registerValidator(txtUsername, (Control textfield, String value) -> ValidationResult.fromErrorIf(
                textfield,
                validatorMessages.validatorMinMax(lang.getSources(LangProperties.USERNAME), 3, 25),
                value.trim().length() < 3 || value.trim().length() > 25 || value.trim().isEmpty()
        ));
        validationSupport.registerValidator(txtPassword, true,
                Validator.createEmptyValidator(
                        validatorMessages.validatorNotNull(lang.getSources(LangProperties.PASSWORD))));
    }

    @Override
    public void doClose() {
        SecondStageController controller = springContext.getBean(SecondStageController.class);
        controller.closeSecondStage();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.springContext = applicationContext;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtType.setItems(AutheticationLevel.getLevels());
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private void clearFields() {
        txtType.getSelectionModel().clearSelection();
        txtUsername.clear();
        txtFullname.clear();
        txtPassword.clear();
    }

    @FXML
    public void doSave() {
        Account anAccount = new Account();
        try {
            anAccount.setUsername(txtUsername.getText());
            anAccount.setFullname(txtFullname.getText());
            anAccount.setPasswd(txtPassword.getText());
            anAccount.setLevel(AutheticationLevel.getValue(txtType.getValue()));
            anAccount.setActive(false);
            anAccount.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
            service.save(anAccount);
            clearFields();
            this.doClose();
            balloon.sucessedSave(lang.getSources(LangProperties.DATA_AN_ACCOUNT), anAccount.getUsername());
        } catch (ConstraintViolationException e) {
            windows.errorSave(lang.getSources(LangProperties.DATA_AN_ACCOUNT), anAccount.getUsername(), e);
        } catch (Exception e) {
            windows.errorSave(lang.getSources(LangProperties.DATA_AN_ACCOUNT), e);
        }
    }

    @Autowired
    public void setService(ServiceOfAccount service) {
        this.service = service;
    }

    @Autowired
    public void setWindows(DialogWindows windows) {
        this.windows = windows;
    }

    @Autowired
    public void setBalloon(DialogBalloon balloon) {
        this.balloon = balloon;
    }

    @Autowired
    public void setLang(LangSource lang) {
        this.lang = lang;
    }

    @Autowired
    public void setValidatorMessages(ValidatorMessages validatorMessages) {
        this.validatorMessages = validatorMessages;
    }
}
