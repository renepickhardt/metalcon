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

	@Override
	public boolean createImage(final String imageIdentifier,
			final InputStream imageStream, final String metaData,
			final boolean autoRotate, final CreateResponse response) {
		final String hash = generateHash(imageIdentifier);

		if (this.imageMetaDatabase.addDatabaseEntry(imageIdentifier, metaData)) {

			try {
				// store original image
				final MagickImage image = this.storeAndLoadImage(hash,
						imageStream);

				if (image != null) {
					if (autoRotate) {
						// TODO: implement auto orientation
						// WARNING: no documentation available!
						// image.autoOrientImage();
					}

					// store basis version
					this.storeCompressedImage(hash, image,
							new File(this.getBasisImageDirectory(hash)));

					return true;
				}
			} catch (final MagickException e) {
				// TODO error: no image file/error while rotating
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
				// store and load original image
				MagickImage image = this.storeAndLoadImage(hash, imageStream);

				if (image != null) {
					// crop the image
					image = cropImage(image, left, top, width, height);

					// store basis version
					this.storeCompressedImage(hash, image,
							new File(this.getBasisImageDirectory(hash)));

					return true;
				} else {
					// TODO error: no valid image file
				}
			} catch (final IOException e) {
				// internal server error: failed to store image(s)
				e.printStackTrace();
			} catch (final MagickException e) {
				// internal server error: failed to rotate the image
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
				this.storeCompressedImage(hash, image,
						new File(this.getBasisImageDirectory(hash)));

				return true;
			}

		} catch (final FileNotFoundException e) {
			// internal server error: file not found
		} catch (final IOException e) {
			// internal server error: failed to store image(s)
		} catch (final MagickException e) {
			// TODO error: no image file
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
			final File originalImageDir = new File(
					this.getOriginalImageDirectory(hash));
			try {
				return new ImageData(metaData, this.accessImageStream(
						originalImageDir, hash));
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
				final File imageFileDir = new File(
						this.getImageDirectoryForSize(hash, width, height));

				if (this.imageMetaDatabase.imageHasSizeRegistered(
						imageIdentifier, width, height)) {
					return this.accessImageStream(imageFileDir, hash);
				} else {
					// try to create the scaled version
					final String largerImagePath = this.imageMetaDatabase
							.getSmallestImagePath(imageIdentifier, width,
									height);
					if (largerImagePath != null) {
						return this.storeAndAccessScaledImage(hash, width,
								height, largerImagePath, imageFileDir);
					} else {
						// TODO warning: requested size larger than the original
						final File basisImageDir = new File(
								this.getBasisImageDirectory(hash));
						return this.accessImageStream(basisImageDir, hash);
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
			final File imageFileDir = new File(this.getImageDirectoryForSize(
					hash, width, height));

			try {
				if (this.imageMetaDatabase.imageHasSizeRegistered(
						imageIdentifier, width, height)) {
					return new ImageData(metaData, this.accessImageStream(
							imageFileDir, hash));
				} else {
					// try to create the scaled version
					final String largerImagePath = this.imageMetaDatabase
							.getSmallestImagePath(imageIdentifier, width,
									height);
					if (largerImagePath != null) {
						return new ImageData(metaData,
								this.storeAndAccessScaledImage(hash, width,
										height, largerImagePath, imageFileDir));
					} else {
						// TODO warning: requested size larger than the original
						final File basisImageDir = new File(
								this.getBasisImageDirectory(hash));
						return new ImageData(metaData, this.accessImageStream(
								basisImageDir, hash));
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

			File imageFileDir;
			ZipEntry zipEntry;
			try {
				for (String imageIdentifier : imageIdentifiers) {
					final String hash = generateHash(imageIdentifier);
					if (this.imageMetaDatabase
							.hasEntryWithIdentifier(imageIdentifier)) {
						imageFileDir = new File(this.getImageDirectoryForSize(
								hash, width, height));

						// no ZIP compression while JPEG is already compressed
						zipEntry = new ZipEntry(imageIdentifier);
						zipEntry.setMethod(ZipEntry.STORED);
						archiveStream.putNextEntry(zipEntry);

						if (this.imageMetaDatabase.imageHasSizeRegistered(
								imageIdentifier, width, height)) {
							IOUtils.copy(
									this.accessImageStream(imageFileDir, hash),
									archiveStream);
						} else {
							// try to create the scaled version
							final String largerImagePath = this.imageMetaDatabase
									.getSmallestImagePath(imageIdentifier,
											width, height);
							if (largerImagePath != null) {
								IOUtils.copy(this.storeAndAccessScaledImage(
										hash, width, height, largerImagePath,
										imageFileDir), archiveStream);
							} else {
								// TODO warning: requested size larger than the
								// original
								final File basisImageDir = new File(
										this.getBasisImageDirectory(hash));
								IOUtils.copy(this.accessImageStream(
										basisImageDir, hash), archiveStream);
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
			// delete all registered images
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
			try {
				final ImageInfo imageInfo = new ImageInfo(
						imageFile.getAbsolutePath());
				return new MagickImage(imageInfo);
			} catch (final MagickException e) {
				// no valid image file
				return null;
			}
		}

		// collision between original image files
		return null;
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
	 * @throws MagickException
	 *             if the compression/writing failed
	 */
	private void storeCompressedImage(final String hash,
			final MagickImage image, final File imageFileDir)
			throws MagickException {
		imageFileDir.mkdirs();

		final File imageFile = new File(imageFileDir, hash + ".jpg");
		final ImageInfo imageInfo = new ImageInfo();
		imageInfo.setCompression(CompressionType.JPEGCompression);

		image.setFileName(imageFile.getAbsolutePath());
		image.writeImage(imageInfo);
	}

	/**
	 * access the stream of an image
	 * 
	 * @param imageFileDir
	 *            parental directory of the image
	 * @param hash
	 *            image identifier hash
	 * @return input stream of the image
	 * @throws FileNotFoundException
	 *             if the image was not found
	 */
	private InputStream accessImageStream(final File imageFileDir,
			final String hash) throws FileNotFoundException {
		return new FileInputStream(new File(imageFileDir, hash));
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
	 * @param largerImagePath
	 *            path to the basic version for scaling
	 * @param imageFileDir
	 *            parental directory of the scaled image
	 * @return input stream of the scaled image
	 * @throws MagickException
	 *             if the writing failed
	 * @throws FileNotFoundException
	 *             if the image was not found
	 */
	private InputStream storeAndAccessScaledImage(final String hash,
			final int width, final int height, final String largerImagePath,
			final File imageFileDir) throws MagickException,
			FileNotFoundException {
		final MagickImage image = new MagickImage(
				new ImageInfo(largerImagePath));
		image.scaleImage(width, height);
		this.storeCompressedImage(hash, image, imageFileDir);
		return this.accessImageStream(imageFileDir, hash);
	}

}