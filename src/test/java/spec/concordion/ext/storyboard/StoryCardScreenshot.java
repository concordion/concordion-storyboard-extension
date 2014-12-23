/*
 * Copyright (c) 2010 Two Ten Consulting Limited, New Zealand 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package spec.concordion.ext.storyboard;

import org.concordion.api.extension.Extension;
import org.concordion.ext.StoryboardExtension;
import org.concordion.ext.storyboard.CardResult;
import org.concordion.ext.storyboard.ScreenshotCard;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.Before;
import org.junit.runner.RunWith;

import test.concordion.ext.storyboard.DummyScreenshotTaker;
import test.concordion.ext.storyboard.DummyStoryboardFactory;

@RunWith(ConcordionRunner.class)
public class StoryCardScreenshot extends AcceptanceTest {
    
    public static final String SPEC_NAME = "/" + StoryCardScreenshot.class.getName().replaceAll("\\.","/");
    
    @Extension
    public StoryboardExtension storyboard = new StoryboardExtension();
    
    public StoryCardScreenshot() {
    	storyboard.setTakeScreenshotOnCompletion(false);
    	storyboard.setScreenshotTaker(new DummyScreenshotTaker());
    }
    
    @Before 
    public void installExtension() {
        System.setProperty("concordion.extensions", DummyStoryboardFactory.class.getName());
    }
    
    public String render(String fragment, String acronym) throws Exception {
        return getTestRig()
        		.processFragment(fragment, SPEC_NAME)	            
        		.getElementXML("storyboard");
    }

    public void addScreenshot() {
    	DummyStoryboardFactory.storyboard.addScreenshot("Screenshot Example", "This is a screenshot");
    }
        
    public boolean screenshotCardAdded(String fragment) {
    	addScreenshotToSpec(fragment);
    	
    	return fragment.contains("This is a screenshot");    	
    }
    
    public boolean failureCardAdded(String fragment) {
    	addScreenshotToSpec(fragment);
    	
    	return fragment.contains("Test Failed");
    }
    
    public boolean exceptionCardAdded(String fragment) {
    	addScreenshotToSpec(fragment);
    	
    	return fragment.contains("Exception");
    }
    
    private void addScreenshotToSpec(String fragment) {
    	// This is so the specification will echo what the test was doing
    	ScreenshotCard card = new ScreenshotCard();
    	card.setTitle(extractText(fragment, "scsummary"));
    	card.setDescription(extractText(fragment, "scdescription"));
    	if(fragment.contains("scfailure")) {
    		card.setResult(CardResult.FAILURE);
    	} else {
    		card.setResult(CardResult.SUCCESS);
    	}
    	    	
    	storyboard.addCard(card);
    }
    
    private String extractText(String fragment, String className) {
    	int start = fragment.indexOf(className);
    	int end = 0;
    	
    	if (start < 1) return "";
    	
    	start = fragment.indexOf(">", start) + 1;
    	end = fragment.indexOf("<", start);
    	
    	return fragment.substring(start, end);
    }
}
