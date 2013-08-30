package de.metalcon.imageServer;

import java.io.File;

import org.json.simple.JSONObject;

/**
 * prototype interface for the image server API
 * 
 * @author sebschlicht
 * 
 */
public interface ImageServer {

	/**
	 * create an image using a binary file
	 * 
	 * @param imageIdentifier
	 *            image identifier
	 * @param image
	 *            binary image file
	 * @param albumIdentifier
	 *            album identifier
	 * @param imageInformation
	 *            JSON according to the image meta data model
	 * @param autoRotate
	 *            rotation flag - if set to <b>true</b> the server will rotate
	 *            the image using its EXIF data automatically
	 */
	void createImage(String imageIdentifier, File image,
			String albumIdentifier, JSONObject imageInformation,
			boolean autoRotate);

	/**
	 * create a cropped image using a binary file
	 * 
	 * @param imageIdentifier
	 *            image identifier
	 * @param image
	 *            binary image file
	 * @param albumIdentifier
	 *            album identifier
	 * @param imageInformation
	 *            JSON according to the image meta data model
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
	void createImage(String imageIdentifier, File image,
			String albumIdentifier, JSONObject imageInformation,
			boolean autoRotate, int left, int right, int width, int height);

	/**
	 * create an image using a link to an existing image
	 * 
	 * @param imageIdentifier
	 *            image identifier
	 * @param imageUrl
	 *            URL to the existing image
	 * @param albumIdentifier
	 *            album identifier
	 * @param autoRotate
	 *            rotation flag - if set to <b>true</b> the server will rotate
	 *            the image using its EXIF data automatically
	 */
	void createImage(String imageIdentifier, String imageUrl,
			String albumIdentifier, boolean autoRotate);

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
	 * @return JSON according to the image meta data model
	 */
	JSONObject readImageInformation(String imageIdentifier);

	/**
	 * read the meta data of an album
	 * 
	 * @param albumIdentifier
	 *            album identifier
	 * @return JSON according to the album meta data model
	 */
	JSONObject readAlbumInformation(String albumIdentifier);

	/**
	 * update the meta data of an image
	 * 
	 * @param imageIdentifier
	 *            image identifier
	 * @param imageInformation
	 *            JSON according to the image meta data model
	 */
	void updateImageInformation(String imageIdentifier,
			JSONObject imageInformation);

	/**
	 * update the meta data of an album
	 * 
	 * @param albumIdentifier
	 *            album identifier
	 * @param albumInformation
	 *            JSON according to the album meta data model
	 */
	void updateAlbumInformation(String albumIdentifier,
			JSONObject albumInformation);

	/**
	 * delete an image from the file system and the album
	 * 
	 * @param imageIdentifier
	 *            image identifier
	 */
	void deleteImage(String imageIdentifier);

	/**
	 * delete an album and all images it includes
	 * 
	 * @param albumIdentifier
	 *            album identifier
	 */
	void deleteAlbum(String albumIdentifier);

}