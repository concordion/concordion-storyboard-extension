package spec.concordion.ext.storyboard;

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
public class StoryCardGroup extends AcceptanceTest {
    
    public static final String SPEC_NAME = "/" + StoryCardGroup.class.getName().replaceAll("\\.","/");
    private int example = 0;
    
    @Extension
    public StoryboardExtension storyboard = new StoryboardExtension().setScreenshotTaker(null);
    
    @Before 
    public void installExtension() {
        System.setProperty("concordion.extensions", DummyStoryboardFactory.class.getName());
        DummyStoryboardFactory.prepareWithoutScreenShot();
    }
    
    public String render(String fragment, String acronym) throws Exception {    	
    	example++;
    	
    	String title = "";
    	
    	switch (example) {
    	case 1:
    		title = "Start Group";
			break;
    	case 2:
    		title = "Stop Card";
			break;
    	case 3:
    		title = "Fail Group";
			break;
    	}
    	
    	ProcessingResult result = getTestRig().processFragment(fragment, SPEC_NAME + example);    	
    	
    	NotificationCard card = new NotificationCard();    	
    	card.setTitle("Example " + example + ": " + title);	    
    	card.setDescription("Click image to see example");
    	
	    //TODO Not sure what going on with this html but it doesn't like this script definition in short form
    	card.setData(prettyFormat(result.getXOMDocument().toXML().replace("storyboard.js\" />", "storyboard.js\"></script>"), 4));
    	card.setFileExtension("html");
    	card.setCardImage(StockCardImage.HTML);    	
    	card.setResult(CardResult.SUCCESS);
    	
    	storyboard.addCard(card);
    	
        return result.getElementXML("storyboard");
    }
    
    public void startGroup(String data) {    	
    	DummyStoryboardFactory.getStoryboard().startGroup(data);
    	DummyStoryboardFactory.getStoryboard().addNotification(data + " Group Member", "Example", "", StockCardImage.TEXT, CardResult.SUCCESS);
    }
 
    public void stopGroup(String data) {
    	DummyStoryboardFactory.getStoryboard().startGroup(data);
    	DummyStoryboardFactory.getStoryboard().addNotification(data + " Group Member", "Example", "", StockCardImage.TEXT, CardResult.SUCCESS);
    	DummyStoryboardFactory.getStoryboard().stopGroup();
    }
    
    public void addFailureToGroup(String data) {
    	DummyStoryboardFactory.getStoryboard().startGroup(data);
    	DummyStoryboardFactory.getStoryboard().addNotification(data + " Group Member", "Example", "", StockCardImage.TEXT, CardResult.FAILURE);
    	DummyStoryboardFactory.getStoryboard().stopGroup();
    }
    
        
    public boolean groupAddedCollapsed(String fragment) {
    	return fragment.contains("src=\"../../../../expand.png\"");    	
    }
    
    public boolean groupAddedExpanded(String fragment) {
    	return fragment.contains("src=\"../../../../collapse.png\"");    	
    }
    
    public boolean groupFailed(String fragment) {
    	return fragment.contains("<p class=\"scsummary scfailure\">Setup</p>");    	
    }
    
}
