package org.concordion.ext.storyboard;

import org.concordion.api.Resource;

public enum CardImage {
	XML_REQUEST("/xmlrequest.png"),
	XML_RESPONSE("/xmlresponse.png"),
	EXPAND("/expand.png"),
	COLLAPSE("/collapse.png"),
	ERROR("/error.png"),
	COMPLETE("/complete.png");

	private final String key;

	CardImage(final String key) {
		this.key = key;
	}

	protected String getKey() {
		return key;
	}

	@Override
	public String toString() {
		return key;
	}

	public Resource getResource() {
		return new Resource(key);
	}
}
