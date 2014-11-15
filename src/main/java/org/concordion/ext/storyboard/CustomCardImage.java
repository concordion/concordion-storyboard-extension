package org.concordion.ext.storyboard;

import org.concordion.api.Resource;

public class CustomCardImage implements CardImage {
	private final String filename;
	private final String sourcePath;

	public CustomCardImage(String sourcePath, String filename) {
		if (sourcePath.endsWith("/")) {
			sourcePath = sourcePath.substring(0, sourcePath.length() - 1);
		}

		if (!filename.startsWith("/")) {
			filename = "/" + filename;
		}

		this.filename = filename;
		this.sourcePath = sourcePath;
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public String getFilename() {
		return filename;
	}

	@Override
	public Resource getResource() {
		return new Resource(filename);
	}

	@Override
	public String toString() {
		return filename;
	}

	public static String getKeyFromFileName(final String fileName) {
		return fileName.replaceAll("/", "");
	}
}
