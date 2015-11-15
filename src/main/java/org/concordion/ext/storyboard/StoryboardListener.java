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

/**
 * Listens to Concordion events and/or method calls and then adds the required cards to the story board. 
 */
public class StoryboardListener implements AssertEqualsListener, AssertTrueListener, AssertFalseListener, ConcordionBuildListener,
		SpecificationProcessingListener, ThrowableCaughtListener, ExampleListener {

	private final Storyboard storyboard = new Storyboard();
	private Container currentContainer = null;
	private ExampleEvent currentExample = null;
	private boolean addSectionsForExamples = true;
	private boolean addCardsToExample = false;
	private boolean addCardOnThrowable = true;
	private boolean addCardOnFailure = true;
	private boolean failureDetected = false;
	private boolean takeScreenshotOnCompletion = true;
	private ScreenshotTaker screenshotTaker = null;
	private boolean lastScreenShotWasThrowable = false;
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
	 * Add custom card and/or set common details for all cards
	 */
	public void addCard(final Card card) {
		if (getResource() == null || getTarget() == null) {
			return;
		}

		if (currentContainer != null) {
			currentContainer.addCard(card);
			
			if (card.getResult() != CardResult.SUCCESS) {
				currentContainer.setResult(card.getResult());
			}
		} else {
			storyboard.addItem(card);
		}
		
		card.setStoryboardListener(this);
		card.setContainer(currentContainer);
		card.captureData();
	}

	/**
	 * Add containers
	 * @param container if null this is regarded as stating all future cards should be added to storyboard
	 */
	public void addContainer(final Container container) {
		if (container != null) {
			container.setStoryboardListener(this);
			storyboard.addItem(container);
		}
		
		currentContainer = container;
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
		this.currentExample  = event;
		
		if (!addSectionsForExamples && !addCardsToExample) return;
		
		// Automatically add section breaks for each example
		Element element = event.getElement();		
		
		if (addCardsToExample) {
			ExampleContainer container = new ExampleContainer(); 
			container.setExampleElement(element);
			
			addContainer(container);
		} else if (addSectionsForExamples) {
			SectionContainer container = new SectionContainer();
			container.setTitle(getExampleTitle(element));
			
			addContainer(container);
		}
	}

	@Override
	public void afterExample(ExampleEvent event) {
		this.currentExample = null;
		
		if (!addSectionsForExamples && !addCardsToExample) return;
		
		if (takeScreenshotOnCompletion) {
			if (!lastScreenShotWasThrowable && screenshotTaker != null) {
				ScreenshotCard card = new ScreenshotCard();
				card.setTitle("Example Completed");
				card.setDescription("");
				
				if (event.getResultSummary().hasExceptions() || event.getResultSummary().getFailureCount() > 0) {
					card.setResult(CardResult.FAILURE);
				} else {
					card.setResult(CardResult.SUCCESS);
				}
				
				addCard(card);
			}
		}
		
		addContainer(null);
	}
	
	@Override
	public void afterProcessingSpecification(final SpecificationProcessingEvent event) {
		if (storyboard.getItems().isEmpty()) {
			return;
		}
		
		if (!lastScreenShotWasThrowable && takeScreenshotOnCompletion && screenshotTaker != null) {
			ScreenshotCard card = new ScreenshotCard();
			card.setTitle("Test Completed");
			card.setDescription("");
			card.setResult(CardResult.SUCCESS);
			addCard(card);
		}

		storyboard.addToSpecification(event, failureDetected);

//		resource = null;
//		target = null;
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
		storyboard.setRemovePriorScreenshotsOnSuccess();
	}

	public void setAutomaticallyAddSectionBreaksForExamples(boolean value) {
		this.addSectionsForExamples = value;
	}

	public void setAddCardsToExample(boolean value) {
		this.addCardsToExample = value;
	}
	
	public String getItemIndex(StoryboardItem item) {
		return storyboard.getItemIndex(item);
	}

	public String getExampleTitle() {
		if (currentExample != null) {
			return getExampleTitle(currentExample.getElement());
		}
		
		return "";
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
}
