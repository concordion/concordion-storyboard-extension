package org.concordion.ext;

import java.io.ByteArrayOutputStream;

import org.concordion.ext.storyboard.CardImage;
import org.concordion.ext.storyboard.CardResult;
import org.concordion.ext.storyboard.NotificationCard;
import org.concordion.ext.storyboard.ScreenshotCard;
import org.concordion.ext.storyboard.StockCardImage;
import org.concordion.logback.LoggingListener;
import org.concordion.slf4j.markers.AttachmentMarker;
import org.concordion.slf4j.markers.ReportLoggerMarkers;
import org.concordion.slf4j.markers.ScreenshotMarker;
import org.slf4j.Marker;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;

public class StoryboardLogListener extends LoggingListener {
	ByteArrayOutputStream stream = new ByteArrayOutputStream();

	public final StoryboardExtension storyboard;

	public StoryboardLogListener(StoryboardExtension storyboard) {
		this.storyboard = storyboard;
		this.storyboard.getListener().setUseLogListener();
	}

	@Override
	protected void append(ILoggingEvent event) {
		Marker marker = findMarker(event.getMarker(), StoryboardMarkerFactory.STORYBOARD_MAKRER_NAME);
		Marker dataMarker = findMarker(event.getMarker(), ReportLoggerMarkers.DATA_MARKER_NAME);
		String title = getTitle(marker, event);

		if (dataMarker != null) {
			if (dataMarker instanceof ScreenshotMarker) {
				ScreenshotMarker screenshotMarker = (ScreenshotMarker) dataMarker;

				ScreenshotCard card = new ScreenshotCard();
				card.setTitle(title);
				card.setDescription(event.getFormattedMessage());
				card.setResult(CardResult.SUCCESS);
				card.setImageName(screenshotMarker.getFile(), screenshotMarker.getImageSize());

				storyboard.addCard(card);
			}

			if (dataMarker instanceof AttachmentMarker) {
				AttachmentMarker attachmentMarker = (AttachmentMarker) dataMarker;

				NotificationCard card = new NotificationCard();
				card.setTitle(title);
				card.setDescription(event.getFormattedMessage());
				card.setCardImage(getCardImage(marker, event));
				card.setFilePath(attachmentMarker.getFile());
				card.setResult(CardResult.SUCCESS);

				storyboard.addCard(card);
			}
		} else {
			NotificationCard card = new NotificationCard();
			card.setTitle(title);
			card.setDescription("See specification for further information");
			card.setCardImage(StockCardImage.ERROR);
			card.setResult(CardResult.FAILURE);

			storyboard.addCard(card);
		}
	}

	private String getTitle(Marker marker, ILoggingEvent event) {
		String title;

		if (marker instanceof StoryboardMarker) {
			StoryboardMarker storyboardMarker = (StoryboardMarker) marker;
			title = storyboardMarker.getTitle();
		} else {
			IThrowableProxy cause = event.getThrowableProxy();
			if (cause == null) {
				title = "Test Failed";
			} else {
				if (cause.getCause() != null) {
					cause = cause.getCause();
				}

				title = cause.getClassName();
				int index = title.lastIndexOf(".");
				if (index > 0) {
					title = title.substring(index + 1);
				}
			}
		}

		return title;
	}

	private CardImage getCardImage(Marker marker, ILoggingEvent event) {
		if (marker instanceof StoryboardMarker) {
			StoryboardMarker storyboardMarker = (StoryboardMarker) marker;
			return storyboardMarker.getCardImage();
		} else {
			return StockCardImage.ERROR;
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
		return StoryboardMarkerFactory.STORYBOARD_MAKRER;
	}

}
