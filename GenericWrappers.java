package wrappers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Function;

import utils.Reporter;

public class GenericWrappers {

	protected static RemoteWebDriver driver;
	protected static Properties prop;
	public String sUrl;
	public static String UserName, Password, App_Rej_UserName, App_Rej_Password;

	protected static JavascriptExecutor js = (JavascriptExecutor) driver;


	public GenericWrappers() {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(new File("./config.properties")));
			sUrl = prop.getProperty("URL");
			UserName = prop.getProperty("USERNAME");
			Password = prop.getProperty("PASSWORD");
			App_Rej_UserName = prop.getProperty("A_R_USERNAME");
			App_Rej_Password = prop.getProperty("A_R_PASSWORD");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method will launch browser and maximize the browser and set the wait for 30 seconds and load the url
	 *
	 * @param browser - The type of browser you wish to open
	 * @return boolean
	 */
	public boolean invokeApp(String browser) {
		boolean bReturn = false;
		try {

			DesiredCapabilities dc = new DesiredCapabilities();
			dc.setBrowserName(browser);
			dc.setPlatform(Platform.WINDOWS);

			//set system property for each browser based on the selection
			if(browser.equalsIgnoreCase("chrome")){
				System.setProperty("webdriver.chrome.driver", "./drivers/chromedriver.exe");
				driver = new ChromeDriver();

			}	else if (browser.equalsIgnoreCase("ie")) {
				System.setProperty("webdriver.ie.driver", "./drivers/chromedriver.exe");
				driver = new InternetExplorerDriver();

			}
			else
				driver = new FirefoxDriver();
			//load the URL
			driver.get(sUrl);

			//maximize the browser
			driver.manage().window().maximize();

			//wait implicitly for 30 seconds
			driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
			bReturn = true;

		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("The browser: " + browser + " could not be launched.", "FAIL");
		}
		return bReturn;
	}

	/**
	 * This method will split the locator name(id, xpath etc) from value in object.properties file
	 *
	 * @return By (with different locators)
	 */
	public static By locatorSplit(String locatorVal){
		By bReturn = null;
		try {
			//split the locator value from its locator type
			String[] locator = locatorVal.split(" ", 2);

			//select the locator By based on the input
			switch (locator[0]) {
			case "id":
				bReturn = By.id(locator[1]);
				break;

			case "name":
				bReturn = By.name(locator[1]);
				break;

			case "class":
				bReturn = By.className(locator[1]);
				break;

			case "tag":
				bReturn = By.tagName(locator[1]);
				break;

			case "link":
				bReturn = By.linkText(locator[1]);
				break;

			case "partial":
				bReturn = By.partialLinkText(locator[1]);
				break;

			case "css":
				bReturn = By.cssSelector(locator[1]);
				break;

			case "xpath":
				bReturn = By.xpath(locator[1]);	
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Locator given in properties file is invalid", "FAIL");
		}
		return bReturn;
	}
	
	/**
	 * This method will clear existing value in text field & enter the new value
	 * 
	 * @param locateVal - locator value from property file
	 * @param data - The data to be sent to the web element
	 * @return boolean
	 */
	public boolean enterBy(String locateVal, String data) {
		boolean bReturn = false;
		try {

			driver.findElement(locatorSplit(locateVal)).clear();
			driver.findElement(locatorSplit(locateVal)).sendKeys(data);
			bReturn = true;

		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("The data: "+data+" could not be entered in the field: "+locateVal, "FAIL");
		}
		return bReturn;
	}

	/**
	 * This method will enter the value in the text field without clearing existing value
	 *
	 * @param locateVal - locator value from property file
	 * @param data - The data to be sent to the web element
	 * @return boolean
	 */
	public boolean enterWithoutClearBy(String locateVal, String data) {
		boolean bReturn = false;
		try {
			driver.findElement(locatorSplit(locateVal)).sendKeys(data);
			bReturn = true;

		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("The data: "+data+" could not be entered in the field: "+locateVal, "FAIL");
		}
		return bReturn;
	}

	/**
	 * This method is used to enter text in a field by focusing on it. Eg: Entering Sales, Margin values in OC module
	 *
	 * @param locateVal - locator value from property file
	 * @param value - The value to be set on the field
     * @return boolean
     */
	public boolean focusElementAndSendTextBy(String locateVal, String value) {
		boolean bReturn = false;
		try {
			//store the web element
			WebElement inputField = driver.findElement(locatorSplit(locateVal));

			//invoke Actions class to perform the below tasks
			Actions actions = new Actions(driver);
			actions.moveToElement(inputField);
			actions.doubleClick();
			actions.sendKeys(Keys.BACK_SPACE);
			actions.sendKeys(value);
			actions.build().perform();
			bReturn = true;

		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("The data: "+value+" could not be entered in the input field: "+locateVal, "WARNING");
		}
		return bReturn;
	}

	/**
	 * This method will verify the title of the browser (exact match) 
	 * 
	 * @param title - The expected title of the browser
	 * @return boolean
	 * @author Raghunath
	 */
	public boolean verifyTitle(String title){
		boolean bReturn = false;
		try{
			if (driver.getTitle().equalsIgnoreCase(title)){
				bReturn = true;
			}else
				Reporter.reportStep("The title of the page: "+driver.getTitle()+" did not match with the value: "+title, "FAIL");
		}catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Exception occurred while verifying title.", "FAIL");
		}
		return bReturn;
	}

	/**
	 * This method is used to verify whether title contains the given text
	 * 
	 * @param title - The expected title of the browser
	 * @return boolean
	 */
	public boolean verifyTitleContains(String title) 
	{
		boolean bReturn = false;
		try {
			if (driver.getTitle().trim().contains(title)){
				bReturn = true;			
			}else{
				Reporter.reportStep("The title: "+driver.getTitle()+" does not contains the value :"+title, "FAIL");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Exception occured while verifying title.", "FAIL");
		}
		return bReturn;
	}

	/**
	 * This method is used to refresh the web page
	 *
	 * @return boolean
	 */
	public boolean refreshPage(){
		boolean bReturn = false;

		try{
			driver.navigate().refresh();
			bReturn = true;
		}catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Page refresh could not be completed", "FAIL");
		}
		return bReturn;
	}

	/**
	 * This method will verify whether given text(exact match) matches with text in web element
	 * 
	 * @param locateVal - locator value from property file
	 * @param text - The text to be verified
	 * @return boolean
	 */
	public boolean verifyTextBy(String locateVal, String text){
		boolean bReturn = false;

		try {
			String sText = getTextBy(locateVal);
			if (sText.trim().equalsIgnoreCase(text)){
                bReturn = true;
            }else{
                Reporter.reportStep("The text: "+sText+" did not match with the value: "+text, "FAIL");
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bReturn;
	}

	/**
	 * This method will verify whether the text in webElement contains the given text
	 * 
	 * @param locateVal - locator value from property file
	 * @param text - The text to be verified
	 * @return boolean
	 */
	public boolean verifyTextContainsBy(String locateVal, String text){
		boolean bReturn = false;

		try {
			String sText = getTextBy(locateVal);
			if (sText.trim().contains(text)){
                bReturn = true;
            }else{
                Reporter.reportStep("The text: "+sText+" did not contain the value: "+text, "FAIL");
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bReturn;
	}

	/**
	 * This method will close the current browser
	 *
	 */
	public void closeBrowser() {

		try {
			driver.close();
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("The browser: "+driver.getCapabilities().getBrowserName()+" could not be closed.", "FAIL");
		}
	}

	/**
	 * This method will close all the browsers opened by automation code
	 *
	 */
	public void quitBrowser() {

		try {
			driver.quit();
		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("The browser: "+driver.getCapabilities().getBrowserName()+" could not be closed.", "FAIL");
		}
	}

	/**
	 * This method will click the element in a web page
	 * 
	 * @param locateVal - locator value from property file
	 * @return boolean
	 */
	public boolean clickBy(String locateVal) {
		boolean bReturn = false;

		try{
			driver.findElement(locatorSplit(locateVal)).click();
			bReturn = true;

		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("The element with locator: "+locateVal+" could not be clicked.", "FAIL");
		}
		return bReturn;
	}

	/**
	 * This method will mouse over on the element in a web page
	 * 
	 * @param locateVal - locator value from property file
	 * @return boolean
	 */
	public boolean mouseOverBy(String locateVal) {
		boolean bReturn = false;

		try{
			new Actions(driver).moveToElement(driver.findElement(locatorSplit(locateVal))).build().perform();
			bReturn = true;

		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("The mouse over by locator: "+locateVal+" could not be performed.", "FAIL");
		}
		return bReturn;
	}

	/**
	 * This method will get text from the web element
	 * 
	 * @param locateVal - locator value from property file
	 * @return String
	 */
	public String getTextBy(String locateVal){
		String sReturn = "";

		try{
			sReturn = driver.findElement(locatorSplit(locateVal)).getText();

		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("The element with locator: "+locateVal+" could not be found.", "FAIL");
		}
		return sReturn; 
	}

	/**
	 * This method will select the value from drop down in a web page by using its value
	 * 
	 * @param locateVal - locator value from property file
	 * @param value - The value to be selected from the dropdown
	 * @return boolean
	 */
	public boolean selectByValueBy(String locateVal, String value) {
		boolean bReturn = false;

		try{
			new Select(driver.findElement(locatorSplit(locateVal))).selectByValue(value);
			bReturn = true;

		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("The value: "+value+" could not be selected from dropdown.", "FAIL");
		}
		return bReturn;
	}

	/**
	 * This method will select the value from drop down in a web page by using its visible text
	 * 
	 * @param locateVal - locator value from property file
	 * @param value - The value to be selected (visible text) from the dropdown
	 * @return boolean
	 */
	public boolean selectByVisibleTextBy(String locateVal, String value) {
		boolean bReturn = false;

		try{
			new Select(driver.findElement(locatorSplit(locateVal))).selectByVisibleText(value);
			bReturn = true;

		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("The value: "+value+" could not be selected.", "FAIL");
		}
		return bReturn;
	}

	/**
	 * This method will select the value from drop down in a web page by using its index
	 * 
	 * @param locateVal - locator value from property file
	 * @param index - The index of the value to be selected from the dropdown
	 * @return boolean
	 */
	public boolean selectByIndexBy(String locateVal, int index) {
		boolean bReturn = false;

		try{
			new Select(driver.findElement(locatorSplit(locateVal))).selectByIndex(index);
			bReturn = true;

		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("The value with given index: "+index+" could not be selected.", "FAIL");
		}
		return bReturn;
	}

	/**
	 * This method is used to load objects(locators) from object.properties file
	 *
	 * @throws FileNotFoundException
	 * @throws IOException
     */
	public void loadObjects() throws FileNotFoundException, IOException{
		prop = new Properties();
		prop.load(new FileInputStream(new File("./object.properties")));

	}

	/**
	 * This method will scroll(send keys) the web element in a web page based on the Keyboard keys value
	 * 
	 * @param locateVal - locator value from property file
	 * @param scroll - Keyboard keys value like ENTER, UP, DOWN etc.
	 * @return boolean
	 */
	public boolean scrollBy(String locateVal, Keys scroll) {
		boolean bReturn = false;

		try{
			driver.findElement(locatorSplit(locateVal)).sendKeys(scroll);
			bReturn = true;

		}	catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("The scroll with given locator: "+locateVal +" could not be performed.", "FAIL");
		}	
		return bReturn;
	}

	/**
	 * This method will perform Keyboard key actions without any locator
	 *
	 * @param action - Keyboard keys value like ENTER, UP, DOWN etc.
	 * @return boolean
     */
	public boolean keyBoardActions(Keys action) {
		boolean bReturn = false;

		try{
			new Actions(driver).sendKeys(action).build().perform();
			bReturn = true;

		}	catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Requested keyboard action could not be performed.", "FAIL");
		}
		return bReturn;
	}

	/**
	 * This method will switch to frame by using name/id of the frame
	 * 	
	 * @param frameVal - the name/id which is used to represent frame in HTML
	 * @return boolean
	 */
	public boolean switchToFrame(String frameVal){
		boolean bReturn = false;

		try {
			driver.switchTo().frame(frameVal);
			bReturn = true;

		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Switch to frame with given frame id/name: "+frameVal+" cannot be performed.", "FAIL");
		}
		return bReturn;
	}

	/**
	 * This method will switch to frame by using its locator
	 * 	
	 * @param locateVal - locator value from property file
	 * @return boolean
	 */
	public boolean switchToFrameBy(String locateVal){
		boolean bReturn = false;

		try {
			driver.switchTo().frame(driver.findElement(locatorSplit(locateVal)));
			bReturn = true;

		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Switch to frame with locator: "+locateVal+" cannot be performed.", "FAIL");
		}
		return bReturn;
	}

	/**
	 * This method will switch to default content/ home page from any frame
	 * 
	 * @return boolean
	 */
	public boolean switchToDefaultFromFrame(){
		boolean bReturn = false;

		try {
			driver.switchTo().defaultContent();
			bReturn = true;

		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Switch to default content cannot be performed.", "FAIL");
		}
		return bReturn;
	}

	/**
	 * This method is used to accept the alert (or) click Ok/Submit button on the Alert
	 * 
	 * @return boolean
	 */
	public boolean acceptAlert(){
		boolean bReturn = false;

		try {
			driver.switchTo().alert().accept();
			bReturn = true;

		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Alert-accept could not be performed.", "FAIL");
		}
		return bReturn;
	}

	/**
	 * This method is used to dismiss the alert (or) click Close button on the Alert
	 * 
	 * @return boolean
	 */
	public boolean dismissAlert(){
		boolean bReturn = false;

		try {
			driver.switchTo().alert().dismiss();
			bReturn = true;

		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Alert-dismiss could not be performed.", "FAIL");
		}
		return bReturn;
	}

	/**
	 * This method is used to get text from the alert
	 * 
	 * @return String
	 */
	public String getAlertText(){
		String sReturn = "";

		try {
			sReturn = driver.switchTo().alert().getText();

		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Get text from alert could not be performed.", "FAIL");
		}
		return sReturn;
	}

	/**
	 * This method is used to send text to the alert
	 * 
	 * @param textVal - The text needs to be entered in alert
	 * @return boolean
	 */
	public boolean sendAlertText(String textVal){
		boolean bReturn = false;

		try {
			driver.switchTo().alert().sendKeys(textVal);
			bReturn = true;

		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("The given text: "+textVal+" could not be entered in the alert.", "FAIL");
		}
		return bReturn;
	}

	/**
	 * This method is used to get the current window/ parent window handle
	 * 
	 * @return String
	 */
	public String getParentWindowHandle(){
		String sReturn="";

		try {
			sReturn = driver.getWindowHandle();

		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Parent window handle could not be found", "FAIL");
		}
		return sReturn;
	}

	/**
	 * This method is used to get the window handles of all opened windows
	 * 
	 * @return Set<String>
	 */
	public Set<String> getallWindowHandles(){
		Set<String> sReturn = null;

		try {
			sReturn = driver.getWindowHandles();

		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("All window handles could not be found", "FAIL");
		}
		return sReturn;
	}

	/**
	 * This method is used to get the current URL of the web page
	 * 
	 * @return String
	 */
	public String getCurrentPageUrl(){
		String sReturn="";

		try {
			sReturn = driver.getCurrentUrl();

		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("The current URL could not be found", "FAIL");
		}
		return sReturn;
	}

	/**
	 * This method is used to get the source of the webpage
	 * 
	 * @return String
	 */
	public String getPageSource(){
		String sReturn="";

		try {
			sReturn = driver.getPageSource();

		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("The page source could not be found", "FAIL");
		}
		return sReturn;
	}

	/**
	 * This method is used to wait explicitly (max 120 seconds)until the web element loading completes
	 * 
	 * @param locateVal - locator value from property file
	 * @return boolean
	 */
	public boolean waitTillLoadingCompletesBy(String locateVal){
		boolean bReturn = false;

		try {
			//can change the wait time as per need
			WebDriverWait wdWait = new WebDriverWait(driver, 120);

			//script will wait for element to be clickable within the given time
			wdWait.until(ExpectedConditions.elementToBeClickable(driver.findElement(locatorSplit(locateVal))));
			bReturn = true;

		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Loading page takes more than 120 seconds.", "FAIL");
		}
		return bReturn;
	}

	/**
	 * This method is used to wait explicitly (max 600 seconds)until the angular js/ service call/ page loading completes
	 *
	 * @return boolean
	 */
	public boolean waitForPageLoad(){
		boolean bReturn = false;

		try {
			//can change the wait time as per need
			WebDriverWait wdWait = new WebDriverWait(driver, 600);

			//wait until loading completes within given time
			wdWait.until(new Function<WebDriver, Boolean>(){
				public Boolean apply(WebDriver driver){
					System.out.println("Current Window State :"
						+String.valueOf(((JavascriptExecutor) driver).executeScript("return document.readyState"))+" "
						+String.valueOf(((JavascriptExecutor) driver).executeScript("return angular.element(document).injector().get('$http').pendingRequests.length")));
					return (Boolean) (((JavascriptExecutor) driver).executeScript("return (document.readyState == 'complete') && (angular.element(document).injector().get('$http').pendingRequests.length === 0)"));
				}
			});
			bReturn = true;

		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Loading page takes more than 600 seconds.", "FAIL");
		}
		return bReturn;
	}

	/**
	 * This method is used to implement Thread.sleep/ wait script for given time
	 *
	 * @param waitTimeInMilliSec - time till code should wait
	 * @return boolean
     */
	public boolean waitTime(long waitTimeInMilliSec){
		boolean bReturn = false;

		try {
			Thread.sleep(waitTimeInMilliSec);
			bReturn = true;

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return bReturn;
	}

	/**
	 * This method is used to clear text which is already available in the field
	 * 
	 * @param locateVal - locator value from property file
	 * @return boolean
	 */
	public boolean clearTextFieldBy(String locateVal){
		boolean bReturn = false;

		try {
			driver.findElement(locatorSplit(locateVal)).clear();
			bReturn = true;

		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("The text in the locator: "+locateVal+" could not be cleared.", "FAIL");
		}
		return bReturn;
	}

	/**
	 * This method is used to verify whether the element is displayed in the webpage
	 *
	 * @param locateVal - locator value from property file
	 * @return boolean
     */
	public boolean isElementDisplayedBy(String locateVal){
		boolean bReturn = false;

		try {
			bReturn = driver.findElement(locatorSplit(locateVal)).isDisplayed();

		} catch (NoSuchElementException e) {
			e.printStackTrace();
			Reporter.reportStep("The element with locator: "+locateVal+" is not displayed.", "INFO");
		}
		return bReturn;
	}

	/**
	 * This method is used to verify whether the element is enabled in the webpage
	 *
	 * @param locateVal - locator value from property file
	 * @return boolean
	 */
	public boolean isElementEnabledBy(String locateVal){
		boolean bReturn = false;

		try {
			bReturn = driver.findElement(locatorSplit(locateVal)).isEnabled();

		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("The element with locator: "+locateVal+" is not enabled.", "FAIL");
		}
		return bReturn;
	}

	/**
	 * This method is used to verify whether the element is selected in the webpage
	 *
	 * @param locateVal - locator value from property file
	 * @return boolean
	 */
	public boolean isElementSelectedBy(String locateVal){
		boolean bReturn = false;

		try {
			bReturn = driver.findElement(locatorSplit(locateVal)).isSelected();

		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("The element with locator: "+locateVal+" is not selected.", "FAIL");
		}
		return bReturn;
	}

	/**
	 * This method is used to get list of all elements with the given locator. It will return empty list if there is no element present.
	 *
	 * @param locateVal - locator value from property file
	 * @return List
     */
	public List<WebElement> getAllElementsBy(String locateVal){
		List<WebElement> eReturn = null;

		try {
			driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
			eReturn = driver.findElements(locatorSplit(locateVal));

		} catch (NoSuchElementException e) {
			e.printStackTrace();
			Reporter.reportStep("Elements with given locator: "+locateVal+" could not be found.", "INFO");
		}
		return eReturn;
	}

	/**
	 * This method is used to get Attribute value of an element in a web page
	 *
	 * @param locatorVal - locator value from property file
	 * @param attributeVal - attribute name for which value needs to be retrieved
     * @return String
     */
	public String getElementAttributeValueBy(String locatorVal, String attributeVal){
		String sReturn = "";

		try {
			sReturn = driver.findElement(locatorSplit(locatorVal)).getAttribute(attributeVal);

		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Getting element attribute value for "+locatorVal+" could not be performed.", "FAIL");
		}
		return sReturn;
	}

	/**
	 * This method is used to get CSS value of an element in a web page
	 *
	 * @param locatorVal - locator value from property file
	 * @param cssVal - CSS attribute name for which value needs to be retrieved
	 * @return String
	 */
	public String getCSSValueBy(String locatorVal, String cssVal){
		String sReturn = "";

		try {
			sReturn = driver.findElement(locatorSplit(locatorVal)).getCssValue(cssVal);

		} catch (Exception e) {
			e.printStackTrace();
			Reporter.reportStep("Getting CSS value could not be performed.", "FAIL");
		}
		return sReturn;
	}

}