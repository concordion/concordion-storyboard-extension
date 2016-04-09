package test.concordion.ext.storyboard;

import org.concordion.api.Scope;
import org.concordion.api.extension.ConcordionExtension;
import org.concordion.api.extension.ConcordionExtensionFactory;
import org.concordion.ext.ScreenshotTaker;
import org.concordion.ext.StoryboardExtension;
import org.concordion.ext.StoryboardExtension.AppendMode;

public class DummyStoryboardFactory implements ConcordionExtensionFactory {
    private static StoryboardExtension storyboard = null;
    private static ScreenshotTaker screenshotTaker = null;
    private static boolean addCardOnFailure = true;
    private static boolean takeScreenshotOnTestCompletion = true;
    private static AppendMode appendMode = AppendMode.ItemsToStoryboard;
    private static boolean supressRepeatingFailures = true;
    
    public static void prepareWithScreenShot() {
    	addCardOnFailure = true;
        takeScreenshotOnTestCompletion = true;
    	screenshotTaker = new DummyScreenshotTaker();
    	appendMode = AppendMode.ItemsToStoryboard;
    	supressRepeatingFailures = true;
    }
    
    public static void prepareWithoutScreenShot() {
    	addCardOnFailure = true;
        takeScreenshotOnTestCompletion = true;
    	screenshotTaker = null;
    	appendMode = AppendMode.ItemsToStoryboard;
    	supressRepeatingFailures = true;
	}
    
    public static void setAddCardOnFailure(final boolean value) {
    	addCardOnFailure = value;
	}
    
    public static void setTakeScreenshotOnTestCompletion(final boolean value) {
		takeScreenshotOnTestCompletion = value;
	}
    
    public static void setAppendMode(AppendMode value) {
    	appendMode = value;
    }
    
    public static void setSupressRepeatingFailures(boolean value) {
    	supressRepeatingFailures = value;
    }
    
    @Override
    public ConcordionExtension createExtension() {    	
    	storyboard = new StoryboardExtension();
    	storyboard.setScreenshotTaker(screenshotTaker, Scope.EXAMPLE);
    	storyboard.setAddCardOnFailure(addCardOnFailure);
    	storyboard.setTakeScreenshotOnCompletion(takeScreenshotOnTestCompletion);
    	storyboard.setAppendMode(appendMode);
    	
    	if (!supressRepeatingFailures) {
    		storyboard.setSupressRepeatingFailures(false);
    	}
        return storyboard;
    }
    
    public static StoryboardExtension getStoryboard() {
    	return storyboard;
    }
	
	
}
