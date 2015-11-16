package org.concordion.ext.storyboard;

import java.util.ArrayList;
import java.util.List;
import org.concordion.api.Element;

/**
 * Base class for all items that can contain cards
 */
public abstract class Container extends StoryboardItem {
	List<StoryboardItem> items = new ArrayList<StoryboardItem>();
	
	public void addCard(Card card) {
		items.add(card);
	}

	public List<StoryboardItem> getCards() {
		return items;
	}
	
	public String getItemIndex(StoryboardItem item) {
		return String.valueOf(items.indexOf(item));
	}
	
	public abstract void appendToParent();

	public abstract Element getElement();
	
	public abstract Element getContentElement();

	public abstract Element getParentElement();

}
