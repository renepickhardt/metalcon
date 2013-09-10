package de.metalcon.imageServer;

import java.net.UnknownHostException;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

/**
 * database for image meta data
 * 
 * @author sebschlicht
 * 
 */
public class ImageMetaDatabase extends MetaDatabase {

	/**
	 * name of the database table for registered image sizes
	 */
	private static final String TABLE_SIZES = "image_sizes";

	/**
	 * BSON key for the width in an image size entry
	 */
	private static final String SIZE_WIDTH = "width";

	/**
	 * BSON key for the height in an image size entry
	 */
	private static final String SIZE_HEIGHT = "height";

	/**
	 * BSON key for the image path in an image size entry
	 */
	private static final String SIZE_IMAGE_PATH = "path";

	/**
	 * database table for registered image sizes
	 */
	protected final DBCollection tableSizes;

	/**
	 * create a new database for image meta data
	 * 
	 * @param hostAddress
	 *            host address of the server the database runs at
	 * @param port
	 *            port to connect to the database
	 * @param database
	 *            name of the database used
	 * @throws UnknownHostException
	 *             if the database server could not be reached
	 */
	public ImageMetaDatabase(final String hostAddress, final int port,
			final String database) throws UnknownHostException {
		super(hostAddress, port, database);
		this.tableSizes = this.mongoDB.getCollection(TABLE_SIZES);
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
	 * @return true - if the image size has been registered successfully<br>
	 *         false - if there is no image with such identifier
	 */
	public boolean registerImageSize(final String imageIdentifier,
			final int width, final int height, final String imagePath) {
		if (this.hasEntryWithIdentifier(imageIdentifier)) {
			final BasicDBObject entry = new BasicDBObject();
			entry.put(ENTRY_IDENTIFIER, imageIdentifier);
			entry.put(SIZE_WIDTH, width);
			entry.put(SIZE_HEIGHT, height);
			entry.put(SIZE_IMAGE_PATH, imagePath);
			this.tableSizes.insert(entry);

			return true;
		}

		return false;
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
		final BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put(ENTRY_IDENTIFIER, imageIdentifier);
		searchQuery.put(SIZE_WIDTH, width);
		searchQuery.put(SIZE_HEIGHT, height);
		return (this.tableSizes.findOne(searchQuery) != null);
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
		final BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put(ENTRY_IDENTIFIER, imageIdentifier);

		final BasicDBObject searchQueryWidth = new BasicDBObject("$gte", width);
		searchQuery.put(SIZE_WIDTH, searchQueryWidth);

		final BasicDBObject searchQueryHeight = new BasicDBObject("$gte",
				height);
		searchQuery.put(SIZE_HEIGHT, searchQueryHeight);

		final BasicDBObject orderBy = new BasicDBObject(SIZE_WIDTH, 1);

		String imagePath = null;
		final DBCursor cursor = this.tableSizes.find(searchQuery).sort(orderBy);
		try {
			if (cursor.hasNext()) {
				imagePath = (String) cursor.next().get(SIZE_IMAGE_PATH);
			}
		} finally {
			cursor.close();
		}

		return imagePath;
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
		if (this.hasEntryWithIdentifier(imageIdentifier)) {
			final BasicDBObject searchQuery = new BasicDBObject();
			searchQuery.put(ENTRY_IDENTIFIER, imageIdentifier);

			final BasicDBObject keys = new BasicDBObject(SIZE_IMAGE_PATH, true);
			final DBCursor cursor = this.tableSizes.find(searchQuery, keys);

			String[] paths = null;
			try {
				paths = new String[cursor.size()];
				for (int i = 0; cursor.hasNext(); i++) {
					paths[i] = (String) cursor.next().get(SIZE_IMAGE_PATH);
				}
			} finally {
				cursor.close();
			}

			return paths;
		}

		return null;
	}

	@Override
	public boolean deleteDatabaseEntry(final String identifier) {
		final BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put(ENTRY_IDENTIFIER, identifier);
		this.tableSizes.remove(searchQuery);

		return super.deleteDatabaseEntry(identifier);
	}

	@Override
	public void clear() {
		super.clear();
		this.tableSizes.drop();
	}

}