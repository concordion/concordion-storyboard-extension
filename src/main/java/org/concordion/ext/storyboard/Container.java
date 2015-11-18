package org.concordion.ext.storyboard;

import java.util.ArrayList;
import java.util.List;
import org.concordion.api.Element;

/**
 * Base class for all items that can contain cards
 */
public abstract class Container extends StoryboardItem {
	List<StoryboardItem> items = new ArrayList<StoryboardItem>();
	
	public void addItem(StoryboardItem card) {
		items.add(card);
		
		if (card.getResult() != CardResult.SUCCESS) {
			setResult(card.getResult());
		}
	}

	public List<StoryboardItem> getItems() {
		return items;
	}
	
	public String getItemIndex(StoryboardItem item) {
		String index;
		
		if (container != null) {
			index = container.getItemIndex(this) + "-";
		} else {
			index = listener.getItemIndex(this) + "-";
		}
		
		return index += String.valueOf(items.indexOf(item));
	}
	
	public abstract void writeTo(Element parent);

	public abstract Element getElement();
	
	public abstract Element getContentElement();

	public abstract Element getParentElement();

}
