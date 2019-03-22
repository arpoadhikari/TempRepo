package appWindow;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;

import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.observablecollections.ObservableListListener;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import tools.Baseline;
import tools.ExcelUtil;
import tools.HTMLReport;
import tools.TableData;
import tools.customDriver.AttachedWebDriver2;

public class Inspector extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String jsFilePath = "/resources/inspector.js";

	private JPanel panel_Connector;
	private JPanel panel_Inspector;
	private JTextField textField_ServerURL;
	private JTextField textField_SessionID;
	private JButton btnInspect;
	private JComboBox<String> objectType_ComboBox;
	private JComboBox<String> objectSubType_ComboBox;
	private JTable table;
	private JTextPane txtpnl_Description;
	private JLabel lblImageView;
	private JTextField txtApplicationurl;
	private JButton btnValidateAll;
	private JButton btnGenerateReport;
	
	final int sl_ColID = 0;
	final int pageTitle_ColID = 1;
	final int objectType_ColID = 2;
	final int objectSubType_ColID = 3;
	final int objectName_ColID = 4;
	final int status_ColID = 5;

	private static WebDriver driver;

	private WebElement[] currentElements = new WebElement[] {null, null};
	private WebElement[] previousElements = new WebElement[] {null, null};

	private boolean isRunningXpathCapture  = false;
	private Thread captureXpath;
	private boolean setXpathCapture = false;
	private int elementID = 1;

	String baseLinePath = "Baseline/Baseline.xlsx";
	String imagePath = "Baseline/Images/";
	ExcelUtil excelUtil = new ExcelUtil(baseLinePath);
	HTMLReport report;
	List<String> elementTypeList = excelUtil.getWorksheetList();
	List<String> elementSubTypeList = new ArrayList<String>();
	List<String> allCSSList = new ArrayList<String>();
	HashMap<String, Baseline> baselineList = new HashMap<String, Baseline>();
	ObservableList<TableData> tableData = ObservableCollections.observableList(new ArrayList<TableData>());
	JTableBinding<TableData, List<TableData>, JTable> tb;

	public static boolean isRunning = false;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			Inspector dialog = new Inspector(new URL("http://localhost:21956"), "78f2eec4131474f7ed240df483b2201c");
			//dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public Inspector(URL serverURL, String sessionID) {
		
		isRunning = true;

		//Load the baseline data
		loadBaseLine();

		//Initialize the window with the settings ->
		setResizable(false);
		getContentPane().setFont(new Font("Segoe UI", Font.PLAIN, 14));
		setFont(new Font("Segoe UI", Font.PLAIN, 14));
		setTitle("UI Inspector");
		setIconImage(Toolkit.getDefaultToolkit().getImage(Inspector.class.getResource("/icons/inspector.png")));
		//setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 900, 550);
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);

		UIManager.put("OptionPane.messageFont", new Font("Segoe UI", Font.PLAIN, 14));
		UIManager.put("OptionPane.buttonFont", new Font("Segoe UI", Font.PLAIN, 14));
		//<-----

		//Initialize the JPanel Containing the controls which creates the connection with the browser ->
		panel_Connector = new JPanel();
		panel_Connector.setName("");
		panel_Connector.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		panel_Connector.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_Connector.setBounds(6, 6, 882, 61);
		getContentPane().add(panel_Connector);
		panel_Connector.setLayout(null);
		//<-----

		//Initializes the ServerURL input box with the label
		JLabel lblServerURL = new JLabel("Server URL :");
		lblServerURL.setBounds(6, 3, 110, 20);
		lblServerURL.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		panel_Connector.add(lblServerURL);

		textField_ServerURL = new JTextField();
		textField_ServerURL.setToolTipText("<html>\r\n\t<p>\r\n\t\t<font size=\"4\" face=\"Segoe UI\">\r\n\t\t\tInput the Selenium Server URL.\r\n\t\t</font>\r\n\t</p>\r\n</html>");
		textField_ServerURL.setBounds(6, 22, 164, 30);
		textField_ServerURL.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		textField_ServerURL.setColumns(10);
		panel_Connector.add(textField_ServerURL);
		if (serverURL != null) {
			textField_ServerURL.setText(serverURL.toString());
		}
		else {
			textField_ServerURL.setText("");
		}
		//<-----

		//Initializes the SessionID input box with the label
		JLabel lblSessionId = new JLabel("Session ID :");
		lblSessionId.setBounds(180, 3, 110, 20);
		lblSessionId.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		panel_Connector.add(lblSessionId);

		textField_SessionID = new JTextField();
		textField_SessionID.setToolTipText("<html>\r\n\t<p>\r\n\t\t<font size=\"4\" face=\"Segoe UI\">\r\n\t\t\tInput the browser session ID.\r\n\t\t</font>\r\n\t</p>\r\n</html>");
		textField_SessionID.setBounds(180, 22, 250, 30);
		textField_SessionID.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		textField_SessionID.setColumns(10);
		panel_Connector.add(textField_SessionID);
		textField_SessionID.setText(sessionID);
		//<-----

		//Initializes the Connect button and performs the Connection check
		JButton btnConnect = new JButton("Connect");
		btnConnect.setIconTextGap(2);
		btnConnect.setToolTipText("<html>\r\n\t<p>\r\n\t\t<font size=\"4\" face=\"Segoe UI\">\r\n\t\t\tConnect to an existing browser session.\r\n\t\t</font>\r\n\t</p>\r\n</html>");
		btnConnect.setIcon(new ImageIcon(Inspector.class.getResource("/icons/connect.png")));
		btnConnect.setMargin(new Insets(2, 2, 2, 2));
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				connectBrowser();
			}
		});
		btnConnect.setBounds(672, 22, 95, 30);
		btnConnect.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		panel_Connector.add(btnConnect);
		//<-----

		//Initializes the Inspect button to inject the javascript to the browser
		btnInspect = new JButton("Inspect");
		btnInspect.setEnabled(false);
		btnInspect.setBounds(777, 22, 95, 30);
		panel_Connector.add(btnInspect);
		btnInspect.setIconTextGap(2);
		btnInspect.setToolTipText("<html>\r\n\t<p>\r\n\t\t<font size=\"4\" face=\"Segoe UI\">\r\n\t\t\tMouseover to any object in web page to inspect &\r\n\t\t\t<br/>\r\n\t\t\tCtrl+Alt+C to capture the Xpath\r\n\t\t</font>\r\n\t</p>\r\n</html>");
		btnInspect.setIcon(new ImageIcon(Inspector.class.getResource("/icons/inspector.png")));
		btnInspect.setMargin(new Insets(2, 2, 2, 2));
		btnInspect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				//Injecting the JavaScript to the DOM
				JOptionPane.showMessageDialog(panel_Inspector, "Please go to the browser to inpect.");
				driver.switchTo().defaultContent();
				runInpector(driver);
				List<WebElement> iframes = driver.findElements(By.xpath("//iframe | //frame"));
				for (WebElement iframe : iframes) {
					driver.switchTo().frame(iframe);
					runInpector(driver);
					driver.switchTo().defaultContent();
				}
				//<-----

				//Background process to capture the xpath from Alert box
				captureXpath = new Thread(new Runnable() {
					public void run() {
						isRunningXpathCapture = true;
						setXpathCapture = true;
						do {
							captureElementDetails();
						} while (setXpathCapture);

					}
				});
				if(!isRunningXpathCapture) {
					captureXpath.start();
				}
				//<-----

			}
		});
		btnInspect.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		//<-----

		//Label for Application URL Text box
		JLabel lblApplicationUrl = new JLabel("Application URL :");
		lblApplicationUrl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lblApplicationUrl.setBounds(440, 3, 110, 20);
		panel_Connector.add(lblApplicationUrl);
		//<-----

		//Initializes the text box for application URL
		txtApplicationurl = new JTextField();
		txtApplicationurl.setToolTipText("<html>\r\n\t<p>\r\n\t\t<font size=\"4\" face=\"Segoe UI\">\r\n\t\t\tInput the Application URL.\r\n\t\t</font>\r\n\t</p>\r\n</html>");
		txtApplicationurl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		txtApplicationurl.setColumns(10);
		txtApplicationurl.setBounds(440, 22, 222, 30);
		panel_Connector.add(txtApplicationurl);
		//<-----

		//Initialize the JPanel Containing the controls which finds the elements ->
		panel_Inspector = new JPanel();
		panel_Inspector.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_Inspector.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		panel_Inspector.setBounds(211, 73, 677, 397);
		getContentPane().add(panel_Inspector);
		panel_Inspector.setLayout(null);
		//<-----

		//Initializes the scroll panel containing the table view 
		JScrollPane scrollPane_TableView = new JScrollPane();
		scrollPane_TableView.setBounds(6, 6, 664, 339);
		scrollPane_TableView.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel_Inspector.add(scrollPane_TableView);
		//<-----

		//Initializes the table view
		table = new JTable();
		table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		table.setShowHorizontalLines(true);
		table.setShowVerticalLines(true);
		table.setRowHeight(25);
		table.getTableHeader().setFont(new Font("Segoe UI", Font.PLAIN, 12));
		table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		scrollPane_TableView.setViewportView(table);
		//<-----

		//Initiate DataBinding for Table
		initDataBindings();
		//<-----

		//PopupMenu for Remove Row, Row Details, Validate and Highlight object ->
		JPopupMenu popupMenu_AddRemoveRow = new JPopupMenu();
		popupMenu_AddRemoveRow.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		popupMenu_AddRemoveRow.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		addPopup(table, popupMenu_AddRemoveRow);
		
		//Menu Item for validate
		JMenuItem mntmValidateRow = new JMenuItem("Validate");
		mntmValidateRow.setIcon(new ImageIcon(SessionGenerator.class.getResource("/icons/validate.png")));
		mntmValidateRow.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		mntmValidateRow.addActionListener(new ActionListener() {
			
			//Validate on action performed
			public void actionPerformed(ActionEvent e) {
				if (table.getSelectedRowCount() > 0) {
					validateSelectedData(table.getSelectedRow());
				}
				else {
					JOptionPane.showMessageDialog(scrollPane_TableView, "Please select an item in the table to validate.");
				}
			}
			//<-----
		});
		popupMenu_AddRemoveRow.add(mntmValidateRow);
		//<-----
		
		//Menu Item for Highlight
		JMenuItem mntmHighlight = new JMenuItem("Highlight Object");
		mntmHighlight.setIcon(new ImageIcon(SessionGenerator.class.getResource("/icons/highlight.png")));
		mntmHighlight.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		mntmHighlight.addActionListener(new ActionListener() {
			
			//Highlight on action performed
			public void actionPerformed(ActionEvent ev) {
				
				if (table.getSelectedRowCount() > 0) {
					String xpath = tableData.get(table.getSelectedRow()).getObjectXpath().trim();
					moveMouseTo(0, 0);
					setState(ICONIFIED);
					highlightObject(xpath, true);
				}
				else {
					JOptionPane.showMessageDialog(scrollPane_TableView, "Please select an item in the table to highlight.");
				}
			}
			//<-----
		});
		popupMenu_AddRemoveRow.add(mntmHighlight);
		//<-----
		
		//Menu item for display details
		JMenuItem mntmRowDetails = new JMenuItem("Display Details");
		mntmRowDetails.setIcon(new ImageIcon(SessionGenerator.class.getResource("/icons/info.png")));
		mntmRowDetails.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		mntmRowDetails.addActionListener(new ActionListener() {
			
			//Display details on action performed
			public void actionPerformed(ActionEvent e) {
				
				if (table.getSelectedRowCount() > 0) {
					displayRowDetails();
				}
				else {
					JOptionPane.showMessageDialog(scrollPane_TableView, "Please select an item in the table to diplay the details.");
				}
			}
			//<-----
		});
		popupMenu_AddRemoveRow.add(mntmRowDetails);
		//<-----
		
		popupMenu_AddRemoveRow.addSeparator();
		
		//Menu item for remove row
		JMenuItem mntmRemoveRow = new JMenuItem("Remove Row");
		mntmRemoveRow.setIcon(new ImageIcon(SessionGenerator.class.getResource("/icons/DelRow.png")));
		mntmRemoveRow.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		mntmRemoveRow.addActionListener(new ActionListener() {
			
			//Remove row on action performed
			public void actionPerformed(ActionEvent e) {
				removeRow();
			}
			//<-----
		});
		popupMenu_AddRemoveRow.add(mntmRemoveRow);
		//<-----

		//Initializes the Generate Report button
		btnGenerateReport = new JButton("Generate Report");
		btnGenerateReport.setIcon(new ImageIcon(Inspector.class.getResource("/icons/generate_report.png")));
		btnGenerateReport.addActionListener(new ActionListener() {
			
			//Generate report on action performed
			public void actionPerformed(ActionEvent arg0) {
				if (tableData.size() > 0) {
					String folderPath = openFileChooser();
					if (!folderPath.equalsIgnoreCase("")) {
						SimpleDateFormat T = new SimpleDateFormat("MM_dd_yyyy_h_mm_ss_a");
						String formattedDate = T.format(new Date());
						String reportDirName = folderPath+"\\"+"UsabiltiyTestingReport_"+formattedDate;
						new File(reportDirName).mkdirs();
						try {
							report = new HTMLReport(tableData, reportDirName);
							JOptionPane.showMessageDialog(panel_Inspector, "Report Created.\n"+reportDirName+"\\Report.html");
							Desktop.getDesktop().browse(new File(reportDirName).toURI());
						} catch (Exception e) {
							JOptionPane.showMessageDialog(panel_Inspector, "Report Creation Failed.\n"+e.getMessage());
						}
					}
				}
				else {
					JOptionPane.showMessageDialog(panel_Inspector, "Nothing to report.");
				}
			}
		});
		btnGenerateReport.setToolTipText("<html>\r\n\t<p>\r\n\t\t<font size=\"4\" face=\"Segoe UI\">\r\n\t\t\tGenerates a report in HTML format.\r\n\t\t</font>\r\n\t</p>\r\n</html>");
		btnGenerateReport.setMargin(new Insets(2, 2, 2, 2));
		btnGenerateReport.setIconTextGap(2);
		btnGenerateReport.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		btnGenerateReport.setBounds(376, 356, 140, 30);
		panel_Inspector.add(btnGenerateReport);
		//<-----

		//Initializes the CloseValidate All button
		btnValidateAll = new JButton("Validate All");
		btnValidateAll.addActionListener(new ActionListener() {
			
			//Validate all on action performed
			public void actionPerformed(ActionEvent e) {
				if (tableData.size() > 0) {
					for (int i = 0; i < tableData.size(); i++) {
						validateSelectedData(i);
					}
					JOptionPane.showMessageDialog(panel_Inspector, "Validation Completed.");
				}
				else {
					JOptionPane.showMessageDialog(panel_Inspector, "Nothing to validate.");
				}
			}
			//<-----
		});
		btnValidateAll.setIcon(new ImageIcon(Inspector.class.getResource("/icons/validate.png")));
		btnValidateAll.setToolTipText("<html>\r\n\t<p>\r\n\t\t<font size=\"4\" face=\"Segoe UI\">\r\n\t\t\tValidate all the data in the table. \r\n\t\t</font>\r\n\t</p>\r\n</html>");
		btnValidateAll.setMargin(new Insets(2, 2, 2, 2));
		btnValidateAll.setIconTextGap(2);
		btnValidateAll.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		btnValidateAll.setBounds(160, 356, 140, 30);
		panel_Inspector.add(btnValidateAll);
		//<-----

		//Initializes the Close button
		JButton btnClose = new JButton("Close");
		btnClose.setIconTextGap(2);
		btnClose.setMargin(new Insets(2, 2, 2, 2));
		btnClose.setToolTipText("<html>\r\n\t<p>\r\n\t\t<font size=\"4\" face=\"Segoe UI\">\r\n\t\t\tClose the UI Inspector.\r\n\t\t</font>\r\n\t</p>\r\n</html>");
		btnClose.setIcon(new ImageIcon(Inspector.class.getResource("/icons/close.png")));
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//Stop the running thread and dispose
				setXpathCapture = false;
				dispose();
				isRunning = false;
				//<-----
			}
		});
		btnClose.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		btnClose.setBounds(399, 481, 95, 30);
		getContentPane().add(btnClose);
		//<-----

		//Initializes the JPanel containing the controls from element type selection
		JPanel panel_ObjectType = new JPanel();
		panel_ObjectType.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_ObjectType.setBounds(6, 73, 200, 397);
		getContentPane().add(panel_ObjectType);
		panel_ObjectType.setLayout(null);
		//<-----

		//Label for element type combo box
		JLabel lblObjecttype = new JLabel("Object Type :");
		lblObjecttype.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lblObjecttype.setBounds(6, 3, 110, 20);
		panel_ObjectType.add(lblObjecttype);
		//<-----

		//Initializes the element type selection combo box
		objectType_ComboBox = new JComboBox<String>();
		objectType_ComboBox.setToolTipText("<html>\r\n\t<p>\r\n\t\t<font size=\"4\" face=\"Segoe UI\">\r\n\t\t\tSelect Object Type.\r\n\t\t</font>\r\n\t</p>\r\n</html>");
		String[] elementTypes = elementTypeList.toArray(new String[0]);
		objectType_ComboBox.setModel(new DefaultComboBoxModel<String>(elementTypes));
		objectType_ComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				//Event listener to populate the items to be shown in the element sub type combo box, description and image
				elementSubTypeList = baselineList.get(objectType_ComboBox.getSelectedItem().toString()).getSubTypeList();
				String[] elementSubTypes = elementSubTypeList.toArray(new String[0]);
				objectSubType_ComboBox.setModel(new DefaultComboBoxModel<String>(elementSubTypes));
				txtpnl_Description.setText(baselineList.get(objectType_ComboBox.getSelectedItem().toString()).
						getDescription(objectSubType_ComboBox.getSelectedItem().toString()));
				loadElementImage();
			}
		});
		objectType_ComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		objectType_ComboBox.setBounds(6, 24, 188, 30);
		panel_ObjectType.add(objectType_ComboBox);
		//<-----

		//Label for element sub type combo box
		JLabel lblElmentSubtype = new JLabel("Object SubType :");
		lblElmentSubtype.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lblElmentSubtype.setBounds(6, 63, 110, 20);
		panel_ObjectType.add(lblElmentSubtype);
		//<-----

		//Initializes the element sub type selection combo box
		objectSubType_ComboBox = new JComboBox<String>();
		objectSubType_ComboBox.setToolTipText("<html>\r\n\t<p>\r\n\t\t<font size=\"4\" face=\"Segoe UI\">\r\n\t\t\tSelect Object Sub Type.\r\n\t\t</font>\r\n\t</p>\r\n</html>");
		elementSubTypeList = baselineList.get(objectType_ComboBox.getSelectedItem().toString()).getSubTypeList();
		String[] elementSubTypes = elementSubTypeList.toArray(new String[0]);
		objectSubType_ComboBox.setModel(new DefaultComboBoxModel<String>(elementSubTypes));
		objectSubType_ComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				//Event listener to set the description text pane and image
				txtpnl_Description.setText(baselineList.get(objectType_ComboBox.getSelectedItem().toString()).
						getDescription(objectSubType_ComboBox.getSelectedItem().toString()));
				loadElementImage();
			}
		});
		objectSubType_ComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		objectSubType_ComboBox.setBounds(6, 84, 188, 30);
		panel_ObjectType.add(objectSubType_ComboBox);

		JScrollPane scrollPane_Description = new JScrollPane();
		scrollPane_Description.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		scrollPane_Description.setBounds(6, 125, 188, 114);
		panel_ObjectType.add(scrollPane_Description);
		//<-----


		//Initializes the description text panel
		txtpnl_Description = new JTextPane();
		scrollPane_Description.setViewportView(txtpnl_Description);
		txtpnl_Description.setToolTipText("<html>\r\n\t<p>\r\n\t\t<font size=\"4\" face=\"Segoe UI\">\r\n\t\t\tObject Description.\r\n\t\t</font>\r\n\t</p>\r\n</html>");
		txtpnl_Description.setEditable(false);
		txtpnl_Description.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		txtpnl_Description.setText(baselineList.get(objectType_ComboBox.getSelectedItem().toString()).
				getDescription(objectSubType_ComboBox.getSelectedItem().toString()));

		//Initializes the Image View panel surrounded by scroll pane
		JScrollPane scrollPane_ImageView = new JScrollPane();
		scrollPane_ImageView.setBounds(6, 250, 188, 140);
		panel_ObjectType.add(scrollPane_ImageView);

		lblImageView = new JLabel("");
		lblImageView.setToolTipText("<html>\r\n\t<p>\r\n\t\t<font size=\"4\" face=\"Segoe UI\">\r\n\t\t\tObject Image.\r\n\t\t</font>\r\n\t</p>\r\n</html>");
		scrollPane_ImageView.setViewportView(lblImageView);
		lblImageView.setHorizontalAlignment(SwingConstants.CENTER);
		loadElementImage();
		lblImageView.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		//<-----
		
		//Initializes the Image View panel to display the logo
		JLabel lblLogo = new JLabel("");
		lblLogo.setBounds(6, 474, 77, 42);
		ImageIcon logoIcon = new ImageIcon(SessionGenerator.class.getResource("/icons/logo.png"));
		Image logoImg = logoIcon.getImage();
		logoImg = logoImg.getScaledInstance(lblLogo.getWidth(), lblLogo.getHeight(), Image.SCALE_SMOOTH);
		lblLogo.setIcon(new ImageIcon(logoImg));
		getContentPane().add(lblLogo);
		//<-----

		//Windows Listener to stop the running thread
		addWindowListener(new WindowAdapter()
		{
			public void windowClosed(WindowEvent e)
			{
				setXpathCapture = false;
				isRunning = false;
			}
			public void windowClosing(WindowEvent e)
			{
				setXpathCapture = false;
				isRunning = false;
			}
		});
		//<-----


		//Adds listener to the table data observable list
		tableData.addObservableListListener(new ObservableListListener() {

			@Override
			public void listElementsRemoved(ObservableList arg0, int arg1, List arg2) {

				ObservableList<TableData> list = (ObservableList<TableData>) arg0;
				for (int i = 0; i < list.size(); i++) {
					list.get(i).setSl(i+1);
				}
				tb.unbind();
				tb.bind();
				setColumnWidth();
				addObjectTypeCombo();
			}

			@Override
			public void listElementsAdded(ObservableList arg0, int arg1, int arg2) {
				
				ObservableList<TableData> list = (ObservableList<TableData>) arg0;
				for (int i = 0; i < list.size(); i++) {
					list.get(i).setSl(i+1);
				}
				tb.unbind();
				tb.bind();
				setColumnWidth();
				addObjectTypeCombo();
			}

			@Override
			public void listElementReplaced(ObservableList arg0, int arg1, Object arg2) {
				
				tb.unbind();
				tb.bind();
				setColumnWidth();
				addObjectTypeCombo();
			}

			@Override
			public void listElementPropertyChanged(ObservableList arg0, int arg1) {
				
				tb.unbind();
				tb.bind();
				setColumnWidth();
				addObjectTypeCombo();
			}
		});
	}

	/** Method to load the data from baseline document
	 * @return
	 */
	public void loadBaseLine() {

		for (String elementType : elementTypeList) {
			Baseline baseline = new Baseline(baseLinePath, elementType);
			baselineList.put(elementType, baseline);
			allCSSList.addAll(baseline.getCSSList());
		}
		Set<String> hs = new HashSet<>();
		hs.addAll(allCSSList);
		allCSSList.clear();
		allCSSList.addAll(hs);
	}

	/**
	 * Loads base image of selected object
	 */
	public void loadElementImage() {
		String imageFileName = imagePath + objectType_ComboBox.getSelectedItem().toString()+"_"+objectSubType_ComboBox.getSelectedItem().toString()+".png";
		lblImageView.setIcon(new ImageIcon(imageFileName));
	}

	/**
	 * Method to auto capture Element details
	 */
	public void captureElementDetails() {

		try {
			Alert alert = driver.switchTo().alert();
			String objectXpath = alert.getText();
			alert.accept();

			getWebElement("//body").sendKeys("");
			moveMouseTo(0, 0);
			Thread.sleep(1000);
			
			WebElement ele = getWebElement(objectXpath);
			String pageTitle = driver.getTitle();
			String pageURL = driver.getCurrentUrl();
			String objectType = "--Select";//objectType_ComboBox.getSelectedItem().toString();
			String objectSubType = "--Select";//objectSubType_ComboBox.getSelectedItem().toString();
			String objectName = getObjectName(ele);
			HashMap<String, String> allCSS = new HashMap<String, String>();
			HashMap<String, String> actualCSS = new HashMap<String, String>();
			HashMap<String, String> expectedCSS = new HashMap<String, String>();
			String status = "Not Run";
			String comments = "";
			highlightObject(objectXpath, false);
			BufferedImage screenshot = takeScreenshot(ele);

			for (String css : allCSSList) {
				allCSS.put(css, ele.getCssValue(css));
			}

			addRow(pageTitle, pageURL, objectType, objectSubType, objectName, objectXpath, allCSS, actualCSS, expectedCSS, status, comments, screenshot);
		} catch (UnhandledAlertException | NoAlertPresentException | ClassCastException e) {
			//Do Nothing
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	/** Generate object name
	 * @param ele
	 * @return String
	 */
	public String getObjectName(WebElement ele) {

		String objectName = "";
		if(ele.getText().length() > 0) {
			objectName = "text_"+ele.getText().trim();
		}
		else if (ele.getAttribute("id").length() > 0) {
			objectName = "id_"+ele.getAttribute("id").trim();
		}
		else if (ele.getAttribute("class").length() > 0) {
			objectName = "class_"+ele.getAttribute("class").trim();
		}
		else {
			objectName = "Object_"+(elementID++);
		}
		return objectName;
	}

	/** Method to format CSS value
	 * @param cssProperty
	 * @param cssValue
	 * @return
	 */
	public String formatCSSValue(String cssProperty, String cssValue) {

		// Resolves the color formatting issue
		if (cssProperty.contains("color")) {
			if (cssValue.startsWith("rgba")) {
				cssValue = cssValue.replaceAll("rgba", "rgb");
				cssValue = cssValue.substring(0, cssValue.length()-4)+")";
			}
		}
		// Resolves the font-family formatting issue
		else if (cssProperty.equalsIgnoreCase("font-family")) {
			cssValue = cssValue.replaceAll(", ", ",");
			cssValue = cssValue.replaceAll("\"", "");
		}

		return cssValue;
	}

	/** Highlight and views the WebElement
	 * @param element
	 */
	public void enableHighlight(WebElement element, boolean scrollToView) {

		JavascriptExecutor js = (JavascriptExecutor) driver;
		try {
			js.executeScript("arguments[0].style.border='2px solid green'", element);
			if (scrollToView) {
				js.executeScript("arguments[0].scrollIntoView(true)",element);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/** Disable the element highlighting
	 * @param element
	 */
	public void disableHighlight(WebElement element) {

		JavascriptExecutor js = (JavascriptExecutor) driver;
		try {
			js.executeScript("arguments[0].style.border=''", element);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/** Runs the Element Inspector
	 * @param driver
	 */
	public void runInpector(WebDriver driver) {

		try {
			InputStream is = getClass().getResourceAsStream(jsFilePath);
			Scanner scanner = new Scanner(is);
			String content = scanner.useDelimiter("\\Z").next();
			scanner.close();
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript(content);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	/** Add a Row in the Table
	 * @param capability
	 * @param value
	 * @param bool
	 */
	public void addRow(String pageTitle, String pageURL, String objectType, String objectSubType, 
			String objectName, String objectXpath, HashMap<String, String> allCSS, 
			HashMap<String, String> actualCSS, HashMap<String, String> expectedCSS, String status, String comments, BufferedImage screenshot) {

		tableData.add(new TableData(0, pageTitle, pageURL, objectType, objectSubType, objectName, objectXpath, allCSS, actualCSS, expectedCSS, status, comments, screenshot));
	}

	/**
	 * Remove the selected row from the table
	 */
	public void removeRow() {

		if (table.getSelectedRowCount() > 0) {
			tableData.remove(table.getSelectedRow());
		}	
	}

	/**
	 * Remove all row
	 */
	public void removeAllRow() {
		for (int i = 0; i < tableData.size(); i++) {
			tableData.remove(i);
		}
	}

	/**
	 * Method to display row details
	 */
	public void displayRowDetails() {

		DisplayDetails details = new DisplayDetails(tableData.get(table.getSelectedRow()));
		details.setVisible(true);
	}
	
	/**
	 * Highlight object from the selected row
	 */
	public void highlightObject(String xpath, boolean scrollToView) {

		try {
			driver.switchTo().alert().dismiss();
		} catch (Exception e) {}

		if (previousElements[0] != null) {
			driver.switchTo().defaultContent();
			try {
				driver.switchTo().frame(previousElements[0]);
			} catch (StaleElementReferenceException e) {
				System.out.println(e.getMessage());
			}
		}
		else {
			driver.switchTo().defaultContent();
		}
		if (previousElements[1] != null) {
			disableHighlight(previousElements[1]);
		}

		currentElements = findObject(xpath);

		if (currentElements[0] != null) {
			driver.switchTo().defaultContent();
			driver.switchTo().frame(currentElements[0]);
		}
		else {
			driver.switchTo().defaultContent();
		}
		if (currentElements[1] != null) {
			enableHighlight(currentElements[1], scrollToView);
		}
		else {
			JOptionPane.showMessageDialog(panel_Inspector, "Object Not Found.");
		}
		previousElements = currentElements;

	}

	/** Get WebElement from xpath
	 * @param objectXpath
	 * @return WebElement
	 */
	public WebElement getWebElement(String objectXpath) {

		driver.switchTo().defaultContent();
		List<WebElement> finalElementList = new ArrayList<WebElement>();
		List<WebElement> elementList = driver.findElements(By.xpath(objectXpath));
		int count = 0;
		if (elementList.size() == 0) {

			List<WebElement> iframes = driver.findElements(By.xpath("//iframe | //frame"));
			for (WebElement iframe : iframes) {

				driver.switchTo().frame(iframe);
				elementList = driver.findElements(By.xpath(objectXpath));
				if (elementList.size() > 0) {

					finalElementList = elementList;
					count++;
				}
			}
		}
		else {

			finalElementList = elementList;
			count++;
		}

		if (count > 0 ) {
			return finalElementList.get(0);
		}
		else {
			return null;
		}

	}

	/**
	 * Method to connect to the Browser session
	 */
	public void connectBrowser() {

		URL url = null;
		boolean isConnected = false;
		try {
			url = new URL(textField_ServerURL.getText().trim());
			String sessionId = textField_SessionID.getText().trim();
			driver = new AttachedWebDriver2(url, sessionId);

			try {
				if(!driver.getCurrentUrl().contains("no such session")) {
					isConnected = true;
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
				btnInspect.setEnabled(false);
			}

			if (isConnected) {
				JOptionPane.showMessageDialog(panel_Inspector, "Connection Successfull !");
				btnInspect.setEnabled(true);
				if (txtApplicationurl.getText().trim().length() > 5) {
					driver.get(txtApplicationurl.getText().trim());
				}	
			}
			else {
				JOptionPane.showMessageDialog(panel_Inspector, "Connection Failed !");
				btnInspect.setEnabled(false);
			}
		} catch (MalformedURLException e) {
			JOptionPane.showMessageDialog(panel_Inspector, "Invalid URL !");
			System.out.println(e.getMessage());
			btnInspect.setEnabled(false);
		} catch (InvalidArgumentException e) {
			System.out.println(e.getMessage());
		}

	}

	/** Add PopUp to the text area
	 * @param component
	 * @param popup
	 */
	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}

	/** Take Page screenshot
	 * @param element
	 * @return
	 * @throws IOException
	 */
	public BufferedImage takeScreenshot(WebElement element) throws IOException {

		File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		return ImageIO.read(scrFile);
	}

	/**
	 * Method to initiate bean binding with table
	 */
	protected void initDataBindings() {

		tb = SwingBindings.createJTableBinding(UpdateStrategy.READ_WRITE, tableData, table);

		BeanProperty<TableData, String> slP = BeanProperty.create("sl");
		BeanProperty<TableData, String> pageTitleP = BeanProperty.create("pageTitle");
		BeanProperty<TableData, String> objectTypeP = BeanProperty.create("objectType");
		BeanProperty<TableData, String> objectSubTypeP = BeanProperty.create("objectSubType");
		BeanProperty<TableData, String> objectNameP = BeanProperty.create("objectName");
		BeanProperty<TableData, String> statusP = BeanProperty.create("status");

		tb.addColumnBinding(slP).setColumnName("Sl").setEditable(false);
		tb.addColumnBinding(pageTitleP).setColumnName("Page Title").setEditable(false);
		tb.addColumnBinding(objectTypeP).setColumnName("Object Type").setEditable(true);
		tb.addColumnBinding(objectSubTypeP).setColumnName("Object SubType").setEditable(true);
		tb.addColumnBinding(objectNameP).setColumnName("Object Name");
		tb.addColumnBinding(statusP).setColumnName("Status").setEditable(false);

		tb.bind();
		setColumnWidth();
		addObjectTypeCombo();
	}

	/**
	 * Sets Column Width
	 */
	public void setColumnWidth() {

		table.getColumnModel().getColumn(sl_ColID).setPreferredWidth(30);
		table.getColumnModel().getColumn(sl_ColID).setMaxWidth(50);
		table.getColumnModel().getColumn(pageTitle_ColID).setPreferredWidth(50);
		table.getColumnModel().getColumn(status_ColID).setPreferredWidth(70);
		table.getColumnModel().getColumn(status_ColID).setMaxWidth(90);
	}

	/**
	 * Set DropDown for ObjectType and ObjectSubType in the Table 
	 */
	public void addObjectTypeCombo() {

		table.getColumnModel().getColumn(objectType_ColID).setCellEditor(new CustomComboBoxEditor(elementTypeList, baselineList));
		table.getColumnModel().getColumn(objectSubType_ColID).setCellEditor(new CustomComboBoxEditor(elementTypeList, baselineList));
	}

	/** Validate selected data
	 * @param rowIndex
	 */
	public void validateSelectedData(int rowIndex) {

		TableData tableRowData = tableData.get(rowIndex);
		String objectType = tableRowData.getObjectType();
		String objectSubType = tableRowData.getObjectSubType();
		HashMap<String, String> allCSS = tableRowData.getAllCSS();
		HashMap<String, String> actualCSS = new HashMap<String, String>();
		HashMap<String, String> expectedCSS = new HashMap<String, String>();
		List<String> cssList = baselineList.get(objectType).getCSSList();
		HashMap<String, String> baseLineCSS = baselineList.get(objectType).getCSSTableFor(objectSubType);
		String status = tableRowData.getStatus();
		String comments = "";
		int matchCount = 0;

		for (int i = 0; i < cssList.size(); i++) {

			String actualCSSValue = allCSS.get(cssList.get(i)).trim();
			actualCSSValue = formatCSSValue(cssList.get(i), actualCSSValue);
			actualCSS.put(cssList.get(i), actualCSSValue);
			expectedCSS.put(cssList.get(i), baseLineCSS.get(cssList.get(i)));

			if (actualCSSValue.equalsIgnoreCase(baseLineCSS.get(cssList.get(i)))) {
				matchCount++;
			}
			else {
				comments = comments + cssList.get(i) + "\n";
			}
		}
		if (matchCount < cssList.size()) {
			status = "FAIL";
			if (comments.length() > 0) {
				comments = "Not matching CSS are =>\n"+comments;
			}

		}
		else {
			status = "PASS";
		}
		tableRowData.setActualCSS(actualCSS);
		tableRowData.setExpectedCSS(expectedCSS);
		tableRowData.setStatus(status);
		tableRowData.setComments(comments.trim());
		tableData.set(rowIndex, tableRowData);

	}

	/** Find a WebElement for highlighting
	 * @return WebElement[Frame, Element]
	 */
	public WebElement[] findObject(String xpath) {

		WebElement frame_Element = null;
		WebElement curr_Element = null;

		driver.switchTo().defaultContent();
		List<WebElement> elementList = driver.findElements(By.xpath(xpath));

		if (elementList != null) {

			if (elementList.size() ==  0) {

				List<WebElement> iframes = driver.findElements(By.xpath("//iframe | //frame"));
				for (WebElement iframe : iframes) {

					frame_Element = iframe;
					driver.switchTo().frame(iframe);
					List<WebElement> elementList1 = driver.findElements(By.xpath(xpath));
					if (elementList1.size() > 0) {

						curr_Element = elementList1.get(0);
						//enableHighlight(curr_Element, true);
						break;
					}
					driver.switchTo().defaultContent();
				}
			}
			else {
				curr_Element = elementList.get(0);
				//enableHighlight(curr_Element, true);
			}
		}

		return new WebElement[]{frame_Element, curr_Element};
	}
	
	/** Open the file chooser dialog
	 * @return String
	 */
	public String openFileChooser() {
		
		String folderPath = "";
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new java.io.File("."));
		chooser.setDialogTitle("Select a Folder");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);

		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			folderPath = chooser.getSelectedFile().getAbsolutePath();
		}
		
		return folderPath;
	}
	
	/** Move the mouse a particular coordinate
	 * @param x
	 * @param y
	 */
	public void moveMouseTo(int x, int y) {
		try {
			new Robot().mouseMove(x, y);
		} catch (AWTException e) {
			System.out.println(e.getMessage());
		}
	}
}
