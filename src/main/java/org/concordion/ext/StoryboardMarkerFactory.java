package org.concordion.ext;

import org.concordion.ext.storyboard.CardImage;
import org.concordion.ext.storyboard.CardResult;
import org.concordion.ext.storyboard.StockCardImage;

public class StoryboardMarkerFactory {
	static final String STORYBOARD_MAKRER_NAME = "STORYBOARD";
	static final String STORYBOARD_CONTAINER_MAKRER_NAME = "STORYBOARD_CONTAINER";

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

