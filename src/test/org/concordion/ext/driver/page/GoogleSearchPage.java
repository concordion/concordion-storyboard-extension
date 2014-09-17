package org.concordion.ext.driver.page;

import org.concordion.ext.StoryboardExtension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * A WebDriver Page Object corresponding to the Google Search Page.
 */
public class GoogleSearchPage {
	
    @CacheLookup
	@FindBy(name = "q") 
	private WebElement queryBox;
	
    @CacheLookup
	@FindBy(name = "btnG") 
	private WebElement submitButton;
	
    @FindBy(className = "nonExistent")
    private WebElement nonExistentLink;

    private final WebDriver driver;
    private final StoryboardExtension storyboard;
    
	/**
	 * Opens the Google Search Page.
	 */
	public GoogleSearchPage(WebDriver webDriver, StoryboardExtension storyboard) {
		this.driver = webDriver;
		this.storyboard = storyboard;
		
        PageFactory.initElements(driver, this);
		driver.get("http://www.google.com");
		
		this.storyboard.addScreenshot(this.getClass().getSimpleName(), "Opened Google's web page");
	}

    /**
     * Searches for the specified string and opens the results page, 
     * waiting for the page to fully load. 
     */
	public GoogleResultsPage searchFor(String query) {
        queryBox.sendKeys(query);
        queryBox.sendKeys(Keys.ESCAPE);
        storyboard.addScreenshot(this.getClass().getSimpleName(), "Entered search text, and about to click search button");
		submitButton.click();
		return new GoogleResultsPage(driver, storyboard);
	}
    
    public void clickOnNonExistentLink() {
    	storyboard.addScreenshot(this.getClass().getSimpleName(), "About to click non existant link");
        nonExistentLink.click();
    }
}