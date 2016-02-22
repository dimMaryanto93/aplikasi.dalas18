package dallastools.actions.income;

import dallastools.controllers.FxInitializable;
import dallastools.controllers.notifications.LangProperties;
import dallastools.controllers.notifications.LangSource;
import dallastools.controllers.notifications.ValidatorMessages;
import dallastools.controllers.stages.SecondStageController;
import dallastools.models.Address;
import dallastools.models.masterdata.Customer;
import dallastools.services.ServiceOfCustomer;
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
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

/**
 * Created by dimmaryanto on 06/11/15.
 */
public class QuickNewCustomerAction implements FxInitializable {
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtContact;
    @FXML
    private TextArea txtStreetAddress;
    @FXML
    private Spinner<Integer> txtRt;
    @FXML
    private Spinner<Integer> txtRw;
    @FXML
    private Button btnSave;

    private ValidationSupport validator;
    private ServiceOfCustomer service;
    private MessageSource messageSource;
    private ApplicationContext springContext;
    private SalesInvoiceDataAction salesInvoice;
    private SalesOrderDataAction salesOrder;
    private ValidatorMessages validatorMessages;
    private LangSource lang;

    private void initValidator() {
        this.validator = new ValidationSupport();
        this.validator.registerValidator(txtName, true, Validator.createEmptyValidator(
                validatorMessages.validatorNotNull(lang.getSources(LangProperties.NAME)), Severity.ERROR));
        this.validator.registerValidator(txtContact, false, Validator.createEmptyValidator(
                validatorMessages.validatorNotNull(lang.getSources(LangProperties.CONTACT_PERSON)), Severity.ERROR));
        this.validator.registerValidator(txtStreetAddress, false, Validator.createEmptyValidator(
                validatorMessages.validatorEmpty(lang.getSources(LangProperties.STREET_ADDRESS)), Severity.WARNING));
        this.validator.invalidProperty().addListener((observable, oldValue, newValue) -> btnSave.setDisable(newValue));
    }

    @FXML
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

        this.txtRt.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0));
        this.txtRt.getEditor().setAlignment(Pos.CENTER_RIGHT);
        this.txtRt.setEditable(true);

        this.txtRw.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0));
        this.txtRw.getEditor().setAlignment(Pos.CENTER_RIGHT);
        this.txtRw.setEditable(true);

    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @FXML
    public void doSave() {
        Customer aCustomer = new Customer();
        aCustomer.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        aCustomer.setCustomerName(txtName.getText());
        aCustomer.setPhone(txtContact.getText());
        Address anAddress = new Address(
                txtStreetAddress.getText(),
                "-",
                0,
                txtRt.getValueFactory().getValue(),
                txtRw.getValueFactory().getValue(),
                "-");
        aCustomer.setAddress(anAddress);
        if (salesInvoice != null) {
            try {
                service.save(aCustomer);
                salesInvoice.loadAllComponentNeeded();
                doClose();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (salesOrder != null) {
            try {
                service.save(aCustomer);
                salesOrder.loadAllComponent();
                doClose();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Autowired
    public void setService(ServiceOfCustomer service) {
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

    public void fromSalesInvoice(SalesInvoiceDataAction sales) {
        initValidator();
        this.salesInvoice = sales;
        this.validator.redecorate();
    }

    public void fromSalesOrder(SalesOrderDataAction order) {
        initValidator();
        this.salesOrder = order;
        this.validator.redecorate();
    }
}
