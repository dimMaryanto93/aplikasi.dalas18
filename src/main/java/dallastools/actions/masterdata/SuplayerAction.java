package dallastools.actions.masterdata;

import dallastools.actions.HomeAction;
import dallastools.controllers.FxInitializable;
import dallastools.controllers.TableViewColumnAction;
import dallastools.controllers.notifications.DialogBalloon;
import dallastools.controllers.notifications.DialogWindows;
import dallastools.controllers.notifications.LangProperties;
import dallastools.controllers.notifications.LangSource;
import dallastools.models.masterdata.Suplayer;
import dallastools.services.ServiceOfSuplayer;
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
 * Created by dimmaryanto on 10/10/15.
 */
public class SuplayerAction implements FxInitializable {

    @FXML
    private TextField txtName;
    @FXML
    private TextField txtPhone;
    @FXML
    private TextArea txtStreetAddress;
    @FXML
    private TextField txtCity;
    @FXML
    private TextField txtDistrict;
    @FXML
    private TextField txtRt;
    @FXML
    private TextField txtRw;
    @FXML
    private TextField txtPos;
    @FXML
    private TableView<Suplayer> tableView;
    @FXML
    private TableColumn<Suplayer, Integer> columnId;
    @FXML
    private TableColumn<Suplayer, String> columnContact;
    @FXML
    private TableColumn<Suplayer, String> columnName;
    @FXML
    private TableColumn columnAction;

    private HomeAction homeAction;
    private DialogBalloon ballon;
    private DialogWindows windows;
    private ServiceOfSuplayer service;
    private TableViewColumnAction actionColumn;
    private ApplicationContext springContext;
    private MessageSource messageSource;
    private LangSource lang;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.springContext = applicationContext;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        columnId.setCellValueFactory(new PropertyValueFactory<Suplayer, Integer>("suplayerId"));
        columnName.setCellValueFactory(new PropertyValueFactory<Suplayer, String>("name"));
        columnContact.setCellValueFactory(new PropertyValueFactory<Suplayer, String>("phone"));
        columnAction.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn param) {
                return new TableColumnAction(tableView.getItems());
            }
        });
        tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Suplayer>() {
            @Override
            public void changed(ObservableValue<? extends Suplayer> observable, Suplayer oldValue, Suplayer newValue) {
                if (newValue != null) {
                    showToFields(newValue);
                } else {
                    clearFields();
                }
            }
        });

    }

    @FXML
    public void newSuplayer() {
        SuplayerDataAction action = springContext.getBean(SuplayerDataAction.class);
        homeAction.updateContent();
        action.newData();
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

    @FXML
    public void loadData() {
        try {
            tableView.getItems().clear();
            windows.loading(tableView.getItems(), service.findAll(), lang.getSources(LangProperties.LIST_OF_SUPLAYERS));
            tableView.requestFocus();
        } catch (Exception e) {
            windows.errorLoading(lang.getSources(LangProperties.LIST_OF_SUPLAYERS), e);
            e.printStackTrace();
        }
    }

    private void clearFields() {
        txtName.clear();
        txtPhone.clear();
        txtStreetAddress.clear();
        txtCity.clear();
        txtDistrict.clear();
        txtRt.clear();
        txtRw.clear();
        txtPos.clear();
    }

    private void showToFields(Suplayer aSuplayer) {
        txtName.setText(aSuplayer.getName());
        txtPhone.setText(aSuplayer.getPhone());
        if (aSuplayer.getAddress() != null) {
            txtStreetAddress.setText(aSuplayer.getAddress().getStreetAddress());
            txtCity.setText(aSuplayer.getAddress().getCity());
            txtDistrict.setText(aSuplayer.getAddress().getDistrict());
            txtRt.setText(aSuplayer.getAddress().getRt().toString());
            txtRw.setText(aSuplayer.getAddress().getRw().toString());
            txtPos.setText(aSuplayer.getAddress().getPinCode().toString());
        } else {
            txtStreetAddress.clear();
            txtCity.clear();
            txtDistrict.clear();
            txtRt.clear();
            txtRw.clear();
            txtPos.clear();
        }
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
    public void setService(ServiceOfSuplayer service) {
        this.service = service;
    }

    @Autowired
    public void setActionColumn(TableViewColumnAction actionColumn) {
        this.actionColumn = actionColumn;
    }

    @Autowired
    public void setLang(LangSource lang) {
        this.lang = lang;
    }

    private class TableColumnAction extends TableCell<Suplayer, String> {
        ObservableList<Suplayer> list;

        public TableColumnAction(ObservableList<Suplayer> list) {
            this.list = list;
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                Suplayer aSuplayer = list.get(getIndex());
                setGraphic(actionColumn.getDefautlTableModel());
                actionColumn.getUpdateLink().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        SuplayerDataAction action = springContext.getBean(SuplayerDataAction.class);
                        homeAction.updateContent();
                        action.exitsData(aSuplayer);
                    }
                });

                actionColumn.getDeleteLink().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if (windows.confirmDelete(
                                lang.getSources(LangProperties.DATA_A_SUPLAYER), aSuplayer.getName(),
                                lang.getSources(LangProperties.ID), aSuplayer.getSuplayerId()
                        ).get() == ButtonType.OK) {
                            try {
                                service.delete(aSuplayer);
                                loadData();
                                ballon.sucessedRemoved(lang.getSources(LangProperties.DATA_A_SUPLAYER), aSuplayer.getName());
                            } catch (Exception e) {
                                windows.errorRemoved(lang.getSources(LangProperties.DATA_A_SUPLAYER), lang.getSources(LangProperties.ID), aSuplayer.getSuplayerId(), e);
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }
    }
}
