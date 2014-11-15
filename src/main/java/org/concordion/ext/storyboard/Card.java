package org.concordion.ext.storyboard;

import org.concordion.api.Element;
import org.concordion.api.Resource;
import org.concordion.api.Target;

public abstract class Card {
	private String title = "";
	private String description = "";
	private CardResult result = CardResult.SUCCESS;
	private String groupMembership = "";
	private StoryboardListener listener;

	protected abstract void captureData();

	protected abstract void addHTMLToContainer(final Element storyboard, final Element listItem);

	protected void setStoryboardListener(final StoryboardListener listener) {
		this.listener = listener;
	}

	protected Resource getResource() {
		return listener.getResource();
	}

	protected Target getTarget() {
		return listener.getTarget();
	}

	protected int getCardNumber() {
		return listener.getCardNumber();
	}

	protected String getFileName(final String specificationName, final int cardNumber, final String fileExtension) {
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

	protected void setGroupMembership(final String group) {
		this.groupMembership = group;
	}

	protected boolean isGroupMember() {
		return groupMembership != null && !groupMembership.isEmpty();
	}
}
