package utils;

// import from sql
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;

import datascience.MarketShare_DataScience;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import wrappers.OptumeraWrappers;

public class DatabaseConnect extends OptumeraWrappers{

	// JDBC driver name and database URL
	private static final String JDBC_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	private static final String DB_URL = "jdbc:sqlserver://DHONT58446US:14481;databaseName=WM_Release2";

	// Database credentials
	private static final String USER = "SVCspaceopti";
	private static final String PASS = "space4B$";

	public static ArrayList<MarketShare_DataScience> marketShareDBConnect() {

		//ArrayList<MarketShare_DataScience> MarketShareList = null;
		Connection conn = null;
		Statement stmt = null;

		try {

			// STEP 2: Register JDBC driver
			Class.forName(JDBC_DRIVER);

			// STEP 3: Open a connection
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			// STEP 4: Execute a query
			stmt = conn.createStatement();
			
			int intScenarioID = Integer.parseInt(scenarioID);

			String sql = "select A.CAT_SPACE_PLAN_ID,A.STORE_ID,A.CATEGORY_ID,D.AVG_FSI,B.LIMIT_TYPE,B.MIN_LIMIT,B.MAX_LIMIT,C.FOOTAGE," +
					"A.MIN_FOOTAGE,A.MAX_FOOTAGE from CAT_SPACE_PLAN_MS_OP A,CAT_SPACE_PLAN_MIN_MAX_RULE_IP B,CAT_SPACE_PLAN_FOOTAGE C," +
					"DQ_FSI_AVG_DEPT_CAT D where A.CAT_SPACE_PLAN_ID=B.CAT_SPACE_PLAN_ID and A.CAT_SPACE_PLAN_ID=C.CAT_SPACE_PLAN_ID " +
					"and A.CATEGORY_ID=D.CATEGORY_ID and B.LIMIT_TYPE=3 and A.STORE_ID=C.STORE_ID and A.CATEGORY_ID=C.CATEGORY_ID " +
					"and A.CAT_SPACE_PLAN_ID="+intScenarioID;
			
			ResultSet rs = stmt.executeQuery(sql);			

			// STEP 5: Extract data from result set
			while (rs.next()) {
				
				MarketShare_DataScience ms = new MarketShare_DataScience();
				
				// Retrieve by column name
				ms.setScenarioId(rs.getInt("CAT_SPACE_PLAN_ID"));
				
				ms.setStoreId(rs.getInt("STORE_ID"));
				
				ms.setCatId(rs.getInt("CATEGORY_ID"));

				ms.setAvg_FSI(rs.getString("AVG_FSI"));
				
				ms.setLimitType(rs.getInt("LIMIT_TYPE"));
				
				ms.setInputMin(rs.getString("MIN_LIMIT"));
				
				ms.setInputMax(rs.getString("MAX_LIMIT"));
				
				ms.setCurrentFootage(rs.getInt("FOOTAGE"));
				
				ms.setMinFootageVal(rs.getString("MIN_FOOTAGE"));
				
				ms.setMaxFootageVal(rs.getString("MAX_FOOTAGE"));
				
				MarketShareList.add(ms);
				
				/*// Display values
				System.out.println(MarketShareList);*/
			}
			//System.out.println(MarketShareList);
			
			System.out.println("Array List Size: "+MarketShareList.size());
			/*for (MarketShare_DataScience market : MarketShareList) {
				System.out.println(market.getScenarioId()+", "+market.getStoreId()+", "+market.getCatId()+", "+market.getMinFootageVal()+", "+market.getMaxFootageVal());
			}*/
			// STEP 6: Clean-up environment
			rs.close();
			stmt.close();
			conn.close();

		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();

		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();

		} finally {

			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}

			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		return MarketShareList;
	}
	
	public static void writeMarketShareData(String marketShareExcelFileName, String categoriesSelected, String storeGroupSelected){

		//String excelFileName = "./data/Test.xlsx";//name of excel file

		try {
			String sheetName = "MarketShare";//name of sheet
			
			File file = new File(marketShareExcelFileName);
			
			XSSFWorkbook wb = new XSSFWorkbook();
			
			//FileOutputStream fileOut = new FileOutputStream(excelFileName);
			
			XSSFSheet sheet = wb.createSheet(sheetName) ;
			
			XSSFRow header = sheet.createRow(0);
			XSSFCell scenId = header.createCell(0);
			scenId.setCellValue("Scenario Id");
			XSSFCell stores = header.createCell(1);
			stores.setCellValue("Stores Selected");
			XSSFCell categories = header.createCell(2);
			categories.setCellValue("Categories Selected "+"("+categoriesSelected+")");
			XSSFCell avgFSI = header.createCell(3);
			avgFSI.setCellValue("Average FSI Value");
			XSSFCell limitType = header.createCell(4);
			limitType.setCellValue("Limit Type");
			XSSFCell inputMinFoot = header.createCell(5);
			inputMinFoot.setCellValue("Input Min. Foot Value");
			XSSFCell inputMaxFoot = header.createCell(6);
			inputMaxFoot.setCellValue("Input Max. Foot Value");
			XSSFCell currentFoot = header.createCell(7);
			currentFoot.setCellValue("Current Footage");
			XSSFCell outputMinFoot = header.createCell(8);
			outputMinFoot.setCellValue("Output Min. Foot Value");
			XSSFCell outputMaxFoot = header.createCell(9);
			outputMaxFoot.setCellValue("Output Max. Foot Value");
			XSSFCell storeGroup = header.createCell(10);
			storeGroup.setCellValue("Store Group Selected "+"("+storeGroupSelected+")");

			Iterator<MarketShare_DataScience> iterator = MarketShareList.iterator();
			
			 //Iterate over data and write to sheet  
			  int rownum = 1;  
			  while(iterator.hasNext()){
				  MarketShare_DataScience market = iterator.next();
				  XSSFRow row = sheet.createRow(rownum++);
				  XSSFCell scenarioId = row.createCell(0);
				  scenarioId.setCellValue(market.getScenarioId());
				  XSSFCell storeId = row.createCell(1);
				  storeId.setCellValue(market.getStoreId());
				  XSSFCell categoryId = row.createCell(2);
				  categoryId.setCellValue(market.getCatId());
				  XSSFCell averageFSI = row.createCell(3);
				  averageFSI.setCellValue(market.getAvg_FSI());
				  XSSFCell limit = row.createCell(4);
				  limit.setCellValue(market.getLimitType());
				  XSSFCell ipMinFoot = row.createCell(5);
				  ipMinFoot.setCellValue(market.getInputMin());
				  XSSFCell ipMaxFoot = row.createCell(6);
				  ipMaxFoot.setCellValue(market.getInputMax());
				  XSSFCell currentFootage = row.createCell(7);
				  currentFootage.setCellValue(market.getCurrentFootage());
				  XSSFCell opMinFoot = row.createCell(8);
				  opMinFoot.setCellValue(market.getMinFootageVal());
				  XSSFCell maxFoot = row.createCell(9);
				  maxFoot.setCellValue(market.getMaxFootageVal());
			  }

				FileOutputStream fileOut = new FileOutputStream(file);

				//write this workbook to an Outputstream.
				wb.write(fileOut);
				fileOut.flush();
				wb.close();
				fileOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}