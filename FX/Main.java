package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;


public class Main extends Application {

	public static Stage primaryStage;
	public static String appTitle = "GTAF Object Repository Manager";

	@Override
    public void start(Stage stage) {
		primaryStage = stage;
        primaryStage.setTitle(appTitle);

        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/MainView.fxml"));
            VBox page = loader.load();
            Scene scene = new Scene(page);
            primaryStage.setScene(scene);
            primaryStage.setMinHeight(600);
            primaryStage.setMinWidth(1000);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
