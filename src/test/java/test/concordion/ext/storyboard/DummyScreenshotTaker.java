package test.concordion.ext.storyboard;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.concordion.ext.ScreenshotTaker;

/**
 * Returns the contents of a static file, so that screenshots of my system don't inadvertently make it onto the net :-)
 */
public final class DummyScreenshotTaker implements ScreenshotTaker {
    private static final String IMAGE_PATH = "image/details#.jpg";
    private int index = 1;
    
    public DummyScreenshotTaker() {
    }
    public DummyScreenshotTaker(int startIndex) {
    	index = startIndex;
    }
    
    private BufferedImage getImage() {
    	
        try {
            InputStream imageStream = DummyScreenshotTaker.class.getClassLoader().getResourceAsStream(IMAGE_PATH.replace("#", String.valueOf(index)));
            if (imageStream == null) {
                throw new RuntimeException(String.format("Unable to find IMAGE '%s' on classpath", IMAGE_PATH));
            }
            
            index ++;
            if (index > 2) {
            	index = 1;
            }
            return ImageIO.read(imageStream);
        } catch (IOException e) {
            throw new RuntimeException("Unable to create DummyScreenshotFactory", e);
        }
    }
    
    @Override
    public Dimension writeScreenshotTo(OutputStream outputStream) throws IOException {
    	BufferedImage image = getImage();
    	
        ImageIO.write(image, getFileExtension(), outputStream);
        return new Dimension(image.getWidth(), image.getHeight());
    }

    @Override
    public String getFileExtension() {
        return "jpg";
    }
}