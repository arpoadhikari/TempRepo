package application.views;

import application.support.ObjectProperty;
import application.support.ObjectRepositoryData;
import application.support.Page;
import application.support.XML_Reader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.controlsfx.control.Notifications;
import org.controlsfx.control.table.TableFilter;
import org.controlsfx.control.table.TableRowExpanderColumn;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import application.Main;
import application.support.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

public class MergerViewController {


	String xmlPath2 = "";

	private List<Application> appList = new ArrayList<>();

	@FXML
	private Label header1;
	@FXML
	private Label header2;
	@FXML
	private Accordion accordion;
	@FXML
	private TitledPane duplicateObjectPane;
	@FXML
	private TitledPane uniqueObjectPane;
	@FXML
	private Button merge;
	@FXML
	private Button importFile;
	@FXML
	private Button delete;
	@FXML
	private Button move;
	@FXML
	private TableView<ObjectRepositoryData> orMergerTable;
	@FXML
	private TableView<ObjectRepositoryData> orDuplicateTable;
	@FXML
	private TableColumn<ObjectRepositoryData, String> objectName_Merged;
	@FXML
	private TableColumn<ObjectRepositoryData, String> locatorType_Merged;
	@FXML
	private TableColumn<ObjectRepositoryData, String> objectProperties_Merged;
	@FXML
	private TableColumn<ObjectRepositoryData, String> objectName_Duplicate;
	@FXML
	private TableColumn<ObjectRepositoryData, String> locatorType_Duplicate;
	@FXML
	private TableColumn<ObjectRepositoryData, String> objectProperties_Duplicate;

	private ObservableList<ObjectRepositoryData> xmlData1 = FXCollections.observableArrayList();
	private ObservableList<ObjectRepositoryData> xmlData2 = FXCollections.observableArrayList();

	private ObservableList<ObjectRepositoryData> mergedData = FXCollections.observableArrayList();
	private ObservableList<ObjectRepositoryData> duplicateData = FXCollections.observableArrayList();

	@FXML
	void initialize() {

		//Sets Table View Properties for Unique Objects Table
		objectName_Merged.setCellValueFactory(new PropertyValueFactory<ObjectRepositoryData, String>("objectName"));
		locatorType_Merged.setCellValueFactory(new PropertyValueFactory<ObjectRepositoryData, String>("locatorType"));
		objectProperties_Merged.setCellValueFactory(new PropertyValueFactory<ObjectRepositoryData, String>("objectProperties"));

		//Sets Table View Properties for Duplicate Objects Table
		objectName_Duplicate.setCellValueFactory(new PropertyValueFactory<ObjectRepositoryData, String>("objectName"));
		locatorType_Duplicate.setCellValueFactory(new PropertyValueFactory<ObjectRepositoryData, String>("locatorType"));
		objectProperties_Duplicate.setCellValueFactory(new PropertyValueFactory<ObjectRepositoryData, String>("objectProperties"));

		//Expand the Unique Objects title pane
		accordion.setExpandedPane(uniqueObjectPane);

		//Initialize the row expander for duplicate objects table
		TableRowExpanderColumn<ObjectRepositoryData> expander = new TableRowExpanderColumn<>(this::createEditor);
		orDuplicateTable.getColumns().add(0, expander);
		setTableFilter();

		//Listener to automatically get the object count from Unique Objects table and to display it
		mergedData.addListener(new ListChangeListener<ObjectRepositoryData>() {
			@Override
			public void onChanged(ListChangeListener.Change<? extends ObjectRepositoryData> c) {
				while(c.next()){
					if(c.wasAdded() || c.wasRemoved() || c.wasReplaced() | c.wasUpdated()){
						uniqueObjectPane.setText("Unique Objects ("+mergedData.size()+")");
					}
				}

			}
		});

		//Listener to automatically get the object count from Duplicate Objects table and to display it
		duplicateData.addListener(new ListChangeListener<ObjectRepositoryData>() {
			@Override
			public void onChanged(ListChangeListener.Change<? extends ObjectRepositoryData> c) {
				while(c.next()){
					if(c.wasAdded() || c.wasRemoved() || c.wasReplaced() | c.wasUpdated()){
						duplicateObjectPane.setText("Duplicate Objects ("+duplicateData.size()+")");
					}
				}

			}
		});

	}

	//Initialize the UI for row expander
	private GridPane createEditor(TableRowExpanderColumn.TableRowDataFeatures<ObjectRepositoryData> param) {

		GridPane editor = new GridPane();
		editor.setPadding(new Insets(10));
		editor.setHgap(10);
		editor.setVgap(5);

		TextField appNameInput = new TextField(param.getValue().getObjectName().split("\\.")[0]);
		appNameInput.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue,
					String newValue) {
				if (newValue.split("\\.").length > 0 || newValue.contains(".")) {
					appNameInput.setText(newValue.replaceAll("[.]", ""));
				}
			}
		});

		TextField pageNameInput = new TextField(param.getValue().getObjectName().split("\\.")[1]);
		pageNameInput.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue,
					String newValue) {
				if (newValue.split("\\.").length > 0 || newValue.contains(".")) {
					pageNameInput.setText(newValue.replaceAll("[.]", ""));
				}
			}
		});

		TextField objectNameInput = new TextField(param.getValue().getObjectName().split("\\.")[2]);
		objectNameInput.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue,
					String newValue) {
				if (newValue.split("\\.").length > 0 || newValue.contains(".")) {
					objectNameInput.setText(newValue.replaceAll("[.]", ""));
				}
			}
		});

		ComboBox<String> locatorTypeCombo = new ComboBox<String>(FXCollections.observableArrayList( "id", "name", "cssSelector", "linkText", "partialLinkText", "tagName", "xpath"));
		locatorTypeCombo.getSelectionModel().select(param.getValue().getLocatorType());
		TextField objectPropertyInput = new TextField(param.getValue().getObjectProperties());

		Button save = new Button("Save");
		save.setPrefWidth(75);
		save.setOnAction(event -> {
			String objectName = appNameInput.getText().trim()+"."
					+pageNameInput.getText().trim()+"."
					+objectNameInput.getText().trim();
			String locatorType = locatorTypeCombo.getSelectionModel().getSelectedItem();
			String objectProperty = objectPropertyInput.getText().trim();

			if (appNameInput.getText().trim().isEmpty() ||
					pageNameInput.getText().trim().isEmpty() ||
					objectNameInput.getText().trim().isEmpty() ||
					locatorTypeCombo.getSelectionModel().isEmpty()) {

				DialogViewController.showAlert("Add Object", "Application Name / Page Name / Object Name can't be blank !", "Please provide correct input.");
			}
			else {
				param.getValue().setObjectName(objectName);
				param.getValue().setLocatorType(locatorType);
				param.getValue().setObjectProperties(objectProperty);
			}
			setTableFilter();
			param.toggleExpanded();
		});

		Button cancel = new Button("Cancel");
		cancel.setPrefWidth(75);
		cancel.setOnAction(event -> param.toggleExpanded());

		editor.addRow(0, new Label("Applicaton Name :"), new Label("Page Name :"), new Label("Object Name :"), new Label("Locator Type :"), new Label("Object Properties :"));
		editor.addRow(1, appNameInput, pageNameInput, objectNameInput, locatorTypeCombo, objectPropertyInput);
		editor.addRow(2, save, cancel);

		setTableFilter();

		return editor;
	}

	/**
	 * Load data from the XML to display the content in the table
	 */
	void loadDataFromXML(String xmlPath, ObservableList<ObjectRepositoryData> xmlData) {

		File file = new File(xmlPath);
		if (file != null && file.canRead()) {
			try {
				XML_Reader xmlReader = new XML_Reader(xmlPath);

				String objectName;
				String locatorType;
				String objectProperties;

				NodeList nodeList = xmlReader.getNodeListFromXPATH("//Object");

				for (int i = 0; i < nodeList.getLength(); i++) {

					Node node = nodeList.item(i);
					org.w3c.dom.Element element = (org.w3c.dom.Element) node;

					objectName = nodeList.item(i).getTextContent();
					locatorType = element.getAttribute("locatorType");
					objectProperties = element.getAttribute("property");

					xmlData.add(new ObjectRepositoryData(objectName, locatorType, objectProperties));
				}
			} catch (Exception e) {
				System.out.println("ERROR! --- Unable to load data from XML : "+xmlPath+"\n"+e);
				DialogViewController.showExceptionDialog("ERROR! --- Unable to load data from XML : "+xmlPath, e);
			}
		}
	}

	/**
	 * Import the OR data and populate in UI
	 */
	@FXML
	void importData() {

		xmlData1.clear();
		xmlData2.clear();
		mergedData.clear();
		duplicateData.clear();
		orMergerTable.refresh();
		orDuplicateTable.refresh();

		xmlData1.addAll(MainViewController.masterData);
		header1.setText("File 1 selected - "+MainViewController.xmlPath);

		openFile();
		loadDataFromXML(xmlPath2, xmlData2);

		for (int i = 0; i < xmlData1.size(); i++) {

			String objectName = xmlData1.get(i).getObjectName();
			String locatorType = xmlData1.get(i).getLocatorType();
			String objectProperty = xmlData1.get(i).getObjectProperties();

			mergedData.add(new ObjectRepositoryData(objectName, locatorType, objectProperty));
		}

		int defaultMergedDataSize = mergedData.size();
		int defaultXmlData2Size = xmlData2.size();

		for (int i = 0; i < defaultXmlData2Size; i++) {

			String xml2ObjectName = xmlData2.get(i).getObjectName();
			String xml2LocatorType = xmlData2.get(i).getLocatorType();
			String xml2ObjectProperty = xmlData2.get(i).getObjectProperties();

			boolean isFound = false;

			String mergedObjectName;
			for (int j = 0; j < defaultMergedDataSize; j++) {

				mergedObjectName = mergedData.get(j).getObjectName();

				if(xml2ObjectName.equalsIgnoreCase(mergedObjectName)) {
					isFound = true;
					break;
				}

			}
			if (!isFound) {
				mergedData.add(new ObjectRepositoryData( xml2ObjectName, xml2LocatorType, xml2ObjectProperty));
			}
			else {
				duplicateData.add(new ObjectRepositoryData( xml2ObjectName, xml2LocatorType, xml2ObjectProperty));
			}
		}

		orMergerTable.setItems(mergedData);
		orDuplicateTable.setItems(duplicateData);
		setTableFilter();
		if(duplicateData.size() > 0) {
			accordion.setExpandedPane(duplicateObjectPane);
		}
	}

	/**
	 * Set Table Filter
	 */
	void setTableFilter() {
		orMergerTable.setItems(mergedData);
		orDuplicateTable.setItems(duplicateData);
		TableFilter.forTableView(orMergerTable).lazy(false).apply();
		TableFilter.forTableView(orDuplicateTable).lazy(false).apply();
	}

	/**
	 * Merge the into master data
	 */
	@FXML
	void merge() {

		Optional<ButtonType> option = DialogViewController.showConfirmation("Merge Objects", "Please confirm.", "Are you sure you want to merge objects ?");
		if (option.get() == ButtonType.OK) {
			MainViewController.masterData.clear();
			MainViewController.masterData.addAll(mergedData);
			Notifications.create().title("Merge Objects")
			.text(mergedData.size()+" Objects are merged.")
			.position(Pos.BOTTOM_RIGHT).showInformation();
			DialogViewController.showAlert("Merge Objects", mergedData.size()+" Objects are merged.", "Please close this window and save the data from Main Window.");
		}

	}

	/**
	 * Creates a list of "Application" class object from the Object Repository table
	 */
	void getTableData() {

		int nRow = orMergerTable.getItems().size();
		String app;
		String page;
		String locator;
		String objectName;
		String objectProperty;
		appList = new ArrayList<>();
		for (int rowIndex = 0; rowIndex < nRow; rowIndex++) {

			app = orMergerTable.getItems().get(rowIndex).getObjectName().split("\\.")[0];
			page = orMergerTable.getItems().get(rowIndex).getObjectName().split("\\.")[1];
			objectName = orMergerTable.getItems().get(rowIndex).getObjectName().split("\\.")[2];
			locator = orMergerTable.getItems().get(rowIndex).getLocatorType();
			objectProperty = orMergerTable.getItems().get(rowIndex).getObjectProperties();

			boolean appFlag = false;
			for (Application application : appList) {
				appFlag = false;
				if (application.getApp().equals(app)) {
					application.setPage(page, locator, objectName,
							objectProperty);
					appFlag = true;
					break;
				}
			}

			if (!appFlag) {
				Application application = new Application();
				application.setApp(app);
				application.setPage(page, locator, objectName, objectProperty);
				appList.add(application);
			}
		}
	}

	/**	Generates the XML file from the "Application" class object
	 * @param appList
	 * @param fileName
	 */
	void prepareXMLTemplate(List<Application> appList, String fileName) {

		Document doc = new Document();
		Element rootElement = new Element("Object_Repository");
		doc.setRootElement(rootElement);
		Element application = null;
		Element pageNode;
		Element object = null;

		for (Application app : appList) {
			application = new Element("Application");
			application.setAttribute("name", app.getApp());

			for (Page p : app.getPage()) {
				pageNode = new Element("Page");
				pageNode.setAttribute("name", p.getPage());
				application.addContent(pageNode);
				for (ObjectProperty o : p.getobjectPropertyList()) {
					object = new Element("Object");
					object.setAttribute("reference", o.getObject());
					object.setAttribute("locatorType", o.getLocator());
					object.setAttribute("property", o.getObjectProperty());
					object.setText(app.getApp() + "." + p.getPage() + "."
							+ o.getObject());
					pageNode.addContent(object);
				}
			}
			doc.getRootElement().addContent(application);
		}

		XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
		try {
			FileOutputStream out = new FileOutputStream(fileName);
			xmlOutputter.output(doc, out);
			out.close();
			//setRightStatus("Object repository is saved to - "+fileName);
		} catch (IOException e) {
			System.out.println("ERROR! --- Unbale to write into XML : "+fileName+"\n"+e);
			//DialogViewController.showExceptionDialog("ERROR! --- Unable to write data to XML : "+fileName, e);
		}
	}

	/**
	 * Deletes selected object from the Duplicate Object Repository table
	 */
	@FXML
	void deleteObject() {
		ObjectRepositoryData object = orDuplicateTable.getSelectionModel().getSelectedItem();
		if (object != null) {
			Optional<ButtonType> option = DialogViewController.showConfirmation("Delete Object", "Are you sure want to delete this object ?", object.getObjectName());
			if (option.get() == ButtonType.OK) {
				duplicateData.remove(object);
				Notifications.create().title("Delete Object")
				.text("Object deleted - "+object.getObjectName())
				.position(Pos.BOTTOM_RIGHT).showInformation();
			}
			orDuplicateTable.requestFocus();
		}
		else {
			DialogViewController.showAlert("Delete Object", "No object is selected.", "Please select an item from the table to delete.");
		}
	}

	/**
	 * Move selected object from the Duplicate Object Repository table to Unique Repository table
	 */
	@FXML
	void moveObject() {

		ObjectRepositoryData duplicateobject = orDuplicateTable.getSelectionModel().getSelectedItem();
		ObjectRepositoryData foundData = null;
		if (duplicateobject != null) {

			boolean isFound = false;
			for (ObjectRepositoryData data : mergedData) {

				if (data.getObjectName().equalsIgnoreCase(duplicateobject.getObjectName())) {

					isFound = true;
					foundData = data;
					break;
				}
			}
			if (isFound) {

				Optional<ButtonType> option = DialogViewController.showConfirmation("Move Object", "Are you sure want to replace this object ?", foundData.getObjectName());
				if (option.get() == ButtonType.OK) {

					mergedData.remove(foundData);
					mergedData.add(duplicateobject);
					duplicateData.remove(duplicateobject);
					Notifications.create().title("Move Object")
					.text(foundData.getObjectName()+" is replaced with "+duplicateobject.getObjectName())
					.position(Pos.BOTTOM_RIGHT).showInformation();
				}
			}
			else {

				mergedData.add(duplicateobject);
				duplicateData.remove(duplicateobject);
				Notifications.create().title("Move Object")
				.text(duplicateobject.getObjectName()+" is now unique and it has been moved successfully !")
				.position(Pos.BOTTOM_RIGHT).showInformation();
			}
			orDuplicateTable.requestFocus();
		}
		else {
			DialogViewController.showAlert("Move Object", "No object is selected.", "Please select an item from the table to replace.");
		}
	}

	/**
	 * Open an OR XML file
	 */
	@FXML
	void openFile() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open GTAF Object Repository");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("GTAF OR file (*.xml)", "*.xml");
		fileChooser.getExtensionFilters().add(extFilter);
		File selectedFile = fileChooser.showOpenDialog(Main.mergerStage);
		if (selectedFile != null) {
			xmlPath2 = selectedFile.getAbsolutePath();
			header2.setText("File 2 selected - "+xmlPath2);
			Notifications.create().title("Import File")
			.text("File selected - "+xmlPath2)
			.position(Pos.BOTTOM_RIGHT).showInformation();
		}
		else {
			xmlPath2 = "";
			header2.setText("File 2 - Import cancelled !");
			Notifications.create().title("Import File")
			.text("File import cancelled !")
			.position(Pos.BOTTOM_RIGHT).showInformation();
		}
	}

}
