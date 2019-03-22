package tools;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtil {

	private String fPath;
	private FileInputStream excelFile;
	private XSSFWorkbook workBook;
	private XSSFSheet workSheet;
	private XSSFRow row;
	private XSSFCell cell;
	private CreationHelper createHelper;
	private CellStyle cs;
	
	public String getfPath() {
		return fPath;
	}

	public void setfPath(String fPath) {
		this.fPath = fPath;
	}

	public FileInputStream getExcelFile() {
		return excelFile;
	}

	public void setExcelFile(FileInputStream excelFile) {
		this.excelFile = excelFile;
	}

	public XSSFWorkbook getWorkBook() {
		return workBook;
	}

	public void setWorkBook(XSSFWorkbook workBook) {
		this.workBook = workBook;
	}

	public XSSFSheet getWorkSheet() {
		return workSheet;
	}

	public void setWorkSheet(XSSFSheet workSheet) {
		this.workSheet = workSheet;
	}

	public XSSFRow getRow() {
		return row;
	}

	public void setRow(XSSFRow row) {
		this.row = row;
	}

	public XSSFCell getCell() {
		return cell;
	}

	public void setCell(XSSFCell cell) {
		this.cell = cell;
	}

	public CreationHelper getCreateHelper() {
		return createHelper;
	}

	public void setCreateHelper(CreationHelper createHelper) {
		this.createHelper = createHelper;
	}

	public CellStyle getCs() {
		return cs;
	}

	public void setCs(CellStyle cs) {
		this.cs = cs;
	}

	/**
	 *  Constructor of ExcelUtil Class
	 */
	public ExcelUtil() {
		workBook = new XSSFWorkbook();
		cs = workBook.createCellStyle();
		createHelper = workBook.getCreationHelper();
	}
	
	/** Constructor of ExcelUtil Class
	 * @param filePath
	 */
	public ExcelUtil(String filePath) {
		try {
			fPath = filePath;
			excelFile = new FileInputStream(fPath);
			workBook = new XSSFWorkbook(excelFile);
			cs = workBook.createCellStyle();
			createHelper = workBook.getCreationHelper();
		} catch (Exception e) {
			System.out.println("Unable to open the excel file : "+e.getMessage());
		}
	}
	
	/** Constructor of ExcelUtil Class
	 * @param filePath
	 * @param sheetName
	 */
	public ExcelUtil(String filePath, String sheetName) {
		try {
			fPath = filePath;
			excelFile = new FileInputStream(fPath);
			workBook = new XSSFWorkbook(excelFile);
			cs = workBook.createCellStyle();
			createHelper = workBook.getCreationHelper();
			workSheet = workBook.getSheet(sheetName);
		} catch (Exception e) {
			System.out.println("Unable to open the excel file : "+e.getMessage());
		}
	}
	
	/** Create a WorkSheet
	 * @param sheetName
	 */
	public void createSheet(String sheetName) {
		workBook.createSheet(sheetName);
	}

	/** Method to read from a excel cell
	 * @param rowID
	 * @param colID
	 * @return String
	 */
	public String readCell(int rowID, int colID) {
		String value = "";
		try {
			row = workSheet.getRow(rowID);
			if (row != null) {
				cell = row.getCell(colID, MissingCellPolicy.RETURN_BLANK_AS_NULL);
				if (cell != null) {
					value = new DataFormatter().formatCellValue(cell);
				}
			}
		}
		catch (Exception e) {
			System.out.println("Unable to read excel cell :"+e.getMessage());
		}
		return value.trim();
	}

	/** Method to write into a excel cell
	 * @param data
	 * @param rowNum
	 * @param colNum
	 * @throws Exception
	 */
	public void writeCell(String data, Integer rowNum, Integer colNum) throws Exception {
		row = workSheet.getRow(rowNum);
		if (row == null) {
			row = workSheet.createRow(rowNum);
		}
		cell = row.getCell(colNum, MissingCellPolicy.RETURN_BLANK_AS_NULL);
		if (cell == null) {
			cell = row.createCell(colNum);
            cs.setWrapText(true);
			cell.setCellValue(data);
			cell.setCellStyle(cs);
		}
		cell.setCellValue(data);
		/*FileOutputStream outputStream = new FileOutputStream(fPath);
		workBook.write(outputStream);
		outputStream.close();*/
	}

	/** Method to find a value in a WorkSheet
	 * @param value
	 * @return List<XSSFCell>
	 * @throws Exception
	 */
	public List<XSSFCell> findValueInSheet(String value) throws Exception {
		int firstRow = workSheet.getFirstRowNum();
		int lastRow = workSheet.getLastRowNum();
		List<XSSFCell> cells = new ArrayList<XSSFCell>();

		for (int i = firstRow; i <= lastRow; i++) {
			int firstCol = workSheet.getRow(i).getFirstCellNum();
			int lastCol = workSheet.getRow(i).getLastCellNum();
			for (int j = firstCol; j <= lastCol; j++) {
				if (this.readCell(i, j).equalsIgnoreCase(value)) {
					cells.add(workSheet.getRow(i).getCell(j)); 
				}
			}
		}
		return cells;	
	}

	/** Get last row number
	 * @return integer
	 */
	public int getLatRowNum() {
		return workSheet.getLastRowNum();
	}

	/** Get last column number of a row
	 * @param rowIndex
	 * @return integer
	 */
	public int getLastColumn(int rowIndex) {
		row = workSheet.getRow(rowIndex);
		return row.getLastCellNum();
	}
	
	/** Searches a value in a column and return the matching row number
	 * @param value
	 * @param columnIndex
	 * @return integer
	 */
	public int getRowIndex(String value, int columnIndex) {
		
		int rowIndex = -1;
		for (int i = 0; i <= getLatRowNum(); i++ ) {
			if (readCell(i, columnIndex).equalsIgnoreCase(value)) {
				rowIndex = i;
				break;
			}
		}
		return rowIndex;
	}
	
	/** Searches a value in a row and return the matching column number
	 * @param value
	 * @param rowIndex
	 * @return integer
	 */
	public int getColumnIndex(String value, int rowIndex) {
		
		int columnIndex = -1;
		int endColumnindex = getLastColumn(rowIndex);
		for (int i = 0; i <= endColumnindex; i++) {
			if (readCell(rowIndex, i).equalsIgnoreCase(value)) {
				columnIndex = i;
				break;
			}
		}
		return columnIndex;
	}
	
	/** Set active WorkSheet
	 * @param sheetName
	 */
	public void setActiveWorkSheet(String sheetName) {
		workSheet = workBook.getSheet(sheetName);
	}
	
	/** Get the list of WorkSheets
	 * @return ArrayList<String>
	 */
	public ArrayList<String> getWorksheetList() {
		
		ArrayList<String> workSheetList = new ArrayList<String>();
		for (int i = 0; i < workBook.getNumberOfSheets(); i++) {
			workSheetList.add(workBook.getSheetAt(i).getSheetName().trim());
		}
		return workSheetList;
	}
	
	/** Save the excel file
	 * @param filePath
	 * @throws IOException
	 */
	public void saveFile(String filePath) throws IOException {
		fPath = filePath;
		FileOutputStream outputStream = new FileOutputStream(fPath);
		workBook.write(outputStream);
		outputStream.close();
		workBook.close();
	}

}
