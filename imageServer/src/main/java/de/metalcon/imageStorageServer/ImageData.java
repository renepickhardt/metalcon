package de.metalcon.imageStorageServer;

import java.io.InputStream;

/**
 * wrapper for image data: meta data and image
 * 
 * @author sebschlicht
 * 
 */
public class ImageData {

	/**
	 * meta data of the image
	 */
	private final String metaData;

	/**
	 * stream for the image
	 */
	private final InputStream imageStream;

	/**
	 * create a new image data object
	 * 
	 * @param metaData
	 *            meta data of the image
	 * @param imageStream
	 *            stream for the image
	 */
	public ImageData(final String metaData, final InputStream imageStream) {
		this.metaData = metaData;
		this.imageStream = imageStream;
	}

	/**
	 * @return meta data of the image
	 */
	public String getMetaData() {
		return this.metaData;
	}

	/**
	 * @return stream for the image
	 */
	public InputStream getImageStream() {
		return this.imageStream;
	}

}