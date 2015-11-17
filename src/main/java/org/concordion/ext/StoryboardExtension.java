package org.concordion.ext;

import java.awt.Robot;
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
		concordionExtender.withResource(path + StockCardImage.EMAIL, StockCardImage.EMAIL.getResource());
		concordionExtender.withResource(path + StockCardImage.HTML, StockCardImage.HTML.getResource());
		concordionExtender.withResource(path + StockCardImage.TEXT, StockCardImage.TEXT.getResource());		
		concordionExtender.withResource(path + StockCardImage.XML_REQUEST, StockCardImage.XML_REQUEST.getResource());
		concordionExtender.withResource(path + StockCardImage.XML_RESPONSE, StockCardImage.XML_RESPONSE.getResource());
		concordionExtender.withResource(path + StockCardImage.ERROR, StockCardImage.ERROR.getResource());

		for (CustomCardImage image : customImages.values()) {
			concordionExtender.withResource(image.getSourcePath() + image.getFilename(), image.getResource());
		}
	}

	/**
	 * Add custom card image that will be shared by all tests, must be called before test starts otherwise this will do nothing
	 */
	public void addCardImage(final String sourcePath, final String filename) {
		customImages.put(CustomCardImage.getKeyFromFileName(filename), new CustomCardImage(sourcePath, filename));
	}

	/**
	 * Get a previously added custom card image
	 * 
	 * @param filename
	 * @return
	 */
	public CustomCardImage getCardImage(final String filename) {
		return customImages.get(filename);
	}

	/**
	 * Sets whether a card will be added to the storyboard when an uncaught exception occurs in the test. Defaults to <b><code>true</code></b>. 
	 * If screenshotTaker is set then it will take a {@link ScreenshotCard}, else it will add a {@link NotificationCard}
	 * 
	 * @param value
	 *            <code>true</code> to add a card when an uncaught exception occurs in the test, <code>false</code> to not add a card.
	 */
	public StoryboardExtension setAddCardOnThrowable(final boolean value) {
		extension.setAddCardOnThrowable(value);
		return this;
	}

	/**
	 * Sets whether a card will be added to the storyboard when a failure occurs in the test. Defaults to <b><code>true</code></b>. 
	 * If screenshotTaker is set then it will take a {@link ScreenshotCard}, else it will add a {@link NotificationCard}
	 * 
	 * @param value
	 *            <code>true</code> to add a card when a failure occurs in the test, <code>false</code> to not add a card.
	 */
	public StoryboardExtension setAddCardOnFailure(final boolean value) {
		extension.setAddCardOnFailure(value);
		return this;
	}
	
	/**
	 * Set a screenshot taker. If not set, the extension will default to using {@link Robot} which will take a shot of the full visible screen.
	 * 
	 * For a non gui application this won't be the most appropriate behaviour and you will want to set the screenshot taker to null.
	 * See the demo application for an example of a custom SeleniumScreenshotTaker.
	 * 
	 * @param screenshotTaker
	 */
	public StoryboardExtension setScreenshotTaker(final ScreenshotTaker screenshotTaker) {
		extension.setScreenshotTaker(screenshotTaker);
		return this;
	}

	/**
	 * When the test, or example completes the storyboard will add a screenshot of the current screen.
	 * 
	 * This assumes that the ScreenshotTaker is not null, and in the case of the example command: automatically 
	 * adding section breaks has not been turned off.
	 * 
	 * @param value
	 * 			<code>true</code> to take screenshot (default), <code>false</code> to not take screenshot
	 */
	public StoryboardExtension setTakeScreenshotOnCompletion(final boolean value) {
		extension.setTakeScreenshotOnCompletion(value);
		return this;
	}

	/**
	 * By default a section break will be added to the storyboard each time the example command is encountered.  
	 * 
	 * The title for the section break will either be first header found in the example section (searched in order h1...h5), if a 
	 * header is not found the name of the section break will be used>
	 * 
	 * @param value
	 */
	public StoryboardExtension setAutoAddSectionForExample(boolean value) {
		extension.setAutoAddSectionForExample(value);
		return this;
	}
	
	/**
	 * If using the ExampleCommand add cards to the end of the example rather than placing in the 
	 * storyboard section at the bottom of the specification
	 * 
	 * @param value
	 */
	public StoryboardExtension setAddCardsToExample(boolean value) {
		extension.setAddCardsToExample(value);
		return this;
	}
	
	/**
	 * Set to false to stop screenshot cards being added, this is useful in situations where might be looping and refreshing screen constantly and don't want to
	 * show many screenshots of same screen in storyboard
	 * 
	 * @param accept
	 */
	public void setAcceptsScreenshots(final boolean accept) {
		this.acceptsScreenShots = accept;
	}
	
	/**
	 * Adds screenshot card to story board
	 * 
	 * @param title
	 * @param description
	 */
	public void addScreenshot(final String title, final String description) {
		if (acceptsScreenShots) {
			ScreenshotCard card = new ScreenshotCard();
			card.setTitle(title);
			card.setDescription(description);
			card.setResult(CardResult.SUCCESS);

			extension.addCard(card);
		}
	}

	/**
	 * Marks any screenshot cards added before this point to be deleted if tests completes successfully.
	 * If added within a section break then it will only apply to the screenshots in the section break.
	 */
	public void setRemovePriorScreenshotsOnSuccess() {
		extension.setRemovePriorScreenshotsOnSuccess();
	}

	/**
	 * 
	 * @param title
	 *            short description
	 * @param description
	 *            card summary
	 * @param image
	 * 			StockCardImage or custom CardImage to display 
	 * @param result
	 * 			Success/failure status 
	 */
	public void addNotification(final String title, final String description, final CardImage image, final CardResult result) {
		addNotification(title, description, null, null, image, result);
	}
	
	/**
	 * Adds data/information card to story board
	 * 
	 * @param title
	 *            short description
	 * @param description
	 *            card summary
	 * @param data
	 *            any data that may want to present to user when they click on it, can be empty, xml, json, etc
	 * @param image
	 * 			StockCardImage or custom CardImage to display 
	 * @param result
	 * 			Success/failure status
	 */
	public void addNotification(final String title, final String description, final String data, final CardImage image, final CardResult result) {
		addNotification(title, description, data, "", image, result);
	}

	/**
	 * Adds data/information card to story board
	 * 
	 * @param title
	 *            short description
	 * @param description
	 *            card summary
	 * @param data
	 *            any data that may want to present to user when they click on it, can be empty, xml, json, etc
	 * @param fileExtension
	 *            file extension of file to write data to, defaults to txt
	 * @param image
	 * 			StockCardImage or custom CardImage to display 
	 * @param result
	 * 			Success/failure status
	 */
	public void addNotification(final String title, final String description, final String data, final String fileExtension, final CardImage image,
			final CardResult result) {
		NotificationCard card = new NotificationCard();
		card.setTitle(title);
		card.setDescription(description);
		card.setCardImage(image);
		card.setData(data);
		card.setFileExtension(fileExtension);
		card.setResult(result);

		extension.addCard(card);
	}

	/**
	 * Adds a section break to the storyboard
	 *
	 * @param title 
	 * 		Title of the section break, pass in null or empty string to close
	 * 	 	the section break and add subsequent cards to the storyboard
	 */
	public void addSectionBreak(String title) {
		SectionContainer container = null;
		
		if (title != null && !title.isEmpty()) {
			container = new SectionContainer();
			container.setTitle(title);
		}
		
		extension.addContainer(container);
	}
	
	/**
	 * Allow custom cards to be passed into storyboard
	 * 
	 * @param card
	 */
	public void addCard(final Card card) {
		extension.addCard(card);
	}
	public void addCard(final ScreenshotCard card) {
		extension.addCard(card);
	}
	
	/**
	 * Allow custom containers to be passed into storyboard
	 * 
	 * @param card
	 */
	public void addContainer(final Container container) {
		extension.addContainer(container);
	}

	/**
	 * Get the title of the current example
	 * @return Title if executing an example, otherwise an empty string
	 */
	public String getExampleTitle() {
		return extension.getExampleTitle();
	}

	public StoryboardExtension setwho(String string) {
		extension.whoami = string;
		return this;
	}
}
