package de.metalcon.imageServer;

import java.io.File;

import org.json.simple.JSONObject;

/**
 * image server API interface
 * 
 * @author sebschlicht
 * 
 */
public interface ImageServer {

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
	 * @return JON according to the album meta data model
	 */
	JSONObject readAlbumInformation(String albumIdentifier);

}