package dallastools.actions.reports;

import dallastools.controllers.FxInitializable;
import dallastools.controllers.NumberFormatter;
import dallastools.models.other.FinancialStatements;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by dimmaryanto on 28/11/15.
 */
public class FinancialStatementsDetailAction implements FxInitializable {

    @FXML
    private TableView<FinancialStatements> tableView;
    @FXML
    private TableColumn<FinancialStatements, String> columnId;
    @FXML
    private TableColumn<FinancialStatements, Double> columnDebit;
    @FXML
    private TableColumn<FinancialStatements, Double> columnCredit;
    @FXML
    private TableColumn<FinancialStatements, Double> columnTotal;
    @FXML
    private TextField txtTotal;
    private ApplicationContext springContext;
    private MessageSource messageSource;
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
        tableView.setFocusTraversable(false);
        tableView.setSelectionModel(null);
        columnId.setCellValueFactory(new PropertyValueFactory<FinancialStatements, String>("id"));
        columnDebit.setCellValueFactory(new PropertyValueFactory<FinancialStatements, Double>("debit"));
        columnDebit.setCellFactory(param -> new TableCell<FinancialStatements, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setAlignment(Pos.CENTER_RIGHT);
                if (empty) setText(null);
                else setText(numberFormatter.getCurrency(item));
            }
        });
        columnCredit.setCellValueFactory(new PropertyValueFactory<FinancialStatements, Double>("credit"));
        columnCredit.setCellFactory(param -> new TableCell<FinancialStatements, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setAlignment(Pos.CENTER_RIGHT);
                if (empty) setText(null);
                else setText(numberFormatter.getCurrency(item));
            }
        });
        columnTotal.setCellValueFactory(new PropertyValueFactory<FinancialStatements, Double>("total"));
        columnTotal.setCellFactory(param -> new TableCell<FinancialStatements, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setAlignment(Pos.CENTER_RIGHT);
                if (empty) setText(null);
                else setText(numberFormatter.getCurrency(item));
            }
        });
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void setData(ObservableList<FinancialStatements> statements) {
        tableView.setItems(statements);
        Double result = 0.0;
        for (FinancialStatements value : tableView.getItems()) {
            result += value.getTotal();
        }
        txtTotal.setText(numberFormatter.getCurrency(result));
    }

    @Autowired
    public void setNumberFormatter(NumberFormatter numberFormatter) {
        this.numberFormatter = numberFormatter;
    }
}
