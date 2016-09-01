package org.concordion.ext;

import java.io.ByteArrayOutputStream;

import org.concordion.ext.storyboard.NotificationCard;
import org.concordion.ext.storyboard.ScreenshotCard;
import org.concordion.logback.LoggingListener;
import org.concordion.slf4j.markers.AttachmentMarker;
import org.concordion.slf4j.markers.ReportLoggerMarkers;
import org.concordion.slf4j.markers.ScreenshotMarker;
import org.slf4j.Marker;

import ch.qos.logback.classic.spi.ILoggingEvent;

public class StoryboardLogListener extends LoggingListener {
	ByteArrayOutputStream stream = new ByteArrayOutputStream();

	public final StoryboardExtension storyboard;

	public StoryboardLogListener(StoryboardExtension storyboard) {
		this.storyboard = storyboard;
		this.storyboard.getListener().setUseLogListener();
	}

	@Override
	protected void append(ILoggingEvent event) {
		StoryboardMarker storyboardMarker = (StoryboardMarker) findMarker(event.getMarker(), StoryboardMarkerFactory.STORYBOARD_MAKRER_NAME);
		Marker dataMarker = findMarker(event.getMarker(), ReportLoggerMarkers.DATA_MARKER_NAME);
		String title = storyboardMarker.getTitle();
		String description = storyboardMarker.getDescription();
		
		if (description.isEmpty()) {
			description = event.getFormattedMessage();
		}
		
		if (title.isEmpty()) {
			title = description;
			description = "";
		}

		if (dataMarker instanceof ScreenshotMarker) {
			ScreenshotMarker screenshotMarker = (ScreenshotMarker) dataMarker;

			ScreenshotCard card = new ScreenshotCard();
			card.setTitle(title);
			card.setDescription(description);
			card.setResult(storyboardMarker.getCardResult());
			card.setImageName(screenshotMarker.getFile(), screenshotMarker.getImageSize());

			storyboard.addCard(card);
		} else {
			NotificationCard card = new NotificationCard();
			card.setTitle(title);
			card.setDescription(description);
			card.setCardImage(storyboardMarker.getCardImage());
			card.setResult(storyboardMarker.getCardResult());

			if (dataMarker instanceof AttachmentMarker) {
				AttachmentMarker attachmentMarker = (AttachmentMarker) dataMarker;
				card.setFilePath(attachmentMarker.getFile());
			}
			
			storyboard.addCard(card);
		}
	}

	@Override
	public String[] getFilterMarkers() {
		return new String[] { 
				StoryboardMarkerFactory.STORYBOARD_MAKRER_NAME
		};
	}

	@Override
	public Marker getConcordionEventMarker() {
		return null;
	}

	@Override
	public boolean getHandleFailureAndThrowableEvents() {
		return false;
	}

}
