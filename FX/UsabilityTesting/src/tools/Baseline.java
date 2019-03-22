package tools;

import java.util.ArrayList;
import java.util.HashMap;

public class Baseline {
	
	final String baseLinePath;
	private String elementType;
	private ExcelUtil  excelUtil;
	final int cssColumnStartIndex = 1;
	final int cssRowStartIndex = 2;
	final int subTypeRowStartIndex = 0;
	final int subTypeColumnStartIndex = 2;
	final int subTypeDescriptionRowIndex = 1;

	/** Constructor of the Baseline class 
	 * @param baseLinePath
	 * @param elementType
	 */
	public Baseline(String baseLinePath, String elementType) {
		this.baseLinePath = baseLinePath;
		this.elementType = elementType;
		this.excelUtil = new ExcelUtil(baseLinePath);
		this.excelUtil.setActiveWorkSheet(elementType);
	}
	
	/** Method to get the list of CSS properties
	 * @return ArrayList<String>
	 */
	public ArrayList<String> getCSSList() {
		
		ArrayList<String> cssList = new ArrayList<String>();
		
		for (int i = cssRowStartIndex; i <= excelUtil.getLatRowNum(); i++) {
			//Store the CSS if Validation flag is set to Y
			if (excelUtil.readCell(i, 0).equalsIgnoreCase("Y")) {
				cssList.add(excelUtil.readCell(i, cssColumnStartIndex).trim());
			}
		}
		return cssList;
	}
	
	/** Method to get the list of object sub types
	 * @return ArrayList<String>
	 */
	public ArrayList<String> getSubTypeList() {
		
		ArrayList<String> subTypeList = new ArrayList<String>();
		
		for (int i = subTypeColumnStartIndex; i <excelUtil.getLastColumn(subTypeRowStartIndex); i++) {
			subTypeList.add(excelUtil.readCell(subTypeRowStartIndex, i).trim());
		}
		return subTypeList;
	}
	
	/** Method to get the list of CSS property and value pair
	 * @param subType
	 * @return HashMap<String, String>
	 */
	public HashMap<String, String> getCSSTableFor(String subType) {
		
		HashMap<String, String> cssTable = new HashMap<String, String>();
		int subTypeColumnIndex = excelUtil.getColumnIndex(subType, subTypeRowStartIndex);
		
		if(subTypeColumnIndex < 0) {
			System.out.println("ERROR: "+subType+" not found in "+elementType);
		}
		
		for (int i = cssRowStartIndex; i <= excelUtil.getLatRowNum(); i++) {
			String cssProperty = excelUtil.readCell(i, cssColumnStartIndex).trim();
			//Store the CSS if Validation flag is set to Y
			if (excelUtil.readCell(i, 0).equalsIgnoreCase("Y")) {
				String cssValue = excelUtil.readCell(i, subTypeColumnIndex).trim();
				cssTable.put(cssProperty, cssValue);
			}
		}
		return cssTable;
	}
	
	/** Method to get the description of an object
	 * @param subType
	 * @return String
	 */
	public String getDescription(String subType) {
	
		int subTypeColumnIndex = excelUtil.getColumnIndex(subType, subTypeRowStartIndex);
		if(subTypeColumnIndex < 0) {
			System.out.println("ERROR: "+subType+" not found in "+elementType);
		}
		return excelUtil.readCell(subTypeDescriptionRowIndex, subTypeColumnIndex).trim();
	}
	
}
