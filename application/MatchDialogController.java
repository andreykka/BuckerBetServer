package application;

import database.Connector;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import pojo.DateConverter;
import pojo.OutputData;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * Created by gandy on 20.04.15.
 *
 */
public class MatchDialogController implements Initializable {

    @FXML private TextArea      eventArea;
    @FXML private DatePicker    datePicker;
    @FXML private TextField     timeField;
    @FXML private TextArea      resultArea;
    @FXML private Button        saveBtn;
    @FXML private Button        cancelBtn;

    private Stage       dialogStage;
    private OutputData  matchData = null;
    private boolean     okClicked = false;
    private Pattern     timePattern = Pattern.compile("([0-1][0-9]|2[0-3]):[0-5][0-9]");


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.datePicker.setConverter(new DateConverter());

        this.datePicker.setDayCellFactory(val -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (item.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: rgba(176, 9, 41, 0.55);");
                }
            }
        });

        this.cancelBtn.setOnAction(act -> {
            matchData = null;
            okClicked = false;
            dialogStage.close();
        });

        this.saveBtn.setOnAction(act -> {
            if (!isInputValid())
                return;

            OutputData data = new OutputData();
            data.setEvent(this.eventArea.getText());
            data.setDate(this.datePicker.getValue());
            data.setTime(this.timeField.getText());
            data.setResult(this.resultArea.getText());

            if (matchData == null){ // adding mode
                Connector.getInstance().addItem(data);
            } else { // editing mode
                data.setId(this.matchData.getId());
                Connector.getInstance().updateItem(data);
            }
            this.matchData = null;
            this.okClicked = true;
            this.dialogStage.close();
        });

    }

    public void setMatchData(OutputData data) {
        if (data == null) {
            this.datePicker.setValue(LocalDate.now());
            return;
        }

        this.matchData = data;
        this.eventArea.setText(data.getEvent());
        this.datePicker.setValue(data.getDate());
        this.timeField.setText(data.getTime().toString());
        this.resultArea.setText(data.getResult());
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (eventArea.getText() == null || eventArea.getText().isEmpty())
            errorMessage += "Введите событие!\n";

        if (datePicker.getValue() == null || datePicker.getValue().isBefore(LocalDate.now()))
            errorMessage += "Введите коректную дату\n";

        if (timeField.getText() == null || timeField.getText().isEmpty())
            errorMessage += "Введите время\n";
        else if (!timePattern.matcher(timeField.getText()).matches())
            errorMessage += "Введите время в формате hh:mm\n";

        if (resultArea.getText() == null || resultArea.getText().isEmpty())
            errorMessage += "Введите прогноз!\n";

        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Show the error message.
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Введены неверные даные");
            alert.setHeaderText("Введите коректные даные");
            alert.setContentText(errorMessage);

            alert.show();
            return false;
        }
    }

}
