package org.concordion.ext.storyboard;

import org.concordion.api.Element;

/**
 * Card that marks the start of a collapsible group in the storyboard. Clicking on this card will show/hide contained cards.
 * 
 * You wouldn't normally add this card directly but would call storyboard.startGroup("groupname");		
 */
public class GroupStartCard extends Card {
	private boolean collapsed = false;
	private String groupOwnership;

	public GroupStartCard() {
		setDescription("Click image to show/hide story cards for this section");
	}

	protected void setGroupOwnership(final String groupName) {
		groupOwnership = groupName;
	}

	protected String getGroupOwnership() {
		return groupOwnership;
	}

	protected void setCollapsed(final boolean collapsed) {
		this.collapsed = collapsed;
	}

	@Override
	protected void captureData() {
		// Do nothing
	}

	@Override
	protected void addHTMLToContainer(final Element storyboard, final Element container) {
		String collapseImageName = getResource().getRelativePath(StockCardImage.COLLAPSE.getResource());
		String expandImageName = getResource().getRelativePath(StockCardImage.EXPAND.getResource());

		Element img = new Element("img");
		img.setId(this.getDescription());
		img.addStyleClass("sizeheight");
		img.addAttribute("src", collapsed ? expandImageName : collapseImageName);
		img.addAttribute("onClick", "showStoryboardSection(this, '" + groupOwnership + "', '" + collapseImageName + "', '" + expandImageName + "');");

		container.appendChild(img);
	}

	protected void addHTMLToGroupCard(final Element li, final Card card) {
		li.addStyleClass(card.getGroupMembership());

		if (collapsed) {
			li.addAttribute("style", "display:none");
		}
	}
}
