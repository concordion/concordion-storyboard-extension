package org.concordion.ext.storyboard;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;
import test.concordion.ProcessingResult;
import test.concordion.TestRig;

@RunWith(ConcordionRunner.class)
public class StoryboardCss extends AcceptanceTest {
	TestRig rig = null;
	ProcessingResult result = null;
	
    public void setSystemProperty(String name, String value) {
        System.setProperty(name, value);
    }
    
    public boolean hasStyle(String style) throws Exception {
    	rig = getTestRig();
    	result = rig.processFragment("");
        
        return result.hasLinkedCSS(rig.getBaseOutputDir(), style);
    }
    
    public String getStyle(String style) throws Exception {
    	return result.getLinkedCSS(rig.getBaseOutputDir(), style);
    }
}
