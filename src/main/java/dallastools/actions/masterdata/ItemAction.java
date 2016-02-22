package dallastools.actions.masterdata;

import dallastools.actions.HomeAction;
import dallastools.controllers.FxInitializable;
import dallastools.controllers.NumberFormatter;
import dallastools.controllers.PrintController;
import dallastools.controllers.TableViewColumnAction;
import dallastools.controllers.notifications.DialogBalloon;
import dallastools.controllers.notifications.DialogWindows;
import dallastools.controllers.notifications.LangProperties;
import dallastools.controllers.notifications.LangSource;
import dallastools.models.masterdata.CategoryOfItem;
import dallastools.models.masterdata.Item;
import dallastools.models.masterdata.Unit;
import dallastools.models.masterdata.Warehouse;
import dallastools.services.ServiceOfItem;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Font;
import javafx.util.Callback;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by dimmaryanto on 08/10/15.
 */
public class ItemAction implements FxInitializable {

    @FXML
    private TextField txtId;
    @FXML
    private TextField txtWarehouse;
    @FXML
    private TextField txtQty;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtCategory;
    @FXML
    private TextField txtUnit;
    @FXML
    private TextField txtPriceSell;
    @FXML
    private TextField txtPriceBuy;
    @FXML
    private CheckBox txtCheckSell;
    @FXML
    private TableView<Item> tableView;
    @FXML
    private TableColumn<Item, String> columnWarehouse;
    @FXML
    private TableColumn<Item, String> columnCategory;
    @FXML
    private TableColumn<Item, String> columnName;
    @FXML
    private TableColumn<Item, String> columnQty;
    @FXML
    private TableColumn columnAction;

    private ServiceOfItem service;
    private TableViewColumnAction actionColumn;
    private ApplicationContext springContext;
    private MessageSource messageSource;
    private DialogWindows windows;
    private DialogBalloon ballon;
    private HomeAction homeAction;
    private LangSource lang;
    private NumberFormatter numberFormatter;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtQty.setAlignment(Pos.CENTER);
        txtPriceBuy.setAlignment(Pos.CENTER_RIGHT);
        txtPriceSell.setAlignment(Pos.CENTER_RIGHT);

        columnWarehouse.setCellValueFactory(param -> {
            if (param != null) {
                Warehouse warehouse = param.getValue().getWarehouse();
                if (warehouse != null) {
                    return new SimpleStringProperty(warehouse.getName());
                } else return new SimpleStringProperty();
            } else return null;
        });
        columnCategory.setCellValueFactory(param -> {
            if (param != null) {
                CategoryOfItem catgory = param.getValue().getCategory();
                if (catgory != null) {
                    return new SimpleStringProperty(catgory.getId());
                } else return new SimpleStringProperty();
            } else return null;
        });
        columnName.setCellValueFactory(new PropertyValueFactory<Item, String>("name"));
        columnQty.setCellValueFactory(param -> {
            if (param != null) {
                Unit anUnit = param.getValue().getUnit();
                if (anUnit != null) {
                    return new SimpleStringProperty(numberFormatter.getNumber(param.getValue().getQty()) + "/" + anUnit.getId());
                } else return new SimpleStringProperty();
            } else return null;
        });
        columnQty.setCellFactory(param -> new TableCell<Item, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setAlignment(Pos.CENTER);
                if (empty) setGraphic(null);
                else {
                    Item anItem = tableView.getItems().get(getIndex());
                    if (anItem.getQty() < 10) {
                        FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.EXCLAMATION_TRIANGLE);
                        icon.setFont(new Font("FontAwesome", 18));
                        Label label = new Label(item);
                        label.setGraphicTextGap(10);
                        label.setGraphic(icon);
                        label.setTooltip(new Tooltip(messageSource.getMessage(
                                lang.getSources(LangProperties.QTY_OF_ITEM_LESS_THAN_PARAMS),
                                new Object[]{anItem.getName(), 10}, Locale.getDefault())));
                        setGraphic(label);
                    } else {
                        FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.CHECK);
                        icon.setFont(new Font("FontAwesome", 18));
                        Label label = new Label(item);
                        label.setGraphic(icon);
                        label.setGraphicTextGap(10);
                        setGraphic(label);
                    }
                }

            }
        });
        columnAction.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn param) {
                return new TableColumnAction(tableView.getItems());
            }
        });
        tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Item>() {
            @Override
            public void changed(ObservableValue<? extends Item> observable, Item oldValue, Item newValue) {
                if (newValue != null) {
                    showToFields(newValue);
                } else {
                    clearFields();
                }
            }
        });
    }

    @FXML
    public void loadData() {
        try {
            tableView.getItems().clear();
            windows.loading(tableView.getItems(), service.findAll(), lang.getSources(LangProperties.LIST_OF_ITEMS));
            tableView.requestFocus();
        } catch (Exception e) {
            windows.errorLoading(lang.getSources(LangProperties.LIST_OF_ITEMS), e);
            e.printStackTrace();
        }
    }

    @FXML
    public void newItem() {
        ItemDataAction action = springContext.getBean(ItemDataAction.class);
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

    private void clearFields() {
        txtId.clear();
        txtWarehouse.clear();
        txtQty.clear();
        txtName.clear();
        txtCategory.clear();
        txtUnit.clear();
        txtPriceBuy.clear();
        txtPriceSell.clear();
        txtCheckSell.setSelected(false);
    }

    private void showToFields(Item anItem) {
        CategoryOfItem aCategory = anItem.getCategory();
        Unit anUnit = anItem.getUnit();
        Warehouse aWarehouse = anItem.getWarehouse();

        txtId.setText(anItem.getId());
        txtName.setText(anItem.getName());
        if (aCategory != null) txtCategory.setText(aCategory.getName());
        else txtCategory.clear();
        if (anUnit != null) txtUnit.setText(anUnit.getName());
        else txtUnit.clear();
        if (aWarehouse != null) txtWarehouse.setText(aWarehouse.getName());
        else txtWarehouse.clear();

        txtQty.setText(numberFormatter.getNumber(anItem.getQty()));
        txtPriceBuy.setText(numberFormatter.getCurrency(anItem.getPriceBuy()));
        txtPriceSell.setText(numberFormatter.getCurrency(anItem.getPriceSell()));
        txtCheckSell.setSelected(anItem.getSell());
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @FXML
    public void doPrint() {
        try {
            PrintController print = springContext.getBean(PrintController.class);
            print.showItemsReport(messageSource.getMessage(lang.getSources(LangProperties.LIST_OF_ITEMS), null, Locale.getDefault()), tableView.getItems());
        } catch (JRException e) {
            windows.errorPrint(lang.getSources(LangProperties.LIST_OF_ITEMS), e);
            e.printStackTrace();
        }
    }

    public void tableViewClearSelection() {
        tableView.getSelectionModel().clearSelection();
    }

    @Autowired
    public void setLang(LangSource lang) {
        this.lang = lang;
    }

    @Autowired
    public void setService(ServiceOfItem service) {
        this.service = service;
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
    public void setNumberFormatter(NumberFormatter numberFormatter) {
        this.numberFormatter = numberFormatter;
    }

    private class TableColumnAction extends TableCell<Item, String> {
        List<Item> list;

        public TableColumnAction(List<Item> list) {
            this.list = list;
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                Item anItem = list.get(getIndex());
                setGraphic(actionColumn.getDefautlTableModel());
                actionColumn.getUpdateLink().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        ItemDataAction action = springContext.getBean(ItemDataAction.class);
                        homeAction.updateContent();
                        action.exitsData(anItem);
                    }
                });
                actionColumn.getDeleteLink().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if (windows.confirmDelete(
                                lang.getSources(LangProperties.DATA_AN_ITEM), anItem.getName(),
                                lang.getSources(LangProperties.ID), anItem.getId()).
                                get() == ButtonType.OK) {
                            try {
                                service.delete(anItem);
                                ballon.sucessedRemoved(lang.getSources(LangProperties.DATA_AN_ITEM), anItem.getId());
                                loadData();
                            } catch (Exception e) {
                                windows.errorRemoved(lang.getSources(LangProperties.DATA_AN_ITEM), lang.getSources(LangProperties.ID), anItem.getId(), e);
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }
    }
}
