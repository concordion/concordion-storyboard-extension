package spec.concordion.ext.storyboard;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.concordion.api.ConcordionScoped;
import org.concordion.api.Scope;
import org.concordion.api.ScopedObjectHolder;
import org.concordion.api.extension.Extension;
import org.concordion.ext.StoryboardExtension;
import org.concordion.ext.storyboard.CardResult;
import org.concordion.ext.storyboard.NotificationCard;
import org.concordion.ext.storyboard.StockCardImage;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.ProcessingResult;
import test.concordion.ext.storyboard.ExampleFixture;

@RunWith(ConcordionRunner.class)
public class RemoveUnwantedScreenshots extends AcceptanceTest {
    
    public static final String SPEC_NAME = "/" + RemoveUnwantedScreenshots.class.getName().replaceAll("\\.","/");
    
    @ConcordionScoped(Scope.SPECIFICATION)
    private ScopedObjectHolder<Counter> example = new ScopedObjectHolder<Counter>() {
        @Override
        protected Counter create() {
            return new Counter();
        }
    };
    
    private class Counter {
    	int count;
    	
    	public int value() {
    		return count;
    	}
    	
    	public void increment() {
    		count++;
    	}
    }
    
    private ExampleFixture fixture;
    
    @Extension
    public StoryboardExtension storyboard = new StoryboardExtension();

    public String render(String fragment) throws Exception {
    	example.get().increment();

    	fixture = new ExampleFixture();
    	fixture.getStoryboard().setTakeScreenshotOnExampleCompletion(false);
    			
    	ProcessingResult result = getTestRig()
    			.withFixture(fixture)
    			.processFragment(fragment, SPEC_NAME + example.get().value());    	

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
		
    	final String fileName = this.getClass().getSimpleName() + (forSpec ? example.get().value() : "");
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
