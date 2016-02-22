package dallastools.actions.settings;

import dallastools.actions.HomeAction;
import dallastools.controllers.AutheticationLevel;
import dallastools.controllers.FxInitializable;
import dallastools.controllers.dataselections.SecurityLevelAccessed;
import dallastools.controllers.notifications.*;
import dallastools.models.masterdata.Account;
import dallastools.models.other.Level;
import dallastools.services.ServiceOfAccount;
import javafx.fxml.FXML;
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
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

/**
 * Created by dimmaryanto on 11/10/15.
 */
public class AccountDataAction implements FxInitializable {
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

    private SecurityLevelAccessed security;
    private ServiceOfAccount service;
    private DialogWindows windows;
    private DialogBalloon ballon;
    private HomeAction homeAction;

    private ApplicationContext springContext;
    private MessageSource messageSource;
    private Account anAccount;
    private LangSource lang;
    private ValidationSupport validationSupport;
    private ValidatorMessages validatorMessages;

    private void initValidator() {
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

    public void newData() {
        initValidator();
        clearFields();
        this.anAccount = new Account();
        this.validationSupport.redecorate();
    }

    private void clearFields() {
        txtType.getSelectionModel().clearSelection();
        txtUsername.clear();
        txtPassword.clear();
        txtFullname.clear();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtType.setItems(AutheticationLevel.getLevels());
    }

    @FXML
    public void doSave() {
        anAccount.setActive(false);
        anAccount.setLevel(AutheticationLevel.getValue(txtType.getValue()));
        anAccount.setUsername(txtUsername.getText());
        anAccount.setFullname(txtFullname.getText());
        anAccount.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        anAccount.setPasswd(txtPassword.getText());
        try {
            service.save(anAccount);
            ballon.sucessedSave(lang.getSources(LangProperties.DATA_AN_ACCOUNT), anAccount.getUsername());
            newData();
        } catch (Exception e) {
            e.printStackTrace();
            windows.errorSave(lang.getSources(LangProperties.DATA_AN_ACCOUNT), e);
        }
    }

    @Override
    public void doClose() {
        homeAction.showAccounts();
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
    public void setSecurity(SecurityLevelAccessed security) {
        this.security = security;
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
    public void setBallon(DialogBalloon ballon) {
        this.ballon = ballon;
    }

    @Autowired
    public void setHomeAction(HomeAction homeAction) {
        this.homeAction = homeAction;
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
