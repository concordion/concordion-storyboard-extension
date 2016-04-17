package org.concordion.ext.storyboard;

import org.concordion.api.Element;

/**
 * Container that is appended to example to present all cards pertaining to the example.
 * 
 * You wouldn't normally add this card directly, it would get added automatically when using examples and
 * default append mode: storyboard.setAppendMode(AppendTo.EXAMPLE);		
 */
public class ExampleContainer extends Container {
	private Element hr;
	private Element container;
	private Element content;
	private Element exampleElement;
	
	public ExampleContainer() {
		setTitle("Storyboard");
	}

	public void setExampleElement(Element element) {
		this.exampleElement = element;
	}
	
	@Override
	public void writeTo(Element parent) {
		String id = "toggleheader" + getItemIndex();
		
		container = new Element("div");
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
		
		this.content = new Element("div");
		this.content.addStyleClass("toggle-example-content");
		
		container.appendChild(input);
		container.appendChild(label);
		container.appendChild(this.content);
		
		hr = new Element("hr");
		hr.addStyleClass("toggle-separator");
				
		getParentElement().appendChild(hr);		
		getParentElement().appendChild(container);
	}

//	@Override
//	public Element getElement() {
//		return container;
//	}
	
	@Override
	public Element getContentElement() {
		return this.content;
	}
	
	@Override
	public Element getParentElement() {
		return exampleElement;
	}

	@Override
	public void removeFromParent() {
		getParentElement().removeChild(hr);
		getParentElement().removeChild(container);
	}
}
