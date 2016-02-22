package dallastools.actions.expeditur;

import dallastools.actions.HomeAction;
import dallastools.controllers.FxInitializable;
import dallastools.controllers.NumberFormatter;
import dallastools.controllers.TableViewColumnAction;
import dallastools.controllers.notifications.DialogBalloon;
import dallastools.controllers.notifications.DialogWindows;
import dallastools.controllers.notifications.LangProperties;
import dallastools.controllers.notifications.LangSource;
import dallastools.models.expenditur.PurchaseInvoice;
import dallastools.models.masterdata.Suplayer;
import dallastools.services.ServiceOfPurchaseInvoice;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Font;
import javafx.util.Callback;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by dimmaryanto on 17/10/15.
 */
public class PurchaseInvoiceAction implements FxInitializable {

    @FXML
    public TableColumn<PurchaseInvoice, Boolean> columnExpired;
    @FXML
    private TableColumn<PurchaseInvoice, Double> columnPaid;
    @FXML
    private TableView<PurchaseInvoice> tableView;
    @FXML
    private TableColumn<PurchaseInvoice, Integer> columnId;
    @FXML
    private TableColumn<PurchaseInvoice, Date> columnOrder;
    @FXML
    private TableColumn<PurchaseInvoice, String> columnSuplayerPhone;
    @FXML
    private TableColumn<PurchaseInvoice, String> columnSuplayerName;
    @FXML
    private TableColumn<PurchaseInvoice, String> columnAction;

    private ServiceOfPurchaseInvoice service;
    private DialogBalloon ballon;
    private HomeAction homeAction;
    private DialogWindows windows;
    private TableViewColumnAction actionColumn;
    private ApplicationContext springContext;
    private MessageSource messageSource;
    private LangSource lang;
    private NumberFormatter numberFormatter;


    @Override
    public void doClose() {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.springContext = applicationContext;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        columnId.setCellValueFactory(new PropertyValueFactory<PurchaseInvoice, Integer>("id"));
        columnOrder.setCellValueFactory(new PropertyValueFactory<PurchaseInvoice, Date>("transDate"));
        columnSuplayerName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PurchaseInvoice, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<PurchaseInvoice, String> param) {
                if (param != null) {
                    Suplayer aSuplayer = param.getValue().getSuplayer();
                    if (aSuplayer != null) {
                        return new SimpleStringProperty(aSuplayer.getName());
                    } else return new SimpleStringProperty("");
                } else
                    return null;
            }
        });
        columnSuplayerPhone.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PurchaseInvoice, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<PurchaseInvoice, String> param) {
                if (param != null) {
                    Suplayer aSuplayer = param.getValue().getSuplayer();
                    if (aSuplayer != null) {
                        return new SimpleStringProperty(aSuplayer.getPhone());
                    } else return new SimpleStringProperty("");
                } else
                    return null;
            }
        });
        columnAction.setCellFactory(new Callback<TableColumn<PurchaseInvoice, String>, TableCell<PurchaseInvoice, String>>() {
            @Override
            public TableCell<PurchaseInvoice, String> call(TableColumn<PurchaseInvoice, String> param) {
                return new TableColumnAction(tableView.getItems());
            }
        });
        columnPaid.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PurchaseInvoice, Double>, ObservableValue<Double>>() {
            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<PurchaseInvoice, Double> param) {
                if (param != null) {
                    PurchaseInvoice invoice = param.getValue();
                    if (invoice != null) {
                        return new SimpleObjectProperty<Double>(invoice.getGrantTotal() - invoice.getAmmount());
                    } else return null;
                } else
                    return null;
            }
        });
        columnPaid.setCellFactory(new Callback<TableColumn<PurchaseInvoice, Double>, TableCell<PurchaseInvoice, Double>>() {
            @Override
            public TableCell<PurchaseInvoice, Double> call(TableColumn<PurchaseInvoice, Double> param) {
                return new TableColumnPaidAction();
            }
        });
        columnExpired.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PurchaseInvoice, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<PurchaseInvoice, Boolean> param) {
                if (param != null) {
                    PurchaseInvoice invoice = param.getValue();
                    if (invoice != null) {
                        Double paid = invoice.getGrantTotal() - invoice.getAmmount();
                        LocalDate expired = invoice.getTransDate().toLocalDate().plusMonths(1);
                        return new SimpleObjectProperty<Boolean>(paid > 0 && LocalDate.now().isAfter(expired));
                    } else return null;
                } else
                    return null;
            }
        });
        columnExpired.setCellFactory(new Callback<TableColumn<PurchaseInvoice, Boolean>, TableCell<PurchaseInvoice, Boolean>>() {
            @Override
            public TableCell<PurchaseInvoice, Boolean> call(TableColumn<PurchaseInvoice, Boolean> param) {
                return new TableColumnExpiredAction(tableView.getItems());
            }
        });
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void newPurchase() {
        PurchaseInvoiceDataAction action = springContext.getBean(PurchaseInvoiceDataAction.class);
        homeAction.updateContent();
        action.newData();
    }

    @FXML
    public void loadData() {
        try {
            tableView.getItems().clear();
            List<PurchaseInvoice> list = service.findAllPurchaseInvoice();
            windows.loading(tableView.getItems(), list, lang.getSources(LangProperties.LIST_PURCHASES));
        } catch (Exception e) {
            windows.errorLoading(lang.getSources(LangProperties.LIST_PURCHASES), e);
            e.printStackTrace();
        }
    }

    @FXML
    public void tableViewClearSelection() {
        tableView.getSelectionModel().clearSelection();
    }

    @Autowired
    public void setLang(LangSource lang) {
        this.lang = lang;
    }

    @Autowired
    public void setService(ServiceOfPurchaseInvoice service) {
        this.service = service;
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
    public void setWindows(DialogWindows windows) {
        this.windows = windows;
    }

    @Autowired
    public void setActionColumn(TableViewColumnAction actionColumn) {
        this.actionColumn = actionColumn;
    }

    @Autowired
    public void setNumberFormatter(NumberFormatter numberFormatter) {
        this.numberFormatter = numberFormatter;
    }

    private class TableColumnAction extends TableCell<PurchaseInvoice, String> {
        ObservableList<PurchaseInvoice> list;

        public TableColumnAction(ObservableList<PurchaseInvoice> list) {
            this.list = list;
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) setGraphic(null);
            else {
                PurchaseInvoice invoice = list.get(getIndex());
                setGraphic(actionColumn.getMasterDetailTableModel());
                actionColumn.getDeleteLink().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if (windows.confirmDelete(
                                lang.getSources(LangProperties.DATA_PURCHASE),
                                invoice.getSuplayer().getName(),
                                lang.getSources(LangProperties.ID),
                                invoice.getId()
                        ).get() == ButtonType.OK) {
                            try {
                                service.updateItemBeforeUpdateOrDelete(invoice, false);
                                service.deletePurchaseDetailsByPurchaseInvoice(invoice);
                                service.delete(invoice);
                                loadData();
                                ballon.sucessedRemoved(lang.getSources(LangProperties.DATA_PURCHASE));
                            } catch (Exception e) {
                                windows.errorRemoved(lang.getSources(LangProperties.DATA_PURCHASE), lang.getSources(LangProperties.ID), invoice.getId(), e);
                                e.printStackTrace();
                            }
                        }
                    }
                });
                actionColumn.getUpdateLink().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        PurchaseInvoiceDataAction action = springContext.getBean(PurchaseInvoiceDataAction.class);
                        homeAction.updateContent();
                        action.exitsData(invoice);
                    }
                });
                actionColumn.getDetailLink().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        PurchaseInvoiceDataAction action = springContext.getBean(PurchaseInvoiceDataAction.class);
                        homeAction.updateContent();
                        action.readOnly(invoice);
                    }
                });
            }
        }
    }

    private class TableColumnPaidAction extends CheckBoxTableCell<PurchaseInvoice, Double> {
        private CheckBox box;

        @Override
        public void updateItem(Double item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) setGraphic(null);
            else {
                box = new CheckBox(messageSource.getMessage(lang.getSources(LangProperties.NOT_YET), null, Locale.getDefault()));
                box.selectedProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        if (newValue)
                            box.setText(messageSource.getMessage(lang.getSources(LangProperties.PAID), null, Locale.getDefault()));
                        else
                            box.setText(messageSource.getMessage(lang.getSources(LangProperties.NOT_YET), null, Locale.getDefault()));
                    }
                });
                box.setDisable(true);
                box.setOpacity(0.9);
                box.setSelected(item == 0);
                setGraphic(box);
            }
        }
    }

    private class TableColumnExpiredAction extends TableCell<PurchaseInvoice, Boolean> {

        private List<PurchaseInvoice> list;

        public TableColumnExpiredAction(List<PurchaseInvoice> list) {
            this.list = list;
        }

        @Override
        protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) setGraphic(null);
            else {
                FontAwesomeIconView icon = new FontAwesomeIconView();
                icon.setFont(new Font("FontAwesome", 14));
                Label text = new Label();
                PurchaseInvoice invoice = list.get(getIndex());
                Double paid = invoice.getGrantTotal() - invoice.getAmmount();
                LocalDate expired = invoice.getTransDate().toLocalDate().plusMonths(1);
                if (paid <= 0.0) {
                    text.setText(messageSource.getMessage(lang.getSources(LangProperties.HAS_PAID_OF), null, Locale.getDefault()));
                    icon.setIcon(FontAwesomeIcon.BELL_SLASH_ALT);
                } else if (paid > 0 && LocalDate.now().isBefore(expired)) {
                    text.setText(messageSource.getMessage(lang.getSources(LangProperties.SOON_TO_BE_REPAID), null, Locale.getDefault()));
                    icon.setIcon(FontAwesomeIcon.BELL_ALT);
                } else {
                    text.setText(messageSource.getMessage(lang.getSources(LangProperties.HAS_EXPIRED), null, Locale.getDefault()));
                    icon.setIcon(FontAwesomeIcon.CLOSE);
                }
                text.setGraphic(icon);
                setGraphic(text);
            }
        }
    }

}
