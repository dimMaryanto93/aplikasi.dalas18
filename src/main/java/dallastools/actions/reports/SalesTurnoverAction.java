package dallastools.actions.reports;

import dallastools.controllers.FxInitializable;
import dallastools.controllers.notifications.DialogBalloon;
import dallastools.controllers.notifications.DialogWindows;
import dallastools.controllers.notifications.LangProperties;
import dallastools.controllers.notifications.LangSource;
import dallastools.models.masterdata.Customer;
import dallastools.models.other.ItemSum;
import dallastools.models.other.SalesSumOfDate;
import dallastools.models.other.SalesSumOfMonth;
import dallastools.services.ServiceOfSalesTurnover;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import java.net.URL;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by dimmaryanto on 08/11/15.
 */
public class SalesTurnoverAction implements FxInitializable {
    private final Logger log = LoggerFactory.getLogger(SalesTurnoverAction.class);
    private final List<Customer> emptyList = new ArrayList<>();
    private final ObservableList<XYChart.Series> dateSeries = FXCollections.observableArrayList();
    private final ObservableList<XYChart.Series> monthSeries = FXCollections.observableArrayList();
    private final ObservableList<XYChart.Series> itemSeries = FXCollections.observableArrayList();
    private final ObservableList<String> dateCategory = FXCollections.observableArrayList();
    private final ObservableList<String> monthCategory = FXCollections.observableArrayList();
    private final ObservableList<String> itemCategory = FXCollections.observableArrayList();

    @FXML
    private TableView<Customer> tableView;
    @FXML
    private TableColumn<Customer, Integer> columnId;
    @FXML
    private TableColumn<Customer, String> columnName;
    @FXML
    private TableColumn<Customer, String> columnCek;
    @FXML
    private DatePicker txtDateBefore;
    @FXML
    private DatePicker txtDateAfter;
    @FXML
    private Hyperlink btnAction;
    @FXML
    private LineChart lineDateChart;
    @FXML
    private CategoryAxis lineDateCategoryAxis;
    @FXML
    private NumberAxis lineDateNumberAxis;
    @FXML
    private LineChart lineMonthChart;
    @FXML
    private CategoryAxis lineMonthCategoryAxis;
    @FXML
    private NumberAxis lineMonthNumberAxis;
    @FXML
    private BarChart barChart;
    @FXML
    private CategoryAxis barCategoryAxis;
    @FXML
    private NumberAxis barNumberAxis;

    private ApplicationContext springContext;
    private MessageSource messageSource;
    private ServiceOfSalesTurnover service;
    private DialogWindows windows;
    private DialogBalloon ballon;
    private LangSource lang;

    @Override
    public void doClose() {

    }

    public void loadData() {
        try {
            List<Customer> list = service.findAllCustomer();
            windows.loading(tableView.getItems(), list, lang.getSources(LangProperties.LIST_OF_CUSTOMERS));
        } catch (Exception ex) {
            windows.errorLoading(lang.getSources(LangProperties.LIST_OF_CUSTOMERS), ex);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.springContext = applicationContext;
    }

    private StringConverter<LocalDate> date() {
        return new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate object) {
                return DateTimeFormatter.ofPattern("dd MMM yy").format(object);
            }

            @Override
            public LocalDate fromString(String string) {
                return LocalDate.parse(string);
            }
        };
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        barChart.setAnimated(false);
        barChart.setLegendSide(Side.LEFT);
        barChart.setData(itemSeries);
        barCategoryAxis.setCategories(itemCategory);

        lineDateChart.setAnimated(false);
        lineDateChart.setLegendSide(Side.LEFT);
        lineDateChart.setData(dateSeries);
        lineDateCategoryAxis.setCategories(dateCategory);

        lineMonthChart.setAnimated(false);
        lineMonthChart.setLegendSide(Side.LEFT);
        lineMonthChart.setData(monthSeries);
        lineMonthCategoryAxis.setCategories(monthCategory);

        LocalDate now = LocalDate.now();
        txtDateBefore.setValue(now.withDayOfYear(1));
        txtDateAfter.setValue(now.withDayOfYear(Year.now().length()));

        columnId.setCellValueFactory(new PropertyValueFactory<Customer, Integer>("id"));
        columnId.setCellFactory(param -> new TableCell<Customer, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setText(null);
                else setText(item.toString());
                setAlignment(Pos.CENTER);
            }
        });
        columnName.setCellValueFactory(new PropertyValueFactory<Customer, String>("customerName"));
        columnCek.setCellFactory(new Callback<TableColumn<Customer, String>, TableCell<Customer, String>>() {
            @Override
            public TableCell<Customer, String> call(TableColumn<Customer, String> param) {
                return new selectedbyTableView(tableView.getItems());
            }
        });
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @FXML
    public void doAction() {
        try {
            dateCategory.clear();
            itemCategory.clear();
            monthCategory.clear();
            barChart.getData().clear();
            lineMonthChart.getData().clear();
            lineDateChart.getData().clear();
            LocalDate before = txtDateBefore.getValue();
            LocalDate after = txtDateAfter.getValue();
            //log.info("Data Pelanggan yang telah dipilih sebanyak {} data", emptyList.size());
            if (emptyList.size() >= 1) {
                for (Customer aCustomer : emptyList) {
                    //log.info("nama {} dengan kode {}", aCustomer.getCustomerName(), aCustomer.getId());
                    loadIncomeByDate(before, after, aCustomer);
                    loadIncomeByMonth(before, after, aCustomer);
                    loadItemByCustomer(before, after, aCustomer);
                }
            } else {
                ballon.warningEmptyMessage(lang.getSources(LangProperties.REPORT_SALES_TURNOVER), lang.getSources(LangProperties.LIST_OF_CUSTOMERS));
            }
        } catch (Exception e) {
            windows.errorLoading(lang.getSources(LangProperties.REPORT_SALES_TURNOVER), e);
            e.printStackTrace();
        }
    }

    private void loadIncomeByDate(LocalDate before, LocalDate after, Customer aCustomer) throws Exception {
        XYChart.Series<String, Double> customerSeries = new XYChart.Series<>();
        List<SalesSumOfDate> datas = service.findIncomeByDate(before, after, aCustomer);
        customerSeries.setName(aCustomer.getId() + " (" + datas.size() + " data)");
        //log.info("Data Transaksi Penjualan ditemukan {}", datas.size());
        for (int i = 0; i < datas.size(); i++) {
            SalesSumOfDate value = datas.get(i);
            /*log.info("Tanggal transaksi {} penlanggan {}, pendapatan {}, Piutang {}",
                    new Object[]{value.getDate(),
                            aCustomer.getCustomerName(),
                            value.getGrantTotal(),
                            value.getSalesRecivable()});*/
            if (dateCategory.indexOf(value.getDate().toString()) < 0) {
                dateCategory.add(value.getDate().toString());
                //log.info("Date Category Axis {} telah ditambahkan!", value.getDate());
            }
            customerSeries.getData().add(new XYChart.Data<>(value.getDate().toString(), value.getIncome()));
        }
        dateCategory.sort(Comparator.<String>naturalOrder());
        dateSeries.add(customerSeries);
    }

    private void loadIncomeByMonth(LocalDate before, LocalDate after, Customer aCustomer) throws Exception {
        XYChart.Series<String, Double> customerSeries = new XYChart.Series<>();
        List<SalesSumOfMonth> datas = service.findIncomeByMonth(before, after, aCustomer);
        customerSeries.setName(aCustomer.getId() + " (" + datas.size() + " data)");
        //log.info("Data Transaksi Penjualan ditemukan {}", datas.size());
        for (int i = 0; i < datas.size(); i++) {
            SalesSumOfMonth value = datas.get(i);
            /*log.info("Tanggal transaksi {} penlanggan {}, pendapatan {}, Piutang {}",
                    new Object[]{value.getDate(),
                            aCustomer.getCustomerName(),
                            value.getGrantTotal(),
                            value.getSalesRecivable()});*/
            if (monthCategory.indexOf(value.getDate()) < 0) {
                monthCategory.add(value.getDate());
                //log.info("Month Category Axis {} telah ditambahkan!", value.getDate());
            }
            customerSeries.getData().add(new XYChart.Data<>(value.getDate(), value.getIncome()));
        }
        monthCategory.sort(Comparator.<String>naturalOrder());
        monthSeries.add(customerSeries);
    }

    private void loadItemByCustomer(LocalDate before, LocalDate after, Customer customer) throws Exception {
        BarChart.Series<String, Integer> customerSeries = new XYChart.Series<>();
        List<ItemSum> datas = service.findItemsByCustomer(before, after, customer);
        //log.info("Memproses Data Barang berdasarkan nama pelanggan {} ditemukan sebanyak {} data", customer.getCustomerName(), datas.size());
        customerSeries.setName(customer.getId() + " (" + datas.size() + " data)");
        for (int i = 0; i < datas.size(); i++) {
            ItemSum value = datas.get(i);
            if (itemCategory.indexOf(value.getItem().getName()) < 0) {
                itemCategory.add(value.getItem().getName());
                // log.info("Item Category Axis {} telah ditambahkan", value.getItem().getName());
            }
            customerSeries.getData().add(new XYChart.Data<String, Integer>(value.getItem().getName(), value.getQty().intValue()));
        }
        itemCategory.sort(Comparator.<String>naturalOrder());
        itemSeries.add(customerSeries);
    }

    @Autowired
    public void setWindows(DialogWindows windows) {
        this.windows = windows;
    }

    @Autowired
    public void setService(ServiceOfSalesTurnover service) {
        this.service = service;
    }

    @Autowired
    public void setBallon(DialogBalloon ballon) {
        this.ballon = ballon;
    }

    @Autowired
    public void setLang(LangSource lang) {
        this.lang = lang;
    }

    private class selectedbyTableView extends CheckBoxTableCell<Customer, String> {

        private List<Customer> listOfCustomer;

        public selectedbyTableView(List<Customer> listOfCustomer) {
            this.listOfCustomer = listOfCustomer;
        }

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) setGraphic(null);
            else {
                Customer aCustomer = listOfCustomer.get(getIndex());
                CheckBox check = new CheckBox();
                check.setTextFill(Color.RED);
                check.setText(messageSource.getMessage(lang.getSources(LangProperties.WILL_NOT_BE_COUNTED), null, Locale.getDefault()));
                check.selectedProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        if (newValue) {
                            emptyList.add(aCustomer);
                            check.setTextFill(Color.BLACK);
                            check.setText(messageSource.getMessage(lang.getSources(LangProperties.WILL_BE_CALCULATED), null, Locale.getDefault()));
                        } else {
                            emptyList.remove(aCustomer);
                            check.setTextFill(Color.RED);
                            check.setText(messageSource.getMessage(lang.getSources(LangProperties.WILL_NOT_BE_COUNTED), null, Locale.getDefault()));

                        }

                    }
                });
                setGraphic(check);
            }
        }
    }
}
