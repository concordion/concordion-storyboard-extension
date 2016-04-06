package spec.concordion.ext.storyboard;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.concordion.api.Resource;
import org.concordion.ext.storyboard.StockCardImage;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

import test.concordion.FileOutputStreamer;
import test.concordion.TestRig;

@RunWith(ConcordionRunner.class)
public abstract class AcceptanceTest {
    
	private FileOutputStreamer streamer;
	
	public AcceptanceTest() {
		streamer = new FileOutputStreamer();
	}
	
    protected TestRig getTestRig() throws InstantiationException, IllegalAccessException {
    	TestRig testrig = new TestRig()
	        .withFixture(this.getClass().newInstance())
    	    .withResource(new Resource("/org/concordion/ext/storyboard/storyboard.css"), "")
	        .withResource(new Resource("/org/concordion/ext/storyboard/storyboard.js"), "")
	        .withOutputStreamer(streamer);
    	    
    	for (StockCardImage stockImage : StockCardImage.values()) {
    		testrig.withResource(new Resource("/org/concordion/ext/storyboard" + stockImage.toString()), "");
    	}
    	
    	return testrig;
    }
    
    protected FileOutputStreamer getStreamer() {
		return streamer;
	}
    
    protected String getBaseOutputDir() {
    	return streamer.getBaseOutputDir().getPath();
    }
    
    protected static String prettyFormat(String input, int indent) {
    	if (input.equals("")) return input;
    	
        try {
            Source xmlInput = new StreamSource(new StringReader(input));
            StringWriter stringWriter = new StringWriter();
            StreamResult xmlOutput = new StreamResult(stringWriter);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", indent);
            Transformer transformer = transformerFactory.newTransformer(); 
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(xmlInput, xmlOutput);
            return xmlOutput.getWriter().toString();
        } catch (Exception e) {
            throw new RuntimeException(e); 
        }
    }
}
