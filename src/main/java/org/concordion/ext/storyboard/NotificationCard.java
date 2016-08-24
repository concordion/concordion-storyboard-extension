package org.concordion.ext.storyboard;

import java.io.OutputStream;

import org.concordion.api.Element;
import org.concordion.api.Resource;

/**
 * Generic card that can optionally provide data to the user.  This data can be text based data such as: xml, json, html, etc.
 */
public class NotificationCard extends Card {
	private String dataFileName = null;
	private CardImage cardImage = StockCardImage.XML_REQUEST;
	private String data = "";
	private String fileExtension = "txt";

	/**
	 * S Override the default card image of XML_REQUEST
	 * @param cardImage A card image - can be StockCardImage or a custom image
	 */
	public void setCardImage(final CardImage cardImage) {
		this.cardImage = cardImage;
	}

	/**
	 * Set the data that you wish the card to display
	 * 
	 * @param data Data
	 */
	public void setData(final String data) {
		if (data == null) {
			this.data = "";
		} else {
			this.data = data;
		}
	}

	@Override
	protected void captureData() {
		if (dataFileName != null) {
			return;
		}

		if (data != null && !data.isEmpty()) {
			dataFileName = getFileName(getResource().getName(), getItemIndex(), fileExtension);
			Resource xmlResource = getResource().getRelativeResource(dataFileName);

			try {
				// As don't have access to the concordion spec, store the results for later
				OutputStream outputStream = getTarget().getOutputStream(xmlResource);
				outputStream.write(data.getBytes());
				this.dataFileName = xmlResource.getName();
			} catch (Exception e) {
				// Unable to write file
				this.dataFileName = "";
			}

			data = "";
		}
	}

	@Override
	protected void addHTMLToContainer(final Element container) {
		String imageName = getResource().getRelativePath(cardImage.getResource());

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

	/**
	 * Set the file type for the data.  This helps with formatting/display.  Defaults to 'txt'.
	 * 
	 * @param fileExtension File extension
	 */
	public void setFileExtension(final String fileExtension) {
		if (fileExtension == null || fileExtension.isEmpty()) {
			return;
		}

		if (fileExtension.startsWith(".")) {
			this.fileExtension = fileExtension.substring(1);
		} else {
			this.fileExtension = fileExtension;
		}
	}

	/**
	 * Use an existing file rather than have this class attempt to create one.
	 * 
	 * @param file Relative path to the file
	 */
	public void setFilePath(String file) {
		this.dataFileName = file;
	}
}
