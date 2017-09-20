package utils;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriverException;

import wrappers.OptumeraWrappers;

import com.relevantcodes.extentreports.DisplayOrder;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;


public class Reporter extends OptumeraWrappers{

	private static ExtentTest test;
	private static ExtentReports extent;

	private static ExtentTest childTest;

	private static String timeStamp = new Timestamp(System.currentTimeMillis()).toString().replaceAll(":", ".");


	public static void reportStep(String desc, String status) {

		long number = (long) Math.floor(Math.random() * 900000000L) + 10000000L;
		try {
			FileUtils.copyFile(driver.getScreenshotAs(OutputType.FILE) , new File("./reports/images1/"+number+".jpg"));
		} catch (WebDriverException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Write if it is successful or failure or information
		if(status.toUpperCase().equals("PASS")){
			test.log(LogStatus.PASS, desc+test.
					addScreenCapture("./../reports/images1/"+number+".jpg"));
		}else if(status.toUpperCase().equals("FAIL")){
			test.log(LogStatus.FAIL, desc+test.addScreenCapture("./../reports/images1/"+number+".jpg"));
			throw new RuntimeException("FAILED");
		}else if(status.toUpperCase().equals("INFO")){
			test.log(LogStatus.INFO, desc+test.addScreenCapture("./../reports/images1/"+number+".jpg"));
		}else if(status.toUpperCase().equals("WARNING")){
			test.log(LogStatus.WARNING, desc+test.addScreenCapture("./../reports/images1/"+number+".jpg"));
		}
	}


	public static void startResult(){
		extent = new ExtentReports("./reports/result"+timeStamp+".html", false, DisplayOrder.NEWEST_FIRST);
		extent.loadConfig(new File("./extent-config.xml"));
	}

	public static void startTestCase(){
		test = extent.startTest(testCaseName, testDescription);
	}

	public static void endResult(){
		extent.endTest(test);
		extent.endTest(childTest);
		extent.flush();
	}

	/*public static void childReport(){
		childTest = extent.startTest("Plan ID: "+planID);
		childTest.log(LogStatus.INFO, "Plan ID for the Test Case: "+testCaseName+" is: "+planID);
		test.appendChild(childTest);
	}*/

	public static void finalReport(Set<String> result){

		if (testCaseName.contains("CSO")){

			childTest = extent.startTest("Final Report with Scenario ID's for all Test Cases");

			for (String scenario : result) {

				childTest.log(LogStatus.INFO, "Scenario ID: "+scenario);
			}
		}
		else if (testCaseName.contains("OC")){

			childTest = extent.startTest("Final Report with Objective ID's for all Test Cases");

			for (String objective : result) {

				childTest.log(LogStatus.INFO, "Objective ID: "+objective);
			}
		}


		/*for (Entry<String, String> plan : allPlanID.entrySet()) {

			String planIdVal = plan.getKey();
			String testCaseVal = plan.getValue();
			test.log(LogStatus.INFO, "Plan ID for Test Case : "+testCaseVal+" is : "+planIdVal);
		}*/
	}


}
