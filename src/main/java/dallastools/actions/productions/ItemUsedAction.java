package dallastools.actions.productions;

import dallastools.actions.HomeAction;
import dallastools.controllers.FxInitializable;
import dallastools.controllers.TableViewColumnAction;
import dallastools.controllers.notifications.DialogBalloon;
import dallastools.controllers.notifications.DialogWindows;
import dallastools.controllers.notifications.LangProperties;
import dallastools.controllers.notifications.LangSource;
import dallastools.models.masterdata.CategoryOfItem;
import dallastools.models.masterdata.Item;
import dallastools.models.productions.ItemUsed;
import dallastools.services.ServiceOfItemUsed;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by dimmaryanto on 16/11/15.
 */
public class ItemUsedAction implements FxInitializable {

    private final Logger log = LoggerFactory.getLogger(ItemUsedAction.class);
    @FXML
    private TableView<ItemUsed> tableViewTransaction;
    @FXML
    private TableColumn<ItemUsed, Integer> columnId;
    @FXML
    private TableColumn<ItemUsed, String> columnDate;
    @FXML
    private TableColumn columnActions;
    @FXML
    private TableView<Item> tableViewItems;
    @FXML
    private TableColumn<Item, String> columnItemId;
    @FXML
    private TableColumn<Item, String> columnitemCategory;
    @FXML
    private TableColumn<Item, String> columnItemName;
    @FXML
    private TableColumn<Item, Integer> columnItemQty;

    private ApplicationContext springContext;
    private MessageSource messageSource;
    private ServiceOfItemUsed service;
    private DialogWindows windows;
    private HomeAction homeAction;
    private DialogBalloon ballon;
    private TableViewColumnAction actionColumn;
    private LangSource lang;

    @Override
    public void doClose() {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.springContext = applicationContext;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tableViewItems.setSelectionModel(null);
        columnId.setCellFactory(new Callback<TableColumn<ItemUsed, Integer>, TableCell<ItemUsed, Integer>>() {
            @Override
            public TableCell<ItemUsed, Integer> call(TableColumn<ItemUsed, Integer> param) {
                return new TableCell<ItemUsed, Integer>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        setAlignment(Pos.CENTER);
                        if (empty) setText(null);
                        else setText(item.toString());
                    }
                };
            }
        });

        columnItemId.setCellValueFactory(param -> {
            if (param != null)
                return new SimpleStringProperty(param.getValue().getId());
            else return null;
        });
        columnitemCategory.setCellValueFactory(param -> {
            if (param != null) {
                CategoryOfItem category = param.getValue().getCategory();
                if (category != null) {
                    return new SimpleStringProperty(category.getName());
                } else return new SimpleStringProperty();
            } else return null;
        });

        columnItemName.setCellValueFactory(new PropertyValueFactory<Item, String>("name"));

        columnItemQty.setCellValueFactory(new PropertyValueFactory<Item, Integer>("qty"));
        columnItemQty.setCellFactory(param -> new TableCell<Item, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setAlignment(Pos.CENTER);
                if (empty) setText("");
                else setText(item.toString());
            }
        });

        columnId.setCellValueFactory(new PropertyValueFactory<ItemUsed, Integer>("id"));

        columnDate.setCellValueFactory(param -> {
            if (param != null) {
                return new SimpleStringProperty(
                        DateTimeFormatter.ofPattern("dd MMM yyyy").format(
                                param.getValue().getDate().toLocalDate()));
            } else return null;
        });

        columnActions.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn param) {
                return new ColumnActionTransaction(tableViewTransaction.getItems());
            }
        });

    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
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
    public void setService(ServiceOfItemUsed service) {
        this.service = service;
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

    private Task<Object> getWorker() {
        return new Task<Object>() {

            private final Integer INDICATOR_PROGRESSED = 50;

            private Integer workDone;
            private Integer workMax;

            private void setWorkDone(Integer workDone) {
                this.workDone = workDone;
            }

            private void setWorkMax(Integer workMax) {
                this.workMax = workMax;
            }

            private void loadItemsForUsed() throws Exception {
                List<Item> items = service.findItemForUsed();
                setWorkMax(items.size());
                for (int i = 0; i < workMax; i++) {
                    setWorkDone(i);
                    updateMessage(messageSource.getMessage(lang.getSources(LangProperties.PROGRESS_GETTING_WITH_PARAMS), new Object[]{
                            messageSource.getMessage(lang.getSources(LangProperties.LIST_OF_ITEMS), null, Locale.getDefault()),
                            workDone,
                            workMax
                    }, Locale.getDefault()));
                    updateProgress(workDone, workMax - 1);
                    Item anItem = items.get(workDone);
                    tableViewItems.getItems().add(anItem);
                    Thread.sleep(INDICATOR_PROGRESSED);
                }
            }

            private void loadAllTransaction() throws Exception {
                List<ItemUsed> list = service.findAllTransaction();
                setWorkMax(list.size());
                for (int i = 0; i < workMax; i++) {
                    setWorkDone(i);
                    updateMessage(messageSource.getMessage(lang.getSources(LangProperties.PROGRESS_GETTING_WITH_PARAMS), new Object[]{
                            messageSource.getMessage(lang.getSources(LangProperties.LIST_OF_ITEMS_USED), null, Locale.getDefault()),
                            workDone,
                            workMax
                    }, Locale.getDefault()));
                    updateProgress(workDone, workMax - 1);
                    ItemUsed used = list.get(workDone);
                    tableViewTransaction.getItems().add(used);
                    Thread.sleep(INDICATOR_PROGRESSED);
                }
            }

            @Override
            protected void succeeded() {
                try {
                    setWorkMax(100);
                    for (int i = 0; i < workMax; i++) {
                        setWorkDone(i);
                        updateMessage(messageSource.getMessage(lang.getSources(LangProperties.PROGRESS_FINISHED_WITH_PARAM), new Object[]{
                                workDone
                        }, Locale.getDefault()));
                        updateProgress(workDone, workMax - 1);
                        Thread.sleep(10);
                    }
                    updateMessage(messageSource.getMessage(lang.getSources(LangProperties.PROGRESS_FINISHED_WITH_PARAM), new Object[]{workMax}, Locale.getDefault()));
                    Thread.sleep(500);
                    super.succeeded();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected Object call() throws Exception {
                loadItemsForUsed();
                loadAllTransaction();
                succeeded();
                return null;
            }
        };
    }

    public void loadData() {
        try {
            tableViewItems.getItems().clear();
            tableViewTransaction.getItems().clear();
            windows.loading(getWorker(), lang.getSources(LangProperties.LIST_OF_ITEMS_USED));
            tableViewTransaction.requestFocus();
            tableViewTransaction.refresh();
        } catch (Exception e) {
            windows.errorLoading(lang.getSources(LangProperties.LIST_OF_ITEMS_USED), e);
            e.printStackTrace();
        }

    }

    @FXML
    public void newData() {
        ItemUsedDataAction action = springContext.getBean(ItemUsedDataAction.class);
        homeAction.updateContent();
        action.newData(tableViewItems.getItems());
    }

    @FXML
    public void tableViewClearselection() {
        tableViewTransaction.getSelectionModel().clearSelection();
    }

    private class ColumnActionTransaction extends TableCell<ItemUsed, String> {

        private ObservableList<ItemUsed> values;

        public ColumnActionTransaction(ObservableList<ItemUsed> list) {
            this.values = list;
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) setGraphic(null);
            else {
                ItemUsed anItem = values.get(getIndex());
                setGraphic(actionColumn.getDefautlTableModel());
                actionColumn.getUpdateLink().setText(messageSource.getMessage(lang.getSources(LangProperties.VIEW), null, Locale.getDefault()));
                actionColumn.getUpdateLink().setGraphic(new FontAwesomeIconView(FontAwesomeIcon.FOLDER_ALTPEN_ALT));
                actionColumn.getUpdateLink().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        ItemUsedDataAction action = springContext.getBean(ItemUsedDataAction.class);
                        homeAction.updateContent();
                        action.readOnly(anItem);
                    }
                });

                actionColumn.getDeleteLink().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if (windows.confirmDelete(
                                lang.getSources(LangProperties.DATA_ITEM_USED),
                                anItem.getDate(),
                                lang.getSources(LangProperties.ID),
                                anItem.getId()
                        ).get() == ButtonType.OK) {
                            try {
                                service.delete(anItem);
                                service.deleteMaster(anItem);
                                loadData();
                                ballon.sucessedRemoved(lang.getSources(LangProperties.DATA_ITEM_USED));
                            } catch (Exception e) {
                                windows.errorRemoved(lang.getSources(LangProperties.DATA_ITEM_USED), lang.getSources(LangProperties.ID), anItem.getId(), e);
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }
    }
}
