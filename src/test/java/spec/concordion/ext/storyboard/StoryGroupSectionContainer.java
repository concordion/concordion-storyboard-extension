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
public class StoryGroupSectionContainer extends AcceptanceTest {
    
    public static final String SPEC_NAME = "/" + StoryGroupSectionContainer.class.getName().replaceAll("\\.","/");
    private int example = 0;
    
    @Extension
    public StoryboardExtension storyboard = new StoryboardExtension().setScreenshotTaker(null).setAddCardOnFailure(false).setwho("StoryCardSectionBreakTest");
    
    @Before 
    public void installExtension() {
        System.setProperty("concordion.extensions", DummyStoryboardFactory.class.getName());
        DummyStoryboardFactory.prepareWithoutScreenShot();
    }
    
    public String renderAutoAddSection(String fragment) throws Exception {
    	return render(fragment, true);
    }
    
    public String render(String fragment) throws Exception {
    	return render(fragment, false);
    }
    
    private String render(String fragment, boolean autoAdd) throws Exception {
    	String title = storyboard.getExampleTitle();
    	
    	DummyStoryboardFactory.setAutoAddSectionForExample(autoAdd);
    	
    	ProcessingResult result = getTestRig().processFragment(fragment, SPEC_NAME + example);    	

    	DummyStoryboardFactory.setAutoAddSectionForExample(false);
    	
    	NotificationCard card = new NotificationCard();    	
    	card.setTitle(title);
    	card.setDescription("Click image to see example");
    	
	    //TODO Not sure what going on with this html but it doesn't like this script definition in short form
    	card.setData(prettyFormat(result.getXOMDocument().toXML().replace("storyboard.js\" />", "storyboard.js\"></script>"), 4));
    	card.setFileExtension("html");
    	card.setCardImage(StockCardImage.HTML);    	
    	card.setResult(CardResult.SUCCESS);
    	
    	storyboard.addCard(card);
    	
        return result.getElementXML("storyboard");
    }
    
    public boolean addCard(String title) {    	
    	DummyStoryboardFactory.getStoryboard().addNotification(title, "", StockCardImage.TEXT, CardResult.SUCCESS);
    	return true;
    }
    
    public void addSectionBreak(String data) {    	
    	DummyStoryboardFactory.getStoryboard().addSectionBreak(data);
    	DummyStoryboardFactory.getStoryboard().addNotification(data + " Section Member", "Example", "", StockCardImage.TEXT, CardResult.SUCCESS);
    }
    
    public void addSectionBreak() {
    	DummyStoryboardFactory.getStoryboard().addSectionBreak("Example");
    	DummyStoryboardFactory.getStoryboard().addNotification("Example Section Member", "Example", "", StockCardImage.TEXT, CardResult.SUCCESS);
    	
    	DummyStoryboardFactory.getStoryboard().addSectionBreak("");
    	DummyStoryboardFactory.getStoryboard().addNotification("Storyboard Member", "Example", "", StockCardImage.TEXT, CardResult.SUCCESS);
    }
    
    public void autoAddSectionBreak() {
    	DummyStoryboardFactory.getStoryboard().addSectionBreak("Example");
    	DummyStoryboardFactory.getStoryboard().addNotification("Example Section Member", "Example", "", StockCardImage.TEXT, CardResult.SUCCESS);
    	
    	DummyStoryboardFactory.getStoryboard().addSectionBreak("");
    	DummyStoryboardFactory.getStoryboard().addNotification("Storyboard Member", "Example", "", StockCardImage.TEXT, CardResult.SUCCESS);
    }
    
    public void addSectionBreaks() {
    	DummyStoryboardFactory.getStoryboard().addSectionBreak("Example 1");
    	DummyStoryboardFactory.getStoryboard().addNotification("Example Section Member", "Example", "", StockCardImage.TEXT, CardResult.SUCCESS);
    	
    	DummyStoryboardFactory.getStoryboard().addSectionBreak("Example 2");
    	DummyStoryboardFactory.getStoryboard().addNotification("Storyboard Member", "Example", "", StockCardImage.TEXT, CardResult.SUCCESS);
    }
    
    public void addFailureToSection(String data) {
    	DummyStoryboardFactory.getStoryboard().addSectionBreak(data);
    	DummyStoryboardFactory.getStoryboard().addNotification(data + " Section Member", "Example", "", StockCardImage.TEXT, CardResult.FAILURE);
    }
    
        
    public boolean sectionAddedCollapsed(String fragment) {
    	return fragment.contains("class=\"toggle-box scsuccess\">Example</label>");    	
    }
    
    public boolean sectionAddedExpanded(String fragment) {
    	return fragment.contains("class=\"toggle-box scsuccess\">Setup</label>");    	
    }
    
    public boolean sectionFailed(String fragment) {
    	return fragment.contains("class=\"toggle-box scfailure\"") && fragment.contains("checked");    	
    }
    
    public boolean multipleSectionsAdded(String fragment) {    	
    	return fragment.contains("<input id=\"toggleheader0\"") && fragment.contains("<input id=\"toggleheader2\"");     	
    }
}