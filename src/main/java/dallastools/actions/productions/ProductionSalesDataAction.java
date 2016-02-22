package dallastools.actions.productions;

import dallastools.actions.HomeAction;
import dallastools.controllers.FxInitializable;
import dallastools.controllers.dataselections.EmployeeChooser;
import dallastools.controllers.notifications.*;
import dallastools.models.masterdata.*;
import dallastools.models.productions.ProductionOfSales;
import dallastools.models.productions.ProductionOfSalesDetails;
import dallastools.services.ServiceOfEmployee;
import dallastools.services.ServiceOfProduction;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.paint.Color;
import javafx.util.Callback;
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
import java.util.*;

/**
 * Created by dimmaryanto on 15/11/15.
 */
public class ProductionSalesDataAction implements FxInitializable {

    private final Logger log = LoggerFactory.getLogger(ProductionSalesDataAction.class);

    @FXML
    private ComboBox<String> txtEmployee;
    @FXML
    private DatePicker txtDate;
    @FXML
    private TableView<ProductionOfSalesDetails> tableView;
    @FXML
    private TableColumn<ProductionOfSalesDetails, String> columnID;
    @FXML
    private TableColumn<ProductionOfSalesDetails, String> columnName;
    @FXML
    private TableColumn<ProductionOfSalesDetails, String> columnCategory;
    @FXML
    private TableColumn<ProductionOfSalesDetails, String> columnUnit;
    @FXML
    private TableColumn<ProductionOfSalesDetails, String> columnWarehouse;
    @FXML
    private TableColumn<ProductionOfSalesDetails, Integer> columnQty;
    @FXML
    private Button btnAction;

    private ApplicationContext springContext;
    private MessageSource messageSource;
    private DialogWindows windows;
    private DialogBalloon ballon;
    private ServiceOfProduction service;
    private EmployeeChooser chooser;
    private HashMap<String, Employee> employeeHashMap;
    private ServiceOfEmployee serviceOfEmployee;
    private ProductionOfSales production;
    private ValidationSupport validator;
    private ValidatorMessages validatorMessages;
    private LangSource lang;

    private void initValidator() {
        this.validator = new ValidationSupport();
        this.validator.registerValidator(txtEmployee, Validator.createEmptyValidator(
                validatorMessages.validatorNotSelected(lang.getSources(LangProperties.NAME)), Severity.ERROR));
        this.validator.registerValidator(txtDate, (Control c, LocalDate date) -> ValidationResult.fromWarningIf(c,
                validatorMessages.validatorDateNotEqualsNow(), !LocalDate.now().equals(date)));
        this.validator.invalidProperty().addListener((observable, oldValue, newValue) -> btnAction.setDisable(newValue));
    }


    @Override
    public void doClose() {
        HomeAction action = springContext.getBean(HomeAction.class);
        action.showSalesProductions();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.springContext = applicationContext;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.txtDate.setValue(LocalDate.now());
        this.txtDate.setOpacity(0.9);
        txtEmployee.setOpacity(0.9);

        tableView.setEditable(true);
        columnID.setCellValueFactory(param -> {
            if (param != null) {
                Item anItem = param.getValue().getItem();
                if (anItem != null) {
                    return new SimpleStringProperty(anItem.getId());
                } else return new SimpleStringProperty();
            } else return null;
        });

        columnName.setCellValueFactory(param -> {
            if (param != null) {
                Item anItem = param.getValue().getItem();
                if (anItem != null) {
                    return new SimpleStringProperty(anItem.getName());
                } else return new SimpleStringProperty();
            } else return null;
        });

        columnCategory.setCellValueFactory(param -> {
            if (param != null) {
                Item anItem = param.getValue().getItem();
                if (anItem != null) {
                    CategoryOfItem aCategory = anItem.getCategory();
                    if (aCategory != null) {
                        return new SimpleStringProperty(aCategory.getName());
                    } else
                        return new SimpleStringProperty();
                } else return null;
            } else return null;
        });

        columnUnit.setCellValueFactory(param -> {
            if (param != null) {
                Item anItem = param.getValue().getItem();
                if (anItem != null) {
                    Unit anUnit = anItem.getUnit();
                    if (anUnit != null) {
                        return new SimpleStringProperty(anUnit.getId());
                    } else
                        return new SimpleStringProperty();
                } else return null;
            } else return null;
        });

        columnWarehouse.setCellValueFactory(param -> {
            if (param != null) {
                Item anItem = param.getValue().getItem();
                if (anItem != null) {
                    Warehouse war = anItem.getWarehouse();
                    if (war != null) {
                        return new SimpleStringProperty(war.getName());
                    } else
                        return new SimpleStringProperty();
                } else return null;
            } else return null;
        });

        columnQty.setCellValueFactory(param -> {
            return new SimpleObjectProperty<Integer>(param.getValue().getItemUsed());
        });
        columnQty.setCellFactory(new Callback<TableColumn<ProductionOfSalesDetails, Integer>, TableCell<ProductionOfSalesDetails, Integer>>() {
            @Override
            public TableCell<ProductionOfSalesDetails, Integer> call(TableColumn<ProductionOfSalesDetails, Integer> param) {
                return new TableColumnQty(new StringConverter<Integer>() {
                    @Override
                    public String toString(Integer object) {
                        return object.toString();
                    }

                    @Override
                    public Integer fromString(String string) {
                        return Integer.valueOf(string);
                    }
                });
            }
        });

    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }


    @FXML
    public void doAction() {
        try {
            this.production.setDate(Date.valueOf(txtDate.getValue()));
            this.production.setEmployee(employeeHashMap.get(txtEmployee.getValue()));
            List<ProductionOfSalesDetails> list = new ArrayList<>();
            for (ProductionOfSalesDetails details : tableView.getItems()) {
                if (details.getItemUsed() >= 1)
                    list.add(details);
            }
            if (list.size() >= 1) {
                service.save(production, list);
                ballon.sucessedSave(lang.getSources(LangProperties.DATA_SALES_PRODUCTION));
                newData();
            } else {
                ballon.setTitle(messageSource.getMessage(lang.getSources(LangProperties.LIST_OF_ITEMS), null, Locale.getDefault()));
                ballon.setMessage(messageSource.getMessage(lang.getSources(LangProperties.EMPTY_WITH_PARAM), new Object[]{ballon.getTitle()}, Locale.getDefault()));
                ballon.showWarning();
            }
        } catch (Exception e) {
            windows.errorSave(lang.getSources(LangProperties.DATA_SALES_PRODUCTION), production.getId(), e);
            e.printStackTrace();
        }
    }

    @Autowired
    public void setLang(LangSource lang) {
        this.lang = lang;
    }

    @Autowired
    public void setService(ServiceOfProduction service) {
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
    public void setEmployeeHashMap(HashMap<String, Employee> employeeHashMap) {
        this.employeeHashMap = employeeHashMap;
    }

    @Autowired
    public void setChooser(EmployeeChooser chooser) {
        this.chooser = chooser;
    }

    @Autowired
    public void setServiceOfEmployee(ServiceOfEmployee serviceOfEmployee) {
        this.serviceOfEmployee = serviceOfEmployee;
    }

    @Autowired
    public void setValidatorMessages(ValidatorMessages validatorMessages) {
        this.validatorMessages = validatorMessages;
    }

    private void setEnableFields(Boolean active) {
        txtEmployee.setDisable(!active);
        txtDate.setDisable(!active);
    }

    public void readOnly(ProductionOfSales production) {
        try {
            columnQty.setEditable(false);
            btnAction.setVisible(false);
            setEnableFields(false);
            loadComponentNeeded();
            txtEmployee.setValue(chooser.getValue(production.getEmployee()));
            txtDate.setValue(production.getDate().toLocalDate());
            tableView.getItems().clear();
            this.production = production;

            List<ProductionOfSalesDetails> list = service.findTransactionPerProduction(production);
            windows.loading(tableView.getItems(), list, lang.getSources(LangProperties.LIST_OF_ITEMS));
        } catch (Exception e) {
            windows.errorLoading(lang.getSources(LangProperties.LIST_OF_ITEMS), e);
            e.printStackTrace();
        }

    }

    private Task<Object> newDataWorker() {
        return new Task<Object>() {
            private final Integer INDICATOR_PROGRESSED = 50;
            private final Integer INDICATOR_SUCCESSED = 100;

            private Integer workDone;
            private Integer workMax;

            private void setWorkDone(Integer workDone) {
                this.workDone = workDone;
            }

            private void setWorkMax(Integer workMax) {
                this.workMax = workMax;
            }

            @Override
            protected void succeeded() {
                try {
                    setWorkMax(100);
                    for (int i = 0; i < workMax; i++) {
                        setWorkDone(i);
                        updateMessage(messageSource.getMessage(lang.getSources(LangProperties.PROGRESS_FINISHED_WITH_PARAM), new Object[]{workDone}, Locale.getDefault()));
                        updateProgress(workDone, workMax - 1);
                        Thread.sleep(10);
                    }
                    updateMessage(messageSource.getMessage(lang.getSources(LangProperties.PROGRESS_FINISHED_WITH_PARAM), new Object[]{workMax}, Locale.getDefault()));
                    Thread.sleep(INDICATOR_SUCCESSED);
                    super.succeeded();
                } catch (InterruptedException e) {
                    cancel();
                    e.printStackTrace();
                }
            }

            @Override
            protected Object call() throws Exception {
                tableView.getItems().clear();

                List<Item> list = service.findItemForSalesProduction();
                setWorkMax(list.size());

                for (int i = 0; i < workMax; i++) {
                    setWorkDone(i);
                    Item anItem = list.get(workDone);
                    updateProgress(workDone, workMax - 1);
                    updateMessage(messageSource.getMessage(lang.getSources(LangProperties.PROGRESS_GETTING_WITH_PARAMS),
                            new Object[]{workDone, workMax}, Locale.getDefault()));
                    ProductionOfSalesDetails details = new ProductionOfSalesDetails();
                    details.setItem(anItem);
                    details.setItemUsed(0);
                    tableView.getItems().add(details);

                    Thread.sleep(INDICATOR_PROGRESSED);
                }
                succeeded();

                return null;
            }
        };
    }

    private void loadComponentNeeded() {
        try {
            employeeHashMap.clear();
            txtEmployee.getItems().clear();
            List<Employee> employees = serviceOfEmployee.findAll();
            for (Employee anEmployee : employees) {
                String key = chooser.getValue(anEmployee);
                employeeHashMap.put(key, anEmployee);
                txtEmployee.getItems().add(key);
            }
        } catch (Exception e) {
            windows.errorLoading(lang.getSources(LangProperties.LIST_OF_EMPLOYEES), e);
            e.printStackTrace();
        }
    }

    public void newData() {
        try {
            initValidator();
            columnQty.setEditable(true);
            columnQty.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<ProductionOfSalesDetails, Integer>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<ProductionOfSalesDetails, Integer> event) {
                    Integer value = event.getNewValue();
                    if (value > 0) {
                        event.getTableView().getItems().get(event.getTablePosition().getRow()).setItemUsed(value);
                        tableView.refresh();
                    } else {
                        event.getTableView().getItems().get(event.getTablePosition().getRow()).setItemUsed(event.getOldValue());
                        tableView.refresh();
                        ballon.setTitle(messageSource.getMessage(lang.getSources(LangProperties.DATA_AN_ITEM), null, Locale.getDefault()));
                        ballon.setMessage(messageSource.getMessage(lang.getSources(LangProperties.MIN_WITH_PARAM), new Object[]{1}, Locale.getDefault()));
                        ballon.showWarning();
                    }
                }
            });
            btnAction.setVisible(true);
            setEnableFields(true);
            loadComponentNeeded();
            this.production = new ProductionOfSales();
            windows.loading(newDataWorker(), lang.getSources(LangProperties.LIST_OF_SALES_PRODUCTIONS));
            this.validator.redecorate();
        } catch (Exception e) {
            windows.errorLoading(lang.getSources(LangProperties.LIST_OF_SALES_PRODUCTIONS), e);
            e.printStackTrace();
        }
    }

    private class TableColumnQty extends TextFieldTableCell<ProductionOfSalesDetails, Integer> {
        public TableColumnQty(StringConverter<Integer> converter) {
            super(converter);
            setAlignment(Pos.CENTER);
            setTextFill(Color.DARKGREEN);
        }
    }
}
