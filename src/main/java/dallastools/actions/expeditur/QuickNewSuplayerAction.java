package dallastools.actions.expeditur;

import dallastools.controllers.FxInitializable;
import dallastools.controllers.notifications.LangProperties;
import dallastools.controllers.notifications.LangSource;
import dallastools.controllers.notifications.ValidatorMessages;
import dallastools.controllers.stages.SecondStageController;
import dallastools.models.Address;
import dallastools.models.masterdata.Suplayer;
import dallastools.services.ServiceOfSuplayer;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by dimmaryanto on 06/11/15.
 */
public class QuickNewSuplayerAction implements FxInitializable {
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

    private ServiceOfSuplayer service;
    private ApplicationContext springContainer;
    private MessageSource messageSource;
    private PurchaseInvoiceDataAction invoice;
    private ValidationSupport validationSupport;
    private ValidatorMessages validatorMessages;
    private LangSource lang;

    private void initValidator() {
        this.validationSupport = new ValidationSupport();
        this.validationSupport.registerValidator(txtName, true,
                Validator.createEmptyValidator(
                        validatorMessages.validatorNotNull(lang.getSources(LangProperties.DATA_A_SUPLAYER))));
        this.validationSupport.registerValidator(txtContact, false, Validator.createEmptyValidator(
                validatorMessages.validatorNotNull(lang.getSources(LangProperties.CONTACT_PERSON))
        ));
        this.validationSupport.invalidProperty().addListener((observable, oldValue, newValue) -> btnSave.setDisable(newValue));
    }

    @FXML
    @Override
    public void doClose() {
        SecondStageController controller = springContainer.getBean(SecondStageController.class);
        controller.closeSecondStage();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.springContainer = applicationContext;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtRt.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0));
        txtRt.getEditor().setAlignment(Pos.CENTER_RIGHT);
        txtRt.setEditable(true);

        txtRw.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0));
        txtRw.getEditor().setAlignment(Pos.CENTER_RIGHT);
        txtRw.setEditable(true);
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @FXML
    private void doSave() {
        Suplayer aSuplayer = new Suplayer();
        aSuplayer.setName(txtName.getText());
        aSuplayer.setPhone(txtContact.getText());
        Address anAddress = new Address(
                txtStreetAddress.getText(),
                "-",
                0,
                txtRt.getValueFactory().getValue(),
                txtRw.getValueFactory().getValue(),
                "-");
        aSuplayer.setAddress(anAddress);
        if (invoice != null) {
            try {
                service.save(aSuplayer);
                invoice.initComponents();
                doClose();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Autowired
    public void setService(ServiceOfSuplayer service) {
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

    public void fromPurchaseInvoice(PurchaseInvoiceDataAction invoice) {
        initValidator();
        this.invoice = invoice;
        this.validationSupport.redecorate();

    }
}
