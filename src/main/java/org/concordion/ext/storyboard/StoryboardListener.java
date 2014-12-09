package org.concordion.ext.storyboard;

import java.util.ArrayList;
import java.util.List;

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
import org.concordion.api.listener.SpecificationProcessingEvent;
import org.concordion.api.listener.SpecificationProcessingListener;
import org.concordion.api.listener.ThrowableCaughtEvent;
import org.concordion.api.listener.ThrowableCaughtListener;
import org.concordion.ext.ScreenshotTaker;
import org.concordion.ext.screenshot.RobotScreenshotTaker;

public class StoryboardListener implements AssertEqualsListener, AssertTrueListener, AssertFalseListener, ConcordionBuildListener,
		SpecificationProcessingListener, ThrowableCaughtListener {

	private final List<Card> cards = new ArrayList<Card>();
	private ScreenshotTaker screenshotTaker = new RobotScreenshotTaker();
	private boolean addCardOnThrowable = true;
	private boolean addCardOnFailure = true;
	private boolean lastScreenShotWasThrowable = false;
	private String collapsibleGroup = "";
	private Resource resource;
	private Target target;

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
	public void addCard(final CollapsibleStartCard card) {
		if (!collapsibleGroup.isEmpty()) {
			CollapsibleStopCard stopCard = new CollapsibleStopCard();
			stopCard.setResult(CardResult.WARN);
			addCard(stopCard);
		}

		addCard((Card) card);

		collapsibleGroup = "scgroup" + getCardNumber();
		card.setGroupOwnership(collapsibleGroup);
	}

	/**
	 * Add collapsible section end
	 */
	public void addCard(final CollapsibleStopCard card) {
		if (collapsibleGroup.isEmpty()) {
			return;
		}

		CollapsibleStartCard start = getLastCollapsibleStartCard();
		if (start == null) {
			return;
		}

		if (card.getResult() != CardResult.FAILURE) {
			start.setCollapsed(true);
		}
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
		cards.add(card);

		if (!collapsibleGroup.isEmpty() && card.getResult() != CardResult.SUCCESS) {
			CollapsibleStartCard start = getLastCollapsibleStartCard();

			if (start != null) {
				start.setResult(card.getResult());
			}
		}
	}

	private CollapsibleStartCard getLastCollapsibleStartCard() {
		CollapsibleStartCard last = null;

		for (int i = cards.size() - 1; i >= 0; i--) {
			if (cards.get(i) instanceof CollapsibleStartCard) {
				last = (CollapsibleStartCard) cards.get(i);
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
	public void afterProcessingSpecification(final SpecificationProcessingEvent event) {
		if (cards.isEmpty()) {
			return;
		}

		overrideIECompatibilityView(event);
		
		if (!lastScreenShotWasThrowable && screenshotTaker != null) {
			ScreenshotCard card = new ScreenshotCard();
			card.setTitle("Test Completed");
			card.setDescription("This is the page the test  finished on");
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
		CollapsibleStartCard collapseGroup = null;

		Element ul = new Element("ul");
		storyboard.appendChild(ul);

		for (Card card : cards) {
			// Story card
			Element li = new Element("li");
			li.addStyleClass("storycard");

			if (card instanceof CollapsibleStartCard) {
				collapseGroup = (CollapsibleStartCard) card;
			}

			if (card.isGroupMember()) {
				collapseGroup.addHTMLToGroupCard(li, card);
			}

			ul.appendChild(li);

			Element container = new Element("div");
			container.addStyleClass("scimgcontainer");
			li.appendChild(container);

			card.addHTMLToContainer(storyboard, container);

			// Summary
			Element summary = new Element("p");
			summary.appendText(card.getTitle());
			summary.addStyleClass("scsummary " + card.getResult().getKey());

			li.appendChild(summary);

			// Description
			Element description = new Element("p");
			description.appendText(card.getDescription());
			description.addStyleClass("scdescription");
			description.addAttribute("title", card.getDescription());

			li.appendChild(description);
		}
	}

	public int getCardNumber() {
		return cards.size();
	}

	public void setScreenshotTaker(final ScreenshotTaker screenshotTaker) {
		this.screenshotTaker = screenshotTaker;
	}

	public void setAddCardOnThrowable(final boolean takeShot) {
		this.addCardOnThrowable = takeShot;
	}

	public void setAddCardOnFailure(final boolean takeShot) {
		this.addCardOnFailure = takeShot;
	}
	
	public Resource getResource() {
		return resource;
	}

	public Target getTarget() {
		return target;
	}
}
