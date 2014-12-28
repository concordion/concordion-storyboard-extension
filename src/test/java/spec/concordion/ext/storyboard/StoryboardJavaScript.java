package spec.concordion.ext.storyboard;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;
import test.concordion.ProcessingResult;
import test.concordion.TestRig;

@RunWith(ConcordionRunner.class)
public class StoryboardJavaScript extends AcceptanceTest {
	ProcessingResult result = null;
	
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
