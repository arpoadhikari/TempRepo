package appWindow;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.openqa.grid.internal.utils.configuration.StandaloneConfiguration;
import org.openqa.selenium.remote.server.SeleniumServer;

import tools.BrowserCapabilities;
import tools.BrowserFactory;
import tools.CheckForLock;
import tools.ServerStatus;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.border.EtchedBorder;

public class SessionGenerator {

	private JFrame frmSessionGenerator;
	public JTextArea textArea;
	@SuppressWarnings("rawtypes")
	private JComboBox comboBox;
	private JCheckBox chckbxForServer;
	private JCheckBox chckbxAddFirebug;
	private JTextField textField;
	private JLabel lblPort;
	private JCheckBox chckbxEnablePassthrough;
	private JTable table;
	private DefaultTableModel model;
	private JScrollPane scrollPane_table;
	private JScrollPane scrollPane_textArea;

	private static CheckForLock ua = new CheckForLock("SessionGenerator");
	private BrowserFactory factory = new BrowserFactory();
	private String browserName;
	private String defaultBrowser = "Firefox";
	private int dismissDelay = 15000;
	public HashMap<String, String> browserCapabilities;

	private String hostName = "localhost";
	private String serverURL = "";
	private StandaloneConfiguration config = new StandaloneConfiguration();
	private SeleniumServer server;
	private ServerStatus serverStatus = new ServerStatus();

	private JPopupMenu popupMenu_AddRow;
	private JMenuItem mntmAddRow_1;
	private JPopupMenu popupMenu_CopyClearText;
	private JMenuItem mntmCopyText;
	private JMenuItem mntmClearLogs;
	private JButton btnStart;
	private JButton btnStop;

	private final String lookNfeel_Nimbus = "javax.swing.plaf.nimbus.NimbusLookAndFeel";
	private final String lookNfeel_Metal = "javax.swing.plaf.metal.MetalLookAndFeel";
	private final String lookNfeel_Motif = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
	private JButton btnLaunchInspector;

	/**
	 * Launch the application.
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		if (ua.isAppActive()) {
			System.out.println("Application is already running.");
			Thread.sleep(1000);
			System.exit(0);
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {		
					SessionGenerator window = new SessionGenerator();
					window.frmSessionGenerator.setVisible(true);
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @throws UnsupportedLookAndFeelException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	public SessionGenerator() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {

		//UIManager.setLookAndFeel("com.jtattoo.plaf.aluminium.AluminiumLookAndFeel");
		
		UIManager.setLookAndFeel("com.jtattoo.plaf.luna.LunaLookAndFeel");
		
		//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		//UIManager.setLookAndFeel(lookNfeel_Nimbus);
		initialize();
		comboBox.setSelectedItem(defaultBrowser);
		setDefaultTableData(defaultBrowser);	
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initialize() {

		//Main Window ->
		frmSessionGenerator = new JFrame();
		frmSessionGenerator.setIconImage(Toolkit.getDefaultToolkit().getImage(SessionGenerator.class.getResource("/icons/icon.png")));
		frmSessionGenerator.setResizable(false);
		frmSessionGenerator.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		frmSessionGenerator.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				UIManager.put("OptionPane.messageFont", new Font("Segoe UI", Font.PLAIN, 14));
				UIManager.put("OptionPane.buttonFont", new Font("Segoe UI", Font.PLAIN, 14));
				int dialogResult = JOptionPane.showConfirmDialog (frmSessionGenerator, 
						"Would you like to close the browser session ?", 
						"Information", 
						JOptionPane.YES_NO_OPTION);
				if(dialogResult == JOptionPane.YES_OPTION){
					stop();
					frmSessionGenerator.dispose();
				}
				else { 
					frmSessionGenerator.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				} 

			}
		});
		frmSessionGenerator.setTitle("Session Generator v2.0");
		frmSessionGenerator.getContentPane().setFont(new Font("Segoe UI", Font.PLAIN, 14));
		frmSessionGenerator.setBounds(100, 100, 550, 650);
		frmSessionGenerator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmSessionGenerator.getContentPane().setLayout(null);
		frmSessionGenerator.setLocationRelativeTo(null);
		ToolTipManager.sharedInstance().setDismissDelay(dismissDelay);
		//<-----

		//Label for Browser Type Combo Box ->
		JLabel lblChooseABrowser = new JLabel("Choose a Browser :");
		lblChooseABrowser.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lblChooseABrowser.setBounds(40, 15, 119, 25);
		frmSessionGenerator.getContentPane().add(lblChooseABrowser);
		//<-----

		//Combo Box for Browser Type ->
		String[] browsers = {"Firefox", "Chrome", "Internet Explorer", "Edge"};
		comboBox = new JComboBox(browsers);
		comboBox.setToolTipText("<html>\r\n\t<p>\r\n\t\t<font size=\"4\" face=\"Segoe UI\">\r\n\t\t\tSelect a Browser from the Dropdown.\r\n\t\t</font>\r\n\t</p>\r\n</html>");
		comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				browserName = comboBox.getItemAt(comboBox.getSelectedIndex()).toString();
				setDefaultTableData(browserName);
				if (browserName.equalsIgnoreCase("Firefox")) {
					chckbxAddFirebug.setEnabled(true);
				}	
				else {
					chckbxAddFirebug.setEnabled(false);
				}
			}
		});
		comboBox.setBounds(159, 12, 153, 30);
		frmSessionGenerator.getContentPane().add(comboBox);
		//<-----

		//Check box for FireBug and FirePath ->
		chckbxAddFirebug = new JCheckBox("Add Firebug & Firepath");
		chckbxAddFirebug.setToolTipText("<html>\r\n\t<p>\r\n\t\t<font size=\"4\" face=\"Segoe UI\">\r\n\t\t\tInstall Firebug and Firepath in Firefox Session for inspecting WebElements.\r\n\t\t</font>\r\n\t</p>\r\n</html>");
		chckbxAddFirebug.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		chckbxAddFirebug.setBounds(342, 15, 178, 25);
		frmSessionGenerator.getContentPane().add(chckbxAddFirebug);
		//<-----

		//Check box for StandAlone Server ->
		chckbxForServer = new JCheckBox("Use Standalone Server");
		chckbxForServer.setToolTipText("<html>\r\n\t<p>\r\n\t\t<font size=\"4\" face=\"Segoe UI\">\r\n\t\t\tInitiate a Selenium Standalone Server and launch Browsers using it.\r\n\t\t</font>\r\n\t</p>\r\n</html>");
		chckbxForServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (chckbxForServer.isSelected()) {
					lblPort.setEnabled(true);
					textField.setEnabled(true);
					//chckbxEnablePassthrough.setEnabled(true);
				}
				else {
					lblPort.setEnabled(false);
					textField.setEnabled(false);
					//chckbxEnablePassthrough.setEnabled(false);
				}
			}
		});
		chckbxForServer.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		chckbxForServer.setBounds(117, 208, 172, 23);
		frmSessionGenerator.getContentPane().add(chckbxForServer);
		//<-----

		//Label for Port No. ->
		lblPort = new JLabel("Port :");
		lblPort.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lblPort.setBounds(303, 208, 38, 23);
		frmSessionGenerator.getContentPane().add(lblPort);
		lblPort.setEnabled(false);
		//<-----

		//Input box for Port No. ->
		textField = new JTextField();
		textField.setToolTipText("<html>\r\n\t<p>\r\n\t\t<font size=\"4\" face=\"Segoe UI\">\r\n\t\t\tSpecify the Port (default 4444) on which the Server will be running.\r\n\t\t</font>\r\n\t</p>\r\n</html>");
		textField.setText("4444");
		textField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		textField.setBounds(343, 206, 73, 28);
		frmSessionGenerator.getContentPane().add(textField);
		textField.setColumns(10);
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent evt) {
				char vChar = evt.getKeyChar();
				if (!(Character.isDigit(vChar) 
						|| (vChar == KeyEvent.VK_BACK_SPACE) 
						|| (vChar == KeyEvent.VK_DELETE))) 
				{ 
					evt.consume();
				}
			}
		});
		textField.setEnabled(false);
		//<-----

		//Check Box enable/disable PassThrough ->
		/*chckbxEnablePassthrough = new JCheckBox("Enable PassThrough");
		chckbxEnablePassthrough.setToolTipText("<html>\r\n\t<p>\r\n\t\t<font size=\"4\" face=\"Segoe UI\">\r\n\t\t\tWith this in place, and a few tweaks to Grid, it's possible to use a w3c remote end <br/>\r\n\t\t\t(eg. geckodriver) with a w3c speaking local end (eg. a recent 3.x release of selenium).\r\n\t\t</font>\r\n\t</p>\r\n</html>");
		chckbxEnablePassthrough.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		chckbxEnablePassthrough.setBounds(355, 207, 165, 23);
		frmSessionGenerator.getContentPane().add(chckbxEnablePassthrough);
		chckbxEnablePassthrough.setEnabled(false);*/
		//<-----

		//Browser Capability Table ->
		scrollPane_table = new JScrollPane();
		scrollPane_table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		scrollPane_table.setToolTipText("<html>\r\n\t<p>\r\n\t\t<font size=\"4\" face=\"Segoe UI\">\r\n\t\t\tThese are the list of Capabilities which you can set to your Browser session. <br/>\r\n\t\t\tYou can deselect any Capabilities you want. <br/>\r\n\t\t\tAlso you can Add/Remove any Capabilities.\r\n\t\t</font>\r\n\t</p>\r\n</html>");
		scrollPane_table.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		scrollPane_table.setBounds(12, 55, 520, 140);
		frmSessionGenerator.getContentPane().add(scrollPane_table);
		String[] columns = new String[] {
				"Sl.", "Capability", "Value", "Selection"
		};

		Object[][] data = null;

		final Class[] columnClass = new Class[] {
				Integer.class, String.class, String.class, Boolean.class
		};

		model = new DefaultTableModel(data, columns) {

			private static final long serialVersionUID = 1L;
			@Override
			public boolean isCellEditable(int row, int column)
			{
				return column != 0 ? true : false;
			}
			@Override
			public Class<?> getColumnClass(int columnIndex)
			{
				return columnClass[columnIndex];
			}
		};
		table = new JTable(model);
		table.setToolTipText("<html>\r\n\t<p>\r\n\t\t<font size=\"4\" face=\"Segoe UI\">\r\n\t\t\tThese are the list of Capabilities which you can set to your Browser session. <br/>\r\n\t\t\tYou can deselect any Capabilities you want. <br/>\r\n\t\t\tAlso you can Add/Remove any Capabilities.\r\n\t\t</font>\r\n\t</p>\r\n</html>");
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER );
		table.setDefaultRenderer(Integer.class, centerRenderer);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		table.setShowHorizontalLines(true);
		table.setShowVerticalLines(true);
		table.setRowHeight(20);
		table.getTableHeader().setFont(new Font("Segoe UI", Font.PLAIN, 14));
		table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		table.getColumnModel().getColumn(0).setPreferredWidth(30);
		table.getColumnModel().getColumn(1).setPreferredWidth(250);
		table.getColumnModel().getColumn(3).setPreferredWidth(50);
		scrollPane_table.setViewportView(table);
		//<-----

		//PopupMenu Add/Remove Row ->
		JPopupMenu popupMenu_AddRemoveRow = new JPopupMenu();
		popupMenu_AddRemoveRow.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		popupMenu_AddRemoveRow.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		addPopup(table, popupMenu_AddRemoveRow);

		JMenuItem mntmAddRow = new JMenuItem("Add Row");
		mntmAddRow.setIcon(new ImageIcon(SessionGenerator.class.getResource("/icons/AddRow.png")));
		mntmAddRow.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		mntmAddRow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				addRow("", "", false);
			}
		});		
		popupMenu_AddRemoveRow.add(mntmAddRow);

		JMenuItem mntmRemoveRow = new JMenuItem("Remove Row");
		mntmRemoveRow.setIcon(new ImageIcon(SessionGenerator.class.getResource("/icons/DelRow.png")));
		mntmRemoveRow.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		mntmRemoveRow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeRow();
			}
		});
		popupMenu_AddRemoveRow.add(mntmRemoveRow);
		//<-----

		//PopupMenu Add Row ->
		popupMenu_AddRow = new JPopupMenu();
		popupMenu_AddRow.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		popupMenu_AddRow.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		addPopup(scrollPane_table, popupMenu_AddRow);

		mntmAddRow_1 = new JMenuItem("Add Row");
		mntmAddRow_1.setIcon(new ImageIcon(SessionGenerator.class.getResource("/icons/AddRow.png")));
		mntmAddRow_1.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		mntmAddRow_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				addRow("", "", false);
			}
		});		
		popupMenu_AddRow.add(mntmAddRow_1);
		//<-----

		//Text Area with Scroll Pane ->
		scrollPane_textArea = new JScrollPane();
		scrollPane_textArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		scrollPane_textArea.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		scrollPane_textArea.setBounds(12, 240, 520, 280);
		frmSessionGenerator.getContentPane().add(scrollPane_textArea);

		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane_textArea.setViewportView(textArea);
		textArea.setAutoscrolls(true);
		textArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		textArea.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));

		popupMenu_CopyClearText = new JPopupMenu();
		popupMenu_CopyClearText.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		popupMenu_CopyClearText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		addPopup(textArea, popupMenu_CopyClearText);

		mntmCopyText = new JMenuItem("Copy Text");
		mntmCopyText.setIcon(new ImageIcon(SessionGenerator.class.getResource("/icons/Copy.png")));
		mntmCopyText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StringSelection stringSelection = new StringSelection(textArea.getSelectedText());
				Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
				clpbrd.setContents(stringSelection, null);
			}
		});
		mntmCopyText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		popupMenu_CopyClearText.add(mntmCopyText);

		mntmClearLogs = new JMenuItem("Clear Logs");
		mntmClearLogs.setIcon(new ImageIcon(SessionGenerator.class.getResource("/icons/ClearLogs.png")));
		mntmClearLogs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea.setText(null);
			}
		});

		mntmClearLogs.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		popupMenu_CopyClearText.add(mntmClearLogs);	
		//<-----

		//Start Button ->
		btnStart = new JButton("Start");
		btnStart.setToolTipText("<html>\r\n\t<p>\r\n\t\t<font size=\"4\" face=\"Segoe UI\">\r\n\t\t\tStart a Browser session.\r\n\t\t</font>\r\n\t</p>\r\n</html>");
		btnStart.setIcon(new ImageIcon(SessionGenerator.class.getResource("/icons/Start.png")));
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new Thread(new Runnable() {
					public void run() {
						btnStart.setEnabled(false);
						btnStop.setEnabled(false);
						btnLaunchInspector.setEnabled(false);
						start();
						btnStart.setEnabled(true);
						btnStop.setEnabled(true);
						btnLaunchInspector.setEnabled(true);
					}
				}).start();
			}
		});
		btnStart.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		btnStart.setBounds(56, 527, 95, 30);
		frmSessionGenerator.getContentPane().add(btnStart);
		//<-----

		//Stop Button ->
		btnStop = new JButton("Stop");
		btnStop.setToolTipText("<html>\r\n\t<p>\r\n\t\t<font size=\"4\" face=\"Segoe UI\">\r\n\t\t\tStop the Browser session.\r\n\t\t</font>\r\n\t</p>\r\n</html>");
		btnStop.setIcon(new ImageIcon(SessionGenerator.class.getResource("/icons/Stop.png")));
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					public void run() {
						btnStart.setEnabled(false);
						btnStop.setEnabled(false);
						btnLaunchInspector.setEnabled(false);
						stop();
						btnStart.setEnabled(true);
						btnStop.setEnabled(true);
						btnLaunchInspector.setEnabled(true);
					}
				}).start();
			}
		});
		btnStop.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		btnStop.setBounds(393, 528, 95, 30);
		frmSessionGenerator.getContentPane().add(btnStop);
		//<-----

		//UI Inspector Button ->
		btnLaunchInspector = new JButton("UI Inspector");
		btnLaunchInspector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				if (!Inspector.isRunning) {
					try {
						if (factory.getDriver() != null) {
							Inspector inspector = new Inspector(factory.getLocalServerURL(), factory.getSessionId());
							if (!inspector.isVisible()) {
								inspector.setVisible(true);
							}
						}
						else {
							Inspector inspector = new Inspector(null, "");
							if (!inspector.isVisible()) {
								inspector.setVisible(true);
							}
						}
					} catch (Exception e) {
						JOptionPane.showMessageDialog(frmSessionGenerator, "Unable to launch the UI Inspector.\n"+e.getMessage());
					}
				}
				else {
					JOptionPane.showMessageDialog(frmSessionGenerator, "Already Running !");
				}
			}
		});
		btnLaunchInspector.setIcon(new ImageIcon(SessionGenerator.class.getResource("/icons/inspector.png")));
		btnLaunchInspector.setToolTipText("<html>\r\n\t<p>\r\n\t\t<font size=\"4\" face=\"Segoe UI\">\r\n\t\t\tLaunches the UI Inspector\r\n\t\t</font>\r\n\t</p>\r\n</html>");
		btnLaunchInspector.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		btnLaunchInspector.setBounds(207, 527, 130, 30);
		frmSessionGenerator.getContentPane().add(btnLaunchInspector);
		//<-----
		
		//Initializes the Panel to set the border
		JPanel panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		panel.setBounds(5, 5, 534, 563);
		frmSessionGenerator.getContentPane().add(panel);
		//<-----
		
		//Initializes the Logo
		JLabel lblLogo = new JLabel("");
		lblLogo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lblLogo.setBounds(5, 575, 77, 42);
		ImageIcon logoIcon = new ImageIcon(SessionGenerator.class.getResource("/icons/logo.png"));
		Image logoImg = logoIcon.getImage();
		logoImg = logoImg.getScaledInstance(lblLogo.getWidth(), lblLogo.getHeight(), Image.SCALE_SMOOTH);
		lblLogo.setIcon(new ImageIcon(logoImg));
		frmSessionGenerator.getContentPane().add(lblLogo);
		//<-----

		//Initializes the Close button
		JButton btnClose = new JButton("Close");
		btnClose.setIconTextGap(2);
		btnClose.setMargin(new Insets(2, 2, 2, 2));
		btnClose.setToolTipText("<html>\r\n\t<p>\r\n\t\t<font size=\"4\" face=\"Segoe UI\">\r\n\t\t\tClose the Application.\r\n\t\t</font>\r\n\t</p>\r\n</html>");
		btnClose.setIcon(new ImageIcon(Inspector.class.getResource("/icons/close.png")));
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				UIManager.put("OptionPane.messageFont", new Font("Segoe UI", Font.PLAIN, 14));
				UIManager.put("OptionPane.buttonFont", new Font("Segoe UI", Font.PLAIN, 14));
				int dialogResult = JOptionPane.showConfirmDialog (frmSessionGenerator, 
						"Would you like to close the browser session ?", 
						"Information", 
						JOptionPane.YES_NO_OPTION);
				if(dialogResult == JOptionPane.YES_OPTION){
					stop();
					frmSessionGenerator.dispose();
				}
				else { 
					frmSessionGenerator.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				} 
			}
		});
		btnClose.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		btnClose.setBounds(224, 580, 95, 30);
		frmSessionGenerator.getContentPane().add(btnClose);
		//<-----
	}

	/** Method for Starting StandAlone Server
	 * @throws Exception 
	 */
	private void startServer() throws Exception {
		setServerConfiguration();
		if(serverStatus.get(serverURL).equalsIgnoreCase("OK")) {
			textArea.append("Server is already RUNNING.\n");
		}
		else {
			textArea.append("Trying to start the server.\n");
			server = new SeleniumServer(config);
			server.boot();
			textArea.append("Server is STARTED Now.\n");
		}
	}

	/**  Method for Stopping StandAlone Server
	 * @throws IOException
	 */
	private void stopServer() throws IOException {
		if (!serverURL.equalsIgnoreCase("")) {
			if(serverStatus.get(serverURL).equalsIgnoreCase("OK")) {	
				server.stop();
				serverURL = "";
				textArea.append("Server is STOPPED now.\n");	
			}
			else {
				textArea.append("Server is Not Running.\n");
			}
		}
	}

	/**
	 *  Set the StandAlone Server Configuration
	 * @throws Exception 
	 */
	private void setServerConfiguration() throws Exception {
		int portNo = Integer.parseInt(textField.getText());
		hostName = "localhost";
		if (serverStatus.isFreePort(hostName, portNo) == true) {
			config.port = portNo;
		}
		else {
			textArea.append("Port No. : "+portNo+ " is blocked/invalid.\n");
			throw new Exception();
		}
		config.enablePassThrough = false;//chckbxEnablePassthrough.isSelected();
		config.debug = true;
		serverURL = "http://"+hostName+":"+config.port+"/wd/hub";	
	}

	/**
	 * Method for starting browser session
	 */
	private void start() {
		try {
			if (chckbxForServer.isSelected() && factory.getDriver() == null) {
				startServer();
			}
			textArea.append("Trying to launch the browser.\n");
			textArea.append(factory.openBrowser(serverURL, comboBox.getSelectedItem().toString(), getCapabilitiesFromTable(), chckbxAddFirebug.isSelected()));
			textArea.append("Local Server URL : "+factory.getLocalServerURL()+"\n");

			textArea.append("Session ID : "+factory.getSessionId()+"\n");
		} catch (Exception e1) {
			textArea.append("ERROR while trying to start the server/browser : "+e1.getMessage()+"\n");
		}
	}

	/**
	 *  Method for closing browser session
	 */
	private void stop() {	
		try {
			textArea.append(factory.closeBrowser());
			stopServer();
		} catch (Exception e1) {
			textArea.append("ERROR while trying to close the server/browser : "+e1.getMessage()+"\n");
		}
	}

	/** Set the capabilities in the table
	 * @param browser
	 */
	private void setDefaultTableData(String browser) {
		if(browser.equalsIgnoreCase("Firefox")) {
			removeAllRow();
			for (Map.Entry<String, String> kv: BrowserCapabilities.firefox.entrySet()) {
				String capability = kv.getKey();
				String value = kv.getValue();
				if (capability != null && capability != "" && value != null && value != "") {
					if (capability.equalsIgnoreCase("marionette")) {
						addRow(capability, value, false);
					}
					else {
						addRow(capability, value, true);
					}
				}
			}
		}
		else if (browser.equalsIgnoreCase("Chrome")) {
			removeAllRow();
			for (Map.Entry<String, String> kv: BrowserCapabilities.chrome.entrySet()) {
				String capability = kv.getKey();
				String value = kv.getValue();
				if (capability != null && capability != "" && value != null && value != "") {
					addRow(capability, value, true);
				}
			}
		}
		else if (browser.equalsIgnoreCase("Internet Explorer")) {
			removeAllRow();
			for (Map.Entry<String, String> kv: BrowserCapabilities.ie.entrySet()) {
				String capability = kv.getKey();
				String value = kv.getValue();
				if (capability != null && capability != "" && value != null && value != "") {
					addRow(capability, value, true);
				}
			}
		}
		else if (browser.equalsIgnoreCase("Edge")) {
			removeAllRow();
			for (Map.Entry<String, String> kv: BrowserCapabilities.edge.entrySet()) {
				String capability = kv.getKey();
				String value = kv.getValue();
				if (capability != null && capability != "" && value != null && value != "") {
					addRow(capability, value, true);
				}
			}
		}
	}

	/** Get the capabilities from the table
	 * @return HashMap
	 */
	private HashMap<String, String> getCapabilitiesFromTable() {	
		browserCapabilities = new HashMap<String, String>();
		for(int i = 0; i < table.getRowCount(); i++) {
			String capability = table.getModel().getValueAt(i, 1).toString().trim();
			String value = table.getModel().getValueAt(i, 2).toString().trim();
			Boolean selection = (Boolean) table.getModel().getValueAt(i, 3);
			if (capability != null && capability != "" && value != null && value != "" && selection == true) {
				browserCapabilities.put(capability, value);
			}
		}
		return browserCapabilities;
	}

	/** Add a Row in the Table
	 * @param capability
	 * @param value
	 * @param bool
	 */
	public void addRow(String capability, String value, Boolean bool) {
		model.addRow(new Object[]{null, capability, value, bool});
		setSequence();
	}

	/**
	 * Remove the selected row from the table
	 */
	public void removeRow() {
		if (table.getSelectedRowCount() > 0) {
			model.removeRow(table.getSelectedRow());
			setSequence();
		}	
	}

	/**
	 * Set the row sequence
	 */
	public void setSequence() {	
		for(int i = 0; i < table.getRowCount(); i++) {
			model.setValueAt(i+1, i, 0);
		}
	}

	/**
	 * Remove all row
	 */
	public void removeAllRow() {
		if (model.getRowCount() > 0) {
			for (int i = model.getRowCount() - 1; i > -1; i--) {
				model.removeRow(i);
			}
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
}
