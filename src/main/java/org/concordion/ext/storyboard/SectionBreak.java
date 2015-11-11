package org.concordion.ext.storyboard;

import org.concordion.api.Element;

/**
 * Card that marks the start of a collapsible section in the storyboard. This section will contain card - it will not be a card.
 * 
 * You wouldn't normally add this card directly but would call storyboard.addSectionBreak("section title");		
 */
public class SectionBreak extends Card {
	private Element exampleElement;
	
	public SectionBreak() {
		setDescription("");
	}

	@Override
	protected void captureData() {
		// Do nothing
	}

	@Override
	protected void addHTMLToContainer(final Element storyboard, final Element container) {
	}

	public void setExampleElement(Element element) {
		this.exampleElement = element;
	}
	
	public Element getExampleElement() {
		return this.exampleElement;
	}
}
