package application;

import database.Connector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import listener.ICustomerListener;
import org.apache.log4j.Logger;
import pojo.*;
import server.CustomerService;
import server.ServerService;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Created by gandy on 23.09.14.
 *
 */
public class BukerBetController implements Initializable{

    private final double INPUT_PANE_WIDTH = 625.0;
    private final double MAIN_PANE_WIDTH = 995.0;

    private class InnerCustomerListener implements ICustomerListener {

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

    @FXML private TextArea      eventArea;
    @FXML private DatePicker    datePicker;
    @FXML private TextField     timeField;
    @FXML private TextArea      resultArea;

    @FXML private Button        saveBtn;
    @FXML private Button        cancelBtn;

    @FXML private TabPane tabPane;

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
    @FXML private Button btnActivate;
    @FXML private Button btnDeleteCust;

    @FXML private Button            btnClearFilter;
    @FXML private ComboBox<String>  filterComboBox;
    @FXML private TextField         filterTextField;

    @FXML private MenuBar       menuBar;
    @FXML private Menu          managementMenu;
    @FXML private Menu          mailingMenu;
    @FXML private MenuItem      menuItemaddMatch;   // add new match
    @FXML private MenuItem      menuItemSendInf;    // send information into clients

    private ObservableList<Customer> sourceCustomerItems;
    private FilteredList<Customer>  filteredCustomerList;

    private Logger logger = Logger.getLogger(getClass());

    private Connector           connector   = Connector.getInstance();
    private ServerService       serverService = ServerService.getInstance();

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

    private void showInputForm(){
        this.mainPane.setVisible(false);
//        clearInputFields();
        this.inputPane.setVisible(true);
    }

    private void cancelBtnClick(){
        this.inputPane.setVisible(false);
        clearInputFields();
        refreshTableContent();
        isEdit = false;
        this.mainPane.setVisible(true);
    }

    private Boolean isFilledData(){
        return !(   eventArea.getText().isEmpty()
                ||  datePicker.getEditor().getText().isEmpty()
                ||  timeField.getText().isEmpty()
                ||  resultArea.getText().isEmpty());
    }

    private void stopFiltering(){
        filterComboBox.getSelectionModel().select(-1);
        filterTextField.clear();
        btnClearFilter.setDisable(true);

        filteredCustomerList.setPredicate(p -> true);
        customerTableView.setItems(filteredCustomerList);
    }

    private void filterCustomers(){
        filteredCustomerList.setPredicate(pred -> {
            boolean result = false;

            switch (filterComboBox.getSelectionModel().getSelectedIndex()) {
                // Surname select
                case 0: {
                    result = pred.getSurname().toLowerCase().contains(filterTextField.getText().toLowerCase());
                    break;
                }
                // Name select
                case 1: {
                    result = pred.getName().toLowerCase().contains(filterTextField.getText().toLowerCase());
                    break;
                }
                // Login select
                case 2: {
                    result = pred.getLogin().toLowerCase().contains(filterTextField.getText().toLowerCase());
                    break;
                }
                // Email select
                case 3: {
                    result = pred.getEmail().toLowerCase().contains(filterTextField.getText().toLowerCase());
                    break;
                }
                default:
                    result = true;
            }
            return result;
        });
        customerTableView.setItems(filteredCustomerList);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        filterComboBox.getItems().addAll("Фамилия", "Имя",  "Логин", "Email");

        // register listener in CustomerService class
        CustomerService.addListener(new InnerCustomerListener());

        // start the server
        serverService.runServer();

        menuItemSendInf.setOnAction(action -> {
            // broadcast sending information
            serverService.sendBroadcastData();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Информационное сообщение");
            alert.initModality(Modality.WINDOW_MODAL);
            alert.initOwner(BukerBet.stage);
            alert.setHeaderText(null);
            alert.setContentText("Даные успешно отправлены");
            alert.show();
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
                } else {
                    setText("");
                    setStyle("");
                }
            }
        });

        sourceCustomerItems = FXCollections.observableArrayList(connector.getAllCustomers());
        this.customerTableView.getItems().addAll(sourceCustomerItems);
        this.filteredCustomerList = new FilteredList<>(customerTableView.getItems(), p -> true);

        this.tableView.setEditable(true);

        this.idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        this.eventCol.setCellValueFactory(new PropertyValueFactory<>("event"));
        this.dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        this.timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        this.resultCol.setCellValueFactory(new PropertyValueFactory<>("result"));

        this.dateCol.setCellFactory(param -> new ComboBoxTableCell<>(new DateConverter()));
        this.timeCol.setCellFactory(param -> new ComboBoxTableCell<>(new TimeConverter()));
        this.datePicker.setConverter(new DateConverter());

        refreshTableContent();

        this.addBut.setOnAction((ActionEvent) -> {
            isEdit = false;
            this.datePicker.setValue(LocalDate.now());
            this.showInputForm();
        });
        this.menuItemaddMatch.setOnAction((ActionEvent) -> {
            isEdit = false;
            this.showInputForm();
        });
        this.cancelBtn.setOnAction((ActionEvent) -> this.cancelBtnClick());

        this.saveBtn.setOnAction((ActionEvent) -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Информационное сообщение");
            alert.initModality(Modality.WINDOW_MODAL);
            alert.initOwner(BukerBet.stage);
            alert.setHeaderText(null);

            if (! this.isFilledData()){
                alert.setContentText("Необходимо заполнить все поля даными !!!");
                alert.show();
                isEdit = false;
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
                alert.setContentText("Даные успешно отредактированы");
                isEdit = false;
            } else {
                connector.addItem(data);
                alert.setContentText("Даные успешно доданы");
            }
            this.refreshTableContent();
            this.isEdit = false;
            this.cancelBtnClick();
            alert.show();
        });

        this.editBut.setOnAction((ActionEvent) -> {
            if (this.tableView.getSelectionModel().getSelectedIndex() < 0 )
                return;

            editableId = this.idCol.getCellData(this.tableView.getSelectionModel().getSelectedIndex());
            if (editableId < 0){
                return;
            }
            this.isEdit = true;
            this.showInputForm();
            OutputData editionData = connector.getItem(editableId);
            this.eventArea.setText(editionData.getEvent());
            this.datePicker.setValue(editionData.getDate());
            this.timeField.setText(editionData.getTime().toString());
            this.resultArea.setText(editionData.getResult());
        });

        this.deleteBut.setOnAction((ActionEvent) -> {

            OutputData deletingData = this.tableView.getSelectionModel().getSelectedItem();

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Внимание!!!");
            alert.initModality(Modality.WINDOW_MODAL);
            alert.initOwner(BukerBet.stage);
            alert.setHeaderText("Вы уверенны что хотите удалить ");
            alert.setContentText("Событие:    " + deletingData.getEvent() + "\r\n" +
                    "Дата/Время: " + deletingData.getDate() + "/" + deletingData.getTime() + "\r\n" +
                    "Результат:  " + deletingData.getResult());

            ObservableList<ButtonType> buttonTypes = alert.getButtonTypes();
            alert.getButtonTypes().clear();
            alert.getButtonTypes().addAll(new ButtonType("Нет", ButtonBar.ButtonData.NO),
                    new ButtonType("Да", ButtonBar.ButtonData.YES));

            Optional<ButtonType> buttonType = alert.showAndWait();

            alert.getButtonTypes().clear();
            alert.getButtonTypes().addAll(buttonTypes);

            if (buttonType.get().getButtonData().isDefaultButton()){
                this.connector.removeItem(deletingData);
                this.refreshTableContent();
            }

        });

        this.tabPane.setOnMouseClicked((event) -> {
            // if check tab Customers -> show activation Btn
            if (tabPane.getSelectionModel().getSelectedIndex() == 1) {
                btnActivate.setVisible(true);
                btnDeleteCust.setVisible(true);
                btnClearFilter.setVisible(true);
                filterComboBox.setVisible(true);
                filterTextField.setVisible(true);
            } else {
                btnActivate.setVisible(false);
                btnDeleteCust.setVisible(false);
                btnClearFilter.setVisible(false);
                filterComboBox.setVisible(false);
                filterTextField.setVisible(false);
            }
        });

        btnClearFilter.setOnAction(action -> this.stopFiltering());

        filterComboBox.setOnAction(act ->{
            if (filterComboBox.getSelectionModel().getSelectedIndex() < 0){
                filterTextField.clear();
                return;
            }
            if (filterTextField.getText().isEmpty())
                return;

            filterCustomers();
        });

        filterTextField.setOnKeyReleased(keyEvent -> {
            if (filterTextField.getText().isEmpty()) {
                filteredCustomerList.setPredicate(p -> true);
                customerTableView.setItems(filteredCustomerList);
                return;
            } else
                btnClearFilter.setDisable(false);

            // set predicate and filter customers
            filterCustomers();
        });


        btnDeleteCust.setOnAction(event -> {
            int selectedIndex = customerTableView.getSelectionModel().getSelectedIndex();

            if (selectedIndex < 0)
                return;

            Customer customer = customerTableView.getSelectionModel().getSelectedItem();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Внимание!!!");
            alert.initModality(Modality.WINDOW_MODAL);
            alert.initOwner(BukerBet.stage);
            alert.setHeaderText("Вы уверенны что хотите удалить \r\nпользователя с системы?");
            String out = String.format("Пользователь: %s %s\r\n\t\t\t %s", customer.getSurname(), customer.getName(), customer.getEmail());
            alert.setContentText(out);

            Optional<ButtonType> buttonType = alert.showAndWait();

            Connector.getInstance().deleteCustomer(customer);

            if (buttonType.get().equals(ButtonType.OK)){
                sourceCustomerItems.remove(customer);
                Predicate<? super Customer> predicate = filteredCustomerList.getPredicate();
                filteredCustomerList = new FilteredList<>(sourceCustomerItems, predicate);
                customerTableView.setItems(filteredCustomerList);
            }
        });

        btnActivate.setOnAction((event) -> {
            int selectedIndex = customerTableView.getSelectionModel().getSelectedIndex();

            if (selectedIndex < 0)
                return;

            int daysActivation = 7;
            Customer customer = customerTableView.getSelectionModel().getSelectedItem();

            Pattern p = Pattern.compile("[0-9]++");
            TextInputDialog inputDialog = new TextInputDialog("7");
            inputDialog.setTitle("Окно ввода значения");
            inputDialog.setHeaderText("Ввод периода активация от текущего дня");
            inputDialog.setContentText("Введите значение периода активации");
            inputDialog.initModality(Modality.APPLICATION_MODAL);
            inputDialog.initOwner(BukerBet.stage);

            inputDialog.getEditor().setOnKeyReleased(keyEvent -> {
                TextField field  = null;
                if (keyEvent.getSource() instanceof  TextField ){
                    field = (TextField) keyEvent.getSource();
                } else
                    throw new Error("Event not a 'TextField type' FIX IT please!!");

                while (field.getText().length() > 0) {
                    if (p.matcher(field.getText()).matches())
                        break;
                    field.setText(field.getText().substring(0, field.getText().length() - 1));
                    field.positionCaret(field.getLength());
                }
            });

            Optional<String> optional = inputDialog.showAndWait();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Информационное сообщение");
            alert.initModality(Modality.WINDOW_MODAL);
            alert.initOwner(BukerBet.stage);
            alert.setHeaderText(null);

            if (optional.isPresent()) {
                try {
                    daysActivation = Integer.parseInt(optional.get());
                } catch (Exception e){
                    alert.setContentText("Введено неверные даные.\r\nВозможно вы не ввели ничего");
                    alert.show();
                    return;
                }
                Connector.getInstance().activateCustomer(customer, daysActivation); // activation
                alert.setContentText("Пользователю: " + customer.getSurname() + " "
                                    + customer.getName() + "\r\nактивировано лицензию на "
                                    + daysActivation + " (дней)");

            }  else {
                alert.setContentText("активация не произведена");
            }
            alert.showAndWait();

        });

    }

}
