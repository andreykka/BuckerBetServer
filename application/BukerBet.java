package application;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import server.Server;
import server.ServerService;

import java.util.Optional;

/**
 * Created by gandy on 23.09.14.
 *
 */
public class BukerBet extends Application {

    public static Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
        Scene scene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());
        primaryStage.setTitle("BukerBet");
        primaryStage.setScene(scene);
        primaryStage.getScene().getStylesheets().add("/application/Main.css");
        stage = primaryStage;
        primaryStage.show();


        primaryStage.setOnCloseRequest(event ->  ServerService.getInstance().stopServer());
//
//        primaryStage.onCloseRequestProperty().setValue(new EventHandler<WindowEvent>() {
//        @Override
//        public void handle(WindowEvent event) {
//
//            String message = "Вы уверенны что хотите выйти??\r\nПосле етого сервер перестанет работать";
//            Alert alert = new Alert(Alert.AlertType.WARNING);
//            alert.setTitle("Внимание!!!");
//            alert.initModality(Modality.WINDOW_MODAL);
//            alert.initOwner(BukerBet.stage);
//            alert.setContentText(message);
//            alert.getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
//            Optional<ButtonType> buttonType = alert.showAndWait();
//
//            if (buttonType.get().equals(ButtonType.OK))
//                Server.getInstance().stopServer();
//        }});
    }

    public static void main(String[] args) {
        launch(args);
    }
}
