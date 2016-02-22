package dallastools.actions.masterdata;

import dallastools.actions.HomeAction;
import dallastools.controllers.FxInitializable;
import dallastools.controllers.TableViewColumnAction;
import dallastools.controllers.notifications.DialogBalloon;
import dallastools.controllers.notifications.DialogWindows;
import dallastools.controllers.notifications.LangProperties;
import dallastools.controllers.notifications.LangSource;
import dallastools.models.masterdata.Department;
import dallastools.services.ServiceOfDepartment;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by dimmaryanto on 10/5/15.
 */
public class DepartmentAction implements FxInitializable {

    @FXML
    private TableView<Department> tableView;
    @FXML
    private TableColumn<Department, String> columnId;
    @FXML
    private TableColumn<Department, String> columnName;
    @FXML
    private TableColumn<Department, String> columnDescription;
    @FXML
    private TableColumn columnAction;

    private HomeAction homeAction;
    private DialogWindows windows;
    private DialogBalloon ballon;
    private ServiceOfDepartment service;
    private TableViewColumnAction actionColumn;
    private ApplicationContext springContext;
    private MessageSource messageSource;
    private LangSource lang;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        columnId.setCellValueFactory(new PropertyValueFactory<Department, String>("id"));
        columnName.setCellValueFactory(new PropertyValueFactory<Department, String>("name"));
        columnDescription.setCellValueFactory(new PropertyValueFactory<Department, String>("description"));
        columnAction.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn param) {
                return new TableColumnAction(tableView);
            }
        });
    }

    @FXML
    public void loadData() {
        try {
            tableView.getItems().clear();
            windows.loading(tableView.getItems(), service.findAll(), lang.getSources(LangProperties.LIST_OF_DEPARTMENTS));
            tableView.requestFocus();
        } catch (Exception e) {
            windows.errorLoading(lang.getSources(LangProperties.LIST_OF_DEPARTMENTS), e);
            e.printStackTrace();
        }

    }

    @FXML
    private void newJob() {
        DepartmentDataAction action = springContext.getBean(DepartmentDataAction.class);
        homeAction.updateContent();
        action.newData();
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

    @Autowired
    public void setLang(LangSource lang) {
        this.lang = lang;
    }

    @Autowired
    public void setActionColumn(TableViewColumnAction actionColumn) {
        this.actionColumn = actionColumn;
    }

    @Autowired
    public void setService(ServiceOfDepartment service) {
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

    @FXML
    public void tableViewClearSelection() {
        tableView.getSelectionModel().clearSelection();
    }

    private class TableColumnAction extends TableCell<Department, String> {
        TableView table;

        public TableColumnAction(TableView table) {
            this.table = table;
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                Department job = (Department) table.getItems().get(getIndex());
                setGraphic(actionColumn.getDefautlTableModel());
                actionColumn.getUpdateLink().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        DepartmentDataAction action = springContext.getBean(DepartmentDataAction.class);
                        homeAction.updateContent();
                        action.ExitsData(job);
                    }
                });
                actionColumn.getDeleteLink().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if (windows.confirmDelete(
                                lang.getSources(LangProperties.DATA_A_DEPARTMENT), job.getName(), lang.getSources(LangProperties.ID), job.getId()
                        ).get() == ButtonType.OK) {
                            try {
                                service.delete(job);
                                loadData();
                                ballon.sucessedRemoved(lang.getSources(LangProperties.DATA_A_DEPARTMENT), job.getName());
                            } catch (Exception e) {
                                windows.errorRemoved(lang.getSources(LangProperties.DATA_A_DEPARTMENT), lang.getSources(LangProperties.ID), job.getId(), e);
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }
    }
}
