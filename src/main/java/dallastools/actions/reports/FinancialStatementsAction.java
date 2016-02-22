package dallastools.actions.reports;

import dallastools.actions.HomeAction;
import dallastools.controllers.FxInitializable;
import dallastools.controllers.NumberFormatter;
import dallastools.controllers.PrintController;
import dallastools.controllers.notifications.DialogWindows;
import dallastools.controllers.notifications.LangProperties;
import dallastools.controllers.notifications.LangSource;
import dallastools.controllers.stages.SecondStageController;
import dallastools.models.expenditur.*;
import dallastools.models.income.Sales;
import dallastools.models.other.FinancialStatements;
import dallastools.services.ServiceOfFinancialStatements;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by dimmaryanto on 27/11/15.
 */
public class FinancialStatementsAction implements FxInitializable {
    @FXML
    private Button btnPurchase;
    @FXML
    private Button btnPayroll;
    @FXML
    private Button btnTransporation;
    @FXML
    private Button btnOtherPayment;
    @FXML
    private Button btnSales;
    @FXML
    private Button btnCashReciept;
    @FXML
    private TextField txtPurchase;
    @FXML
    private TextField txtPayrollEmpProduction;
    @FXML
    private TextField txtTransport;
    @FXML
    private TextField txtOtherPayment;
    @FXML
    private TextField txtTotalExpenditure;
    @FXML
    private TextField txtSales;
    @FXML
    private TextField txtTotalIncome;
    @FXML
    private TextField txtEmployeeCashReciept;
    @FXML
    private TextField txtNetIncome;
    @FXML
    private DatePicker txtFirstDate;
    @FXML
    private DatePicker txtLastDate;
    @FXML
    private Hyperlink action;
    @FXML
    private CheckBox checkedPrinted;

    private ApplicationContext springContext;
    private MessageSource messageSource;
    private HomeAction homeAction;
    private FinancialStatementsDetailAction showDetails;
    private SecondStageController stage;

    private ObservableList<Sales> emptyListSales;
    private ObservableList<PurchaseInvoice> emptyListPurchases;
    private ObservableList<PaymentInvoice> emptyListOtherPayment;
    private ObservableList<PayrollAnEmployee> emptyListPayrolls;
    private ObservableList<DeliveryOfSales> emptyListTransports;
    private ObservableList<CashRecieptForEmployee> emptyListCashEmployees;
    private ObservableList<FinancialStatements> statements;
    private PrintController print;
    private Logger log;
    private DialogWindows windows;
    private ServiceOfFinancialStatements service;
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
        this.log = LoggerFactory.getLogger(FinancialStatementsAction.class);

        this.emptyListSales = FXCollections.observableArrayList();
        this.emptyListSales.addListener(new ListChangeListener<Sales>() {
            @Override
            public void onChanged(Change<? extends Sales> c) {
                btnSales.setDisable(c.getList().size() <= 0);
            }
        });
        this.emptyListPurchases = FXCollections.observableArrayList();
        this.emptyListPurchases.addListener(new ListChangeListener<PurchaseInvoice>() {
            @Override
            public void onChanged(Change<? extends PurchaseInvoice> c) {
                btnPurchase.setDisable(c.getList().size() <= 0);
            }
        });
        this.emptyListOtherPayment = FXCollections.observableArrayList();
        this.emptyListOtherPayment.addListener(new ListChangeListener<PaymentInvoice>() {
            @Override
            public void onChanged(Change<? extends PaymentInvoice> c) {
                btnOtherPayment.setDisable(c.getList().size() <= 0);
            }
        });
        this.emptyListTransports = FXCollections.observableArrayList();
        this.emptyListTransports.addListener(new ListChangeListener<DeliveryOfSales>() {
            @Override
            public void onChanged(Change<? extends DeliveryOfSales> c) {
                btnTransporation.setDisable(c.getList().size() <= 0);
            }
        });
        this.emptyListPayrolls = FXCollections.observableArrayList();
        this.emptyListPayrolls.addListener(new ListChangeListener<PayrollAnEmployee>() {
            @Override
            public void onChanged(Change<? extends PayrollAnEmployee> c) {
                btnPayroll.setDisable(c.getList().size() <= 0);
            }
        });
        this.emptyListCashEmployees = FXCollections.observableArrayList();
        this.emptyListCashEmployees.addListener(new ListChangeListener<CashRecieptForEmployee>() {
            @Override
            public void onChanged(Change<? extends CashRecieptForEmployee> c) {
                btnCashReciept.setDisable(c.getList().size() <= 0);
            }
        });

        LocalDate now = LocalDate.now();
        this.txtFirstDate.getEditor().setAlignment(Pos.CENTER_RIGHT);
        this.txtFirstDate.setEditable(false);
        this.txtFirstDate.setValue(now.withDayOfMonth(1));

        this.txtLastDate.getEditor().setAlignment(Pos.CENTER_RIGHT);
        this.txtLastDate.setEditable(false);
        this.txtLastDate.setValue(now.withDayOfMonth(YearMonth.now().lengthOfMonth()));

    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @FXML
    private void showPurchases() {
        showDetails = springContext.getBean(FinancialStatementsDetailAction.class);
        stage.getSecondStage().setTitle(
                messageSource.getMessage(lang.getSources(LangProperties.LIST_PURCHASES), null, Locale.getDefault()));
        this.statements = FXCollections.observableArrayList();
        for (PurchaseInvoice value : emptyListPurchases) {
            StringBuilder sb = new StringBuilder();
            sb.append("PURCHASE_INV-");
            sb.append(DateTimeFormatter.ofPattern("yyyyMMdd").format(value.getTransDate().toLocalDate()));
            sb.append("-");
            sb.append(value.getId());
            FinancialStatements financial = new FinancialStatements();
            financial.setId(sb.toString());
            financial.setDebit((value.getAmmount() - value.getGrantTotal()));
            financial.setCredit(value.getGrantTotal());
            financial.setTotal(value.getAmmount());
            financial.setTotal(value.getAmmount());
            statements.add(financial);
        }
        showDetails.setData(statements);
    }

    @FXML
    private void showPayroll() {
        showDetails = springContext.getBean(FinancialStatementsDetailAction.class);
        stage.getSecondStage().setTitle(messageSource.getMessage(
                lang.getSources(LangProperties.LIST_PAYROLL_EMPLOYEE_DEPT_PRODUCTION), null, Locale.getDefault()));
        this.statements = FXCollections.observableArrayList();
        for (PayrollAnEmployee value : emptyListPayrolls) {
            StringBuilder sb = new StringBuilder();
            sb.append("PAYROLL_INV-");
            sb.append(DateTimeFormatter.ofPattern("yyyyMMdd").format(value.getDate().toLocalDate()));
            sb.append("-");
            sb.append(value.getId());
            FinancialStatements financial = new FinancialStatements();
            financial.setId(sb.toString());
            financial.setDebit(-(value.getOtherAmount() + value.getAmount()));
            financial.setCredit(0.0);
            financial.setTotal((value.getOtherAmount() + value.getAmount()));
            statements.add(financial);
        }
        showDetails.setData(statements);
    }

    @FXML
    private void showTransport() {
        showDetails = springContext.getBean(FinancialStatementsDetailAction.class);
        stage.getSecondStage().setTitle(messageSource
                .getMessage(lang.getSources(LangProperties.LIST_OF_SALES_DELIVERIES), null, Locale.getDefault()));
        this.statements = FXCollections.observableArrayList();
        for (DeliveryOfSales value : emptyListTransports) {
            FinancialStatements financial = new FinancialStatements();
            financial.setId(value.getDeliveryId());
            financial.setDebit(-value.getGrantTotal());
            financial.setCredit(0.0);
            financial.setTotal(value.getGrantTotal());
            statements.add(financial);
        }
        showDetails.setData(statements);
    }

    @FXML
    private void showOtherPayment() {
        showDetails = springContext.getBean(FinancialStatementsDetailAction.class);
        stage.getSecondStage().setTitle(messageSource.getMessage(lang.getSources(LangProperties.LIST_OF_OTHER_PAYMENTS),
                null, Locale.getDefault()));
        this.statements = FXCollections.observableArrayList();
        for (PaymentInvoice value : emptyListOtherPayment) {
            StringBuilder sb = new StringBuilder();
            sb.append("PAY_");
            sb.append(value.getCategory().getId().toUpperCase());
            sb.append("-");
            sb.append(DateTimeFormatter.ofPattern("yyyyMMdd").format(value.getTransDate().toLocalDate()));
            sb.append("-");
            sb.append(value.getId());
            FinancialStatements financial = new FinancialStatements();
            financial.setId(sb.toString());
            financial.setDebit(-value.getAmmount());
            financial.setCredit(0.0);
            financial.setTotal(value.getAmmount());
            statements.add(financial);
        }
        showDetails.setData(statements);
    }

    @FXML
    private void showSales() {
        showDetails = springContext.getBean(FinancialStatementsDetailAction.class);
        stage.getSecondStage().setTitle(
                messageSource.getMessage(lang.getSources(LangProperties.LIST_OF_SALES), null, Locale.getDefault()));
        this.statements = FXCollections.observableArrayList();
        for (Sales sales : emptyListSales) {
            FinancialStatements financial = new FinancialStatements();
            financial.setId(sales.getTransId());
            financial.setDebit(sales.getGrantTotal());
            financial.setCredit((sales.getAmmount() - sales.getGrantTotal()));
            financial.setTotal(sales.getAmmount());
            this.statements.add(financial);
        }
        showDetails.setData(statements);
    }

    @FXML
    private void showCashReciept() {
        showDetails = springContext.getBean(FinancialStatementsDetailAction.class);
        stage.getSecondStage().setTitle(messageSource
                .getMessage(lang.getSources(LangProperties.LIST_OF_CASH_RECIEPT_EMPLOYEE), null, Locale.getDefault()));
        this.statements = FXCollections.observableArrayList();
        for (CashRecieptForEmployee value : emptyListCashEmployees) {
            StringBuilder sb = new StringBuilder();
            sb.append("CASH-");
            sb.append(DateTimeFormatter.ofPattern("yyyyMMdd").format(value.getDate().toLocalDate()));
            sb.append("-");
            sb.append(value.getId());
            FinancialStatements financial = new FinancialStatements();
            financial.setId(sb.toString());
            financial.setDebit(-value.getAmount());
            financial.setCredit(value.getPayment());
            financial.setTotal(value.getAmount() - value.getPayment());
            this.statements.add(financial);
        }
        showDetails.setData(statements);
    }

    private void clearFiels() {
        txtSales.clear();
        txtPurchase.clear();
        txtOtherPayment.clear();
        txtTransport.clear();
        txtPayrollEmpProduction.clear();
        txtEmployeeCashReciept.clear();

        txtTotalIncome.clear();
        txtTotalExpenditure.clear();
        txtNetIncome.clear();
    }

    @FXML
    private void doAction() {
        clearFiels();
        clearListItems();
        Task<Object> taskWorker = getWorker();
        windows.loading(taskWorker, lang.getSources(LangProperties.FINANCIAL_STATEMENT));
        taskWorker.setOnSucceeded(event -> {
            txtSales.setText(numberFormatter.getCurrency(getTotalSalesTurnover()));
            txtPurchase.setText(numberFormatter.getCurrency(getTotalPurchase()));
            txtTransport.setText(numberFormatter.getCurrency(getTotalSalesDelivery()));
            txtPayrollEmpProduction.setText(numberFormatter.getCurrency(getTotalPayrollEmployees()));
            txtOtherPayment.setText(numberFormatter.getCurrency(getTotalPayment()));
            txtEmployeeCashReciept.setText(numberFormatter.getCurrency(getTotalCashRecieptEmployees()));

            txtTotalIncome.setText(numberFormatter.getCurrency(getIncomeStatement()));
            txtTotalExpenditure.setText(numberFormatter.getCurrency(getExpenditurStatement()));

            Double netIncome = getIncomeStatement() - getExpenditurStatement();
            txtNetIncome.setText(numberFormatter.getCurrency(netIncome));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E , dd-MMMM-yyyy hh:mm a");
            action.setText(formatter.format(LocalDateTime.now()));

            if (checkedPrinted.isSelected()) {
                try {
                    DateTimeFormatter formater = DateTimeFormatter.ofPattern("yyyy-MMM-dd");
                    StringBuilder sb = new StringBuilder();
                    sb.append(formater.format(txtFirstDate.getValue()));
                    sb.append(" s/d ");
                    sb.append(formater.format(txtLastDate.getValue()));
                    HashMap<String, Object> valueMapped = new HashMap<>();
                    valueMapped.put("datePeriode", sb.toString());
                    valueMapped.put("sales", getTotalSales());
                    valueMapped.put("salesTurnover", getTotalSales() - getTotalSalesTurnover());
                    valueMapped.put("transport", getTotalSalesDelivery());
                    valueMapped.put("production", getTotalPayrollEmployees());
                    valueMapped.put("purchase", getTotalPurchase());
                    valueMapped.put("payment", getTotalPayment());
                    valueMapped.put("cashReciept", getTotalCashRecieptEmployees());
                    print.showReportIncomeStatemenet(
                            messageSource.getMessage(lang.getSources(LangProperties.REPORT_FINANCIAL_STATEMENT), null,
                                    Locale.getDefault()),
                            valueMapped);
                } catch (Exception e) {
                    e.printStackTrace();
                    windows.errorPrint(lang.getSources(LangProperties.FINANCIAL_STATEMENT), e);
                }
            }
        });
        taskWorker.setOnFailed(event -> {
            action.setText(
                    messageSource.getMessage(lang.getSources(LangProperties.UNPROCESSED), null, Locale.getDefault()));
        });
    }

    private Double getIncomeStatement() {
        return getTotalSalesTurnover();
    }

    public Double getTotalSales() {
        Double result = 0.0;
        for (Sales sales : emptyListSales) {
            result += sales.getGrantTotal();
        }
        return result;
    }

    private Double getTotalSalesTurnover() {
        Double result = 0.0;
        for (Sales sales : emptyListSales) {
            result += sales.getAmmount();
        }
        return result;
    }

    private Double getTotalPurchase() {
        Double result = 0.0;
        for (PurchaseInvoice invoice : emptyListPurchases) {
            result += invoice.getAmmount();
        }
        return result;
    }

    private Double getTotalSalesDelivery() {
        Double result = 0.0;
        for (DeliveryOfSales invoice : emptyListTransports) {
            result += invoice.getGrantTotal();
        }
        return result;
    }

    private Double getTotalPayrollEmployees() {
        Double result = 0.0;
        for (PayrollAnEmployee invoice : emptyListPayrolls) {
            result += (invoice.getAmount() + invoice.getOtherAmount());
        }
        return result;
    }

    private Double getTotalPayment() {
        Double result = 0.0;
        for (PaymentInvoice invoice : emptyListOtherPayment) {
            result += invoice.getAmmount();
        }
        return result;
    }

    private Double getTotalCashRecieptEmployees() {
        Double result = 0.0;
        for (CashRecieptForEmployee invoice : emptyListCashEmployees) {
            result += (invoice.getAmount() - invoice.getPayment());
        }
        return result;
    }

    private Double getExpenditurStatement() {
        return getTotalPurchase() + getTotalSalesDelivery() + getTotalPayrollEmployees() + getTotalPayment()
                + getTotalCashRecieptEmployees();
    }

    @Autowired
    public void setWindows(DialogWindows windows) {
        this.windows = windows;
    }

    @Autowired
    public void setStage(SecondStageController stage) {
        this.stage = stage;
    }

    @Autowired
    public void setService(ServiceOfFinancialStatements service) {
        this.service = service;
    }

    @Autowired
    public void setLang(LangSource lang) {
        this.lang = lang;
    }

    @Autowired
    public void setPrint(PrintController print) {
        this.print = print;
    }

    @Autowired
    public void setNumberFormatter(NumberFormatter numberFormatter) {
        this.numberFormatter = numberFormatter;
    }

    @Autowired
    public void setHomeAction(HomeAction homeAction) {
        this.homeAction = homeAction;
    }

    private void clearListItems() {
        emptyListSales.clear();
        emptyListPurchases.clear();
        emptyListPayrolls.clear();
        emptyListOtherPayment.clear();
        emptyListTransports.clear();
        emptyListCashEmployees.clear();
    }

    private Task<Object> getWorker() {
        return new Task<Object>() {
            private final Integer INDICATOR_PROGRESSED = 10;
            private final Integer INDICATOR_SUCCESSED = 500;
            private Integer workDone;
            private Integer workMax;

            public void setWorkMax(Integer workMax) {
                this.workMax = workMax;
            }

            public void setWorkDone(Integer workDone) {
                this.workDone = workDone;
            }

            private void extractList(List emptyList, List items) throws Exception {
                setWorkMax(items.size());
                for (int i = 0; i < workMax; i++) {
                    setWorkDone(i);
                    updateProgress(workDone, workMax - 1);
                    updateMessage(messageSource.getMessage(lang.getSources(LangProperties.PROGRESS_GETTING_WITH_PARAMS),
                            new Object[]{workDone, workMax}, Locale.getDefault()));
                    emptyList.add(items.get(workDone));
                    Thread.sleep(INDICATOR_PROGRESSED);
                }
                succeeded();
            }

            @Override
            protected void succeeded() {
                try {
                    setWorkMax(100);
                    for (int i = 0; i < workMax; i++) {
                        setWorkDone(i);
                        updateProgress(workDone, workMax - 1);
                        updateMessage(
                                messageSource.getMessage(lang.getSources(LangProperties.PROGRESS_FINISHED_WITH_PARAM),
                                        new Object[]{workDone}, Locale.getDefault()));
                        Thread.sleep(5);
                    }
                    Thread.sleep(INDICATOR_SUCCESSED);
                    super.succeeded();
                } catch (InterruptedException e) {
                    super.cancel();
                    e.printStackTrace();
                }
            }

            private void salesWorker(LocalDate before, LocalDate after) throws Exception {
                List<Sales> salesItems = service.findSales(before, after);
                // log.info("Data Penjualan Ditemukan {} Data",
                // salesItems.size());

                extractList(emptyListSales, salesItems);
            }

            private void purchaseWorker(LocalDate before, LocalDate after) throws Exception {
                List<PurchaseInvoice> purchaseItems = service.findPurchase(before, after);
                // log.info("Data Pembelian Ditemukan {} Data",
                // purchaseItems.size());

                extractList(emptyListPurchases, purchaseItems);
            }

            private void payrollWorker(LocalDate before, LocalDate after) throws Exception {
                List<PayrollAnEmployee> payrollItems = service.findPayrollEmployeesForDivProduction(before, after);
                // log.info("Data Gaji Pegawai bag.Produksi Ditemukan {} Data",
                // payrollItems.size());

                extractList(emptyListPayrolls, payrollItems);
            }

            private void otherPaymentWorker(LocalDate before, LocalDate after) throws Exception {
                List<PaymentInvoice> paymentItems = service.findPayment(before, after);
                // log.info("Data Pembayaran Lain-lain Ditemukan {} Data",
                // paymentItems.size());

                extractList(emptyListOtherPayment, paymentItems);
            }

            private void transportWorker(LocalDate before, LocalDate after) throws Exception {
                List<DeliveryOfSales> deliveryItems = service.findTransportations(before, after);
                // log.info("Data Pengiriman Barang Ditemukan {} Data",
                // deliveryItems.size());

                extractList(emptyListTransports, deliveryItems);
            }

            private void cashRecieptWorker(LocalDate before, LocalDate after) throws Exception {
                List<CashRecieptForEmployee> cashItems = service.findCashRecieptForEmployee(before, after);
                // log.info("Data Kasbon Pegawai Ditemukan {} Data",
                // cashItems.size());

                extractList(emptyListCashEmployees, cashItems);
            }

            @Override
            protected Object call() throws Exception {
                LocalDate before = txtFirstDate.getValue();
                LocalDate after = txtLastDate.getValue();

				/* income */
                salesWorker(before, after);

				/* exmpenditur */
                purchaseWorker(before, after);
                transportWorker(before, after);
                payrollWorker(before, after);
                cashRecieptWorker(before, after);
                otherPaymentWorker(before, after);
                return null;
            }

        };
    }

}
