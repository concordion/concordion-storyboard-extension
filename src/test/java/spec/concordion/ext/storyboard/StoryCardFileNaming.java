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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.concordion.api.Element;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.Before;
import org.junit.runner.RunWith;

import test.concordion.FileOutputStreamer;
import test.concordion.ProcessingResult;
import test.concordion.TestRig;
import test.concordion.ext.storyboard.DummyStoryboardFactory;

@RunWith(ConcordionRunner.class)
public class StoryCardFileNaming extends AcceptanceTest {
    
    public static final String SPEC_NAME = "/" + StoryCardFileNaming.class.getName().replace(".java", ".html").replaceAll("\\.","/");
    public static final String TEXT_BEFORE_IMAGE_NAME = "<a href=\"";
    public String acronym;
    
    @Before 
    public void installExtension() {
        System.setProperty("concordion.extensions", DummyStoryboardFactory.class.getName());
        DummyStoryboardFactory.prepareWithScreenShot();
    }
    
    public String render(String fragment, String acronym) throws Exception {
        this.acronym = acronym;
        
        return getTestRig()
        		.processFragment(fragment, SPEC_NAME)	            
        		.getElementXML("storyboard");
    }

    public String renderUsingFixtureNamed(String fragment, String fixtureName) throws Exception {
    	return getTestRig()
	            .processFragment(fragment, fixtureName)
	            .getElementXML("storyboard");
    }
    
    public List<String> getResourceNamesOutput() {
        return getStreamer().getResourcesOutput();
    }
    
    public List<ImageResult> listStoryboardImages(String folder, String fragment) {
        ArrayList<ImageResult> list = new ArrayList<ImageResult>();
        int pos = 0;
        while ((pos=fragment.indexOf(TEXT_BEFORE_IMAGE_NAME, pos)) != -1) {
            ImageResult result = new ImageResult();
            pos = pos + TEXT_BEFORE_IMAGE_NAME.length();
            result.imageName = fragment.substring(pos, fragment.indexOf("\"", pos));
            pos++;
            File file = new File(new File(getBaseOutputDir(), folder), result.imageName);
            System.out.println("looking for " + file.toString());
            result.storedOnDisk = file.exists();
            list.add(result);
        }
        return list;
    }
    
    public static class ImageResult {
        public String imageName;
        public boolean storedOnDisk;
    }
}
