package org.concordion.ext.storyboard;

import org.concordion.api.Resource;

/**
 * A list of the stock images that can be assigned to a card
 */
public enum StockCardImage implements CardImage {
	EMAIL("/email.png"),	
	HTML("/html.png"),
	TEXT("/text.png"),
	XML_REQUEST("/xmlrequest.png"),
	XML_RESPONSE("/xmlresponse.png"),
	ERROR("/error.png");

	private final String key;

	private StockCardImage(final String key) {
		this.key = key;
	}

	protected String getKey() {
		return key;
	}

	@Override
	public Resource getResource() {
		return new Resource(key);
	}

	@Override
	public String toString() {
		return key;
	}
}
