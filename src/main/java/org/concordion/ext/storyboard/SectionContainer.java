package org.concordion.ext.storyboard;

import org.concordion.api.Element;

/**
 * Card that marks the start of a collapsible section in the storyboard. This section will contain card - it will not be a card.
 * 
 * You wouldn't normally add this card directly but would call storyboard.addSectionBreak("section title");		
 */
public class SectionContainer extends Container {

	Element content;
	
	@Override
	public Element build() {
		String id = "toggleheader" + getItemIndex();
		
		Element container = new Element("div");
		container.addStyleClass("toggle-box-container");
		
		Element input = new Element("input");
		input.setId(id);
		input.addStyleClass("toggle-box");
		input.addAttribute("type", "checkbox");
		
		if (getResult() == CardResult.FAILURE) {
			input.addAttribute("checked", "");
		}
		
		Element label = new Element("label");
		label.addAttribute("for", id);
		label.addStyleClass("toggle-box");
		label.addStyleClass(getResult().getKey());
		label.appendText(getTitle());
		
		this.content = new Element("div");
		this.content.addStyleClass("toggle-box-content");
		
		container.appendChild(input);
		container.appendChild(label);
		container.appendChild(this.content);
		
		return container;
	}
	
	@Override
	public Element getContent() {
		return this.content;
	}

	@Override
	public Element getParentElement() {
		return listener.getStoryboardElement();
	}
}
