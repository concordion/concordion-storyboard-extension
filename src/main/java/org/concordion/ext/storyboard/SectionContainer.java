package org.concordion.ext.storyboard;

import org.concordion.api.Element;

/**
 * Container that presents cards within a collapsible section in the storyboard. 
 * 
 * You wouldn't normally add this card directly but would call storyboard.addSectionContainer("section title");
 * To stop added cards to the container, complete the container using storyboard.completeContainer();		
 */
public class SectionContainer extends Container {

	private Element parent;
	private Element container;
	private Element content;
		
	@Override
	public void writeTo(Element parent) {
		this.parent = parent;
		
		String id = "toggleheader" + getItemIndex();
		
		container = new Element("div");
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
		container.appendChild(content);
		
		getParentElement().appendChild(container);
	}
	
	@Override
	public Element getContentElement() {
		return content;
	}

	@Override
	public Element getParentElement() {
		return parent;
	}

	@Override
	public void removeFromParent() {
		getParentElement().removeChild(container);
	}
}
