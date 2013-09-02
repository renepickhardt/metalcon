package de.metalcon.imageServer;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import magick.CompressionType;
import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;

import org.apache.commons.io.IOUtils;

import de.metalcon.imageServer.protocol.create.CreateResponse;
import de.metalcon.imageServer.protocol.delete.DeleteResponse;
import de.metalcon.imageServer.protocol.read.ReadResponse;
import de.metalcon.imageServer.protocol.update.UpdateResponse;

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
	 * year represented by the current formatted year
	 */
	private static int YEAR;

	/**
	 * day of the year represented by the current formatted day
	 */
	private static int DAY;

	/**
	 * formatted year
	 */
	private static String FORMATTED_YEAR;

	/**
	 * formatted day
	 */
	private static String FORMATTED_DAY;

	/**
	 * root directory for the image storage server
	 */
	private final String imageDirectory = "/etc/imageStorageServer/";

	/**
	 * database for image meta data
	 */
	private final MetaDatabase imageMetaDatabase;

	public ImageStorageServer() {
		this.imageMetaDatabase = new MetaDatabase();
	}

	/**
	 * update the current date strings
	 */
	private static void updateDateLabels() {
		final Calendar calendar = Calendar.getInstance();
		final int day = calendar.get(Calendar.DAY_OF_YEAR);
		final int year = calendar.get(Calendar.YEAR);

		if ((day != DAY) || (year != YEAR)) {
			YEAR = year;
			DAY = day;

			FORMATTED_YEAR = String.valueOf(year);
			FORMATTED_DAY = FORMATTER.format(calendar.getTime());
		}
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
	 * generate the relative directory path for an image
	 * 
	 * @param hash
	 *            image identifier hash
	 * @param depth
	 *            number of sub directories
	 * @return relative directory path using hashes
	 */
	private static String getRelativeDirectory(final String hash,
			final int depth) {
		int pathLength = 0;
		for (int i = 0; i < depth; i++) {
			pathLength += i + 1;
		}

		final StringBuilder path = new StringBuilder(pathLength);
		for (int i = 0; i < depth; i++) {
			path.append(hash.substring(0, i + 1));
			path.append(File.separator);
		}

		return path.toString();
	}

	/**
	 * crop an image
	 * 
	 * @param image
	 *            image to be cropped
	 * @param left
	 *            distance between the old and the new left border of the image
	 * @param top
	 *            distance between the old and the new upper border of the image
	 * @param width
	 *            target width
	 * @param height
	 *            target height
	 * @return true - if the image was cropped successfully<br>
	 *         false - otherwise
	 */
	private static boolean cropImage(final MagickImage image, final int left,
			final int top, final int width, final int height) {
		try {
			image.cropImage(new Rectangle(left, top, width, height));
			return true;
		} catch (final MagickException e) {
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public boolean createImage(final String imageIdentifier,
			final InputStream imageStream, final String metaData,
			final boolean autoRotate, final CreateResponse response) {
		final String hash = generateHash(imageIdentifier);

		// TODO: use response object
		if (this.imageMetaDatabase.addDatabaseEntry(imageIdentifier, metaData)) {

			try {
				// store original image
				final MagickImage image = this.storeAndLoadImage(hash,
						imageStream);

				if (image != null) {
					if (autoRotate) {
						// WARNING: no documentation available!
						image.autoOrientImage();
					}

					// store basis version
					this.storeImage(hash, image);

					return true;
				}
			} catch (final MagickException e) {
				// TODO error: no image file/error while rotating
				e.printStackTrace();
			} catch (final IOException e) {
				// TODO error: failed to store image(s)
				e.printStackTrace();
			}

		} else {
			// TODO error: image identifier in use
		}

		return false;
	}

	@Override
	public boolean createImage(String imageIdentifier, InputStream imageStream,
			String metaData, boolean autoRotate, int left, int right,
			int width, int height, final CreateResponse response) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean createImage(String imageIdentifier, String imageUrl,
			final CreateResponse response) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ImageData readImageWithMetaData(String imageIdentifier,
			final ReadResponse response) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream readImage(String imageIdentifier, int width, int height,
			final ReadResponse response) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImageData readImageWithMetaData(String imageIdentifier, int width,
			int height, final ReadResponse response) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream readImages(final String[] imageIdentifiers, int width,
			final int height, final ReadResponse response) {
		// TODO
		return null;
	}

	@Override
	public void appendImageInformation(String imageIdentifier, String key,
			String value, final UpdateResponse response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteImage(String imageIdentifier,
			final DeleteResponse response) {
		// TODO Auto-generated method stub

	}

	/**
	 * generate the parental directory for an original image
	 * 
	 * @param hash
	 *            image identifier hash
	 * @return parental directory for the original image passed
	 */
	private String getOriginalImageDirectory(final String hash) {
		updateDateLabels();
		return this.imageDirectory + "originals" + File.separator
				+ FORMATTED_YEAR + File.separator + FORMATTED_DAY
				+ File.separator + getRelativeDirectory(hash, 2);
	}

	/**
	 * generate the parental directory for the basis image
	 * 
	 * @param hash
	 *            image identifier hash
	 * @return parental directory for the basis image passed
	 */
	private String getBasisImageDirectory(final String hash) {
		return this.imageDirectory + getRelativeDirectory(hash, 3)
				+ File.separator + "basis";
	}

	/**
	 * store the original image to the file system
	 * 
	 * @param hash
	 *            image identifier hash
	 * @param imageInputStream
	 *            image input stream
	 * @return file handle to the original image<br>
	 *         <b>null</b> if there was a collision between two original image
	 *         files
	 * @throws IOException
	 *             failed to write original image to the destination file
	 */
	private File storeOriginalImage(final String hash,
			final InputStream imageInputStream) throws IOException {
		final File imageFileDir = new File(this.getOriginalImageDirectory(hash));
		imageFileDir.mkdirs();

		final File imageFile = new File(imageFileDir, hash);
		System.out.println("original image: " + imageFile.getAbsolutePath());
		if (imageFile.exists()) {
			// hash codes of two image identifiers overlap
			return null;
		}

		final OutputStream imageOutputStream = new FileOutputStream(imageFile);
		IOUtils.copy(imageInputStream, imageOutputStream);
		return imageFile;
	}

	/**
	 * store the original image and load it for editing
	 * 
	 * @param hash
	 *            image identifier hash
	 * @param imageStream
	 *            image input stream
	 * @return magick image for editing<br>
	 *         <b>null</b> if the image is not valid or there was a collision
	 *         between original images
	 * @throws IOException
	 *             failed to store the original image
	 * @throws MagickException
	 *             image stream invalid
	 */
	private MagickImage storeAndLoadImage(final String hash,
			final InputStream imageStream) throws IOException, MagickException {
		final File imageFile = this.storeOriginalImage(hash, imageStream);
		if (imageFile != null) {
			final ImageInfo imageInfo = new ImageInfo(
					imageFile.getAbsolutePath());
			return new MagickImage(imageInfo);
		}

		// collision between original image files
		return null;
	}

	private void storeImage(final String hash, final MagickImage image)
			throws MagickException {
		final File imageFileDir = new File(this.getBasisImageDirectory(hash));
		imageFileDir.mkdirs();

		final File imageFile = new File(imageFileDir, hash);
		final ImageInfo imageInfo = new ImageInfo(imageFile.getAbsolutePath());
		imageInfo.setCompression(CompressionType.JPEGCompression);

		image.writeImage(imageInfo);
	}
}