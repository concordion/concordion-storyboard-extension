package org.concordion.ext.storyboard;

import org.concordion.api.Element;
import org.concordion.api.Resource;
import org.concordion.api.Target;

/**
 * Base class for all cards that can be added to the storyboard
 */
public abstract class Card extends StoryboardItem {
	private String description = "";
	private String groupMembership = "";

	/**
	 * Write the data to a file
	 */
	protected abstract void captureData();

	/**
	 * Add HTML to the storyboard to display the card details
	 * 
	 * @param storyboard
	 *            The storyboard element on the specification
	 * @param listItem
	 *            The card element
	 */
	protected abstract void addHTMLToContainer(final Element storyboard, final Element listItem);
	
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
			final int cardNumber, final String fileExtension) {
		String prefix = specificationName;

		int lastDot = specificationName.lastIndexOf('.');
		if (lastDot > 0) {
			prefix = specificationName.substring(0, lastDot);
		}

		return String.format("%s%d.%s", prefix, cardNumber, fileExtension);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	protected String getGroupMembership() {
		return groupMembership;
	}

	/**
	 * Set the collapsible section this card belongs to
	 *
	 * @param group
	 *            Name of group
	 */
	protected void setGroupMembership(final String group) {
		this.groupMembership = group;
	}

	/**
	 * Check if this card is a member of a group
	 * 
	 * @return True if is member of a group
	 */
	protected boolean isGroupMember() {
		return groupMembership != null && !groupMembership.isEmpty();
	}

	/**
	 * Override this method to update whether the card should be added to the storyboard or not.
	 * 
	 * @param hasFailure True if the test failed, or if within a section break, the section was marked as failure
	 * @return
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
