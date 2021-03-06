package org.concordion.ext.storyboard;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.concordion.api.Element;
import org.concordion.api.listener.SpecificationProcessingEvent;

class Storyboard {
	private StoryboardListener listener;
	private Element storyboard = null;
	private List<StoryboardItem> items = new ArrayList<StoryboardItem>();
	private List<Container> openContainers = new ArrayList<Container>();
	private String title = "Storyboard";
		
	Storyboard(StoryboardListener listener) {
		this.listener = listener;
	}
	
	public Element getElement() {
		return storyboard;
	}
	
	private int getCurrentContainerIndex() {
		return openContainers.size() - 1;
	}
	
	/**
	 * Add card to the currently open container or storyboard
	 * @param container Container to add
	 */
	public void addItem(Card card) {
		card.setStoryboardListener(listener);
		
		if (openContainers.isEmpty()) {
			items.add(card);
		} else {
			Container container = openContainers.get(getCurrentContainerIndex());
			card.setContainer(container);
			container.addItem(card);
		}		

		card.captureData();
	}
	
	public Card getLastCard() {
		List<StoryboardItem> search;
		
		if (openContainers.isEmpty()) {
			search = items;
		} else {
			search = openContainers.get(getCurrentContainerIndex()).getItems();
		}
		
		if (search.size() > 0) {
			StoryboardItem item = search.get(search.size() - 1);
			
			if (item instanceof Card) {
				return (Card) item;
			}
		}
		
		return null;
	}
	
	/**
	 * Add container to the currently open container or storyboard
	 * @param container Container to add
	 */
	public void addItem(Container container) {
		if (container == null) {
			return;
		}
		
		container.setStoryboardListener(listener);
		
		if (openContainers.isEmpty()) {
			// Append container to storyboard 
			items.add(container);
		} else {
			// Append container to current container
			Container current = getCurrentContainer();
			
			container.setContainer(current);
			current.addItem(container);
		}
		
		openContainers.add(container);
	}
	
	public boolean hasCurrentContainer() {
		return !openContainers.isEmpty();
	}

	public Container getCurrentContainer() {
		return openContainers.get(getCurrentContainerIndex());
	}	
	
	public void closeContainer() {
		if (hasCurrentContainer()) {
			openContainers.remove(getCurrentContainerIndex());
		}
	}

	public void resetContainers() {
		openContainers.clear();
	}
	
	public List<StoryboardItem> getItems() {
		return items;
	}
	
	public String getItemIndex(StoryboardItem item) {
		return String.valueOf(items.indexOf(item));
	}
	
	public void markPriorScreenshotsForRemoval() {
		List<StoryboardItem> items;	
		ListIterator<StoryboardItem> iter;
	
		if (openContainers.isEmpty()) {
			items = this.items;
		} else {
			items = openContainers.get(getCurrentContainerIndex()).getItems();
		}
		
		iter = items.listIterator(items.size());

		while(iter.hasPrevious()) {
			StoryboardItem card = iter.previous();
		
			if (card instanceof Container) {
				break;
			}
			
			if (card instanceof ScreenshotCard) {
				((ScreenshotCard)card).setDeleteIfSuccessful(true);
			}
		}
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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
				ul = null;
				 
				Container container = (Container)item;
				
				container.writeTo(parent);
												
				addItemsToList(container.getItems(), container.getContentElement(), container.getResult() == CardResult.FAILURE);
				
				if (!hasChildren(container.getContentElement())) {
					container.removeFromParent();
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

			if (getTitle() != null && !getTitle().isEmpty()) {
				Element header = new Element("h3");
				header.setId("StoryboardHeader");
				header.appendText(title);
				div.appendChild(header);
			}
			
			// Add screenshot popup image div
			Element popupImg = new Element("img");
			popupImg.setId("StoryCardScreenshotPopup");
			popupImg.addStyleClass("screenshot");
			
			//div.appendChild(popupImg);
			body.appendChild(popupImg);
			
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
