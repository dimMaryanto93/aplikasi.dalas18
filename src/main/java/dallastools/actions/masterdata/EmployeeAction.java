package dallastools.actions.masterdata;

import dallastools.actions.HomeAction;
import dallastools.controllers.FxInitializable;
import dallastools.controllers.TableViewColumnAction;
import dallastools.controllers.notifications.DialogBalloon;
import dallastools.controllers.notifications.DialogWindows;
import dallastools.controllers.notifications.LangProperties;
import dallastools.controllers.notifications.LangSource;
import dallastools.models.masterdata.Department;
import dallastools.models.masterdata.Employee;
import dallastools.services.ServiceOfEmployee;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by dimmaryanto on 10/1/15.
 */
public class EmployeeAction implements FxInitializable {

    @FXML
    public TableColumn<Employee, String> columnJobId;
    @FXML
    private TableView<Employee> tableView;
    @FXML
    private TableColumn<Employee, Integer> columnId;
    @FXML
    private TableColumn<Employee, String> columnName;
    @FXML
    private TableColumn columnAction;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtJob;
    @FXML
    private TextArea txtStreetAddress;
    @FXML
    private TextField txtPinCode;
    @FXML
    private TextField txtRt;
    @FXML
    private TextField txtRw;
    @FXML
    private TextField txtCity;
    @FXML
    private TextField txtDistrict;

    private DialogWindows windows;
    private DialogBalloon ballon;
    private HomeAction homeAction;
    private ServiceOfEmployee service;
    private TableViewColumnAction actionColumn;
    private ApplicationContext springContext;
    private MessageSource messageSource;
    private LangSource lang;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        columnId.setCellValueFactory(new PropertyValueFactory<Employee, Integer>("id"));
        columnName.setCellValueFactory(new PropertyValueFactory<Employee, String>("employeeName"));
        columnJobId.setCellValueFactory(param -> {
            if (param != null) {
                Department aDepartment = param.getValue().getJobdesc();
                if (aDepartment != null) {
                    return new SimpleStringProperty(aDepartment.getId());
                } else return new SimpleStringProperty();
            } else return null;
        });
        columnJobId.setCellFactory(param -> new TableCell<Employee, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setAlignment(Pos.CENTER);
                if (empty) setText(null);
                else setText(item);
            }
        });
        columnAction.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn param) {
                return new TableColumnAction(tableView);
            }
        });

        tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Employee>() {
            @Override
            public void changed(ObservableValue<? extends Employee> observable, Employee oldValue, Employee newValue) {
                if (newValue != null) {
                    showFields(newValue);
                } else clearField();
            }
        });
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.springContext = applicationContext;
    }

    @Override
    public void doClose() {

    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @FXML
    private void newEmployee() {
        EmployeeDataAction action = springContext.getBean(EmployeeDataAction.class);
        homeAction.updateContent();
        action.newData();
    }

    @FXML
    public void loadData() {
        try {
            tableView.getItems().clear();
            windows.loading(tableView.getItems(), service.findAll(), lang.getSources(LangProperties.LIST_OF_EMPLOYEES));
            tableView.requestFocus();
        } catch (Exception e) {
            windows.errorLoading(lang.getSources(LangProperties.LIST_OF_EMPLOYEES), e);
            e.printStackTrace();
        }
    }

    @FXML
    public void tableViewClearSelected() {
        tableView.getSelectionModel().clearSelection();
    }

    @Autowired
    public void setLang(LangSource lang) {
        this.lang = lang;
    }

    @Autowired
    public void setActionColumn(TableViewColumnAction actionColumn) {
        this.actionColumn = actionColumn;
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
    public void setService(ServiceOfEmployee service) {
        this.service = service;
    }

    private void showFields(Employee anEmployee) {
        txtName.setText(anEmployee.getEmployeeName());
        txtJob.setText(anEmployee.getJobdesc().getName());
        txtStreetAddress.setText(anEmployee.getAddress().getStreetAddress());
        txtPinCode.setText(anEmployee.getAddress().getPinCode().toString());
        txtRt.setText(anEmployee.getAddress().getRt().toString());
        txtRw.setText(anEmployee.getAddress().getRw().toString());
        txtCity.setText(anEmployee.getAddress().getCity());
        txtDistrict.setText(anEmployee.getAddress().getDistrict());
    }

    private void clearField() {
        txtName.clear();
        txtJob.clear();
        txtStreetAddress.clear();
        txtPinCode.clear();
        txtRt.clear();
        txtRw.clear();
        txtCity.clear();
        txtDistrict.clear();
    }

    private class TableColumnAction extends TableCell<Employee, String> {

        private TableView<Employee> table;

        public TableColumnAction(TableView tableView) {
            this.table = tableView;
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty)
                setGraphic(null);
            else {
                Employee anEmployee = table.getItems().get(getIndex());
                setGraphic(actionColumn.getDefautlTableModel());
                actionColumn.getUpdateLink().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        EmployeeDataAction action = springContext.getBean(EmployeeDataAction.class);
                        homeAction.updateContent();
                        action.exitsData(anEmployee);
                    }
                });
                actionColumn.getDeleteLink().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {

                        if (windows.confirmDelete(lang.getSources(LangProperties.DATA_AN_EMPLOYEE), anEmployee.getEmployeeName(),
                                lang.getSources(LangProperties.ID), anEmployee.getId())
                                .get() == ButtonType.OK) {
                            try {
                                service.delete(anEmployee);
                                loadData();
                                ballon.sucessedRemoved(lang.getSources(LangProperties.DATA_AN_EMPLOYEE), anEmployee.getEmployeeName());
                            } catch (Exception e) {
                                windows.errorRemoved(lang.getSources(LangProperties.DATA_AN_EMPLOYEE), lang.getSources(LangProperties.ID), anEmployee.getId(), e);
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }

        }
    }
}
