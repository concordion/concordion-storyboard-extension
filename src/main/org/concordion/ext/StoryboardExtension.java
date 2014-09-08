package org.concordion.ext;

import java.awt.Robot;

import org.concordion.api.Resource;
import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;
import org.concordion.ext.storyboard.CardImage;
import org.concordion.ext.storyboard.CardResult;
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
		concordionExtender.withResource(path + CardImage.XML_REQUEST, CardImage.XML_REQUEST.getResource());
		concordionExtender.withResource(path + CardImage.XML_RESPONSE, CardImage.XML_RESPONSE.getResource());
		concordionExtender.withResource(path + CardImage.EXPAND, CardImage.EXPAND.getResource());
		concordionExtender.withResource(path + CardImage.COLLAPSE, CardImage.COLLAPSE.getResource());
		concordionExtender.withResource(path + CardImage.COMPLETE, CardImage.COMPLETE.getResource());
		concordionExtender.withResource(path + CardImage.ERROR, CardImage.ERROR.getResource());
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
	 * @param summary
	 * @param description
	 */
	public void addScreenshot(final String summary, final String description) {
		if (acceptsScreenShots) {
			extension.addScreenshotCard(summary, description, CardResult.SUCCESS);
		}
	}

	/**
	 * Adds xml card to story board
	 * 
	 * @param summary
	 * @param description
	 * @param xml
	 */
	public void addNotification(final String summary, final String description, final String xml, final CardImage image, final CardResult result) {
		extension.addNotificationCard(summary, description, xml, image, result);
	}

	/**
	 * Adds collapsible section card to story board
	 * 
	 * @param summary
	 *            Must be unique for each collapsilbe group added
	 */
	public void startCollapsibleGroup(final String summary) {
		extension.startCollapsibleGroup(summary);
	}

	/**
	 * Wraps all story cards from startCollapsibleGroup() call to last added card in a collapse/expand region (defaults to collapsed)
	 */
	public void stopCollapsibleGroup() {
		extension.stopCollapsibleGroup(CardResult.SUCCESS);
	}
}
