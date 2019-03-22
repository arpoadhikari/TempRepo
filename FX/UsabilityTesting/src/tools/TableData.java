package tools;

import java.awt.image.BufferedImage;
import java.util.HashMap;

public class TableData {

	private int sl;
	private String pageTitle;
	private String pageURL;
	private String objectType;
	private String objectSubType;
	private String objectName;
	private String objectXpath;
	private HashMap<String, String> allCSS;
	private HashMap<String, String> actualCSS;
	private HashMap<String, String> expectedCSS;
	private String status;
	private String comments;
	private BufferedImage screenshot;

	public TableData(int sl, String pageTitle, String pageURL, String objectType, String objectSubType, String objectName, 
			String objectXpath, HashMap<String, String> allCSS, HashMap<String, String> actualCSS, 
			HashMap<String, String> expectedCSS, String status, String comments, BufferedImage screenshot) {
		
		this.sl = sl;
		this.pageTitle = pageTitle;
		this.pageURL = pageURL;
		this.objectType = objectType;
		this.objectSubType = objectSubType;
		this.objectName = objectName;
		this.objectXpath = objectXpath;
		this.allCSS = allCSS;
		this.actualCSS = actualCSS;
		this.expectedCSS = expectedCSS;
		this.status = status;
		this.comments = comments;
		this.screenshot = screenshot;
	}

	public int getSl() {
		return sl;
	}

	public void setSl(int sl) {
		this.sl = sl;
	}

	public String getPageTitle() {
		return pageTitle;
	}

	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}

	public String getPageURL() {
		return pageURL;
	}

	public void setPageURL(String pageURL) {
		this.pageURL = pageURL;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getObjectSubType() {
		return objectSubType;
	}

	public void setObjectSubType(String objectSubType) {
		this.objectSubType = objectSubType;
	}

	public String getObjectXpath() {
		return objectXpath;
	}

	public void setObjectXpath(String objectXpath) {
		this.objectXpath = objectXpath;
	}

	public HashMap<String, String> getAllCSS() {
		return allCSS;
	}

	public void setAllCSS(HashMap<String, String> allCSS) {
		this.allCSS = allCSS;
	}

	public HashMap<String, String> getActualCSS() {
		return actualCSS;
	}

	public void setActualCSS(HashMap<String, String> actualCSS) {
		this.actualCSS = actualCSS;
	}

	public HashMap<String, String> getExpectedCSS() {
		return expectedCSS;
	}

	public void setExpectedCSS(HashMap<String, String> expectedCSS) {
		this.expectedCSS = expectedCSS;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
	
	public BufferedImage getScreenshot() {
		return screenshot;
	}

	public void setScreenshot(BufferedImage screenshot) {
		this.screenshot = screenshot;
	}

	public String toString() {
		
		return "Sl="+sl
				+"\nPage Title="+pageTitle
				+"\nPage URL="+pageURL
				+"\nObject Type="+objectType
				+"\nObject Name="+objectName
				+"\nObject Xpath="+objectXpath
				+"\nActual CSS="+actualCSS
				+"\nExpected CSS="+expectedCSS
				+"\nStatus="+status
				+"\nComments="+comments;
	}
}
