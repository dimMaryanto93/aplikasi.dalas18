package dallastools.actions.masterdata;

import dallastools.actions.HomeAction;
import dallastools.controllers.FxInitializable;
import dallastools.controllers.TableViewColumnAction;
import dallastools.controllers.notifications.DialogBalloon;
import dallastools.controllers.notifications.DialogWindows;
import dallastools.controllers.notifications.LangProperties;
import dallastools.controllers.notifications.LangSource;
import dallastools.models.masterdata.CategoryOfItem;
import dallastools.services.ServiceOfItemCategory;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by dimmaryanto on 08/10/15.
 */
public class CategoryOfItemAction implements FxInitializable {

    private Logger log = LoggerFactory.getLogger(CategoryOfItemAction.class);

    @FXML
    private TableView<CategoryOfItem> tableView;
    @FXML
    private TableColumn<CategoryOfItem, String> columnId;
    @FXML
    private TableColumn<CategoryOfItem, String> columnName;
    @FXML
    private TableColumn<CategoryOfItem, String> columnDescription;
    @FXML
    private TableColumn columnAction;

    private TableViewColumnAction actionColumn;
    private ServiceOfItemCategory service;
    private ApplicationContext springContext;
    private MessageSource messageSource;
    private DialogWindows windows;
    private DialogBalloon ballon;
    private HomeAction homeAction;
    private LangSource lang;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log = LoggerFactory.getLogger(CategoryOfItemAction.class);
        columnId.setCellValueFactory(new PropertyValueFactory<CategoryOfItem, String>("id"));
        columnName.setCellValueFactory(new PropertyValueFactory<CategoryOfItem, String>("name"));
        columnDescription.setCellValueFactory(new PropertyValueFactory<CategoryOfItem, String>("description"));
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
            List<CategoryOfItem> list = service.findAll();
            windows.loading(tableView.getItems(), list, new LangSource(LangProperties.LIST_OF_ITEM_CATEGORIES).toString());
            tableView.requestFocus();
        } catch (Exception e) {
            windows.errorLoading(new LangSource(LangProperties.LIST_OF_ITEM_CATEGORIES).toString(), e);
        }
    }

    @FXML
    private void newItem() {
        CategoryOfItemDataAction action = springContext.getBean(CategoryOfItemDataAction.class);
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
    public void setHomeAction(HomeAction homeAction) {
        this.homeAction = homeAction;
    }

    @Autowired
    public void setService(ServiceOfItemCategory service) {
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
    public void setLang(LangSource lang) {
        this.lang = lang;
    }

    private class TableColumnAction extends TableCell<CategoryOfItem, String> {
        private TableView<CategoryOfItem> table;

        public TableColumnAction(TableView<CategoryOfItem> table) {
            this.table = table;
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                CategoryOfItem aCategory = table.getItems().get(getIndex());
                setGraphic(actionColumn.getDefautlTableModel());
                actionColumn.getUpdateLink().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        CategoryOfItemDataAction action = springContext.getBean(CategoryOfItemDataAction.class);
                        homeAction.updateContent();
                        action.existData(aCategory);
                    }
                });
                actionColumn.getDeleteLink().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if (windows.confirmDelete(
                                lang.getSources(LangProperties.DATA_A_CATEGORY_OF_ITEM), aCategory.getName(),
                                lang.getSources(LangProperties.ID), aCategory.getId()).get() == ButtonType.OK) {
                            try {
                                service.delete(aCategory);
                                ballon.sucessedRemoved(lang.getSources(LangProperties.DATA_A_CATEGORY_OF_ITEM), aCategory.getName());
                                loadData();
                            } catch (Exception e) {
                                windows.errorRemoved(
                                        lang.getSources(LangProperties.DATA_A_CATEGORY_OF_ITEM),
                                        lang.getSources(LangProperties.ID),
                                        aCategory.getId(), e);
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }
    }
}
