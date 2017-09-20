package wrappers;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.Map.Entry;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.*;

import pages.CSO_DSO_SpaceRulesPage;
import datascience.MarketShare_DataScience;
import utils.DataInputProvider;
import utils.DatabaseConnect;
import utils.Reporter;

public class OptumeraWrappers extends GenericWrappers {

	protected String browserName;
	protected String dataSheetName;
	protected static String marketShareExcelFileName;
	protected static String testCaseName;
	protected static String testDescription;
	protected static String scenarioID;
	protected static String objectiveID;
	protected static String savedID;
	protected static String page;
	protected static String scenario_Name;
	protected static String objective_Name;

	protected static Set<String> resultSet = new TreeSet<String>();

	public static ArrayList<MarketShare_DataScience> MarketShareList = new ArrayList<>();

	@BeforeSuite
	public void beforeSuite() throws FileNotFoundException, IOException{
		Reporter.startResult();
		loadObjects();
	}

	@BeforeTest
	public void beforeTest(){

	}

	@BeforeMethod
	public void beforeMethod(){
		Reporter.startTestCase();
		invokeApp(browserName);
	}

	@AfterSuite
	public void afterSuite(){
		Reporter.finalReport(resultSet);
		resultSet.clear();
		Reporter.endResult();
	}

	@AfterTest
	public void afterTest(){

	}

	@AfterMethod
	public void afterMethod(){

		if(scenarioID!=null){
			addScenarioIDWithScenarioName();
		}
		if(objectiveID!=null){
			addObjectiveIDWithObjectiveName();
		}
		driver.manage().deleteAllCookies();
		quitBrowser();
	}

	@DataProvider(name="fetchData")
	public Object[][] getData(){
		return DataInputProvider.getSheet(dataSheetName);		
	}

	public boolean generateObjectiveName(String objName){
		boolean bReturn = false;

		try {
			long number = (long) Math.floor(Math.random() * 90000L) + 1000L;
			objective_Name = objName+"_"+number;
			bReturn = true;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Scenario/Objective Name could not be generated.", "FAIL");
		}
		return bReturn;
	}

	public boolean generateScenarioName(String sceName){
		boolean bReturn = false;

		try {
			long number = (long) Math.floor(Math.random() * 90000L) + 1000L;
			scenario_Name = sceName+"_"+number;
			bReturn = true;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Scenario/Objective Name could not be generated.", "FAIL");
		}
		return bReturn;
	}

	public boolean dropDownSelectionMethod(String locatorValue, String inputValues){
		boolean bReturn = false;

		try {
			//split the given input values and store in array
			String[] givenInput = inputValues.split(",");

			//get all elements matching the locator
			List<WebElement> allChoices = getAllElementsBy(locatorValue);

			waitTillLoadingCompletesBy(locatorValue);

			//traverse through each element
			for (WebElement choice : allChoices) {

				//get text from element and compare it with array and select the element
//				String textInElement = choice.getText();
				String[] text = choice.getText().split(" ");
				for(int i=0;i<givenInput.length;i++){
					if(text[0].equalsIgnoreCase(givenInput[i])){
						choice.click();
						waitForPageLoad();
						/*if (getAllElementsBy(prop.getProperty("AllPage.PopUpTitle")).size() != 0){
							String warningText = getTextBy(prop.getProperty("AllPage.PopUpContent"));
							Reporter.reportStep("Error occurred: "+warningText, "FAIL");
						}*/
					}
				}
			}
			scrollBy(locatorValue, Keys.ESCAPE);
//			clickRandom();
			waitForPageLoad();
			bReturn = true;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("One of the given inputs: "+inputValues+" could not be selected.", "FAIL");
		}
		return bReturn;
	}

	public boolean dropDownSelectionWithPopUpCheck(String locatorValue, String inputValues){
		boolean bReturn = false;

		try {
			//split the given input values and store in array
			String[] givenInput = inputValues.split(",");

			//get all elements matching the locator
			List<WebElement> allChoices = getAllElementsBy(locatorValue);

			waitTillLoadingCompletesBy(locatorValue);

			//traverse through each element
			for (WebElement choice : allChoices) {

				//get text from element and compare it with array and select the element
//				String textInElement = choice.getText();
				String[] text = choice.getText().split(" ");
				for(int i=0;i<givenInput.length;i++){
					if(text[0].equalsIgnoreCase(givenInput[i])){
						choice.click();
						waitForPageLoad();
						if (getAllElementsBy(prop.getProperty("AllPage.PopUpTitle")).size() != 0){
							String warningText = getTextBy(prop.getProperty("AllPage.PopUpContent"));
							Reporter.reportStep("Error occurred: "+warningText, "FAIL");
						}
					}
				}
			}
			scrollBy(locatorValue, Keys.ESCAPE);
//			clickRandom();
			waitForPageLoad();
			bReturn = true;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("One of the given inputs: "+inputValues+" could not be selected.", "FAIL");
		}
		return bReturn;
	}

	public boolean dropDownChooseMethod(String dropDownLocator, String dropDownOptionsLocator, String inputValue){
		boolean bReturn = false;

		try {
			//split the given input values and store in array
			//String[] givenInput = inputValues.split(",");

			String dropDownVal = getTextBy(dropDownLocator);
			boolean checker = false;

			if (dropDownVal.contains(inputValue))
				bReturn = true;
			else if (!dropDownVal.contains(inputValue)) {

				clickBy(dropDownLocator);
				waitForPageLoad();
				waitTillLoadingCompletesBy(dropDownOptionsLocator);

				//get all elements matching the locator
				List<WebElement> allChoices = getAllElementsBy(dropDownOptionsLocator);

				waitTillLoadingCompletesBy(dropDownOptionsLocator);

				//traverse through each element
				for (WebElement choice : allChoices) {

					//get text from element and compare it with array and choose particular element and break
					String[] textInElement = choice.getText().split(" ");

					if (textInElement[0].equals(inputValue)) {
						choice.click();
						checker = true;
						break;
					}
				}
				if (!checker)
					Reporter.reportStep("The requested input: "+inputValue+" is not available in the dropdown", "FAIL");
				waitForPageLoad();
				/*if (getAllElementsBy(prop.getProperty("AllPage.PopUpTitle")).size() != 0){
					String warningText = getTextBy(prop.getProperty("AllPage.PopUpContent"));
					Reporter.reportStep("Error occurred: "+warningText, "FAIL");
				}*/
				bReturn = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("The given input: "+inputValue+" could not be selected.", "FAIL");
		}
		return bReturn;
	}

	public boolean dropDownChooseWithAlertPopUpCheck(String dropDownLocator, String dropDownOptionsLocator, String inputValue){
		boolean bReturn = false;

		try {
			//split the given input values and store in array
			//String[] givenInput = inputValues.split(",");

			String dropDownVal = getTextBy(dropDownLocator);
			boolean checker = false;

			if (dropDownVal.contains(inputValue))
				bReturn = true;
			else if (!dropDownVal.contains(inputValue)) {

				clickBy(dropDownLocator);
				waitForPageLoad();
				waitTillLoadingCompletesBy(dropDownOptionsLocator);

				//get all elements matching the locator
				List<WebElement> allChoices = getAllElementsBy(dropDownOptionsLocator);

				waitTillLoadingCompletesBy(dropDownOptionsLocator);

				//traverse through each element
				for (WebElement choice : allChoices) {

					//get text from element and compare it with array and choose particular element and break
					String[] textInElement = choice.getText().split(" ");

					if (textInElement[0].equals(inputValue)) {
						choice.click();
						checker = true;
						break;
					}
				}
				if (!checker)
					Reporter.reportStep("Thre requested input: "+inputValue+" is not available in the dropdown", "FAIL");
				waitForPageLoad();
				if (getAllElementsBy(prop.getProperty("AllPage.PopUpTitle")).size() != 0){
					String warningText = getTextBy(prop.getProperty("AllPage.PopUpContent"));
					Reporter.reportStep("Error occurred: "+warningText, "FAIL");
				}
				bReturn = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("The given input: "+inputValue+" could not be selected.", "FAIL");
		}
		return bReturn;
	}

	public boolean dropDownChooseWithConfirmationPopUpCheck(String dropDownLocator, String dropDownOptionsLocator, String inputValue){
		boolean bReturn = false;

		try {
			//split the given input values and store in array
			//String[] givenInput = inputValues.split(",");

			String dropDownVal = getTextBy(dropDownLocator);
			boolean checker = false;

			if (dropDownVal.contains(inputValue))
				bReturn = true;
			else if (!dropDownVal.contains(inputValue)) {

				clickBy(dropDownLocator);
				waitForPageLoad();
				waitTillLoadingCompletesBy(dropDownOptionsLocator);

				//get all elements matching the locator
				List<WebElement> allChoices = getAllElementsBy(dropDownOptionsLocator);

				waitTillLoadingCompletesBy(dropDownOptionsLocator);

				//traverse through each element
				for (WebElement choice : allChoices) {

					//get text from element and compare it with array and choose particular element and break
					String[] textInElement = choice.getText().split(" ");

					if (textInElement[0].equals(inputValue)) {
						choice.click();
						checker = true;
						break;
					}
				}
				if (!checker)
					Reporter.reportStep("Thre requested input: "+inputValue+" is not available in the dropdown", "FAIL");
				waitForPageLoad();
				//click Yes in the confirmation popup
				if (getAllElementsBy(prop.getProperty("AllPage.PopUpTitle")).size() != 0){
					String warningText = getTextBy(prop.getProperty("AllPage.PopUpContent"));
					if (warningText.contains("Unsaved data (if any) will be lost")){
						clickBy(prop.getProperty("AllPage.BackPopUpYes"));
						waitForPageLoad();
					}
					else {
						Reporter.reportStep("Error occurred: "+warningText, "FAIL");
					}
				}
				bReturn = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("The given input: "+inputValue+" could not be selected.", "FAIL");
		}
		return bReturn;
	}

	public boolean dropDownChooseMethodforAdjacency(String dropDownLocator, String dropDownOptionsLocator, String inputValue){
		boolean bReturn = false;

		try {
			//split the given input values and store in array
			//String[] givenInput = inputValues.split(",");

			String dropDownVal = getTextBy(dropDownLocator);
			boolean checker = false;

			if (dropDownVal.contains(inputValue))
				bReturn = true;
			else if (!dropDownVal.contains(inputValue)) {

				clickBy(dropDownLocator);
				waitForPageLoad();
				waitTillLoadingCompletesBy(dropDownOptionsLocator);

				//get all elements matching the locator
				List<WebElement> allChoices = getAllElementsBy(dropDownOptionsLocator);

				waitTillLoadingCompletesBy(dropDownOptionsLocator);

				//traverse through each element
				for (WebElement choice : allChoices) {

					//get text from element and compare it with array and choose particular element and break
					String textInElement = choice.getText();

					if (textInElement.contains(inputValue)) {
						choice.click();
						checker = true;
						break;
					}
				}
				if (!checker)
					Reporter.reportStep("The requested input: "+inputValue+" is not available in the dropdown", "FAIL");
				waitForPageLoad();
				/*if (getAllElementsBy(prop.getProperty("AllPage.PopUpTitle")).size() != 0){
					String warningText = getTextBy(prop.getProperty("AllPage.PopUpContent"));
					Reporter.reportStep("Error occurred: "+warningText, "FAIL");
				}*/
				bReturn = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("The given input: "+inputValue+" could not be selected.", "FAIL");
		}
		return bReturn;
	}

	public boolean dropDownLatestValueChooseMethod(String dropDownLocator, String dropDownOptionsLocator){
		boolean bReturn = false;

		try {
			clickBy(dropDownLocator);
			waitForPageLoad();
			waitTillLoadingCompletesBy(dropDownOptionsLocator);

			//get all elements matching the locator
			List<WebElement> allChoices = getAllElementsBy(dropDownOptionsLocator);

			waitTillLoadingCompletesBy(dropDownOptionsLocator);

			//traverse through each element
			for (WebElement choice : allChoices) {

				choice.click();
				break;
			}
			waitForPageLoad();
			bReturn = true;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Latest value from dropdown could not be selected.", "FAIL");
		}
		return bReturn;
	}

	public boolean dropDownChooseMethodForObjective(String dropDownLocator, String dropDownOptionsLocator, String inputValue){
		boolean bReturn = false;

		try {
			//split the given input values and store in array
			//String[] givenInput = inputValues.split(",");

			String dropDownVal = getTextBy(dropDownLocator);

			if (dropDownVal.contains(inputValue))
				bReturn = true;
			else if (!dropDownVal.contains(inputValue)) {

				clickBy(dropDownLocator);
				waitTillLoadingCompletesBy(dropDownOptionsLocator);

				//get all elements matching the locator
				List<WebElement> allChoices = getAllElementsBy(dropDownOptionsLocator);

				waitTillLoadingCompletesBy(dropDownOptionsLocator);

				//traverse through each element
				for (WebElement choice : allChoices) {

					//get text from element and compare it with array and choose particular element and break
					String textInElement = choice.getText();

					if (textInElement.contains(inputValue)) {
						choice.click();
						break;
					}
				}
				waitTime(3000);
				bReturn = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("The given input: "+inputValue+" could not be selected.", "FAIL");
		}
		return bReturn;
	}

	public boolean validatePermissionAssignedInHomePage(String modulePermission, String tileLocator, String sideBarLocator){
		boolean bReturn = false;

		try {
			if (modulePermission.equals("No")){

                if (getAllElementsBy(tileLocator).size() != 0 && getAllElementsBy(sideBarLocator).size() != 0)
                    Reporter.reportStep("Modules which User doesn't have permission are available in Home Screen", "FAIL");
				else
					bReturn = true;
            }
            else if (modulePermission.equals("Yes")){

                if (getAllElementsBy(tileLocator).size() == 0 && getAllElementsBy(sideBarLocator).size() == 0)
                    Reporter.reportStep("Modules which User have permission are not available in Home Screen", "FAIL");
				else
					bReturn = true;
            }
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("User permission validation in Home Screen could not be completed", "FAIL");
		}
		return bReturn;
	}

	public boolean checkBoxFilterSelectionMethod(){
		boolean bReturn = false;

		try {
			//click filter icon
			waitTillLoadingCompletesBy(prop.getProperty("AllPage.FilterIcon"));
			clickBy(prop.getProperty("AllPage.FilterIcon"));
			waitForPageLoad();

			//select column selection dropdown arrow
			waitTillLoadingCompletesBy(prop.getProperty("AllPage.FilterColumnSelectionArrow"));
			//scrollBy(prop.getProperty("AllPage.FilterColumnSelectionArrow"), Keys.PAGE_UP);
			Thread.sleep(1000);
			clickBy(prop.getProperty("AllPage.FilterColumnSelectionArrow"));

			//click All to unselect it
			clickBy(prop.getProperty("AllPage.FilterColumnSelection_ClickAll"));

			//click column unique to the grid
			clickBy(prop.getProperty("AllPage.FilterSelectUniqueColumn"));
			scrollBy(prop.getProperty("AllPage.FilterSelectUniqueColumn"), Keys.PAGE_DOWN);
			Thread.sleep(1000);

			//click apply in filter popup
			waitTillLoadingCompletesBy(prop.getProperty("AllPage.FilterApplyIcon"));
			clickBy(prop.getProperty("AllPage.FilterApplyIcon"));
			waitForPageLoad();

			bReturn = true;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Filter could not be applied", "FAIL");
		}
		return bReturn;
	}

	public boolean checkBoxFilterMethodInScenarioSetupPage(){
		boolean bReturn = false;

		try {
			//click filter icon
			Thread.sleep(3000);
			waitTillLoadingCompletesBy(prop.getProperty("AllPage.FilterIcon"));
			clickBy(prop.getProperty("AllPage.FilterIcon"));
			waitForPageLoad();

			//select column selection dropdown arrow
			waitTillLoadingCompletesBy(prop.getProperty("AllPage.FilterColumnSelectionArrow"));
			//scrollBy(prop.getProperty("AllPage.FilterColumnSelectionArrow"), Keys.PAGE_UP);
			Thread.sleep(1000);
			clickBy(prop.getProperty("AllPage.FilterColumnSelectionArrow"));

			//click All to unselect it
			clickBy(prop.getProperty("AllPage.FilterColumnSelection_ClickAll"));

			//click column unique to the grid
			clickBy(prop.getProperty("AllPage.FilterSelectThirdColumn"));
			scrollBy(prop.getProperty("AllPage.FilterSelectThirdColumn"), Keys.PAGE_DOWN);

			//click apply in filter popup
			clickBy(prop.getProperty("AllPage.FilterApplyIcon"));
			waitForPageLoad();
			waitTillLoadingCompletesBy(prop.getProperty("AllPage.FilterIcon"));
			bReturn = true;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Filter could not be applied", "FAIL");
		}
		return bReturn;
	}

	public boolean checkBoxFilterMethodInStoreGroupingPage(){
		boolean bReturn = false;

		try {
			//click filter icon
			waitTime(2000);
			waitTillLoadingCompletesBy(prop.getProperty("StoreGrouping.FilterButton"));
			clickBy(prop.getProperty("StoreGrouping.FilterButton"));
			waitForPageLoad();

			//Click Number of Stores checkbox to un-select it
			waitTillLoadingCompletesBy(prop.getProperty("StoreGrouping.FilterNoOfStoresCheckbox"));
			waitTime(1000);
			clickBy(prop.getProperty("StoreGrouping.FilterNoOfStoresCheckbox"));
			waitTime(1000);

			//click apply in filter popup
			clickBy(prop.getProperty("AllPage.FilterApplyIcon"));
			waitForPageLoad();
			waitTime(1000);
			waitTillLoadingCompletesBy(prop.getProperty("StoreGrouping.FilterButton"));
			bReturn = true;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Store Grouping filter could not be applied", "FAIL");
		}
		return bReturn;
	}

	public boolean checkBoxSelectionMethod(String inputValues){
		boolean bReturn = false;

		try {
			Set<Integer> elementPosition = new TreeSet<Integer>();
			int count = -1;
			int position = 0;
			int flag;

			//split the given input
			String[] input = inputValues.split(",");

			//get the unique column in grid and traverse through each element
			List<WebElement> allElements = getAllElementsBy(prop.getProperty("AllPage.GridUniqueColumn"));			
			for (WebElement eachElement : allElements) {
				flag=position;

				//get text in each element and compare it with input
				String value = eachElement.getText();				
				for(int i=0;i<input.length;i++){
					if(value.trim().equals(input[i])){
						position++;

						//store position in tree set
						elementPosition.add(position);
						flag--;
						//eachElement.sendKeys(Keys.DOWN);
					}
				}
				//capture position of element even if input not matches
				if(flag==position){
					position++;
					//eachElement.sendKeys(Keys.DOWN);
				}			
			}
			//scroll to the top of the grid
			//scrollBy(prop.getProperty("AllPage.GridUniqueColumn"), Keys.HOME);
			System.out.println(elementPosition);

			//get all check box elements and traverse through it
			List<WebElement> allCheckBoxes = getAllElementsBy(prop.getProperty("AllPage.CheckBoxSelection"));
			for (WebElement eachCheckBox : allCheckBoxes) {
				count++;

				//click check box if position matches
				if(elementPosition.contains(count)){
					scrollIntoView(eachCheckBox);
					eachCheckBox.click();
					//eachCheckBox.sendKeys(Keys.DOWN);
				}
			}
			waitForPageLoad();
			bReturn = true;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("CheckBox selection could not be performed", "FAIL");			
		}
		return bReturn;
	}

	public boolean selectAllCheckBoxInPermissionAssignment(String checkBoxLocator, String setPermission){
		boolean bReturn = false;

		try {
			waitTillLoadingCompletesBy(checkBoxLocator);
			String checkBoxStatus = getElementAttributeValueBy(checkBoxLocator, "class");
			if (setPermission.equals("Grant")){
				if (!checkBoxStatus.contains("ui-grid-all-selected")){
					clickBy(checkBoxLocator);
					waitForPageLoad();
				}
			}
			else if (setPermission.equals("Deny")){
				if (checkBoxStatus.contains("ui-grid-all-selected")){
					clickBy(checkBoxLocator);
					waitForPageLoad();
				}
			}
			waitTime(2000);
			bReturn = true;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Select-All checkbox in Permission Assignment could not be completed", "FAIL");
		}
		return bReturn;
	}

	public boolean cloneModulesSwitchInPermAssign(String switchLocator, String cloneModule){
		boolean bReturn = false;

		try {
			waitTillLoadingCompletesBy(switchLocator);
			String switchStatus = getElementAttributeValueBy(switchLocator, "aria-checked");
			if (cloneModule.equals("Yes")){
                if (switchStatus.equalsIgnoreCase("false")){
                    clickBy(switchLocator);
                    waitForPageLoad();
                }
            }
            else if (cloneModule.equals("No")){
                if (switchStatus.equalsIgnoreCase("true")){
                    clickBy(switchLocator);
                    waitForPageLoad();
                }
            }
			waitTime(1000);
			bReturn = true;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Clone modules switch in Permission Assignment could not be completed", "FAIL");
		}
		return bReturn;
	}

	public boolean rulesResultValidationWithoutPopUp(String rulesResultLocator, String ruleName, String ruleSwitchLocator){
		boolean bReturn = false;

		try {
			String rulesResult = getElementAttributeValueBy(rulesResultLocator, "title");

			String rulesSwitchStatus = getElementAttributeValueBy(ruleSwitchLocator, "aria-checked");

			if (rulesSwitchStatus.equalsIgnoreCase("true")){

				if (rulesResult.equalsIgnoreCase("Success") || rulesResult.contains("Success") || rulesResult.contains("success") || rulesResult.contains("inserted")){

					Reporter.reportStep("Rule/Modifier: "+ruleName+" applied successfully with message: "+rulesResult, "PASS");
					bReturn = true;
				}
				else
					Reporter.reportStep("Please validate. Error- "+rulesResult+" while applying rule/modifier: "+ruleName, "WARNING");
			}
			else {
				Reporter.reportStep("Rule/Modifier: "+ruleName+"is not applied", "WARNING");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Rules Validation could not be performed", "FAIL");
		}
	return bReturn;
	}

	public boolean validateRulesSwitch(String ruleSwitchLocator){
		boolean bReturn = false;

		try {
			waitTillLoadingCompletesBy(ruleSwitchLocator);
			if (getElementAttributeValueBy(ruleSwitchLocator, "aria-checked").equalsIgnoreCase("true"))
				bReturn = true;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Rules switch validation could not be performed", "FAIL");
		}
		return bReturn;
	}

	public boolean iconDisableValidation(){
		boolean bReturn = false;

		try {
			if (getAllElementsBy(prop.getProperty("CSO_DSO_Create.IconDisableVal")).size() == 2)
				bReturn = true;
            else
				Reporter.reportStep("The icons like Dept, Footage Multiple, Grid etc. should not be enabled for Edit/Copy flow", "FAIL");

		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Icon disable validation could not be performed", "FAIL");
		}
		return bReturn;
	}

	public boolean iconDisableValForNewStoreScenario(){
		boolean bReturn = false;

		try {
			if (getAllElementsBy(prop.getProperty("CSO_DSO_Create.IconDisableVal")).size() == 1)
				bReturn = true;
			else
				Reporter.reportStep("The icons like Dept, Footage Multiple etc. should not be enabled for Edit/Copy flow of New Stores Scenario", "FAIL");

		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Icon disable validation could not be performed for New Stores Scenario", "FAIL");
		}
		return bReturn;
	}
	
	public boolean scrollIntoView(WebElement element){
		boolean bReturn = false;

		//scroll the webpage until the element is visible
		try {
			((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView();", element);
			Thread.sleep(500);
			bReturn = true;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Scroll to element could not be performed.", "FAILED");
		}
		return bReturn;		
	}

	public boolean messagePopUpHandle(){
		boolean bReturn = false;

		try {
			waitForPageLoad();
			//get popup message title and content
			waitTillLoadingCompletesBy(prop.getProperty("AllPage.PopUpTitle"));
			String messageTitle = getTextBy(prop.getProperty("AllPage.PopUpTitle"));
			String messageContent = getTextBy(prop.getProperty("AllPage.PopUpContent"));
			//verify the status
			if(messageTitle.contains("Success")){
				Reporter.reportStep(messageContent, "PASS");
				//click Ok button in popup
				clickBy(prop.getProperty("AllPage.PopUpOkButton"));
				waitForPageLoad();
				waitTime(4000);
				bReturn = true;
			}
			//verify the status
			else if(messageTitle.equalsIgnoreCase("Success")){
				Reporter.reportStep(messageContent, "PASS");
				//click Ok button in popup
				clickBy(prop.getProperty("AllPage.PopUpOkButton"));
				waitForPageLoad();
				waitTime(4000);
				bReturn = true;				
			}
			else{
				Reporter.reportStep(messageContent, "FAIL");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Message popup could not be handled", "FAIL");
		}
		return bReturn;
	}

	public boolean decisionPopUpHandle(String decision){
		boolean bReturn = false;

		try {
			waitForPageLoad();
			//get popup message title and content
			waitTillLoadingCompletesBy(prop.getProperty("AllPage.PopUpTitle"));
			String messageTitle = getTextBy(prop.getProperty("AllPage.PopUpTitle"));
			//verify the decision action
			if(decision.equals("Yes")){
				//click Yes button in popup
				clickBy(prop.getProperty("AllPage.BackPopUpYes"));
				waitForPageLoad();
				waitTime(4000);
				bReturn = true;
			}
			//verify the status
			else if(decision.equals("No")){
				//click No button in popup
				clickBy(prop.getProperty("AllPage.BackPopUpNo"));
				waitForPageLoad();
				waitTime(4000);
				bReturn = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Decision popup could not be handled", "FAIL");
		}
		return bReturn;
	}

	public boolean decisionPopUpHandleToSavePage(String decision){
		boolean bReturn = false;

		try {
			waitForPageLoad();
			if (getAllElementsBy(prop.getProperty("AllPage.PopUpTitle")).size() != 0){
				//verify the decision action
				if(decision.equals("Yes")){
					//click Yes button in popup
					clickBy(prop.getProperty("AllPage.BackPopUpYes"));
					waitForPageLoad();
					waitTime(2000);
				}
				//verify the status
				else if(decision.equals("No")){
					//click No button in popup
					clickBy(prop.getProperty("AllPage.BackPopUpNo"));
					waitForPageLoad();
					waitTime(2000);
				}
			}
			bReturn = true;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Decision popup to save page could not be handled", "FAIL");
		}
		return bReturn;
	}

	public boolean allPopUpHandleForLogOut(){
		boolean bReturn = false;

		try {
//			waitForPageLoad();
			//get popup message title and content
			waitTillLoadingCompletesBy(prop.getProperty("AllPage.PopUpTitle"));
			String messageTitle = getTextBy(prop.getProperty("AllPage.PopUpTitle"));
			String messageContent = getTextBy(prop.getProperty("AllPage.PopUpContent"));
			//verify the status
			if(messageTitle.contains("Success")){
				Reporter.reportStep(messageContent, "PASS");
				//click Ok button in popup
				clickBy(prop.getProperty("AllPage.PopUpOkButton"));
//				waitForPageLoad();
				Thread.sleep(3000);
				bReturn = true;
			}
			//verify the status
			else if(messageTitle.equalsIgnoreCase("Success")){
				Reporter.reportStep(messageContent, "PASS");
				//click Ok button in popup
				clickBy(prop.getProperty("AllPage.PopUpOkButton"));
//				waitForPageLoad();
				Thread.sleep(3000);
				bReturn = true;
			}
			else{
				Reporter.reportStep(messageContent, "FAIL");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Logout message popup could not be handled", "FAIL");
		}
		return bReturn;
	}

	public boolean backPopUpHandle(String proceedBack){
		boolean bReturn = false;

		try {
			//click back button
			waitTillLoadingCompletesBy(prop.getProperty("AllPage.BackButton"));
			clickBy(prop.getProperty("AllPage.BackButton"));
			waitTillLoadingCompletesBy(prop.getProperty("AllPage.BackPopUp"));
			//to select Yes in back popup
			if(proceedBack.equals("Yes")){
				clickBy(prop.getProperty("AllPage.BackPopUpYes"));
				waitForPageLoad();
				Thread.sleep(2000);
				bReturn = true;
			}
			//to select No in back popup
			else if(proceedBack.equals("No")){
				clickBy(prop.getProperty("AllPage.BackPopUpNo"));
				waitForPageLoad();
				Thread.sleep(2000);
				bReturn = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Back popup could not be handled", "FAIL");
		}
		return bReturn;
	}

	public boolean commentPopUpHandle(String commentValue){
		boolean bReturn = false;

		try {
			//wait till comment PopUp appears
			waitTillLoadingCompletesBy(prop.getProperty("AllPage.CommentPopUp"));

			if (!commentValue.equals("No"))
				//enter the comment and click apply
				enterBy(prop.getProperty("AllPage.CommentText"), commentValue);
			clickBy(prop.getProperty("AllPage.CommentApply"));
			waitForPageLoad();
			waitTime(2000);
			bReturn = true;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Comment PopUp could not be handled", "FAIL");
		}
		return bReturn;
	}

	public boolean storeSelectionPopUpHandle(){
		boolean bReturn = false;

		try {
			if (getAllElementsBy(prop.getProperty("AllPage.InfoPopUpHeader")).size() != 0){

				Reporter.reportStep("Re-Apply Rules message", "INFO");
                clickBy(prop.getProperty("AllPage.InfoPopUpOKButton"));
            }
			bReturn = true;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Rules re-apply pop-up could not be handled", "FAIL");
		}
		return bReturn;
	}

	/*public boolean finalLimitsPopUpHandle(){
		boolean bReturn = false;

		try {
			waitForPageLoad();
			waitTillLoadingCompletesBy(prop.getProperty("AllPage.PopUpTitle"));
			String messageTitle = getTextBy(prop.getProperty("AllPage.PopUpTitle"));
			String messageContent = getTextBy(prop.getProperty("AllPage.PopUpContent"));
			if(!messageTitle.equalsIgnoreCase("Success")){
				Reporter.reportStep(messageContent, "FAIL");
			}
			Reporter.reportStep(messageContent, "PASS");
			clickBy(prop.getProperty("AllPage.PopUpOkButton"));
			Thread.sleep(3000);
			waitForPageLoad();
			bReturn = true;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Message popup could not be handled", "FAIL");
		}
		return bReturn;
	}*/

	/*public boolean rulesPopUpHandle(){
		boolean bReturn = false;

		try {
			waitForPageLoad();
			waitTillLoadingCompletesBy(prop.getProperty("AllPage.PopUpTitle"));
			String messageTitle = getTextBy(prop.getProperty("AllPage.PopUpTitle"));
			String messageContent = getTextBy(prop.getProperty("AllPage.PopUpContent"));
			Reporter.reportStep(messageTitle+": "+messageContent, "INFO");
			clickBy(prop.getProperty("AllPage.PopUpOkButton"));
			waitForPageLoad();
			Thread.sleep(3000);
			bReturn = true;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Message popup could not be handled", "FAIL");
		}
		return bReturn;
	}*/

	public boolean allCSVUpload(String uploadIcon, String csvFilePath){
		boolean bReturn = false;

		try {
			//click CSV upload icon
//			waitTillLoadingCompletesBy(uploadIcon);
			/*clickBy(uploadIcon);
			waitForPageLoad();
			Thread.sleep(5000);
			//provide CSV filepath in the window popup
			robotKeyActions(csvFilePath);
			Thread.sleep(5000);
			waitForPageLoad();
			//handle the popup after CSV upload
			messagePopUpHandle();
			//Reporter.reportStep("CSV File Upload completed successfully", "PASS");*/
			String csvFile = "C:\\Users\\"+UserName+csvFilePath;
			Thread.sleep(2000);
			enterWithoutClearBy(uploadIcon, csvFile);
			waitForPageLoad();
			//handle the popup after CSV upload
			messagePopUpHandle();
			bReturn = true;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("CSV file upload could not be performed", "FAIL");
		}
		return bReturn;
	}

	public boolean rulesCSVUpload(String uploadLocator, String csvFilePath, String passUploadIcon, String failUploadIcon){
		boolean bReturn = false;

		try {
			String csvFile = "C:\\Users\\"+UserName+csvFilePath;
			waitForPageLoad();
			enterWithoutClearBy(uploadLocator, csvFile);
			waitForPageLoad();
			waitTime(2000);
			if (getElementAttributeValueBy(passUploadIcon, "aria-hidden").equals("false")) {

				clickBy(passUploadIcon);
				waitForPageLoad();
				waitTillLoadingCompletesBy(prop.getProperty("AllPage.CSVOverWriteHeader"));
				clickBy(prop.getProperty("CSO_DSO_HardLimits.CancelButton"));
				waitForPageLoad();
				waitTime(1000);
			}
			else if (getElementAttributeValueBy(failUploadIcon, "aria-hidden").equals("false")){

				clickBy(failUploadIcon);
				waitForPageLoad();
				waitTime(3000);
				keyBoardActions(Keys.ESCAPE);
				waitTime(1000);
			}
			bReturn = true;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Rules/Modifier CSV file upload could not be performed", "FAIL");
		}
		return bReturn;
	}

	/*public boolean checkBoxCSVUpload(String uploadIcon, String csvFilePath){
		boolean bReturn = false;

		try {
			waitTillLoadingCompletesBy(uploadIcon);
			clickBy(uploadIcon);
			Thread.sleep(2000);
			robotKeyActions(csvFilePath);
			waitForPageLoad();
			messagePopUpHandle();
			//Reporter.reportStep("CSV File Upload completed successfully", "PASS");
			bReturn = true;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("CSV file upload could not be performed", "FAIL");
		}
		return bReturn;
	}*/

	/**
	 * This method is used to handle file upload in window popup
	 *  
	 * @param filePath - String - relative file path of the CSV file
	 * @return boolean
	 */
	public boolean robotKeyActions(String filePath){
		boolean bReturn = false;
		try {
			//put path to your image in a clipboard
			StringSelection ss = new StringSelection(filePath);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);

			//imitate mouse events like ENTER, CTRL+C, CTRL+V
			Robot robot = new Robot();
			robot.delay(250);
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.delay(50);
			robot.keyRelease(KeyEvent.VK_ENTER);
			bReturn = true;
		} catch (AWTException e) {
			e.printStackTrace();
			Reporter.reportStep("File Upload could not be performed.", "FAIL");
		}
		return bReturn;
	}

	public boolean robotKeyActionsForDownload(String filePath){
		boolean bReturn = false;
		try {
			//put path to your image in a clipboard
			StringSelection ss = new StringSelection(filePath);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);

			//imitate mouse events like ENTER, CTRL+C, CTRL+V
			Robot robot = new Robot();
			robot.delay(250);
			/*robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);*/
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);
			robot.delay(100);
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);
			bReturn = true;
		} catch (AWTException e) {
			e.printStackTrace();
			Reporter.reportStep("File/Report Download could not be performed.", "FAIL");
		}
		return bReturn;
	}

	public void addScenarioIDWithScenarioName(){

		//adding Scenario ID with Scenario name for displaying in report
		String scenarios = "";
		if (scenario_Name == null)
			scenarios = scenarioID+" - "+testCaseName;
		else
			scenarios = scenarioID+" - "+scenario_Name;
		resultSet.add(scenarios);
		//make ***Scenario ID*** as null every time after addition
		scenarioID = null;
		scenario_Name = null;
	}

	public void addObjectiveIDWithObjectiveName(){

		//adding Objective ID with Objective name for displaying in report
		String objectives = "";
		if (objective_Name == null)
			objectives = objectiveID+" - "+testCaseName;
		else
			objectives = objectiveID+" - "+objective_Name;
		resultSet.add(objectives);
		//make ***Objective ID*** as null every time after addition
		objectiveID = null;
		objective_Name = null;
	}

	public boolean getObjectiveIdInObjectiveSetupScreen(String locatorVal){
		boolean bReturn = false;

		try {
			//get text in Objective ID field, split and store the objective id alone
			String[] textInLocator = getTextBy(locatorVal).split(" ");
			objectiveID = textInLocator[2];
			savedID = objectiveID;
			Reporter.reportStep("Objective ID: "+objectiveID+" has been successfully captured", "PASS");
			bReturn = true;

		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Objective ID could not be captured", "FAIL");
		}
		return bReturn;
	}

	public boolean getScenarioId(String locatorVal){
		boolean bReturn = false;

		try {
			//get text in Scenario ID field, split and store the scenario id alone
			String[] textInLocator = getTextBy(locatorVal).split(" ");
			scenarioID = textInLocator[3];
			savedID = scenarioID;
			Reporter.reportStep("Scenario ID: "+scenarioID+" has been successfully captured", "PASS");
			bReturn = true;

		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Scenario ID could not be captured", "FAIL");
		}
		return bReturn;	
	}

	public boolean verifyPlanTypeInCSOStoreSelection(String userPlanType){
		boolean bReturn = false;
//		String planType;

		try {
			//get text in Scenario Type field, split and store the scenario type alone
			String[] textInLocator = getTextBy(prop.getProperty("CSO_DSO_StoreSelection.GetPlanType")).split(" ");
			String planType = textInLocator[3];
			if (!userPlanType.equalsIgnoreCase(planType))
				Reporter.reportStep("User provided Plan Type: "+userPlanType+" doesn't match with system plan type: "+planType, "FAIL");
			bReturn = true;

			/*try {
			if (userPlanType.equalsIgnoreCase("Basic"))
				planType = getTextBy(prop.getProperty("CSO_DSO_StoreSelection.GetPlanType"));
			else
				planType = getTextBy(prop.getProperty("CSO_DSO_StoreSelection.PlanType"));
			if (!planType.contains(userPlanType))
				Reporter.reportStep("User provided Plan Type: "+userPlanType+" doesn't match with system plan type: "+planType, "FAIL");
			else
				bReturn = true;*/
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Plan Type verification could not be performed.", "FAIL");
		}
		return bReturn;
	}

	public boolean marketShareDataScienceMethod(String marketShareMinValues, String marketShareMaxValues, String categories, String storeGroups){
		boolean bReturn = false;

		try {
			//get market share min & max values, split and store it
			String[] min = marketShareMinValues.split(",");
			String[] max = marketShareMaxValues.split(",");

			//apply market share value in UI for every iteration
			for(int i=0; i<min.length; i++){
				new CSO_DSO_SpaceRulesPage()
				.enterMinValueInMarketSharePage(min[i])
				.enterMaxValueInMarketSharePage(max[i])
				.clickApplyInMarketSharePage();

				//store the data for every iteration from DB
				DatabaseConnect.marketShareDBConnect();
				new CSO_DSO_SpaceRulesPage().clickMarketShareRulesIcon();
			}
			clickRandom();
			//new CSO_DSO_MarketShareRulesPage().clickCloseInMarketSharePage();
			/*System.out.println("Size of final list: "+MarketShareList.size());
			for (MarketShare_DataScience market : MarketShareList) {
				System.out.println(market.getScenarioId()+", "+market.getStoreId()+", "+market.getCatId()+", "+market.getMinFootageVal()+", "+market.getMaxFootageVal());
			}*/
			//Write to excel sheet after all iterations are over
			DatabaseConnect.writeMarketShareData(marketShareExcelFileName, categories, storeGroups);
			bReturn = true;			
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Market Share Data Science Iteration could not be performed.", "FAIL");
		}
		return bReturn;
	}

	public boolean verifyAllDropDownValues(String locatorVal, String dropDownLocator, String inputValues){
		boolean bReturn = false;

		try {
			String[] input = inputValues.split(",");
			int count = 0;

			waitTillLoadingCompletesBy(locatorVal);
			clickBy(locatorVal);

			waitTillLoadingCompletesBy(dropDownLocator);
			List<WebElement> dropDownValues = getAllElementsBy(dropDownLocator);

			for (WebElement value : dropDownValues) {
				String valueInDropDown = value.getText();

				for(int i=0;i<input.length;i++){
					if(valueInDropDown.contains(input[i])){
						count++;
					}
				}
			}
			if(input.length == count)
				Reporter.reportStep("The given inputs "+"("+inputValues+")"+" with count: "+input.length+" and the dropdown values count: "+count+" are equal which ensures all values are present.", "PASS");
			else
				Reporter.reportStep("The given inputs "+"("+inputValues+")"+" with count: "+input.length+" and the dropdown values count: "+count+" are not equal which shows values are missing.", "FAIL");
			refreshPage();
			waitForPageLoad();
			bReturn = true;			
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Verify dropdown values could not be performed.", "FAIL");
		}
		return bReturn;		
	}

	public boolean verifyDropDownValuesCount(String locatorVal, String valueLocator, String dropDownLocator){
		boolean bReturn = false;

		try {
			String input = getTextBy(valueLocator);
			int value = Integer.parseInt(input.trim());

			waitTillLoadingCompletesBy(locatorVal);
			clickBy(locatorVal);

			waitTillLoadingCompletesBy(dropDownLocator);
			List<WebElement> dropDownValues = getAllElementsBy(dropDownLocator);
			int count = dropDownValues.size();

			if(count == value)
				Reporter.reportStep("The dropdown values count: "+count+" matches with value: "+value, "PASS");
			else
				Reporter.reportStep("The dropdown values count: "+count+" does not matches with value: "+value, "FAIL");
			refreshPage();
			waitForPageLoad();
			bReturn = true;			
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Verify dropdown values count could not be performed.", "FAIL");
		}
		return bReturn;		
	}

	public boolean clickRandom(){
		boolean bReturn = false;

		try {
			Actions builder = new Actions(driver);
//			builder.sendKeys(Keys.ESCAPE).build().perform();
			builder.moveByOffset(50, 50).click().build().perform();
			bReturn = true;

		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Mouse random click could not be performed.", "FAIL");
		}
		return bReturn;
	}

	public boolean clickBreadCrumbIcon(String iconLocator, String destinationPageLocator){
		boolean bReturn = false;

		try {
			if(isElementEnabledBy(iconLocator)){
				clickBy(iconLocator);
				waitForPageLoad();
				waitTillLoadingCompletesBy(destinationPageLocator);
				bReturn = true;
			}
			else
				Reporter.reportStep("Requested Icon is not enabled", "WARNING");

		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Requested Icon could not be clicked.", "FAIL");
		}
		return bReturn;
	}

	public boolean validateRuleBeforeApplying(String ruleSwitchLocator, String ruleResultLocator, String ruleName){
		boolean bReturn = false;

		try {
			if (getElementAttributeValueBy(ruleSwitchLocator, "aria-checked").equalsIgnoreCase("true") && getTextBy(ruleResultLocator).contains("uccessful")){

				Reporter.reportStep("The "+ruleName+" rule/modifier has been applied", "PASS");
				bReturn = true;
			}
			else if (getElementAttributeValueBy(ruleSwitchLocator, "aria-checked").equalsIgnoreCase("true") || getTextBy(ruleResultLocator).contains("uccessful"))
				Reporter.reportStep("Rule switch & success message are not in sync for rule/modifier: "+ruleName, "FAIL");
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Validating rule before applying could not be completed.", "FAIL");
		}
		return bReturn;
	}

	public boolean validateRulesResult(String ruleResultLocator, String ruleSwitchLocator, String ruleName) {
		boolean bReturn = false;

		try {
			/*waitTillLoadingCompletesBy(applyButtonLocator);
			clickBy(applyButtonLocator);
			waitForPageLoad();
			Thread.sleep(3000);*/
			if (getElementAttributeValueBy(ruleSwitchLocator, "aria-checked").equalsIgnoreCase("true") && getCSSValueBy(ruleResultLocator, "color").equalsIgnoreCase("rgba(8, 189, 40, 1)")){

				Reporter.reportStep("The "+ruleName+" rule/modifier has been successfully applied", "PASS");
				bReturn = true;
			}
			else
				Reporter.reportStep("The "+ruleName+" rule/modifier could not be applied", "WARNING");
			bReturn = true;
			/*else if (getElementAttributeValueBy(ruleSwitchLocator, "aria-checked").equalsIgnoreCase("true") && getTextBy(ruleResultLocator).isEmpty())
				Reporter.reportStep("Rule switch & success message are not in sync for rule/modifier: "+ruleName, "FAIL");

			else if (getElementAttributeValueBy(ruleSwitchLocator, "aria-checked").equalsIgnoreCase("false") && getTextBy(ruleResultLocator).contains("failed")){

				Reporter.reportStep("The "+ruleName+" rule/modifier has been failed", "WARNING");
				bReturn = true;
			}*/
			/*String result = getTextBy(resultLocator);
			Reporter.reportStep("The result for applied rule/modifier is: " + result, "INFO");
			bReturn = true;*/

		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Rules result validation could not be performed.", "FAIL");
		}
		return bReturn;
	}

	public boolean validateCSVRulesResult(String ruleResultLocator, String ruleSwitchLocator, String ruleName) {
		boolean bReturn = false;

		try {
			waitForPageLoad();
			Thread.sleep(3000);
			if (getElementAttributeValueBy(ruleSwitchLocator, "aria-checked").equalsIgnoreCase("true") && getTextBy(ruleResultLocator).contains("uccessful")){

				Reporter.reportStep("The "+ruleName+" rule/modifier has been successfully applied", "PASS");
				bReturn = true;
			}
			else if (getElementAttributeValueBy(ruleSwitchLocator, "aria-checked").equalsIgnoreCase("true") && getTextBy(ruleResultLocator).isEmpty())
				Reporter.reportStep("Rule switch & success message are not in sync for rule/modifier: "+ruleName, "FAIL");

			else if (getElementAttributeValueBy(ruleSwitchLocator, "aria-checked").equalsIgnoreCase("false") && getTextBy(ruleResultLocator).contains("failed")){

				Reporter.reportStep("The "+ruleName+" rule/modifier has been failed", "WARNING");
				bReturn = true;
			}
			else if (getElementAttributeValueBy(ruleSwitchLocator, "aria-checked").equalsIgnoreCase("true") && getTextBy(ruleResultLocator).contains("inserted")){

				Reporter.reportStep("The "+ruleName+" rule/modifier has been successfully applied", "WARNING");
				bReturn = true;
			}
			/*String result = getTextBy(resultLocator);
			Reporter.reportStep("The result for applied rule/modifier is: " + result, "INFO");
			bReturn = true;*/

		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Rules result validation could not be performed.", "FAIL");
		}
		return bReturn;
	}

	public boolean searchForUniqueIDAllPages(String searchValue){
		boolean bReturn = false;

		try {
			waitTillLoadingCompletesBy(prop.getProperty("AllPage.SearchIcon"));
			clickBy(prop.getProperty("AllPage.SearchIcon"));

//			waitForPageLoad();
			waitTillLoadingCompletesBy(prop.getProperty("AllPage.SearchTextBox"));

			enterBy(prop.getProperty("AllPage.SearchTextBox"), searchValue);

			waitTillLoadingCompletesBy(prop.getProperty("AllPage.CheckBoxUnique"));
//			waitForPageLoad();
			bReturn = true;

		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Search for Unique Id could not be performed.", "FAIL");
		}
		return bReturn;
	}

	public Map<Integer, String> getScenarioStatusandIDInCSO_DSOHome(String searchValue){

		Map<Integer, String> scenarioIDStatus = new LinkedHashMap<>();
		try {
			String[] input = searchValue.split(",");
			//store grid element locator value to String
			String gridLocator = prop.getProperty("AllPage.GridUniqueColumn");
//			int count = 1;
			String scenarioId = null, scenarioType = null, planStatus = null;

			//get total number of elements displayed for the search result
			int gridElementsSize = getAllElementsBy(gridLocator).size();

			//traverse through scenario Id field in each column
			for (int i=1; i<=gridElementsSize; i = i+11){
//				count++;
				scenarioId = getTextBy(gridLocator+"["+i+"]");

				//for each value in input, search for objective id
				for (int j=0; j<input.length; j++){

					//check given seach text equals scenario Id, else re-iterate
					if(scenarioId.equals(input[j])){
						int typePosition = i+8;
						int statusPosition  = i+9;
						//get scenario type and plan status
						scenarioType = getTextBy(gridLocator+"["+typePosition+"]");
						planStatus = getTextBy(gridLocator+"["+statusPosition+"]");
						String sceTypePlan = scenarioType+"-"+planStatus;
						int sceID = Integer.parseInt(scenarioId);
						scenarioIDStatus.put(sceID, sceTypePlan);
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Search and Find Status in CSO Home Page could not be performed.", "FAIL");
		}
		return scenarioIDStatus;
	}

	public boolean checkScenarioStatusAndActionToPerform(String searchValue, String iconLocator, String actionType){
		boolean bReturn = false;

		try {
//			clickBy(prop.getProperty("AllPage.SelectAllCheckBox"));
			//get Objective id,status & store it in map
			Map<Integer, String> scenarioIDStatus = getScenarioStatusandIDInCSO_DSOHome(searchValue);

			Reporter.reportStep("Scenario IDs & it's status are: "+scenarioIDStatus.entrySet(), "INFO");

			//check the correponding button is enabled for the required action (like delete, edit, copy etc.)
			if(!isElementEnabledBy(iconLocator)){
				Reporter.reportStep("The requested action: "+actionType+" icon is not enabled for the given scenario/s"+searchValue, "FAIL");
			}
			bReturn = true;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Check Scenario status & Action To perform could not be completed", "FAIL");
		}
		return bReturn;
	}

	public boolean validateActionPerformedInCSO_DSOHome(String searchValue, String actionType, String expectedStatus){
		boolean bReturn = false;

		try {
			//get Objective id,status & store it in map
			Map<Integer, String> scenarioIDStatus = getScenarioStatusandIDInCSO_DSOHome(searchValue);

			for (Map.Entry<Integer, String> objMap: scenarioIDStatus.entrySet()) {

				String sceTypeStatus[] = objMap.getValue().split("-");
				if (sceTypeStatus[1].equalsIgnoreCase(expectedStatus))
					Reporter.reportStep("The required action: "+actionType+" for Scenario ID: "+objMap.getKey()+" has been validated successfully.", "PASS");
				else
					Reporter.reportStep("The required action: "+actionType+" for Scenario ID: "+objMap.getKey()+" is not completed.", "FAIL");
			}
//			clickBy(prop.getProperty("AllPage.SelectAllCheckBox"));
			bReturn = true;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Validating the action performed could not be completed.", "FAIL");
		}
		return bReturn;
	}

	public boolean searchAndValidateActionInCSOHome(String searchValue, String iconLocator, String actionType){
		boolean bReturn = false;

		try {
			//store check box locator value to String
			String checkBoxLocator = prop.getProperty("AllPage.CheckBoxSelection");

			//click the search icon
			waitTillLoadingCompletesBy(prop.getProperty("AllPage.SearchIcon"));
			clickBy(prop.getProperty("AllPage.SearchIcon"));

			//enter the search value in the text box
			waitForPageLoad();
			waitTillLoadingCompletesBy(prop.getProperty("AllPage.SearchTextBox"));
			enterBy(prop.getProperty("AllPage.SearchTextBox"), searchValue);
			waitForPageLoad();

			//search for given serachValue and find the corresponding status and store the values
			Object[] values = getPlanStatusInCSOHome(searchValue);
			String countVal = values[3].toString();
			int count = Integer.parseInt(countVal);
			String scenarioId = values[0].toString();
			String scenarioType = values[1].toString();
			String planStatus = values[2].toString();

			//click the corresponding checkbox
			clickBy(checkBoxLocator+"["+count+"]");
			waitForPageLoad();
			//check the correponding button is enabled for the required action (like delete, archive etc.)
			if(!isElementEnabledBy(iconLocator)){
				Reporter.reportStep("For Scenario ID: "+scenarioId+" with Scenario Type: "+scenarioType+" and Status: "+planStatus+". The requested action: "+actionType+" could not be performed.", "FAIL");
			}
			bReturn = true;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Search and Select could not be performed.", "FAIL");
		}
		return bReturn;
	}

	public Object[] getPlanStatusInCSOHome(String searchValue){
		Object[] oReturn = {"","","",""};

		try {
			//store grid element locator value to String
			String gridLocator = prop.getProperty("AllPage.GridUniqueColumn");
			int count = 1;
			String scenarioId = null, scenarioType = null, planStatus = null;

			//get total number of elements displayed for the search result
			int gridElementsSize = getAllElementsBy(gridLocator).size();

			//traverse through scenario Id field in each column
			for (int i=1; i<=gridElementsSize; i = i+11){
                count++;
                scenarioId = getTextBy(gridLocator+"["+i+"]");

                //check given seach text equals scenario Id, else re-iterate
                if(scenarioId.equals(searchValue)){
                    int typePosition = i+8;
                    int statusPosition  = i+9;
                    //get scenario type and plan status
                    scenarioType = getTextBy(gridLocator+"["+typePosition+"]");
                    planStatus = getTextBy(gridLocator+"["+statusPosition+"]");
                    break;
                }
            }
			oReturn[0] = scenarioId;
			oReturn[1] = scenarioType;
			oReturn[2] = planStatus;
			oReturn[3] = count;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Search and Find Status in CSO Home Page could not be performed.", "FAIL");
		}
		return oReturn;
	}

	public boolean validateActionPerformedInCSOHome(String searchValue, String actionType, String expectedStatus){
		boolean bReturn = false;

		try {

			//search for given serachValue and find the corresponding status and store the values
			Object[] values = getPlanStatusInCSOHome(searchValue);
			String countVal = values[3].toString();
//			int count = Integer.parseInt(countVal);
			String scenarioId = values[0].toString();
//			String scenarioType = values[1].toString();
			String planStatus = values[2].toString();

			if (planStatus.equalsIgnoreCase(expectedStatus))
                Reporter.reportStep("The required action: "+actionType+" for Scenario ID: "+scenarioId+" has been validated successfully.", "PASS");
            else
                Reporter.reportStep("The required action: "+actionType+" for Scenario ID: "+scenarioId+" is not completed.", "FAIL");
			bReturn = true;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Validating the action performed could not be completed.", "FAIL");
		}
		return bReturn;
	}

	public boolean validateDeleteAction(String searchValue){
		boolean bReturn = false;

		try {
			/*//Split the input values & store in array
			String[] inputVal = searchValue.split(",");

			//Click the filter icon
			waitTillLoadingCompletesBy(prop.getProperty("AllPage.FilterIcon"));
			clickBy(prop.getProperty("AllPage.FilterIcon"));
			waitTillLoadingCompletesBy(prop.getProperty("AllPage.FilterFirstColumnValue"));

			//Enter the values in the Filter text box
			for (int i=0; i<inputVal.length; i++){

				enterWithoutClearBy(prop.getProperty("AllPage.FilterFirstColumnValue"), inputVal[i]);
				scrollBy(prop.getProperty("AllPage.FilterFirstColumnValue"), Keys.ENTER);
			}

			//Apply filter & select all checkbox
			waitTillLoadingCompletesBy(prop.getProperty("AllPage.FilterApplyIcon"));
			clickBy(prop.getProperty("AllPage.FilterApplyIcon"));

			waitForPageLoad();
			waitTillLoadingCompletesBy(prop.getProperty("AllPage.SelectAllCheckBox"));*/

			//check Grid is empty, if empty then plan got deleted successfully
			if (getAllElementsBy(prop.getProperty("AllPage.GridUniqueColumn")).size() == 0)
				Reporter.reportStep("Plan IDs: "+searchValue+" got deleted successfully", "PASS");
			else
				Reporter.reportStep("Plan IDs: "+searchValue+" is not deleted. Please check.", "FAIL");

			waitForPageLoad();
			bReturn = true;

			/*List<WebElement> gridElement = getAllElementsBy(prop.getProperty("AllPage.GridUniqueColumn"));
			if (!gridElement.isEmpty()){

				Reporter.reportStep("Delete action is not successful.", "FAIL");
			}*/
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Delete action could not be performed", "FAIL");
		}
		return bReturn;
	}

	/*public boolean validateDeleteActionInOC(){
		boolean bReturn = false;

		try {
			List<WebElement> gridElement = driver.findElements(locatorSplit(prop.getProperty("AllPage.GridUniqueColumn")));
			if (!gridElement.isEmpty()){
				Object[] values = getObjectiveStatusInOCHome(objectiveID);
				String objId = values[0].toString();
				if (objectiveID.equals(objId))
					Reporter.reportStep("Delete action is not successful.", "FAIL");
			}
		} catch (NoSuchElementException e) {
			Reporter.reportStep("Delete action has been performed successfully.", "PASS");
			bReturn = true;
		}
		return bReturn;
	}*/

	public Object[] getObjectiveStatusInOCHome(String searchValue){
		Object[] oReturn = {"","",""};

		try {
			//store grid element locator value to String
			String gridLocator = prop.getProperty("AllPage.GridUniqueColumn");
			int count = 1;
			String objectiveId = null, objectiveStatus = null;

			//get total number of elements displayed for the search result
			int gridElementsSize = getAllElementsBy(gridLocator).size();

			//traverse through objective Id field in each column
			for (int i=1; i<=gridElementsSize; i = i+8){
				count++;
				objectiveId = getTextBy(gridLocator+"["+i+"]");

				//check given search text equals objective Id, else re-iterate
				if(objectiveId.equals(searchValue)){
					int statusPosition  = i+7;
					//get objective status
					objectiveStatus = getTextBy(gridLocator+"["+statusPosition+"]");
					break;
				}
			}

			oReturn[0] = objectiveId;
			oReturn[1] = objectiveStatus;
			oReturn[2] = count;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Search and Find Status in OC Home Page could not be performed.", "FAIL");
		}
		return oReturn;
	}

	public Map<Integer, String> getObjectiveStatusandIDInOCHome(String searchValue){

		Map<Integer, String> objIDStatus = new LinkedHashMap<>();
		try {
			String[] input = searchValue.split(",");
			//store grid element locator value to String
			String gridLocator = prop.getProperty("AllPage.GridUniqueColumn");
//			int count = 1;
			String objectiveId = null, objectiveStatus = null;

			//get total number of elements displayed for the search result
			int gridElementsSize = getAllElementsBy(gridLocator).size();

			//traverse through objective Id field in each column
			for (int i=1; i<=gridElementsSize; i=i+8){
//				count++;
				objectiveId = getTextBy(gridLocator+"["+i+"]");

				//for each value in input, search for objective id
				for (int j=0; j<input.length; j++){
					//check given search text equals objective Id, else re-iterate
					if(objectiveId.equals(input[j])){
						int statusPosition  = i+7;
						//get objective status
						objectiveStatus = getTextBy(gridLocator+"["+statusPosition+"]");
						int objID = Integer.parseInt(objectiveId);
						objIDStatus.put(objID, objectiveStatus);
						break;
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Search and Find Status in OC Home Page could not be performed.", "FAIL");
		}
		return objIDStatus;
	}

	public boolean selectObjectiveIDUsingSearchBox(String searchValue){
		boolean bReturn = false;

		try {
			//store check box locator value to String
			String checkBoxLocator = prop.getProperty("AllPage.CheckBoxSelection");

			//click the search icon
			waitTillLoadingCompletesBy(prop.getProperty("AllPage.SearchIcon"));
			clickBy(prop.getProperty("AllPage.SearchIcon"));

			//enter the search value in the text box
			waitForPageLoad();
			waitTillLoadingCompletesBy(prop.getProperty("AllPage.SearchTextBox"));
			enterBy(prop.getProperty("AllPage.SearchTextBox"), searchValue);
			waitForPageLoad();

			//search for given serachValue and find the corresponding status and store the values
			Object[] values = getObjectiveStatusInOCHome(searchValue);
			String countVal = values[2].toString();
			int count = Integer.parseInt(countVal);

			//click the corresponding checkbox
			clickBy(checkBoxLocator+"["+count+"]");
			waitForPageLoad();
			bReturn = true;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Objective ID selection using searchbox could not be performed", "FAIL");
		}
		return bReturn;
	}

	public boolean selectPlanIDsInHomePageGrid(String searchValue){
		boolean bReturn = false;

		try {
			//Split the input values & store in array
			String[] inputVal = searchValue.split(",");

			//Click the filter icon
			waitTillLoadingCompletesBy(prop.getProperty("AllPage.FilterIcon"));
			clickBy(prop.getProperty("AllPage.FilterIcon"));
			waitTillLoadingCompletesBy(prop.getProperty("AllPage.FilterFirstColumnValue"));

			//Enter the values in the Filter text box
			for (int i=0; i<inputVal.length; i++){

				enterWithoutClearBy(prop.getProperty("AllPage.FilterFirstColumnValue"), inputVal[i]);
				scrollBy(prop.getProperty("AllPage.FilterFirstColumnValue"), Keys.ENTER);
			}

			//Apply filter & select all checkbox
			waitTillLoadingCompletesBy(prop.getProperty("AllPage.FilterApplyIcon"));
			scrollIntoView(driver.findElement(locatorSplit(prop.getProperty("AllPage.FilterApplyIcon"))));
			clickBy(prop.getProperty("AllPage.FilterApplyIcon"));

			waitForPageLoad();
			waitTillLoadingCompletesBy(prop.getProperty("AllPage.SelectAllCheckBox"));

			//check Grid is empty, if not then select all checkboxes available
			if (getAllElementsBy(prop.getProperty("AllPage.GridUniqueColumn")).size() == 0)
				Reporter.reportStep("Plan IDs provided is not available", "FAIL");
			else
				clickBy(prop.getProperty("AllPage.SelectAllCheckBox"));

			waitForPageLoad();
			bReturn = true;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Plan IDs selection using filter could not be performed", "FAIL");
		}
		return bReturn;
	}

	public boolean selectCategoriesInAnyGrid(String deptValue, String catValue){
		boolean bReturn = false;

		try {
			//Split the input values & store in array
			String[] deptVal = deptValue.split(",");
			String[] catVal = catValue.split(",");

			//Click the filter icon
			waitTillLoadingCompletesBy(prop.getProperty("AllPage.FilterIcon"));
			clickBy(prop.getProperty("AllPage.FilterIcon"));
			waitTillLoadingCompletesBy(prop.getProperty("AllPage.FilterFirstColumnValue"));

			//Enter the dept values
			for (int j=0; j<deptVal.length; j++){

				String dept = deptVal[j].substring(1);
				enterWithoutClearBy(prop.getProperty("AllPage.FilterFirstColumnValue"), dept);
				scrollBy(prop.getProperty("AllPage.FilterFirstColumnValue"), Keys.ENTER);
			}
			waitTillLoadingCompletesBy(prop.getProperty("AllPage.FilterThirdColumnValue"));

			//Enter the category values
			for (int i=0; i<catVal.length; i++){

				enterWithoutClearBy(prop.getProperty("AllPage.FilterThirdColumnValue"), catVal[i]);
				scrollBy(prop.getProperty("AllPage.FilterThirdColumnValue"), Keys.ENTER);
			}

			//Apply filter & select all checkbox
			waitTillLoadingCompletesBy(prop.getProperty("AllPage.FilterApplyIcon"));
			clickBy(prop.getProperty("AllPage.FilterApplyIcon"));

			waitForPageLoad();
			waitTillLoadingCompletesBy(prop.getProperty("AllPage.SelectAllCheckBox"));

			//check Grid is empty, if not then select all checkboxes available
			if (getAllElementsBy(prop.getProperty("AllPage.GridUniqueColumn")).size() == 0)
				Reporter.reportStep("Categories provided is not available", "FAIL");
			else
				clickBy(prop.getProperty("AllPage.SelectAllCheckBox"));

			waitForPageLoad();
			bReturn = true;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Categories selection using filter could not be performed", "FAIL");
		}
		return bReturn;
	}

	/*public boolean selectPlanIDsInHomePageGrid(String searchValue){
		boolean bReturn = false;

		try {
			String[] search = searchValue.split(",");
			if (search.length==1) {
				selectObjectiveIDUsingSearchBox(searchValue);
				bReturn = true;
			}
            else {
				selectObjectiveIDsUsingFilter(searchValue);
				bReturn = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Objective ID selection could not be performed.", "FAIL");
		}
		return bReturn;
	}*/

	public boolean checkObjectiveStatusAndActionToPerform(String searchValue, String iconLocator, String actionType){
		boolean bReturn = false;

		try {
//			clickBy(prop.getProperty("AllPage.SelectAllCheckBox"));
			//get Objective id,status & store it in map
			Map<Integer, String> objIDStatus = getObjectiveStatusandIDInOCHome(searchValue);

			Reporter.reportStep("Objective IDs & it's status are: "+objIDStatus.entrySet(), "INFO");

			//check the correponding button is enabled for the required action (like delete, edit, copy etc.)
			if(!isElementEnabledBy(iconLocator)){
                Reporter.reportStep("The requested action: "+actionType+" icon is not enabled for the given objective/s"+searchValue, "FAIL");
            }
			bReturn = true;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Check Objective status & Action To perform could not be completed", "FAIL");
		}
		return bReturn;
	}

	public boolean validateActionPerformedInOCHome(String searchValue, String actionType, String expectedStatus){
		boolean bReturn = false;

		try {
			//get Objective id,status & store it in map
			Map<Integer, String> objIDStatus = getObjectiveStatusandIDInOCHome(searchValue);

			for (Entry<Integer, String> objMap: objIDStatus.entrySet()) {

				if (objMap.getValue().equalsIgnoreCase(expectedStatus))
					Reporter.reportStep("The required action: "+actionType+" for Objective ID: "+objMap.getKey()+" has been validated successfully.", "PASS");
				else
					Reporter.reportStep("The required action: "+actionType+" for Objective ID: "+objMap.getKey()+" is not completed.", "FAIL");
			}
//			clickBy(prop.getProperty("AllPage.SelectAllCheckBox"));
			bReturn = true;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Validating the action performed could not be completed.", "FAIL");
		}
		return bReturn;
	}

	public boolean searchAndValidateActionInObjectiveHome(String searchValue, String iconLocator, String actionType){
		boolean bReturn = false;

		try {
			//store check box locator value to String
			String checkBoxLocator = prop.getProperty("AllPage.CheckBoxSelection");

			//click the search icon
			waitTillLoadingCompletesBy(prop.getProperty("AllPage.SearchIcon"));
			clickBy(prop.getProperty("AllPage.SearchIcon"));

			//enter the search value in the text box
			waitForPageLoad();
			waitTillLoadingCompletesBy(prop.getProperty("AllPage.SearchTextBox"));
			enterBy(prop.getProperty("AllPage.SearchTextBox"), searchValue);
			waitForPageLoad();

			//search for given serachValue and find the corresponding status and store the values
			Object[] values = getObjectiveStatusInOCHome(searchValue);
			String countVal = values[2].toString();
			int count = Integer.parseInt(countVal);
			String objectiveId = values[0].toString();
			String objectiveStatus = values[1].toString();

			//click the corresponding checkbox
			clickBy(checkBoxLocator+"["+count+"]");
			waitForPageLoad();
			//check the correponding button is enabled for the required action (like delete, archive etc.)
			if(!isElementEnabledBy(iconLocator)){
				Reporter.reportStep("The requested action: "+actionType+" could not be performed (Icon Disabled) for Objective ID: "+objectiveId+" with status: "+objectiveStatus, "FAIL");
			}
			bReturn = true;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Search and Select could not be performed.", "FAIL");
		}
		return bReturn;
	}

	public boolean validateActionPerformedInObjectiveHome(String searchValue, String actionType, String expectedStatus){
		boolean bReturn = false;

		try {

			//search for given searchValue and find the corresponding status and store the values
			Object[] values = getObjectiveStatusInOCHome(searchValue);
			String objectiveId = values[0].toString();
			String objectiveStatus = values[1].toString();

			if (objectiveStatus.equalsIgnoreCase(expectedStatus))
				Reporter.reportStep("The required action: "+actionType+" for Objective ID: "+objectiveId+" has been validated successfully.", "PASS");
			else
				Reporter.reportStep("The required action: "+actionType+" for Objective ID: "+objectiveId+" is not completed.", "FAIL");
			bReturn = true;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Validating the action performed could not be completed.", "FAIL");
		}
		return bReturn;
	}

	public boolean selectAllColumnsInAnyGridFilter(){
		boolean bReturn = false;

		try {
			waitTillLoadingCompletesBy(prop.getProperty("AllPage.FilterIcon"));
			clickBy(prop.getProperty("AllPage.FilterIcon"));
			waitTime(2000);
			waitTillLoadingCompletesBy(prop.getProperty("AllPage.FilterColumnSelectionArrow"));
			clickBy(prop.getProperty("AllPage.FilterColumnSelectionArrow"));
			waitTime(1000);
			waitTillLoadingCompletesBy(prop.getProperty("AllPage.FilterColumnSelection_ClickAll"));
			clickBy(prop.getProperty("AllPage.FilterColumnSelection_ClickAll"));
			waitTime(1000);
			clickBy(prop.getProperty("AllPage.FilterColumnSelection_ClickAll"));
			waitTime(1000);
			scrollIntoView(driver.findElement(locatorSplit(prop.getProperty("AllPage.FilterApplyIcon"))));
			clickBy(prop.getProperty("AllPage.FilterApplyIcon"));
			waitForPageLoad();
			waitTillLoadingCompletesBy(prop.getProperty("AllPage.SelectAllCheckBox"));
			bReturn = true;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Select All Columns in any Grid filter could not be completed", "FAIL");
		}
		return bReturn;
	}

	public boolean getObjectiveIDByNameInHomeGrid(String searchValue){
		boolean bReturn = false;

		try {

			//click the search icon
			waitTillLoadingCompletesBy(prop.getProperty("AllPage.SearchIcon"));
			clickBy(prop.getProperty("AllPage.SearchIcon"));

			//enter the search value in the text box
			waitTime(1000);
			waitTillLoadingCompletesBy(prop.getProperty("AllPage.SearchTextBox"));
			clearTextFieldBy(prop.getProperty("AllPage.SearchTextBox"));
			enterBy(prop.getProperty("AllPage.SearchTextBox"), searchValue);
			waitTime(2000);

			//store grid element locator value to String
			String gridLocator = prop.getProperty("AllPage.GridUniqueColumn");
//			int count = 1;
			String objectiveStatus = null, objectiveName;

			//get total number of elements displayed for the search result
			int gridElementsSize = getAllElementsBy(gridLocator).size();

			//traverse through objective Id field in each column
			for (int i=1; i<=gridElementsSize; i = i+9){
//				count++;
				int objNamePosition = i+1;
				objectiveName = getTextBy(gridLocator+"["+objNamePosition+"]");
//				System.out.println("Objective Name in grid: "+objectiveName);

				//check given search text equals objective Id, else re-iterate
				if(objectiveName.contains(searchValue)){

					objectiveID = getTextBy(gridLocator+"["+i+"]");
					int statusPosition  = i+8;
					//get objective status
					objectiveStatus = getTextBy(gridLocator+"["+statusPosition+"]");
					bReturn = true;
					break;
				}
			}
			Reporter.reportStep("Objective Id is: "+objectiveID+" and it's Status is: "+objectiveStatus, "PASS");
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Search and Find Objective ID in OC Home Page could not be performed.", "FAIL");
		}
		return bReturn;
	}

	/*public boolean getOCDSOIDByName(String searchValue){
		boolean bReturn = false;

		try {

			//click the search icon
			waitTillLoadingCompletesBy(prop.getProperty("AllPage.SearchIcon"));
			clickBy(prop.getProperty("AllPage.SearchIcon"));

			//enter the search value in the text box
			waitTime(1000);
			waitTillLoadingCompletesBy(prop.getProperty("AllPage.SearchTextBox"));
			clearTextFieldBy(prop.getProperty("AllPage.SearchTextBox"));
			enterBy(prop.getProperty("AllPage.SearchTextBox"), searchValue);
			waitTime(2000);

			//store grid element locator value to String
			String gridLocator = prop.getProperty("AllPage.GridUniqueColumn");
//			int count = 1;
			String objectiveStatus = null, objectiveName;

			//get total number of elements displayed for the search result
			int gridElementsSize = getAllElementsBy(gridLocator).size();

			//traverse through objective Id field in each column
			for (int i=1; i<=gridElementsSize; i = i+8){
//				count++;
				int objNamePosition = i+1;
				objectiveName = getTextBy(gridLocator+"["+objNamePosition+"]");
//				System.out.println("Objective Name in grid: "+objectiveName);

				//check given search text equals objective Id, else re-iterate
				if(objectiveName.contains(searchValue)){

					objectiveID = getTextBy(gridLocator+"["+i+"]");
					int statusPosition  = i+7;
					//get objective status
					objectiveStatus = getTextBy(gridLocator+"["+statusPosition+"]");
					bReturn = true;
					break;
				}
			}
			Reporter.reportStep("Objective Id is: "+objectiveID+" and it's Status is: "+objectiveStatus, "PASS");
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Search and Find Objective ID in OC Home Page could not be performed.", "FAIL");
		}
		return bReturn;
	}*/

	public boolean verifyDataAfterLoadingtheGrid(){
		boolean bReturn = false;

		try {
			//get Grid elements count & verify if it's empty
			if (getAllElementsBy(prop.getProperty("AllPage.GridUniqueColumn")).size() == 0)
				Reporter.reportStep("Grid is empty after load. Please check", "FAIL");
			else
				bReturn = true;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Data verify after grid load could not be performed.", "FAIL");
		}
		return bReturn;
	}

	/*public boolean validatePlanStatusInCSODSOHomePage(String iconLocator, String actionType){
		boolean bReturn = false;

		try {
			String scenarioId = getTextBy(prop.getProperty("CSO_DSO_Home.GetScenarioId"));
			String scenarioType = getTextBy(prop.getProperty("CSO_DSO_Home.GetScenarioType"));
			String planStatus = getTextBy(prop.getProperty("CSO_DSO_Home.GetPlanStatus"));
			if(!isElementEnabledBy(iconLocator) == true){
				Reporter.reportStep("For Scenario ID: "+scenarioId+" with Scenario Type: "+scenarioType+" and Status: "+planStatus+". The requested action: "+actionType+" could not be performed.", "FAIL");
			}
			bReturn = true;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Plan Status Validation in CSO/DSO Home page could not be performed.", "FAIL");
		}
		return bReturn;
	}*/

	public boolean copyFunctionality(String inputValue, String copyType){
		boolean bReturn = false;

		try {
			String locatorValue = prop.getProperty("CSO_DSO_Home.CopyFlowDropDown");

			waitTillLoadingCompletesBy(locatorValue);

			//get all elements matching the locator
			List<WebElement> allChoices = getAllElementsBy(locatorValue);

			if(copyType.equals("Basic") || copyType.equals("Implementation") || copyType.equals("New Store")){

				if(allChoices.size()>1){
					Reporter.reportStep("Basic/Implementation/New Store Copy scenarios cannot have more than one copy flows.", "WARNING");
				}
			}

			if(copyType.equals("Remodel")){

				if(allChoices.size()>2){
					Reporter.reportStep("Remodel Copy scenario cannot have more than two copy flows.", "WARNING");
				}
			}

			if(copyType.equals("Baseline")){

				if(allChoices.size()!=4){
					Reporter.reportStep("Baseline Copy scenario should have all four copy flows.", "WARNING");
				}
			}

			//traverse through each element
			for (WebElement choice : allChoices) {

				//get text from element and compare it with array and select the element
				String textInElement = choice.getText();

				if(textInElement.contains(inputValue)){
					choice.click();
					break;
				}
			}
			waitForPageLoad();
			bReturn = true;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Copy functionality in CSO Home Page could not be performed.", "FAIL");
		}
		return bReturn;
	}

	public long getCurrentTime(){

		try {
			Timestamp time1 = new Timestamp(System.currentTimeMillis());
			long currentTime = time1.getTime();
			//System.out.println("Current Time: "+currentTime);
			return currentTime;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Issue while fetching timestamp", "INFO");
			return 0L;
		}
	}

	public void getTimeDifference(long loadBeforeTime, String stepName){

		Timestamp time2 = new Timestamp(System.currentTimeMillis());
		long loadAfterTime = time2.getTime();
		//System.out.println("Time after load: "+loadAfterTime);
		double timeDifferenceInSecs = (double)(loadAfterTime - loadBeforeTime)/1000;
		//System.out.println("Difference in time: "+timeDifferenceInSecs);
		Reporter.reportStep("Time taken for the step: "+stepName+" is: "+timeDifferenceInSecs+" secs", "INFO");
	}

	/**
	 * This method is used to check whether the button/icon is enabled for that scenario & then click it
	 * @param locatorVal - web page locator of that button/icon
	 * @param scenario
     * @return
     */
	public boolean clickIfEnabledBy(String locatorVal, String scenario){
		boolean bReturn = false;
		
		try {
			if(isElementEnabledBy(locatorVal)){
				clickBy(locatorVal);
				waitForPageLoad();
				bReturn = true;
			}else{
				Reporter.reportStep("The button is not enabled for the scenario: "+scenario, "FAIL");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Click button if enabled by could not be completed.", "FAIL");
		}
		return bReturn;
	}

	/**
	 * This method is used to validate error message appears in the grid when there is no selection made
	 * @return boolean
     */
	public boolean validateGridErrorMessage(){
		boolean bReturn = false;

		try {
//			waitForPageLoad();
			WebElement errorMessageElement = driver.findElement(locatorSplit(prop.getProperty("AllPage.GridErrorMessage")));
			String errorMessage = errorMessageElement.getText();
			Reporter.reportStep("Error Message: "+errorMessage, "FAIL");
		} catch (NoSuchElementException e) {
			bReturn = true;
		}
		return bReturn;
	}

	/*public boolean selectValueInDropDown(String inputVal, String dropDownLocator, String dropDownOptionsLocator){
		boolean bReturn = false;

		try {
			String dropDownVal = getTextBy(dropDownLocator);
			if (!dropDownVal.contains(inputVal)){

                clickBy(dropDownLocator);
                waitTillLoadingCompletesBy(dropDownOptionsLocator);
                dropDownChooseMethod(dropDownOptionsLocator, inputVal);
                bReturn = true;
            }
            else if (dropDownVal.contains(inputVal))
                bReturn = true;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Dropdown selection could not be performed", "FAIL");
		}
		return bReturn;
	}

	public boolean selectValueInDropDownForOC(String inputVal, String dropDownLocator, String dropDownOptionsLocator){
		boolean bReturn = false;

		try {
			String dropDownVal = getTextBy(dropDownLocator);
			if (!dropDownVal.contains(inputVal)){

				clickBy(dropDownLocator);
				waitTillLoadingCompletesBy(dropDownOptionsLocator);
				dropDownChooseMethodForObjective(dropDownOptionsLocator, inputVal);
				bReturn = true;
			}
			else if (dropDownVal.contains(inputVal))
				bReturn = true;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Dropdown selection could not be performed", "FAIL");
		}
		return bReturn;
	}*/

	public boolean moveSliderWithinRange(String pointerLocator, String pointerValue, String inputValue){
		boolean bReturn = false;

		try {
			//Change given input to int
			int input = Integer.parseInt(inputValue);
			System.out.println("Given input value: "+input);

//			Thread.sleep(3000);

			//Get current slider value in UI
			String sliderText = getElementAttributeValueBy(pointerValue, "innerHTML");
//			String sliderText = getTextBy(pointerValue);
			System.out.println("Text in the slider: "+sliderText);

			if (!sliderText.isEmpty()){

				int sliderValue = Integer.parseInt(sliderText);
				System.out.println("Value in the slider: "+sliderValue);

				//Compare and perform slider movement based on the input value
				WebElement slider = driver.findElement(locatorSplit(pointerLocator));

				if (input == sliderValue){

					bReturn = true;
					waitForPageLoad();
				}
				else if (input < sliderValue){

					int diff = sliderValue - input;
					for (int i=1; i<=diff;i++){
//						slider.click();
						slider.sendKeys(Keys.ARROW_LEFT);
					waitTime(100);
					}
					waitForPageLoad();
					bReturn = true;
				}
				else if (input > sliderValue){

					int diff = input - sliderValue;
					for (int i=1; i<=diff;i++){
//						slider.click();
						slider.sendKeys(Keys.ARROW_RIGHT);
						waitTime(100);
					}
					waitForPageLoad();
					bReturn = true;
				}
			}
			else {
				waitForPageLoad();
				bReturn = true;
				Reporter.reportStep("Please check the date range", "WARNING");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Slider movement within range could not be performed", "FAIL");
		}
		return bReturn;
	}

	public boolean moveSliderBetweenRanges(String pointerLocator, String pointerValue, String inputValue){
		boolean bReturn = false;

		try {
			//Change given input to int
			int input = Integer.parseInt(inputValue);
			System.out.println("Given input value: "+input);

//			Thread.sleep(3000);

			//Get current slider value in UI
			String sliderText = getElementAttributeValueBy(pointerValue, "innerHTML");
//			String sliderText = getTextBy(pointerValue);
			System.out.println("Text in the slider: "+sliderText);

			if (!sliderText.isEmpty()){

				int sliderValue = Integer.parseInt(sliderText);
				System.out.println("Value in the slider: "+sliderValue);

				//Compare and perform slider movement based on the input value
				WebElement slider = driver.findElement(locatorSplit(pointerLocator));

				if (input == sliderValue){

					bReturn = true;
					waitForPageLoad();
				}
//				else if (input < sliderValue || input > sliderValue){
				else {
					for (int i=1; i<=input; i++){
//						slider.click();
//						waitTime(1000);
						slider.sendKeys(Keys.ARROW_RIGHT);
//						System.out.println("Inside Pointer"+i);
					waitTime(100);
					}
					waitForPageLoad();
					bReturn = true;
				}
			}
			else {
				waitForPageLoad();
				bReturn = true;
				Reporter.reportStep("Please check the date range", "WARNING");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Slider movement between ranges could not be performed", "FAIL");
		}
		return bReturn;
	}

	public boolean selectAndUnSelectDataParameters(String dataParameters){
		boolean bReturn = false;

		try {
			String[] givenParameters = dataParameters.split(",");

			for (int i = 0; i < givenParameters.length; i++){

                int dataPar = Integer.parseInt(givenParameters[i]);

				switch (dataPar){

					case 1:
						clickBy(prop.getProperty("OC_Create.Demographics_Switch"));
						waitForPageLoad();
						break;
					case 2:
						clickBy(prop.getProperty("OC_Create.Competition_Switch"));
						waitForPageLoad();
						break;
					case 3:
						clickBy(prop.getProperty("OC_Create.Location_Switch"));
						waitForPageLoad();
						break;
					case 4:
						clickBy(prop.getProperty("OC_Create.MarketShare_Switch"));
						waitForPageLoad();
						break;
					case 5:
						clickBy(prop.getProperty("OC_Create.Weather_Switch"));
						waitForPageLoad();
						break;
					case 6:
						clickBy(prop.getProperty("OC_Create.StoreParameter_Switch"));
						waitForPageLoad();
						break;
					case 7:
						clickBy(prop.getProperty("OC_Create.CatgPenetration_Switch"));
						waitForPageLoad();
						break;
					case 8:
						clickBy(prop.getProperty("OC_Create.DeptPenetration_Switch"));
						waitForPageLoad();
						break;
					case 9:
						clickBy(prop.getProperty("OC_Create.DeptSpecific_Switch"));
						waitForPageLoad();
						break;
					case 10:
						clickBy(prop.getProperty("OC_Create.CompSales_Switch"));
						waitForPageLoad();
						break;
				}
//				String dataPar = givenParameters[i];

               /* if (dataPar.equalsIgnoreCase("Demographics") && isElementEnabledBy(prop.getProperty("OC_Create.Demographics_Switch"))){

                    clickBy(prop.getProperty("OC_Create.Demographics_Switch"));
					waitForPageLoad();
                }
                else if (dataPar.equalsIgnoreCase("Competition") && isElementEnabledBy(prop.getProperty("OC_Create.Competition_Switch"))){

                    clickBy(prop.getProperty("OC_Create.Competition_Switch"));
					waitForPageLoad();
                }
                else if (dataPar.equalsIgnoreCase("Location") && isElementEnabledBy(prop.getProperty("OC_Create.Location_Switch"))){

                    clickBy(prop.getProperty("OC_Create.Location_Switch"));
					waitForPageLoad();
                }
                else if (dataPar.equalsIgnoreCase("MarketShare") && isElementEnabledBy(prop.getProperty("OC_Create.MarketShare_Switch"))){

                    clickBy(prop.getProperty("OC_Create.MarketShare_Switch"));
					waitForPageLoad();
                }
                else if (dataPar.equalsIgnoreCase("Weather") && isElementEnabledBy(prop.getProperty("OC_Create.Weather_Switch"))){

                    clickBy(prop.getProperty("OC_Create.Weather_Switch"));
					waitForPageLoad();
                }
                *//*else if (dataPar.equalsIgnoreCase("SplMetrics") && isElementEnabledBy(prop.getProperty("OC_Create.SplMetrics_Switch"))){

                    clickBy(prop.getProperty("OC_Create.SplMetrics_Switch"));
					waitForPageLoad();
                }*//*
				else if (dataPar.equalsIgnoreCase("CompSales") && isElementEnabledBy(prop.getProperty("OC_Create.CompSales_Switch"))){

					clickBy(prop.getProperty("OC_Create.CompSales_Switch"));
					waitForPageLoad();
				}
				else if (dataPar.equalsIgnoreCase("SkipClustering") && isElementEnabledBy(prop.getProperty("OC_Create.EnableClustering"))){

					clickBy(prop.getProperty("OC_Create.EnableClustering"));
					waitForPageLoad();
				}
				else if (dataPar.equalsIgnoreCase("LearningMechanism") && isElementEnabledBy(prop.getProperty("OC_Create.LearningMechanism"))){

					clickBy(prop.getProperty("OC_Create.LearningMechanism"));
					waitForPageLoad();
				}
				else if (dataPar.equalsIgnoreCase("CrossElasticity") && isElementEnabledBy(prop.getProperty("OC_Create.CrossElasticity"))){

					clickBy(prop.getProperty("OC_Create.CrossElasticity"));
					waitForPageLoad();
				}*/
            }
			bReturn = true;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Select and Un-Select required Data Parameters could not be performed.", "FAIL");
		}
		return bReturn;
	}

	public boolean enterWPIValuesForCSO(String wpiValues){
		boolean bReturn = false;

		try {
			String wpiVal[] = wpiValues.split(",");

			if (!wpiVal[0].isEmpty()){

				waitTime(1000);
                waitTillLoadingCompletesBy(prop.getProperty("OC_Create.Sales"));
				clickBy(prop.getProperty("OC_Create.Sales"));
				focusElementAndSendTextBy(prop.getProperty("OC_Create.Sales"), wpiVal[0]);
				waitTime(500);
            }
			if (!wpiVal[1].isEmpty()){

                waitTillLoadingCompletesBy(prop.getProperty("OC_Create.Margins"));
				focusElementAndSendTextBy(prop.getProperty("OC_Create.Margins"), wpiVal[1]);
//				scrollBy(prop.getProperty("OC_Create.Margins"), Keys.TAB);
				waitTime(500);
            }
			if (!wpiVal[2].isEmpty()){

                waitTillLoadingCompletesBy(prop.getProperty("OC_Create.Units"));
				focusElementAndSendTextBy(prop.getProperty("OC_Create.Units"), wpiVal[2]);
//				scrollBy(prop.getProperty("OC_Create.Units"), Keys.TAB);
				waitTime(500);
            }
			if (!wpiVal[3].isEmpty()){

                waitTillLoadingCompletesBy(prop.getProperty("OC_Create.Inventory"));
				focusElementAndSendTextBy(prop.getProperty("OC_Create.Inventory"), wpiVal[3]);
				waitTime(500);
            }
			bReturn = true;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Enter WPI values in Objective for CSO could not be completed.", "FAIL");
		}
		return bReturn;
	}

	public boolean enterWPIValuesForDSO(String wpiValues){
		boolean bReturn = false;

		try {
			String wpiVal[] = wpiValues.split(",");

			if (!wpiVal[0].isEmpty()){

				waitTime(1000);
				waitTillLoadingCompletesBy(prop.getProperty("OC_Create.Sales"));
				clickBy(prop.getProperty("OC_Create.Sales"));
				focusElementAndSendTextBy(prop.getProperty("OC_Create.Sales"), wpiVal[0]);
				waitTime(500);
			}
			if (!wpiVal[1].isEmpty()){

				waitTillLoadingCompletesBy(prop.getProperty("OC_Create.Margins"));
				focusElementAndSendTextBy(prop.getProperty("OC_Create.Margins"), wpiVal[1]);
//				scrollBy(prop.getProperty("OC_Create.Margins"), Keys.TAB);
				waitTime(500);
			}
			if (!wpiVal[2].isEmpty()){

				waitTillLoadingCompletesBy(prop.getProperty("OC_Create.Units"));
				focusElementAndSendTextBy(prop.getProperty("OC_Create.Units"), wpiVal[2]);
//				scrollBy(prop.getProperty("OC_Create.Units"), Keys.TAB);
				waitTime(500);
			}
			bReturn = true;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Enter WPI values in Objective for DSO could not be completed.", "FAIL");
		}
		return bReturn;
	}

	public boolean selectPlanIDsInRecyclePageGrid(String searchValue, String searchTextBoxLocator){
		boolean bReturn = false;

		try {
			//Split the input values & store in array
			String[] inputVal = searchValue.split(",");

			//Click the filter icon
			waitTillLoadingCompletesBy(prop.getProperty("CSO_DSO_Home.RecycleFilter"));
			clickBy(prop.getProperty("CSO_DSO_Home.RecycleFilter"));
			waitTillLoadingCompletesBy(prop.getProperty("CSO_DSO_Home.RecycleFilterIDTextBox"));

			//Enter the values in the Filter text box
			for (int i=0; i<inputVal.length; i++){

                enterWithoutClearBy(searchTextBoxLocator, inputVal[i]);
                scrollBy(searchTextBoxLocator, Keys.ENTER);
            }

			//Apply filter & select all checkbox
			waitTillLoadingCompletesBy(prop.getProperty("CSO_DSO_Home.RecycleFilterApply"));
			clickBy(prop.getProperty("CSO_DSO_Home.RecycleFilterApply"));

			waitForPageLoad();
			waitTillLoadingCompletesBy(prop.getProperty("CSO_DSO_Home.RecycleGridSelectAllCheckBox"));
			clickBy(prop.getProperty("CSO_DSO_Home.RecycleGridSelectAllCheckBox"));

			bReturn = true;
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Plan IDs selection in Recycle grid could not be performed", "FAIL");
		}
		return bReturn;
	}

	public boolean restoreDeletedPlanInRecycle(String cancelBtnLocator, String searchValue, String searchTextBoxLocator){
		boolean bReturn = false;

		try {
			if (!isElementEnabledBy(prop.getProperty("CSO_DSO_Home.Recycle")))

			waitTillLoadingCompletesBy(prop.getProperty("CSO_DSO_Home.Recycle"));
			int homeGridCellsCount = getAllElementsBy(prop.getProperty("AllPage.GridUniqueColumn")).size();
			clickBy(prop.getProperty("CSO_DSO_Home.Recycle"));
			waitForPageLoad();
			waitTillLoadingCompletesBy(prop.getProperty("CSO_DSO_Home.RecycleGridSelectAllCheckBox"));

			int gridCellsCountWithRecycle = getAllElementsBy(prop.getProperty("AllPage.GridUniqueColumn")).size();

			if (gridCellsCountWithRecycle <= homeGridCellsCount) {

                Reporter.reportStep("No Plans available in Recycle grid to Restore", "INFO");
                clickBy(cancelBtnLocator);
                waitForPageLoad();
                waitTillLoadingCompletesBy(prop.getProperty("AllPage.SelectAllCheckBox"));
				bReturn = true;
            }
			else {
				selectPlanIDsInRecyclePageGrid(searchValue, searchTextBoxLocator);

				if (isElementEnabledBy(prop.getProperty("CSO_DSO_Home.RecycleRestoreBtn"))){

					clickBy(prop.getProperty("CSO_DSO_Home.RecycleRestoreBtn"));
					waitForPageLoad();
					messagePopUpHandle();
					waitTillLoadingCompletesBy(prop.getProperty("AllPage.SelectAllCheckBox"));
					selectPlanIDsInHomePageGrid(searchValue);

					if (getAllElementsBy(prop.getProperty("AllPage.GridUniqueColumn")).size() == 0)
						Reporter.reportStep("Restored plan is not displayed in Home Page Grid", "FAIL");
					else
						Reporter.reportStep("Plan restored successfully in Home Page Grid", "PASS");
					bReturn = true;
				}
				else
					Reporter.reportStep("Given Plan IDs: "+searchValue+" are not available in Recycle Grid to Restore", "FAIL");
			}
		} catch (Exception e) {

			e.printStackTrace();
			Reporter.reportStep("Restoring deleted plan in Recycle grid could not be performed", "FAIL");
		}
		return bReturn;
	}

	public boolean purgeDeletedPlanInRecycle(String cancelBtnLocator, String searchValue, String searchTextBoxLocator){
		boolean bReturn = false;

		try {
			waitTillLoadingCompletesBy(prop.getProperty("AllPage.SelectAllCheckBox"));
			clickBy(prop.getProperty("AllPage.SelectAllCheckBox"));
			waitForPageLoad();
			waitTime(1000);
			clickBy(prop.getProperty("AllPage.SelectAllCheckBox"));

			waitTillLoadingCompletesBy(prop.getProperty("CSO_DSO_Home.Recycle"));
			int homeGridCellsCount = getAllElementsBy(prop.getProperty("AllPage.GridUniqueColumn")).size();
			clickBy(prop.getProperty("CSO_DSO_Home.Recycle"));
			waitForPageLoad();
			waitTillLoadingCompletesBy(prop.getProperty("CSO_DSO_Home.RecycleGridSelectAllCheckBox"));

			int gridCellsCountWithRecycle = getAllElementsBy(prop.getProperty("AllPage.GridUniqueColumn")).size();

			if (gridCellsCountWithRecycle <= homeGridCellsCount) {

				Reporter.reportStep("No Plans available in Recycle grid to Purge", "INFO");
				clickBy(cancelBtnLocator);
				waitForPageLoad();
				waitTillLoadingCompletesBy(prop.getProperty("AllPage.SelectAllCheckBox"));
				bReturn = true;
			}
			else {
				selectPlanIDsInRecyclePageGrid(searchValue, searchTextBoxLocator);

				if (isElementEnabledBy(prop.getProperty("CSO_DSO_Home.RecyclePurgeBtn"))){

					clickBy(prop.getProperty("CSO_DSO_Home.RecyclePurgeBtn"));
					waitForPageLoad();
					messagePopUpHandle();
					waitTillLoadingCompletesBy(prop.getProperty("AllPage.SelectAllCheckBox"));
					selectPlanIDsInHomePageGrid(searchValue);

					if (getAllElementsBy(prop.getProperty("AllPage.GridUniqueColumn")).size() == 0)
						Reporter.reportStep("Purged plan is not displayed in Home Page Grid - Purge Successful", "PASS");
					else
						Reporter.reportStep("Purged plans are displayed in HomePage Grid", "FAIL");
					bReturn = true;
				}
				else
					Reporter.reportStep("Given Plan IDs: "+searchValue+" are not available in Recycle Grid to Purge", "FAIL");
			}
		} catch (Exception e) {

			e.printStackTrace();
			Reporter.reportStep("Purging deleted plan in Recycle grid could not be performed", "FAIL");
		}
		return bReturn;
	}

	public boolean addCommentInActivityFeed(String activityFeedComment){
		boolean bReturn = false;

		try {
			checkObjectiveStatusAndActionToPerform(objectiveID, prop.getProperty("CSO_DSO_Home.Activity"), "Activity Feed");
			waitTillLoadingCompletesBy(prop.getProperty("CSO_DSO_Home.Activity"));
			clickBy(prop.getProperty("CSO_DSO_Home.Activity"));
			waitForPageLoad();
			waitTime(2000);
			String activityFeedLocator = prop.getProperty("ActivityFeed.Heading");
			waitTillLoadingCompletesBy(activityFeedLocator);
			if (!activityFeedComment.equals("No")){

                if (isElementDisplayedBy(prop.getProperty("CSO_DSO_Home.ActivityFeedComment"))){

                    enterBy(prop.getProperty("CSO_DSO_Home.ActivityFeedComment"), activityFeedComment);
                    clickBy(prop.getProperty("CSO_DSO_Home.ActivityFeedAddBtn"));
                    waitForPageLoad();
					waitTime(2000);
					Reporter.reportStep("Adding comment in Activity Feed is successful", "PASS");
                    clickBy(prop.getProperty("CSO_DSO_Home.ActivityFeedComment"));
					scrollBy(prop.getProperty("CSO_DSO_Home.ActivityFeedComment"), Keys.ESCAPE);
					waitTillLoadingCompletesBy(prop.getProperty("AllPage.SelectAllCheckBox"));
                    bReturn = true;
                }
                else
                    Reporter.reportStep("Activity Feed is not displayed properly. Please check.", "FAIL");
            }
			else {
				if (!isElementDisplayedBy(prop.getProperty("CSO_DSO_Home.ActivityFeedComment")))
					Reporter.reportStep("Activity Feed is not displayed properly. Please check.", "FAIL");
				else
					Reporter.reportStep("Screenshot taken for Activity Feed", "INFO");
				bReturn = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Adding comment in Activity Feed could not be performed", "FAIL");
		}
		return bReturn;
	}

	public boolean validateReviewScreenIsNotEmptyInOC(){
		boolean bReturn = false;

		try {
			checkObjectiveStatusAndActionToPerform(objectiveID, prop.getProperty("CSO_DSO_Home.Review"), "Review Objective");
			waitTillLoadingCompletesBy(prop.getProperty("CSO_DSO_Home.Review"));
			int homeGridCellsCount = getAllElementsBy(prop.getProperty("AllPage.GridUniqueColumn")).size();
			clickBy(prop.getProperty("CSO_DSO_Home.Review"));
			waitForPageLoad();
			waitTillLoadingCompletesBy(prop.getProperty("AllPage.ReportHeading"));
			int reviewGridCellsCount = getAllElementsBy(prop.getProperty("AllPage.GridUniqueColumn")).size();
			if (reviewGridCellsCount <= homeGridCellsCount)
                Reporter.reportStep("Review Screen Grid is empty. Please check.", "FAIL");
            else {
                Reporter.reportStep("Review Screen Grid contains data", "PASS");
				clickBy(prop.getProperty("AllPage.ReportCloseBtn"));
				waitForPageLoad();
				waitTime(3000);
				waitTillLoadingCompletesBy(prop.getProperty("AllPage.SelectAllCheckBox"));
                bReturn = true;
            }
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("OC Review Screen validation could not be performed", "FAIL");
		}
		return bReturn;
	}

	public boolean generateDownloadableReport(String searchValue, String reportDownloadIcon, String reportType){
		boolean bReturn = false;

		try {
			checkObjectiveStatusAndActionToPerform(searchValue, prop.getProperty("AllPage.ReportIcon"), "Report");
			clickBy(prop.getProperty("AllPage.ReportIcon"));
			waitForPageLoad();
			waitTillLoadingCompletesBy(reportDownloadIcon);
			clickBy(reportDownloadIcon);
			waitForPageLoad();
			waitTime(1000);
			robotKeyActionsForDownload("C:\\Users\\"+UserName+"\\Documents\\Workspace\\space-optimization\\reports\\DownloadableToolReports");
			bReturn = true;

		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Report/CSV: "+reportType+" could not be downloaded", "FAIL");
		}
		return bReturn;
	}

	/**
	 * This method is used to select checkbox in table based on the input
	 * 
	 * @param tableLocator - The locator of the table
	 * @param columnLocator - The locator of a particular column in the table
	 * @param checkBoxLocator - The locator of the checkbox column in the table
	 * @param inputVal - String - input from the user
	 * @return boolean
	 * @author Raghunath
	 *//*
	public boolean selectMethod(String tableLocator, String columnLocator, String checkBoxLocator, String inputVal){
		boolean bReturn = false;

		try {
			//split the string input and store it in array
			String[] input = inputVal.split(",");

			//find the web table
			WebElement table = driver.findElement(locatorSplit(tableLocator));

			//get size of table row
			int rowSize = table.findElements(By.tagName("tr")).size();

			//traverse through each row
			for(int i=0;i<rowSize;i++){

				WebElement eachRow = table.findElement(By.tagName("tbody")).findElements(By.tagName("tr")).get(i);

				//get content of particular column
				String idColumnVal = eachRow.findElement(locatorSplit(columnLocator)).getText();

				//compare input value with the column and click corresponding checkbox
				for(int j=0;j<input.length;j++){

					if(idColumnVal.trim().equals(input[j])){

						eachRow.findElement(locatorSplit(checkBoxLocator)).click();
						break;
					}
				}
			}
			bReturn=true;
		}catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("One of the given input: "+inputVal+" is not available.", "FAIL");
		}
		return bReturn;
	}*/

	/**
	 * This method is used to upload CSV file which hand
	 * 
	 * @param csvUploadXpath - The xpath (locator) of the Upload CSV button element
	 * @param csvFilePath - String - complete file path of the CSV file
	 * @param msg1Id - The id (locator) of the First Message PopUp
	 * @param msg1ProceedXpath - The xpath (locator) of the Proceed button in First Message PopUp
	 * @param msg2Id - The id (locator) of the Second Message PopUp
	 * @param msg2ProceedXpath - The xpath (locator) of the Proceed button in Second Message PopUp
	 * @param uploadSuccessId - The id (locator) of the Upload success message element
	 * @return boolean
	 * @author Raghunath
	 */

	/*public boolean uploadFootageCSV(String csvUploadXpath, String csvFilePath, String msg1Id, String msg1ProceedXpath, String msg2Id, String msg2ProceedXpath, String uploadSuccessId){
		boolean bReturn = false;
		try {
			String parentWindow = getParentWindowHandle();

			clickBy(csvUploadXpath);
			Set<String> allWindows = getallWindowHandles();

			//traverse through each window and perform CSV Upload
			for (String window : allWindows) {

				if(!window.equals(parentWindow)){
					driver.switchTo().window(window);

					enterBy(csvChooseFile, csvFilePath);
					Thread.sleep(2000);
					clickBy(csvSubmit);
				}
			}
			Set<String> allWindowsRecheck = getallWindowHandles();
			while(allWindowsRecheck.size()>1){

				enterBy(csvChooseFile, csvFilePath);
				Thread.sleep(2000);
				clickBy(csvSubmit);
				allWindowsRecheck = getallWindowHandles();
			}
			driver.switchTo().window(parentWindow);

			//wait until popup appears and upload CSV
			waitForPageLoad();
			if(isElementDisplayed(msg1Id)){
				clickBy(msg1ProceedXpath);
				waitForPageLoad();
			}
			if(isElementDisplayed(msg2Id)){
				clickBy(msg2ProceedXpath);
				waitForPageLoad();
			}
			if(isElementDisplayed(uploadSuccessId)){

				String successMsg = getTextBy(uploadSuccessId);
				if(successMsg.equals("CSV is uploaded successfully")){
					bReturn = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("CSV file upload could not be performed", "FAIL");
		}
		return bReturn;
	}*/

	/**
	 * This method is used to enter the values in the table based on user input
	 * 
	 * @param tableXpath - The xpath(locator) of the table element
	 * @param colClassName - The class name(locator) of the column in the the table
	 * @param fieldClassName - The class name(locator) of the field in which text to be entered in the table
	 * @param inputVal - String - input from the user - column for which data to be entered
	 * @param setValue - String - value needs to be set in each cell
	 * @return boolean
	 * @author Raghunath
	 *//*
	public boolean enterValueInSpaceRulesTable(String tableXpath, String colClassName, String fieldClassName, String inputVal, String setValue){
		boolean bReturn = false;

		try {
			//split the string input and store it in array
			String[] input = inputVal.split(",");

			String[] set = setValue.split(",");

			//find the web table
			WebElement table = driver.findElement(By.xpath(tableXpath));

			//get size of table row
			int rowSize = table.findElements(By.tagName("tr")).size();

			//traverse through each row
			for(int i=0;i<rowSize;i++){

				WebElement eachRow = table.findElement(By.tagName("tbody")).findElements(By.tagName("tr")).get(i);


				//get content of particular column
				String firstColumn = eachRow.findElement(By.className(colClassName)).getText();

				//compare input value with the column and click corresponding checkbox
				for(int j=0;j<input.length;j++){

					if(firstColumn.trim().equals(input[j])){

						eachRow.findElement(By.className(fieldClassName)).click();
						eachRow.findElement(By.className(fieldClassName)).click();
						Thread.sleep(1000);
						eachRow.findElement(By.className(fieldClassName)).sendKeys(Keys.BACK_SPACE);
						//Thread.sleep(1000);
						String cellChangeValue = set[j];
						//System.out.println("Change Value given by user: "+changeVal);
						//cell.sendKeys(Keys.BACK_SPACE);
						WebElement xpath = driver.findElement(By.xpath("//*[@id='spaceRulesBotGrid2_bodyDiv']/div/input"));
						try {
							//js.executeScript("document.getElementsByClassName('gt-col-spacerulesbotgrid2-upper1 gt-cell-actived-editable')[0].value="+changeVal);
							js.executeScript("arguments[0].value='150';", xpath);
						} catch (Exception e) {
							e.printStackTrace();
						}
						//eachRow.findElement(By.xpath("//*[contains(@class,'gt-cell-actived-editable')]/div")).
						xpath.sendKeys(cellChangeValue);
						break;
					}
				}
			}
			Reporter.reportStep("The given data: "+setValue+" is entered in the rows: "+inputVal, "PASS");
			bReturn=true;
		}catch (Exception e) {
			Reporter.reportStep("One of the given input value: "+setValue+" could not be entered in the table.", "FAIL");
		}
		return bReturn;
	}

	  *//**
	  * This method is used to clear the values in the Space Rules Page table based on user input
	  * 
	  * @param tableXpath - The xpath(locator) of the table element
	  * @param colClassName - The class name(locator) of the column in the the table
	  * @param fieldClassName - The class name(locator) of the field from which text entered to be cleared in the table
	  * @param inputVal - String - input from the user - column for which data to be cleared
	  * @return boolean
	  * @author Raghunath
	  *//*
	public boolean clearValueInSpaceRulesTable(String tableXpath, String colClassName, String fieldClassName, String inputVal){
		boolean bReturn = false;

		try {
			//split the string input and store it in array
			String[] input = inputVal.split(",");

			//find the web table
			WebElement table = driver.findElement(By.xpath(tableXpath));

			//get size of table row
			int rowSize = table.findElements(By.tagName("tr")).size();

			//traverse through each row
			for(int i=0;i<rowSize;i++){

				WebElement eachRow = table.findElement(By.tagName("tbody")).findElements(By.tagName("tr")).get(i);

				//get content of particular column
				String firstColumn = eachRow.findElement(By.className(colClassName)).getText();

				//compare input value with the column and click corresponding checkbox
				for(int j=0;j<input.length;j++){

					if(firstColumn.trim().equals(input[j])){

						eachRow.findElement(By.className(fieldClassName)).click();
						eachRow.findElement(By.className(fieldClassName)).click();
						//Thread.sleep(1000);
						driver.findElement(By.xpath("//*[@id='spaceRulesBotGrid2_bodyDiv']/div/input")).clear();
						break;						
					}
				}
			}
			Reporter.reportStep("Requested fields are emptied in the given rows: "+inputVal, "PASS");
			bReturn=true;
		}catch (Exception e) {
			Reporter.reportStep("One of the given input: "+inputVal+" could not be cleared in the table.", "FAIL");
		}
		return bReturn;
	}*/

	/**
	 * This method is used to get Plan ID value from Review Page
	 * 
	 * @param idVal - The id (locator) of the element
	 * @return boolean
	 * @author Raghunath
	 *//*
	public boolean getPlanID(String idVal){
		boolean bReturn = false;

		try {
			planID = getTextById(idVal);
			Reporter.reportStep("The Plan ID for Test Case: "+testCaseName+" is: "+planID, "PASS");			
			bReturn = true;
		} catch (Exception e) {
			Reporter.reportStep("Error while retriving Plan ID.", "FAIL");
		}
		return bReturn;
	}

	public boolean applyAllMethodInAssortCov(String applyAllButtonId, String popupMessageId, String popupProceedXpath){
		boolean bReturn = false;

		try {
			clickById(applyAllButtonId);
			waitForPageLoad();
			if(driver.findElement(By.id(popupMessageId)).isDisplayed()){

				clickByXpath(popupProceedXpath);
				waitForPageLoad();
			}
			bReturn = true;
		} catch (Exception e) {
			Reporter.reportStep("Apply All could not be completed in Assortment Coverage Page", "FAIL");
		}
		return bReturn;
	}

	public void addAllPlanIDs(String testCase, String planId){

		String plans = planId+" - "+testCase;
		resultSet.add(plans);
		//allPlans.put(planId, testCase);
		//System.out.println("Hash Table Plan IDs: "+allPlans);
	}

	public boolean clickNextInCategoryPage(String nextIdVal, String popupMessageIdVal, String proceedButtonXpath){
		boolean bReturn = false;

		try {
			clickById(nextIdVal);
			waitForPageLoad();
			if(driver.findElement(By.id(popupMessageIdVal)).isDisplayed()){

				clickByXpath(proceedButtonXpath);
				waitForPageLoad();
			}
			bReturn = true;
		} catch (Exception e) {
			Reporter.reportStep("Issue while moving from Category Selection to Space Rules page", "FAIL");
			e.printStackTrace();
		}
		return bReturn;
	}

	public boolean clickNextInReviewPage(String nextXpathVal, String popupMessageIdVal, String proceedButtonXpath){
		boolean bReturn = false;

		try {
			clickByXpath(nextXpathVal);
			if(waitUntilCSVPopUp(popupMessageIdVal)==true){
				clickByXpath(proceedButtonXpath);
				Reporter.reportStep("Plan Creation completed successfully", "PASS");
				bReturn = true;
			}
		} catch (Exception e) {
			Reporter.reportStep("Issue at Results Page", "FAIL");
		}
		return bReturn;
	}*/
}
