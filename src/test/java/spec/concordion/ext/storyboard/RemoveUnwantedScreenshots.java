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
    public StoryboardExtension storyboard = new StoryboardExtension().setAddCardOnFailure(false);
    
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
    
    public String render(String fragment) throws Exception {
    	example++;
    	
    	rig = getTestRig();
    	ProcessingResult result = rig.processFragment(fragment, SPEC_NAME + example);    	

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
    
    public void addScreenshots() {
    	StoryboardExtension dummyboard = DummyStoryboardFactory.getStoryboard();
    	
    	dummyboard.addScreenshot("Screen 1", "Screenshot 1");
    	dummyboard.addScreenshot("Screen 2", "Screenshot 2");
    	dummyboard.markPriorScreenshotsForRemoval();
    	dummyboard.addScreenshot("Screen 3", "Screenshot 3");
    }
 
    public void addFailedScreenshots() {
    	StoryboardExtension dummyboard = DummyStoryboardFactory.getStoryboard();
    	
    	dummyboard.addScreenshot("Screen 1", "Screenshot 1");
    	dummyboard.addScreenshot("Screen 2", "Screenshot 2");
    	dummyboard.markPriorScreenshotsForRemoval();
    	
    	throw new RuntimeException("Random Error");
    }
    
    public void addScreenshotsInSectionBreak() {
    	StoryboardExtension dummyboard = DummyStoryboardFactory.getStoryboard();
    	    	
    	dummyboard.addSectionContainer("Example 1");
    	dummyboard.addScreenshot("Screen 1", "Screenshot 1");
    	dummyboard.addScreenshot("Screen 2", "Screenshot 2");
    	dummyboard.markPriorScreenshotsForRemoval();
    	dummyboard.addScreenshot("Screen 3", "Screenshot 3");
    	dummyboard.closeContainer();
    	
    	dummyboard.addSectionContainer("Example 2");
    	dummyboard.addScreenshot("Screen 1", "Screenshot 1");
    	dummyboard.addScreenshot("Screen 2", "Screenshot 2");
    	dummyboard.markPriorScreenshotsForRemoval();
    	ScreenshotCard card = new ScreenshotCard();
    	card.setTitle("Screen 3");
    	card.setDescription("Screenshot 3");
    	card.setResult(CardResult.FAILURE);
    	dummyboard.addCard(card);
    	dummyboard.closeContainer();
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
    	Path path = Paths.get(this.getBaseOutputDir(), this.getClass().getPackage().getName().replace(".", "/"));
		
    	final String fileName = this.getClass().getSimpleName() + (forSpec ? example : "");
    	File dir = path.toFile();
		
		File [] files = dir.listFiles(new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
		        return name.startsWith(fileName) && name.endsWith(".jpg");
		    }
		});
		
		if (files == null) {
			files = new File[0];
		}
		
		return files;
    }
}
