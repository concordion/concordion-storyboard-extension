package spec.concordion.ext.storyboard;

import java.nio.file.Paths;

import org.concordion.api.Resource;
import org.concordion.api.extension.Extension;
import org.concordion.api.extension.Extensions;
import org.concordion.ext.EmbedExtension;
import org.concordion.ext.StoryboardExtension;
import org.concordion.ext.storyboard.StockCardImage;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.ProcessingResult;
import test.concordion.TestRig;

@RunWith(ConcordionRunner.class)
@Extensions(EmbedExtension.class)
public class StoryboardImages extends AcceptanceTest {
	public static final String SPEC_NAME = "/" + StoryboardImages.class.getName().replaceAll("\\.","/");
	TestRig rig = null;
	ProcessingResult result = null;
	
	//TODO This is required to get the images, etc to copy.  Is there any way to avoid this?
	@Extension
    public StoryboardExtension storyboard = new StoryboardExtension().setScreenshotTaker(null).setAddCardOnFailure(false);
	
    public void setSystemProperty(String name, String value) {
        System.setProperty(name, value);
    }
    
    public void render() throws Exception {    	
    	rig = getTestRig();
    	result = rig.processFragment("hello");
    }
    
    public boolean hasImage(String fileName) throws Exception {
    	return rig.hasCopiedResource(StockCardImage.COMPLETE.getResource()) && Paths.get(getBaseOutputDir(), fileName).toFile().exists();
    }
    
    public String getBaseFolder() {
    	return getBaseOutputDir();
    }
    public String getImages() {
    	Resource resource = new Resource("/" + this.getClass().getName().replace(".", "/"));
    	StringBuilder images = new StringBuilder();
    	    	
    	images.append("<ul style=\"list-style-type: none;\">").append("\n");
    	
    	for (StockCardImage stockImage : StockCardImage.values()) {
    		images.append("<li style=\"display: inline-block; padding-right: 20px\">").append("\n");
    		images.append("<img style=\"box-shadow: 0.2em 0.2em 0.5em #333333; margin-bottom: 10px;\" src=\"" + resource.getRelativePath(stockImage.getResource()) + "\"></img><br/>" + stockImage.name()).append("\n");
    		images.append("</li>").append("\n");
		}
    	
    	images.append("</ul>").append("\n");
    	
    	return images.toString();
    }
}
