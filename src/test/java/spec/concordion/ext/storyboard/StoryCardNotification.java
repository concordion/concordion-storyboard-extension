package spec.concordion.ext.storyboard;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.concordion.api.extension.Extension;
import org.concordion.ext.StoryboardExtension;
import org.concordion.ext.storyboard.CardResult;
import org.concordion.ext.storyboard.NotificationCard;
import org.concordion.ext.storyboard.StockCardImage;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.Before;
import org.junit.runner.RunWith;

import test.concordion.ProcessingResult;
import test.concordion.ext.storyboard.DummyStoryboardFactory;

@RunWith(ConcordionRunner.class)
public class StoryCardNotification extends AcceptanceTest {
    
    public static final String SPEC_NAME = "/" + StoryCardNotification.class.getName().replaceAll("\\.","/");
    private int example = 0;
    
    @Extension
    public StoryboardExtension storyboard = new StoryboardExtension().setScreenshotTaker(null).setAddCardOnFailure(false);
    
    @Before 
    public void installExtension() {
        System.setProperty("concordion.extensions", DummyStoryboardFactory.class.getName());
        DummyStoryboardFactory.prepareWithoutScreenShot();
    }
    
    public String render(String fragment, String acronym) throws Exception {
    	ProcessingResult result = getTestRig().processFragment(fragment, SPEC_NAME + ++example);    	
    	
    	NotificationCard card = new NotificationCard();    	
    	card.setTitle(storyboard.getCurrentExampleTitle());
    	card.setDescription(card.getDescription() + ": click image to see example");
    	
	    //Not sure what going on with this html but it doesn't like this script definition in short form
    	card.setData(prettyFormat(result.getXOMDocument().toXML().replace("storyboard.js\" />", "storyboard.js\"></script>"), 4));
    	card.setFileExtension("html");
    	card.setCardImage(StockCardImage.HTML);    	
    	card.setResult(CardResult.SUCCESS);
    	
    	storyboard.addCard(card);
    	
        return result.getElementXML("storyboard");
    }
    
    public String renderWithFailureOff(String fragment, String acronym) throws Exception {
    	DummyStoryboardFactory.setAddCardOnFailure(false);
    	
    	String result = render(fragment, acronym);
    	
    	DummyStoryboardFactory.setAddCardOnFailure(true);
    	
    	return result;
    }
    
    public void addNotification(String data) {
    	DummyStoryboardFactory.getStoryboard().addNotification("Notification Example", "Example 1", data, "html", StockCardImage.XML_RESPONSE, CardResult.SUCCESS);
    }
        
    public void allowRepeatedFailures() {
    	DummyStoryboardFactory.setSupressRepeatingFailures(false);
    }
    
    public int failureCardCount(String fragment) {
    	Pattern p = Pattern.compile(Pattern.quote("Test Failed"));
    	Matcher m = p.matcher(fragment);
    	
    	int count = 0;
    	while (m.find()) {
    		count ++;
    	}
    	return count;
    }
    
    public boolean notificationCardAdded(String fragment) {
    	return fragment.contains("Notification Example");    	
    }
    
    public boolean failureCardAdded(String fragment) {
    	return fragment.contains("Test Failed");
    }
    
    public boolean noCardAdded(String fragment) {
    	return "".equals(fragment);
    }
    
    public boolean exceptionCardAdded(String fragment) {    	
    	return fragment.contains("Exception");
    }
}
