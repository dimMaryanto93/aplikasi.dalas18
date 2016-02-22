package dallastools.actions.masterdata;

import dallastools.actions.HomeAction;
import dallastools.controllers.FxInitializable;
import dallastools.controllers.TableViewColumnAction;
import dallastools.controllers.notifications.DialogBalloon;
import dallastools.controllers.notifications.DialogWindows;
import dallastools.controllers.notifications.LangProperties;
import dallastools.controllers.notifications.LangSource;
import dallastools.models.Address;
import dallastools.models.masterdata.Warehouse;
import dallastools.services.ServiceOfWarehouse;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
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
 * Created by dimmaryanto on 20/10/15.
 */
public class WarehouseAction implements FxInitializable {

    @FXML
    private TextField txtName;
    @FXML
    private TextField txtPhone;
    @FXML
    private TextField txtCity;
    @FXML
    private TextField txtDistrict;
    @FXML
    private TextField txtRT;
    @FXML
    private TextField txtRw;
    @FXML
    private TextField txtPinCode;
    @FXML
    private TextArea txtStreetAddress;
    @FXML
    private TableView<Warehouse> tableView;
    @FXML
    private TableColumn<Warehouse, Integer> columnId;
    @FXML
    private TableColumn<Warehouse, String> columnContact;
    @FXML
    private TableColumn<Warehouse, String> columnName;
    @FXML
    private TableColumn<Warehouse, String> columnAction;

    private TableViewColumnAction actionColumn;
    private ServiceOfWarehouse service;
    private DialogWindows windows;
    private DialogBalloon ballon;
    private HomeAction homeAction;
    private MessageSource messageSource;
    private ApplicationContext springContext;
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
        tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Warehouse>() {
            @Override
            public void changed(ObservableValue<? extends Warehouse> observable, Warehouse oldValue, Warehouse newValue) {
                if (newValue != null) showToFields(newValue);
                else clearFields();
            }
        });
        columnId.setCellValueFactory(new PropertyValueFactory<Warehouse, Integer>("id"));
        columnName.setCellValueFactory(new PropertyValueFactory<Warehouse, String>("name"));
        columnContact.setCellValueFactory(new PropertyValueFactory<Warehouse, String>("phoneNumber"));
        columnAction.setCellFactory(new Callback<TableColumn<Warehouse, String>, TableCell<Warehouse, String>>() {
            @Override
            public TableCell<Warehouse, String> call(TableColumn<Warehouse, String> param) {
                return new TableColumnAction(tableView.getItems());
            }
        });
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @FXML
    public void loadData() {
        try {
            tableView.getItems().clear();
            windows.loading(tableView.getItems(), service.findAll(), lang.getSources(LangProperties.LIST_OF_WAREHOUSES));
        } catch (Exception e) {
            windows.errorLoading(lang.getSources(LangProperties.LIST_OF_WAREHOUSES), e);
            e.printStackTrace();
        }
    }

    @FXML
    public void newWarehouse() {
        WarehouseDataAction action = springContext.getBean(WarehouseDataAction.class);
        homeAction.updateContent();
        action.newData();
    }

    private void clearFields() {
        txtName.clear();
        txtPhone.clear();
        txtCity.clear();
        txtDistrict.clear();
        txtRT.clear();
        txtRw.clear();
        txtPinCode.clear();
        txtStreetAddress.clear();
    }

    private void showToFields(Warehouse aWarehouse) {
        txtName.setText(aWarehouse.getName());
        txtPhone.setText(aWarehouse.getPhoneNumber());
        Address anAddress = aWarehouse.getAddress();
        txtCity.setText(anAddress.getCity());
        txtDistrict.setText(anAddress.getDistrict());
        txtRT.setText(anAddress.getRt().toString());
        txtRw.setText(anAddress.getRw().toString());
        txtPinCode.setText(anAddress.getPinCode().toString());
        txtStreetAddress.setText(anAddress.getStreetAddress());
    }

    @FXML
    public void tableViewClearSelection() {
        tableView.getSelectionModel().clearSelection();
    }

    @Autowired
    public void setActionColumn(TableViewColumnAction actionColumn) {
        this.actionColumn = actionColumn;
    }

    @Autowired
    public void setService(ServiceOfWarehouse service) {
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
    public void setHomeAction(HomeAction homeAction) {
        this.homeAction = homeAction;
    }

    @Autowired
    public void setLang(LangSource lang) {
        this.lang = lang;
    }

    private class TableColumnAction extends TableCell<Warehouse, String> {
        ObservableList<Warehouse> list;

        public TableColumnAction(ObservableList<Warehouse> list) {
            this.list = list;
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) setGraphic(null);
            else {
                Warehouse aWarehouse = list.get(getIndex());
                setGraphic(actionColumn.getDefautlTableModel());
                actionColumn.getDeleteLink().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if (windows.confirmDelete(
                                lang.getSources(LangProperties.DATA_A_WAREHOUSE),
                                aWarehouse.getName(), lang.getSources(LangProperties.ID), aWarehouse.getId()
                        ).get() == ButtonType.OK) {
                            try {
                                service.delete(aWarehouse);
                                loadData();
                                ballon.sucessedRemoved(lang.getSources(LangProperties.DATA_A_WAREHOUSE), aWarehouse.getName());
                            } catch (Exception e) {
                                e.printStackTrace();
                                windows.errorRemoved(lang.getSources(LangProperties.DATA_A_WAREHOUSE), lang.getSources(LangProperties.ID), aWarehouse.getId(), e);
                            }

                        }
                    }
                });
                actionColumn.getUpdateLink().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        WarehouseDataAction action = springContext.getBean(WarehouseDataAction.class);
                        homeAction.updateContent();
                        action.exitsData(aWarehouse);
                    }
                });
            }
        }
    }
}
