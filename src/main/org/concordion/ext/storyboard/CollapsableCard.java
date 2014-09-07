package org.concordion.ext.storyboard;

import org.concordion.api.Element;
import org.concordion.api.Resource;

public class CollapsableCard extends Card {
	private String collapseImageName;
	private String expandImageName;
	private boolean collapsed = false;
	private String groupOwnership;

	public void captureDetails(final Resource resource, final String groupName) {
		groupOwnership = groupName;
		collapseImageName = resource.getRelativePath(CardImage.COLLAPSE.getResource());
		expandImageName = resource.getRelativePath(CardImage.EXPAND.getResource());
	}

	@Override
	public void addHTMLToContainer(final Element storyboard, final Element container) {

		Element img = new Element("img");
		img.setId(this.getDescription());
		img.addStyleClass("sizeheight");
		img.addAttribute("src", collapsed ? expandImageName : collapseImageName);
		img.addAttribute("onClick", "showStoryboardSection(this, '" + groupOwnership + "', '" + collapseImageName + "', '" + expandImageName + "');");

		container.appendChild(img);
	}

	@Override
	protected String getFileExtension() {
		return "txt";
	}

	public String getGroupOwnership() {
		return groupOwnership;
	}

	public void setCollapsed(final boolean collapsed) {
		this.collapsed = collapsed;
	}

	public void addHTMLToGroupCard(final Element li, final Card card) {
		li.addStyleClass(card.getGroupMembership());

		if (collapsed) {
			li.addAttribute("style", "display:none");
		}
	}
}
