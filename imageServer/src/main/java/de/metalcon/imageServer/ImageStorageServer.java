package de.metalcon.imageServer;

import java.awt.Rectangle;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

	private final String temporaryDirectory = "/dev/shm/";

	/**
	 * database for image meta data
	 */
	private final ImageMetaDatabase imageMetaDatabase;

	// TODO: remove image from temporary directory and load it into the "real"
	// memory

	public ImageStorageServer() {
		this.imageMetaDatabase = new ImageMetaDatabase();
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
			if (i != (depth - 1)) {
				path.append(File.separator);
			}
		}

		return path.toString();
	}

	/**
	 * access the stream of an image
	 * 
	 * @param directory
	 *            path to the parental directory
	 * @param fileName
	 *            name of the image file to be accessed
	 * @return input stream of the image
	 * @throws FileNotFoundException
	 *             if the image was not found
	 */
	private static InputStream accessImageStream(final String directory,
			final String fileName) throws FileNotFoundException {
		return new FileInputStream(new File(directory, fileName));
	}

	/**
	 * write an image stream to a file
	 * 
	 * @param imageInputStream
	 *            image stream to be written
	 * @param destinationFile
	 *            destination file to be written in
	 * @throws FileNotFoundException
	 *             if the destination file can not be created/accessed
	 * @throws IOException
	 *             if the writing failed
	 */
	private static void writeImageStream(final InputStream imageInputStream,
			final File destinationFile) throws FileNotFoundException,
			IOException {
		final OutputStream imageOutputStream = new FileOutputStream(
				destinationFile);
		IOUtils.copy(imageInputStream, imageOutputStream);
	}

	/**
	 * store an image
	 * 
	 * @param fileName
	 *            destination file name
	 * @param imageFileDir
	 *            destination directory (will be created if not existing)
	 * @param imageInputStream
	 *            input stream of the image
	 * @return file handle to the destination file<br>
	 *         <b>null</b> if the destination file is already existing
	 * @throws IOException
	 *             if the storing fails due to IO errors
	 */
	private static File storeImage(final String fileName,
			final File imageFileDir, final InputStream imageInputStream)
			throws IOException {
		imageFileDir.mkdirs();

		final File imageFile = new File(imageFileDir, fileName);
		if (imageFile.exists()) {
			// hash codes of two image identifiers overlap
			return null;
		}

		// write image stream
		writeImageStream(imageInputStream, imageFile);
		return imageFile;
	}

	/**
	 * store an image using compression
	 * 
	 * @param hash
	 *            image identifier hash
	 * @param image
	 *            magick image to be stored
	 * @param imageFileDir
	 *            parental directory of the image
	 * @return file handle to the compressed image
	 * @throws MagickException
	 *             if the compression/writing failed
	 */
	private static File storeCompressedImage(final String hash,
			final MagickImage image, final File imageFileDir)
			throws MagickException {
		imageFileDir.mkdirs();

		final File imageFile = new File(imageFileDir, hash + ".jpg");
		final ImageInfo imageInfo = new ImageInfo();
		imageInfo.setCompression(CompressionType.JPEGCompression);

		image.setFileName(imageFile.getAbsolutePath());
		image.writeImage(imageInfo);
		return imageFile;
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
	 * @return cropped image<br>
	 *         <b>null</b> if the cropping failed
	 */
	private static MagickImage cropImage(final MagickImage image,
			final int left, final int top, final int width, final int height) {
		try {
			return image.cropImage(new Rectangle(left, top, width, height));
		} catch (final MagickException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * scale an image
	 * 
	 * @param image
	 *            image to be scaled
	 * @param width
	 *            target width
	 * @param height
	 *            target height
	 * @return scaled image<br>
	 *         <b>null</b> if the scaling failed
	 */
	private static MagickImage scaleImage(final MagickImage image,
			final int width, final int height) {
		try {
			return image.scaleImage(width, height);
		} catch (final MagickException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public boolean createImage(final String imageIdentifier,
			final InputStream imageStream, final String metaData,
			final boolean autoRotate, final CreateResponse response) {
		final String hash = generateHash(imageIdentifier);

		if (this.imageMetaDatabase.addDatabaseEntry(imageIdentifier, metaData)) {

			try {
				// store and try to load original image
				final MagickImage image = this.storeAndLoadImage(hash,
						imageStream);

				if (image != null) {
					if (autoRotate) {
						// TODO: implement auto orientation
						// WARNING: no documentation available!
						// image.autoOrientImage();
					}

					// store basis version
					storeCompressedImage(hash, image,
							new File(this.getBasisImageDirectory(hash)));

					return true;
				} else {
					// TODO internal server error: hash collision
				}
			} catch (final MagickException e) {
				// TODO error: no image file
				e.printStackTrace();
			} catch (final IOException e) {
				// internal server error: failed to store image(s)
				e.printStackTrace();
			}

		} else {
			// TODO error: image identifier in use
		}

		return false;
	}

	@Override
	public boolean createImage(final String imageIdentifier,
			final InputStream imageStream, final String metaData,
			final int left, final int top, final int width, final int height,
			final CreateResponse response) {
		final String hash = generateHash(imageIdentifier);

		if (this.imageMetaDatabase.addDatabaseEntry(imageIdentifier, metaData)) {

			try {
				// store and try to load original image
				MagickImage image = this.storeAndLoadImage(hash, imageStream);

				if (image != null) {
					// crop the image
					image = cropImage(image, left, top, width, height);

					if (image != null) {
						// store basis version
						storeCompressedImage(hash, image,
								new File(this.getBasisImageDirectory(hash)));

						return true;
					}

					// TODO: internal server error: cropping failed

				} else {
					// TODO internal server error: hash collision
				}
			} catch (final MagickException e) {
				// TODO error: no image file
				e.printStackTrace();
			} catch (final IOException e) {
				// internal server error: failed to store image(s)
				e.printStackTrace();
			}
		} else {
			// TODO error: image identifier in use
		}

		return false;
	}

	@Override
	public boolean createImage(String imageIdentifier, String imageUrl,
			final CreateResponse response) {
		final String hash = generateHash(imageIdentifier);
		final File tmpImageFile = new File(
				this.getTemporaryImageDirectory(hash));

		try {
			// TODO: download image

			// store original image
			final MagickImage image = this.storeAndLoadImage(hash,
					new FileInputStream(tmpImageFile));

			if (image != null) {
				// store basis version
				storeCompressedImage(hash, image,
						new File(this.getBasisImageDirectory(hash)));

				return true;
			} else {
				// TODO internal server error: hash collision
			}

		} catch (final MagickException e) {
			// TODO error: no image file
		} catch (final IOException e) {
			// internal server error: failed to store image(s)
		}

		return false;
	}

	@Override
	public ImageData readImageWithMetaData(final String imageIdentifier,
			final ReadResponse response) {
		final String hash = generateHash(imageIdentifier);
		final String metaData = this.imageMetaDatabase
				.getMetadata(imageIdentifier);

		if (metaData != null) {
			try {
				// return the original image
				return new ImageData(metaData, accessImageStream(
						this.getOriginalImageDirectory(hash), hash));
			} catch (final FileNotFoundException e) {
				// internal server error: file not found
				e.printStackTrace();
			}
		} else {
			// TODO error: no image with such identifier
		}

		return null;
	}

	@Override
	public InputStream readImage(final String imageIdentifier, final int width,
			final int height, final ReadResponse response) {
		final String hash = generateHash(imageIdentifier);
		if (this.imageMetaDatabase.hasEntryWithIdentifier(imageIdentifier)) {
			try {
				final String imageFileDirPath = this.getImageDirectoryForSize(
						hash, width, height);

				if (this.imageMetaDatabase.imageHasSizeRegistered(
						imageIdentifier, width, height)) {
					// read the scaled image
					return accessImageStream(imageFileDirPath, hash + ".jpg");
				} else {
					// try to create the scaled version
					final String largerImagePath = this.imageMetaDatabase
							.getSmallestImagePath(imageIdentifier, width,
									height);

					if (largerImagePath != null) {
						final InputStream imageStream = this
								.storeAndAccessScaledImage(hash, width, height,
										largerImagePath, imageFileDirPath,
										imageIdentifier);
						if (imageStream != null) {
							return imageStream;
						}

						// TODO: internal server error: scaling failed

					} else {
						// TODO warning: requested size larger than the original
						return accessImageStream(
								this.getBasisImageDirectory(hash), hash
										+ ".jpg");
					}
				}

			} catch (final FileNotFoundException e) {
				// internal server error: file not found
				e.printStackTrace();
			} catch (final MagickException e) {
				// internal server error: failed to load/scale/store image
				e.printStackTrace();
			}
		} else {
			// error: no image with such identifier
		}

		return null;
	}

	@Override
	public ImageData readImageWithMetaData(String imageIdentifier, int width,
			int height, final ReadResponse response) {
		final String metaData = this.imageMetaDatabase
				.getMetadata(imageIdentifier);
		if (metaData != null) {
			final String hash = generateHash(imageIdentifier);
			final String imageFileDirPath = this.getImageDirectoryForSize(hash,
					width, height);

			try {
				if (this.imageMetaDatabase.imageHasSizeRegistered(
						imageIdentifier, width, height)) {
					// read the scaled image
					return new ImageData(metaData, accessImageStream(
							imageFileDirPath, hash));
				} else {
					// try to create the scaled version
					final String largerImagePath = this.imageMetaDatabase
							.getSmallestImagePath(imageIdentifier, width,
									height);

					if (largerImagePath != null) {
						final InputStream imageStream = this
								.storeAndAccessScaledImage(hash, width, height,
										largerImagePath, imageFileDirPath,
										imageIdentifier);

						if (imageStream != null) {
							return new ImageData(metaData, imageStream);
						}

						// TODO: internal server error: scaling failed
					} else {
						// TODO warning: requested size larger than the original
						return new ImageData(metaData, accessImageStream(
								this.getBasisImageDirectory(hash), hash));
					}
				}
			} catch (final FileNotFoundException e) {
				// internal server error: file not found
				e.printStackTrace();
			} catch (final MagickException e) {
				// internal server error: failed to store image
				e.printStackTrace();
			}
		} else {
			// TODO error: no image with such identifier
		}

		return null;
	}

	@Override
	public InputStream readImages(final String[] imageIdentifiers, int width,
			final int height, final ReadResponse response) {

		boolean succeeded = true;
		try {
			final ByteArrayOutputStream memoryOutputStream = new ByteArrayOutputStream();
			final ZipOutputStream archiveStream = new ZipOutputStream(
					memoryOutputStream);

			ZipEntry zipEntry;
			try {
				for (String imageIdentifier : imageIdentifiers) {
					final String hash = generateHash(imageIdentifier);
					if (this.imageMetaDatabase
							.hasEntryWithIdentifier(imageIdentifier)) {
						final String imageFileDirPath = this
								.getImageDirectoryForSize(hash, width, height);

						// no ZIP compression while JPEG is already compressed
						zipEntry = new ZipEntry(imageIdentifier);
						zipEntry.setMethod(ZipEntry.STORED);
						archiveStream.putNextEntry(zipEntry);

						if (this.imageMetaDatabase.imageHasSizeRegistered(
								imageIdentifier, width, height)) {
							// add scaled image
							IOUtils.copy(
									accessImageStream(imageFileDirPath, hash),
									archiveStream);
						} else {
							// try to create the scaled version
							final String largerImagePath = this.imageMetaDatabase
									.getSmallestImagePath(imageIdentifier,
											width, height);
							if (largerImagePath != null) {
								// add scaled image
								IOUtils.copy(this.storeAndAccessScaledImage(
										hash, width, height, largerImagePath,
										imageFileDirPath, imageIdentifier),
										archiveStream);
							} else {
								// TODO warning: requested size larger than the
								// original
								IOUtils.copy(
										accessImageStream(this
												.getBasisImageDirectory(hash),
												hash), archiveStream);
							}
						}

						archiveStream.closeEntry();

					} else {
						// TODO error: no image with such identifier
						succeeded = false;
						break;
					}
				}
			} catch (final FileNotFoundException e) {
				// internal server error: file not found
				e.printStackTrace();
				succeeded = false;
			} catch (final IOException e) {
				// internal server error: failed to write to archive stream
				e.printStackTrace();
				succeeded = false;
			} catch (final MagickException e) {
				// internal server error: failed to load/scale/store image
				e.printStackTrace();
				succeeded = false;
			}

			archiveStream.close();

			if (succeeded) {
				return new ByteArrayInputStream(
						memoryOutputStream.toByteArray());
			}
		} catch (final IOException e) {
			// internal server error: failed to create archive
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public boolean appendImageInformation(final String imageIdentifier,
			final String key, final String value, final UpdateResponse response) {
		if (this.imageMetaDatabase.appendMetadata(imageIdentifier, key, value)) {
			return true;
		} else {
			// TODO error: no image with such identifier
		}

		return false;
	}

	@Override
	public boolean deleteImage(String imageIdentifier,
			final DeleteResponse response) {
		final String[] imagePaths = this.imageMetaDatabase
				.getRegisteredImagePaths(imageIdentifier);
		if (this.imageMetaDatabase.deleteDatabaseEntry(imageIdentifier)) {
			// TODO: ensure to delete the original and the basis version

			// delete all additional image versions registered
			File imageFile;
			for (String imagePath : imagePaths) {
				imageFile = new File(imagePath);
				imageFile.delete();
			}
		} else {
			// TODO error: no image with such identifier
		}

		return false;
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
	 * generate the parental directory for a basis image
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
	 * generate the parental directory for a temporary image
	 * 
	 * @param hash
	 *            image identifier hash
	 * @return parental directory for the temporary image passed
	 */
	private String getTemporaryImageDirectory(final String hash) {
		return this.temporaryDirectory;
	}

	/**
	 * generate the parental directory for a specific size
	 * 
	 * @param hash
	 *            image identifier hash
	 * @param width
	 *            target width
	 * @param height
	 *            target height
	 * @return parental directory for the scaled version of the image
	 */
	private String getImageDirectoryForSize(final String hash, final int width,
			final int height) {
		return this.imageDirectory + getRelativeDirectory(hash, 3)
				+ File.separator + String.valueOf(width) + "x"
				+ String.valueOf(height);
	}

	/**
	 * store the original image and load it for editing
	 * 
	 * @param hash
	 *            image identifier hash
	 * @param imageStream
	 *            image input stream
	 * @return original image ready for editing<br>
	 *         <b>null</b> if there was a collision between original images
	 * @throws IOException
	 *             failed to store the original image
	 * @throws MagickException
	 *             image stream invalid
	 */
	private MagickImage storeAndLoadImage(final String hash,
			final InputStream imageStream) throws IOException, MagickException {
		// store the original image
		final File tmpFileDir = new File(this.getTemporaryImageDirectory(hash));
		final File tmpFile = storeImage(hash, tmpFileDir, imageStream);

		if (tmpFile != null) {
			try {
				final MagickImage originalImage = new MagickImage(
						new ImageInfo(tmpFile.getAbsolutePath()));

				// save the original image to the disk
				final FileInputStream tmpInputStream = new FileInputStream(
						tmpFile);
				final File originalFileDir = new File(
						this.getOriginalImageDirectory(hash));
				storeImage(hash, originalFileDir, tmpInputStream);

				return originalImage;

			} catch (final MagickException e) {
				// delete invalid (image) file
				tmpFile.delete();

				// TODO: delete parental directory/ies?

				throw e;
			}
		}

		// collision between original image files
		return null;
	}

	/**
	 * store and access a scaled image
	 * 
	 * @param hash
	 *            image identifier hash
	 * @param width
	 *            target width
	 * @param height
	 *            target height
	 * @param basisImagePath
	 *            path to the basic version for scaling
	 * @param imageFileDirPath
	 *            path to the parental directory of the scaled image
	 * @param imageIdentifier
	 *            image identifier
	 * @return input stream of the scaled image<br>
	 *         <b>null</b> if the scaling failed
	 * @throws MagickException
	 *             if the basis image could not be loaded
	 * @throws FileNotFoundException
	 *             if the image was not found
	 */
	private InputStream storeAndAccessScaledImage(final String hash,
			final int width, final int height, final String basisImagePath,
			final String imageFileDirPath, final String imageIdentifier)
			throws MagickException, FileNotFoundException {
		final MagickImage image = scaleImage(new MagickImage(new ImageInfo(
				basisImagePath)), width, height);

		if (image != null) {
			final File imageFileDir = new File(imageFileDirPath);
			final File imageFile = storeCompressedImage(hash, image,
					imageFileDir);
			this.imageMetaDatabase.registerImageSize(imageIdentifier, width,
					height, imageFile.getAbsolutePath());

			return accessImageStream(imageFileDirPath, hash);
		}

		return null;
	}

}