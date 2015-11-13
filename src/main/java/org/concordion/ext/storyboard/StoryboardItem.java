package org.concordion.ext.storyboard;

import org.concordion.api.Resource;
import org.concordion.api.Target;

/**
 * Base class for all cards that can be added to the storyboard
 */
public abstract class StoryboardItem {
	private String title = "";
	private CardResult result = CardResult.SUCCESS;
	protected StoryboardListener listener = null;
	protected Container container = null;

	protected void setStoryboardListener(final StoryboardListener listener) {
		this.listener = listener;
	}

	protected void setContainer(final Container container) {
		this.container = container;
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
	 * @return The unique number of this card
//	 */
//	protected String getItemIndex(StoryboardItem item) {
//		return String.valueOf(listener.getItemIndex(this)) + "-" + String.valueOf(items.indexOf(item));
//	}
//
//	protected String getCardIndex() {
//		return String.valueOf(listener.getItemIndex(this));
//	}

	
	/**
	 * @return The unique number of this card in the container
	 */
	protected String getItemIndex() {
		if (container != null) {
			return listener.getItemIndex(container) + "-" + String.valueOf(container.getCards().indexOf(this));
		} else {
			return String.valueOf(listener.getItemIndex(this));
		}
	}

	protected String getNextCardIndex() {
		if (container != null) {
			return listener.getItemIndex(container) + "-" + container.getCards().size();
		} else {
			return listener.getItemIndex(this);
		}
	}
}
