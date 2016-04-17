package org.concordion.ext.storyboard;

import org.concordion.api.Element;

/**
 * Base class for all cards that can be added to the storyboard
 */
public abstract class Card extends StoryboardItem {
	private String description = "";

	/**
	 * Write the data to a file
	 */
	protected abstract void captureData();

	/**
	 * Add HTML to the storyboard to display the card details
	 * 
	 * @param listItem
	 *            The card element
	 */
	protected abstract void addHTMLToContainer(final Element listItem);
	
	/**
	 * Gets a unique file name for data that is to be stored alongside a card
	 * 
	 * @param specificationName
	 *            The name of the specification
	 * @param cardNumber
	 *            Index of the card in the list
	 * @param fileExtension
	 *            Extension of the file
	 * @return A unique filename
	 */
	protected String getFileName(final String specificationName,
			final String cardNumber, final String fileExtension) {
		String prefix = specificationName;

		int lastDot = specificationName.lastIndexOf('.');
		if (lastDot > 0) {
			prefix = specificationName.substring(0, lastDot);
		}

		return String.format("%s%s.%s", prefix, cardNumber, fileExtension);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * Override this method to update whether the card should be added to the storyboard or not.
	 * 
	 * @param hasFailure True if the test failed, or if within a section break, the section was marked as failure
	 * @return True to append card to storyboard, false to ignore card
	 */
	protected boolean shouldAppend(boolean hasFailure) {
		return true;
	}

	/** 
	 * Override this method to clean up data belonging to this card - likely to be used in 
	 * conjunction with the shouldAppend() method
	 */
	protected void cleanupData() {
		
	}
}
