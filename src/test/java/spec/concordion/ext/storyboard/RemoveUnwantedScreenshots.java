package spec.concordion.ext.storyboard;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.concordion.api.extension.Extension;
import org.concordion.ext.StoryboardExtension;
import org.concordion.ext.storyboard.CardResult;
import org.concordion.ext.storyboard.NotificationCard;
import org.concordion.ext.storyboard.ScreenshotCard;
import org.concordion.ext.storyboard.StockCardImage;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.Before;
import org.junit.runner.RunWith;

import test.concordion.ProcessingResult;
import test.concordion.TestRig;
import test.concordion.ext.storyboard.DummyStoryboardFactory;

@RunWith(ConcordionRunner.class)
public class RemoveUnwantedScreenshots extends AcceptanceTest {
    
    public static final String SPEC_NAME = "/" + RemoveUnwantedScreenshots.class.getName().replaceAll("\\.","/");
    private int example = 0;
    TestRig rig = null;
    
    @Extension
    public StoryboardExtension storyboard = new StoryboardExtension().setScreenshotTaker(null).setAddCardOnFailure(false);
    
    @Before 
    public void installExtension() {
    	File[] files = getImages(false);

		for (File file : files) {
			System.gc();
		    file.delete();
		}
		
        System.setProperty("concordion.extensions", DummyStoryboardFactory.class.getName());
        DummyStoryboardFactory.prepareWithScreenShot();
        DummyStoryboardFactory.setAddCardOnFailure(false);
        DummyStoryboardFactory.setTakeScreenshotOnTestCompletion(false);
    }
    
    public String render(String fragment, String acronym) throws Exception {
    	example++;
    	
    	String title = "";
    	
    	switch (example) {
    	case 1:
    		title = "Delete Cards on Success";
			break;
			
    	case 2:
    		title = "Don't delete cards on failure";
    		break;
    		
    	case 3:
    		title = "Delete Cards within Section Break";
			break;
    	}
    	
    	rig = getTestRig();
    	ProcessingResult result = rig.processFragment(fragment, SPEC_NAME + example);    	

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
    
    public void addScreenshots() {
    	StoryboardExtension dummyboard = DummyStoryboardFactory.getStoryboard();
    	
    	dummyboard.addScreenshot("Screen 1", "Screenshot 1");
    	dummyboard.addScreenshot("Screen 2", "Screenshot 2");
    	dummyboard.setRemovePriorScreenshotsOnSuccess();
    	dummyboard.addScreenshot("Screen 3", "Screenshot 3");
    }
 
    public void addFailedScreenshots() {
    	StoryboardExtension dummyboard = DummyStoryboardFactory.getStoryboard();
    	
    	dummyboard.addScreenshot("Screen 1", "Screenshot 1");
    	dummyboard.addScreenshot("Screen 2", "Screenshot 2");
    	dummyboard.setRemovePriorScreenshotsOnSuccess();
    	
    	throw new RuntimeException("Random Error");
    }
    
    public void addScreenshotsInSectionBreak() {
    	StoryboardExtension dummyboard = DummyStoryboardFactory.getStoryboard();
    	    	
    	dummyboard.addSectionBreak("Example 1");
    	dummyboard.addScreenshot("Screen 1", "Screenshot 1");
    	dummyboard.addScreenshot("Screen 2", "Screenshot 2");
    	dummyboard.setRemovePriorScreenshotsOnSuccess();
    	dummyboard.addScreenshot("Screen 3", "Screenshot 3");
    	
    	dummyboard.addSectionBreak("Example 2");
    	dummyboard.addScreenshot("Screen 1", "Screenshot 1");
    	dummyboard.addScreenshot("Screen 2", "Screenshot 2");
    	dummyboard.setRemovePriorScreenshotsOnSuccess();
    	
    	ScreenshotCard card = new ScreenshotCard();
    	card.setTitle("Screen 3");
    	card.setDescription("Screenshot 3");
    	card.setResult(CardResult.FAILURE);
    	
    	dummyboard.addCard(card);
    } 
    
    public int getCountScreenShots(String fragment) {
    	String search = "<a href=\"RemoveUnwantedScreenshots";
    	int pos = 0;
    	int count = 0;
    	
    	while ((pos = fragment.indexOf(search, pos)) > 0) {
    		pos += search.length();
    		count ++;
    	}
    	    	
    	return count;
    }
        
    public int getCountImages() {
    	return getImages(true).length;
    }
    
    private File[] getImages(boolean forSpec) {
    	System.out.println("Base output directory: " + this.getBaseOutputDir());
    	Path path = Paths.get(this.getBaseOutputDir(), this.getClass().getPackage().getName().replace(".", "/"));
    	System.out.println("Path to images: " + path.toString());
		
    	final String fileName = this.getClass().getSimpleName() + (forSpec ? example : "");
    	File dir = path.toFile();
		
		File [] files = dir.listFiles(new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
		        return name.startsWith(fileName) && name.endsWith(".jpg");
		    }
		});
		
		return files;
    }
}
