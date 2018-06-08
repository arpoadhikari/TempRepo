package UI;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ObjectRepository {

	private JFrame frame;
	DefaultTableModel dm;
	private JTable table;
	private JTextField objectProperties;
	JComboBox moduleCombo = new JComboBox();
	JComboBox pageCombo = new JComboBox();
	JComboBox objectCombo = new JComboBox();
	JComboBox<String> locatorCombo;
	List<String> dataList = new ArrayList<>();
	Set<String> moduleSet = new HashSet<>();
	Set<String> pageSet = new HashSet<>();
	Set<String> objectSet = new HashSet<>();

	List<Application> appList = new ArrayList<>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ObjectRepository window = new ObjectRepository();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ObjectRepository() {
		initialize();
		moduleSet.add("");
		pageSet.add("");
		objectSet.add("");
		createColumns();
	}

	public void createColumns() {
		dm = (DefaultTableModel) table.getModel();
		dm.addColumn("ObjectName");
		dm.addColumn("FindBy");
		dm.addColumn("Properties");
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 864, 393);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 88, 828, 234);
		table = new JTable();
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setCellSelectionEnabled(true);
		table.setBorder(new LineBorder(new Color(0, 0, 0)));
		table.setBackground(SystemColor.activeCaption);
		scrollPane.setViewportView(table);
		frame.getContentPane().add(scrollPane);
		table.setColumnSelectionAllowed(true);
		table.setCellSelectionEnabled(true);

		table.setShowHorizontalLines(true);
		table.setShowVerticalLines(true);

		JLabel moduleName = new JLabel("Module Name");
		moduleName.setBounds(10, 11, 70, 14);
		frame.getContentPane().add(moduleName);

		moduleCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (String model : moduleSet) {
					moduleCombo.addItem(model);
				}
				moduleSet.add(moduleCombo.getSelectedItem().toString());
			}
		});
		moduleCombo.setEditable(true);
		moduleCombo.setBounds(10, 26, 107, 20);
		frame.getContentPane().add(moduleCombo);

		JLabel pageName = new JLabel("Page Name");
		pageName.setBounds(145, 11, 70, 14);
		frame.getContentPane().add(pageName);

		pageCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (String page : pageSet) {
					pageCombo.addItem(page);
				}
				pageSet.add(pageCombo.getSelectedItem().toString());
			}
		});
		pageCombo.setEditable(true);
		pageCombo.setBounds(145, 26, 107, 20);
		frame.getContentPane().add(pageCombo);

		JLabel objectName = new JLabel("Object Name");
		objectName.setBounds(277, 11, 77, 14);
		frame.getContentPane().add(objectName);

		objectCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (String object : objectSet) {
					objectCombo.addItem(object);
				}
				objectSet.add(objectCombo.getSelectedItem().toString());
			}
		});
		objectCombo.setEditable(true);
		objectCombo.setBounds(277, 26, 107, 20);
		frame.getContentPane().add(objectCombo);

		JLabel locator = new JLabel("Locator");
		locator.setBounds(413, 11, 60, 14);
		frame.getContentPane().add(locator);
		locatorCombo = new javax.swing.JComboBox<>();
		locatorCombo.setModel(new javax.swing.DefaultComboBoxModel<>(
				new String[] { "", "id", "name", "cssSelector", "linkText",
						"partialLinkText", "tagName", "xpath" }));
		locatorCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}
		});
		locatorCombo.setBounds(413, 26, 97, 20);
		frame.getContentPane().add(locatorCombo);

		JButton btnAdd = new JButton("ADD");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/*
				 * objectLocator = new ArrayList<>();
				 * objectLocator.add(objectProperties.getText());
				 * objectLocator.add(locatorCombo.getSelectedItem().toString());
				 * objectMapper.put(moduleCombo.getSelectedItem().toString() +
				 * "." + pageCombo.getSelectedItem().toString() + "." +
				 * objectCombo.getSelectedItem().toString(), objectLocator);
				 */
				dataList = new ArrayList<>();
				String app = moduleCombo.getSelectedItem().toString();
				String page = pageCombo.getSelectedItem().toString();
				String locator = locatorCombo.getSelectedItem().toString();
				String object = objectCombo.getSelectedItem().toString();
				String objectProperty = objectProperties.getText();
				dataList.add(app + "." + page + "." + object);
				dataList.add(locator);
				dataList.add(objectProperty);

				populateRows(dataList);
				boolean appFlag = false;
				for (Application application : appList) {
					appFlag = false;
					if (application.getApp().equals(app)) {
						application.setPage(page, locator, object,
								objectProperty);
						appFlag = true;
						break;
					}
				}

				if (!appFlag) {
					Application application = new Application();
					application.setApp(app);
					application.setPage(page, locator, object, objectProperty);
					appList.add(application);
				}

				moduleCombo.removeAllItems();
				pageCombo.removeAllItems();
				objectCombo.removeAllItems();
				locatorCombo.setSelectedIndex(0);
				objectProperties.setText("");
			}
		});
		btnAdd.setBounds(368, 54, 70, 23);
		frame.getContentPane().add(btnAdd);

		JLabel properties = new JLabel("Properties");
		properties.setBounds(539, 11, 70, 14);
		frame.getContentPane().add(properties);

		objectProperties = new JTextField();
		objectProperties.setBounds(538, 26, 300, 20);
		frame.getContentPane().add(objectProperties);
		objectProperties.setColumns(10);

		JButton btnSubmit = new JButton("SUBMIT");
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				prepareXMLTemplate(appList);

			}
		});
		btnSubmit.setBounds(749, 321, 89, 23);
		frame.getContentPane().add(btnSubmit);

		JButton btnImport = new JButton("IMPORT");
		btnImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					/*table = new JTable();
					table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
					table.setCellSelectionEnabled(true);
					table.setBorder(new LineBorder(new Color(0, 0, 0)));
					table.setBackground(SystemColor.activeCaption);
					JScrollPane scrollPane = new JScrollPane();
					scrollPane.setViewportView(table);
					frame.getContentPane().add(scrollPane);
					table.setColumnSelectionAllowed(true);
					table.setCellSelectionEnabled(true);

					table.setShowHorizontalLines(true);
					table.setShowVerticalLines(true);*/
					loadXMLFile();
				} catch (XPathExpressionException | SAXException | IOException
						| ParserConfigurationException | TransformerException
						| XMLStreamException e) {
					e.printStackTrace();
				}

			}
		});
		btnImport.setBounds(10, 54, 89, 23);
		frame.getContentPane().add(btnImport);
	}

	private void populateRows(List<String> dataList) {
		dm = (DefaultTableModel) table.getModel();
		String[] rowData = new String[dataList.size()];
		dataList.toArray(rowData);
		dm.addRow(rowData);
	}

	public void prepareXMLTemplate(List<Application> appList) {
		Document doc = new Document();
		Element rootElement = new Element("Object_Repository");
		doc.setRootElement(rootElement);
		Element application = null;
		Element pageNode;
		Element object = null;
		/**
		 * R_Start.moduleList contains all different sets of modules and its
		 * details
		 */
		for (Application app : appList) {
			application = new Element("Application");
			application.setAttribute("name", app.getApp());

			/**
			 * R_Start.moduleList.get(moduleName) contains the details of
			 * individual test cases within the modules
			 */
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
			xmlOutputter.output(doc,
					new FileOutputStream(System.getProperty("user.dir")
							+ "/CICD/Test" + ".xml"));
		} catch (IOException e) {
			e.printStackTrace();

		}
	}

	public void loadXMLFile() throws SAXException, IOException,
			ParserConfigurationException, TransformerException,
			XMLStreamException, XPathExpressionException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		org.w3c.dom.Document doc = builder.parse(new File(
				"C:\\GTAF_Release\\GTAF_6.5_SC\\Libraries\\CICD\\Test.xml"));
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		XPathExpression object = xpath.compile("//Object");
		NodeList objectList = (NodeList) object.evaluate(doc,
				XPathConstants.NODESET);
		for (int i = 0; i < objectList.getLength(); i++) {
			dataList = new ArrayList<>();
			System.out.println(objectList.item(i).getTextContent());
			Node node = objectList.item(i);
			org.w3c.dom.Element element = (org.w3c.dom.Element) node;
			System.out.println(element.getAttribute("reference"));
			System.out.println(element.getAttribute("locatorType"));
			System.out.println(element.getAttribute("property"));
			
			dataList.add(objectList.item(i).getTextContent());
			dataList.add(element.getAttribute("locatorType").toString());
			dataList.add(element.getAttribute("property").toString());
			populateRows(dataList);
		}
	}
}
