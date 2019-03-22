package appWindow;



import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import tools.customDriver.AttachedWebDriver2;

public class Inspector extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String jsFilePath = "/resources/inspector.js";

	private JPanel panel_Connector;
	private JPanel panel_Inspector;
	private JTextField textField_LocatorValue;
	private JTextField textField_ServerURL;
	private JTextField textField_SessionID;
	private JTextPane textPane_Element;
	private JTextPane textPane_Frame;
	private JButton btnValidate;
	private JComboBox<String> comboBox_LocatorType;
	private JLabel labelElementCount;

	private WebElement[] currentElements = new WebElement[] {null, null};
	private WebElement[] previousElements = new WebElement[] {null, null};

	private static WebDriver driver;
	private JButton btnInspect;

	/**
	 * Launch the application.
	 */
	 public static void main(String[] args) {
		 try {
			 Inspector dialog = new Inspector(new URL("http://localhost:21956"), "");
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
		 
		 setResizable(false);
		 
		 //Initialize the window with the settings ->
		 getContentPane().setFont(new Font("Segoe UI", Font.PLAIN, 14));
		 setFont(new Font("Segoe UI", Font.PLAIN, 14));
		 setTitle("Object Spy");
		 setIconImage(Toolkit.getDefaultToolkit().getImage(Inspector.class.getResource("/icons/inspector.png")));
		 setModalityType(ModalityType.APPLICATION_MODAL);
		 setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		 setBounds(100, 100, 700, 500);
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
		 panel_Connector.setBounds(10, 10, 674, 70);
		 getContentPane().add(panel_Connector);
		 panel_Connector.setLayout(null);
		 //<-----

		 //Initializes the ServerURL input box with the label
		 JLabel lblServerURL = new JLabel("Server URL :");
		 lblServerURL.setBounds(10, 0, 110, 23);
		 lblServerURL.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		 panel_Connector.add(lblServerURL);

		 textField_ServerURL = new JTextField();
		 textField_ServerURL.setToolTipText("<html>\r\n\t<p>\r\n\t\t<font size=\"4\" face=\"Segoe UI\">\r\n\t\t\tInput the Selenium Server URL.\r\n\t\t</font>\r\n\t</p>\r\n</html>");
		 textField_ServerURL.setBounds(10, 22, 200, 35);
		 textField_ServerURL.setFont(new Font("Segoe UI", Font.PLAIN, 14));
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
		 lblSessionId.setBounds(220, 0, 110, 23);
		 lblSessionId.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		 panel_Connector.add(lblSessionId);

		 textField_SessionID = new JTextField();
		 textField_SessionID.setToolTipText("<html>\r\n\t<p>\r\n\t\t<font size=\"4\" face=\"Segoe UI\">\r\n\t\t\tInput the browser session ID.\r\n\t\t</font>\r\n\t</p>\r\n</html>");
		 textField_SessionID.setBounds(220, 22, 310, 35);
		 textField_SessionID.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		 textField_SessionID.setColumns(10);
		 panel_Connector.add(textField_SessionID);
		 textField_SessionID.setText(sessionID);
		 //<-----

		 //Initializes the Connect button and performs the Connection check
		 JButton btnConnect = new JButton("Connect");
		 btnConnect.setToolTipText("<html>\r\n\t<p>\r\n\t\t<font size=\"4\" face=\"Segoe UI\">\r\n\t\t\tConnect to an existing browser session.\r\n\t\t</font>\r\n\t</p>\r\n</html>");
		 btnConnect.setIcon(new ImageIcon(Inspector.class.getResource("/icons/connect.png")));
		 btnConnect.setMargin(new Insets(2, 10, 2, 10));
		 btnConnect.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent arg0) {

				 connectBrowser();
			 }
		 });
		 btnConnect.setBounds(540, 22, 122, 35);
		 btnConnect.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		 panel_Connector.add(btnConnect);
		 //<-----

		 //Initialize the JPanel Containing the controls which finds the elements ->
		 panel_Inspector = new JPanel();
		 panel_Inspector.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		 panel_Inspector.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		 panel_Inspector.setBounds(10, 87, 674, 328);
		 getContentPane().add(panel_Inspector);
		 panel_Inspector.setLayout(null);
		 //<-----

		 //Initializes the LocatorType ComboBox
		 comboBox_LocatorType = new JComboBox<String>();
		 comboBox_LocatorType.setToolTipText("<html>\r\n\t<p>\r\n\t\t<font size=\"4\" face=\"Segoe UI\">\r\n\t\t\tChoose locator type.\r\n\t\t</font>\r\n\t</p>\r\n</html>");
		 comboBox_LocatorType.setBounds(10, 64, 125, 35);
		 panel_Inspector.add(comboBox_LocatorType);
		 comboBox_LocatorType.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		 comboBox_LocatorType.setModel(new DefaultComboBoxModel<String>(new String[] {"xpath", "tagName", "partialLinkText", "name", "linkText", "id", "cssSelector", "className"}));
		 //<-----

		 //Initializes the LocatorValue Input box
		 textField_LocatorValue = new JTextField();
		 textField_LocatorValue.setToolTipText("<html>\r\n\t<p>\r\n\t\t<font size=\"4\" face=\"Segoe UI\">\r\n\t\t\tInput locator value.\r\n\t\t</font>\r\n\t</p>\r\n</html>");
		 textField_LocatorValue.setBounds(145, 64, 385, 35);
		 panel_Inspector.add(textField_LocatorValue);
		 textField_LocatorValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		 textField_LocatorValue.setColumns(10);
		 //<-----

		 //Initializes the controls to display the element details
		 JLabel lblElementDetails = new JLabel("Element Details :");
		 lblElementDetails.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		 lblElementDetails.setBounds(10, 202, 110, 23);
		 panel_Inspector.add(lblElementDetails);

		 JScrollPane scrollPane_Element = new JScrollPane();
		 scrollPane_Element.setBounds(10, 226, 654, 91);
		 panel_Inspector.add(scrollPane_Element);

		 textPane_Element = new JTextPane();
		 textPane_Element.addKeyListener(new KeyAdapter() {
		 	@Override
		 	public void keyReleased(KeyEvent event) {
		 		if(event.getKeyCode() == KeyEvent.VK_DELETE) {
		 			textPane_Element.setText("");
		 		}
		 	}
		 });
		 textPane_Element.setEditable(false);
		 scrollPane_Element.setViewportView(textPane_Element);
		 textPane_Element.setFont(new Font("Courier New", Font.PLAIN, 12));
		 textPane_Element.setBorder(UIManager.getBorder("TextField.border"));
		 //<-----

		 //Initializes the controls to display the frame details
		 JLabel lblFrameDetails = new JLabel("Frame Details :");
		 lblFrameDetails.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		 lblFrameDetails.setBounds(10, 102, 110, 23);
		 panel_Inspector.add(lblFrameDetails);

		 JScrollPane scrollPane_Frame = new JScrollPane();
		 scrollPane_Frame.setBounds(10, 126, 654, 65);
		 panel_Inspector.add(scrollPane_Frame);

		 textPane_Frame = new JTextPane();
		 textPane_Frame.addKeyListener(new KeyAdapter() {
		 	@Override
		 	public void keyReleased(KeyEvent event) {
		 		if(event.getKeyCode() == KeyEvent.VK_DELETE) {
		 			textPane_Frame.setText("");
		 		}
		 	}
		 });
		 textPane_Frame.setEditable(false);
		 scrollPane_Frame.setViewportView(textPane_Frame);
		 textPane_Frame.setFont(new Font("Courier New", Font.PLAIN, 12));
		 textPane_Frame.setBorder(UIManager.getBorder("TextField.border"));
		 //<-----

		 //Initializes the Find Element button
		 btnValidate = new JButton("Validate");
		 btnValidate.setToolTipText("<html>\r\n\t<p>\r\n\t\t<font size=\"4\" face=\"Segoe UI\">\r\n\t\t\tVisually validate an element and disaplay it's details.\r\n\t\t</font>\r\n\t</p>\r\n</html>");
		 btnValidate.setMargin(new Insets(2, 10, 2, 10));
		 btnValidate.setIcon(new ImageIcon(Inspector.class.getResource("/icons/validate.png")));
		 btnValidate.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent arg0) {
				 
				 textPane_Element.setText("");
				 textPane_Frame.setText("");
				 labelElementCount.setText("");
				 
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
				 
				 currentElements = findObject();

				 if (currentElements[0] != null) {
					 driver.switchTo().defaultContent();
					 driver.switchTo().frame(currentElements[0]);
				 }
				 else {
					 driver.switchTo().defaultContent();
				 }
				 if (currentElements[1] != null) {
					 enableHighlight(currentElements[1]);
				 }
				 previousElements = currentElements;
			 }
		 });
		 btnValidate.setBounds(540, 64, 122, 35);
		 btnValidate.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		 panel_Inspector.add(btnValidate);
		 btnValidate.setEnabled(false);

		 btnInspect = new JButton("Inspect");
		 btnInspect.setToolTipText("<html>\r\n\t<p>\r\n\t\t<font size=\"4\" face=\"Segoe UI\">\r\n\t\t\tMouseover to any object in web page to inspect &\r\n\t\t\t<br/>\r\n\t\t\tCtrl+C to capture the Xpath\r\n\t\t</font>\r\n\t</p>\r\n</html>");
		 btnInspect.setIcon(new ImageIcon(Inspector.class.getResource("/icons/inspector.png")));
		 btnInspect.setMargin(new Insets(2, 10, 2, 10));
		 btnInspect.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent e) {
				 JOptionPane.showMessageDialog(panel_Inspector, "Please go to the browser to inpect.");
				 driver.switchTo().defaultContent();
				 runInpector(driver);
				 List<WebElement> iframes = driver.findElements(By.xpath("//iframe | //frame"));
				 for (WebElement iframe : iframes) {
					 driver.switchTo().frame(iframe);
					 runInpector(driver);
					 driver.switchTo().defaultContent();
				 }
			 }
		 });
		 btnInspect.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		 btnInspect.setEnabled(false);
		 btnInspect.setBounds(276, 11, 122, 40);
		 panel_Inspector.add(btnInspect);

		 labelElementCount = new JLabel("");
		 labelElementCount.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		 labelElementCount.setBounds(124, 202, 285, 23);
		 panel_Inspector.add(labelElementCount);
		 //<-----

		 //Initializes the Close button
		 JButton btnClose = new JButton("Close");
		 btnClose.setToolTipText("<html>\r\n\t<p>\r\n\t\t<font size=\"4\" face=\"Segoe UI\">\r\n\t\t\tClose the Object Spy.\r\n\t\t</font>\r\n\t</p>\r\n</html>");
		 btnClose.setIcon(new ImageIcon(Inspector.class.getResource("/icons/close.png")));
		 btnClose.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent arg0) {
				 dispose();
			 }
		 });
		 btnClose.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		 btnClose.setBounds(299, 426, 95, 35);
		 getContentPane().add(btnClose);
		 //<-----

	 }

	 /** Highlight and views the WebElement
	  * @param element
	  */
	 public void enableHighlight(WebElement element) {

		 JavascriptExecutor js = (JavascriptExecutor) driver;
		 try {
			 js.executeScript("arguments[0].style.border='2px solid green'", element);
			 js.executeScript("arguments[0].scrollIntoView(true)",element);
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

	 /** Find a WebElement and display the details
	  * @return WebElement[Frame, Element]
	  */
	 public WebElement[] findObject() {

		 WebElement frame_Element = null;
		 WebElement curr_Element = null;
		 List<WebElement> foundElements = new ArrayList<WebElement>();
		 String elementCode = "";
		 String frameCode = "";
		 String locatorType = comboBox_LocatorType.getSelectedItem().toString();
		 String locatorValue = textField_LocatorValue.getText();
		 
		 driver.switchTo().defaultContent();
		 List<WebElement> elementList = getWebElements(locatorType, locatorValue);
		 int count = 0;
		 
		 if (elementList != null) {
			 if (elementList.size() ==  0) {
				 List<WebElement> iframes = driver.findElements(By.xpath("//iframe | //frame"));
				 for (WebElement iframe : iframes) {
					 frame_Element = iframe;
					 frameCode = (String)((JavascriptExecutor)driver).executeScript("return arguments[0].outerHTML;", iframe);
					 driver.switchTo().frame(iframe);
					 List<WebElement> elementList1 = getWebElements(locatorType, locatorValue);
					 if (elementList1.size() > 0) {
						 count++;
						 foundElements = elementList1;
						 curr_Element = elementList1.get(0);

						 elementCode = (String)((JavascriptExecutor)driver).executeScript("return arguments[0].outerHTML;", curr_Element);
						 enableHighlight(curr_Element);
						 break;
					 }
					 driver.switchTo().defaultContent();
				 }
			 }
			 else {
				 count++;
				 foundElements = elementList;
				 curr_Element = elementList.get(0);
				 frameCode = "The element isn't inside an iframe";
				 elementCode = (String)((JavascriptExecutor)driver).executeScript("return arguments[0].outerHTML;", curr_Element);
				 enableHighlight(curr_Element);
			 }
		 }
		 
		 if (count == 0) {
			 frameCode = "";
			 elementCode = "No element found";
		 }
		 textPane_Frame.setText(frameCode);
		 textPane_Element.setText(elementCode);
		 if (curr_Element != null) {
			 labelElementCount.setText("Showing 1 of "+foundElements.size());
		 }
		 return new WebElement[]{frame_Element, curr_Element};

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
				 btnValidate.setEnabled(false);
			 }

			 if (isConnected) {
				 JOptionPane.showMessageDialog(panel_Inspector, "Connection Successfull !");
				 btnInspect.setEnabled(true);
				 btnValidate.setEnabled(true);
			 }
			 else {
				 JOptionPane.showMessageDialog(panel_Inspector, "Connection Failed !");
				 btnInspect.setEnabled(false);
				 btnValidate.setEnabled(false);
			 }
		 } catch (MalformedURLException e) {
			 JOptionPane.showMessageDialog(panel_Inspector, "Invalid URL !");
			 System.out.println(e.getMessage());
			 btnInspect.setEnabled(false);
			 btnValidate.setEnabled(false);
		 }

	 }

	 /** Method to return list of WebLements
	  * @param locatorType
	  * @param locatorValue
	  * @return
	  */
	 public List<WebElement> getWebElements(String locatorType, String locatorValue) {
		 
		 List<WebElement> webElements = null;
		 try {
			 switch (locatorType) {
			 case "xpath":
				 webElements = driver.findElements(By.xpath(locatorValue));
				 break;

			 case "tagName":
				 webElements = driver.findElements(By.tagName(locatorValue));
				 break;

			 case "partialLinkText":
				 webElements = driver.findElements(By.partialLinkText(locatorValue));
				 break;

			 case "name":
				 webElements = driver.findElements(By.name(locatorValue));
				 break;

			 case "linkText":
				 webElements = driver.findElements(By.linkText(locatorValue));
				 break;

			 case "id":
				 webElements = driver.findElements(By.id(locatorValue));
				 break;

			 case "cssSelector":
				 webElements = driver.findElements(By.cssSelector(locatorValue));
				 break;

			 case "className":
				 webElements = driver.findElements(By.className(locatorValue));
				 break;

			 }
		 } catch (Exception e) {
			 System.out.println(e.getMessage());
		 }
		 return webElements;
	 }
	 
}
