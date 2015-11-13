package org.concordion.ext.storyboard;

import org.concordion.api.Element;

/**
 * Card that marks the start of a collapsible section in the storyboard. This section will contain card - it will not be a card.
 * 
 * You wouldn't normally add this card directly but would call storyboard.addSectionBreak("section title");		
 */
public class ExampleContainer extends Container {
	private Element exampleElement;
	
	public ExampleContainer() {
		setTitle("Storyboard");
	}

	public void setExampleElement(Element element) {
		this.exampleElement = element;
	}
	
	public Element getExampleElement() {
		return this.exampleElement;
	}

	@Override
	public void addCardsToSpecification() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Element addContainerToSpecification(Element storyboard) {
		String id = "toggleheader" + getCardIndex();
		
		Element container = new Element("div");
		container.addStyleClass("toggle-example-container");
		
		Element input = new Element("input");
		input.setId(id);
		input.addStyleClass("toggle-example");
		input.addAttribute("type", "checkbox");
		
		if (getResult() == CardResult.FAILURE) {
			input.addAttribute("checked", "");
		}
		
		Element label = new Element("label");
		label.addAttribute("for", id);
		label.addStyleClass("toggle-example");
		label.addStyleClass(getResult().getKey());
		label.appendText(getTitle());
		
		Element hr = new Element("hr");
		
		Element content = new Element("div");
		content.addStyleClass("toggle-example-content");
		
		container.appendChild(input);
		container.appendChild(label);
		container.appendChild(hr);
		container.appendChild(content);
		
		getExampleElement().appendChild(container);

		return content;


	}
}
