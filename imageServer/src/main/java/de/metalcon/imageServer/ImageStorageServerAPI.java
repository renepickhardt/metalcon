package de.metalcon.imageServer;

import java.io.File;
import java.io.InputStream;

/**
 * prototype interface for the image storage server API
 * 
 * @author sebschlicht
 * 
 */
public interface ImageStorageServerAPI {

	/**
	 * create an image using a binary file
	 * 
	 * @param imageIdentifier
	 *            image identifier
	 * @param imageStream
	 *            image input stream
	 * @param imageInformation
	 *            JSON holding the image's meta data
	 * @param autoRotate
	 *            rotation flag - if set to <b>true</b> the server will rotate
	 *            the image using its EXIF data automatically
	 */
	void createImage(String imageIdentifier, InputStream imageStream,
			String imageInformation, boolean autoRotate);

	/**
	 * create a cropped image using a binary file
	 * 
	 * @param imageIdentifier
	 *            image identifier
	 * @param imageStream
	 *            image input stream
	 * @param imageInformation
	 *            JSON holding the image's meta data
	 * @param autoRotate
	 *            rotation flag - if set to <b>true</b> the server will rotate
	 *            the image using its EXIF data automatically
	 * @param left
	 *            distance between the old and the new left border of the image
	 * @param top
	 *            distance between the old and the new upper border of the image
	 * @param width
	 *            new image width
	 * @param height
	 *            new image height
	 */
	void createImage(String imageIdentifier, InputStream imageStream,
			String imageInformation, boolean autoRotate, int left, int right,
			int width, int height);

	/**
	 * create an image using a link to an existing image
	 * 
	 * @param imageIdentifier
	 *            image identifier
	 * @param imageUrl
	 *            URL to the existing image
	 * @param autoRotate
	 *            rotation flag - if set to <b>true</b> the server will rotate
	 *            the image using its EXIF data automatically
	 */
	void createImage(String imageIdentifier, String imageUrl, boolean autoRotate);

	/**
	 * read the image having new dimensions
	 * 
	 * @param imageIdentifier
	 *            image identifier
	 * @param width
	 *            alternate width
	 * @param height
	 *            alternate height
	 * @return file handle to the image version having the dimensions passed
	 */
	File readImage(String imageIdentifier, int width, int height);

	/**
	 * read the image in its original state
	 * 
	 * @param imageIdentifier
	 *            image identifier
	 * @return file handle to the original image
	 */
	File readImage(String imageIdentifier);

	/**
	 * read the meta data of an image
	 * 
	 * @param imageIdentifier
	 *            image identifier
	 * @return JSON holding the image's meta data
	 */
	String readImageInformation(String imageIdentifier);

	/**
	 * update the meta data of an image
	 * 
	 * @param imageIdentifier
	 *            image identifier
	 * @param key
	 *            key of the meta data value<br>
	 *            &emsp;<b>if the meta already has a value for this key the old
	 *            value will get overridden</b>
	 * @param value
	 *            meta data value that shall be appended
	 */
	void appendImageInformation(String imageIdentifier, String key, String value);

	/**
	 * delete an image from the server
	 * 
	 * @param imageIdentifier
	 *            image identifier
	 */
	void deleteImage(String imageIdentifier);

}