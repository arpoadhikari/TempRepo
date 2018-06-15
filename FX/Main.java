package application;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import application.views.DialogViewController;
import application.views.MainViewController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
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
			//setUserAgentStylesheet(STYLESHEET_CASPIAN);
			primaryStage.setScene(scene);
			primaryStage.setMinHeight(650);
			primaryStage.setMinWidth(1000);
			primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("icons/appIcon.png")));
			primaryStage.show();

			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {
					Optional<ButtonType> option = DialogViewController.showConfirmation("Confirmation", "Confirm Exit", "Are you sure you want to exit ?");
					if (option.get() != ButtonType.OK) {
						we.consume();
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
