package spec.concordion.ext.storyboard;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

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
    
    @After
    public void clearConcordionExtensionsSystemProperty() {
        System.clearProperty("concordion.extensions");
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
            pos = pos + TEXT_BEFORE_IMAGE_NAME.length();
            String imageName = fragment.substring(pos, fragment.indexOf("\"", pos));
            pos++;
            
            int index = imageName.indexOf("?");
            if (index > 0) {
            	imageName = imageName.substring(0, index);
            }
            		
            File file = new File(new File(getBaseOutputDir(), folder), imageName);
            System.out.println("looking for " + file.toString());
            
            ImageResult result = new ImageResult();
            result.imageName = imageName;
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
