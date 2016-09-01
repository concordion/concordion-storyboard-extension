package test.concordion.ext.storyboard;

import org.concordion.api.extension.Extension;
import org.concordion.ext.LoggingFormatterExtension;
import org.concordion.ext.StoryboardExtension;
import org.concordion.ext.StoryboardLogListener;
import org.concordion.ext.StoryboardMarkerFactory;
import org.concordion.slf4j.ext.MediaType;
import org.concordion.slf4j.ext.ReportLogger;
import org.concordion.slf4j.ext.ReportLoggerFactory;

public class DummyTest {
	private static final ReportLogger LOGGER = ReportLoggerFactory.getReportLogger(DummyTest.class);
	
	@Extension
    public StoryboardExtension storyboard = new StoryboardExtension()
    		.setSupressRepeatingFailures(false);
    
    @Extension
    public LoggingFormatterExtension logging = new LoggingFormatterExtension()
    		.registerListener(new StoryboardLogListener(storyboard));

    public DummyTest() {
    	ReportLoggerFactory.setScreenshotTaker(new DummyScreenshotTaker());
    }
    
    public boolean doScreenshot() {
    	LOGGER.debug("hello");
    	
    	LOGGER.with()
    		.message("This is a screenshot")
    		.screenshot()
    		.marker(StoryboardMarkerFactory.addCard("Hello"))
    		.debug();
    	
    	return true;
    }
    
    public boolean doAttachment() {
    	LOGGER.with()
    		.message("This is an attachment")
    		.attachment("This is some data", "example.txt", MediaType.PLAIN_TEXT)
    		.marker(StoryboardMarkerFactory.addCard("Hello"))
    		.debug();
    	
    	return true;
    }

    public boolean doException() {
    	throw new RuntimeException("I threw an execption");
    }
    
    public boolean doFailure() {
    	return false;
    }
}
