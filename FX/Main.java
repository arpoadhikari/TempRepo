package application;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import application.views.DialogViewController;
import application.views.MainViewController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;


public class Main extends Application {

	public static Stage primaryStage;
	public static Stage mergerStage;
	public static String appTitle = "GTAF Object Repository Manager";

	@Override
	public void start(Stage stage) {
		primaryStage = stage;
		primaryStage.setTitle(appTitle);

		mergerStage = new Stage();
		mergerStage.setTitle("Object Repository Merger");

		try {

			//Initializes the main window
			FXMLLoader primaryLoader = new FXMLLoader(Main.class.getResource("views/MainView.fxml"));
			VBox primaryPage = primaryLoader.load();
			Scene primaryScene = new Scene(primaryPage);
			primaryStage.setScene(primaryScene);
			primaryStage.setMinHeight(650);
			primaryStage.setMinWidth(1000);
			primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("icons/appIcon.png")));

			//Initializes the merger window
			FXMLLoader mergerLoader = new FXMLLoader(Main.class.getResource("views/MergerView.fxml"));
			AnchorPane mergerPage = mergerLoader.load();
			Scene mergerScene = new Scene(mergerPage);
			mergerStage.setScene(mergerScene);
			mergerStage.setMinHeight(650);
			mergerStage.setMinWidth(1000);
			mergerStage.getIcons().add(new Image(Main.class.getResourceAsStream("icons/merge.png")));
			mergerStage.initModality(Modality.WINDOW_MODAL);
			mergerStage.initOwner(Main.primaryStage);

			primaryStage.show();

			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {
					Optional<ButtonType> option = DialogViewController.showConfirmation("Close Application", "Confirm Exit", "Are you sure you want to exit ?");
					if (option.get() != ButtonType.OK) {
						we.consume();
					}
					else {
						primaryStage.close();
					}
				}
			});

			mergerStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {
					Optional<ButtonType> option = DialogViewController.showConfirmation("Close Application", "Confirm Exit", "Are you sure you want to exit ?");
					if (option.get() != ButtonType.OK) {
						we.consume();
					}
					else {
						mergerStage.close();
					}
				}
			});

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		if (args.length == 1) {
			File file = new File(args[0]);
			if(file.canRead()) {
				MainViewController.xmlPath = file.getAbsolutePath();
			}
		}
		launch(args);
	}
}
