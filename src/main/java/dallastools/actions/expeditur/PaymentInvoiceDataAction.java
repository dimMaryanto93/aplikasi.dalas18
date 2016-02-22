package dallastools.actions.expeditur;

import dallastools.actions.HomeAction;
import dallastools.controllers.FxInitializable;
import dallastools.controllers.NumberFormatter;
import dallastools.controllers.dataselections.CategoryPaymentChooser;
import dallastools.controllers.notifications.*;
import dallastools.models.expenditur.PaymentInvoice;
import dallastools.models.masterdata.CategoryOfPayment;
import dallastools.services.ServiceOfPaymentCategory;
import dallastools.services.ServiceOfPaymentInvoice;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
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
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by dimmaryanto on 18/10/15.
 */
public class PaymentInvoiceDataAction implements FxInitializable {

    @FXML
    private DatePicker txtTransDate;
    @FXML
    private ComboBox<String> txtCategory;
    @FXML
    private Spinner<Double> txtAmmount;
    @FXML
    private TextArea txtDescription;
    @FXML
    private Button btnSave;

    private HashMap<String, CategoryOfPayment> categoryMap;
    private CategoryPaymentChooser chooser;
    private ServiceOfPaymentInvoice service;
    private ServiceOfPaymentCategory serviceCategory;
    private DialogBalloon ballon;
    private DialogWindows windows;
    private Boolean isUpdate;
    private PaymentInvoice invoice;
    private ApplicationContext springContext;
    private MessageSource messageSource;
    private ValidationSupport validator;
    private LangSource lang;
    private ValidatorMessages validatorMessages;
    private NumberFormatter numberFormatter;


    @Override
    public void doClose() {
        HomeAction homeAction = springContext.getBean(HomeAction.class);
        homeAction.showPaymentInvoice();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.springContext = applicationContext;
    }

    private void initValidator() {
        this.validator = new ValidationSupport();
        this.validator.registerValidator(txtTransDate,
                (Control date, LocalDate value) -> ValidationResult.fromWarningIf(
                        date, validatorMessages.validatorDateNotEqualsNow(), !value.equals(LocalDate.now())));
        this.validator.registerValidator(txtCategory, true,
                Validator.createEmptyValidator(validatorMessages.validatorNotSelected(lang.getSources(LangProperties.DATA_CATEGORY_OF_PAYMENT)),
                        Severity.ERROR));
        this.validator.registerValidator(txtAmmount.getEditor(), (Control ammount, String value) ->
                ValidationResult.fromErrorIf(ammount, validatorMessages.validatorMin(1), value.equals("0") || value.trim().isEmpty()));
        this.validator.invalidProperty().addListener((observable, oldValue, newValue) -> btnSave.setDisable(newValue));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtTransDate.setValue(LocalDate.now());
        txtAmmount.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, Double.MAX_VALUE, 0, 500));
        txtAmmount.getEditor().setAlignment(Pos.CENTER_RIGHT);


    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @FXML
    private void doSave() {
        if (isUpdate) {
            try {
                this.invoice.setAmmount(txtAmmount.getValueFactory().getValue());
                this.invoice.setTransDate(Date.valueOf(txtTransDate.getValue()));
                this.invoice.setCategory(categoryMap.get(txtCategory.getValue()));
                this.invoice.setDescription(txtDescription.getText());
                service.update(this.invoice);
                ballon.sucessedUpdated(lang.getSources(LangProperties.DATA_AN_OTHER_PAYMENT),
                        lang.getSources(LangProperties.NAME), invoice.getCategory().getPaymentFor());
                doClose();
            } catch (Exception e) {
                windows.errorUpdate(lang.getSources(LangProperties.DATA_AN_OTHER_PAYMENT),
                        lang.getSources(LangProperties.ID), invoice.getId(), e);
                e.printStackTrace();
            }
        } else {
            try {
                this.invoice = new PaymentInvoice(
                        txtAmmount.getValueFactory().getValue(),
                        categoryMap.get(txtCategory.getValue()),
                        txtDescription.getText(),
                        Date.valueOf(txtTransDate.getValue()));
                service.save(invoice);
                ballon.sucessedSave(lang.getSources(LangProperties.DATA_AN_OTHER_PAYMENT));
                clearFields();
                setUpdate(false);
            } catch (Exception e) {
                windows.errorSave(lang.getSources(LangProperties.DATA_AN_OTHER_PAYMENT), e);
                e.printStackTrace();
            }
        }
    }

    @Autowired
    public void setChooser(CategoryPaymentChooser chooser) {
        this.chooser = chooser;
    }

    @Autowired
    public void setCategoryMap(HashMap<String, CategoryOfPayment> categoryMap) {
        this.categoryMap = categoryMap;
    }

    @Autowired
    public void setService(ServiceOfPaymentInvoice service) {
        this.service = service;
    }

    @Autowired
    public void setServiceCategory(ServiceOfPaymentCategory serviceCategory) {
        this.serviceCategory = serviceCategory;
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
    public void setLang(LangSource lang) {
        this.lang = lang;
    }

    @Autowired
    public void setValidatorMessages(ValidatorMessages validatorMessages) {
        this.validatorMessages = validatorMessages;
    }

    @Autowired
    public void setNumberFormatter(NumberFormatter numberFormatter) {
        this.numberFormatter = numberFormatter;
    }

    public void newData() {
        initValidator();
        initComponents();
        clearFields();
        setUpdate(false);
        this.validator.redecorate();
    }

    public void exitsData(PaymentInvoice invoice) {
        initValidator();
        initComponents();
        this.invoice = invoice;
        showToField(invoice);
        setUpdate(true);
        this.validator.redecorate();
    }

    private void clearFields() {
        txtTransDate.setValue(LocalDate.now());
        txtCategory.getSelectionModel().clearSelection();
        txtAmmount.getValueFactory().setValue(0.0);
        txtDescription.clear();
    }

    private void showToField(PaymentInvoice invoice) {
        txtTransDate.setValue(invoice.getTransDate().toLocalDate());
        CategoryOfPayment category = invoice.getCategory();
        if (category != null)
            txtCategory.getSelectionModel().select(chooser.getKey(invoice.getCategory()));
        else txtCategory.getSelectionModel().clearSelection();
        txtAmmount.getValueFactory().setValue(invoice.getAmmount());
        txtDescription.setText(invoice.getDescription());
    }


    private void setUpdate(Boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    private void initComponents() {
        try {
            txtCategory.getItems().clear();
            List<CategoryOfPayment> list = serviceCategory.findAll();
            for (CategoryOfPayment category : list) {
                String key = chooser.getKey(category);
                categoryMap.put(key, category);
                txtCategory.getItems().add(key);
            }
        } catch (Exception e) {
            windows.errorLoading(lang.getSources(LangProperties.LIST_OF_PAYMENT_CATEGORY), e);
            e.printStackTrace();
        }
    }
}
