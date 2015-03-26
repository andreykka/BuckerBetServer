package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import server.Server;
import server.ServerService;

/**
 * Created by gandy on 23.09.14.
 *
 */
public class BukerBet extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
        Scene scene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());
        primaryStage.setTitle("BukerBet");
        primaryStage.setScene(scene);
        primaryStage.getScene().getStylesheets().add("/application/Main.css");
        primaryStage.show();

        primaryStage.setOnCloseRequest(event ->  ServerService.getInstance().stopServer());

        /* primaryStage.onCloseRequestProperty().setValue(event -> {
            String message = "Вы уверенны что хотите выйти??\r\nПосле етого сервер перестанет работать";
            if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(null, message)){
                System.out.println("try to stop server");
                Log.write("try to stop server");
                Server.getInstance().stopServer();
            }
        });*/
    }

    public static void main(String[] args) {
        launch(args);
    }
}
