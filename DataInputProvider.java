package utils;

import java.io.File;
import java.io.FileInputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import wrappers.GenericWrappers;

public class DataInputProvider extends GenericWrappers {

	public static String[][] getSheet(String dataSheetName) {

		String[][] data = null;
		int count = 0;

		try {
			FileInputStream fis = new FileInputStream(new File("./data/TestCase/" + dataSheetName + ".xlsx"));
			XSSFWorkbook workbook = new XSSFWorkbook(fis);
			XSSFSheet sheet = workbook.getSheetAt(0);

			// get the number of rows
			int rowCount = sheet.getLastRowNum();
			int updatedRowCount = getUpdatedRowCount(sheet);

			// get the number of columns
			int columnCount = sheet.getRow(0).getLastCellNum();

			data = new String[updatedRowCount][columnCount];

			// loop through the rows
			for (int i = 1; i < rowCount + 1; i++) {
				try {
					XSSFRow row = sheet.getRow(i);
					String firstCell = row.getCell(0).getStringCellValue();
					if (!firstCell.equals("No")) {

						int diff = count + 1;
						for (int j = 0; j < columnCount; j++) { // loop through the columns
							try {
								String cellValue = "";
								try {
									XSSFCell cell = row.getCell(j);
								/*if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
									cellValue = NumberToTextConverter.toText(cell.getNumericCellValue());
									//System.out.println("Content in each cell: "+cellValue);
								}else{*/
									cellValue = cell.getStringCellValue();
									//System.out.println("Content in each cell: "+cellValue);
//								}
								} catch (NullPointerException e) {

								}

								data[i - diff][j] = cellValue; // add to the data array
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					} else
						count = count + 1;

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			fis.close();
			workbook.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return data;
	}

	public static int getUpdatedRowCount(XSSFSheet sheet) {

		// get the number of rows
		int rowCount = sheet.getLastRowNum();
		int count = 0;

		// loop through the rows
		for (int i = 1; i < rowCount + 1; i++) {

			XSSFRow row = sheet.getRow(i);
			String firstCell = row.getCell(0).getStringCellValue();
			if (!firstCell.equals("No")) {

				count = count + 1;
			}
		}
		return count;
	}
}

