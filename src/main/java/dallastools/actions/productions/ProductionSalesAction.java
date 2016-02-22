package dallastools.actions.productions;

import dallastools.actions.HomeAction;
import dallastools.controllers.FxInitializable;
import dallastools.controllers.PrintController;
import dallastools.controllers.TableViewColumnAction;
import dallastools.controllers.notifications.DialogBalloon;
import dallastools.controllers.notifications.DialogWindows;
import dallastools.controllers.notifications.LangProperties;
import dallastools.controllers.notifications.LangSource;
import dallastools.models.masterdata.Item;
import dallastools.models.other.ItemSum;
import dallastools.models.productions.ProductionOfSales;
import dallastools.services.ServiceOfProduction;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import net.sf.jasperreports.engine.JRException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by dimmaryanto on 13/11/15.
 */
public class ProductionSalesAction implements FxInitializable {
	private final Logger log = LoggerFactory.getLogger(ProductionSalesAction.class);
	@FXML
	private TableColumn<ItemSum, Integer> columnItemQty;
	@FXML
	private TableColumn<ItemSum, Integer> columnStockNeeded;
	@FXML
	private TableColumn<ItemSum, Integer> columnStockPending;
	@FXML
	private TableView<ProductionOfSales> tableProduction;
	@FXML
	private TableColumn<ProductionOfSales, Integer> columnProductionId;
	@FXML
	private TableColumn<ProductionOfSales, Date> columnProductionDate;
	@FXML
	private TableColumn<ProductionOfSales, String> columnProductionAction;
	@FXML
	private TableView<ItemSum> tableView;
	@FXML
	private TableColumn<ItemSum, String> columnItemId;
	@FXML
	private TableColumn<ItemSum, String> columnItemName;
	private ApplicationContext springContext;
	private MessageSource messageSource;
	private ServiceOfProduction service;
	private DialogBalloon ballon;
	private DialogWindows windows;
	private HomeAction homeAction;
	private TableViewColumnAction actionColumn;
	private LangSource lang;
	private PrintController print;

	@Override
	public void doClose() {

	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.springContext = applicationContext;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		columnProductionId.setCellValueFactory(new PropertyValueFactory<ProductionOfSales, Integer>("id"));
		columnProductionId.setCellFactory(param -> new TableCell<ProductionOfSales, Integer>() {
			@Override
			protected void updateItem(Integer item, boolean empty) {
				super.updateItem(item, empty);
				setAlignment(Pos.CENTER);
				if (empty)
					setText("");
				else
					setText(item.toString());

			}
		});
		columnProductionDate.setCellValueFactory(new PropertyValueFactory<ProductionOfSales, Date>("date"));

		columnItemId.setCellValueFactory(param -> {
			if (param != null) {
				Item anItem = param.getValue().getItem();
				if (anItem != null) {
					return new SimpleStringProperty(anItem.getId());
				} else
					return new SimpleStringProperty();
			}
			return null;
		});

		columnItemName.setCellValueFactory(param -> {
			if (param != null) {
				Item anItem = param.getValue().getItem();
				if (anItem != null) {
					return new SimpleStringProperty(anItem.getName());
				} else
					return new SimpleStringProperty();
			} else
				return null;
		});
		columnItemQty.setCellValueFactory(param -> {
			if (param != null) {
				Item anItem = param.getValue().getItem();
				if (anItem != null) {
					return new SimpleObjectProperty<Integer>(anItem.getQty());
				} else
					return new SimpleObjectProperty<Integer>(0);
			} else
				return null;
		});
		columnItemQty.setCellFactory(param -> new TableCellRenderCenter());
		columnStockNeeded.setCellValueFactory(param -> {
			if (param != null) {
				return new SimpleObjectProperty<Integer>(param.getValue().getQty().intValue());
			} else
				return null;
		});
		columnStockNeeded.setCellFactory(param -> new TableCellRenderCenter());
		columnStockPending.setCellValueFactory(param -> {
			if (param != null) {
				Item anItem = param.getValue().getItem();
				if (anItem != null) {
					Integer qtyNeed = param.getValue().getQty().intValue();
					Integer qtyNow = anItem.getQty();
					return new SimpleObjectProperty<Integer>(qtyNow - qtyNeed);
				} else
					return new SimpleObjectProperty<Integer>(0);
			} else
				return null;
		});
		columnStockPending.setCellFactory(param -> new TableCellRenderCenter());

		columnProductionAction.setCellFactory(param -> new TableProductionColumnAction(tableProduction.getItems()));
	}

	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	private Task<Object> getWorker() {
		return new Task<Object>() {

			private final Integer INDICATOR_PROGRESSED = 50;
			private final Integer INDICATOR_SUCCESSED = 100;
			private Integer workDone;
			private Integer workMax;

			private void setWorkDone(Integer workDone) {
				this.workDone = workDone;
			}

			private void setWorkMax(Integer workMax) {
				this.workMax = workMax;
			}

			private void loadDataItemDeadline() throws Exception {
				tableView.getItems().clear();
				List<ItemSum> list = service.findItemDeadline();
				setWorkMax(list.size());
				for (int i = 0; i < workMax; i++) {
					setWorkDone(i);
					ItemSum anItem = list.get(workDone);
					updateMessage(messageSource.getMessage(lang.getSources(LangProperties.PROGRESS_GETTING_WITH_PARAMS),
							new Object[] { workDone, workMax }, Locale.getDefault()));
					updateProgress(workDone, workMax - 1);
					tableView.getItems().add(anItem);
					Thread.sleep(INDICATOR_PROGRESSED);
				}
			}

			private void loadDataProduction() throws Exception {
				tableProduction.getItems().clear();
				List<ProductionOfSales> list = service.findAllTransaction();
				setWorkMax(list.size());
				for (int i = 0; i < workMax; i++) {
					setWorkDone(i);
					ProductionOfSales production = list.get(workDone);
					updateMessage(messageSource.getMessage(lang.getSources(LangProperties.PROGRESS_GETTING_WITH_PARAMS),
							new Object[] { workDone, workMax }, Locale.getDefault()));
					updateProgress(workDone, workMax - 1);
					tableProduction.getItems().add(production);
					Thread.sleep(INDICATOR_PROGRESSED);
				}
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
										new Object[] { workDone }, Locale.getDefault()));
						Thread.sleep(10);
					}
					updateMessage(messageSource.getMessage(lang.getSources(LangProperties.PROGRESS_FINISHED_WITH_PARAM),
							new Object[] { workMax }, Locale.getDefault()));
					Thread.sleep(INDICATOR_SUCCESSED);
					super.succeeded();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			@Override
			protected Object call() throws Exception {
				loadDataProduction();
				loadDataItemDeadline();
				succeeded();
				return null;
			}
		};
	}

	public void loadData() {
		try {
			windows.loading(getWorker(), lang.getSources(LangProperties.LIST_OF_SALES_PRODUCTIONS));
		} catch (Exception e) {
			windows.errorLoading(lang.getSources(LangProperties.LIST_OF_SALES_PRODUCTIONS), e);
			e.printStackTrace();
		}
	}

	@FXML
	public void printed() {
		try {
			List<ItemSum> list = new ArrayList<>();
			for (ItemSum anItem : tableView.getItems()) {
				Long qtyNeed = anItem.getQty();
				Integer qtyAvailabel = anItem.getItem().getQty();
				if (qtyNeed.intValue() - qtyAvailabel >= 1) {
					list.add(anItem);
				}
			}
			print.showItemForProduction(
					messageSource.getMessage(lang.getSources(LangProperties.LIST_OF_ITEMS), null, Locale.getDefault()),
					list, homeAction.getAccount());
		} catch (JRException e) {
			windows.errorPrint(lang.getSources(LangProperties.LIST_OF_ITEMS), e);
			e.printStackTrace();
		}
	}

	@FXML
	public void newData() {
		ProductionSalesDataAction action = springContext.getBean(ProductionSalesDataAction.class);
		homeAction.updateContent();
		action.newData();
	}

	@Autowired
	public void setService(ServiceOfProduction service) {
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
	public void setHomeAction(HomeAction homeAction) {
		this.homeAction = homeAction;
	}

	@Autowired
	public void setActionColumn(TableViewColumnAction actionColumn) {
		this.actionColumn = actionColumn;
	}

	@Autowired
	public void setLang(LangSource lang) {
		this.lang = lang;
	}

	@FXML
	public void tableMasterClearSelection() {
		tableProduction.getSelectionModel().clearSelection();
	}

	@Autowired
	public void setPrint(PrintController print) {
		this.print = print;
	}

	public void tableRelationClearSelection() {
		tableView.getSelectionModel().clearSelection();
	}

	private class TableProductionColumnAction extends TableCell<ProductionOfSales, String> {

		private ObservableList<ProductionOfSales> list;

		public TableProductionColumnAction(ObservableList<ProductionOfSales> list) {
			this.list = list;
		}

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			if (empty)
				setGraphic(null);
			else {
				ProductionOfSales production = list.get(getIndex());
				setGraphic(actionColumn.getDefautlTableModel());
				actionColumn.getDeleteLink().setOnAction(event -> {
					try {
						if (windows
								.confirmDelete(lang.getSources(LangProperties.DATA_SALES_PRODUCTION),
										production.getDate(), lang.getSources(LangProperties.ID), production.getId())
								.get() == ButtonType.OK) {
							service.updateItemBeforeUpdateOrDelete(production, false);
							service.deleteTransaction(production);
							ballon.sucessedRemoved(lang.getSources(LangProperties.DATA_SALES_PRODUCTION));
							loadData();
						}
					} catch (Exception e) {
						windows.errorRemoved(lang.getSources(LangProperties.DATA_SALES_PRODUCTION),
								lang.getSources(LangProperties.ID), production.getId(), e);
						e.printStackTrace();
					}
				});

				actionColumn.getUpdateLink().setText(
						messageSource.getMessage(lang.getSources(LangProperties.VIEW), null, Locale.getDefault()));
				actionColumn.getUpdateLink().setGraphic(new FontAwesomeIconView(FontAwesomeIcon.FOLDER_ALTPEN_ALT));
				actionColumn.getUpdateLink().setOnAction(event -> {
					ProductionSalesDataAction action = springContext.getBean(ProductionSalesDataAction.class);
					homeAction.updateContent();
					action.readOnly(production);
				});
			}
		}
	}

	private class TableCellRenderCenter extends TableCell<ItemSum, Integer> {
		@Override
		protected void updateItem(Integer item, boolean empty) {
			super.updateItem(item, empty);
			setAlignment(Pos.CENTER);
			if (empty)
				setText("");
			else
				setText(item.toString());
		}
	}

}
