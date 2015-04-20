package test;

import application.BukerBet;
import application.BukerBetController;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;


/**
 * Created by gandy on 16.04.15.
 *
 */
public class TestDialogs {
    @SuppressWarnings("deprecated")
    public static void main(String[] args) {

        Alert al = new Alert(Alert.AlertType.INFORMATION);
        al.setTitle("TiTle alertDialog");
        al.initModality(Modality.WINDOW_MODAL);
        al.initOwner(BukerBet.stage);
        al.setHeaderText("HEader Text");
        al.setContentText("Content text");
        al.showAndWait();
    }
}
