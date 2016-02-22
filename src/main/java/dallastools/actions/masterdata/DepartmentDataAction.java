package dallastools.actions.masterdata;

import dallastools.actions.HomeAction;
import dallastools.controllers.FxInitializable;
import dallastools.controllers.notifications.*;
import dallastools.controllers.stages.InnerScene;
import dallastools.models.masterdata.Department;
import dallastools.services.ServiceOfDepartment;
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
 * Created by dimmaryanto on 10/5/15.
 */
public class DepartmentDataAction implements FxInitializable {


    @FXML
    private Button btnSave;
    @FXML
    private TextField txtId;
    @FXML
    private TextField txtName;
    @FXML
    private TextArea txtDesc;

    private ServiceOfDepartment service;
    private DialogWindows windows;
    private DialogBalloon ballon;
    private InnerScene innerScene;
    private ValidationSupport validator;
    private ApplicationContext springContext;
    private MessageSource messageSource;
    private ValidatorMessages validatorMessages;
    private Department job;
    private boolean isUpdate;
    private LangSource lang;

    private void initValidator() {
        this.validator = new ValidationSupport();
        this.validator.invalidProperty().addListener((observable, oldValue, newValue) -> btnSave.setDisable(newValue));
        this.validator.registerValidator(txtId, (control, o) -> ValidationResult.fromErrorIf(control,
                validatorMessages.validatorMinMax(lang.getSources(LangProperties.ID), 1, 10)
                , o.toString().length() >= 10 || o.toString().isEmpty()));
        this.validator.registerValidator(txtName, true, Validator.createEmptyValidator(
                validatorMessages.validatorNotNull(lang.getSources(LangProperties.NAME)), Severity.ERROR));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void newData() {
        initValidator();
        setUpdate(false);
        this.job = new Department();
        clearFields();
        this.validator.redecorate();
    }

    public void ExitsData(Department aJob) {
        initValidator();
        setUpdate(true);
        this.job = aJob;
        showField(aJob);
        this.validator.redecorate();
    }

    @FXML
    private void doSave() {
        job.setId(txtId.getText().toUpperCase());
        job.setName(txtName.getText());
        job.setDescription(txtDesc.getText());
        if (isUpdate) {
            try {
                service.update(job);
                ballon.sucessedUpdated(lang.getSources(LangProperties.DATA_A_DEPARTMENT), lang.getSources(LangProperties.ID), job.getId());
                doClose();
            } catch (Exception e) {
                windows.errorUpdate(lang.getSources(LangProperties.DATA_A_DEPARTMENT), lang.getSources(LangProperties.ID), job.getId(), e);
                e.printStackTrace();
            }
        } else {
            try {
                service.save(job);
                ballon.sucessedSave(lang.getSources(LangProperties.DATA_A_DEPARTMENT), job.getName());
                newData();
            } catch (Exception e) {
                windows.errorSave(lang.getSources(LangProperties.DATA_A_DEPARTMENT), job.getName(), e);
                e.printStackTrace();
            }
        }
    }

    @Override
    public void doClose() {
        HomeAction homeAction = springContext.getBean(HomeAction.class);
        homeAction.showJobs();
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
    public void setService(ServiceOfDepartment service) {
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
    public void setInnerScene(InnerScene innerScene) {
        this.innerScene = innerScene;
    }

    @Autowired
    public void setValidatorMessages(ValidatorMessages validatorMessages) {
        this.validatorMessages = validatorMessages;
    }

    private void setUpdate(boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    private void clearFields() {
        txtId.clear();
        txtName.clear();
        txtDesc.clear();
    }

    private void showField(Department job) {
        txtId.setText(job.getId());
        txtName.setText(job.getName());
        txtDesc.setText(job.getDescription());
    }
}
