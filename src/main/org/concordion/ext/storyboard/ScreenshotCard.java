package org.concordion.ext.storyboard;

import java.io.OutputStream;

import org.concordion.api.Element;
import org.concordion.api.Resource;
import org.concordion.api.Target;
import org.concordion.ext.ScreenshotTaker;

public class ScreenshotCard extends Card {
	private ScreenshotTaker screenshotTaker;
	private String imageName;
	private int imageWidth;
	private static final int MAX_WIDTH = 700;

	public void captureScreenshot(final Resource resource, final Target target, final ScreenshotTaker screenshotTaker, final int nextCardNumber) {
		this.screenshotTaker = screenshotTaker;

		imageName = getNextFileName(resource.getName(), nextCardNumber);
		Resource imageResource = resource.getRelativeResource(imageName);

		try {
			// As don't have access to the concordion spec, store the results for later
			OutputStream outputStream = target.getOutputStream(imageResource);
			this.imageWidth = screenshotTaker.writeScreenshotTo(outputStream);
			this.imageName = imageResource.getName();
		} catch (Exception e) {
			// Unable to take screenshot
			this.imageName = "";
			this.imageWidth = 0;
		}
	}

	@Override
	public void addHTMLToContainer(final Element storyboard, final Element container) {
		Element anchorImg = new Element("a");
		anchorImg.addAttribute("href", this.imageName);
		container.appendChild(anchorImg);

		Element img = new Element("img");
		img.setId(this.getDescription());
		img.addStyleClass("sizewidth");
		img.addAttribute("src", this.imageName);
		img.addAttribute("width", Integer.toString(this.imageWidth));

		anchorImg.appendChild(img);

		addImagePopupToElement(storyboard, img, this.imageName, this.imageWidth);
	}

	private void addImagePopupToElement(final Element showOver, final Element hoverOver, final String imageName, final int imageWidth) {
		Element img = new Element("img");
		img.setId(imageName);
		img.addAttribute("src", imageName);
		img.addAttribute("width", Integer.toString(Math.min(MAX_WIDTH, imageWidth)));

		img.addStyleClass("screenshot");
		Element hoverElement = hoverOver;

		hoverElement.addAttribute("onMouseOver", "showScreenPopup(this, '" + imageName + "');this.style.cursor='pointer'");
		hoverElement.addAttribute("onMouseOut", "hideScreenPopup('" + imageName + "');this.style.cursor='default'");

		showOver.appendChild(img);
	}

	@Override
	protected String getFileExtension() {
		return screenshotTaker.getFileExtension();
	}
}
