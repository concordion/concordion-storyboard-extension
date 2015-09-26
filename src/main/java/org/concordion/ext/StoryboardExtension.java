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
import org.concordion.ext.storyboard.GroupStartCard;
import org.concordion.ext.storyboard.GroupStopCard;
import org.concordion.ext.storyboard.CustomCardImage;
import org.concordion.ext.storyboard.NotificationCard;
import org.concordion.ext.storyboard.ScreenshotCard;
import org.concordion.ext.storyboard.SectionBreak;
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
		concordionExtender.withResource(path + StockCardImage.EXPAND, StockCardImage.EXPAND.getResource());
		concordionExtender.withResource(path + StockCardImage.COLLAPSE, StockCardImage.COLLAPSE.getResource());
		concordionExtender.withResource(path + StockCardImage.COMPLETE, StockCardImage.COMPLETE.getResource());
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
	 * When the test completes and the ScreenshotTaker is not null, the storyboard will take a screenshot of the current screen.
	 * 
	 * @param value
	 * 			<code>true</code> to take screenshot (default), <code>false</code> to not take screenshot
	 */
	public StoryboardExtension setTakeScreenshotOnCompletion(final boolean value) {
		extension.setTakeScreenshotOnCompletion(value);
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

	public void setRemovePriorScreenshotsOnSuccess() {
		extension.setRemovePriorScreenshotsOnSuccess();
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
	 * Adds collapsible group card to story board
	 * 
	 * @param title
	 *            Must be unique for each group added
	 */
	public void startGroup(final String title) {
		GroupStartCard card = new GroupStartCard();
		card.setTitle(title);

		extension.addCard(card);
	}

	/**
	 * Wraps all story cards from startGroup() call to last added card in a collapse/expand region (defaults to collapsed)
	 */
	public void stopGroup() {
		extension.addCard(new GroupStopCard());
	}

	/**
	 * Wraps all story cards from startGroup() call to last added card in a collapse/expand region (defaults to collapsed)
	 */
	public void addSectionBreak(String title) {
		SectionBreak card = new SectionBreak();
		card.setTitle(title);

		extension.addCard(card);
	}
	
	/**
	 * Allow customs cards to be passed into storyboard
	 * 
	 * @param card
	 */
	public void addCard(final Card card) {
		extension.addCard(card);
	}
	public void addCard(final ScreenshotCard card) {
		extension.addCard(card);
	}
	public void addCard(final GroupStartCard card) {
		extension.addCard(card);
	}
	public void addCard(final GroupStopCard card) {
		extension.addCard(card);
	}
	public void addCard(final SectionBreak card) {
		extension.addCard(card);
	}
}
