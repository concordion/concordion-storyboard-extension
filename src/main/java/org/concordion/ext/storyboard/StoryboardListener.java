package org.concordion.ext.storyboard;

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
import org.concordion.ext.StoryboardExtension.AppendTo;
import org.concordion.slf4j.markers.ScreenshotMarker;

/**
 * Listens to Concordion events and/or method calls and then adds the required cards to the story board. 
 */
public class StoryboardListener implements AssertEqualsListener, AssertTrueListener, AssertFalseListener, ConcordionBuildListener,
		SpecificationProcessingListener, ThrowableCaughtListener, ExampleListener {

	private final Storyboard storyboard = new Storyboard(this);

	// Setters
	private ScreenshotTaker screenshotTaker = null;
	private boolean addCardOnThrowable = true;
	private boolean addCardOnFailure = true;
	private boolean supressRepeatingFailures = true;
	private boolean takeScreenshotOnExampleCompletion = true;
	private AppendTo appendMode = AppendTo.EXAMPLE;
	private boolean skipFinalScreenshot = false;
	private boolean acceptCards = true;
	private boolean useEventListener = true;
	
	// State
	private ExampleEvent currentExample = null;
	private boolean failureDetected = false;
	private boolean lastScreenShotWasThrowable = false;
	private Resource resource;
	private Target target;
	
	/**
	 * Add screenshot
	 * 
	 * @param card The screenshot card
	 */
	public void addCard(final ScreenshotCard card) {
		card.setScreenshotTaker(screenshotTaker);
		addCard((Card) card);
		lastScreenShotWasThrowable = false;
	}

	/**
	 * Add custom card and/or set common details for all cards
	 * 
	 * @param card The card
	 */
	public void addCard(final Card card) {
		if (getResource() == null || getTarget() == null || !acceptCards) {
			return;
		}

		storyboard.addItem(card);
	}

	/**
	 * Add container to storyboard.  If a container is already open close it first.
	 * 
	 * @param container if null this is regarded as stating all future cards should be added to the 
	 * storyboard/parent container
	 */
	public void addContainer(final Container container) {
		if (getResource() == null || getTarget() == null || !acceptCards) {
			return;
		}
		
		if (storyboard.hasCurrentContainer()) {
			if (storyboard.getCurrentContainer().isAutoClose()) {
				closeContainer();
			}
		}
		
		storyboard.addItem(container);
	}
	
	/**
	 * Add container to storyboard.  If a container is already open, append new container to it.
	 * 
	 * @param container if null this is regarded as stating all future cards should be added to the 
	 * storyboard/parent container
	 */
	public void insertContainer(final Container container) {
		if (getResource() == null || getTarget() == null || !acceptCards) {
			return;
		}
		
		storyboard.addItem(container);
	}

	public void closeContainer() {
		if (storyboard.hasCurrentContainer()) {
			takeFinalScreenshotForExample("Completed");
		}

		storyboard.closeContainer();

		resetCurrentState();
	}
	
	@Override
	public void successReported(final AssertSuccessEvent event) {
		// Do nothing
	}

	@Override
	public void failureReported(final AssertFailureEvent event) {
		if (useEventListener) {
			doFailureReported(event, null);
		}
	}
	
	public void doFailureReported(final AssertFailureEvent event, ScreenshotMarker screenshotMarker) {
		if (!addCardOnFailure) {
			return;
		}
		
		if (supressRepeatingFailures && failureDetected) {
			return;
		}
		
		failureDetected = true;
		String title = "Test Failed";

		StringBuilder sb = new StringBuilder().append("See specification for further information");
		
		if(event.getExpected() != null) {
			sb.append("\n").append("Expected: ").append(event.getExpected());
		}
		
		if(event.getActual() != null) {
			sb.append("\n").append("Actual: ").append(event.getActual().toString());
		}
					
		if (!skipFinalScreenshot && (screenshotTaker != null || screenshotMarker != null)) {
			ScreenshotCard card = new ScreenshotCard();
			card.setTitle(title);
			card.setDescription(sb.toString());
			card.setResult(CardResult.FAILURE);

			if (screenshotMarker != null) {
				card.setImageName(screenshotMarker.getFile(), screenshotMarker.getImageSize());
			}

			addCard(card);

			lastScreenShotWasThrowable = true;
		} else {
			NotificationCard card = new NotificationCard();
			card.setTitle(title);
			card.setDescription(sb.toString());
			card.setCardImage(StockCardImage.ERROR);
			card.setResult(CardResult.FAILURE);

			addCard(card);
		}
	}

	@Override
	public void throwableCaught(final ThrowableCaughtEvent event) {
		if (useEventListener) {
			doThrowableCaught(event, null);
		}
	}
	
	public void doThrowableCaught(final ThrowableCaughtEvent event, ScreenshotMarker screenshotMarker) {
		if (!addCardOnThrowable) {
			return;
		}

		Throwable error = event.getThrowable();
		
		if (error.getCause() != null) {
			error = error.getCause();
		}

		failureDetected = true;
		String title = error.getClass().getSimpleName();

		if (!skipFinalScreenshot && (screenshotTaker != null || screenshotMarker != null)) {
			ScreenshotCard card = new ScreenshotCard();
			card.setTitle(title);
			card.setDescription("See specification for further information");
			card.setResult(CardResult.FAILURE);

			if (screenshotMarker != null) {
				card.setImageName(screenshotMarker.getFile(), screenshotMarker.getImageSize());
			}

			addCard(card);

			lastScreenShotWasThrowable = true;
		} else {
			NotificationCard card = new NotificationCard();
			card.setTitle(title);
			card.setDescription("See specification for further information");
			card.setCardImage(StockCardImage.ERROR);
			card.setResult(CardResult.FAILURE);
			addCard(card);
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
		this.currentExample  = event;
		
		// Automatically add section breaks for each example
		switch (appendMode) {
		case EXAMPLE:
			ExampleContainer c = new ExampleContainer();
			c.setTitle(storyboard.getTitle());
			c.setExampleElement(event.getElement());			
			
			addContainer(c);
			break;
			
		case NEW_STORYBOARD_SECTION_PER_EXAMPLE:
			SectionContainer sc = new SectionContainer();
			sc.setTitle(getExampleTitle(event.getElement()));
			
			addContainer(sc);
			break;
			
		default:
			break;
		}
	}

	@Override
	public void afterExample(ExampleEvent event) {
		takeFinalScreenshotForExample("Example Completed");
		
		switch (appendMode) {
		case EXAMPLE:
		case NEW_STORYBOARD_SECTION_PER_EXAMPLE:
			storyboard.resetContainers();
			break;
		default:
			break;
		}
		
		resetCurrentState();
	}
	
	private void resetCurrentState() {
		// Reset current state as if where starting a new specification
		this.currentExample = null;
		this.failureDetected = false;
		this.acceptCards = true;
		this.skipFinalScreenshot = false;
		this.lastScreenShotWasThrowable = false;
	}
	
	@Override
	public void afterProcessingSpecification(final SpecificationProcessingEvent event) {
		if (storyboard.getItems().isEmpty()) {
			return;
		}

		storyboard.addToSpecification(event, failureDetected);

		resource = null;
		target = null;
	}
	
	private void takeFinalScreenshotForExample(String title) {
		if (!takeScreenshotOnExampleCompletion) return;
		if (skipFinalScreenshot) return;
		if (screenshotTaker == null) return;
		if (lastScreenShotWasThrowable) return;
				
		ScreenshotCard card = new ScreenshotCard();
		card.setTitle(title);
		card.setDescription("");
		card.setResult(CardResult.SUCCESS);
		addCard(card);
	}

	public boolean hasScreenshotTaker() {
		return screenshotTaker != null;
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

	public void setSupressRepeatingFailures(boolean value) {
		this.supressRepeatingFailures = value;
	}
	
	public Resource getResource() {
		return resource;
	}

	public Target getTarget() {
		return target;
	}

	public void setTakeScreenshotOnExampleCompletion(boolean value) {
		this.takeScreenshotOnExampleCompletion = value;
	}
	
	public void setSkipFinalScreenshot() {
		this.skipFinalScreenshot = true;
	}

	public void markPriorScreenshotsForRemoval() {
		storyboard.markPriorScreenshotsForRemoval();
	}
	
	public String getItemIndex(StoryboardItem item) {
		return storyboard.getItemIndex(item);
	}

	public String getCurrentExampleTitle() {
		if (currentExample == null) {
			return "";
		}
		
		return getExampleTitle(getExampleElement());
	}
	
	public String getExampleTitle(Element element) {
		String title = element.getAttributeValue("example", "http://www.concordion.org/2007/concordion"); 
		
		for (int i = 1; i < 5; i++) {
			Element header = element.getFirstChildElement("h" + String.valueOf(i));
			
			if (header != null) {
				title = header.getText();
				break;
			}		
		}

		return title;
	}

	public Element getExampleElement() {
		if (currentExample == null) {
			return null;
		}
		
		return currentExample.getElement();
	}

	public Element getStoryboardElement() {
		return storyboard.getElement();
	}

	public void setTitle(String title) {
		storyboard.setTitle(title);		
	}

	public void setAppendMode(AppendTo appendMode) {
		this.appendMode = appendMode;		
	}

	public void setAcceptCards(boolean accept) {
		this.acceptCards  = accept;
	}

	public void setUseLogListener() {
		this.useEventListener = false;
	}
}
