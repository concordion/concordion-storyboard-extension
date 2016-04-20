package org.concordion.ext;

import java.util.HashMap;
import java.util.Map;

import org.concordion.api.Resource;
import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;
import org.concordion.ext.storyboard.Card;
import org.concordion.ext.storyboard.CardImage;
import org.concordion.ext.storyboard.CardResult;
import org.concordion.ext.storyboard.Container;
import org.concordion.ext.storyboard.CustomCardImage;
import org.concordion.ext.storyboard.NotificationCard;
import org.concordion.ext.storyboard.RobotScreenshotTaker;
import org.concordion.ext.storyboard.ScreenshotCard;
import org.concordion.ext.storyboard.SectionContainer;
import org.concordion.ext.storyboard.StockCardImage;
import org.concordion.ext.storyboard.StoryboardListener;

/**
 * The Storyboard extension provides the capability to embed a series of 'cards' containing screenshots or data (text, xml, or html) in a panel at the bottom of the specification.
 * <p>
 * Further information can be found in the <a href="http://concordion.github.io/concordion-storyboard-extension/spec/spec/concordion/ext/storyboard/Storyboard.html">Specification</a>.
 * </p>
 */
public class StoryboardExtension implements ConcordionExtension {

	private final StoryboardListener extension = new StoryboardListener();
	private boolean acceptsScreenShots = true;
	private final Map<String, CustomCardImage> customImages = new HashMap<String, CustomCardImage>();

	@Override
	public void addTo(final ConcordionExtender concordionExtender) {
		concordionExtender.withSpecificationProcessingListener(extension);
		concordionExtender.withBuildListener(extension);
		concordionExtender.withExampleListener(extension);
		concordionExtender.withAssertEqualsListener(extension);
		concordionExtender.withAssertTrueListener(extension);
		concordionExtender.withAssertFalseListener(extension);
		concordionExtender.withThrowableListener(extension);
		
		String path = StoryboardListener.class.getPackage().getName();
		path = path.replaceAll("\\.", "/");
		path = "/" + path;

		concordionExtender.withLinkedCSS(path + "/storyboard.css", new Resource("/storyboard.css"));
		concordionExtender.withLinkedJavaScript(path + "/storyboard.js", new Resource("/storyboard.js"));
		
		for (StockCardImage stockImage : StockCardImage.values()) {
			concordionExtender.withResource(path + stockImage.toString(), stockImage.getResource());
		}

		for (CustomCardImage image : customImages.values()) {
			concordionExtender.withResource(image.getSourcePath() + image.getFilename(), image.getResource());
		}
	}

	/**
	 * Add custom card image that will be shared by all tests, must be called before test starts otherwise this will do nothing
	 * 
	 * @param sourcePath Location of image
	 * @param filename Filename of image
	 */
	public void addCardImage(final String sourcePath, final String filename) {
		customImages.put(CustomCardImage.getKeyFromFileName(filename), new CustomCardImage(sourcePath, filename));
	}

	/**
	 * Get a previously added custom card image
	 * 
	 * @param filename Original filename of image
	 * @return CustomCardImage object
	 */
	public CustomCardImage getCardImage(final String filename) {
		return customImages.get(filename);
	}

	/**
	 * Set the title to display on the Storyboard and/or ExampleContainer
	 * 
	 * <p>This is a token effort at multi-language support.</p>
	 * 
	 * @param title The title
	 * @return A self reference
	 */
	public StoryboardExtension setTitle(String title) {
		extension.setTitle(title);
		return this;
	}

	/**
	 * Set how items are added to the storyboard
	 * 
	 * <ul>
	 * 	 <li>Append to end of Storyboard (original behaviour)</li>
	 * 	 <li>If item is added within currently executing Example Command append to example element, otherwise append to Storyboard <b>(default)</b></li>
	 *   <li>If item is added within currently executing Example Command append to new section container in Storyboard, otherwise append to Storyboard</li>
	 * </ul> 
	 * 
	 * @param appendMode The card append mode
	 * @return A self reference
	 */
	public StoryboardExtension setAppendMode(AppendTo appendMode) {
		extension.setAppendMode(appendMode);
		return this;
	}

		
	/**
	 * Sets whether a card will be added to the storyboard when an uncaught exception occurs in the test. Defaults to <b><code>true</code></b>. 
	 * If screenshotTaker is set then it will take a {@link ScreenshotCard}, else it will add a {@link NotificationCard}
	 * 
	 * @param value <code>true</code> to add a card when an uncaught exception occurs in the test, <code>false</code> to not add a card.
	 * @return A self reference
	 */
	public StoryboardExtension setAddCardOnThrowable(final boolean value) {
		extension.setAddCardOnThrowable(value);
		return this;
	}

	/**
	 * Sets whether a card will be added to the storyboard when a failure occurs in the test. Defaults to <b><code>true</code></b>. 
	 * If screenshotTaker is set then it will take a {@link ScreenshotCard}, else it will add a {@link NotificationCard}
	 * 
	 * @param value <code>true</code> to add a card when a failure occurs in the test, <code>false</code> to not add a card.
	 * @return A self reference
	 */
	public StoryboardExtension setAddCardOnFailure(final boolean value) {
		extension.setAddCardOnFailure(value);
		return this;
	}
	
	/**
	 * if {@link #setAddCardOnFailure(boolean)} is true then will optionally ignore second or subsequent failure.  If using the Example command then
	 * each example will be treated separately. 
	 * 
	 * @param value <code>true</code> to not add card on second, or subsequent, failure (default behaviour), <code>false</code> to add a card for every failure.
	 * @return A self reference
	 */
	public StoryboardExtension setSupressRepeatingFailures(final boolean value) {
		extension.setSupressRepeatingFailures(value);
		return this;
	}
	
	/**
	 * Check to see if a screenshot taker has been set
	 * 
	 * @return True if screenshot taker exists
	 */
	public boolean hasScreenshotTaker() {
		return extension.hasScreenshotTaker();
	}
	
	/**
	 * Set a screenshot taker. If not set then the storyboard extension cannot take screenshots.
	 * 
	 * For a non gui application this is the most appropriate behaviour and you will not want to change this.  For GUI applications you can either:
	 * <ul>
	 * <li>Use {@link RobotScreenshotTaker} which will take a shot of the full visible screen</li>
	 * <li>See the demo application for an example implementation of a custom <a href="https://github.com/concordion/concordion-storyboard-extension-demo/blob/master/src/test/java/org/concordion/ext/driver/web/SeleniumScreenshotTaker.java">SeleniumScreenshotTaker</a>
	 * </ul>
	 * 
	 * @param screenshotTaker Takes screenshots of the system under test
	 * @return A self reference
	 */
	public StoryboardExtension setScreenshotTaker(final ScreenshotTaker screenshotTaker) {
		extension.setScreenshotTaker(screenshotTaker);
		return this;
	}

	/**
	 * Removes the screenshot taker so no further screenshots will be taken.
	 * 
	 * @return A self reference
	 */
	public StoryboardExtension removeScreenshotTaker() {
		extension.setScreenshotTaker(null);
		return this;
	}
	
	/**
	 * When an example completes a screenshot of the current page will be automatically added to the storyboard - as long as 
	 * the screenshot taker has been set.
	 * 
	 * <p>If not using the example command then final screenshots must be added manually.</p>
	 * 
	 * @param value
	 * 			<code>true</code> to take screenshot (default), <code>false</code> to not take screenshot
	 * @return A self reference
	 */
	public StoryboardExtension setTakeScreenshotOnExampleCompletion(final boolean value) {
		extension.setTakeScreenshotOnExampleCompletion(value);
		return this;
	}
	
	/**
	 * If configured to take final screenshot for example (see {@link #setTakeScreenshotOnExampleCompletion(boolean)}), this
	 * will override that behvaiour for the current example only.
	 * 
	 * @return A self reference
	 */
	public StoryboardExtension setSkipFinalScreenshotForCurrentExample() {
		extension.setSkipFinalScreenshotForCurrentExample();
		return this;
	}
	
	/**
	 * Set to false to stop screenshot cards being added, this is useful in situations where might be looping and refreshing screen
	 * constantly and don't want to show too many screenshots of same screen in storyboard.
	 * 
	 * @param accept Accept screenshot setting
	 * @return A self reference
	 */
	public StoryboardExtension setAcceptsScreenshots(final boolean accept) {
		this.acceptsScreenShots = accept;
		return this;
	}
	
	/**
	 * Adds screenshot card to story board
	 * 
	 * @param title Card title
	 * @param description Card description
	 * @return A self reference
	 */
	public StoryboardExtension addScreenshot(final String title, final String description) {
		if (acceptsScreenShots) {
			ScreenshotCard card = new ScreenshotCard();
			card.setTitle(title);
			card.setDescription(description);
			card.setResult(CardResult.SUCCESS);

			extension.addCard(card);
		}
		return this;
	}

	/**
	 * Marks any screenshot cards added before this point to be deleted if tests completes successfully.
	 * If added within a section break then it will only apply to the screenshots in the section break.
	 * 
	 * @return A self reference
	 */
	public StoryboardExtension markPriorScreenshotsForRemoval() {
		extension.markPriorScreenshotsForRemoval();
		return this;
	}

	/**
	 * 
	 * @param title short description
	 * @param description card summary
	 * @param image StockCardImage or custom CardImage to display 
	 * @param result Success/failure status
	 * @return A self reference 
	 */
	public StoryboardExtension addNotification(final String title, final String description, final CardImage image, final CardResult result) {
		return addNotification(title, description, null, null, image, result);
	}
	
	/**
	 * Adds data/information card to story board
	 * 
	 * @param title short description
	 * @param description card summary
	 * @param data any data that may want to present to user when they click on it, can be empty, xml, json, etc
	 * @param image StockCardImage or custom CardImage to display 
	 * @param result Success/failure status
	 * @return A self reference
	 */
	public StoryboardExtension addNotification(final String title, final String description, final String data, final CardImage image, final CardResult result) {
		return addNotification(title, description, data, "", image, result);
	}

	/**
	 * Adds data/information card to story board
	 * 
	 * @param title short description
	 * @param description card summary
	 * @param data any data that may want to present to user when they click on it, can be empty, xml, json, etc
	 * @param fileExtension file extension of file to write data to, defaults to txt
	 * @param image StockCardImage or custom CardImage to display 
	 * @param result Success/failure status
	 * @return A self reference
	 */
	public StoryboardExtension addNotification(final String title, final String description, final String data, final String fileExtension, final CardImage image,
			final CardResult result) {
		NotificationCard card = new NotificationCard();
		card.setTitle(title);
		card.setDescription(description);
		card.setCardImage(image);
		card.setData(data);
		card.setFileExtension(fileExtension);
		card.setResult(result);

		extension.addCard(card);
		return this;
	}

	/**
	 * Adds a section container to the storyboard. If there is already a section container open 
	 * it will be closed before adding the new section container.
	 *
	 * @param title Title of the section break
	 * @return A self reference
	 */
	public StoryboardExtension addSectionContainer(String title) {
		if (title == null || title.isEmpty()) {
			extension.closeContainer();
		} else {
			SectionContainer container = new SectionContainer();
			container.setTitle(title);
			extension.addContainer(container);
		}
		
		return this;
	}
	
	/**
	 * Mark current container as completed, any cards added past this point will go to the parent container/storyboard.
	 * 
	 * @return A self reference
	 */
	public StoryboardExtension closeContainer() {
		extension.closeContainer();
		return this;
	}
	
	/**
	 * Add custom cards to the storyboard
	 * 
	 * @param card Custom card
	 * @return A self reference
	 */
	public StoryboardExtension addCard(final Card card) {
		extension.addCard(card);
		return this;
	}
	
	/**
	 * Add screenshot card to the storyboard
	 * 
	 * @param card Custom card
	 * @return A self reference
	 */
	public StoryboardExtension addCard(final ScreenshotCard card) {
		extension.addCard(card);
		return this;
	}
	
	/**
	 * Allow custom containers to be added the storyboard. If there is already a section container open 
	 * it will be closed before adding the new section container.
	 * 
	 * @param container Custom container
	 * @return A self reference
	 */
	public StoryboardExtension addContainer(final Container container) {
		if (container == null) {
			extension.closeContainer();
		} else {
			extension.addContainer(container);
		}
		
		return this;
	}
	
	/**
	 * Allow custom containers to be added the storyboard or current container if one is open.
	 * 
	 * @param container Custom container
	 * @return A self reference
	 */
	public StoryboardExtension addContainerToContainer(final Container container) {
		if (container == null) {
			extension.closeContainer();
		} else {
			extension.addContainerToContainer(container);
		}
		
		return this;
	}

	/**
	 * Get the title of the current example
	 * @return Title if executing an example, otherwise an empty string
	 */
	public String getCurrentExampleTitle() {
		return extension.getCurrentExampleTitle();
	}
	
	public enum AppendTo {
		/**
		 * Cards and Containers are added to the Storyboard (original behaviour)
		 */
		STORYBOARD,
		
		/**
		 * Cards and Containers are added to the Example if using the Example Command, otherwise added to the Storyboard (default behaviour)
		 */
		EXAMPLE,
		
		/**
		 *  If using the Example Command then any cards added during exection of the example will be added to a new Section Container on the Storyboard,
		 *  otherwise cards and containers are added to the Storyboard
		 */
		NEW_STORYBOARD_SECTION_PER_EXAMPLE
	}
}
