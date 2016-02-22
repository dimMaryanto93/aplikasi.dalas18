package dallastools.actions.reports;

import dallastools.controllers.FxInitializable;
import dallastools.controllers.NumberFormatter;
import dallastools.controllers.notifications.DialogBalloon;
import dallastools.controllers.notifications.DialogWindows;
import dallastools.controllers.notifications.LangProperties;
import dallastools.controllers.notifications.LangSource;
import dallastools.models.masterdata.Item;
import dallastools.models.masterdata.Unit;
import dallastools.models.other.ItemSum;
import dallastools.models.other.SalesSumOfDate;
import dallastools.models.other.SalesSumOfMonth;
import dallastools.services.ServiceOfIncomeReport;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

/**
 * Created by dimmaryanto on 08/11/15.
 */
public class IncomeReportAction implements FxInitializable {

    private final Logger log = LoggerFactory.getLogger(IncomeReportAction.class);
    private final ObservableList<PieChart.Data> pieDatas = FXCollections.observableArrayList();
    ObservableList<XYChart.Series> xySeries;
    XYChart.Series<String, Double> salesTransaction;
    XYChart.Series<String, Double> salesRecived;
    XYChart.Series<String, Double> income;
    @FXML
    private Hyperlink btnProses;
    @FXML
    private LineChart incomeByMonth;
    @FXML
    private CategoryAxis categoryByMonth;
    @FXML
    private NumberAxis numberByMonth;
    @FXML
    private DatePicker txtDateBefore;
    @FXML
    private DatePicker txtDateAfter;
    @FXML
    private LineChart incomeByDate;
    @FXML
    private CategoryAxis categoryByDate;
    @FXML
    private NumberAxis numberByDate;
    @FXML
    private TextField txtSales;
    @FXML
    private TextField txtIncome;
    @FXML
    private TextField txtSalesTurnover;
    @FXML
    private TextField txtSalesItem;
    @FXML
    private PieChart pieChart;
    @FXML
    private TableView<ItemSum> tableView;
    @FXML
    private TableColumn<ItemSum, String> columnItem;
    @FXML
    private TableColumn<ItemSum, Long> columnQty;
    private ApplicationContext springContext;
    private MessageSource messageSource;
    private ServiceOfIncomeReport service;
    private DialogWindows windows;
    private DialogBalloon ballon;
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
        this.incomeByDate.setAnimated(false);
        this.incomeByDate.setLegendSide(Side.LEFT);

        this.incomeByMonth.setAnimated(false);
        this.incomeByMonth.setLegendSide(Side.LEFT);

        LocalDate date = LocalDate.now();
        txtDateBefore.setValue(date.withDayOfYear(1));
        txtDateAfter.setValue(date.withDayOfYear(date.lengthOfYear()));

        this.pieChart.setClockwise(true);
        this.pieChart.setLabelsVisible(false);
        this.pieChart.setLegendSide(Side.LEFT);
        this.pieChart.setLabelLineLength(15);
        this.pieChart.setData(pieDatas);
        this.pieChart.setAnimated(false);


        tableView.getItems().addListener(new ListChangeListener<ItemSum>() {
            @Override
            public void onChanged(Change<? extends ItemSum> c) {
                if (tableView.getItems().size() <= 0) txtSalesItem.clear();
                else {
                    Iterator<ItemSum> index = tableView.getItems().iterator();
                    Double result = 0.0;
                    while (index.hasNext()) {
                        result += index.next().getQty();
                    }
                    txtSalesItem.setText(numberFormatter.getNumber(result));
                }
            }
        });
        tableView.setSelectionModel(null);

        columnItem.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ItemSum, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<ItemSum, String> param) {
                if (param != null) {
                    Item anItem = param.getValue().getItem();
                    if (anItem != null)
                        return new SimpleStringProperty(anItem.getName() + "/" + anItem.getUnit().getId());
                    else return new SimpleStringProperty(null);
                } else
                    return null;
            }
        });
        columnQty.setCellValueFactory(new PropertyValueFactory<ItemSum, Long>("qty"));
        columnQty.setCellFactory(param -> new TableCell<ItemSum, Long>() {
            @Override
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setText(null);
                else setText(numberFormatter.getNumber(item));
                setAlignment(Pos.CENTER);
            }
        });
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @FXML
    private void doLoad() {
        LocalDate valueBefore = txtDateBefore.getValue();
        LocalDate valueAfter = txtDateAfter.getValue();
        try {
            loadAllItems(valueBefore, valueAfter);
            loadAllSalesByDate(valueBefore, valueAfter);
            loadAllSalesByMonth(valueBefore, valueAfter);
            btnProses.setText(new SimpleDateFormat("E, dd-MMMM-yyyy hh:mm a").format(new Date()));
        } catch (Exception e) {
            btnProses.setText(messageSource.getMessage(lang.getSources(LangProperties.UNPROCESSED), null, Locale.getDefault()));
            windows.errorLoading(lang.getSources(LangProperties.REPORT_SALES_INCOME), e);
            e.printStackTrace();
        }
    }

    private void loadAllSalesByDate(LocalDate valueIn, LocalDate valueOut) throws Exception {
        this.xySeries = FXCollections.observableArrayList();
        this.salesTransaction = new XYChart.Series<>();
        this.salesRecived = new XYChart.Series<>();
        this.income = new XYChart.Series<>();

        incomeByDate.setData(xySeries);

        txtSales.clear();
        txtSalesTurnover.clear();
        txtIncome.clear();

        List<SalesSumOfDate> list = service.findSalesGroupByDate(valueIn, valueOut);
        //log.info("Data Transaksi harian transaksi ditemukan {}", list.size());

        salesTransaction.setName(messageSource.getMessage(lang.getSources(LangProperties.TOTAL_TRANSACTION_REVENUES), null, Locale.getDefault()));
        salesRecived.setName(messageSource.getMessage(lang.getSources(LangProperties.TOTAL_ACCOUNTS_RECIEVABLE), null, Locale.getDefault()));
        income.setName(messageSource.getMessage(lang.getSources(LangProperties.TOTAL_NET_INCOME), null, Locale.getDefault()));

        salesTransaction.getData().add(new XYChart.Data<>(valueIn.minusDays(1).toString(), 0.0));
        salesRecived.getData().add(new XYChart.Data<>(valueIn.minusDays(1).toString(), 0.0));
        income.getData().add(new XYChart.Data<>(valueIn.minusDays(1).toString(), 0.0));

        Double getGrantTotal = 0.0;
        Double getSalesRecivable = 0.0;
        Double getIncome = 0.0;

        for (SalesSumOfDate value : list) {
            getGrantTotal += value.getGrantTotal();
            getSalesRecivable += value.getSalesRecivable();
            getIncome += value.getIncome();

            salesTransaction.getData().add(new XYChart.Data<String, Double>(value.getDate().toString(), value.getGrantTotal()));
            salesRecived.getData().add(new XYChart.Data<String, Double>(value.getDate().toString(), value.getSalesRecivable()));
            income.getData().add(new XYChart.Data<String, Double>(value.getDate().toString(), value.getIncome()));
            // log.info("Tanggal Transaksi {}, total Transaksi {}, sisa {}", new Object[]{value.getDate(), value.getGrantTotal(), value.getSalesRecivable()});
        }

        txtSales.setText(numberFormatter.getCurrency(getGrantTotal));
        txtIncome.setText(numberFormatter.getCurrency(getIncome));
        txtSalesTurnover.setText(numberFormatter.getCurrency(getSalesRecivable));

        salesTransaction.getData().add(new XYChart.Data<>(valueOut.plusDays(1).toString(), 0.0));
        salesRecived.getData().add(new XYChart.Data<>(valueOut.plusDays(1).toString(), 0.0));
        income.getData().add(new XYChart.Data<>(valueOut.plusDays(1).toString(), 0.0));

        xySeries.add(salesTransaction);
        xySeries.add(income);
        xySeries.add(salesRecived);
    }

    private void loadAllSalesByMonth(LocalDate valueIn, LocalDate valueOut) throws Exception {

        this.xySeries = FXCollections.observableArrayList();
        this.salesTransaction = new XYChart.Series<>();
        this.salesRecived = new XYChart.Series<>();
        this.income = new XYChart.Series<>();

        incomeByMonth.setData(xySeries);

        List<SalesSumOfMonth> list = service.findSalesGroupByMonth(valueIn, valueOut);

        //log.info("Data Transaksi perbulan ditemukan {}", list.size());

        salesTransaction.setName(messageSource.getMessage(lang.getSources(LangProperties.TOTAL_TRANSACTION_REVENUES), null, Locale.getDefault()));
        salesRecived.setName(messageSource.getMessage(lang.getSources(LangProperties.TOTAL_ACCOUNTS_RECIEVABLE), null, Locale.getDefault()));
        income.setName(messageSource.getMessage(lang.getSources(LangProperties.TOTAL_NET_INCOME), null, Locale.getDefault()));

        YearMonth before = YearMonth.of(valueIn.getYear(), valueIn.getMonth()).minusMonths(1);
        salesTransaction.getData().add(new XYChart.Data<>(before.toString(), 0.0));
        salesRecived.getData().add(new XYChart.Data<>(before.toString(), 0.0));
        income.getData().add(new XYChart.Data<>(before.toString(), 0.0));

        for (SalesSumOfMonth value : list) {
            salesTransaction.getData().add(new XYChart.Data<String, Double>(value.getDate(), value.getGrantTotal()));
            salesRecived.getData().add(new XYChart.Data<String, Double>(value.getDate(), value.getSalesRecivable()));
            income.getData().add(new XYChart.Data<String, Double>(value.getDate(), value.getIncome()));
            //log.info("Tanggal Transaksi {}, total Transaksi {}, sisa {}", new Object[]{value.getDate(), value.getGrantTotal(), value.getSalesRecivable()});
        }

        YearMonth after = YearMonth.of(valueOut.getYear(), valueOut.getMonth()).plusMonths(1);
        salesTransaction.getData().add(new XYChart.Data<>(after.toString(), 0.0));
        salesRecived.getData().add(new XYChart.Data<>(after.toString(), 0.0));
        income.getData().add(new XYChart.Data<>(after.toString(), 0.0));

        xySeries.add(salesTransaction);
        xySeries.add(income);
        xySeries.add(salesRecived);
    }

    private void loadAllItems(LocalDate valueIn, LocalDate valueOut) throws Exception {
        List<ItemSum> list = service.findItemGrupBySalesTransaction(valueIn, valueOut);
        Iterator<ItemSum> iterator = list.iterator();
        // log.info("Data Barang yang telah tejual ditemukan {}", list.size());
        tableView.getItems().clear();
        pieDatas.clear();
        while (iterator.hasNext()) {
            ItemSum details = iterator.next();
            Integer qty = details.getQty().intValue();
            Item anItem = details.getItem();
            Unit anUnit = anItem.getUnit();
            tableView.getItems().add(details);
            String label = anItem.getName() + "/" + anUnit.getId();
            pieDatas.add(new PieChart.Data(label, qty));
           /* log.info("Nama Barang {}/{} dengan jumlah {}",
                    new Object[]{anItem.getName(), anUnit.getId(),
                            anItem.getQty()});*/
        }
    }

    @Autowired
    public void setLang(LangSource lang) {
        this.lang = lang;
    }

    @Autowired
    public void setBallon(DialogBalloon ballon) {
        this.ballon = ballon;
    }

    @Autowired
    public void setService(ServiceOfIncomeReport service) {
        this.service = service;
    }

    @Autowired
    public void setWindows(DialogWindows windows) {
        this.windows = windows;
    }

    @Autowired
    public void setNumberFormatter(NumberFormatter numberFormatter) {
        this.numberFormatter = numberFormatter;
    }
}
