package org.concordion.ext.storyboard;

import java.io.OutputStream;

import org.concordion.api.Element;
import org.concordion.api.Resource;
import org.concordion.ext.ScreenshotTaker;

public class ScreenshotCard extends Card {
	private static final int MAX_WIDTH = 700;
	private String imageName = "";
	private int imageWidth = MAX_WIDTH;
	private ScreenshotTaker screenshotTaker = null;

	protected void setScreenshotTaker(final ScreenshotTaker screenshotTaker) {
		this.screenshotTaker = screenshotTaker;
	}

	@Override
	protected void captureData() {
		if (screenshotTaker == null) {
			return;
		}

		imageName = getFileName(getResource().getName(), getCardNumber(), screenshotTaker.getFileExtension());
		Resource imageResource = getResource().getRelativeResource(imageName);

		try {
			// As don't have access to the concordion spec, store the results for later
			OutputStream outputStream = getTarget().getOutputStream(imageResource);
			this.imageWidth = screenshotTaker.writeScreenshotTo(outputStream);
			this.imageName = imageResource.getName();
		} catch (Exception e) {
			// Do nothing, unable to take screenshot
		}

		screenshotTaker = null;
	}

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

		// Add mouseover popup image
		Element popupImg = new Element("img");
		popupImg.setId(imageName);
		popupImg.addStyleClass("screenshot");
		popupImg.addAttribute("src", imageName);
		popupImg.addAttribute("width", Integer.toString(Math.min(MAX_WIDTH, imageWidth)));

		img.addAttribute("onMouseOver", "showScreenPopup(this, '" + this.imageName + "');this.style.cursor='pointer'");
		img.addAttribute("onMouseOut", "hideScreenPopup('" + this.imageName + "');this.style.cursor='default'");

		storyboard.appendChild(popupImg);
	}
}
