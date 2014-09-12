package org.concordion.ext.storyboard;

import org.concordion.api.Element;

public class CollapsibleStopCard extends Card {
	private CardImage cardImage;

	public CollapsibleStopCard() {
		setDescription("This step has completed successfully");
		setCardImage(StockCardImage.COMPLETE);
	}

	protected void setCardImage(final CardImage cardImage) {
		this.cardImage = cardImage;
	}

	@Override
	protected void captureData() {
		// Do nothing
	}

	@Override
	protected void addHTMLToContainer(final Element storyboard, final Element container) {
		String imageName = getResource().getRelativePath(cardImage.getResource());

		Element img = new Element("img");
		img.setId(this.getDescription());
		img.addStyleClass("sizeheight");
		img.addAttribute("src", imageName);

		container.appendChild(img);
	}
}
