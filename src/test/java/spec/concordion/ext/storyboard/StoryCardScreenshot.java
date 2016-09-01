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
import test.concordion.ext.storyboard.ExampleFixture;

@RunWith(ConcordionRunner.class)
public class StoryCardScreenshot extends AcceptanceTest {
    
    public static final String SPEC_NAME = "/" + StoryCardScreenshot.class.getName().replaceAll("\\.","/");
    private int example = 0;
    private ExampleFixture fixture;
    
    @Extension
    public StoryboardExtension storyboard = new StoryboardExtension();
    
    @Before
    public void before() {
    	fixture = new ExampleFixture();
    }
    
    public String render(String fragment) throws Exception {
    	example++;
    	
    	ProcessingResult result = getTestRig()
    			.withFixture(fixture)
    			.processFragment(fragment, SPEC_NAME + "Example" + example);    	
    	
    	NotificationCard card = new NotificationCard();    	
    	card.setTitle(storyboard.getCurrentExampleTitle());
    	card.setDescription("Click image to see example");
    	
	    //Not sure what going on with this html but it doesn't like this script definition in short form
    	card.setData(prettyFormat(result.getXOMDocument().toXML().replace("storyboard.js\" />", "storyboard.js\"></script>"), 4));
    	card.setFileExtension("html");
    	card.setCardImage(StockCardImage.HTML);    	
    	card.setResult(CardResult.SUCCESS);
    	
    	storyboard.addCard(card);
    	
        return result.getElementXML("storyboard");
    }

    public void allowRepeatedFailures() {
    	fixture.getStoryboard().setSupressRepeatingFailures(false);
    }
        
    public boolean screenshotCardAdded(String fragment) {
    	return fragment.contains("This is a screenshot");    	
    }
    
    public boolean failureCardAdded(String fragment) {
    	return fragment.contains("Test Failed");
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
    
    public boolean exceptionCardAdded(String fragment) {
    	return fragment.contains("Exception");
    }
    
    public boolean noCardAdded(String fragment) {
    	return "".equals(fragment);
    }
}
