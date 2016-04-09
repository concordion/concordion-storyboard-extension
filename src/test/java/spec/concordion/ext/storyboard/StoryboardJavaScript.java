package spec.concordion.ext.storyboard;

import org.concordion.api.extension.Extension;
import org.concordion.ext.StoryboardExtension;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.ProcessingResult;

@RunWith(ConcordionRunner.class)
public class StoryboardJavaScript extends AcceptanceTest {
	ProcessingResult result = null;
	
	//TODO This is required to get the images, etc to copy.  Is there any way to avoid this?
	@Extension
    public StoryboardExtension storyboard = new StoryboardExtension().setAddCardOnFailure(false);
	
    public void setSystemProperty(String name, String value) {
        System.setProperty(name, value);
    }
    
    public boolean hasJavaScript(String javaScript) throws Exception {
    	result = getTestRig().processFragment("");
        
        return result.hasLinkedJavaScript(getBaseOutputDir(), javaScript);
    }
    
    public String getJavaScript(String javaScript) throws Exception {    	
    	return result.getLinkedJavaScript(getBaseOutputDir(), javaScript);
    }
}
