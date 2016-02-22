package dallastools.actions;

import dallastools.controllers.FxInitializable;
import dallastools.controllers.notifications.*;
import dallastools.controllers.stages.SecondStageController;
import dallastools.models.masterdata.Account;
import dallastools.services.ServiceOfAccount;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.controlsfx.validation.Severity;
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

public class LoginAction implements FxInitializable {

    @FXML
    private Button login;
    @FXML
    private TextField txtUid;
    @FXML
    private PasswordField txtPasswd;

    private ApplicationContext springContext;
    private MessageSource messageSouce;
    private SecondStageController controller;
    private ServiceOfAccount service;
    private DialogWindows windows;
    private DialogBalloon balloon;
    private LangSource lang;
    private ValidationSupport validation;
    private ValidatorMessages validatorMessages;

    public void initValidator() {
        this.validation = new ValidationSupport();
        this.validation.registerValidator(txtUid,
                Validator.createEmptyValidator(validatorMessages.validatorNotNull(lang.getSources(LangProperties.USERNAME)), Severity.ERROR));
        this.validation.registerValidator(txtPasswd,
                Validator.createEmptyValidator(validatorMessages.validatorNotNull(lang.getSources(LangProperties.PASSWORD)), Severity.ERROR));
        this.validation.invalidProperty().addListener((observable, oldValue, newValue) -> login.setDisable(newValue));
    }

    @Autowired
    public void setValidatorMessages(ValidatorMessages validatorMessages) {
        this.validatorMessages = validatorMessages;
    }

    @Autowired
    public void setLang(LangSource lang) {
        this.lang = lang;
    }

    @Autowired
    public void setBalloon(DialogBalloon balloon) {
        this.balloon = balloon;
    }

    @Autowired
    public void setWindows(DialogWindows windows) {
        this.windows = windows;
    }

    @Autowired
    public void setService(ServiceOfAccount service) {
        this.service = service;
    }

    @Autowired
    public void setController(SecondStageController controller) {
        this.controller = controller;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        login.setDisable(true);
        //initValidator();
    }

    @Override
    public void setApplicationContext(ApplicationContext arg0) throws BeansException {
        this.springContext = arg0;
    }

    @Override
    public void setMessageSource(MessageSource arg0) {
        this.messageSouce = arg0;
    }

    @Override
    public void doClose() {
        controller.closeSecondStage();
    }

    private void clearField() {
        txtUid.clear();
        txtPasswd.clear();
    }


    @FXML
    public void doLogging() {
        try {
            HomeAction action = springContext.getBean(HomeAction.class);
            Account anAccount = service.findByUsernameAndPassword(txtUid.getText(), txtPasswd.getText());
            if (anAccount != null && anAccount.getActive()) {
                action.setLogin(anAccount);
                anAccount.setLastLogin(Timestamp.valueOf(LocalDateTime.now()));
                service.update(anAccount);
                controller.closeSecondStage();
            } else if (anAccount != null && !anAccount.getActive()) {
                clearField();
                this.txtUid.requestFocus();
                balloon.warningAuthetication(lang.getSources(LangProperties.THIS_ACCOUNT_NOT_ACTIVE));
            } else {
                clearField();
                this.txtUid.requestFocus();
                balloon.warningAuthetication(lang.getSources(LangProperties.AUTHENTICATION_FAILED));
            }
        } catch (Exception e) {
            windows.errorLoading(lang.getSources(LangProperties.DATA_AN_ACCOUNT), e);
            e.printStackTrace();
        }
    }

}
