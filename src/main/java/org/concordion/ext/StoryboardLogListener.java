package org.concordion.ext;

import java.io.ByteArrayOutputStream;

import org.concordion.ext.storyboard.CardResult;
import org.concordion.ext.storyboard.NotificationCard;
import org.concordion.ext.storyboard.ScreenshotCard;
import org.concordion.logback.LoggingListener;
import org.concordion.slf4j.markers.AttachmentMarker;
import org.concordion.slf4j.markers.ReportLoggerMarkers;
import org.concordion.slf4j.markers.ScreenshotMarker;
import org.concordion.slf4j.markers.ThrowableCaughtMarker;
import org.slf4j.Marker;

import ch.qos.logback.classic.spi.ILoggingEvent;

public class StoryboardLogListener extends LoggingListener {
	ByteArrayOutputStream stream = new ByteArrayOutputStream();

	public final StoryboardExtension storyboard;

	public StoryboardLogListener(StoryboardExtension storyboard) {
		this.storyboard = storyboard;
	}

	@Override
	protected void append(ILoggingEvent event) {
		processThrowableMarker(event);
		processStoryboardMarker(event);
	}

	private void processThrowableMarker(ILoggingEvent event) {
		Marker marker = findMarker(event.getMarker(), ReportLoggerMarkers.THROWABLE_CAUGHT_MARKER_NAME);
		
		if (marker != null) {
			ThrowableCaughtMarker throwableMarker = (ThrowableCaughtMarker) marker;
			
			storyboard.getListener().addThrowable(throwableMarker.getThrowable());
		}
	}

	private void processStoryboardMarker(ILoggingEvent event) {
		Marker marker = findMarker(event.getMarker(), StoryboardMarkerFactory.STORYBOARD_MAKRER_NAME);
		if (marker == null) {
			return;
		}

		StoryboardMarker storyboardMarker = (StoryboardMarker) marker;
		Marker dataMarker = findMarker(event.getMarker(), ReportLoggerMarkers.DATA_MARKER_NAME);

		if (dataMarker != null) {
			if (dataMarker instanceof ScreenshotMarker) {
				ScreenshotMarker screenshotMarker = (ScreenshotMarker) dataMarker;

				ScreenshotCard card = new ScreenshotCard();
				card.setTitle(storyboardMarker.getTitle());
				card.setDescription(event.getFormattedMessage());
				card.setResult(CardResult.SUCCESS);
				card.setImageName(screenshotMarker.getFile(), screenshotMarker.getImageSize());

				storyboard.addCard(card);
			}

			if (dataMarker instanceof AttachmentMarker) {
				AttachmentMarker attachmentMarker = (AttachmentMarker) dataMarker;

				NotificationCard card = new NotificationCard();
				card.setTitle(storyboardMarker.getTitle());
				card.setDescription(event.getFormattedMessage());
				card.setCardImage(storyboardMarker.getCardImage());
				card.setFilePath(attachmentMarker.getFile());
				card.setResult(CardResult.SUCCESS);

				storyboard.addCard(card);
			}
		}
	}

	@Override
	public String[] getFilterMarkers() {
		return new String[] { StoryboardMarkerFactory.STORYBOARD_MAKRER_NAME, ReportLoggerMarkers.THROWABLE_CAUGHT_MARKER_NAME };
	}

	public String getStreamContent() {
		return stream.toString();
	}

	public void resetStream() {
		stream.reset();
	}
}
