package org.concordion.ext.driver.page;

import java.util.List;

import org.concordion.ext.StoryboardExtension;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A WebDriver Page Object corresponding to the Google Results Page.
 */
public class GoogleResultsPage {
	
    @CacheLookup
	@FindBy(id = "res") 
	private WebElement resultWrapper;

    @CacheLookup
	@FindBy(className = "l")
	private WebElement firstResultLink;
	
    @CacheLookup
    @FindBy(id = "cwos")
    private WebElement calcResultLink;

    private static Logger logger = LoggerFactory.getLogger(GoogleResultsPage.class.getName());

    private final WebDriver driver;
    private final StoryboardExtension storyboard;

	/**
	 * Initialises the results page and waits for the page to fully load.
	 * Assumes that the results page is already loading.
	 */
    public GoogleResultsPage(WebDriver driver, StoryboardExtension storyboard) {
		this.driver = driver;
		this.storyboard = storyboard;
        PageFactory.initElements(driver, this);
        waitForFooter();
	}

	/**
	 * Checks whether the specified text occurs in any result on the results page.
	 */
    public boolean resultsContain(String text) {
		List<WebElement> resultsText = resultWrapper.findElements(By.className("s"));
		for (WebElement result : resultsText) {
            if (result.getText().contains(text)) {
                return true;
            }
        }
		return false;
	}

	/**
	 * Returns the text of the topmost result from the results page.
	 */
    public String getTopResultTitle() {
		return firstResultLink.getText();
	}
	
    /**
     * Returns the text of the topmost result from the results page.
     */
    public String getCalculatorResult() {
        String result = calcResultLink.getText();        
        logger.info(String.format("result is '%s'", result));
        return result;
    }
    
    private void waitForFooter() {
		WebDriverWait wait = new  WebDriverWait(driver, 30);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("foot")));
	}
}