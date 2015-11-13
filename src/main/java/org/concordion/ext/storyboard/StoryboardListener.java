package org.concordion.ext.storyboard;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.concordion.api.Element;
import org.concordion.api.Resource;
import org.concordion.api.Target;
import org.concordion.api.listener.AssertEqualsListener;
import org.concordion.api.listener.AssertFailureEvent;
import org.concordion.api.listener.AssertFalseListener;
import org.concordion.api.listener.AssertSuccessEvent;
import org.concordion.api.listener.AssertTrueListener;
import org.concordion.api.listener.ConcordionBuildEvent;
import org.concordion.api.listener.ConcordionBuildListener;
import org.concordion.api.listener.ExampleEvent;
import org.concordion.api.listener.ExampleListener;
import org.concordion.api.listener.SpecificationProcessingEvent;
import org.concordion.api.listener.SpecificationProcessingListener;
import org.concordion.api.listener.ThrowableCaughtEvent;
import org.concordion.api.listener.ThrowableCaughtListener;
import org.concordion.ext.ScreenshotTaker;

/**
 * Listens to Concordion events and/or method calls and then adds the required cards to the story board. 
 */
public class StoryboardListener implements AssertEqualsListener, AssertTrueListener, AssertFalseListener, ConcordionBuildListener,
		SpecificationProcessingListener, ThrowableCaughtListener, ExampleListener {

	private final List<StoryboardItem> cards = new ArrayList<StoryboardItem>();
	private Container currentContainer = null;
	private ScreenshotTaker screenshotTaker = new RobotScreenshotTaker();
	private boolean automaticallyAddSectionBreaksForExamples = true;
	private boolean addCardsToExample = false;
	private boolean takeScreenshotOnCompletion = true;
	private boolean addCardOnThrowable = true;
	private boolean addCardOnFailure = true;
	private boolean lastScreenShotWasThrowable = false;
	private String collapsibleGroup = "";
	private Resource resource;
	private Target target;
	private boolean failureDetected = false;

	/**
	 * Add screenshot
	 */
	public void addCard(final ScreenshotCard card) {
		card.setScreenshotTaker(screenshotTaker);
		addCard((Card) card);
		lastScreenShotWasThrowable = false;
	}

	/**
	 * Add collapsible section start
	 */
	public void addCard(final GroupStartCard card) {
		if (!collapsibleGroup.isEmpty()) {
			GroupStopCard stopCard = new GroupStopCard();
			stopCard.setResult(CardResult.WARN);
			addCard(stopCard);
		}

		addCard((Card) card);

		collapsibleGroup = "scgroup" + card.getCardIndex();
		card.setGroupOwnership(collapsibleGroup);
	}

	/**
	 * Add collapsible section end
	 */
	public void addCard(final GroupStopCard card) {
		if (collapsibleGroup.isEmpty()) {
			return;
		}

		GroupStartCard start = getLastCollapsibleStartCard();
		if (start == null) {
			return;
		}
		
		card.setResult(start.getResult());
		start.setCollapsed(true);
		
		card.setTitle(start.getTitle());

		addCard((Card) card);

		collapsibleGroup = "";
	}

	/**
	 * Add custom card and/or set common details for all cards
	 */
	public void addCard(final Card card) {
		if (resource == null || target == null) {
			return;
		}

		card.setStoryboardListener(this);
		card.setGroupMembership(collapsibleGroup);
		card.captureData();
		
		if (currentContainer != null) {
			currentContainer.addCard(card);
			
			if (card.getResult() != CardResult.SUCCESS) {
				currentContainer.setResult(card.getResult());
			}
		} else {
			cards.add(card);
		
			if (card.getResult() != CardResult.SUCCESS) {
				if (!collapsibleGroup.isEmpty()) {
					GroupStartCard start = getLastCollapsibleStartCard();
		
					if (start != null) {
						start.setResult(card.getResult());
						start.setCollapsed(card.getResult() != CardResult.FAILURE);
					}
				}
			}
		}
	}

	/**
	 * Add containers
	 * @param container if null this is regarded as stating all future cards should be added to storyboard
	 */
	public void addContainer(final Container container) {
		if (container != null) {
			container.setStoryboardListener(this);
			cards.add(container);
		}
		
		currentContainer = container;
	}

	private GroupStartCard getLastCollapsibleStartCard() {
		GroupStartCard last = null;

		for (int i = cards.size() - 1; i >= 0; i--) {
			if (cards.get(i) instanceof GroupStartCard) {
				last = (GroupStartCard) cards.get(i);
				break;
			}
		}
		
		return last;
	}

	@Override
	public void successReported(final AssertSuccessEvent event) {
		// Do nothing
	}

	@Override
	public void failureReported(final AssertFailureEvent event) {
		failureDetected = true;
		
		if (addCardOnFailure) {
			String title = "Test Failed";

			StringBuilder sb = new StringBuilder().append("See specification for further information");
			
			if(event.getExpected() != null) {
				sb.append("\n").append("Expected: ").append(event.getExpected());
			}
			
			if(event.getActual() != null) {
				sb.append("\n").append("Actual: ").append(event.getActual().toString());
			}
						
			if (screenshotTaker == null) {
				NotificationCard card = new NotificationCard();
				card.setTitle(title);
				card.setDescription(sb.toString());
				card.setCardImage(StockCardImage.ERROR);
				card.setResult(CardResult.FAILURE);
				addCard(card);
			} else {
				ScreenshotCard card = new ScreenshotCard();
				card.setTitle(title);
				card.setDescription(sb.toString());
				card.setResult(CardResult.FAILURE);
				addCard(card);

				lastScreenShotWasThrowable = true;
			}
		}
	}

	@Override
	public void throwableCaught(final ThrowableCaughtEvent event) {
		failureDetected = true;
		
		if (currentContainer != null) {
			currentContainer.setResult(CardResult.FAILURE);
		}
		
		if (addCardOnThrowable) {
			String title = "Exception Caught";

			Throwable error = event.getThrowable();
			if (error.getCause() != null) {
				error = error.getCause();
			}

			title = error.getClass().getSimpleName();

			if (screenshotTaker == null) {
				NotificationCard card = new NotificationCard();
				card.setTitle(title);
				card.setDescription("See specification for further information");
				card.setCardImage(StockCardImage.ERROR);
				card.setResult(CardResult.FAILURE);
				addCard(card);
			} else {
				ScreenshotCard card = new ScreenshotCard();
				card.setTitle(title);
				card.setDescription("See specification for further information");
				card.setResult(CardResult.FAILURE);
				addCard(card);

				lastScreenShotWasThrowable = true;
			}
		}
	}

	@Override
	public void beforeProcessingSpecification(final SpecificationProcessingEvent event) {
		resource = event.getResource();
	}

	@Override
	public void concordionBuilt(final ConcordionBuildEvent event) {
		target = event.getTarget();
	}

	@Override
	public void beforeExample(ExampleEvent event) {
		if (!automaticallyAddSectionBreaksForExamples && !addCardsToExample) return;
		
		// Automatically add section breaks for each example
		Element element = event.getElement();		
		
		if (addCardsToExample) {
			ExampleContainer container = new ExampleContainer(); 
			container.setExampleElement(element);
			
			addContainer(container);
		} else if (automaticallyAddSectionBreaksForExamples) {
			String title = element.getAttributeValue("example", "http://www.concordion.org/2007/concordion"); 
			
			for (int i = 1; i < 5; i++) {
				Element header = element.getFirstChildElement("h" + String.valueOf(i));
				
				if (header != null) {
					title = header.getText();
					break;
				}		
			}
			
			SectionContainer container = new SectionContainer();
			container.setTitle(title);
			
			addContainer(container);
		}
	}

	@Override
	public void afterExample(ExampleEvent event) {
		if (!automaticallyAddSectionBreaksForExamples && !addCardsToExample) return;
		
		if (takeScreenshotOnCompletion) {
			if (!lastScreenShotWasThrowable && screenshotTaker != null) {
				ScreenshotCard sscard = new ScreenshotCard();
				sscard.setTitle("Example Completed");
				sscard.setDescription("");
				
				if (event.getResultSummary().hasExceptions() || event.getResultSummary().getFailureCount() > 0) {
					sscard.setResult(CardResult.FAILURE);
				} else {
					sscard.setResult(CardResult.SUCCESS);
				}
				
				addCard(sscard);
			}
		}
		
		addContainer(null);
	}
	
	@Override
	public void afterProcessingSpecification(final SpecificationProcessingEvent event) {
		if (cards.isEmpty()) {
			return;
		}

		overrideIECompatibilityView(event);
		
		if (!lastScreenShotWasThrowable && takeScreenshotOnCompletion && screenshotTaker != null) {
			ScreenshotCard card = new ScreenshotCard();
			card.setTitle("Test Completed");
			card.setDescription("");
			card.setResult(CardResult.SUCCESS);
			addCard(card);
		}

		Element storyboard = getStoryboard(event);
		if (storyboard == null) {
			return;
		}

		addCardsToStoryboard(storyboard);

		resource = null;
		target = null;
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
	
	private Element getStoryboard(final SpecificationProcessingEvent event) {
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

	private void addCardsToStoryboard(final Element storyboard) {
		Element ul = new Element("ul");
		storyboard.appendChild(ul);

		addItemsToList(storyboard, ul, failureDetected, cards);
				
		Element[] uls = storyboard.getChildElements("ul");
		boolean hasChildren = false;
		for (Element element : uls) {
			if (element.hasChildren()) {
				hasChildren = true;
				break;
			}
		}
		
		if (!hasChildren) {
			storyboard.addAttribute("style", "display: none");
		}
	}

	private void addItemsToList(final Element storyboard, Element ul, boolean hasFailure, List<StoryboardItem> items) {
		GroupStartCard collapseGroup = null;
	
		for (StoryboardItem item : items) {
			if (item instanceof Container) {
				Container container = (Container)item;
				
				boolean ulOnStoryboard = ul.getParentElement() == storyboard;
				boolean addUlToStoryboard = false;
				
				if (ulOnStoryboard && !ul.hasChildren()) {
					storyboard.removeChild(ul);
					addUlToStoryboard = true;
				}
				
				Element containerUl = new Element("ul");
				container.addContainerToSpecification(storyboard).appendChild(containerUl);
								
				addItemsToList(storyboard, containerUl, container.getResult() == CardResult.FAILURE, container.getCards());
				
				if (!containerUl.hasChildren()) {
					containerUl.getParentElement().removeChild(containerUl);
				}
				
				if (addUlToStoryboard) {
					storyboard.appendChild(ul);
				}
			} else {
				if (item instanceof GroupStartCard) {
					collapseGroup = (GroupStartCard)item;
				}
		
				Card card = (Card)item;
				
				if (card.shouldAppend(hasFailure)) {
					ul.appendChild(buildCard(storyboard, collapseGroup, card));
				} else {
					card.cleanupData();
				}
			}
		}
		
	}

	private Element buildCard(final Element storyboard, GroupStartCard collapseGroup, Card card) {
		// Story card
		Element li = new Element("li");
		li.addStyleClass("storycard");

		if (card.isGroupMember()) {
			collapseGroup.addHTMLToGroupCard(li, card);
		}		

		Element container = new Element("div");
		container.addStyleClass("scimgcontainer");
		li.appendChild(container);

		card.addHTMLToContainer(storyboard, container);

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
		return cards.indexOf(card);
	}
	
	public int getNextCardIndex() {
		return cards.size();
	}

	public void setScreenshotTaker(final ScreenshotTaker screenshotTaker) {
		this.screenshotTaker = screenshotTaker;
	}

	public void setAddCardOnThrowable(final boolean value) {
		this.addCardOnThrowable = value;
	}

	public void setAddCardOnFailure(final boolean value) {
		this.addCardOnFailure = value;
	}
	
	public Resource getResource() {
		return resource;
	}

	public Target getTarget() {
		return target;
	}

	public void setTakeScreenshotOnCompletion(boolean value) {
		this.takeScreenshotOnCompletion = value;
	}

	public void setRemovePriorScreenshotsOnSuccess() {
		ListIterator<StoryboardItem> list = cards.listIterator(cards.size());

		while(list.hasPrevious()) {
			StoryboardItem card = list.previous();
		
			if (card instanceof Container) {
				break;
			}
			
			if (card instanceof ScreenshotCard) {
				((ScreenshotCard)card).setDeleteIfSuccessful(true);
			}
		}
	}

	public void setAutomaticallyAddSectionBreaksForExamples(boolean value) {
		this.automaticallyAddSectionBreaksForExamples = value;
	}

	public void setAddCardsToExample(boolean value) {
		this.addCardsToExample = value;
	}
}
