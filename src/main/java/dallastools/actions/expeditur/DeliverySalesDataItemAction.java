package dallastools.actions.expeditur;

import dallastools.controllers.FxInitializable;
import dallastools.controllers.NumberFormatter;
import dallastools.controllers.notifications.DialogWindows;
import dallastools.controllers.notifications.LangProperties;
import dallastools.controllers.notifications.LangSource;
import dallastools.controllers.stages.SecondStageController;
import dallastools.models.income.Sales;
import dallastools.models.income.SalesDetails;
import dallastools.models.masterdata.Item;
import dallastools.models.masterdata.Unit;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by dimmaryanto on 26/11/15.
 */
public class DeliverySalesDataItemAction implements FxInitializable {
    @FXML
    private TextField txtDate;
    @FXML
    private TextField txtCustomer;
    @FXML
    private TextArea txtShip;
    @FXML
    private TableView<SalesDetails> tableView;
    @FXML
    private TableColumn<SalesDetails, String> columnItemName;
    @FXML
    private TableColumn<SalesDetails, String> columnItemUnit;
    @FXML
    private TableColumn<SalesDetails, Integer> columnQty;
    @FXML
    private TableColumn<SalesDetails, Double> columnPrice;
    @FXML
    private TableColumn<SalesDetails, Double> columnSubtotal;
    @FXML
    private TextField txtTotal;
    @FXML
    private TextField txtAmount;
    @FXML
    private TextField txtCashBack;

    private ApplicationContext springContext;
    private MessageSource messageSource;
    private DialogWindows windows;
    private LangSource lang;
    private NumberFormatter numberFormatter;

    @Override
    public void doClose() {
        SecondStageController controller = springContext.getBean(SecondStageController.class);
        controller.closeSecondStage();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.springContext = applicationContext;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tableView.setSelectionModel(null);

        columnItemName.setCellValueFactory(param -> {
            if (param != null) {
                Item anItem = param.getValue().getItem();
                if (anItem != null) {
                    return new SimpleStringProperty(anItem.getName());
                } else return new SimpleStringProperty();
            } else return null;
        });
        columnItemUnit.setCellValueFactory(param -> {
            if (param != null) {
                Item anItem = param.getValue().getItem();
                if (anItem != null) {
                    Unit anUnit = anItem.getUnit();
                    if (anUnit != null)
                        return new SimpleStringProperty(anUnit.getId());
                    else return new SimpleStringProperty();
                } else return null;
            } else return null;
        });
        columnItemUnit.setCellFactory(param -> new TableCell<SalesDetails, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setAlignment(Pos.CENTER);
                if (empty) setText("");
                else setText(item);
            }
        });
        columnQty.setCellValueFactory(new PropertyValueFactory<SalesDetails, Integer>("qty"));
        columnQty.setCellFactory(param -> new TableCell<SalesDetails, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setAlignment(Pos.CENTER);
                if (empty) setText(null);
                else setText(numberFormatter.getNumber(item));
            }
        });
        columnPrice.setCellValueFactory(new PropertyValueFactory<SalesDetails, Double>("priceSell"));
        columnPrice.setCellFactory(param -> new TableCell<SalesDetails, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                setAlignment(Pos.CENTER_RIGHT);
                super.updateItem(item, empty);
                if (empty) setText(null);
                else setText(numberFormatter.getCurrency(item));

            }
        });
        columnSubtotal.setCellValueFactory(param -> {
            if (param != null) {
                SalesDetails details = param.getValue();
                if (details != null) {
                    Double price = details.getPriceSell();
                    Integer qty = details.getQty();
                    return new SimpleObjectProperty<Double>(price * qty);
                } else return new SimpleObjectProperty<Double>(0.0);
            } else return null;
        });
        columnSubtotal.setCellFactory(param -> new TableCell<SalesDetails, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                setAlignment(Pos.CENTER_RIGHT);
                super.updateItem(item, empty);
                if (empty) setText(null);
                else setText(numberFormatter.getCurrency(item));

            }
        });
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void showData(Sales sales, List<SalesDetails> salesDetailses) {
        try {
            tableView.getItems().clear();
            txtDate.setText(sales.getDateTransaction().toString());
            txtCustomer.setText(sales.getCustomer().getCustomerName());
            txtShip.setText(sales.getShipTo());
            txtTotal.setText(numberFormatter.getCurrency(sales.getGrantTotal()));
            txtAmount.setText(numberFormatter.getCurrency(sales.getAmmount()));
            txtCashBack.setText(numberFormatter.getCurrency(sales.getGrantTotal() - sales.getAmmount()));
            for (SalesDetails value : salesDetailses) {
                tableView.getItems().add(value);
            }
        } catch (Exception e) {
            windows.errorLoading(lang.getSources(LangProperties.LIST_OF_SALES), e);
            e.printStackTrace();
        }
    }

    @Autowired
    public void setNumberFormatter(NumberFormatter numberFormatter) {
        this.numberFormatter = numberFormatter;
    }

    @Autowired
    public void setLang(LangSource lang) {
        this.lang = lang;
    }

    @Autowired
    public void setWindows(DialogWindows windows) {
        this.windows = windows;
    }
}
