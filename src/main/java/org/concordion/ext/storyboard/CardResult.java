package org.concordion.ext.storyboard;

/**
 * Success/failure levels, these also map to styles in storyboard.css 
 */
public enum CardResult {
	SUCCESS("scsuccess"),
	WARN("scwarning"),
	FAILURE("scfailure");

	private final String key;

	CardResult(final String key) {
		this.key = key;
	}

	protected String getKey() {
		return key;
	}
}
