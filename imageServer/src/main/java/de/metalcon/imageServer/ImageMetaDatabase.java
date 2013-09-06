package de.metalcon.imageServer;

import java.net.UnknownHostException;

/**
 * database for image meta data
 * 
 * @author sebschlicht
 * 
 */
public class ImageMetaDatabase extends MetaDatabase {

	/**
	 * name of the database used for image meta data
	 */
	private static final String DATABASE_NAME = "imagedb";

	/**
	 * create a new database for image meta data
	 * 
	 * @param hostAddress
	 *            host address of the server the database runs at
	 * @param port
	 *            port to connect to the database
	 * @throws UnknownHostException
	 *             if the database server could not be reached
	 */
	public ImageMetaDatabase(String hostAddress, int port)
			throws UnknownHostException {
		super(hostAddress, port, DATABASE_NAME);
	}

	/**
	 * register a size for an image
	 * 
	 * @param imageIdentifier
	 *            image identifier
	 * @param width
	 *            new width
	 * @param height
	 *            new height
	 * @param imagePath
	 *            path to the image
	 */
	public void registerImageSize(final String imageIdentifier,
			final int width, final int height, final String imagePath) {
		// TODO
	}

	/**
	 * check if there is a certain size for an image
	 * 
	 * @param imageIdentifier
	 *            image identifier
	 * @param width
	 *            requested width
	 * @param height
	 *            requested height
	 * @return true - if the size is registered for the image passed<br>
	 *         false - otherwise
	 */
	public boolean imageHasSizeRegistered(final String imageIdentifier,
			final int width, final int height) {
		// TODO
		return false;
	}

	/**
	 * get the path to the smallest image version larger than the dimension
	 * passed
	 * 
	 * @param imageIdentifier
	 *            image identifier
	 * @param width
	 *            minimum width
	 * @param height
	 *            minimum height
	 * @return path to the smallest image larger than the dimension passed<br>
	 *         <b>null</b> if there is no such version
	 */
	public String getSmallestImagePath(final String imageIdentifier,
			final int width, final int height) {
		// TODO
		return null;
	}

	/**
	 * get the paths to all image versions registered for an image
	 * 
	 * @param imageIdentifier
	 *            image identifier
	 * @return paths to all image versions<br>
	 *         <b>null</b> if there is no image with such identifier
	 */
	public String[] getRegisteredImagePaths(final String imageIdentifier) {
		// TODO
		return null;
	}

	@Override
	public boolean deleteDatabaseEntry(final String identifier) {
		// TODO
		return super.deleteDatabaseEntry(identifier);
	}

}