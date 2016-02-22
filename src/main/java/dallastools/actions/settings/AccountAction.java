package dallastools.actions.settings;

import dallastools.actions.HomeAction;
import dallastools.controllers.AutheticationLevel;
import dallastools.controllers.FxInitializable;
import dallastools.controllers.TableViewColumnAction;
import dallastools.controllers.notifications.DialogWindows;
import dallastools.controllers.notifications.LangProperties;
import dallastools.controllers.notifications.LangSource;
import dallastools.models.masterdata.Account;
import dallastools.models.other.Level;
import dallastools.services.ServiceOfAccount;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Callback;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by dimmaryanto on 11/10/15.
 */
public class AccountAction implements FxInitializable {

    @FXML
    private CheckBox txtIsActive;
    @FXML
    private TextField txtUserId;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private TextField txtLevel;
    @FXML
    private TextField txtCreatedBy;
    @FXML
    private TextField txtCreatedDate;
    @FXML
    private TextField txtLastLogin;
    @FXML
    private TextField txtName;
    @FXML
    private TableView<Account> tableView;
    @FXML
    private TableColumn<Account, String> columnFullname;
    @FXML
    private TableColumn<Account, String> columnLevel;
    @FXML
    private TableColumn<Account, String> columnId;
    @FXML
    private TableColumn<Account, String> columnLockUnlock;
    @FXML
    private TableColumn<Account, String> columnDo;

    private ApplicationContext springContext;
    private MessageSource messageSource;

    private TableViewColumnAction actionColumn;
    private DialogWindows windows;
    private ServiceOfAccount service;
    private HomeAction homeAction;
    private LangSource lang;

    private void clearFields() {
        txtUserId.clear();
        txtPassword.clear();
        txtLevel.clear();
        txtCreatedBy.clear();
        txtCreatedDate.clear();
        txtLastLogin.clear();
        txtName.clear();
        txtIsActive.setSelected(false);
    }

    private void showToFields(Account anAccount) {
        txtUserId.setText(anAccount.getUsername());
        txtPassword.setText(anAccount.getPasswd());
        txtLevel.setText(anAccount.getLevel());
        txtCreatedBy.setText(anAccount.getCreatedBy());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy '/' HH:mm a");
        txtCreatedDate.setText(formatter.format(anAccount.getCreatedDate().toLocalDateTime()));
        txtLastLogin.setText(formatter.format(anAccount.getLastLogin().toLocalDateTime()));

        txtName.setText(anAccount.getFullname());
        txtIsActive.setSelected(anAccount.getActive());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.springContext = applicationContext;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtIsActive.setDisable(true);
        tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Account>() {
            @Override
            public void changed(ObservableValue<? extends Account> observable, Account oldValue, Account newValue) {
                if (newValue != null)
                    showToFields(newValue);
                else clearFields();
            }
        });

        columnId.setCellValueFactory(new PropertyValueFactory<Account, String>("username"));
        columnFullname.setCellValueFactory(new PropertyValueFactory<Account, String>("fullname"));
        columnLevel.setCellValueFactory(new PropertyValueFactory<Account, String>("level"));

        columnLockUnlock.setCellFactory(new Callback<TableColumn<Account, String>, TableCell<Account, String>>() {
            @Override
            public TableCell<Account, String> call(TableColumn<Account, String> param) {
                return new TableColumnAction(tableView.getItems());
            }
        });
        columnDo.setCellFactory(new Callback<TableColumn<Account, String>, TableCell<Account, String>>() {
            @Override
            public TableCell<Account, String> call(TableColumn<Account, String> param) {
                return new TableColumnDo(tableView.getItems());
            }
        });

    }

    public void loadData() {
        try {
            tableView.getItems().clear();
            windows.loading(tableView.getItems(), service.findAll(), lang.getSources(LangProperties.LIST_ACCOUNTS));
            tableView.requestFocus();
        } catch (Exception e) {
            windows.errorLoading(lang.getSources(LangProperties.LIST_ACCOUNTS), e);
            e.printStackTrace();
        }
    }


    @FXML
    public void newAccount() {
        AccountDataAction action = springContext.getBean(AccountDataAction.class);
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

    @Autowired
    public void setService(ServiceOfAccount service) {
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
    public void setHomeAction(HomeAction homeAction) {
        this.homeAction = homeAction;
    }

    @Autowired
    public void setLang(LangSource lang) {
        this.lang = lang;
    }

    @FXML
    public void clearSelection() {
        tableView.getSelectionModel().clearSelection();
    }

    private class TableColumnDo extends TableCell<Account, String> {
        private ObservableList<Account> list;

        public TableColumnDo(ObservableList<Account> list) {
            this.list = list;
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            setAlignment(Pos.CENTER);
            super.updateItem(item, empty);
            if (empty) setGraphic(null);
            else {
                Account anAccount = list.get(getIndex());
                FontAwesomeIconView icon = new FontAwesomeIconView();
                icon.setFont(new Font("FontAwesome", 18));
                if (anAccount.getLevel().equals(AutheticationLevel.getValue(Level.ADMIM))) {
                    icon.setIcon(FontAwesomeIcon.BAN);
                    Label text = new Label(messageSource.getMessage(lang.getSources(LangProperties.FORBIDDEN), null, Locale.getDefault()));
                    text.setGraphic(icon);
                    setGraphic(text);
                } else if (anAccount.getActive()) {
                    icon.setIcon(FontAwesomeIcon.EXCLAMATION_TRIANGLE);
                    Label text = new Label(messageSource.getMessage(lang.getSources(LangProperties.PROTECTED), null, Locale.getDefault()));
                    text.setGraphic(icon);
                    setGraphic(text);
                } else if (!anAccount.getActive()) {
                    setGraphic(actionColumn.getSingleHyperlinkTableModel(lang.getSources(LangProperties.DELETE)));
                    FontAwesomeIconView iconDelete = new FontAwesomeIconView(FontAwesomeIcon.TRASH_ALT);
                    iconDelete.setFont(new Font("FontAwesome", 18));
                    actionColumn.getDeleteLink().setGraphic(iconDelete);
                    actionColumn.getDeleteLink().setTextFill(Color.RED);
                    actionColumn.getDeleteLink().setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            ButtonType type = windows.confirmDelete(
                                    lang.getSources(LangProperties.DATA_AN_ACCOUNT),
                                    anAccount.getFullname(),
                                    lang.getSources(LangProperties.USERNAME),
                                    anAccount.getUsername()
                            ).get();
                            if (type == ButtonType.OK) {
                                try {
                                    service.delete(anAccount);
                                    loadData();
                                } catch (Exception e) {
                                    windows.errorRemoved(
                                            lang.getSources(LangProperties.LIST_ACCOUNTS),
                                            lang.getSources(LangProperties.USERNAME),
                                            anAccount.getUsername(),
                                            e);
                                    e.printStackTrace();
                                }
                            }

                        }
                    });
                }
            }
        }
    }

    private class TableColumnAction extends TableCell<Account, String> {
        private ObservableList<Account> list;

        public TableColumnAction(ObservableList<Account> list) {
            this.list = list;
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            setAlignment(Pos.CENTER_LEFT);
            if (empty)
                setGraphic(null);
            else {
                FontAwesomeIconView icon = new FontAwesomeIconView();
                icon.setFont(new Font("FontAwesome", 18));
                Account anAccount = list.get(getIndex());
                if (anAccount.getActive() && !anAccount.getLevel().equals(AutheticationLevel.getValue(Level.ADMIM))) {
                    setGraphic(actionColumn.getSingleHyperlinkTableModel(lang.getSources(LangProperties.DISABLED)));
                    icon.setIcon(FontAwesomeIcon.LOCK);
                    actionColumn.getDeleteLink().setText(
                            messageSource.getMessage(lang.getSources(LangProperties.DISABLED), null, Locale.getDefault()));
                    actionColumn.getDeleteLink().setTextFill(Color.RED);
                    actionColumn.getDeleteLink().setGraphic(icon);
                    actionColumn.getDeleteLink().setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            try {
                                anAccount.setActive(false);
                                service.update(anAccount);
                                loadData();
                            } catch (Exception e) {
                                windows.errorLoading(lang.getSources(LangProperties.LIST_ACCOUNTS), e);
                                e.printStackTrace();
                            }
                        }
                    });
                } else if (!anAccount.getActive() && !anAccount.getLevel().equals(AutheticationLevel.getValue(Level.ADMIM))) {
                    setGraphic(actionColumn.getSingleHyperlinkTableModel(lang.getSources(LangProperties.DISABLED)));
                    icon.setIcon(FontAwesomeIcon.UNLOCK);
                    actionColumn.getDeleteLink().setText(
                            messageSource.getMessage(lang.getSources(LangProperties.ACTIVATED), null, Locale.getDefault()));
                    actionColumn.getDeleteLink().setTextFill(Color.YELLOWGREEN);
                    actionColumn.getDeleteLink().setGraphic(icon);
                    actionColumn.getDeleteLink().setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            try {
                                anAccount.setActive(true);
                                service.update(anAccount);
                                loadData();
                            } catch (Exception e) {
                                windows.errorLoading(lang.getSources(LangProperties.LIST_ACCOUNTS), e);
                                e.printStackTrace();
                            }
                        }
                    });
                } else if (anAccount.getLevel().equals(AutheticationLevel.getValue(Level.ADMIM))) {
                    icon.setIcon(FontAwesomeIcon.BAN);
                    Label text = new Label(messageSource.getMessage(lang.getSources(LangProperties.FORBIDDEN), null, Locale.getDefault()));
                    text.setGraphic(icon);
                    setGraphic(text);
                }
            }
        }
    }
}
