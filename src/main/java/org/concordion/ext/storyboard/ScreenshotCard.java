package org.concordion.ext.storyboard;

import java.awt.Dimension;
import java.io.File;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

import org.concordion.api.Element;
import org.concordion.api.Resource;
import org.concordion.ext.ScreenshotTaker;
import org.concordion.internal.ConcordionBuilder;

/**
 * Card that takes and presents screenshots of the system under test.
 */
public class ScreenshotCard extends Card {
	private String imageName = "";
	private Dimension imageSize;
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

		this.imageName = getFileName(getResource().getName(), getItemIndex(), screenshotTaker.getFileExtension());
		Resource imageResource = getResource().getRelativeResource(imageName);
		this.imagePath = imageResource.getPath();
		
		try {
			// As don't have access to the concordion spec, store the results for later
			OutputStream outputStream = getTarget().getOutputStream(imageResource);
			this.imageSize = screenshotTaker.writeScreenshotTo(outputStream);
			outputStream.close();
		} catch (Exception e) {
			// Do nothing, unable to take screenshot
			//TODO: What logger should I use here?
			System.out.println(e.getMessage());
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
		File file = combine(ConcordionBuilder.getBaseOutputDir().getAbsolutePath(), imagePath);
		if (file.exists()) {
			file.delete();
			
			if(file.exists()) {
				System.out.println("Unable to delete file " + file.getName());
			}
		}
	};
	
	private File combine(String path1, String path2)
	{
	    File file1 = new File(path1);
	    File file2 = new File(file1, path2);
	    
	    return file2;
	}
	
	@Override
	protected void addHTMLToContainer(final Element container) {
		String href = this.imageName + "?version=" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
				
		// Add link to image
		Element anchorImg = new Element("a");
		anchorImg.addAttribute("href", href);
		container.appendChild(anchorImg);

		// Add image to card
		Element img = new Element("img");
		img.setId(this.getDescription());
		
		if (imageSize.width * 1.15 > imageSize.height) {
			img.addStyleClass("sizelandscape");
		} else { 
			img.addStyleClass("sizeportrait");
		}
		
		img.addAttribute("src", href);
		img.addAttribute("width", Integer.toString(this.imageSize.width));
		anchorImg.appendChild(img);

		img.addAttribute("onMouseOver", "showScreenPopup(this);this.style.cursor='pointer'");
		img.addAttribute("onMouseOut", "hideScreenPopup();this.style.cursor='default'");
	}
}
