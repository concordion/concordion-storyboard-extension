package org.concordion.ext.storyboard;

import org.concordion.api.Element;

public abstract class Card {
	private String summary;
	private String description;
	private CardResult result;
	private String groupMembership;

	protected String getNextFileName(final String specName, final int nextCardNumber) {
		String prefix = getPrefix(specName);
		String fileExtension = getFileExtension();
		return String.format("%s%d.%s", prefix, nextCardNumber, fileExtension);
	}

	private String getPrefix(final String specName) {
		int lastDot = specName.lastIndexOf('.');
		if (lastDot == -1) {
			return specName;
		}
		return specName.substring(0, lastDot);
	}

	protected abstract String getFileExtension();

	public abstract void addHTMLToContainer(final Element storyboard, final Element listItem);

	public String getSummary() {
		return summary;
	}

	public void setSummary(final String summary) {
		this.summary = summary;
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

	public String getGroupMembership() {
		return groupMembership;
	}

	public void setGroupMembership(final String group) {
		this.groupMembership = group;
	}

	public boolean isGroupMember() {
		return groupMembership != null && !groupMembership.isEmpty();
	}
}
