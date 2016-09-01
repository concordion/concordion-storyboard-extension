package org.concordion.ext;

import org.concordion.ext.storyboard.CardImage;
import org.concordion.ext.storyboard.CardResult;
import org.slf4j.helpers.ConcordionMarker;

public class StoryboardMarker extends ConcordionMarker {
	private static final long serialVersionUID = 9114866777936384119L;

	private final String title;
	private final String description;
	private final CardImage cardImage;
	private final CardResult cardResult;

	public StoryboardMarker(String title, String description, CardImage cardImage, CardResult cardResult) {
		super(StoryboardMarkerFactory.STORYBOARD_MAKRER_NAME);

		this.title = title;
		this.description = description;
		this.cardImage = cardImage;
		this.cardResult = cardResult;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}
	
	public CardImage getCardImage() {
		return cardImage;
	}

	public CardResult getCardResult() {
		return cardResult;
	}

}