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
import org.concordion.ext.storyboard.NotificationCard;
import org.concordion.ext.storyboard.ScreenshotCard;
import org.concordion.ext.storyboard.StockCardImage;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.Before;
import org.junit.runner.RunWith;

import test.concordion.ProcessingResult;
import test.concordion.ext.storyboard.DummyScreenshotTaker;
import test.concordion.ext.storyboard.DummyStoryboardFactory;

@RunWith(ConcordionRunner.class)
public class StoryCardScreenshot extends AcceptanceTest {
    
    public static final String SPEC_NAME = "/" + StoryCardScreenshot.class.getName().replaceAll("\\.","/");
    private int example = 0;
    
    @Extension
    public StoryboardExtension storyboard = new StoryboardExtension();
    
    public StoryCardScreenshot() {
    	storyboard.setTakeScreenshotOnCompletion(false);
    	storyboard.setScreenshotTaker(new DummyScreenshotTaker());
    }
    
    @Before 
    public void installExtension() {
        System.setProperty("concordion.extensions", DummyStoryboardFactory.class.getName());
        DummyStoryboardFactory.prepareWithScreenShot();
    }
    
    public String render(String fragment, String acronym) throws Exception {    	
    	ProcessingResult result = getTestRig().processFragment(fragment, SPEC_NAME + ++example);    	
    	
    	NotificationCard card = new NotificationCard();    	
    	card.setTitle("Example " + example);
    	
    	switch (example) {
    	case 1:
    		card.setDescription("Add Screenshot");
			break;
    	case 2:
    		card.setDescription("Capture Failure");
			break;
    	case 3:
    		card.setDescription("Capture Exception");
			break;
    	case 4:
    		card.setDescription("Ignore Failure");
			break;			
    	}
	    
    	card.setDescription(card.getDescription() + ": click image to see example");
    	
	    //TODO Not sure what going on with this html but it doesn't like this script definition in short form
    	card.setData(prettyFormat(result.getXOMDocument().toXML().replace("storyboard.js\" />", "storyboard.js\"></script>"), 4));
    	card.setFileExtension("html");
    	card.setCardImage(StockCardImage.HTML);    	
    	card.setResult(CardResult.SUCCESS);
    	
    	storyboard.addCard(card);
    	
        return result.getElementXML("storyboard");
    }
    
    public String renderWithFailureOff(String fragment, String acronym) throws Exception {
    	DummyStoryboardFactory.setAddCardOnFailure(false);
    	
    	String result = render(fragment, acronym);
    	
    	DummyStoryboardFactory.setAddCardOnFailure(true);
    	
    	return result;
    }
    
    public void addScreenshot() {
    	DummyStoryboardFactory.getStoryboard().addScreenshot("Screenshot Example", "This is a screenshot");
    }
        
    public boolean screenshotCardAdded(String fragment) {
    	return fragment.contains("This is a screenshot");    	
    }
    
    public boolean failureCardAdded(String fragment) {
    	return fragment.contains("Test Failed");
    }
    
    public boolean exceptionCardAdded(String fragment) {
    	return fragment.contains("Exception");
    }
    
    public boolean noCardAdded(String fragment) {
    	return "".equals(fragment);
    }
}
