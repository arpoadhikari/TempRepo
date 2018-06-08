package application.views;

import java.io.File;

import org.controlsfx.control.table.TableFilter;
import org.controlsfx.control.textfield.TextFields;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import application.Main;
import application.support.ObjectRepository;
import application.support.XML_Reader;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

public class MainViewController {

	@FXML
	private TableView<ObjectRepository> orTable;
	@FXML
	private TableColumn<ObjectRepository, String> objectName;
	@FXML
	private TableColumn<ObjectRepository, String> locatorType;
	@FXML
	private TableColumn<ObjectRepository, String> objectProperties;
	@FXML
	private Label statusText;
	@FXML
	private TextField applicationNameInput;
	@FXML
	private ComboBox<String> locatorTypeCombo;

	@SuppressWarnings("unchecked")
	@FXML
	void autoPopulate() {
		ObservableList data = FXCollections.observableArrayList();
        String[] s = new String[]{"apple","ball","cat","doll","elephant",
            "fight","georgeous","height","ice","jug",
             "aplogize","bank","call","done","ego",
             "finger","giant","hollow","internet","jumbo",
             "kilo","lion","for","length","primary","stage",
             "scene","zoo","jumble","auto","text",
            "root","box","items","hip-hop","himalaya","nepal",
            "kathmandu","kirtipur","everest","buddha","epic","hotel"};

            for(int j=0; j<s.length; j++){
                data.add(s[j]);
            }
		TextFields.bindAutoCompletion(applicationNameInput, data);
	}

	private ObservableList<ObjectRepository> masterData = FXCollections.observableArrayList();

	void loadDataFromXML() {
		XML_Reader xmlReader = new XML_Reader("D:\\Arpo\\OR_Manager\\OR.xml");

		String objectName;
		String locatorType;
		String objectProperties;

		NodeList nodeList_1 = xmlReader.getNodeListFromXPATH("//repo");
		NodeList nodeList_2 = xmlReader.getNodeListFromXPATH("//Object/*[1]");

		for (int i = 0; i < nodeList_1.getLength(); i++) {
			   Node nNode_1 = nodeList_1.item(i);
			   Node nNode_2 = nodeList_2.item(i);

			   objectName = nNode_1.getTextContent().trim();
			   locatorType = nNode_2.getNodeName().trim();
			   objectProperties = nNode_2.getTextContent().trim();

			   masterData.add(new ObjectRepository(objectName, locatorType, objectProperties));
		}
	}

	void setFilter() {
		TableFilter.forTableView(orTable).lazy(false).apply();
	}

	@FXML
    void initialize() {
		objectName.setCellValueFactory(new PropertyValueFactory<ObjectRepository, String>("objectName"));
		locatorType.setCellValueFactory(new PropertyValueFactory<ObjectRepository, String>("locatorType"));
		objectProperties.setCellValueFactory(new PropertyValueFactory<ObjectRepository, String>("objectProperties"));
		loadDataFromXML();
		orTable.setItems(masterData);
		setFilter();
		autoPopulate();
	}

	@FXML
	void openFile() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open GTAF Object Repository");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("GTAF OR file (*.xml)", "*.xml");
		fileChooser.getExtensionFilters().add(extFilter);
		File selectedFile = fileChooser.showOpenDialog(Main.primaryStage);
		if (selectedFile != null) {
			Main.primaryStage.setTitle(Main.appTitle+" - "+selectedFile.getAbsolutePath());
			statusText.setText("File loaded - "+selectedFile.getName());
		}
		else {
			statusText.setText("File selection canceled");
		}
	}

	@FXML
	void closeApp() {
		Platform.exit();
	}

}
