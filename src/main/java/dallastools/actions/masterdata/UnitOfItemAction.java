package dallastools.actions.masterdata;

import dallastools.actions.HomeAction;
import dallastools.controllers.FxInitializable;
import dallastools.controllers.TableViewColumnAction;
import dallastools.controllers.notifications.DialogBalloon;
import dallastools.controllers.notifications.DialogWindows;
import dallastools.controllers.notifications.LangProperties;
import dallastools.controllers.notifications.LangSource;
import dallastools.models.masterdata.Unit;
import dallastools.services.ServiceOfUnit;
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
 * Created by dimmaryanto on 08/10/15.
 */
public class UnitOfItemAction implements FxInitializable {

    @FXML
    private TableView<Unit> tableView;
    @FXML
    private TableColumn<Unit, String> columnId;
    @FXML
    private TableColumn<Unit, String> columnName;
    @FXML
    private TableColumn<Unit, String> columnDescription;
    @FXML
    private TableColumn columnAction;

    private ServiceOfUnit service;
    private DialogWindows windows;
    private DialogBalloon ballon;
    private TableViewColumnAction actionColumn;
    private MessageSource messageSource;
    private ApplicationContext springContext;
    private HomeAction homeAction;
    private LangSource lang;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        columnId.setCellValueFactory(new PropertyValueFactory<Unit, String>("id"));
        columnName.setCellValueFactory(new PropertyValueFactory<Unit, String>("name"));
        columnDescription.setCellValueFactory(new PropertyValueFactory<Unit, String>("description"));
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
            windows.loading(tableView.getItems(), service.findAll(), lang.getSources(LangProperties.LIST_OF_UNITS));
            tableView.requestFocus();
        } catch (Exception e) {
            windows.errorLoading(lang.getSources(LangProperties.LIST_OF_UNITS), e);
            e.printStackTrace();
        }
    }

    @FXML
    public void newItem() {
        UnitOfItemDataAction action = springContext.getBean(UnitOfItemDataAction.class);
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

    @FXML
    public void tableViewClearSelection() {
        tableView.getSelectionModel().clearSelection();
    }

    @Autowired
    public void setService(ServiceOfUnit service) {
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
    public void setActionColumn(TableViewColumnAction actionColumn) {
        this.actionColumn = actionColumn;
    }

    @Autowired
    public void setHomeAction(HomeAction homeAction) {
        this.homeAction = homeAction;
    }

    @Autowired
    public void setLang(LangSource lang) {
        this.lang = lang;
    }

    private class TableColumnAction extends TableCell<Unit, String> {
        private TableView<Unit> table;

        public TableColumnAction(TableView table) {
            this.table = table;
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                Unit anUnit = table.getItems().get(getIndex());
                setGraphic(actionColumn.getDefautlTableModel());
                actionColumn.getUpdateLink().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        UnitOfItemDataAction action = springContext.getBean(UnitOfItemDataAction.class);
                        homeAction.updateContent();
                        action.exitsData(anUnit);
                    }
                });
                actionColumn.getDeleteLink().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if (windows.confirmDelete(
                                lang.getSources(LangProperties.DATA_AN_UNIT_OF_ITEM), anUnit.getName(), lang.getSources(LangProperties.ID), anUnit.getId()
                        ).get() == ButtonType.OK) {
                            try {
                                service.delete(anUnit);
                                loadData();
                                ballon.sucessedRemoved(lang.getSources(LangProperties.DATA_AN_UNIT_OF_ITEM), anUnit.getName());
                            } catch (Exception e) {
                                e.printStackTrace();
                                windows.errorRemoved(lang.getSources(LangProperties.DATA_AN_UNIT_OF_ITEM), lang.getSources(LangProperties.ID), anUnit.getId(), e);
                            }
                        }
                    }
                });
            }
        }
    }
}
