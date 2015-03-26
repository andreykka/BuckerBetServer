package alertModule;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

/*
 * Created by gandy on 23.03.15.
 */

public class FXOptionPane extends Application {

    private static final String ID_TITLE    = "#messageLabel";
    private static final String ID_TEXT     = "#detailsLabel";
    private static final String ID_BTN      = "#okButton";

    private static String  titleText;
    private static String  msgText;
    private static Window  owner;

    public static void showMessageDialog(Window parent, String title, String msg){
        titleText = title;
        msgText = msg;
        owner = parent;
        launch(new String[]{});
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("start method");
        primaryStage.setTitle("Dialog");
        initRootLayout(primaryStage);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.show();
    }

    // Initialize the root pane and load the css style
    private void initRootLayout(Stage primaryStage){
        try {
            System.out.println("Init root Layout");
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("AlertDialog_css/AlertDialog.fxml"));
            GridPane rootPane = (GridPane) loader.load();
            Scene scene = new Scene(rootPane);
            //scene.getStylesheets().add("AlertDialog_css/AlertDialog.css");
            primaryStage.setScene(scene);

            initNodes(primaryStage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initNodes(Stage primaryStage){
        System.out.println("Init Nodes");
        ((Label) primaryStage.getScene().lookup(ID_TITLE)).setText(titleText);
        ((Label) primaryStage.getScene().lookup(ID_TEXT)).setText(msgText);

        ((Button) primaryStage.getScene().lookup(ID_BTN)).setOnAction(act -> primaryStage.hide());
    }
}
