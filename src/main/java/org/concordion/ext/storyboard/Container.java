package org.concordion.ext.storyboard;

import java.util.ArrayList;
import java.util.List;
import org.concordion.api.Element;

/**
 * Base class for all containers that can be added to the storyboard.
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
	
	/**
	 * Should auto close when a new container is about to be added
	 * @return Is auto close on or not
	 */
	public abstract boolean isAutoClose();
	
	public abstract void writeTo(Element parent);

	public abstract Element getContentElement();

	public abstract Element getParentElement();

	public abstract void removeFromParent();

}
