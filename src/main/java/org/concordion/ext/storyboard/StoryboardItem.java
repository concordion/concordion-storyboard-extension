package org.concordion.ext.storyboard;

import org.concordion.api.Resource;
import org.concordion.api.Target;

/**
 * Base class for all cards and containers that can be added to the storyboard
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
		
		if (container != null) {
			container.setResult(result);
		}
	}
	
	/**
	 * @return The unique number of this card in the container
	 */
	protected String getItemIndex() {
		String index;
		
		if (container != null) {
			index = container.getItemIndex(this);
		} else {
			index = listener.getItemIndex(this);
		}
		
		return index;
	}
}
