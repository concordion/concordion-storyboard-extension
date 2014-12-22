package spec.concordion.ext.storyboard;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;
import test.concordion.ProcessingResult;
import test.concordion.TestRig;

@RunWith(ConcordionRunner.class)
public class StoryboardCss extends AcceptanceTest {
	ProcessingResult result = null;
	
    public void setSystemProperty(String name, String value) {
        System.setProperty(name, value);
    }
    
    public boolean hasStyle(String style) throws Exception {
    	result = getTestRig().processFragment("");
        
        return result.hasLinkedCSS(getBaseOutputDir(), style);
    }
    
    public String getStyle(String style) throws Exception {
    	return result.getLinkedCSS(getBaseOutputDir(), style);
    }
}
