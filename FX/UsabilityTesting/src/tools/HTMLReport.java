package tools;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;

import org.jdesktop.observablecollections.ObservableList;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

public class HTMLReport {
	
	ObservableList<TableData> tableData;
	ExtentHtmlReporter htmlReporter;
	ExtentReports extent;
	ExtentTest test;
	String screenshotFolder;

	/** Constructor of the class
	 * @param tableData
	 * @param folderPath
	 * @throws IOException 
	 */
	public HTMLReport(ObservableList<TableData> tableData, String folderPath) throws IOException {
		
		this.tableData = tableData;
		htmlReporter = new ExtentHtmlReporter(folderPath+"\\Report.html");
		this.screenshotFolder = folderPath+"\\Screenshots";
		new File(this.screenshotFolder).mkdirs();
		extent = new ExtentReports();
		extent.attachReporter(htmlReporter);
		setConfig();
		writeData();
	}
	
	/**
	 * Sets the initial report configuration
	 */
	public void setConfig() {
		
		htmlReporter.config().setDocumentTitle("Usability Testing Report");
		htmlReporter.config().setReportName("Usability Testing");
		extent.setSystemInfo("OS Name", System.getProperty("os.name"));
		extent.setSystemInfo("OS Version", System.getProperty("os.version"));
		extent.setSystemInfo("OS Architecture", System.getProperty("os.arch"));
		extent.setSystemInfo("User Name", System.getProperty("user.name"));
	}
	
	/**
	 * Writes the result into report
	 * @throws IOException 
	 */
	public void writeData() throws IOException {
		
		if (tableData.size() > 0) {
			for (int i = 0; i < tableData.size(); i++) {
				
				TableData rowDetails = tableData.get(i);
				String cssProperty = "";
				String actualCSSValue = "";
				String expectedCSSValue = "";
				
				test = extent.createTest("Test"+rowDetails.getSl(), "Page :- <a target='_blank' href='"+rowDetails.getPageURL()+"'>"+rowDetails.getPageTitle()+"</a><br/>"
																	+"Object Type :- "+rowDetails.getObjectType()+"<br/>"
																	+"Object SubType :- "+rowDetails.getObjectSubType()+"<br/>"
																	+"Object Name :- "+rowDetails.getObjectName()+"<br/>"
																	+"Object Xpath :- "+rowDetails.getObjectXpath());
				
				int row = rowDetails.getActualCSS().size();
				String data[][] = new String[row+1][4];
				data[0][0] = "<u>CSS Property</u>";
				data[0][1] = "<u>Actual Value</u>";
				data[0][2] = "<u>Expected Value</u>";
				data[0][3] = "<u>Status</u>";
				int j = 1;
				
				for (Map.Entry<String, String> kv: rowDetails.getActualCSS().entrySet()) {
					cssProperty = kv.getKey().trim();
					actualCSSValue = kv.getValue().trim();
					expectedCSSValue = rowDetails.getExpectedCSS().get(kv.getKey()).trim();
					data[j][0] = cssProperty;
					data[j][1] = actualCSSValue;
					data[j][2] = expectedCSSValue;
					
					Markup m;
					data[j][3] = actualCSSValue.equalsIgnoreCase(expectedCSSValue) ? "PASS" : "FAIL";
					if (data[j][3].equalsIgnoreCase("FAIL")) {
						m = MarkupHelper.createLabel(data[j][3], ExtentColor.RED);
					}
					else if(data[j][3].equalsIgnoreCase("PASS")) {
						m = MarkupHelper.createLabel(data[j][3], ExtentColor.GREEN);
					}
					else {
						m = MarkupHelper.createLabel(data[j][3], ExtentColor.GREY);
					}
					data[j][3] = m.getMarkup();
					j++;
				}
				
				String screenshotFilePath = screenshotFolder+"\\Test"+rowDetails.getSl()+".png";
				ImageIO.write(rowDetails.getScreenshot(), "PNG", new File(screenshotFilePath));
				
				if (rowDetails.getStatus().equalsIgnoreCase("PASS")) {
					test.log(Status.PASS, MarkupHelper.createTable(data)).addScreenCaptureFromPath(screenshotFilePath);
				}
				else if (rowDetails.getStatus().equalsIgnoreCase("FAIL")) {
					test.log(Status.FAIL, MarkupHelper.createTable(data)).addScreenCaptureFromPath(screenshotFilePath);
				}
				else {
					test.log(Status.SKIP, MarkupHelper.createTable(data)).addScreenCaptureFromPath(screenshotFilePath);
				}
				
			}
			extent.flush();
		}
		
	}
	
}
