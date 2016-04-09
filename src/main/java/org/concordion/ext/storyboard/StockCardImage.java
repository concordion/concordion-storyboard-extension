package org.concordion.ext.storyboard;

import org.concordion.api.Resource;

/**
 * A list of the stock images that can be assigned to a card
 */
public enum StockCardImage implements CardImage {
	CSV("/csv.png"),
	EMAIL("/email.png"),
	ERROR("/error.png"),
	HTML("/html.png"),
	JSON("/json.png"),
	TEXT("/txt.png"),
	XML("/xml.png"),
	XML_REQUEST("/xmlrequest.png"),
	XML_RESPONSE("/xmlresponse.png");
	
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
