package dallastools.actions.productions;

import dallastools.actions.HomeAction;
import dallastools.controllers.FxInitializable;
import dallastools.controllers.notifications.*;
import dallastools.models.masterdata.CategoryOfItem;
import dallastools.models.masterdata.Item;
import dallastools.models.masterdata.Unit;
import dallastools.models.masterdata.Warehouse;
import dallastools.models.productions.ItemUsed;
import dallastools.models.productions.ItemUsedDetails;
import dallastools.services.ServiceOfItemUsed;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by dimmaryanto on 16/11/15.
 */
public class ItemUsedDataAction implements FxInitializable {
    private final Logger log = LoggerFactory.getLogger(ItemUsedDataAction.class);

    @FXML
    private DatePicker txtDate;
    @FXML
    private TableView<ItemUsedDetails> tableView;
    @FXML
    private TableColumn<ItemUsedDetails, String> columnID;
    @FXML
    private TableColumn<ItemUsedDetails, String> columnName;
    @FXML
    private TableColumn<ItemUsedDetails, String> columnCategory;
    @FXML
    private TableColumn<ItemUsedDetails, String> columnUnit;
    @FXML
    private TableColumn<ItemUsedDetails, String> columnWarehouse;
    @FXML
    private TableColumn<ItemUsedDetails, Integer> columnQty;
    @FXML
    private Button btnAction;

    private ApplicationContext springContext;
    private MessageSource messageSource;
    private DialogWindows windows;
    private DialogBalloon ballon;
    private ValidationSupport validator;
    private ItemUsed item;
    private ServiceOfItemUsed service;
    private ValidatorMessages validatorMessages;
    private LangSource lang;


    @Override
    public void doClose() {
        HomeAction action = springContext.getBean(HomeAction.class);
        action.showProductions();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.springContext = applicationContext;
    }

    private void initValidator() {

        this.validator = new ValidationSupport();
        this.validator.registerValidator(txtDate, (Control c, LocalDate date) ->
                ValidationResult.fromWarningIf(c, validatorMessages.validatorDateNotEqualsNow(),
                        !LocalDate.now().equals(date)));
        this.validator.invalidProperty().addListener((observable, oldValue, newValue) -> btnAction.setDisable(newValue));

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.txtDate.setValue(LocalDate.now());
        this.txtDate.setOpacity(0.9);

        tableView.setEditable(true);
        columnID.setCellValueFactory(param -> {
            if (param != null) {
                Item anItem = param.getValue().getItem();
                if (anItem != null) {
                    return new SimpleStringProperty(anItem.getId());
                } else return new SimpleStringProperty();
            } else return null;
        });
        columnName.setCellValueFactory(param -> {
            if (param != null) {
                Item anItem = param.getValue().getItem();
                if (anItem != null) {
                    return new SimpleStringProperty(anItem.getName());
                } else return new SimpleStringProperty();
            } else return null;
        });

        columnCategory.setCellValueFactory(param -> {
            if (param != null) {
                Item anItem = param.getValue().getItem();
                if (anItem != null) {
                    CategoryOfItem aCategory = anItem.getCategory();
                    if (aCategory != null) {
                        return new SimpleStringProperty(aCategory.getName());
                    } else
                        return new SimpleStringProperty();
                } else return null;
            } else return null;
        });

        columnUnit.setCellValueFactory(param -> {
            if (param != null) {
                Item anItem = param.getValue().getItem();
                if (anItem != null) {
                    Unit anUnit = anItem.getUnit();
                    if (anUnit != null) {
                        return new SimpleStringProperty(anUnit.getId());
                    } else
                        return new SimpleStringProperty();
                } else return null;
            } else return null;
        });

        columnWarehouse.setCellValueFactory(param -> {
            if (param != null) {
                Item anItem = param.getValue().getItem();
                if (anItem != null) {
                    Warehouse war = anItem.getWarehouse();
                    if (war != null) {
                        return new SimpleStringProperty(war.getName());
                    } else
                        return new SimpleStringProperty();
                } else return null;
            } else return null;
        });

        columnQty.setEditable(true);
        columnQty.setCellValueFactory(param -> {
            return new SimpleObjectProperty<Integer>(param.getValue().getQty());
        });
        columnQty.setCellFactory(param -> new TableColumnQTY(new StringConverter<Integer>() {
            @Override
            public String toString(Integer object) {
                return object.toString();
            }

            @Override
            public Integer fromString(String string) {
                return Integer.valueOf(string);
            }
        }));

    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @FXML
    public void doAction() {
        try {
            this.item.setDate(Date.valueOf(txtDate.getValue()));
            List<ItemUsedDetails> list = new ArrayList<>();
            for (ItemUsedDetails item : tableView.getItems()) {
                if (item.getQty() >= 1)
                    list.add(item);
            }
            if (list.size() >= 1) {
                service.save(item, list);
                ballon.sucessedSave(lang.getSources(LangProperties.DATA_ITEM_USED));
                doClose();
            } else {
                ballon.setTitle(messageSource.getMessage(lang.getSources(LangProperties.DATA_ITEM_USED), null, Locale.getDefault()));
                ballon.setMessage(messageSource.getMessage(lang.getSources(LangProperties.EMPTY_WITH_PARAM), new Object[]{
                        messageSource.getMessage(lang.getSources(LangProperties.LIST_OF_ITEMS), null, Locale.getDefault())
                }, Locale.getDefault()));
                ballon.showWarning();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Autowired
    public void setWindows(DialogWindows windows) {
        this.windows = windows;
    }

    @Autowired
    public void setBallon(DialogBalloon ballon) {
        this.ballon = ballon;
    }

    @Autowired
    public void setService(ServiceOfItemUsed service) {
        this.service = service;
    }

    @Autowired
    public void setValidatorMessages(ValidatorMessages validatorMessages) {
        this.validatorMessages = validatorMessages;
    }

    @Autowired
    public void setLang(LangSource lang) {
        this.lang = lang;
    }

    public void newData(ObservableList<Item> items) {
        initValidator();
        columnQty.setOnEditCommit(event -> {
            Integer value = event.getNewValue();
            if (value >= 0) {
                event.getTableView().getItems()
                        .get(event.getTablePosition().getRow()).setQty(event.getNewValue());
            } else {
                event.getTableView().getItems()
                        .get(event.getTablePosition().getRow()).setQty(event.getOldValue());
                ballon.setTitle(messageSource.getMessage(lang.getSources(LangProperties.DATA_ITEM_USED), null, Locale.getDefault()));
                ballon.setMessage(messageSource.getMessage(lang.getSources(LangProperties.MIN_WITH_PARAM), new Object[]{1}, Locale.getDefault()));
                ballon.showWarning();
            }
        });
        tableView.getItems().clear();
        columnQty.setEditable(true);
        this.item = new ItemUsed();
        for (Item anItem : items) {
            ItemUsedDetails detail = new ItemUsedDetails();
            detail.setItem(anItem);
            detail.setQty(0);
            tableView.getItems().add(detail);
        }
    }

    public void readOnly(ItemUsed value) {
        try {
            columnQty.setEditable(false);
            tableView.setSelectionModel(null);
            tableView.getItems().clear();

            txtDate.setValue(value.getDate().toLocalDate());
            txtDate.setDisable(true);
            btnAction.setVisible(false);
            columnQty.setEditable(false);

            this.item = value;
            windows.loading(tableView.getItems(), service.findItemPerTransaction(value), lang.getSources(LangProperties.LIST_OF_ITEMS));
        } catch (Exception e) {
            windows.errorLoading(lang.getSources(LangProperties.LIST_OF_ITEMS), e);
            e.printStackTrace();
        }

    }

    private class TableColumnQTY extends TextFieldTableCell<ItemUsedDetails, Integer> {
        public TableColumnQTY(StringConverter<Integer> converter) {
            super(converter);
            setAlignment(Pos.CENTER);
            setTextFill(Color.RED);
        }
    }
}
