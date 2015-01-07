package test.concordion.ext.storyboard;

import org.concordion.api.extension.ConcordionExtension;
import org.concordion.api.extension.ConcordionExtensionFactory;
import org.concordion.ext.ScreenshotTaker;
import org.concordion.ext.StoryboardExtension;

public class DummyStoryboardFactory implements ConcordionExtensionFactory {
    private static StoryboardExtension storyboard = null;
    private static ScreenshotTaker screenshotTaker = null;
    private static boolean addCardOnFailure = true;
    
    public static void prepareWithScreenShot() {
    	screenshotTaker = new DummyScreenshotTaker();
    }
    public static void prepareWithoutScreenShot() {
    	screenshotTaker = null;
	}
    
    public static void setAddCardOnFailure(final boolean value) {
    	addCardOnFailure = value;
	}    
    
    @Override
    public ConcordionExtension createExtension() {    	
    	storyboard = new StoryboardExtension();
    	storyboard.setScreenshotTaker(screenshotTaker);
    	storyboard.setAddCardOnFailure(addCardOnFailure);
        return storyboard;
    }
    
    public static StoryboardExtension getStoryboard() {
    	return storyboard;
    }
}
