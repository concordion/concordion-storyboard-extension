package org.concordion.ext.storyboard;

import org.concordion.api.Element;

/**
 * Card that marks the end of a collapsible section in the storyboard.
 * 
 * You wouldn't normally add this card directly but would call storyboard.stopCollapsibleGroup();
 */
public class CollapsibleStopCard extends Card {
	private CardImage cardImage;

	public CollapsibleStopCard() {
		setDescription("This marks the end of the group");
		setCardImage(StockCardImage.COMPLETE);
	}

	/**
	 * Override the default card image of COMPLETE
	 * @param cardImage
	 */
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
