package application;

import database.Connector;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import org.apache.log4j.Logger;
import pojo.DateConverter;
import pojo.LogInData;
import pojo.OutputData;
import pojo.TimeConverter;
import server.Server;
import server.ServerService;

import javax.swing.*;
import javax.swing.text.html.*;
import java.net.URL;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.Timer;

/**
 * Created by gandy on 23.09.14.
 *
 */
public class BukerBetController implements Initializable{

    @FXML private AnchorPane    mainPane;
    @FXML private AnchorPane    inputPane;

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

    @FXML private Button addBut;
    @FXML private Button editBut;
    @FXML private Button deleteBut;

    @FXML private MenuBar       menuBar;
    @FXML private Menu          managementMenu;
    @FXML private Menu          mailingMenu;
    @FXML private MenuItem      menuItemaddMatch;   // add new match
    @FXML private MenuItem      menuItemSendInf;    // send information into clients


    @FXML private ListView<LogInData> customerListView;

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

        serverService.runServer();

        menuItemSendInf.setOnAction(action -> {

        });
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
