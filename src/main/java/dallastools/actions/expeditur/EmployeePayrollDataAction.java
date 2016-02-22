package dallastools.actions.expeditur;

import dallastools.actions.HomeAction;
import dallastools.controllers.FxInitializable;
import dallastools.controllers.NumberFormatter;
import dallastools.controllers.notifications.*;
import dallastools.models.expenditur.PayrollAnEmployee;
import dallastools.models.masterdata.CategoryOfItem;
import dallastools.models.masterdata.Employee;
import dallastools.models.masterdata.Item;
import dallastools.models.other.ItemSumForProduction;
import dallastools.models.productions.ProductionOfSales;
import dallastools.services.ServiceOfPayrollEmployee;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;
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
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by dimmaryanto on 23/11/15.
 */
public class EmployeePayrollDataAction implements FxInitializable {

    private final Logger log = LoggerFactory.getLogger(EmployeePayrollDataAction.class);
    @FXML
    private TableColumn<ItemSumForProduction, String> columnItemId;
    @FXML
    private TableColumn<ItemSumForProduction, String> columnItemUnit;
    @FXML
    private TableView<ProductionOfSales> tableViewChoose;
    @FXML
    private TableColumn<ProductionOfSales, Boolean> columnSelected;
    @FXML
    private TableColumn<ProductionOfSales, Integer> columnProductionId;
    @FXML
    private TableColumn<ProductionOfSales, Date> columnProductionDate;
    @FXML
    private TableView<ItemSumForProduction> tableViewItems;
    @FXML
    private TableColumn<ItemSumForProduction, String> columnItemName;
    @FXML
    private TableColumn<ItemSumForProduction, Integer> columnItemQty;
    @FXML
    private TableColumn<ItemSumForProduction, Double> columnItemPrice;
    @FXML
    private TableColumn<ItemSumForProduction, Double> columnItemSubtotal;
    @FXML
    private TextField txtEmployee;
    @FXML
    private DatePicker txtDate;
    @FXML
    private TextField txtTotal;
    @FXML
    private Spinner<Double> txtBonus;
    @FXML
    private TextField txtGrantTotal;
    @FXML
    private Button btnSave;

    private PayrollAnEmployee payroll;
    private Employee employee;
    private MessageSource messageSource;
    private ApplicationContext springContext;
    private ObservableList<ProductionOfSales> listProduction;
    private ServiceOfPayrollEmployee service;
    private DialogWindows windows;
    private DialogBalloon ballon;
    private ValidationSupport validator;
    private SpinnerValueFactory.DoubleSpinnerValueFactory bonusValueFactory;
    private LangSource lang;
    private ValidatorMessages validatorMessages;
    private NumberFormatter numberFormatter;

    private void initValidator() {
        this.validator = new ValidationSupport();
        this.validator.invalidProperty().addListener((observable, oldValue, newValue) -> btnSave.setDisable(newValue));
        this.validator.registerValidator(txtEmployee, Validator.createEmptyValidator(
                validatorMessages.validatorNotSelected(lang.getSources(LangProperties.DATA_AN_EMPLOYEE)), Severity.ERROR));
        this.validator.registerValidator(txtDate, (Control c, LocalDate date) ->
                ValidationResult.fromWarningIf(c, validatorMessages.validatorDateNotEqualsNow(), !LocalDate.now().equals(date)));
        this.validator.registerValidator(txtTotal, (Control c, String value) -> ValidationResult.fromErrorIf(c,
                validatorMessages.validatorMin(1), value.trim().isEmpty() || value.trim().equals(Double.valueOf(0))));

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bonusValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0, Double.MAX_VALUE, 0, 500);
        btnSave.setDisable(true);
        txtBonus.setValueFactory(bonusValueFactory);
        txtBonus.setDisable(true);
        txtBonus.setEditable(true);
        txtBonus.getEditor().setAlignment(Pos.CENTER_RIGHT);

        tableViewChoose.setEditable(true);
        tableViewChoose.setSelectionModel(null);

        tableViewItems.setEditable(true);

        this.listProduction = FXCollections.observableArrayList();
        txtDate.setValue(LocalDate.now());

        columnSelected.setEditable(true);
        columnSelected.setCellFactory(param -> new CheckBoxTableCell() {
            private CheckBox check;

            @Override
            public void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else {
                    ProductionOfSales production = tableViewChoose.getItems().get(getIndex());
                    this.check = new CheckBox();
                    this.check.setText(messageSource.getMessage(lang.getSources(LangProperties.YET_CALCULATED), null, Locale.getDefault()));
                    this.check.setTextFill(Color.RED);
                    this.check.setDisable(false);
                    this.check.selectedProperty().addListener(new ChangeListener<Boolean>() {
                        @Override
                        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                            if (newValue) {
                                listProduction.add(production);
                                check.setTextFill(Color.BLACK);
                                check.setText(messageSource.getMessage(lang.getSources(LangProperties.HAS_BEEN_CALCULATED), null, Locale.getDefault()));
                                //log.info("Kode produksi {} akan diproses!", production.getId());
                            } else {
                                listProduction.remove(production);
                                check.setTextFill(Color.RED);
                                check.setText(messageSource.getMessage(lang.getSources(LangProperties.YET_CALCULATED), null, Locale.getDefault()));
                                //log.info("Kode Produksi {} telah dihapus dari list", production.getId());
                            }
                            //log.info("Jumlah data list yang akan diproduksi ada {}", listProduction.size());
                        }
                    });
                    setGraphic(check);
                }
            }
        });
        columnItemId.setCellValueFactory(param1 -> {
            if (param1 != null) {
                Item anItem = param1.getValue().getItem();
                if (anItem != null) {
                    return new SimpleStringProperty(anItem.getId());
                } else return new SimpleStringProperty();
            } else return null;
        });
        columnItemUnit.setCellValueFactory(param -> {
            if (param != null) {
                Item anItem = param.getValue().getItem();
                if (anItem != null) {
                    CategoryOfItem c = anItem.getCategory();
                    if (c != null) {
                        return new SimpleStringProperty(c.getName());
                    } else
                        return new SimpleStringProperty();
                } else return new SimpleStringProperty();
            } else return null;
        });
        columnProductionId.setCellValueFactory(new PropertyValueFactory<ProductionOfSales, Integer>("id"));
        columnProductionId.setCellFactory(param -> new TableCell<ProductionOfSales, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setAlignment(Pos.CENTER);
                if (empty) setText(null);
                else setText(item.toString());
            }
        });
        columnProductionDate.setCellValueFactory(new PropertyValueFactory<ProductionOfSales, Date>("date"));
        columnProductionDate.setCellFactory(param -> new TableCell<ProductionOfSales, Date>() {
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                setAlignment(Pos.CENTER_LEFT);
                if (empty) setText(null);
                else setText(item.toString());
            }
        });

        columnItemName.setCellValueFactory(param -> {
            if (param != null) {
                Item anItem = param.getValue().getItem();
                if (anItem != null) {
                    return new SimpleStringProperty(anItem.getName());
                } else return new SimpleStringProperty();
            } else return null;
        });

        columnItemQty.setCellValueFactory(new PropertyValueFactory<ItemSumForProduction, Integer>("qty"));
        columnItemQty.setCellFactory(param -> new TableCell<ItemSumForProduction, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setAlignment(Pos.CENTER);
                if (empty) setText(null);
                else {
                    setText(numberFormatter.getNumber(item));
                }
            }
        });

        columnItemPrice.setEditable(true);
        columnItemPrice.setCellValueFactory(new PropertyValueFactory<ItemSumForProduction, Double>("price"));
        columnItemPrice.setCellFactory(param -> new ColumnPriceRender(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                return object.toString();
            }

            @Override
            public Double fromString(String string) {
                return Double.valueOf(string);
            }
        }));
        columnItemPrice.setOnEditCommit(event -> {
            Double value = event.getNewValue();
            ItemSumForProduction item = event.getTableView().getItems().get(event.getTablePosition().getRow());
            item.setPrice(value);
            item.setSubTotal(item.getPrice(), item.getQty());
            tableViewItems.refresh();
            Double total = 0.0;
            for (ItemSumForProduction result : tableViewItems.getItems()) {
                /*log.info("Barang {} harga {} dengan jumlah {} totalnya {}",
                        new Object[]{result.getItem().getName(), result.getPrice(), result.getQty(), result.getSubTotal()});*/
                total += result.getSubTotal();
            }
            payroll.setAmount(total);
            txtTotal.setText(numberFormatter.getCurrency(payroll.getAmount()));
            Double result = payroll.getAmount() + payroll.getOtherAmount();
            txtGrantTotal.setText(numberFormatter.getCurrency(result));
        });

        columnItemSubtotal.setCellValueFactory(new PropertyValueFactory<ItemSumForProduction, Double>("subTotal"));
        columnItemSubtotal.setCellFactory(param -> new TableCell<ItemSumForProduction, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setAlignment(Pos.CENTER_RIGHT);
                if (empty) setText(null);
                else setText(numberFormatter.getCurrency(item));
            }
        });

        bonusValueFactory.valueProperty().addListener((observable, oldValue, newValue) -> {
            Double total = payroll.getAmount();
            Double result = newValue + total;
            txtGrantTotal.setText(numberFormatter.getCurrency(result));
        });

        this.listProduction.addListener(new ListChangeListener<ProductionOfSales>() {
            @Override
            public void onChanged(Change<? extends ProductionOfSales> c) {
                tableViewItems.getItems().clear();
                txtBonus.setDisable(listProduction.size() <= 0);
                if (listProduction.size() >= 1) {
                    try {
                        List<ItemSumForProduction> list = service.findItemGroupByProductions(listProduction);
                        windows.loading(tableViewItems.getItems(), list, lang.getSources(LangProperties.LIST_OF_ITEMS));
                    } catch (Exception e) {
                        windows.errorLoading(lang.getSources(LangProperties.LIST_OF_ITEMS), e);
                        e.printStackTrace();
                    }
                } else {
                    txtBonus.getValueFactory().setValue(0.0);
                    txtGrantTotal.clear();
                    txtTotal.clear();
                }
            }
        });

    }

    @FXML
    @Override
    public void doClose() {
        HomeAction action = springContext.getBean(HomeAction.class);
        action.showPayrolls();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.springContext = applicationContext;
    }


    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void newData(Employee anEmployee) {
        try {
            initValidator();
            this.employee = anEmployee;
            this.payroll = new PayrollAnEmployee();
            payroll.setOtherAmount(0.0);
            payroll.setAmount(0.0);
            txtEmployee.setText(anEmployee.getEmployeeName());
            listProduction.clear();
            tableViewChoose.getItems().clear();
            List<ProductionOfSales> list = service.findProductionByEmployee(anEmployee);
            //log.info("Data Produksi untuk pegawai {} sebanyak {} data", anEmployee.getEmployeeName(), list.size());
            windows.loading(tableViewChoose.getItems(), list, lang.getSources(LangProperties.DATA_SALES_PRODUCTION));
        } catch (Exception e) {
            windows.errorLoading(lang.getSources(LangProperties.DATA_SALES_PRODUCTION), e);
            e.printStackTrace();
        }
    }

    @FXML
    public void doSave() {
        if (listProduction.size() >= 1) {
            try {
                payroll.setEmployee(employee);
                payroll.setOtherAmount(txtBonus.getValueFactory().getValue());
                payroll.setDate(Date.valueOf(txtDate.getValue()));
                payroll.setDetails(listProduction);
                service.save(payroll);
                ballon.sucessedSave(lang.getSources(LangProperties.DATA_PAYROLL_AN_EMPLOYEE));
                doClose();
            } catch (Exception e) {
                windows.errorSave(lang.getSources(LangProperties.DATA_PAYROLL_AN_EMPLOYEE), e);
                e.printStackTrace();
            }
        } else {
            ballon.warningEmptyMessage(lang.getSources(LangProperties.DATA_PAYROLL_AN_EMPLOYEE), lang.getSources(LangProperties.LIST_OF_SALES_PRODUCTIONS));
            tableViewChoose.requestFocus();
        }
    }

    @Autowired
    public void setService(ServiceOfPayrollEmployee service) {
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

    private class ColumnPriceRender extends TextFieldTableCell<ItemSumForProduction, Double> {
        public ColumnPriceRender(StringConverter<Double> converter) {
            super(converter);
            setAlignment(Pos.CENTER_RIGHT);
        }

        @Override
        public void updateItem(Double item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) setText(null);
            else setText(numberFormatter.getCurrency(item));
        }
    }

}
