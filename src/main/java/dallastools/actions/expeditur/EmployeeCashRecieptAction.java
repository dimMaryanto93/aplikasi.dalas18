package dallastools.actions.expeditur;

import dallastools.actions.HomeAction;
import dallastools.controllers.FxInitializable;
import dallastools.controllers.notifications.*;
import dallastools.models.expenditur.CashRecieptForEmployee;
import dallastools.models.masterdata.Employee;
import dallastools.services.ServiceOfCashReciept;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * Created by dimmaryanto on 24/11/15.
 */
public class EmployeeCashRecieptAction implements FxInitializable {
    private final Logger log = LoggerFactory.getLogger(EmployeeCashRecieptAction.class);

    @FXML
    private TextField txtEmployee;
    @FXML
    private DatePicker txtDate;
    @FXML
    private Spinner<Double> txtAmount;
    @FXML
    private Spinner<Double> txtPayment;
    @FXML
    private Button btnSave;

    private ApplicationContext springContext;
    private MessageSource messageSource;
    private Boolean update;
    private SpinnerValueFactory.DoubleSpinnerValueFactory amountValueFactory;
    private SpinnerValueFactory.DoubleSpinnerValueFactory paymentValueFactory;
    private ServiceOfCashReciept service;
    private DialogBalloon ballon;
    private DialogWindows windows;
    private HomeAction homeAction;
    private Employee anEmployee;
    private CashRecieptForEmployee cash;
    private ValidationSupport validator;
    private LangSource lang;
    private ValidatorMessages validatorMessages;

    private void setUpdate(Boolean update) {
        this.update = update;
    }

    private void initValidator() {
        this.validator = new ValidationSupport();
        this.validator.registerValidator(txtDate, (Control c, LocalDate date) ->
                ValidationResult.fromWarningIf(c, validatorMessages.validatorDateNotEqualsNow(), !LocalDate.now().equals(date)));
        this.validator.registerValidator(txtEmployee, false, Validator.createEmptyValidator(
                validatorMessages.validatorNotSelected(lang.getSources(LangProperties.DATA_AN_EMPLOYEE)), Severity.ERROR));
        this.validator.registerValidator(txtAmount.getEditor(), (Control c, String value) ->
                ValidationResult.fromErrorIf(c, validatorMessages.validatorMin(1), Double.valueOf(value) < 1));
        this.validator.invalidProperty().addListener((observable, oldValue, newValue) -> btnSave.setDisable(newValue));
    }

    @FXML
    @Override
    public void doClose() {
        homeAction.showPayrolls();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.springContext = applicationContext;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.amountValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0, Double.MAX_VALUE, 0, 500);
        this.paymentValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 0, 0, 500);

        this.txtDate.setValue(LocalDate.now());
        this.txtDate.getEditor().setAlignment(Pos.CENTER_RIGHT);

        this.txtAmount.setValueFactory(amountValueFactory);
        this.txtAmount.setEditable(true);
        this.txtAmount.getEditor().setAlignment(Pos.CENTER_RIGHT);

        this.txtPayment.setValueFactory(paymentValueFactory);
        this.txtPayment.setEditable(true);
        this.txtPayment.getEditor().setAlignment(Pos.CENTER_RIGHT);


    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @FXML
    public void doSave() {
        if (update) {
            try {
                cash.setPayment(txtPayment.getValueFactory().getValue());
                cash.setPaid((cash.getAmount() - cash.getPayment()) <= 0);
                service.update(cash);
                ballon.sucessedUpdated(lang.getSources(LangProperties.DATA_CASH_RECIEPT),
                        lang.getSources(LangProperties.ID), cash.getId());
                doClose();
            } catch (Exception e) {
                windows.errorUpdate(lang.getSources(LangProperties.DATA_CASH_RECIEPT), lang.getSources(LangProperties.ID), cash.getId(), e);
                e.printStackTrace();
            }
        } else {
            try {
                cash.setEmployee(anEmployee);
                cash.setDate(Date.valueOf(txtDate.getValue()));
                cash.setAmount(txtAmount.getValueFactory().getValue());
                cash.setPayment(txtPayment.getValueFactory().getValue());
                cash.setPaid((cash.getAmount() - cash.getPayment()) <= 0);
                service.save(cash);
                ballon.sucessedSave(lang.getSources(LangProperties.DATA_CASH_RECIEPT),
                        lang.getSources(LangProperties.DATA_AN_EMPLOYEE), cash.getEmployee().getEmployeeName());
                doClose();
            } catch (Exception e) {
                windows.errorSave(lang.getSources(LangProperties.DATA_CASH_RECIEPT), e);
                e.printStackTrace();
            }
        }
    }

    @Autowired
    public void setService(ServiceOfCashReciept service) {
        this.service = service;
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

    public void newData(Employee anEmployee) {
        initValidator();
        this.cash = new CashRecieptForEmployee();
        setUpdate(false);
        this.anEmployee = anEmployee;
        txtEmployee.setText(anEmployee.getEmployeeName());
        this.txtPayment.setDisable(true);
        this.validator.redecorate();
    }

    public void paymentMode(CashRecieptForEmployee cash) {
        initValidator();
        this.cash = cash;
        setUpdate(true);

        paymentValueFactory.setMin(0.0);
        paymentValueFactory.setMax(cash.getAmount());

        this.txtEmployee.setText(cash.getEmployee().getEmployeeName());
        this.txtDate.setValue(cash.getDate().toLocalDate());
        this.txtDate.setDisable(true);

        this.txtAmount.setDisable(true);
        this.txtAmount.getValueFactory().setValue(cash.getAmount());
        this.txtPayment.getValueFactory().setValue(cash.getPayment());
        this.validator.redecorate();
    }
}
