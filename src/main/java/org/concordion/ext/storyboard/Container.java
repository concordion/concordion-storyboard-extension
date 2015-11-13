package org.concordion.ext.storyboard;

import java.util.ArrayList;
import java.util.List;

import org.concordion.api.Element;

/**
 * Base class for all items that can contain cards
 */
public abstract class Container extends StoryboardItem {
	List<StoryboardItem> cards = new ArrayList<StoryboardItem>();
	
	public void addCard(Card card) {
		cards.add(card);
	}

	public List<StoryboardItem> getCards() {
		return cards;
	}
	
	public abstract void addCardsToSpecification();

	public abstract Element addContainerToSpecification(Element storyboard);

	
}
