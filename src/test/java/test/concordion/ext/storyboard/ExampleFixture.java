package test.concordion.ext.storyboard;

import org.concordion.api.extension.Extension;
import org.concordion.ext.StoryboardExtension;
import org.concordion.ext.storyboard.CardResult;
import org.concordion.ext.storyboard.ScreenshotCard;
import org.concordion.ext.storyboard.SectionContainer;
import org.concordion.ext.storyboard.StockCardImage;

public class ExampleFixture {

    @Extension
    private StoryboardExtension storyboard = new StoryboardExtension().setScreenshotTaker(new DummyScreenshotTaker());


	public StoryboardExtension getStoryboard() {
		return storyboard;
	}
	
    public void addScreenshot() {
    	storyboard.addScreenshot("Screenshot Example", "This is a screenshot");
    }

    public void addNotification(String data) {
    	storyboard.addNotification("Notification Example", "Example 1", data, "html", StockCardImage.XML_RESPONSE, CardResult.SUCCESS);
    }
    
    public void addSectionContainer(String title) {    	
    	storyboard.addSectionContainer(title);
    }

    /* StoryGroupSectionContainer */
    public boolean addCard(String title) {    	
    	storyboard.addNotification(title, "Success", StockCardImage.TEXT, CardResult.SUCCESS);
    	return true;
    }
    
     public boolean addFailureCard(String title) {
    	 storyboard.addNotification(title, "Failure", StockCardImage.TEXT, CardResult.FAILURE);
    	return true;
    }
   
    public void addSectionContainerToContainer(String title) {    
    	SectionContainer container = new SectionContainer(title).setAutoClose(false);
    	    	
    	storyboard.addContainer(container);
    }
    
    public void closeContainer() {
    	storyboard.closeContainer();
    }

    /* RemoveUnwantedScreenshots */
    public void addScreenshots() {
    	storyboard.addScreenshot("Screen 1", "Screenshot 1");
    	storyboard.addScreenshot("Screen 2", "Screenshot 2");
    	storyboard.markPriorScreenshotsForRemoval();
    	storyboard.addScreenshot("Screen 3", "Screenshot 3");
    }
 
    public void addFailedScreenshots() {
    	storyboard.addScreenshot("Screen 1", "Screenshot 1");
    	storyboard.addScreenshot("Screen 2", "Screenshot 2");
    	storyboard.markPriorScreenshotsForRemoval();
    	
    	throw new RuntimeException("Random Error");
    }
    
    public void addScreenshotsInSectionBreak() {
    	storyboard.addSectionContainer("Example 1");
    	storyboard.addScreenshot("Screen 1", "Screenshot 1");
    	storyboard.addScreenshot("Screen 2", "Screenshot 2");
    	storyboard.markPriorScreenshotsForRemoval();
    	storyboard.addScreenshot("Screen 3", "Screenshot 3");
    	storyboard.closeContainer();
    	
    	storyboard.addSectionContainer("Example 2");
    	storyboard.addScreenshot("Screen 1", "Screenshot 1");
    	storyboard.addScreenshot("Screen 2", "Screenshot 2");
    	storyboard.markPriorScreenshotsForRemoval();
    	ScreenshotCard card = new ScreenshotCard();
    	card.setTitle("Screen 3");
    	card.setDescription("Screenshot 3");
    	card.setResult(CardResult.FAILURE);
    	storyboard.addCard(card);
    	storyboard.closeContainer();
    } 
    
}
