package dallastools.actions.reports;

import dallastools.controllers.FxInitializable;
import dallastools.controllers.NumberFormatter;
import dallastools.controllers.PrintController;
import dallastools.controllers.dataselections.EmployeeChooser;
import dallastools.controllers.notifications.*;
import dallastools.models.masterdata.Employee;
import dallastools.models.masterdata.Item;
import dallastools.models.masterdata.Unit;
import dallastools.models.masterdata.Warehouse;
import dallastools.models.other.ItemSum;
import dallastools.services.ServiceOfEmployee;
import dallastools.services.ServiceOfReportProduction;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import net.sf.jasperreports.engine.JRException;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by dimMaryanto on 1/11/2016.
 */
public class ReportProductionOfItemAction implements FxInitializable {
    @FXML
    private DatePicker txtDateBefore;
    @FXML
    private DatePicker txtDateAfter;
    @FXML
    private ComboBox<String> txtEmployee;
    @FXML
    private CheckBox isPrinted;
    @FXML
    private Button btnProcess;
    @FXML
    private TableView<ItemSum> tableView;
    @FXML
    private TableColumn<ItemSum, String> columnWarehouse;
    @FXML
    private TableColumn<ItemSum, String> columnItemName;
    @FXML
    private TableColumn<ItemSum, String> columnUnit;
    @FXML
    private TableColumn<ItemSum, Integer> columnQTY;
    @FXML
    private TextField txtSum;

    private ApplicationContext applicationContext;
    private MessageSource messageSource;
    private ValidationSupport validationSupport;
    private ValidatorMessages validatorMessages;
    private LangSource lang;
    private ServiceOfReportProduction service;
    private ServiceOfEmployee employeeService;
    private EmployeeChooser employeeChooser;
    private HashMap<String, Employee> employeeMap;
    private DialogWindows windows;
    private DialogBalloon balloon;
    private PrintController print;
    private NumberFormatter numberFormatter;

    public void initComponent() {
        try {
            validationSupport = new ValidationSupport();
            validationSupport.registerValidator(txtEmployee,
                    Validator.createEmptyValidator(validatorMessages.validatorEmpty(
                            lang.getSources(LangProperties.SELECT_AN_EMPLOYEE))));
            validationSupport.invalidProperty().addListener((observable, oldValue, newValue) -> btnProcess.setDisable(newValue));
            employeeMap.clear();
            txtEmployee.getItems().clear();
            for (Employee anEmployee : employeeService.findAll()) {
                employeeMap.put(employeeChooser.getKey(anEmployee), anEmployee);
                txtEmployee.getItems().add(employeeChooser.getKey(anEmployee));
            }
            validationSupport.redecorate();
        } catch (Exception e) {
            e.printStackTrace();
            windows.errorLoading(lang.getSources(LangProperties.LIST_OF_EMPLOYEES), e);
        }
    }

    @Override
    public void doClose() {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tableView.setSelectionModel(null);
        btnProcess.setDisable(true);
        LocalDate date = LocalDate.now();
        txtDateBefore.setValue(date.withDayOfMonth(1));
        txtDateAfter.setValue(date.withDayOfMonth(date.lengthOfMonth()));

        txtSum.setText(NumberFormat.getNumberInstance().format(0));
        tableView.getItems().addListener(new ListChangeListener<ItemSum>() {
            @Override
            public void onChanged(Change<? extends ItemSum> c) {
                Integer value = 0;
                for (ItemSum sum : c.getList()) {
                    value += sum.getQty().intValue();
                }
                txtSum.setText(numberFormatter.getNumber(value));
            }
        });
        columnItemName.setCellValueFactory(param -> {
            if (param != null) {
                Item anItem = param.getValue().getItem();
                if (anItem != null) {
                    return new SimpleStringProperty(anItem.getName());
                } else return null;
            } else return null;
        });
        columnQTY.setCellFactory(param -> new TableCell<ItemSum, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                setAlignment(Pos.CENTER);
                super.updateItem(item, empty);
                if (empty) setText(null);
                else setText(numberFormatter.getNumber(item));
            }
        });
        columnQTY.setCellValueFactory(param -> {
            if (param != null) {
                return new SimpleObjectProperty<Integer>(param.getValue().getQty().intValue());
            } else return null;
        });
        columnUnit.setCellFactory(param -> new TableCell<ItemSum, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setAlignment(Pos.CENTER);
                if (empty) setText(null);
                else setText(item);
            }
        });
        columnUnit.setCellValueFactory(param -> {
            if (param != null) {
                Item anItem = param.getValue().getItem();
                if (anItem != null) {
                    Unit anUnit = anItem.getUnit();
                    if (anUnit != null) {
                        return new SimpleObjectProperty<String>(anUnit.getId());
                    } else
                        return null;
                } else return null;
            } else return null;
        });
        columnWarehouse.setCellValueFactory(param -> {
            if (param != null) {
                Item anItem = param.getValue().getItem();
                if (anItem != null) {
                    Warehouse aWarehouse = anItem.getWarehouse();
                    if (aWarehouse != null) {
                        return new SimpleStringProperty(aWarehouse.getName());
                    } else return null;
                } else return null;
            } else return null;
        });
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @FXML
    private void doProcess() {
        tableView.getItems().clear();
        Employee anEmployee = employeeMap.get(txtEmployee.getSelectionModel().getSelectedItem());
        try {
            Task<Object> worker = new Task<Object>() {
                private Integer workDone = 0;
                private Integer workMax = 0;

                public void setWorkDone(Integer workDone) {
                    this.workDone = workDone;
                }

                public void setWorkMax(Integer workMax) {
                    this.workMax = workMax;
                }

                @Override
                protected void succeeded() {
                    try {
                        setWorkMax(100);
                        for (int i = 0; i < workMax; i++) {
                            setWorkDone(i);
                            updateProgress(workDone, workMax - 1);
                            updateMessage(messageSource.getMessage(
                                    lang.getSources(LangProperties.PROGRESS_FINISHED_WITH_PARAM),
                                    new Object[]{workDone}, Locale.getDefault()));
                            Thread.sleep(10);
                        }
                        super.succeeded();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                protected Object call() throws Exception {
                    List<ItemSum> listItems = service.findProductionByEmployee(anEmployee, txtDateBefore.getValue(), txtDateAfter.getValue());
                    setWorkMax(listItems.size());
                    for (int i = 0; i < workMax; i++) {
                        setWorkDone(i);
                        updateProgress(workDone, workMax - 1);
                        updateMessage(messageSource.getMessage(lang.getSources(LangProperties.PROGRESS_GETTING_WITH_PARAMS),
                                new Object[]{workDone, workMax}, Locale.getDefault()));
                        tableView.getItems().add(listItems.get(workDone));
                        Thread.sleep(50);
                    }
                    succeeded();
                    return null;
                }
            };
            worker.setOnSucceeded(event -> {
                try {
                    if (isPrinted.isSelected()) {
                        print.showReportProductionByEmployee("Produksi Es krim berdasarkan karyawan",
                                tableView.getItems(),
                                txtDateBefore.getValue(),
                                txtDateAfter.getValue(),
                                anEmployee);
                    }
                } catch (JRException e) {
                    e.printStackTrace();
                    windows.errorPrint(lang.getSources(LangProperties.LIST_OF_ITEMS), e);
                }
            });
            windows.loading(worker, lang.getSources(LangProperties.LIST_OF_ITEMS));

        } catch (Exception e) {
            e.printStackTrace();
            windows.errorLoading(lang.getSources(LangProperties.LIST_OF_ITEMS), e);
        }
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
    public void setService(ServiceOfReportProduction service) {
        this.service = service;
    }

    @Autowired
    public void setEmployeeService(ServiceOfEmployee employeeService) {
        this.employeeService = employeeService;
    }

    @Autowired
    public void setEmployeeChooser(EmployeeChooser employeeChooser) {
        this.employeeChooser = employeeChooser;
    }

    @Autowired
    public void setEmployeeMap(HashMap<String, Employee> employeeMap) {
        this.employeeMap = employeeMap;
    }

    @Autowired
    public void setWindows(DialogWindows windows) {
        this.windows = windows;
    }

    @Autowired
    public void setBalloon(DialogBalloon balloon) {
        this.balloon = balloon;
    }

    @Autowired
    public void setPrint(PrintController print) {
        this.print = print;
    }

    @Autowired
    public void setNumberFormatter(NumberFormatter numberFormatter) {
        this.numberFormatter = numberFormatter;
    }
}
