package dallastools.actions.expeditur;

import dallastools.actions.HomeAction;
import dallastools.controllers.FxInitializable;
import dallastools.controllers.NumberFormatter;
import dallastools.controllers.PrintController;
import dallastools.controllers.TableViewColumnAction;
import dallastools.controllers.dataselections.EmployeeChooser;
import dallastools.controllers.notifications.*;
import dallastools.controllers.stages.SecondStageController;
import dallastools.models.expenditur.DeliveryOfSales;
import dallastools.models.expenditur.DeliveryOfSalesDetails;
import dallastools.models.income.Sales;
import dallastools.models.income.SalesDetails;
import dallastools.models.masterdata.Employee;
import dallastools.services.ServiceOfEmployee;
import dallastools.services.ServiceOfSalesDelivery;
import dallastools.services.ServiceOfSalesInvoice;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Callback;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

/**
 * Created by dimmaryanto on 14/11/15.
 */
public class DeliverySalesDataAction implements FxInitializable {
    private final List<DeliveryOfSalesDetails> deliveryDetailses = new ArrayList<>();
    @FXML
    private ComboBox txtEmployee;
    @FXML
    private TableView<DeliveryOfSalesDetails> tableView;
    @FXML
    private TableColumn<DeliveryOfSalesDetails, Boolean> columnAction;
    @FXML
    private TableColumn<DeliveryOfSalesDetails, String> columnInvoice;
    @FXML
    private TableColumn<DeliveryOfSalesDetails, Integer> columnWeight;
    @FXML
    private TableColumn<DeliveryOfSalesDetails, String> columnShipTo;
    @FXML
    private TableColumn columnViewMore;
    @FXML
    private CheckBox txtStatus;
    @FXML
    private DatePicker txtDate;
    @FXML
    private Button btnAction;
    @FXML
    private Spinner<Double> txtAmount;

    private DialogBalloon ballon;
    private DialogWindows windows;
    private ServiceOfSalesDelivery service;
    private ApplicationContext springContext;
    private MessageSource messageSource;
    private Boolean update;
    private SpinnerValueFactory.DoubleSpinnerValueFactory amountValueFactory;
    private Logger log = LoggerFactory.getLogger(DeliverySalesDataAction.class);
    private DeliveryOfSales delivery;
    private EmployeeChooser chooser;
    private ServiceOfEmployee employeeService;
    private HashMap<String, Employee> employeeHashMap;
    private TableViewColumnAction actionColumn;
    private ValidationSupport validator;
    private PrintController print;
    private ValidatorMessages validatorMessages;
    private LangSource lang;
    private NumberFormatter numberFormatter;
    private HashMap<Sales, List<SalesDetails>> salesListHashMap;
    private ServiceOfSalesInvoice serviceOfSales;
    private HomeAction homeAction;

    public void setUpdate(Boolean update) {
        this.update = update;
    }

    private void initValidator() {
        this.validator = new ValidationSupport();
        validator.registerValidator(txtDate, (Control c, LocalDate date) ->
                ValidationResult.fromWarningIf(c, validatorMessages.validatorDateNotEqualsNow(),
                        !LocalDate.now().equals(date)));
        validator.registerValidator(txtEmployee,
                Validator.createEmptyValidator(validatorMessages.validatorNotSelected(lang.getSources(LangProperties.DATA_AN_EMPLOYEE)), Severity.ERROR));
        validator.invalidProperty().addListener((observable, oldValue, newValue) -> btnAction.setDisable(newValue));
    }

    @Override
    public void doClose() {
        HomeAction action = springContext.getBean(HomeAction.class);
        action.showSalesDelivery();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.springContext = applicationContext;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tableView.setSelectionModel(null);

        txtDate.setValue(LocalDate.now());

        amountValueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0, Double.MAX_VALUE, 0, 500);

        txtAmount.setValueFactory(amountValueFactory);
        txtAmount.setEditable(true);
        txtAmount.getEditor().setAlignment(Pos.CENTER_RIGHT);

        columnInvoice.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getSales().getTransId()));
        columnWeight.setCellValueFactory(new PropertyValueFactory<DeliveryOfSalesDetails, Integer>("wightPerKg"));
        columnWeight.setCellFactory(new Callback<TableColumn<DeliveryOfSalesDetails, Integer>, TableCell<DeliveryOfSalesDetails, Integer>>() {
            @Override
            public TableCell<DeliveryOfSalesDetails, Integer> call(TableColumn<DeliveryOfSalesDetails, Integer> param) {
                return new TableCell<DeliveryOfSalesDetails, Integer>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) setText(null);
                        else {
                            setAlignment(Pos.CENTER);
                            setText(numberFormatter.getNumber(item));
                        }
                    }
                };
            }
        });
        columnShipTo.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getSales().getShipTo()));
        columnViewMore.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn param) {
                return new TableColumnViewMore(tableView.getItems());
            }
        });


    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private Double getGrantTotalTransaction() {
        Double value = 0.0;
        for (DeliveryOfSalesDetails delivery : deliveryDetailses) {
            value += delivery.getSales().getGrantTotal();
        }
        return value;
    }


    @FXML
    public void doAction() {
        delivery.setStatus(txtStatus.isSelected());
        delivery.setGrantTotal(txtAmount.getValueFactory().getValue());
        if (deliveryDetailses.size() >= 1) {
            if (update) {
                try {
                    service.update(delivery, deliveryDetailses);
                    ballon.sucessedUpdated(lang.getSources(LangProperties.DATA_SALES_DELIVERY),
                            lang.getSources(LangProperties.ID), delivery.getDeliveryId());
                    doClose();
                } catch (Exception e) {
                    windows.errorUpdate(lang.getSources(LangProperties.DATA_SALES_DELIVERY),
                            lang.getSources(LangProperties.ID), delivery.getDeliveryId(), e);
                    e.printStackTrace();
                }
            } else {
                if (getGrantTotalTransaction() >= Double.valueOf(1500000)) {
                    try {
                        delivery.setDeliveryId("-");
                        delivery.setDateSent(Date.valueOf(txtDate.getValue()));
                        delivery.setEmployee(employeeHashMap.get(txtEmployee.getValue()));

                        //log.info("Data Transaksi yang dipilih sebanyak {} data", deliveryDetailses.size());
                        Integer sequance = service.save(delivery, deliveryDetailses);
                        String key = getDeliveryId(sequance);

                        delivery.setDeliveryId(key);
                        service.update(delivery);

                        doClose();

                        ballon.sucessedSave(lang.getSources(LangProperties.DATA_SALES_DELIVERY),
                                delivery.getDeliveryId());

                        print.showSalesDeliveries(lang.getSources(LangProperties.DATA_SALES_DELIVERY),
                                delivery, deliveryDetailses, homeAction.getAccount(), salesListHashMap);
                    } catch (Exception e) {
                        windows.errorSave(lang.getSources(LangProperties.DATA_SALES_DELIVERY), e);
                        e.printStackTrace();
                    }
                } else {
                    ballon.warningNotEnoughSending(lang.getSources(LangProperties.DATA_SALES_DELIVERY), numberFormatter.getCurrency(1500000));
                }
            }
        } else {
            ballon.warningNotNullMessage(lang.getSources(LangProperties.DATA_SALES_DELIVERY),
                    messageSource.getMessage(lang.getSources(LangProperties.LIST_OF_ITEMS), null, Locale.getDefault()));
        }
    }

    private void loadComponentNeeded() {
        try {
            employeeHashMap.clear();
            txtEmployee.getItems().clear();
            //log.info("Bersihkan Data Pegawai");
            List<Employee> employees = employeeService.findAll();
            for (Employee anEmployee : employees) {
                employeeHashMap.put(chooser.getValue(anEmployee), anEmployee);
                txtEmployee.getItems().add(chooser.getValue(anEmployee));
            }
        } catch (Exception e) {
            windows.errorLoading(lang.getSources(LangProperties.LIST_OF_EMPLOYEES), e);
            e.printStackTrace();
        }
    }

    private void enableComponent(Boolean active) {
        txtAmount.setDisable(!active);
        txtEmployee.setDisable(!active);
        txtDate.setDisable(!active);
    }

    public void forPayment(DeliveryOfSales delivery) {
        initValidator();
        columnAction.setCellFactory(new Callback<TableColumn<DeliveryOfSalesDetails, Boolean>, TableCell<DeliveryOfSalesDetails, Boolean>>() {
            @Override
            public TableCell<DeliveryOfSalesDetails, Boolean> call(TableColumn<DeliveryOfSalesDetails, Boolean> param) {
                return new TableColumnSelectedForPayment(tableView.getItems());
            }
        });
        try {
            setUpdate(true);

            txtStatus.setSelected(true);
            this.delivery = delivery;
            loadComponentNeeded();
            enableComponent(false);

            txtDate.setValue(delivery.getDateSent().toLocalDate());
            txtEmployee.setValue(chooser.getValue(delivery.getEmployee()));
            txtAmount.setDisable(false);

            tableView.getItems().clear();

            List<DeliveryOfSalesDetails> list = service.findDeliveryDetailForDelivery(delivery);
            for (DeliveryOfSalesDetails details : list) {
                tableView.getItems().add(details);
            }
            validator.redecorate();
        } catch (Exception e) {
            windows.errorLoading(lang.getSources(LangProperties.LIST_OF_SALES_DELIVERIES), e);
            e.printStackTrace();
            doClose();
        }
    }

    public void newData(ObservableList<Sales> items) {
        try {
            this.salesListHashMap.clear();
            initValidator();
            columnAction.setCellFactory(new Callback<TableColumn<DeliveryOfSalesDetails, Boolean>, TableCell<DeliveryOfSalesDetails, Boolean>>() {
                @Override
                public TableCell<DeliveryOfSalesDetails, Boolean> call(TableColumn<DeliveryOfSalesDetails, Boolean> param) {
                    return new TableColumnSelected(tableView.getItems());
                }
            });
            setUpdate(false);

            txtStatus.setSelected(false);
            this.delivery = new DeliveryOfSales();

            loadComponentNeeded();

            enableComponent(true);
            txtAmount.setDisable(true);

            tableView.getItems().clear();
            //log.info("Tabel pengiriman barang dikosongkan");
            for (Sales aSales : items) {
                List<SalesDetails> salesDetailsList = serviceOfSales.findSalesDetailPerSales(aSales);
                salesListHashMap.put(aSales, salesDetailsList);

                DeliveryOfSalesDetails delivery = new DeliveryOfSalesDetails();
                delivery.setSales(aSales);
                delivery.setWightPerKg(0);

                tableView.getItems().add(delivery);
                //log.info("Tabel pengiriman barang diisi dengan transaksi {}", aSales.getTransId());
            }
            validator.redecorate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String getDeliveryId(Integer value) {
        StringBuilder builder = new StringBuilder();
        builder.append("SALES_DEL");
        builder.append("-");
        builder.append(new SimpleDateFormat("yyyyMMdd").format(new java.util.Date()));
        builder.append("-");
        builder.append(value);
        return builder.toString();
    }

    @FXML
    public void doCancel() {
        HomeAction action = springContext.getBean(HomeAction.class);
        action.showSalesDelivery();
    }

    @Autowired
    public void setNumberFormatter(NumberFormatter numberFormatter) {
        this.numberFormatter = numberFormatter;
    }

    @Autowired
    public void setService(ServiceOfSalesDelivery service) {
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
    public void setEmployeeService(ServiceOfEmployee employeeService) {
        this.employeeService = employeeService;
    }

    @Autowired
    public void setEmployeeHashMap(HashMap<String, Employee> employeeHashMap) {
        this.employeeHashMap = employeeHashMap;
    }

    @Autowired
    public void setChooser(EmployeeChooser chooser) {
        this.chooser = chooser;
    }

    @Autowired
    public void setActionColumn(TableViewColumnAction actionColumn) {
        this.actionColumn = actionColumn;
    }

    @Autowired
    public void setPrint(PrintController print) {
        this.print = print;
    }

    @Autowired
    public void setValidatorMessages(ValidatorMessages validatorMessages) {
        this.validatorMessages = validatorMessages;
    }

    @Autowired
    public void setHomeAction(HomeAction homeAction) {
        this.homeAction = homeAction;
    }

    @Autowired
    public void setSalesListHashMap(HashMap<Sales, List<SalesDetails>> salesListHashMap) {
        this.salesListHashMap = salesListHashMap;
    }

    @Autowired
    public void setServiceOfSales(ServiceOfSalesInvoice serviceOfSales) {
        this.serviceOfSales = serviceOfSales;
    }

    @Autowired
    public void setLang(LangSource lang) {
        this.lang = lang;
    }

    private class TableColumnSelected extends CheckBoxTableCell<DeliveryOfSalesDetails, Boolean> {

        private ObservableList<DeliveryOfSalesDetails> list;
        private CheckBox box;

        public TableColumnSelected(ObservableList<DeliveryOfSalesDetails> list) {
            this.list = list;
        }

        @Override
        public void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) setGraphic(null);
            else {
                DeliveryOfSalesDetails value = list.get(getIndex());
                box = new CheckBox(messageSource.getMessage(lang.getSources(LangProperties.WILL_NOT_BE_SENT), null, Locale.getDefault()));
                box.setTextFill(Color.RED);
                box.selectedProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        if (newValue) {
                            deliveryDetailses.add(value);
                            box.setText(messageSource.getMessage(lang.getSources(LangProperties.WILL_BE_SENT), null, Locale.getDefault()));
                            box.setTextFill(Color.BLACK);
                            ballon.warningSendingMessage(lang.getSources(LangProperties.DATA_A_SALES),
                                    lang.getSources(LangProperties.WILL_BE_SENT_WITH_PARAM), value.getSales().getTransId());
                            //log.info("Transaksi Penjualan dengan kode {} telah ditambhakan!", value.getSales().getTransId());
                        } else {
                            //log.info("Transaksi Penjualan dengan kode {} telah dihapus!", value.getSales().getTransId());
                            box.setText(messageSource.getMessage(lang.getSources(LangProperties.WILL_NOT_BE_SENT), null, Locale.getDefault()));
                            box.setTextFill(Color.RED);
                            ballon.warningSendingMessage(lang.getSources(LangProperties.DATA_A_SALES),
                                    lang.getSources(LangProperties.WILL_NOT_BE_SENT_WITH_PARAM),
                                    value.getSales().getTransId());
                            deliveryDetailses.remove(value);
                        }
                    }
                });
                setGraphic(box);
            }
        }
    }

    private class TableColumnSelectedForPayment extends CheckBoxTableCell<DeliveryOfSalesDetails, Boolean> {

        private ObservableList<DeliveryOfSalesDetails> list;
        private CheckBox box;

        public TableColumnSelectedForPayment(ObservableList<DeliveryOfSalesDetails> list) {
            this.list = list;
        }

        @Override
        public void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) setGraphic(null);
            else {
                DeliveryOfSalesDetails value = list.get(getIndex());
                box = new CheckBox(messageSource.getMessage(lang.getSources(LangProperties.NOT_YET_RECEIVED), null, Locale.getDefault()));
                box.setTextFill(Color.RED);
                box.selectedProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        if (newValue) {
                            deliveryDetailses.add(value);
                            box.setText(messageSource.getMessage(lang.getSources(LangProperties.ALREADY_RECIEVED), null, Locale.getDefault()));
                            ballon.warningSendingMessage(lang.getSources(LangProperties.DATA_A_SALES),
                                    lang.getSources(LangProperties.ALREADY_RECEIVED_WITH_PARAM), value.getSales().getTransId());
                            box.setTextFill(Color.BLACK);
                            //log.info("Transaksi Penjualan dengan kode {} telah Diterima!", value.getSales().getTransId());
                        } else {
                            //log.info("Transaksi Penjualan dengan kode {} belum Diterima!", value.getSales().getTransId());
                            box.setText(messageSource.getMessage(lang.getSources(LangProperties.NOT_YET_RECEIVED), null, Locale.getDefault()));
                            box.setTextFill(Color.RED);
                            ballon.warningSendingMessage(lang.getSources(LangProperties.DATA_A_SALES),
                                    lang.getSources(LangProperties.NOT_YET_RECEIVED_WITH_PARAM), value.getSales().getTransId());
                            deliveryDetailses.remove(value);
                        }
                    }
                });
                setGraphic(box);
            }
        }
    }

    private class TableColumnViewMore extends TableCell {
        ObservableList<DeliveryOfSalesDetails> list;

        public TableColumnViewMore(ObservableList<DeliveryOfSalesDetails> list) {
            this.list = list;
        }

        @Override
        protected void updateItem(Object item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) setGraphic(null);
            else {
                FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.PAPERCLIP);
                icon.setFont(new Font("FontAwesome", 18));
                DeliveryOfSalesDetails value = list.get(getIndex());
                setAlignment(Pos.CENTER);
                setGraphic(actionColumn.getSingleHyperlinkTableModel(lang.getSources(LangProperties.VIEW)));
                actionColumn.getDeleteLink().setGraphic(icon);
                actionColumn.getDeleteLink().setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        SecondStageController controller = springContext.getBean(SecondStageController.class);
                        DeliverySalesDataItemAction action = springContext.getBean(DeliverySalesDataItemAction.class);
                        controller.getSecondStage().setTitle(
                                messageSource.getMessage(
                                        lang.getSources(LangProperties.SALES_DETAILS_WITH_PARAM),
                                        new Object[]{value.getSales().getTransId()},
                                        Locale.getDefault()));
                        Sales sales = value.getSales();
                        action.showData(sales, salesListHashMap.get(sales));
                    }
                });
            }
        }
    }
}
