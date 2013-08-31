package de.metalcon.imageServer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;

import org.apache.commons.io.IOUtils;

/**
 * image storage server
 * 
 * @author sebschlicht
 * 
 */
public class ImageStorageServer implements ImageStorageServerAPI {

	/**
	 * date formatter
	 */
	private static final Format FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * day represented by the current formatted day
	 */
	private static long DAY;

	/**
	 * formatted day
	 */
	private static String FORMATTED_DAY;

	/**
	 * database for image meta data
	 */
	private final MetaDatabase imageMetaDatabase;

	public ImageStorageServer() {
		this.imageMetaDatabase = new MetaDatabase();
	}

	@Override
	public void createImage(String imageIdentifier, InputStream imageStream,
			String metaData, boolean autoRotate) {
		// TODO: use response object
		if (this.imageMetaDatabase.addDatabaseEntry(imageIdentifier, metaData)) {
			final MagickImage image = storeAndLoadImage(imageIdentifier,
					imageStream);
			if (autoRotate) {
				try {
					// warning: no documentation available!
					image.autoOrientImage();
				} catch (final MagickException e) {
					e.printStackTrace();
				}
			}

			// TODO: save basic version
		}
	}

	@Override
	public void createImage(String imageIdentifier, InputStream imageStream,
			String imageInformation, boolean autoRotate, int left, int right,
			int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void createImage(String imageIdentifier, String imageUrl,
			boolean autoRotate) {
		// TODO Auto-generated method stub

	}

	@Override
	public File readImage(String imageIdentifier, int width, int height) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File readImage(String imageIdentifier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String readImageInformation(String imageIdentifier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void appendImageInformation(String imageIdentifier, String key,
			String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteImage(String imageIdentifier) {
		// TODO Auto-generated method stub

	}

	private static String getFormattedDay() {
		final long crrMs = System.currentTimeMillis();
		final long day = crrMs / 1000 / 60 / 60 / 24;
		if (DAY != day) {
			DAY = day;
			FORMATTED_DAY = FORMATTER.format(new Date(System
					.currentTimeMillis()));
		}

		return FORMATTED_DAY;
	}

	/**
	 * generate the has value for a key
	 * 
	 * @param key
	 *            object to generate the hash for
	 * @return hash for the key passed
	 */
	private static String generateHash(final String key) {
		return String.valueOf(key.hashCode());
	}

	/**
	 * create the relative file path for an image
	 * 
	 * @param imageIdentifier
	 *            image identifier
	 * @param depth
	 *            number of sub directories
	 * @return relative file path using hashes
	 */
	private static String getRelativeFilePath(final String imageIdentifier,
			final int depth) {
		final String hash = generateHash(imageIdentifier);

		int pathLength = hash.length();
		for (int i = 0; i < depth; i++) {
			pathLength += i + 1;
		}

		final StringBuilder path = new StringBuilder(pathLength);
		for (int i = 0; i < depth; i++) {
			path.append(hash.substring(0, depth));
			path.append(File.pathSeparator);
		}
		path.append(hash);

		return path.toString();
	}

	/**
	 * create the path for an original image
	 * 
	 * @param imageIdentifier
	 *            image identifier
	 * @return path for the original image passed
	 */
	private static String getOriginalImagePath(final String imageIdentifier) {
		return "/etc/imageStorageServer/originals/" + getFormattedDay() + "/"
				+ getRelativeFilePath(imageIdentifier, 2);
	}

	/**
	 * store the original image to the file system
	 * 
	 * @param imageIdentifier
	 *            image identifier
	 * @param imageInputStream
	 *            image input stream
	 * @return file handle to the original image<br>
	 *         <b>null</b> if the file could not be written successfully
	 */
	private static File storeOriginalImage(final String imageIdentifier,
			final InputStream imageInputStream) {
		final File imageFile = new File(getOriginalImagePath(imageIdentifier));
		if (imageFile.exists()) {
			// hash codes of two image identifiers overlap
			return null;
		}

		try {
			final OutputStream imageOutputStream = new FileOutputStream(
					imageFile);
			IOUtils.copy(imageInputStream, imageOutputStream);
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}

		// failed to store file
		return null;
	}

	/**
	 * store the original image and load it for editing
	 * 
	 * @param imageIdentifier
	 *            image identifier
	 * @param imageStream
	 *            image input stream
	 * @return magick image for editing
	 */
	private static MagickImage storeAndLoadImage(final String imageIdentifier,
			final InputStream imageStream) {
		final File imageFile = storeOriginalImage(imageIdentifier, imageStream);
		if (imageFile != null) {
			try {
				final ImageInfo imageInfo = new ImageInfo(
						imageFile.getAbsolutePath());
				return new MagickImage(imageInfo);
			} catch (final MagickException e) {
				// file is no valid image file
				return null;
			}
		}

		// failed to load original image
		return null;
	}

}