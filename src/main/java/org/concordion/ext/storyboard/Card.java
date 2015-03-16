package org.concordion.ext.storyboard;

import org.concordion.api.Element;
import org.concordion.api.Resource;
import org.concordion.api.Target;

/**
 * Base class for all cards that can be added to the storyboard
 */
public abstract class Card {
	private String title = "";
	private String description = "";
	private CardResult result = CardResult.SUCCESS;
	private String groupMembership = "";
	private StoryboardListener listener;

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
	protected abstract void addHTMLToContainer(final Element storyboard,
			final Element listItem);

	protected void setStoryboardListener(final StoryboardListener listener) {
		this.listener = listener;
	}

	protected Resource getResource() {
		return listener.getResource();
	}

	protected Target getTarget() {
		return listener.getTarget();
	}

	/**
	 * @return The unique number of this card on the storyboard
	 */
	protected int getCardNumber() {
		return listener.getCardNumber();
	}

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

	public String getTitle() {
		return title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public CardResult getResult() {
		return result;
	}

	public void setResult(final CardResult result) {
		this.result = result;
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
}
