package org.concordion.ext.storyboard;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;
import test.concordion.ProcessingResult;
import test.concordion.TestRig;

@RunWith(ConcordionRunner.class)
public class StoryboardJavaScript extends AcceptanceTest {
 	TestRig rig = null;
	ProcessingResult result = null;
	
    public void setSystemProperty(String name, String value) {
        System.setProperty(name, value);
    }
    
    public boolean hasJavaScript(String javaScript) throws Exception {
    	rig = getTestRig();    	
    	result = rig.processFragment("");
        
        return result.hasLinkedJavaScript(rig.getBaseOutputDir(), javaScript);
    }
    
    public String getJavaScript(String javaScript) throws Exception {
    	return result.getLinkedJavaScript(rig.getBaseOutputDir(), javaScript);
    }
}
