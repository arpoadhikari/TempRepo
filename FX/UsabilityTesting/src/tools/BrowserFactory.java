package tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 *  This class works with the WebDriver object
 * @author Arpo Adhikari
 *
 */
public class BrowserFactory {

	private WebDriver driver = null;

	/** Setter method of driver
	 * @param driver
	 */
	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}

	/** Getter method of driver
	 * @return WebDriver
	 */
	public WebDriver getDriver() {
		return driver;
	}

	/**
	 * Return Session Id of the browserSession
	 * @return Session Id as string
	 */
	public String getSessionId() {
		String sessionId = ((RemoteWebDriver) driver).getSessionId().toString();
		return sessionId;
	}

	/** Get the local server URL on which WebDriver is running
	 * @return URL of the server
	 */
	public URL getLocalServerURL() {
		HttpCommandExecutor executor = (HttpCommandExecutor) ((RemoteWebDriver) driver).getCommandExecutor();
		URL url = executor.getAddressOfRemoteServer();
		return url;
	}

	/**
	 * Open the Browser
	 * @param serverURL
	 * @param browserName
	 * @param capsToBeSet
	 * @return String (Logs)
	 * @throws URISyntaxException 
	 * @throws IOException 
	 */
	public String openBrowser(String serverURL, String browserName, HashMap<String, String> capsToBeSet, Boolean addFirebugFirepath) throws URISyntaxException, IOException {

		String msg = "";
		String sysArch = System.getProperty("os.arch");
		String chromedriverPath ;
		String iedriverPath;
		String geckodriverPath;
		String edgedriverPath;
		DesiredCapabilities capabilities;

		chromedriverPath = "binaries/chromedriver.exe";
		edgedriverPath = "binaries/MicrosoftWebDriver.exe";

		if (sysArch.endsWith("64")) {
			geckodriverPath = "binaries/x64/geckodriver.exe";
			iedriverPath = "binaries/x64/IEDriverServer.exe";
		}
		else {
			geckodriverPath = "binaries/x86/geckodriver.exe";
			iedriverPath = "binaries/x86/IEDriverServer.exe";
		}

		if (driver == null) {
			switch (browserName) {
			case "Firefox":
				capabilities = DesiredCapabilities.firefox();
				capabilities = setCapFromUI(capabilities, capsToBeSet);
				FirefoxProfile firefoxProfile = new FirefoxProfile();
				if (addFirebugFirepath == true) {
					firefoxProfile = installFirebug(firefoxProfile);
					System.out.println("Firebug installation succefull");
					firefoxProfile = installFirepath(firefoxProfile);
					System.out.println("Firepath installation succefull");
				}
				capabilities.setCapability(FirefoxDriver.PROFILE, firefoxProfile);
				System.setProperty("webdriver.gecko.driver", geckodriverPath);
				if (serverURL.equalsIgnoreCase("")) {
					driver = new FirefoxDriver(capabilities);
				}
				else {
					driver = new RemoteWebDriver(new URL(serverURL), capabilities);
				}		
				break;
			case "Chrome":
				capabilities = DesiredCapabilities.chrome();
				capabilities = setCapFromUI(capabilities, capsToBeSet);
				System.setProperty("webdriver.chrome.driver", chromedriverPath);
				if (serverURL.equalsIgnoreCase("")) {
					driver = new ChromeDriver(capabilities);
				}
				else {
					driver = new RemoteWebDriver(new URL(serverURL), capabilities);
				}
				break;
			case "Internet Explorer":
				capabilities = DesiredCapabilities.internetExplorer();
				capabilities = setCapFromUI(capabilities, capsToBeSet);
				System.setProperty("webdriver.ie.driver", iedriverPath);
				if (serverURL.equalsIgnoreCase("")) {
					driver = new InternetExplorerDriver(capabilities);
				}
				else {
					driver = new RemoteWebDriver(new URL(serverURL), capabilities);
				}
				break;
			case "Edge":
				capabilities = DesiredCapabilities.edge();
				capabilities = setCapFromUI(capabilities, capsToBeSet);
				System.setProperty("webdriver.edge.driver", edgedriverPath);
				if (serverURL.equalsIgnoreCase("")) {
					driver = new EdgeDriver(capabilities);
				}
				else {
					driver = new RemoteWebDriver(new URL(serverURL), capabilities);
				}
				break;
			default:
				break;
			}
			msg = "Browser launched : ["+browserName+"]\n";
		}
		else {
			msg = "Browser is already OPEN.\n";
		}
		return msg;
	}
	
	/** Dynamically Sets the desired capabilities from the table
	 * @param capabilities
	 * @param capsToBeSet
	 * @return DesiredCapabilities
	 */
	public DesiredCapabilities setCapFromUI(DesiredCapabilities capabilities, HashMap<String, String> capsToBeSet) {	
		for (Map.Entry<String, String> kv: capsToBeSet.entrySet()) {
			String capability = kv.getKey();
			String value = kv.getValue();
			if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
				capabilities.setCapability(capability, Boolean.parseBoolean(value));
			}
			else if (value.matches("\\d+")) {
				capabilities.setCapability(capability, Integer.parseInt(value));
			}
			else {
				capabilities.setCapability(capability, value);
			}		
		}
		return capabilities;	
	}

	/**
	 *  Close the Browser
	 *  @return String (Logs)
	 */
	public String closeBrowser() {
		String msg;
		if (driver != null) {
			driver.quit();
			driver = null;
			msg = "Browser is Closed.\n";
		}
		else {
			msg = "Browser is NOT OPEN.\n";
		}
		return msg;
	}
	
	/** Install FireBug
	 * @param firefoxProfile
	 * @return
	 * @throws URISyntaxException 
	 * @throws IOException 
	 */
	public FirefoxProfile installFirebug(FirefoxProfile firefoxProfile) throws URISyntaxException, IOException {
		InputStream input = BrowserFactory.class.getResourceAsStream("/addOns/firebug-2.0.19-fx.xpi");
		File targetFile = inputStream_to_File(input, "firebug-2.0.19-fx.xpi");
		String firebug_ext = "extensions.firebug.";
		firefoxProfile.addExtension(targetFile);
		firefoxProfile.setPreference(firebug_ext + "currentVersion", "2.0.19");
		firefoxProfile.setPreference(firebug_ext + "defaultPanelName", "firepath");
		//firefoxprofile.setPreference(firebug_ext + "allPagesActivation", "on");
		//firefoxprofile.setPreference(firebug_ext + "net.enableSites", true);
		targetFile.deleteOnExit();
		return firefoxProfile;
	}
	
	/** INstall FirePath
	 * @param firefoxProfile
	 * @return
	 * @throws URISyntaxException 
	 * @throws IOException 
	 */
	public FirefoxProfile installFirepath(FirefoxProfile firefoxProfile) throws URISyntaxException, IOException {
		InputStream input = BrowserFactory.class.getResourceAsStream("/addOns/firepath-0.9.7.1-fx.xpi");
		File targetFile = inputStream_to_File(input, "firepath-0.9.7.1-fx.xpi");
		String firepath_ext = "extensions.firepath.";
		firefoxProfile.addExtension(targetFile);
		firefoxProfile.setPreference(firepath_ext + "currentVersion", "0.9.7");
		targetFile.deleteOnExit();
		return firefoxProfile;
	}
	
	/** Convert a InputStream object to FIle object
	 * @param inStream
	 * @param outputFileName
	 * @return File
	 * @throws IOException
	 */
	public File inputStream_to_File(InputStream inputStream, String outputFileName) throws IOException {
		String tmpDir = System.getProperty("java.io.tmpdir");
		File targetFile = new File(tmpDir + outputFileName);
		FileUtils.copyInputStreamToFile(inputStream, targetFile);
		return targetFile;
	}
	
}
