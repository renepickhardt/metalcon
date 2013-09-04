package de.metalcon.imageServer;

import java.io.InputStream;

import de.metalcon.imageServer.protocol.create.CreateResponse;
import de.metalcon.imageServer.protocol.delete.DeleteResponse;
import de.metalcon.imageServer.protocol.read.ReadResponse;
import de.metalcon.imageServer.protocol.update.UpdateResponse;

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
	 * @param metaData
	 *            image meta data (JSON)
	 * @param autoRotate
	 *            rotation flag - if set to <b>true</b> the server will rotate
	 *            the image using its EXIF data automatically
	 * @param response
	 *            create response object
	 * @return true - if the image has been created successfully<br>
	 *         false - otherwise
	 */
	boolean createImage(String imageIdentifier, InputStream imageStream,
			String metaData, boolean autoRotate, CreateResponse response);

	/**
	 * create a cropped image using a binary file
	 * 
	 * @param imageIdentifier
	 *            image identifier
	 * @param imageStream
	 *            image input stream
	 * @param metaData
	 *            image meta data (JSON)
	 * @param left
	 *            distance between the old and the new left border of the image
	 * @param top
	 *            distance between the old and the new upper border of the image
	 * @param width
	 *            new image width
	 * @param height
	 *            new image height
	 * @param response
	 *            create response object
	 * @return true - if the image has been created successfully<br>
	 *         false - otherwise
	 */
	boolean createImage(String imageIdentifier, InputStream imageStream,
			String metaData, int left, int top, int width, int height,
			CreateResponse response);

	/**
	 * create an image using a link to an existing image
	 * 
	 * @param imageIdentifier
	 *            image identifier
	 * @param imageUrl
	 *            URL to the existing image
	 * @param response
	 *            create response object
	 * @return true - if the image has been created successfully<br>
	 *         false - otherwise
	 */
	boolean createImage(String imageIdentifier, String imageUrl,
			CreateResponse response);

	/**
	 * read the image in its original state with meta data
	 * 
	 * @param imageIdentifier
	 *            image identifier
	 * @param response
	 *            read response object
	 * @return stream of the original image<br>
	 *         <b>null</b> if the image identifier was invalid
	 */
	ImageData readImageWithMetaData(String imageIdentifier,
			ReadResponse response);

	/**
	 * read the image having new dimensions
	 * 
	 * @param imageIdentifier
	 *            image identifier
	 * @param width
	 *            alternate width
	 * @param height
	 *            alternate height
	 * @param response
	 *            read response object
	 * @return stream of the image version having the dimensions passed<br>
	 *         <b>null</b> if the image identifier was invalid
	 */
	InputStream readImage(String imageIdentifier, int width, int height,
			ReadResponse response);

	/**
	 * read the image having new dimensions with meta data
	 * 
	 * @param imageIdentifier
	 *            image identifier
	 * @param width
	 *            alternate width
	 * @param height
	 *            alternate height
	 * @param response
	 *            read response object
	 * @return stream of the image version having the dimensions passed<br>
	 *         <b>null</b> if the image identifier was invalid
	 */
	ImageData readImageWithMetaData(String imageIdentifier, int width,
			int height, ReadResponse response);

	/**
	 * read a bunch of images
	 * 
	 * @param imageIdentifiers
	 *            array of image identifiers
	 * @param width
	 *            alternate width
	 * @param height
	 *            alternate height
	 * @param response
	 *            read response object
	 * @return stream of the archive including the images<br>
	 *         <b>null</b> if any image identifier was invalid
	 */
	InputStream readImages(String[] imageIdentifiers, int width, int height,
			ReadResponse response);

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
	 * @param response
	 *            update response object
	 * @return true - if the meta data was appended successfully<br>
	 *         false - if the image identifier was invalid
	 */
	boolean appendImageInformation(String imageIdentifier, String key,
			String value, UpdateResponse response);

	/**
	 * delete an image from the server
	 * 
	 * @param imageIdentifier
	 *            image identifier
	 * @param response
	 *            delete response object
	 * @return true - if the image was deleted successfully<br>
	 *         false - if the image identifier was invalid
	 */
	boolean deleteImage(String imageIdentifier, DeleteResponse response);

}