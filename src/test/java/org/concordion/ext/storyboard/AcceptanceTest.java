package org.concordion.ext.storyboard;

import org.concordion.api.Resource;
import org.concordion.api.extension.ConcordionExtension;
import org.concordion.api.extension.ConcordionExtensionFactory;
import org.concordion.ext.ScreenshotExtension;
import org.concordion.ext.StoryboardExtension;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.FileOutputStreamer;
import test.concordion.TestRig;

@RunWith(ConcordionRunner.class)
public class AcceptanceTest {
    
    protected TestRig getTestRig() {
    	 return new TestRig()
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
    }
}
