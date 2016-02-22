package dallastools.actions.masterdata;

import dallastools.actions.HomeAction;
import dallastools.controllers.FxInitializable;
import dallastools.controllers.dataselections.DepartmentChooser;
import dallastools.controllers.notifications.*;
import dallastools.controllers.stages.InnerScene;
import dallastools.models.Address;
import dallastools.models.masterdata.Department;
import dallastools.models.masterdata.Employee;
import dallastools.services.ServiceOfDepartment;
import dallastools.services.ServiceOfEmployee;
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
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by dimmaryanto on 07/10/15.
 */
public class EmployeeDataAction implements FxInitializable {

    @FXML
    private Button btnSave;
    @FXML
    private TextField txtName;
    @FXML
    private ChoiceBox<String> txtJob;
    @FXML
    private TextField txtCity;
    @FXML
    private TextField txtDistrict;
    @FXML
    private TextArea txtStreetAddress;
    @FXML
    private Spinner<Integer> txtRt;
    @FXML
    private Spinner<Integer> txtRw;
    @FXML
    private Spinner<Integer> txtPinCode;

    private DepartmentChooser chooser;
    private InnerScene innerScene;
    private ServiceOfEmployee employeeService;
    private ServiceOfDepartment departmentService;
    private DialogWindows windows;
    private DialogBalloon ballon;
    private HashMap<String, Department> jobClasses;
    private ValidationSupport validationSupport;
    private Boolean isUpdate;
    private Employee anEmployee;
    private ApplicationContext springContext;
    private MessageSource messageSource;
    private ValidatorMessages validatorMessages;
    private LangSource lang;

    @Autowired
    public void setLang(LangSource lang) {
        this.lang = lang;
    }

    @Autowired
    public void setValidatorMessages(ValidatorMessages validatorMessages) {
        this.validatorMessages = validatorMessages;
    }

    @Autowired
    public void setInnerScene(InnerScene innerScene) {
        this.innerScene = innerScene;
    }

    @Autowired
    public void setEmployeeService(ServiceOfEmployee employeeService) {
        this.employeeService = employeeService;
    }

    @Autowired
    public void setDepartmentService(ServiceOfDepartment departmentService) {
        this.departmentService = departmentService;
    }

    @Autowired
    public void setChooser(DepartmentChooser chooser) {
        this.chooser = chooser;
    }

    @Autowired
    public void setWindows(DialogWindows windows) {
        this.windows = windows;
    }

    @Autowired
    public void setJobClasses(HashMap<String, Department> jobClasses) {
        this.jobClasses = jobClasses;
    }

    @Autowired
    public void setBallon(DialogBalloon ballon) {
        this.ballon = ballon;
    }

    private void initValidator() {
        this.validationSupport = new ValidationSupport();
        validationSupport.invalidProperty().addListener((observable, oldValue, newValue) -> btnSave.setDisable(newValue));
        validationSupport.registerValidator(txtName, true, Validator.createEmptyValidator(
                validatorMessages.validatorNotNull(lang.getSources(LangProperties.NAME)), Severity.ERROR));
        validationSupport.registerValidator(txtJob, true, Validator.createEmptyValidator(
                validatorMessages.validatorNotSelected(lang.getSources(LangProperties.JOB_NAME)), Severity.ERROR));

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

    private Address createAddress() {
        return new Address(
                txtStreetAddress.getText(),
                txtCity.getText(),
                txtPinCode.getValueFactory().getValue(),
                txtRt.getValueFactory().getValue(),
                txtRw.getValueFactory().getValue(),
                txtDistrict.getText());
    }

    public void exitsData(Employee anEmployee) {
        initValidator();
        this.loadDataJob();
        this.anEmployee = anEmployee;
        setUpdate(true);
        showToFields(anEmployee);
        this.validationSupport.redecorate();
    }

    public void newData() {
        initValidator();
        loadDataJob();
        this.anEmployee = new Employee();
        setUpdate(false);
        clearFields();
        this.validationSupport.redecorate();
    }

    @FXML
    private void doSave() {
        anEmployee.setAddress(createAddress());
        anEmployee.setEmployeeName(txtName.getText());
        anEmployee.setJobdesc(jobClasses.get(txtJob.getValue()));
        if (isUpdate) {
            try {
                employeeService.update(anEmployee);
                ballon.sucessedUpdated(lang.getSources(LangProperties.DATA_AN_EMPLOYEE), lang.getSources(LangProperties.ID), anEmployee.getId());
                doClose();
            } catch (Exception e) {
                e.printStackTrace();
                windows.errorUpdate(lang.getSources(LangProperties.DATA_AN_EMPLOYEE), lang.getSources(LangProperties.ID), anEmployee.getId(), e);
            }
        } else {
            try {
                employeeService.save(anEmployee);
                ballon.sucessedSave(lang.getSources(LangProperties.DATA_AN_EMPLOYEE), anEmployee.getEmployeeName());
                newData();
            } catch (Exception e) {
                e.printStackTrace();
                windows.errorSave(lang.getSources(LangProperties.DATA_AN_EMPLOYEE), anEmployee.getEmployeeName(), e);
            }
        }
    }

    @Override
    public void doClose() {
        HomeAction homeAction = springContext.getBean(HomeAction.class);
        homeAction.showEmployees();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.springContext = applicationContext;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private void setUpdate(Boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    private void showToFields(Employee anEmployee) {
        Department job = anEmployee.getJobdesc();
        txtName.setText(anEmployee.getEmployeeName());
        txtJob.setValue(chooser.getKey(job));
        txtCity.setText(anEmployee.getAddress().getCity());
        txtDistrict.setText(anEmployee.getAddress().getDistrict());
        txtStreetAddress.setText(anEmployee.getAddress().getStreetAddress());
        txtRt.getValueFactory().setValue(anEmployee.getAddress().getRt());
        txtRw.getValueFactory().setValue(anEmployee.getAddress().getRw());
        txtPinCode.getValueFactory().setValue(anEmployee.getAddress().getPinCode());
    }

    private void clearFields() {
        txtName.clear();
        txtJob.getSelectionModel().clearSelection();
        txtCity.clear();
        txtDistrict.clear();
        txtStreetAddress.clear();
        txtRt.getValueFactory().setValue(0);
        txtRw.getValueFactory().setValue(0);
        txtPinCode.getValueFactory().setValue(0);
    }

    private void loadDataJob() {
        try {
            txtJob.getItems().clear();
            jobClasses.clear();
            List<Department> list = departmentService.findAll();
            for (Department job : list) {
                jobClasses.put(chooser.getKey(job), job);
                txtJob.getItems().add(chooser.getKey(job));
            }
        } catch (Exception e) {
            windows.errorLoading(lang.getSources(LangProperties.LIST_OF_DEPARTMENTS), e);
            e.printStackTrace();
        }
    }
}
