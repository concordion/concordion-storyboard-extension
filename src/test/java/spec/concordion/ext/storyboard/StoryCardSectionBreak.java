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
import org.concordion.ext.storyboard.StockCardImage;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.Before;
import org.junit.runner.RunWith;

import test.concordion.ProcessingResult;
import test.concordion.ext.storyboard.DummyStoryboardFactory;

@RunWith(ConcordionRunner.class)
public class StoryCardSectionBreak extends AcceptanceTest {
    
    public static final String SPEC_NAME = "/" + StoryCardSectionBreak.class.getName().replaceAll("\\.","/");
    private int example = 0;
    
    @Extension
    public StoryboardExtension storyboard = new StoryboardExtension().setScreenshotTaker(null);
    
    @Before 
    public void installExtension() {
        System.setProperty("concordion.extensions", DummyStoryboardFactory.class.getName());
        DummyStoryboardFactory.prepareWithoutScreenShot();
    }
    
    public String render(String fragment, String acronym) throws Exception {    	
    	ProcessingResult result = getTestRig().processFragment(fragment, SPEC_NAME + ++example);    	
    	String title = "";
    	
    	switch (example) {
    	case 1:
    		title = "Add Section Break";
			break;
    	case 2:
    		title = "Stop Section Break";
			break;
    	case 3:
    		title = "Add Failure Card to Section";
			break;
    	}
    	
    	NotificationCard card = new NotificationCard();    	
    	card.setTitle("Example " + example + ": " + title);	    
    	card.setDescription("Click image to see example");
    	
	    //TODO Not sure what going on with this html but it doesn't like this script definition in short form
    	card.setData(prettyFormat(result.getXOMDocument().toXML().replace("storyboard.js\" />", "storyboard.js\"></script>"), 4));
    	card.setFileExtension("html");
    	card.setCardImage(StockCardImage.HTML);    	
    	card.setResult(CardResult.SUCCESS);
    	
    	storyboard.addCard(card);
    	
        return result.getElementXML("storyboard");
    }
    
    public void addSectionBreak(String data) {    	
    	DummyStoryboardFactory.getStoryboard().addSectionBreak(data);
    	DummyStoryboardFactory.getStoryboard().addNotification(data + " Section Member", "Example", "", StockCardImage.TEXT, CardResult.SUCCESS);
    }
 
    public void addSectionBreak() {
    	DummyStoryboardFactory.getStoryboard().addSectionBreak("Example");
    	DummyStoryboardFactory.getStoryboard().addNotification("Example Section Member", "Example", "", StockCardImage.TEXT, CardResult.SUCCESS);
    	
    	DummyStoryboardFactory.getStoryboard().addSectionBreak("");
    	DummyStoryboardFactory.getStoryboard().addNotification("Storyboard Member", "Example", "", StockCardImage.TEXT, CardResult.SUCCESS);
    }
    
    public void addFailureToSection(String data) {
    	DummyStoryboardFactory.getStoryboard().addSectionBreak(data);
    	DummyStoryboardFactory.getStoryboard().addNotification(data + " Section Member", "Example", "", StockCardImage.TEXT, CardResult.FAILURE);
    }
    
        
    public boolean sectionAddedCollapsed(String fragment) {
    	return fragment.contains("class=\"toggle-box scsuccess\">Example</label>");    	
    }
    
    public boolean sectionAddedExpanded(String fragment) {
    	return fragment.contains("class=\"toggle-box scsuccess\">Setup</label>");    	
    }
    
    public boolean sectionFailed(String fragment) {
    	return fragment.contains("class=\"toggle-box scfailure\"");    	
    }
    
}
