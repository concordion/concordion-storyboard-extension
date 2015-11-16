package org.concordion.ext.storyboard;

import java.util.ArrayList;
import java.util.List;
import org.concordion.api.Element;
import org.concordion.api.listener.SpecificationProcessingEvent;

public class Storyboard {
	private Element storyboard = null;
	private List<StoryboardItem> items = new ArrayList<StoryboardItem>();
	
	public Element getElement() {
		return storyboard;
	}
	
	public void addItem(StoryboardItem item) {
		items.add(item);
	}
	
	public List<StoryboardItem> getItems() {
		return items;
	}
	
	public String getItemIndex(StoryboardItem item) {
		return String.valueOf(items.indexOf(item));
	}
	
	public void addToSpecification(final SpecificationProcessingEvent event, boolean failureDetected) {
		if (items.isEmpty()) {
			return;
		}

		overrideIECompatibilityView(event);
		
		storyboard = addStoryboardElement(event);
		if (storyboard == null) {
			return;
		}

		addItemsToList(items, storyboard, failureDetected);
		
		if (!hasChildren(storyboard)) {
			storyboard.addAttribute("style", "display: none");
		}
	}

	private boolean hasChildren(Element el) {
		Element[] children = el.getChildElements("ul");

		for (Element element : children) {
			if (element.hasChildren()) {
				return true;
			}
		}
		
		children = el.getChildElements("div");
		for (Element element : children) {
			if (element.hasChildren()) {
				return true;
			}
		}
		
		return false;
	}

	private void addItemsToList(List<StoryboardItem> items, Element parent, boolean hasFailure) {
		Element ul = null;
		
		for (StoryboardItem item : items) {
			if (item instanceof Container) {
				Container container = (Container)item;
				
				container.appendToParent();
												
				addItemsToList(container.getCards(), container.getContentElement(), container.getResult() == CardResult.FAILURE);
				
				if (!hasChildren(container.getContentElement())) {
					container.getParentElement().removeChild(container.getElement());
				}
			} else {
				Card card = (Card)item;
				
				if (card.shouldAppend(hasFailure)) {
					if (ul == null) {
						ul = new Element("ul");
						parent.appendChild(ul);
					}
					ul.appendChild(buildCard(card));
				} else {
					card.cleanupData();
				}
			}
		}
	}

	private Element buildCard(Card card) {
		// Story card
		Element li = new Element("li");
		li.addStyleClass("storycard");

		Element container = new Element("div");
		container.addStyleClass("scimgcontainer");
		li.appendChild(container);

		card.addHTMLToContainer(container);

		// Summary
		Element summary = new Element("p");
		summary.appendText(card.getTitle());
		summary.addStyleClass("scsummary");
		summary.addStyleClass(card.getResult().getKey());

		li.appendChild(summary);

		// Description
		Element description = new Element("p");
		description.appendText(card.getDescription());
		description.addStyleClass("scdescription");
		description.addAttribute("title", card.getDescription());

		li.appendChild(description);
		
		return li;
	}

	public int getCardIndex(StoryboardItem card) {
		return items.indexOf(card);
	}
	
	public int getNextCardIndex() {
		return items.size();
	}
	/**
	 * Insert meta tag '<meta http-equiv="X-UA-Compatible" content="IE=edge" />";' if not already present.  This
	 * allows the storyboard to display correctly in IE if Compatibility View mode is on - as can happen in a corporate environment.
	 */
	private void overrideIECompatibilityView(final SpecificationProcessingEvent event) {
		Element head = event.getRootElement().getFirstChildElement("head");
		if (head == null) {
			head = event.getRootElement();
		}
		
		Element[] metaTags = head.getChildElements("meta");
		for (Element tag : metaTags) {
			if(tag.getAttributeValue("http-equiv").equalsIgnoreCase("X-UA-Compatible")) {
				if(tag.getAttributeValue("content").equalsIgnoreCase("IE=edge")) {
					return;
				}
			}
		}
		
		Element meta = new Element("meta");
		meta.addAttribute("http-equiv", "X-UA-Compatible");
		meta.addAttribute("content", "IE=edge");
		head.prependChild(meta);
	}
	
	private Element addStoryboardElement(final SpecificationProcessingEvent event) {
		Element body = event.getRootElement().getFirstChildElement("body");
		if (body != null) {
			Element footerDiv = null;

			// Search for storyboard
			Element[] divs = body.getChildElements("div");
			for (Element div : divs) {
				if ("storyboard".equals(div.getAttributeValue("class"))) {
					return div;
				}

				if ("footer".equals(div.getAttributeValue("class"))) {
					footerDiv = div;
				}
			}

			// Append storyboard to page
			Element div = new Element("div");
			div.addStyleClass("storyboard");
			body.appendChild(div);

			Element header = new Element("h3");
			header.setId("StoryboardHeader");
			header.appendText("Storyboard");
			div.appendChild(header);

			// Add screenshot popup image div
			Element popupImg = new Element("img");
			popupImg.setId("StoryCardScreenshotPopup");
			popupImg.addStyleClass("screenshot");
			
			div.appendChild(popupImg);
			
			// If footer exists ensure it is the last element on the page
			if (footerDiv != null) {
				body.removeChild(footerDiv);
				body.appendChild(footerDiv);
			}

			return div;
		}

		return body;
	}
	
}
