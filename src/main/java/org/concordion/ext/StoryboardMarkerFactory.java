package org.concordion.ext;

import org.concordion.ext.storyboard.CardImage;
import org.concordion.ext.storyboard.CardResult;
import org.concordion.ext.storyboard.StockCardImage;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class StoryboardMarkerFactory {
	static final String STORYBOARD_MAKRER_NAME = "STORYBOARD";
	static final Marker STORYBOARD_MAKRER = MarkerFactory.getMarker(STORYBOARD_MAKRER_NAME);

	private StoryboardMarkerFactory() {}

	public static StoryboardMarker addCard(String title) {
		return new StoryboardMarker(title, StockCardImage.TEXT, CardResult.SUCCESS);
	}

	public static StoryboardMarker addCard(String title, CardImage cardImage) {
		return new StoryboardMarker(title, cardImage, CardResult.SUCCESS);
	}

	public static StoryboardMarker addCard(String title, CardImage cardImage, CardResult cardResult) {
		return new StoryboardMarker(title, cardImage, cardResult);
	}

}

