package org.concordion.ext.storyboard;

import java.io.OutputStream;

import org.concordion.api.Element;
import org.concordion.api.Resource;
import org.concordion.api.Target;

public class NotificationCard extends Card {
	private String imageName;
	private String dataFileName = "";

	public void captureData(final Resource resource, final Target target, final String data, final CardImage image, final int nextCardNumber) {
		imageName = resource.getRelativePath(image.getResource());

		if (data != null && !data.isEmpty()) {
			dataFileName = getNextFileName(resource.getName(), nextCardNumber);
			Resource xmlResource = resource.getRelativeResource(dataFileName);

			try {
				// As don't have access to the concordion spec, store the results for later
				OutputStream outputStream = target.getOutputStream(xmlResource);
				outputStream.write(data.getBytes());
				this.dataFileName = xmlResource.getName();
			} catch (Exception e) {
				// Unable to write file
				this.dataFileName = "";
			}
		}
	}

	public void captureData(final Resource resource, final CardImage image) {
		imageName = resource.getRelativePath(image.getResource());
	}

	@Override
	public void addHTMLToContainer(final Element storyboard, final Element container) {
		Element img = new Element("img");
		img.setId(this.getDescription());
		img.addStyleClass("sizeheight");
		img.addAttribute("src", imageName);

		if (dataFileName.isEmpty()) {
			container.appendChild(img);
		} else {
			Element anchorImg = new Element("a");
			anchorImg.addAttribute("href", dataFileName);
			container.appendChild(anchorImg);

			anchorImg.appendChild(img);
		}
	}

	@Override
	protected String getFileExtension() {
		return "txt";
	}
}
