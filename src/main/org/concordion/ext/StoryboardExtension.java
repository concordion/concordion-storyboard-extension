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
import org.concordion.ext.storyboard.CollapsibleStartCard;
import org.concordion.ext.storyboard.CollapsibleStopCard;
import org.concordion.ext.storyboard.CustomCardImage;
import org.concordion.ext.storyboard.NotificationCard;
import org.concordion.ext.storyboard.ScreenshotCard;
import org.concordion.ext.storyboard.StockCardImage;
import org.concordion.ext.storyboard.StoryboardListener;

/**
 * Adds screenshots to the Concordion output, typically when failures or exceptions occur. The screenshot is displayed when you hover over the relevant element.
 * Clicking on the element will open the image in the current browser window.
 * <p>
 * It can also be used as a command, to explicitly include screenshots in the output.
 * <p>
 * By default, the screenshot will be of the full visible screen. This can be overridden using a custom {@link ScreenshotTaker}.
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
	 * 
	 * @param imageName
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
	 * Set a custom screenshot taker. If not set, the extension will default to using {@link Robot} which will take a shot of the full visible screen.
	 * 
	 * @param screenshotTaker
	 */
	public StoryboardExtension setScreenshotTaker(final ScreenshotTaker screenshotTaker) {
		extension.setScreenshotTaker(screenshotTaker);
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
	 * Sets whether screenshots will be embedded in the output when uncaught Throwables occur in the test. Defaults to <b><code>true</code></b>.
	 * 
	 * @param takeShot
	 *            <code>true</code> to take screenshots on uncaught Throwables, <code>false</code> to not take screenshots.
	 */
	public StoryboardExtension setScreenshotOnThrowable(final boolean takeShot) {
		extension.setAddCardOnThrowable(takeShot);
		return this;
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
	 * Adds collapsible section card to story board
	 * 
	 * @param title
	 *            Must be unique for each collapsible group added
	 */
	public void startCollipsableGroup(final String title) {
		CollapsibleStartCard card = new CollapsibleStartCard();
		card.setTitle(title);

		extension.addCard(card);
	}

	/**
	 * Wraps all story cards from startCollapsibleGroup() call to last added card in a collapse/expand region (defaults to collapsed)
	 */
	public void stopCollapsibleGroup() {
		extension.addCard(new CollapsibleStopCard());
	}

	/**
	 * Allow customs cards to be passed into storyboard
	 * 
	 * @param card
	 */
	public void addCard(final Card card) {
		extension.addCard(card);
	}

}
