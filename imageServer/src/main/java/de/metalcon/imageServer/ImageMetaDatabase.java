package de.metalcon.imageServer;

/**
 * database for image meta data
 * 
 * @author sebschlicht
 * 
 */
public class ImageMetaDatabase extends MetaDatabase {

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

}