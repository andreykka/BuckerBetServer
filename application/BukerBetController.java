package application;

import database.Connector;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import listenner.ICustomerListener;
import org.apache.log4j.Logger;
import pojo.*;
import server.CustomerService;
import server.ServerService;

import javax.swing.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
 * Created by gandy on 23.09.14.
 *
 */
public class BukerBetController implements Initializable{

    private class InnerCustomerListenner implements ICustomerListener {

        private void sortColumn(){

        }

        @Override
        public void customerLogin(Customer cust) {
            for(Customer c: customerTableView.getItems()) {
                if (c.equals(cust)) {
                    c.setStatus(true);
                    customerTableView.getColumns().get(customerTableView.getColumns().size() - 1).setSortable(true);
                    return;
                }
            }
            cust.setStatus(true);
            customerTableView.getItems().add(cust);
        }

        @Override
        public void customerLogout(Customer cust) {
            for(Customer c: customerTableView.getItems()) {
                if (c.equals(cust)) {
                    c.setStatus(false);
                    customerTableView.getColumns().get(customerTableView.getColumns().size() - 1).setSortable(true);
                    return;
                }
            }
        }
    }

    @FXML private AnchorPane    mainPane;
    @FXML private AnchorPane    inputPane;
    @FXML private AnchorPane    customerPane;

    @FXML private TextArea      eventArea;
    @FXML private DatePicker    datePicker;
    @FXML private TextField     timeField;
    @FXML private TextArea      resultArea;

    @FXML private Button        saveBtn;
    @FXML private Button        cancelBtn;

    @FXML private TableView<OutputData>                 tableView;

    @FXML private TableColumn<OutputData, Integer>      idCol;
    @FXML private TableColumn<OutputData, String>       eventCol;
    @FXML private TableColumn<Object, LocalDate>        dateCol;
    @FXML private TableColumn<Object, LocalTime>        timeCol;
    @FXML private TableColumn<Object, String>           resultCol;

    @FXML private TableView<Customer>                 customerTableView;

    @FXML private TableColumn<Customer, Integer>      custIdCol;
    @FXML private TableColumn<Customer, String>       custSurnameCol;
    @FXML private TableColumn<Customer, String>       custNameCol;
    @FXML private TableColumn<Customer, String>       custLoginCol;
    @FXML private TableColumn<Customer, String>       custEmailCol;
    @FXML private TableColumn<Customer, String>       custTelCol;
    @FXML private TableColumn<Customer, Boolean>      custStatusCol;


    @FXML private Button addBut;
    @FXML private Button editBut;
    @FXML private Button deleteBut;

    @FXML private MenuBar       menuBar;
    @FXML private Menu          managementMenu;
    @FXML private Menu          mailingMenu;
    @FXML private MenuItem      menuItemaddMatch;   // add new match
    @FXML private MenuItem      menuItemSendInf;    // send information into clients

    private Logger logger = Logger.getLogger(getClass());

    private Connector           connector   = Connector.getInstance();
//    private Server              server      = Server.getInstance();
    private ServerService       serverService = ServerService.getInstance();
    private List<OutputData>    outputData  = new ArrayList<>();

    private Boolean isEdit = false;
    private int     editableId;


    private void refreshTableContent(){
        this.tableView.getItems().clear();
        this.tableView.getItems().addAll(this.connector.getData());
    }

    private void clearInputFields(){
        this.eventArea.clear();
        this.datePicker.getEditor().clear();
        this.timeField.clear();
        this.resultArea.clear();
    }

    private void changeDemonstration(){
        this.mainPane.setVisible(false);
        this.inputPane.setVisible(true);
        clearInputFields();
    }

    private void cancelBtnClick(){
        this.mainPane.setVisible(true);
        this.inputPane.setVisible(false);
        clearInputFields();
        refreshTableContent();
    }

    private Boolean isFilledData(){
        return !(     eventArea.getText().isEmpty()
                ||  datePicker.getEditor().getText().isEmpty()
                ||  timeField.getText().isEmpty()
                ||  resultArea.getText().isEmpty()) ;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // register listener in CustomerService class
        CustomerService.addListener(new InnerCustomerListenner());

        // start the server
        serverService.runServer();

        menuItemSendInf.setOnAction(action -> {
            // broadcast sending information
            serverService.sendBroadcastData();
        });

        this.custIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        this.custSurnameCol.setCellValueFactory(new PropertyValueFactory<>("surname"));
        this.custNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        this.custLoginCol.setCellValueFactory(new PropertyValueFactory<>("login"));
        this.custEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        this.custTelCol.setCellValueFactory(new PropertyValueFactory<>("tel"));
        this.custStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        // if status online paint text in green color else red
        this.custStatusCol.setCellFactory(param -> new TableCell<Customer, Boolean>(){
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setAlignment(Pos.CENTER);
                    if (item) {
                        setText("online");
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold");
                    } else {
                        setText("offline");
                        setStyle("-fx-text-fill: red;");
                    }
                }
            }
        });

        this.customerTableView.getItems().addAll(connector.getAllCustomers());

        this.tableView.setEditable(true);

        this.idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        this.eventCol.setCellValueFactory(new PropertyValueFactory<>("event"));
        this.dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        this.timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        this.resultCol.setCellValueFactory(new PropertyValueFactory<>("result"));

        this.dateCol.setCellFactory(param -> new ComboBoxTableCell<Object, LocalDate>(new DateConverter()));
        this.timeCol.setCellFactory(param -> new ComboBoxTableCell<Object, LocalTime>(new TimeConverter()));
        this.datePicker.setConverter(new DateConverter());

        refreshTableContent();

        this.addBut.setOnAction((ActionEvent) -> this.changeDemonstration());
        this.menuItemaddMatch.setOnAction((ActionEvent) -> this.changeDemonstration());
        this.cancelBtn.setOnAction((ActionEvent) -> this.cancelBtnClick());

        this.saveBtn.setOnAction((ActionEvent) -> {
            if (! this.isFilledData()){
                JOptionPane.showMessageDialog(null, "Необходимо заполнить все поля !!!");
                return;
            }
            OutputData data = new OutputData();
            data.setId(editableId);
            data.setEvent(this.eventArea.getText());
            data.setDate(this.datePicker.getValue());
            data.setTime(this.timeField.getText());
            data.setResult(this.resultArea.getText());
            if (isEdit){
                connector.updateItem(data);
            } else {
                connector.addItem(data);
            }
            this.refreshTableContent();
            this.isEdit = false;
            this.cancelBtnClick();
        });

        this.editBut.setOnAction((ActionEvent) -> {
            editableId = this.idCol.getCellData(this.tableView.getSelectionModel().getSelectedIndex());
            if (editableId < 0){
                return;
            }
            this.isEdit = true;
            this.changeDemonstration();
            OutputData editionData = connector.getItem(editableId);
            this.eventArea.setText(editionData.getEvent());
            this.datePicker.setValue(editionData.getDate());
            this.timeField.setText  (editionData.getTime().toString());
            this.resultArea.setText(editionData.getResult());
        });

        this.deleteBut.setOnAction((ActionEvent) -> {
            OutputData deletingData = this.tableView.getSelectionModel().getSelectedItem();
            this.connector.removeItem(deletingData);
            refreshTableContent();
        });

    }

}
