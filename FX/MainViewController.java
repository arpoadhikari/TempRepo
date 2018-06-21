package application.views;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.controlsfx.control.table.TableFilter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
//import org.w3c.dom.Element;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import application.Main;
import application.support.Application;
import application.support.ObjectProperty;
import application.support.ObjectRepositoryData;
import application.support.Page;
import application.support.XML_Reader;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

/**
 * Main controller class for the application
 *
 */
public class MainViewController {

	@FXML
	private TableView<ObjectRepositoryData> orTable;
	@FXML
	private TableColumn<ObjectRepositoryData, String> objectName;
	@FXML
	private TableColumn<ObjectRepositoryData, String> locatorType;
	@FXML
	private TableColumn<ObjectRepositoryData, String> objectProperties;
	@FXML
	private Label rightStatusText;
	@FXML
	private Label leftStatusText;
	@FXML
	private TextField applicationNameInput;
	@FXML
	private TextField pageNameInput;
	@FXML
	private TextField objectNameInput;
	@FXML
	private TextField objectPropertyInput;
	@FXML
	private ComboBox<String> locatorTypeCombo;
	@FXML
	private Button addEditButton;
	@FXML
	private Button clearButton;
	@FXML
	private Button deleteButton;
	@FXML
	private MenuButton addUpdateMenuButton;
	@FXML
	private MenuItem addButton;
	@FXML
	private MenuItem updateButton;

	public static String xmlPath = "";
	public static ObservableList<ObjectRepositoryData> masterData = FXCollections.observableArrayList();
	private List<Application> appList = new ArrayList<>();

	AutoCompletionBinding<String> autoBindingAppNameInput, autoBindingPageNameInput, autoBindingObjectNameInput;

	/**
	 * Initializes the application
	 */
	@FXML
	void initialize() {

		//Sets Table View Properties
		objectName.setCellValueFactory(new PropertyValueFactory<ObjectRepositoryData, String>("objectName"));
		locatorType.setCellValueFactory(new PropertyValueFactory<ObjectRepositoryData, String>("locatorType"));
		objectProperties.setCellValueFactory(new PropertyValueFactory<ObjectRepositoryData, String>("objectProperties"));

		setLocatorTypes();
		loadDataFromXML();
		setTableFilter();

		// Listener	to auto populate the Suggestion List for Application Name
		applicationNameInput.focusedProperty().addListener(new ChangeListener<Boolean>()
		{
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
			{
				if (newPropertyValue)
				{
					autoPopulateApplicationList();
				}
			}
		});

		// Listener	to auto populate the Suggestion List for Page Name
		pageNameInput.focusedProperty().addListener(new ChangeListener<Boolean>()
		{
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
			{
				if (newPropertyValue)
				{
					autoPopulatePageList();
				}
			}
		});

		// Listener	to auto populate the Suggestion List for Object Name
		objectNameInput.focusedProperty().addListener(new ChangeListener<Boolean>()
		{
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
			{
				if (newPropertyValue)
				{
					autoPopulateObjectList();
				}
			}
		});

		//Listener to restrict '.' in Application name
		applicationNameInput.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue,
					String newValue) {
				if (newValue.split("\\.").length > 0 || newValue.contains(".")) {
					applicationNameInput.setText(newValue.replaceAll("[.]", ""));
				}
			}
		});

		//Listener to restrict '.' in Page name
		pageNameInput.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue,
					String newValue) {
				if (newValue.split("\\.").length > 0 || newValue.contains(".")) {
					pageNameInput.setText(newValue.replaceAll("[.]", ""));
				}
			}
		});

		//Listener to restrict '.' in Object name
		objectNameInput.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue,
					String newValue) {
				if (newValue.split("\\.").length > 0 || newValue.contains(".")) {
					objectNameInput.setText(newValue.replaceAll("[.]", ""));
				}
			}
		});

		// Listener to display the object count on add/remove/replace/update in the or table
		masterData.addListener(new ListChangeListener<ObjectRepositoryData>() {
			@Override
			public void onChanged(ListChangeListener.Change<? extends ObjectRepositoryData> c) {
				while(c.next()){
					if(c.wasAdded() || c.wasRemoved() || c.wasReplaced() | c.wasUpdated()){
						displayObjectCount();
					}
				}

			}
		});

	}

	/** Display text in the right status bar
	 * @param text
	 */
	void setRightStatus(String text) {
		rightStatusText.setText(text);
	}

	/**
	 * Clear all the input fields
	 */
	@FXML
	void clearInputs() {
		applicationNameInput.clear();
		pageNameInput.clear();
		objectNameInput.clear();
		locatorTypeCombo.getSelectionModel().select("xpath");
		objectPropertyInput.clear();
		applicationNameInput.requestFocus();
	}

	/**
	 * Load data from the XML to display the content in the table
	 */
	void loadDataFromXML() {

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

					masterData.add(new ObjectRepositoryData(objectName, locatorType, objectProperties));
				}
				orTable.setItems(masterData);
				displayObjectCount();
				Main.primaryStage.setTitle(Main.appTitle+" - "+xmlPath);
				setRightStatus("File loaded - "+xmlPath);
			} catch (Exception e) {
				System.out.println("ERROR! --- Unable to load data from XML : "+xmlPath+"\n"+e);
				DialogViewController.showExceptionDialog("ERROR! --- Unable to load data from XML : "+xmlPath, e);
				setRightStatus("File was not loaded - "+xmlPath);
			}
		}
		else {
			setRightStatus("File was not loaded - "+xmlPath);
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
			setRightStatus("Object repository is saved to - "+fileName);
		} catch (IOException e) {
			System.out.println("ERROR! --- Unbale to write into XML : "+fileName+"\n"+e);
			DialogViewController.showExceptionDialog("ERROR! --- Unable to write data to XML : "+fileName, e);
		}
	}

	/**
	 * Set Table Filter
	 */
	void setTableFilter() {
		orTable.setItems(masterData);
		TableFilter.forTableView(orTable).lazy(false).apply();
	}

	/**
	 * Set values to the locator type combo box
	 */
	void setLocatorTypes() {
		locatorTypeCombo.getItems().addAll( "id", "name", "cssSelector", "linkText",
				"partialLinkText", "tagName", "xpath");
		locatorTypeCombo.getSelectionModel().select("xpath");
	}

	/**
	 * Creates a list of "Application" class object from the Object Repository table
	 */
	void getTableData() {

		int nRow = orTable.getItems().size();
		String app;
		String page;
		String locator;
		String objectName;
		String objectProperty;
		appList = new ArrayList<>();
		for (int rowIndex = 0; rowIndex < nRow; rowIndex++) {

			app = orTable.getItems().get(rowIndex).getObjectName().split("\\.")[0];
			page = orTable.getItems().get(rowIndex).getObjectName().split("\\.")[1];
			objectName = orTable.getItems().get(rowIndex).getObjectName().split("\\.")[2];
			locator = orTable.getItems().get(rowIndex).getLocatorType();
			objectProperty = orTable.getItems().get(rowIndex).getObjectProperties();

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

	/**
	 * Create a new OR
	 */
	@FXML
	void createNew() {
		if (xmlPath.length() > 0) {
			Optional<ButtonType> option = DialogViewController.showConfirmation("Create New",
					"Would you like to save your current work before creating a new object repository ?", xmlPath);
			if (option.get() == ButtonType.OK) {
				save();
			}
			else {
				return;
			}
		}
		xmlPath = "";
		clearObjectRepositoryTable();
		Main.primaryStage.setTitle(Main.appTitle+" - "+xmlPath);
		setRightStatus("");
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
		File selectedFile = fileChooser.showOpenDialog(Main.primaryStage);
		if (selectedFile != null) {
			xmlPath = selectedFile.getAbsolutePath();
			Main.primaryStage.setTitle(Main.appTitle+" - "+xmlPath);
			setRightStatus("File selected - "+xmlPath);
			clearObjectRepositoryTable();
			loadDataFromXML();
		}
		else {
			setRightStatus("File selection canceled");
		}
	}

	/**
	 * Clear all the data in the Object Repository table
	 */
	void clearObjectRepositoryTable() {
		masterData.clear();
		orTable.refresh();
	}

	/**
	 * Closes the application
	 */
	@FXML
	void closeApp() {
		Optional<ButtonType> option = DialogViewController.showConfirmation("Close Application", "Confirm Exit", "Are you sure you want to exit ?");
		if (option.get() == ButtonType.OK) {
			Platform.exit();
		}
	}

	/**
	 * Display the count of rows in the left status bar
	 */
	void displayObjectCount() {
		leftStatusText.setText("Total Objects : "+orTable.getItems().size());
	}

	/**
	 * Add an object to the Object Repository table
	 */
	@FXML
	void addObject() {

		String objectName = applicationNameInput.getText().trim()+"."
				+pageNameInput.getText().trim()+"."
				+objectNameInput.getText().trim();
		String locatorType = locatorTypeCombo.getSelectionModel().getSelectedItem();
		String objectProperty = objectPropertyInput.getText().trim();
		boolean isFound = false;

		if (applicationNameInput.getText().trim().isEmpty() ||
				pageNameInput.getText().trim().isEmpty() ||
				objectNameInput.getText().trim().isEmpty() ||
				locatorTypeCombo.getSelectionModel().isEmpty()) {

			DialogViewController.showAlert("Add Object", "Application Name / Page Name / Object Name can't be blank !", "Please provide correct input.");
		}
		else {
			for (int i = 0; i < orTable.getItems().size(); i++) {
				if (orTable.getItems().get(i).getObjectName().equalsIgnoreCase(objectName)) {
					isFound = true;
					break;
				}
			}
			if (isFound) {
				DialogViewController.showAlert("Add Object", "Duplicate object exists, please provide different name", objectName);
			}
			else {
				masterData.add(new ObjectRepositoryData(objectName, locatorType, objectProperty));
				setRightStatus("Object added - "+objectName);
				clearInputs();
			}
			orTable.setItems(masterData);
		}
	}

	/**
	 * Update an object to the Object Repository table
	 */
	@FXML
	void updateObject() {

		String objectName = applicationNameInput.getText().trim()+"."
				+pageNameInput.getText().trim()+"."
				+objectNameInput.getText().trim();
		String locatorType = locatorTypeCombo.getSelectionModel().getSelectedItem();
		String objectProperty = objectPropertyInput.getText().trim();
		ObjectRepositoryData object = orTable.getSelectionModel().getSelectedItem();

		if (applicationNameInput.getText().trim().isEmpty() ||
				pageNameInput.getText().trim().isEmpty() ||
				objectNameInput.getText().trim().isEmpty() ||
				locatorTypeCombo.getSelectionModel().isEmpty()) {

			DialogViewController.showAlert("Update Object", "Applicaton Name / Page Name / Object Name can't be blank !", "Please provide correct input.");
		}
		else {
			if (object != null) {
				Optional<ButtonType> option = DialogViewController.showConfirmation("Update Object", "Are you sure want to update this object ?", object.getObjectName());
				if (option.get() == ButtonType.OK) {
					masterData.remove(object);
					masterData.add(new ObjectRepositoryData(objectName, locatorType, objectProperty));
					setRightStatus("Object Updated - "+object.getObjectName());
					clearInputs();
				}
			}
			else {
				DialogViewController.showAlert("Update Object", "No object is selected.", "Please select an item from the table to update.");
			}
			orTable.setItems(masterData);
		}
	}

	/**
	 * Deletes selected row from the Object Repository table
	 */
	@FXML
	void deleteObject() {
		ObjectRepositoryData object = orTable.getSelectionModel().getSelectedItem();
		if (object != null) {
			Optional<ButtonType> option = DialogViewController.showConfirmation("Delete Object", "Are you sure want to delete this object ?", object.getObjectName());
			if (option.get() == ButtonType.OK) {
				masterData.remove(object);
				clearInputs();
				setRightStatus("Object deleted - "+object.getObjectName());
			}
			orTable.requestFocus();
		}
		else {
			DialogViewController.showAlert("Delete Object", "No object is selected.", "Please select an item from the table to delete.");
		}
	}

	/**
	 * Save the Object Repository table data to the loaded XML file
	 */
	@FXML
	void save() {
		if (!xmlPath.equalsIgnoreCase("")) {
			getTableData();
			prepareXMLTemplate(appList, xmlPath);
		}
		else {
			saveAs();
		}
	}

	/**
	 * Save the Object Repository table data to a new XML file
	 */
	@FXML
	void saveAs() {

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save as GTAF Object Repository");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("GTAF OR file (*.xml)", "*.xml");
		fileChooser.getExtensionFilters().add(extFilter);
		File selectedFile = fileChooser.showSaveDialog(Main.primaryStage);
		if (selectedFile != null) {
			xmlPath = selectedFile.getAbsolutePath();
			Main.primaryStage.setTitle(Main.appTitle+" - "+xmlPath);
			setRightStatus("File selected - "+xmlPath);
			getTableData();
			prepareXMLTemplate(appList, xmlPath);
		}
		else {
			setRightStatus("File selection canceled");
		}
	}

	/**
	 * Set values to the input fields on selection of Object Repository table row
	 */
	@FXML
	void setInputFieldsOnRowSelect() {
		deleteButton.setDisable(false);
		int selectedRowIndex = orTable.getSelectionModel().getSelectedIndex();
		if (selectedRowIndex >= 0) {
			String app = orTable.getItems().get(selectedRowIndex).getObjectName().split("\\.")[0];
			String page = orTable.getItems().get(selectedRowIndex).getObjectName().split("\\.")[1];
			String objectName = orTable.getItems().get(selectedRowIndex).getObjectName().split("\\.")[2];
			String locator = orTable.getItems().get(selectedRowIndex).getLocatorType();
			String objectProperty = orTable.getItems().get(selectedRowIndex).getObjectProperties();
			applicationNameInput.setText(app);
			pageNameInput.setText(page);
			objectNameInput.setText(objectName);
			locatorTypeCombo.getSelectionModel().select(locator);
			objectPropertyInput.setText(objectProperty);
		}
	}

	/**
	 * Creates auto completion binding for Application Name input
	 */
	@FXML
	void autoPopulateApplicationList() {

		Set<String> data = new HashSet<String>();
		for (int i = 0; i < masterData.size(); i++) {
			data.add(masterData.get(i).getObjectName().split("\\.")[0]);
		}
		if (autoBindingAppNameInput != null) {
			autoBindingAppNameInput.dispose();
		}
		autoBindingAppNameInput = TextFields.bindAutoCompletion(applicationNameInput, data);
	}

	/**
	 * Creates auto completion binding for Page Name input
	 */
	@FXML
	void autoPopulatePageList() {

		Set<String> data = new HashSet<String>();
		String actualAppName = applicationNameInput.getText();
		for (int i = 0; i < masterData.size(); i++) {
			String appName  = masterData.get(i).getObjectName().split("\\.")[0];
			if (appName.equalsIgnoreCase(actualAppName)) {
				data.add(masterData.get(i).getObjectName().split("\\.")[1]);
			}
		}
		if (autoBindingPageNameInput != null) {
			autoBindingPageNameInput.dispose();
		}
		autoBindingPageNameInput = TextFields.bindAutoCompletion(pageNameInput, data);
	}

	/**
	 * Creates auto completion binding for Object Name input
	 */
	@FXML
	void autoPopulateObjectList() {

		Set<String> data = new HashSet<String>();
		String actualAppName = applicationNameInput.getText();
		String actualPageName = pageNameInput.getText();
		for (int i = 0; i < masterData.size(); i++) {
			String appName  = masterData.get(i).getObjectName().split("\\.")[0];
			String pageName  = masterData.get(i).getObjectName().split("\\.")[1];
			if (appName.equalsIgnoreCase(actualAppName) && pageName.equalsIgnoreCase(actualPageName)) {
				data.add(masterData.get(i).getObjectName().split("\\.")[2]);
			}
		}
		if (autoBindingObjectNameInput != null) {
			autoBindingObjectNameInput.dispose();
		}
		autoBindingObjectNameInput = TextFields.bindAutoCompletion(objectNameInput, data);
	}

	@FXML
	void launchMerger() throws IOException {

		Main.mergerStage.showAndWait();
	}

}
