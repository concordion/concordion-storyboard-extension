package org.concordion.ext.storyboard;

import org.concordion.api.Resource;
import org.concordion.api.Target;

/**
 * Base class for all cards that can be added to the storyboard
 */
public abstract class StoryboardItem {
	private String title = "";
	private CardResult result = CardResult.SUCCESS;
	protected StoryboardListener listener;

	protected void setStoryboardListener(final StoryboardListener listener) {
		this.listener = listener;
	}

	protected Resource getResource() {
		return listener.getResource();
	}

	protected Target getTarget() {
		return listener.getTarget();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public CardResult getResult() {
		return result;
	}

	public void setResult(final CardResult result) {
		this.result = result;
	}
	
	/**
	 * @return The unique number of this card on the storyboard
	 */
	protected int getCardIndex() {
		return listener.getCardIndex(this);
	}

	/**
	 * @return The unique number of this card on the storyboard
	 */
	protected int getNextCardIndex() {
		return listener.getNextCardIndex();
	}
}
