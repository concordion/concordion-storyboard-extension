/*
 * Copyright (c) 2010 Two Ten Consulting Limited, New Zealand 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
	private boolean lastScreenShotWasThrowable = false;
	private String collapsableGroup = "";

	private Resource resource;
	private Target target;

	public void addScreenshotCard(final String summary, final String description, final CardResult result) {
		if (screenshotTaker == null || resource == null) {
			return;
		}

		ScreenshotCard card = new ScreenshotCard();
		setCardBasicDetails(card, summary, description, result);
		card.captureScreenshot(resource, target, screenshotTaker, getNextCardNumber());

		cards.add(card);

		lastScreenShotWasThrowable = false;
	}

	public void addNotificationCard(final String summary, final String description, final String data, final CardImage image, final CardResult result) {
		if (resource == null) {
			return;
		}

		NotificationCard card = new NotificationCard();
		setCardBasicDetails(card, summary, description, result);
		card.captureData(resource, target, data, image, getNextCardNumber());

		cards.add(card);
	}

	public void startCollapsableGroup(final String summary) {
		if (resource == null) {
			return;
		}

		String group = "scgroup" + summary.replaceAll(" ", "");

		if (!collapsableGroup.isEmpty()) {
			stopCollapsibleGroup(CardResult.WARN);
		}

		CollapsableCard card = new CollapsableCard();
		setCardBasicDetails(card, summary, "Click image to show/hide story cards for this section", CardResult.SUCCESS);
		card.captureDetails(resource, group);

		cards.add(card);

		collapsableGroup = group;
	}

	public void stopCollapsibleGroup(final CardResult result) {
		if (collapsableGroup.isEmpty()) {
			return;
		}

		CollapsableCard last = null;

		for (int i = cards.size() - 1; i >= 0; i--) {
			if (cards.get(i) instanceof CollapsableCard) {
				last = (CollapsableCard) cards.get(i);
				break;
			}
		}

		if (last == null) {
			return;
		}

		last.setResult(result);

		if (result != CardResult.FAILURE) {
			NotificationCard card = new NotificationCard();
			setCardBasicDetails(card, last.getSummary(), "This step has completed successfully", result);
			card.captureData(resource, CardImage.COMPLETE);

			cards.add(card);

			last.setCollapsed(true);
		}

		collapsableGroup = "";
	}

	private void setCardBasicDetails(final Card card, final String summary, final String description, final CardResult result) {
		card.setSummary(summary);
		card.setDescription(description.trim() + (description.trim().endsWith(".") ? "" : "."));
		card.setResult(result);
		card.setGroupMembership(collapsableGroup);
	}

	@Override
	public void successReported(final AssertSuccessEvent event) {
		// Do nothing
	}

	@Override
	public void failureReported(final AssertFailureEvent event) {
		// Do nothing
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
				addNotificationCard(title, "See specification for further information", "", CardImage.ERROR, CardResult.FAILURE);
			} else {
				addScreenshotCard(title, "See specification for further information", CardResult.FAILURE);
				lastScreenShotWasThrowable = true;
			}

			stopCollapsibleGroup(CardResult.FAILURE);
		}
	}

	@Override
	public void beforeProcessingSpecification(final SpecificationProcessingEvent event) {
		resource = event.getResource();
	}

	@Override
	public void afterProcessingSpecification(final SpecificationProcessingEvent event) {
		if (cards.isEmpty()) {
			return;
		}

		if (!lastScreenShotWasThrowable) {
			addScreenshotCard("Test Completed", "This is the page the test  finished on", CardResult.SUCCESS);
		}

		Element storyboard = getStoryboard(event);
		if (storyboard == null) {
			return;
		}

		addCardsToStoryboard(storyboard);
	}

	@Override
	public void concordionBuilt(final ConcordionBuildEvent event) {
		target = event.getTarget();
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
		CollapsableCard collapseGroup = null;

		Element ul = new Element("ul");
		storyboard.appendChild(ul);

		for (Card card : cards) {
			// Story card
			Element li = new Element("li");
			li.addStyleClass("storycard");

			if (card instanceof CollapsableCard) {
				collapseGroup = (CollapsableCard) card;
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
			summary.appendText(card.getSummary());
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

	private int getNextCardNumber() {
		return cards.size() + 1;
	}

	public void setScreenshotTaker(final ScreenshotTaker screenshotTaker) {
		this.screenshotTaker = screenshotTaker;
	}

	public void setAddCardOnThrowable(final boolean takeShot) {
		this.addCardOnThrowable = takeShot;
	}
}
