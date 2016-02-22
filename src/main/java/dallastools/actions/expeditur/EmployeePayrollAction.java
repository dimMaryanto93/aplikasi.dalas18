package dallastools.actions.expeditur;

import dallastools.actions.HomeAction;
import dallastools.controllers.FxInitializable;
import dallastools.controllers.NumberFormatter;
import dallastools.controllers.TableViewColumnAction;
import dallastools.controllers.notifications.DialogBalloon;
import dallastools.controllers.notifications.DialogWindows;
import dallastools.controllers.notifications.LangProperties;
import dallastools.controllers.notifications.LangSource;
import dallastools.models.expenditur.CashRecieptForEmployee;
import dallastools.models.expenditur.PayrollAnEmployee;
import dallastools.models.masterdata.Employee;
import dallastools.services.ServiceOfCashReciept;
import dallastools.services.ServiceOfEmployee;
import dallastools.services.ServiceOfPayrollEmployee;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import java.net.URL;
import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by dimmaryanto on 23/11/15.
 */
public class EmployeePayrollAction implements FxInitializable {
    @FXML
    private Button btnNewCash;
    @FXML
    private TableView<CashRecieptForEmployee> tableViewCashbon;
    @FXML
    private TableColumn<CashRecieptForEmployee, Integer> columnCashId;
    @FXML
    private TableColumn<CashRecieptForEmployee, Date> columnCashDate;
    @FXML
    private TableColumn<CashRecieptForEmployee, Double> columnCashAmount;
    @FXML
    private TableColumn<CashRecieptForEmployee, Double> columnCashPaid;
    @FXML
    private TableColumn columnCashAction;
    @FXML
    private Button btnNew;
    @FXML
    private ListView<Employee> listEmployee;
    @FXML
    private TableView<PayrollAnEmployee> tableView;
    @FXML
    private TableColumn<PayrollAnEmployee, Integer> columnId;
    @FXML
    private TableColumn<PayrollAnEmployee, Date> columnDate;
    @FXML
    private TableColumn<PayrollAnEmployee, Double> columnTotal;
    @FXML
    private TableColumn columnAction;

    private ApplicationContext springContext;
    private MessageSource messageSource;
    private ServiceOfEmployee employeeService;
    private ServiceOfPayrollEmployee payrollService;
    private ServiceOfCashReciept cashRecieptService;
    private DialogWindows windows;
    private DialogBalloon ballon;
    private HomeAction homeAction;
    private TableViewColumnAction actionColumn;
    private LangSource lang;
    private NumberFormatter numberFormatter;

    @Override
    public void doClose() {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.springContext = applicationContext;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnNew.setDisable(true);
        btnNewCash.setDisable(true);

        columnCashId.setCellValueFactory(new PropertyValueFactory<CashRecieptForEmployee, Integer>("id"));
        columnCashId.setCellFactory(param -> new TableCell<CashRecieptForEmployee, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setAlignment(Pos.CENTER);
                if (empty) setText(null);
                else setText(item.toString());
            }
        });

        columnCashDate.setCellValueFactory(new PropertyValueFactory<CashRecieptForEmployee, Date>("date"));
        columnCashDate.setCellFactory(param -> new TableCell<CashRecieptForEmployee, Date>() {
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                setAlignment(Pos.CENTER);
                if (empty) setText(null);
                else setText(item.toString());
            }
        });

        columnCashAmount.setCellValueFactory(new PropertyValueFactory<CashRecieptForEmployee, Double>("amount"));
        columnCashAmount.setCellFactory(param -> new TableCell<CashRecieptForEmployee, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setAlignment(Pos.CENTER_RIGHT);
                if (empty) setText(null);
                else setText(numberFormatter.getCurrency(item));
            }
        });
        columnCashPaid.setCellValueFactory(param -> {
            if (param != null) {
                Double result = param.getValue().getAmount() - param.getValue().getPayment();
                return new SimpleObjectProperty<Double>(result);
            }
            return null;
        });
        columnCashPaid.setCellFactory(param -> new TableCell<CashRecieptForEmployee, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setAlignment(Pos.CENTER_RIGHT);
                if (empty) setText(null);
                else setText(numberFormatter.getCurrency(item));
            }
        });

        columnCashAction.setCellFactory(param -> new TableCell() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                setAlignment(Pos.CENTER);
                if (empty) setGraphic(null);
                else {
                    CashRecieptForEmployee cash = tableViewCashbon.getItems().get(getIndex());
                    setGraphic(actionColumn.getDefautlTableModel());
                    actionColumn.getUpdateLink().setOnAction(event -> {
                        if (cash.getAmount() == cash.getPayment()) {
                            ballon.warningPaidMessage(lang.getSources(LangProperties.DATA_CASH_RECIEPT), cash.getId());
                        } else {
                            EmployeeCashRecieptAction action = springContext.getBean(EmployeeCashRecieptAction.class);
                            homeAction.updateContent();
                            action.paymentMode(cash);
                        }
                    });
                    actionColumn.getDeleteLink().setOnAction(event -> {
                        if (windows.confirmDelete(
                                lang.getSources(LangProperties.DATA_CASH_RECIEPT),
                                cash.getEmployee().getEmployeeName(),
                                lang.getSources(LangProperties.ID),
                                cash.getId()
                        ).get() == ButtonType.OK) {
                            try {
                                cashRecieptService.delete(cash);
                                ballon.sucessedRemoved(lang.getSources(LangProperties.DATA_CASH_RECIEPT), cash.getId());
                                homeAction.showPayrolls();
                            } catch (Exception e) {
                                windows.errorRemoved(lang.getSources(LangProperties.LIST_OF_CASH_RECIEPT_EMPLOYEE), lang.getSources(LangProperties.ID), cash.getId(), e);
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });

        columnId.setCellValueFactory(new PropertyValueFactory<PayrollAnEmployee, Integer>("id"));
        columnId.setCellFactory(param -> new TableCell<PayrollAnEmployee, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setAlignment(Pos.CENTER);
                if (empty) setText(null);
                else setText(item.toString());
            }
        });

        columnDate.setCellValueFactory(new PropertyValueFactory<PayrollAnEmployee, Date>("date"));
        columnDate.setCellFactory(param -> new TableCell<PayrollAnEmployee, Date>() {
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                setAlignment(Pos.CENTER);
                if (empty) setText(null);
                else {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, dd MMM yyyy");
                    String value = formatter.format(item.toLocalDate());
                    setText(value);
                }
            }
        });

        columnTotal.setCellValueFactory(param -> {
            if (param != null) {
                Double amount = param.getValue().getAmount();
                Double bonus = param.getValue().getOtherAmount();
                return new SimpleObjectProperty<Double>(amount + bonus);
            } else return null;
        });
        columnTotal.setCellFactory(param1 -> new TableCell<PayrollAnEmployee, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setAlignment(Pos.CENTER_RIGHT);
                if (empty) setText(null);
                else setText(numberFormatter.getCurrency(item));
            }
        });

        listEmployee.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Employee>() {
            @Override
            public void changed(ObservableValue<? extends Employee> observable, Employee oldValue, Employee newValue) {
                btnNew.setDisable(newValue == null);
                btnNewCash.setDisable(newValue == null);
                if (newValue != null) {
                    loadAllData(newValue);
                } else {
                    tableView.getItems().clear();
                    tableViewCashbon.getItems().clear();
                }
            }
        });
        listEmployee.setCellFactory(param -> new ListCell<Employee>() {

            @Override
            protected void updateItem(Employee item, boolean empty) {
                super.updateItem(item, empty);
                setAlignment(Pos.CENTER_LEFT);
                if (empty) setGraphic(null);
                else {
                    Label employeeName;
                    Label jobId;
                    HBox box = new HBox(5);
                    employeeName = new Label(item.getEmployeeName());
                    jobId = new Label("(" + item.getJobdesc().getId() + ")");
                    box.getChildren().add(jobId);
                    box.getChildren().add(employeeName);
                    setGraphic(box);
                }
            }
        });

        columnAction.setCellFactory(param -> new TableCell() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                setAlignment(Pos.CENTER);
                if (empty) setGraphic(null);
                else {
                    PayrollAnEmployee payroll = tableView.getItems().get(getIndex());
                    setGraphic(actionColumn.getSingleHyperlinkTableModel(lang.getSources(LangProperties.DELETE)));
                    actionColumn.getDeleteLink().setTextFill(Color.RED);
                    actionColumn.getDeleteLink().setGraphic(new FontAwesomeIconView(FontAwesomeIcon.TRASH_ALT));
                    actionColumn.getDeleteLink().setOnAction(event -> {
                        try {
                            if (windows.confirmDelete(
                                    lang.getSources(LangProperties.DATA_PAYROLL_AN_EMPLOYEE),
                                    payroll.getDate(),
                                    lang.getSources(LangProperties.ID),
                                    payroll.getId()).get() == ButtonType.OK){
                                payrollService.delete(payroll);
                                ballon.sucessedRemoved(lang.getSources(LangProperties.DATA_PAYROLL_AN_EMPLOYEE), payroll.getId());
                                loadAllData(listEmployee.getSelectionModel().getSelectedItem());
                            }
                        } catch (Exception e) {
                            windows.errorRemoved(lang.getSources(LangProperties.DATA_PAYROLL_AN_EMPLOYEE),
                                    lang.getSources(LangProperties.ID), payroll.getId(), e);
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
    }

    private Task<Object> getWorker(final Employee employee) {
        return new Task<Object>() {

            private final Integer INDICATOR_SUCCESSED = 500;
            private final Integer INDICATOR_PROGRESSED = 25;
            private Integer workDone;
            private Integer workMax;

            private void setWorkDone(Integer workDone) {
                this.workDone = workDone;
            }

            private void setWorkMax(Integer workMax) {
                this.workMax = workMax;
            }

            private void loadPayroll(Employee anEmployee) throws Exception {
                List<PayrollAnEmployee> list = payrollService.findTransactionByEmployee(anEmployee);
                setWorkMax(list.size());
                for (int i = 0; i < workMax; i++) {
                    setWorkDone(i);
                    updateProgress(workDone, workMax - 1);
                    updateMessage(messageSource.getMessage(lang.getSources(LangProperties.PROGRESS_GETTING_WITH_PARAMS),
                            new Object[]{workDone, workMax}, Locale.getDefault()));
                    PayrollAnEmployee payroll = list.get(workDone);
                    tableView.getItems().add(payroll);
                    Thread.sleep(INDICATOR_PROGRESSED);
                }
                succeeded();
            }

            private void loadCashReciept(Employee anEmployee) throws Exception {
                List<CashRecieptForEmployee> list = cashRecieptService.findCashRecieptByEmployee(anEmployee);
                setWorkMax(list.size());
                for (int i = 0; i < workMax; i++) {
                    setWorkDone(i);
                    updateProgress(workDone, workMax - 1);
                    updateMessage(messageSource.getMessage(lang.getSources(LangProperties.PROGRESS_GETTING_WITH_PARAMS),
                            new Object[]{workDone, workMax}, Locale.getDefault()));
                    CashRecieptForEmployee cash = list.get(workDone);
                    tableViewCashbon.getItems().add(cash);
                    Thread.sleep(INDICATOR_PROGRESSED);
                }
                succeeded();
            }

            @Override
            protected void succeeded() {
                try {
                    setWorkMax(100);
                    for (int i = 0; i < workMax; i++) {
                        setWorkDone(i);
                        updateMessage(messageSource.getMessage(lang.getSources(LangProperties.PROGRESS_FINISHED_WITH_PARAM),
                                new Object[]{i}, Locale.getDefault()));
                        updateProgress(workDone, workMax - 1);
                        Thread.sleep(10);
                    }
                    Thread.sleep(INDICATOR_SUCCESSED);
                    super.succeeded();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            @Override
            protected Object call() throws Exception {
                loadPayroll(employee);
                loadCashReciept(employee);
                return null;
            }
        };
    }

    private void loadAllData(Employee newValue) {
        try {
            tableView.getItems().clear();
            tableViewCashbon.getItems().clear();

            windows.loading(getWorker(newValue), lang.getSources(LangProperties.LIST_PAYROLLS));
        } catch (Exception e) {
            windows.errorLoading(lang.getSources(LangProperties.LIST_PAYROLLS), e);
            e.printStackTrace();
        }
    }


    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @FXML
    public void newPayroll() {
        EmployeePayrollDataAction action = springContext.getBean(EmployeePayrollDataAction.class);
        homeAction.updateContent();
        action.newData(listEmployee.getSelectionModel().getSelectedItem());
    }

    @FXML
    public void newCash() {
        EmployeeCashRecieptAction action = springContext.getBean(EmployeeCashRecieptAction.class);
        homeAction.updateContent();
        action.newData(listEmployee.getSelectionModel().getSelectedItem());
    }

    public void loadData() {
        try {
            listEmployee.getItems().clear();
            List<Employee> list = employeeService.findAll();
            for (Employee anEmployee : list) {
                listEmployee.getItems().add(anEmployee);
            }
        } catch (Exception e) {
            windows.errorLoading(lang.getSources(LangProperties.LIST_OF_EMPLOYEES), e);
            e.printStackTrace();
        }
    }

    @FXML
    public void doClearList() {
        listEmployee.getSelectionModel().clearSelection();
    }

    @Autowired
    public void setEmployeeService(ServiceOfEmployee employeeService) {
        this.employeeService = employeeService;
    }

    @Autowired
    public void setPayrollService(ServiceOfPayrollEmployee payrollService) {
        this.payrollService = payrollService;
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
    public void setHomeAction(HomeAction homeAction) {
        this.homeAction = homeAction;
    }

    @Autowired
    public void setActionColumn(TableViewColumnAction actionColumn) {
        this.actionColumn = actionColumn;
    }

    @Autowired
    public void setCashRecieptService(ServiceOfCashReciept cashRecieptService) {
        this.cashRecieptService = cashRecieptService;
    }


    @Autowired
    public void setNumberFormatter(NumberFormatter numberFormatter) {
        this.numberFormatter = numberFormatter;
    }

    @Autowired
    public void setLang(LangSource lang) {
        this.lang = lang;
    }
}
