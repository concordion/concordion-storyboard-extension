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
import test.concordion.ext.storyboard.ExampleFixture;

@RunWith(ConcordionRunner.class)
public class StoryGroupExampleContainer extends AcceptanceTest {
    
    public static final String SPEC_NAME = "/" + StoryGroupExampleContainer.class.getName().replaceAll("\\.","/");
    private int example = 0;
    private ExampleFixture fixture;
    
    @Extension
    public StoryboardExtension storyboard = new StoryboardExtension();
   
    @Before
    public void before() {
    	fixture = new ExampleFixture();
    	//fixture.getStoryboard().setAppendMode(AppendTo.EXAMPLE);
    }
    
    public String render(String fragment) throws Exception {
    	example++;
    	ProcessingResult result = getTestRig().withFixture(fixture).processFragment(fragment, SPEC_NAME + example);  	

    	NotificationCard card = new NotificationCard();    	
    	card.setTitle(storyboard.getCurrentExampleTitle());
    	card.setDescription("Click image to see example");
    	
	    //Not sure what going on with this html but it doesn't like this script definition in short form
    	card.setData(prettyFormat(result.getXOMDocument().toXML().replace("storyboard.js\" />", "storyboard.js\"></script>"), 4));
    	card.setFileExtension("html");
    	card.setCardImage(StockCardImage.HTML);    	
    	card.setResult(CardResult.SUCCESS);
    	
    	storyboard.addCard(card);
    	
        return result.getElementXML("testinput");
    }
    
    public boolean addCard(String title) {    	
    	fixture.getStoryboard().addNotification(title, "", StockCardImage.TEXT, CardResult.SUCCESS);
    	return true;
    }
    
    public boolean addFailureCard(String title) {
    	fixture.getStoryboard().addNotification(title, "Success", StockCardImage.TEXT, CardResult.SUCCESS);
    	fixture.getStoryboard().addNotification(title, "Failure", StockCardImage.TEXT, CardResult.FAILURE);
    	return true;
    }
    
    public boolean sectionAddedCollapsed(String fragment) {
    	return fragment.contains("class=\"toggle-example scfailure\">Storyboard</label>");    	
    }
    
    public boolean sectionAddedExpanded(String fragment) {
    	return fragment.contains("class=\"toggle-example scsuccess\">Storyboard</label>");    	
    }
    
    public boolean sectionFailed(String fragment) {
    	return fragment.contains("class=\"toggle-example scfailure\"") && fragment.contains("checked");    	
    }
}
