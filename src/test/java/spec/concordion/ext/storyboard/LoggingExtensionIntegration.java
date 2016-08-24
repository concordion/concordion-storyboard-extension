package spec.concordion.ext.storyboard;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.concordion.api.Resource;
import org.concordion.api.extension.Extension;
import org.concordion.ext.StoryboardExtension;
import org.concordion.ext.StoryboardExtension.AppendTo;
import org.concordion.ext.loggingFormatter.LogbackAdaptor;
import org.concordion.ext.storyboard.CardResult;
import org.concordion.ext.storyboard.NotificationCard;
import org.concordion.ext.storyboard.StockCardImage;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.Before;
import org.junit.runner.RunWith;
import test.concordion.ProcessingResult;
import test.concordion.TestRig;
import test.concordion.ext.storyboard.DummyStoryboardFactory;
import test.concordion.ext.storyboard.DummyTest;

@RunWith(ConcordionRunner.class)
public class LoggingExtensionIntegration extends AcceptanceTest {
    public static final String SPEC_NAME = "/" + LoggingExtensionIntegration.class.getName().replaceAll("\\.","/");
    private int example = 0;
    
    @Extension
    public StoryboardExtension storyboard = new StoryboardExtension().setAddCardOnThrowable(false).setAddCardOnFailure(false).setAppendMode(AppendTo.STORYBOARD);
    
    
    @Before 
    public void removeExtension() {
        System.clearProperty("concordion.extensions");
    }
    
    public String renderAutoAddSection(String fragment) throws Exception {
    	DummyStoryboardFactory.setAppendMode(AppendTo.NEW_STORYBOARD_SECTION_PER_EXAMPLE);
    	ProcessingResult result = renderTest(fragment);
    	
    	return result.getElementXML("storyboard");
    }
    
    public String renderAddToExample(String fragment) throws Exception {
    	DummyStoryboardFactory.setAppendMode(AppendTo.EXAMPLE);
    	ProcessingResult result = renderTest(fragment);
    	
    	return result.getElementXML("testinput");
    }
    
    public String render(String fragment) throws Exception {
    	DummyStoryboardFactory.setAppendMode(AppendTo.STORYBOARD);
    	
    	return renderTest(fragment).getElementXML("storyboard");
    }
    
    private ProcessingResult renderTest(String fragment) throws Exception {
    	example++;
    	
    	ProcessingResult result = getTestRig().processFragment(fragment, SPEC_NAME + example);    	

    	NotificationCard card = new NotificationCard();    	
    	card.setTitle(storyboard.getCurrentExampleTitle());
    	card.setDescription("Click image to see example");
    	
	    //Not sure what going on with this html but it doesn't like this script definition in short form
    	card.setData(prettyFormat(result.getXOMDocument().toXML().replace("storyboard.js\" />", "storyboard.js\"></script>"), 4));
    	card.setFileExtension("html");
    	card.setCardImage(StockCardImage.HTML);    	
    	card.setResult(CardResult.SUCCESS);
    	
    	storyboard.addCard(card);
    	
        return result;
    }
    
    static {
		LogbackAdaptor.logInternalStatus();
	}
    private int countOccurrences(String haystack, String needle)
    {
    	Pattern p = Pattern.compile(needle);
    	Matcher m = p.matcher(haystack);
    	int count = 0;
    	while (m.find()){
    	    count +=1;
    	}
    	
    	return count;
    }
    

    public boolean screenshot() throws InstantiationException, IllegalAccessException {
    	String fragment = "<p concordion:assertTrue=\"doScreenshot()\">false</p>";
    	ProcessingResult result = getTestRig().withFixture(new DummyTest()).processFragment(fragment);
    	
    	storyboard.addNotification("screenshot", "generated specification", result.toXML(), StockCardImage.HTML, CardResult.SUCCESS);
    	
    	return countOccurrences(result.toXML(), "This is a screenshot</p>") == 1;
    }
    
    public boolean attachment() throws InstantiationException, IllegalAccessException {
    	String fragment = "<p concordion:assertTrue=\"doAttachment()\">false</p>";
    	ProcessingResult result = getTestRig().withFixture(new DummyTest()).processFragment(fragment);
    	
    	storyboard.addNotification("attachment", "generated specification", result.toXML(), StockCardImage.HTML, CardResult.SUCCESS);
    	
    	return countOccurrences(result.toXML(), "This is an attachment</p>") == 1;
    }
    
    public boolean exception() throws InstantiationException, IllegalAccessException {
    	String fragment = "<p concordion:assertTrue=\"doException()\">false</p>";
    	ProcessingResult result = getTestRig().withFixture(new DummyTest()).processFragment(fragment);
    	
    	storyboard.addNotification("exception", "generated specification", result.toXML(), StockCardImage.HTML, CardResult.SUCCESS);
    	
    	return countOccurrences(result.toXML(), "RuntimeException</p>") == 1;
    }
    
    public boolean failure() throws InstantiationException, IllegalAccessException { 
    	String fragment = "<p concordion:assertTrue=\"doFailure()\">false</p>";
    	ProcessingResult result = getTestRig().withFixture(new DummyTest()).processFragment(fragment);
    	
    	storyboard.addNotification("failure", "generated specification", result.toXML(), StockCardImage.HTML, CardResult.SUCCESS);
    	
    	return countOccurrences(result.toXML(), "Test Failed</p>") == 1;
    }
    
    @Override
    protected TestRig getTestRig() throws InstantiationException, IllegalAccessException {
    	TestRig rig = super.getTestRig();
    	
    	rig.withResource(new Resource("/org/concordion/ext/resource/tooltip.css"), "");
		rig.withResource(new Resource("/org/concordion/ext/resource/bubble.gif"), "");
		rig.withResource(new Resource("/org/concordion/ext/resource/bubble_filler.gif"), "");
		rig.withResource(new Resource("/org/concordion/ext/resource/i16.png"), "");
		rig.withResource(new Resource("/font-awesome-4.6.3/css/font-awesome.css"), "");
		rig.withResource(new Resource("/font-awesome-4.6.3/fonts/fontawesome-webfont.eot"), "");
		rig.withResource(new Resource("/font-awesome-4.6.3/fonts/fontawesome-webfont.svg"), "");
		rig.withResource(new Resource("/font-awesome-4.6.3/fonts/fontawesome-webfont.ttf"), "");
		rig.withResource(new Resource("/font-awesome-4.6.3/fonts/fontawesome-webfont.woff"), "");
		rig.withResource(new Resource("/font-awesome-4.6.3/fonts/fontawesome-webfont.woff2"), "");
		rig.withResource(new Resource("/font-awesome-4.6.3/fonts/FontAwesome.otf"), "");

    	return rig;
    }
}
