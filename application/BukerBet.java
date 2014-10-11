package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import server.Server;

/**
 * Created by gandy on 23.09.14.
 */
public class BukerBet extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
        Scene scene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());
        primaryStage.setTitle("BukerBet");
        primaryStage.setScene(scene);
        primaryStage.getScene().getStylesheets().add("application/Main.css");
        primaryStage.show();
        primaryStage.setOnCloseRequest((WindowEvent) ->{
            System.out.println("try to stop server");
            Server.getInstance().stopServer();
        });

    }

    public static void main(String[] args) {
        launch(args);
    }
}