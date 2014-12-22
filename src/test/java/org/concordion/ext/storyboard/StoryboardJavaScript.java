package org.concordion.ext.storyboard;

import org.concordion.api.Resource;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.FileOutputStreamer;
import test.concordion.ProcessingResult;
import test.concordion.TestRig;

@RunWith(ConcordionRunner.class)
public class StoryboardJavaScript {
 	TestRig rig = null;
	ProcessingResult result = null;
	
    public void setSystemProperty(String name, String value) {
        System.setProperty(name, value);
    }
    
    public boolean hasJavaScript(String javaScript) throws Exception {
    	rig = new TestRig()
	        .withFixture(this)
	        .withResource(new Resource("/org/concordion/ext/storyboard/storyboard.css"), "")
	        .withResource(new Resource("/org/concordion/ext/storyboard/storyboard.js"), "")
	        .withResource(new Resource("/org/concordion/ext/storyboard/xmlrequest.png"), "")
	        .withResource(new Resource("/org/concordion/ext/storyboard/xmlresponse.png"), "")
	        .withResource(new Resource("/org/concordion/ext/storyboard/expand.png"), "")
	        .withResource(new Resource("/org/concordion/ext/storyboard/collapse.png"), "")
	        .withResource(new Resource("/org/concordion/ext/storyboard/email.png"), "")
	        .withResource(new Resource("/org/concordion/ext/storyboard/complete.png"), "")
	        .withResource(new Resource("/org/concordion/ext/storyboard/error.png"), "")
	        .withOutputStreamer(new FileOutputStreamer());
    	
    	result = rig.processFragment("");
        
        return result.hasLinkedJavaScript(rig.getBaseOutputDir(), javaScript);
    }
    
    public String getJavaScript(String javaScript) throws Exception {
    	return result.getLinkedJavaScript(rig.getBaseOutputDir(), javaScript);
    }
}
