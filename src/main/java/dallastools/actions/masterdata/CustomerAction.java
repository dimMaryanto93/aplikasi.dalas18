package dallastools.actions.masterdata;

import dallastools.actions.HomeAction;
import dallastools.controllers.FxInitializable;
import dallastools.controllers.TableViewColumnAction;
import dallastools.controllers.notifications.DialogBalloon;
import dallastools.controllers.notifications.DialogWindows;
import dallastools.controllers.notifications.LangProperties;
import dallastools.controllers.notifications.LangSource;
import dallastools.models.Address;
import dallastools.models.masterdata.Customer;
import dallastools.services.ServiceOfCustomer;
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
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by dimmaryanto on 10/1/15.
 */
public class CustomerAction implements FxInitializable {

    @FXML
    private TextField txtPincode;
    @FXML
    private TableView<Customer> tableView;
    @FXML
    private TableColumn<Customer, Integer> columnId;
    @FXML
    private TableColumn<Customer, String> columnCity;
    @FXML
    private TableColumn<Customer, String> columnPhone;
    @FXML
    private TableColumn<Customer, String> columnName;
    @FXML
    private TableColumn columnAction;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtPhone;
    @FXML
    private TextArea txtAddress;
    @FXML
    private TextField txtRT;
    @FXML
    private TextField txtRW;
    @FXML
    private TextField txtCity;
    @FXML
    private TextField txtDistrict;

    private ServiceOfCustomer service;
    private DialogWindows windows;
    private DialogBalloon ballon;
    private HomeAction homeAction;
    private TableViewColumnAction actionColumn;
    private ApplicationContext springContext;
    private MessageSource messageSource;
    private Customer aCustomer;
    private LangSource lang;

    public void initialize(URL location, ResourceBundle resources) {

        columnId.setCellValueFactory(new PropertyValueFactory<Customer, Integer>("id"));
        columnId.setCellFactory(new Callback<TableColumn<Customer, Integer>, TableCell<Customer, Integer>>() {
            @Override
            public TableCell<Customer, Integer> call(TableColumn<Customer, Integer> param) {
                return new TableCell<Customer, Integer>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(item.toString());
                        }
                        setAlignment(Pos.CENTER);
                    }
                };
            }
        });

        columnCity.setCellValueFactory(param -> {
            if (param != null) {
                Address anAddress = param.getValue().getAddress();
                if (anAddress != null) {
                    return new SimpleStringProperty(anAddress.getCity());
                } else {
                    return new SimpleStringProperty();
                }
            } else return null;
        });
        columnPhone.setCellValueFactory(new PropertyValueFactory<Customer, String>("phone"));
        columnName.setCellValueFactory(new PropertyValueFactory<Customer, String>("customerName"));
        columnAction.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn param) {
                return new ColumnActionCustomer(tableView);
            }
        });

        tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Customer>() {
            @Override
            public void changed(ObservableValue<? extends Customer> observable, Customer oldValue, Customer newValue) {
                if (newValue != null) {
                    aCustomer = newValue;
                    showData(newValue);
                } else {
                    aCustomer = null;
                    clearFields();
                }

            }
        });
    }

    @FXML
    public void loadData() {
        try {
            tableView.getItems().clear();
            List<Customer> list = service.findAll();
            windows.loading(tableView.getItems(), list, lang.getSources(LangProperties.LIST_OF_CUSTOMERS));
            tableView.requestFocus();
        } catch (Exception e) {
            windows.errorLoading(lang.getSources(LangProperties.LIST_OF_CUSTOMERS), e);
            e.printStackTrace();
        }
    }

    @FXML
    private void newCustomer() {
        CustomerDataAction action = springContext.getBean(CustomerDataAction.class);
        homeAction.updateContent();
        action.newData();
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
    public void setBallon(DialogBalloon ballon) {
        this.ballon = ballon;
    }

    @Autowired
    public void setWindows(DialogWindows windows) {
        this.windows = windows;
    }

    @Autowired
    public void setService(ServiceOfCustomer service) {
        this.service = service;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.springContext = applicationContext;
    }

    @Autowired
    public void setLang(LangSource lang) {
        this.lang = lang;
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

    private void showData(Customer aCustomer) {
        Address anAddress = aCustomer.getAddress();
        if (anAddress != null) {
            txtAddress.setText(anAddress.getStreetAddress());
            txtRT.setText(anAddress.getRt().toString());
            txtRW.setText(anAddress.getRw().toString());
            txtCity.setText(anAddress.getCity());
            txtDistrict.setText(anAddress.getDistrict());
            txtPincode.setText(anAddress.getPinCode().toString());
        } else {
            clearFields();
        }
        txtName.setText(aCustomer.getCustomerName());
        txtPhone.setText(aCustomer.getPhone());
    }

    private void clearFields() {
        txtName.clear();
        txtPhone.clear();
        txtAddress.clear();
        txtRT.clear();
        txtRW.clear();
        txtCity.clear();
        txtDistrict.clear();
        txtPincode.clear();
    }

    private class ColumnActionCustomer extends TableCell<Customer, String> {

        TableView table;

        public ColumnActionCustomer(TableView table) {
            this.table = table;
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                Customer aCustomer = (Customer) table.getItems().get(getIndex());
                setGraphic(actionColumn.getDefautlTableModel());
                actionColumn.getUpdateLink().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        CustomerDataAction exitsData = springContext.getBean(CustomerDataAction.class);
                        homeAction.updateContent();
                        exitsData.exitsData(aCustomer);
                    }
                });
                actionColumn.getDeleteLink().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {

                        if (windows.confirmDelete(
                                lang.getSources(LangProperties.DATA_A_CUSTOMER),
                                aCustomer.getCustomerName(), lang.getSources(LangProperties.ID), aCustomer.getId()
                        ).get() == ButtonType.OK) {
                            try {
                                service.delete(aCustomer);
                                loadData();
                                ballon.sucessedRemoved(lang.getSources(LangProperties.DATA_A_CUSTOMER), aCustomer.getCustomerName());
                            } catch (Exception e) {
                                windows.errorRemoved(lang.getSources(LangProperties.DATA_A_CUSTOMER),
                                        lang.getSources(LangProperties.ID), aCustomer.getId(), e);
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }
    }


}
