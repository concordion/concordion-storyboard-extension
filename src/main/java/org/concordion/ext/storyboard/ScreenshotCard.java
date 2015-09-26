package org.concordion.ext.storyboard;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.concordion.api.Element;
import org.concordion.api.Resource;
import org.concordion.ext.ScreenshotTaker;
import org.concordion.internal.ConcordionBuilder;

/**
 * Card that takes and presents screenshots of the system under test.
 */
public class ScreenshotCard extends Card {
	private static final int MAX_WIDTH = 700;
	private String imageName = "";
	private int imageWidth = MAX_WIDTH;
	private ScreenshotTaker screenshotTaker = null;
	private boolean deleteIfSuccessful = false;
	private String imagePath;
	
	protected void setScreenshotTaker(final ScreenshotTaker screenshotTaker) {
		this.screenshotTaker = screenshotTaker;
	}

	public boolean isDeleteIfSuccessful() {
		return deleteIfSuccessful;
	}

	public void setDeleteIfSuccessful(boolean deleteIfSuccessful) {
		this.deleteIfSuccessful = deleteIfSuccessful;
	}
	
	@Override
	protected void captureData() {
		if (screenshotTaker == null) {
			return;
		}

		this.imageName = getFileName(getResource().getName(), getNextCardIndex(), screenshotTaker.getFileExtension());
		Resource imageResource = getResource().getRelativeResource(imageName);
		this.imagePath = imageResource.getPath();
		
		try {
			// As don't have access to the concordion spec, store the results for later
			OutputStream outputStream = getTarget().getOutputStream(imageResource);
			this.imageWidth = screenshotTaker.writeScreenshotTo(outputStream);
			outputStream.close();
		} catch (Exception e) {
			// Do nothing, unable to take screenshot
		}

		screenshotTaker = null;
	}
	
	@Override
	protected boolean shouldAppend(boolean hasFailure) {
		if (deleteIfSuccessful) return hasFailure;
		
		return true;
	};
	
	@Override 
	protected void cleanupData() {
		Path path = Paths.get(ConcordionBuilder.getBaseOutputDir().getAbsolutePath(), imagePath);
		
		File file = path.toFile();
		if (file.exists()) {
			file.delete();
			
			if(file.exists()) {
				System.out.println("Unable to delete file " + file.getName());
			}
		}
	};
	
	@Override
	protected void addHTMLToContainer(final Element storyboard, final Element container) {
		// Add link to image
		Element anchorImg = new Element("a");
		anchorImg.addAttribute("href", this.imageName);
		container.appendChild(anchorImg);

		// Add image to card
		Element img = new Element("img");
		img.setId(this.getDescription());
		img.addStyleClass("sizewidth");
		img.addAttribute("src", this.imageName);
		img.addAttribute("width", Integer.toString(this.imageWidth));
		anchorImg.appendChild(img);

		img.addAttribute("onMouseOver", "showScreenPopup(this);this.style.cursor='pointer'");
		img.addAttribute("onMouseOut", "hideScreenPopup();this.style.cursor='default'");
	}
}
