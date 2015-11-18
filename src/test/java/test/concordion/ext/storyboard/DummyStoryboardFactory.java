package test.concordion.ext.storyboard;

import org.concordion.api.extension.ConcordionExtension;
import org.concordion.api.extension.ConcordionExtensionFactory;
import org.concordion.ext.ScreenshotTaker;
import org.concordion.ext.StoryboardExtension;

public class DummyStoryboardFactory implements ConcordionExtensionFactory {
    private static StoryboardExtension storyboard = null;
    private static ScreenshotTaker screenshotTaker = null;
    private static boolean addCardOnFailure = true;
    private static boolean takeScreenshotOnTestCompletion = true;
    private static boolean addCardsToExample = false;
    private static boolean autoAddSectionForExample = false;
    
    public static void prepareWithScreenShot() {
    	addCardOnFailure = true;
        takeScreenshotOnTestCompletion = true;
    	screenshotTaker = new DummyScreenshotTaker();
    }
    
    public static void prepareWithoutScreenShot() {
    	addCardOnFailure = true;
        takeScreenshotOnTestCompletion = true;
    	screenshotTaker = null;
	}
    
    public static void setAddCardsToExample(final boolean value) {
    	addCardsToExample = value;
	}
    
    public static void setAutoAddSectionForExample(final boolean value) {
    	autoAddSectionForExample = value;
	}
    
    public static void setAddCardOnFailure(final boolean value) {
    	addCardOnFailure = value;
	}
    
    public static void setTakeScreenshotOnTestCompletion(final boolean value) {
		takeScreenshotOnTestCompletion = value;
	}
    
    @Override
    public ConcordionExtension createExtension() {    	
    	storyboard = new StoryboardExtension();
    	storyboard.setScreenshotTaker(screenshotTaker);
    	storyboard.setAddCardOnFailure(addCardOnFailure);
    	storyboard.setTakeScreenshotOnCompletion(takeScreenshotOnTestCompletion);
    	storyboard.setAddCardsToExample(addCardsToExample);
    	storyboard.setAutoAddSectionForExample(autoAddSectionForExample);
    	
        return storyboard;
    }
    
    public static StoryboardExtension getStoryboard() {
    	return storyboard;
    }
	
	
}
