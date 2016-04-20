package spec.concordion.ext.storyboard;

import org.concordion.api.extension.Extension;
import org.concordion.ext.StoryboardExtension;
import org.concordion.ext.StoryboardExtension.AppendTo;
import org.concordion.ext.storyboard.CardResult;
import org.concordion.ext.storyboard.NotificationCard;
import org.concordion.ext.storyboard.SectionContainer;
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
    public StoryboardExtension storyboard = new StoryboardExtension().setAddCardOnThrowable(false).setAddCardOnFailure(false).setAppendMode(AppendTo.STORYBOARD);
    
    @Before 
    public void installExtension() {
        System.setProperty("concordion.extensions", DummyStoryboardFactory.class.getName());
        DummyStoryboardFactory.prepareWithoutScreenShot();
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
    
    public boolean addCard(String title) {    	
    	DummyStoryboardFactory.getStoryboard().addNotification(title, "Success", StockCardImage.TEXT, CardResult.SUCCESS);
    	return true;
    }
    
     public boolean addFailureCard(String title) {
    	DummyStoryboardFactory.getStoryboard().addNotification(title, "Failure", StockCardImage.TEXT, CardResult.FAILURE);
    	return true;
    }
    
    public void addSectionContainer(String title) {    	
    	DummyStoryboardFactory.getStoryboard().addSectionContainer(title);
    }
    
    public void addSectionContainerToContainer(String title) {    
    	SectionContainer container = new SectionContainer();
    	container.setTitle(title);
    	
    	DummyStoryboardFactory.getStoryboard().addContainerToContainer(container);
    }
    
    public void closeContainer() {
    	DummyStoryboardFactory.getStoryboard().closeContainer();
    }
    
        
    public boolean sectionAddedCollapsed(String fragment) {
    	return fragment.contains("class=\"toggle-box scsuccess\">") && !fragment.contains("checked");    	
    }

    public boolean sectionAddedExpanded(String fragment) {
    	return fragment.contains("class=\"toggle-box scfailure\"") && fragment.contains("checked");    	
    }

    public int getSectionCount(String fragment) {
    	int found = 0;
    	int pos = 0;
    	
    	while ((pos = fragment.indexOf("<div class=\"toggle-box-container\">", pos + 1)) > 0) {
    		// Check not embedded container, only counting top level containers
    		if (fragment.substring(pos - 5, pos).indexOf("</ul>") < 0) {
    			found ++;
    		}
    	}
    	
    	return found;
    }

    public int getCardCount(String fragment) {
    	int found = 0;
    	int pos = 0;
    	
    	pos = fragment.lastIndexOf("<div class=\"toggle-box-content\">");
    	if (pos < 0) return 0;
    	
    	pos = fragment.indexOf("<ul>", pos);
    	if (pos < 0) return 0;
    	
    	pos = fragment.indexOf("<ul>", pos + 1);
    	if (pos < 0) return 0;
    	    	
    	while ((pos = fragment.indexOf("<li class=\"storycard\">", pos + 1)) > 0) {
    		found ++;
    	}
    	
    	return found;
    }
}
