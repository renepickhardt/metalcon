package de.metalcon.imageStorageServer;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import magick.CompressionType;
import magick.ImageInfo;
import magick.MagickException;
import magick.MagickImage;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.metalcon.imageStorageServer.protocol.ProtocolConstants;
import de.metalcon.imageStorageServer.protocol.create.CreateResponse;
import de.metalcon.imageStorageServer.protocol.delete.DeleteResponse;
import de.metalcon.imageStorageServer.protocol.read.ReadResponse;
import de.metalcon.imageStorageServer.protocol.update.UpdateResponse;

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
	 * JSON parser
	 */
	private static final JSONParser PARSER = new JSONParser();

	/**
	 * minimum hash length
	 */
	private static final int MIN_HASH_LENGTH = 6;

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
	private final String imageDirectory;

	/**
	 * temporary directory for image magic
	 */
	private final String temporaryDirectory;

	/**
	 * database for image meta data
	 */
	private final ImageMetaDatabase imageMetaDatabase;

	/**
	 * server running flag
	 */
	private boolean running = false;

	/**
	 * create a new image storage server
	 * 
	 * @param configFile
	 *            path to the configuration file
	 */
	public ImageStorageServer(final String configFile) {
		ImageMetaDatabase imageMetaDatabase = null;
		ISSConfig config = new ISSConfig(configFile);
		this.imageDirectory = config.getImageDirectory();
		this.temporaryDirectory = config.getTemporaryDirectory();

		try {
			imageMetaDatabase = new ImageMetaDatabase(config.getDatabaseHost(),
					config.getDatabasePort(), config.getDatabaseName());

			this.running = true;
		} catch (final UnknownHostException e) {
			System.err
					.println("failed to connect to the mongoDB server at "
							+ config.getDatabaseHost() + ":"
							+ config.getDatabasePort());
			e.printStackTrace();
		}

		this.imageMetaDatabase = imageMetaDatabase;
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
		final String hash = String.valueOf(key.hashCode());

		if (hash.length() < MIN_HASH_LENGTH) {
			final StringBuilder builder = new StringBuilder();
			for (int i = hash.length(); i < MIN_HASH_LENGTH; i++) {
				builder.append("0");
			}
			builder.append(hash);

			return builder.toString();
		}

		return hash;
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
		try {
			IOUtils.copy(imageInputStream, imageOutputStream);
		} finally {
			imageOutputStream.close();
		}
	}

	/**
	 * store an image
	 * 
	 * @param imageInputStream
	 *            input stream of the image
	 * @param imageFile
	 *            destination file handle
	 * @throws IOException
	 *             if the writing failed
	 */
	private static void storeImage(final InputStream imageInputStream,
			final File imageFile) throws IOException {
		// create the parental directories
		final File imageFileDir = imageFile.getParentFile();
		if (imageFileDir != null) {
			imageFileDir.mkdirs();
		}

		writeImageStream(imageInputStream, imageFile);
	}

	/**
	 * store an image using compression
	 * 
	 * @param image
	 *            magick image to be stored
	 * @param imageFile
	 *            destination file handle
	 * @throws MagickException
	 *             if the compression/writing failed
	 */
	private static void storeCompressedImage(final MagickImage image,
			final File imageFile) throws MagickException {
		// create the parental directories
		final File imageFileDir = imageFile.getParentFile();
		if (imageFileDir != null) {
			imageFileDir.mkdirs();
		}

		final ImageInfo imageInfo = new ImageInfo();
		imageInfo.setCompression(CompressionType.JPEGCompression);

		image.setFileName(imageFile.getAbsolutePath());
		image.writeImage(imageInfo);
	}

	/**
	 * check if the cropping information are valid within the image's boundary
	 * 
	 * @param image
	 *            image that will get cropped
	 * @param croppingInformation
	 *            new image frame
	 * @param response
	 *            create response object for status messages
	 * @return true - if the cropping information are valid<br>
	 *         false - otherwise
	 * @throws MagickException
	 *             if the image dimension could not be accessed
	 */
	private static boolean checkCroppingInformation(final MagickImage image,
			final ImageFrame croppingInformation, final CreateResponse response)
			throws MagickException {
		final int imageWidth = (int) image.getDimension().getWidth();
		final int imageHeight = (int) image.getDimension().getHeight();
		final int left = croppingInformation.getLeft();
		final int top = croppingInformation.getTop();
		final int width = croppingInformation.getWidth();
		final int height = croppingInformation.getHeight();

		// TODO: use more specific error messages

		if (left < 0) {
			// left too low
			response.cropLeftCoordinateInvalid(left);
		} else if (left >= imageWidth) {
			// left too high
			response.cropLeftCoordinateInvalid(left);
		} else if (top < 0) {
			// top too low
			response.cropTopCoordinateInvalid(top);
		} else if (top >= imageHeight) {
			// top too high
			response.cropTopCoordinateInvalid(top);
		} else if (width <= 0) {
			// width too low
			response.cropWidthInvalid(width);
		} else if ((left + width) > imageWidth) {
			// width too high
			response.cropWidthInvalid(width);
		} else if (height <= 0) {
			// height too low
			response.cropHeightInvalid(height);
		} else if ((top + height) > imageHeight) {
			// height too high
			response.cropHeightInvalid(height);
		} else {
			return true;
		}

		// cropping information invalid
		return false;
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
	 * @throws MagickException
	 *             if the cropping failed
	 */
	private static MagickImage cropImage(final MagickImage image,
			final int left, final int top, final int width, final int height)
			throws MagickException {
		return image.cropImage(new Rectangle(left, top, width, height));
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
	 * @param scalingType
	 *            scaling type
	 * @return scaled image
	 * @throws MagickException
	 *             if the scaling failed
	 */
	private static MagickImage scaleImage(final MagickImage image, int width,
			int height, final ScalingType scalingType) throws MagickException {

		switch (scalingType) {

		case WIDTH:
			height = (int) ((image.getDimension().getHeight() / image
					.getDimension().getWidth()) * width);
			break;

		case HEIGHT:
			width = (int) ((image.getDimension().getWidth() / image
					.getDimension().getHeight()) * height);
			break;

		default:
			// width and height have the values desired

		}

		return image.scaleImage(width, height);
	}

	/**
	 * rotate an image according to its EXIF orientation value
	 * 
	 * @param image
	 *            image to be rotated
	 * @return rotated image (or unrotated image with no/unknown orientation
	 *         value)
	 * @throws MagickException
	 *             if the rotation fails
	 */
	private static MagickImage autoRotate(final MagickImage image)
			throws MagickException {
		final String exifOrientation = "exif:Orientation";
		final String sOrientation = image.getImageAttribute(exifOrientation);
		int rotationDegree = 0;

		if (sOrientation != null) {
			final int orientation = Integer.parseInt(sOrientation);

			switch (orientation) {

			case 3:
				rotationDegree = 180;
				break;

			case 5:
				rotationDegree = -90;
				break;

			case 6:
				rotationDegree = 90;
				break;

			default:
				// image rotated correctly or mirrored

			}
		} else {
			// no EXIF data for orientation existing
		}

		if (rotationDegree != 0) {
			final MagickImage rotatedImage = image.rotateImage(rotationDegree);
			rotatedImage.setImageAttribute(exifOrientation, "1");
			return rotatedImage;
		}

		return image;
	}

	/**
	 * delete the content of a directory (clear)
	 * 
	 * @param directory
	 *            directory that shall be cleared
	 * @param removeRoot
	 *            if set the directory passed will be removed too
	 */
	private static void deleteDirectoryContent(final File directory,
			final boolean removeRoot) {
		final File[] content = directory.listFiles();
		if (content != null) {
			for (File contentItem : content) {
				if (contentItem.isDirectory()) {
					deleteDirectoryContent(contentItem, true);
				} else {
					contentItem.delete();
				}
			}
		}
		if (removeRoot) {
			directory.delete();
		}
	}

	@Override
	public boolean createImage(final String imageIdentifier,
			final InputStream imageStream, final String metaData,
			final boolean autoRotate, final CreateResponse response) {
		if (!this.imageMetaDatabase.hasEntryWithIdentifier(imageIdentifier)) {
			final String hash = generateHash(imageIdentifier);

			JSONObject metaDataJSON = null;
			if (metaData != null) {
				try {
					metaDataJSON = (JSONObject) PARSER.parse(metaData);
				} catch (final ParseException e) {
					// error: meta data format invalid
					response.metaDataMalformed();
					return false;
				}
			}

			// store and try to load original image
			MagickImage image = null;
			try {
				image = this.storeAndLoadImage(hash, imageStream);
			} catch (final MagickException e) {
				// error: image invalid
				response.imageStreamInvalid();
				return false;
			} catch (final IOException e) {
				// internal server error: failed to store original image
				response.internalServerError();
				e.printStackTrace();
				return false;
			}

			if (image != null) {
				if (autoRotate) {
					try {
						image = autoRotate(image);
					} catch (final MagickException e) {
						// internal server error: rotation failed
						response.internalServerError();
						System.err
								.println(ProtocolConstants.LogMessage.ROTATION_FAILURE);
						return false;
					}
				}

				// store basis version
				final File basisFile = this.getBasisFile(hash);
				try {
					storeCompressedImage(image, basisFile);
				} catch (final MagickException e) {
					// TODO: internal server error: compression/writing failed
					response.internalServerError();
					return false;
				}

				// create image in database
				this.imageMetaDatabase.addDatabaseEntry(imageIdentifier,
						metaDataJSON);

				// register basis size
				try {
					final int width = (int) image.getDimension().getWidth();
					final int height = (int) image.getDimension().getHeight();
					this.imageMetaDatabase.registerImageSize(imageIdentifier,
							width, height, basisFile.getAbsolutePath());

					return true;

				} catch (final MagickException e) {
					// TODO: internal server error: failed to access image
					// dimension
					response.internalServerError();
					return false;
				}
			} else {
				// internal server error: hash collision
				response.internalServerError();
				System.err.println(ProtocolConstants.LogMessage.HASH_COLLISION);
			}
		} else {
			// error: image identifier used
			response.imageIdentifierInUse(imageIdentifier);
		}

		return false;
	}

	@Override
	public boolean createCroppedImage(final String imageIdentifier,
			final InputStream imageStream, final String metaData,
			final ImageFrame croppingInformation, final CreateResponse response) {
		if (!this.imageMetaDatabase.hasEntryWithIdentifier(imageIdentifier)) {
			final String hash = generateHash(imageIdentifier);

			JSONObject metaDataJSON = null;
			if (metaData != null) {
				try {
					metaDataJSON = (JSONObject) PARSER.parse(metaData);
				} catch (final ParseException e) {
					// error: meta data format invalid
					response.metaDataMalformed();
					return false;
				}
			}

			// store and try to load original image
			MagickImage image = null;
			try {
				image = this.storeAndLoadImage(hash, imageStream);
			} catch (final MagickException e) {
				// error: image invalid
				response.imageStreamInvalid();
				return false;
			} catch (final IOException e) {
				// internal server error: failed to store original image
				response.internalServerError();
				e.printStackTrace();
				return false;
			}

			if (image != null) {
				// check the cropping information and add error messages
				boolean croppingInformationValid = false;
				try {
					croppingInformationValid = checkCroppingInformation(image,
							croppingInformation, response);
				} catch (final MagickException e) {
					// TODO: internal server error: failed to access image
					// dimension
					response.internalServerError();
					return false;
				}

				if (croppingInformationValid) {
					// crop the image
					try {
						image = cropImage(image, croppingInformation.getLeft(),
								croppingInformation.getTop(),
								croppingInformation.getWidth(),
								croppingInformation.getHeight());
					} catch (final MagickException e) {
						// internal server error: cropping failed
						response.internalServerError();
						System.err
								.println(ProtocolConstants.LogMessage.CROPPING_FAILURE);
						return false;
					}

					// store basis version
					final File basisFile = this.getBasisFile(hash);
					try {
						storeCompressedImage(image, basisFile);
					} catch (final MagickException e) {
						// TODO: internal server error: compression/writing
						// failed
						response.internalServerError();
						return false;
					}

					// create image in database
					this.imageMetaDatabase.addDatabaseEntry(imageIdentifier,
							metaDataJSON);

					// register basis size
					this.imageMetaDatabase.registerImageSize(imageIdentifier,
							croppingInformation.getWidth(),
							croppingInformation.getHeight(),
							basisFile.getAbsolutePath());

					return true;
				}
			} else {
				// internal server error: hash collision between originals
				response.internalServerError();
				System.err.println(ProtocolConstants.LogMessage.HASH_COLLISION);
			}
		} else {
			// error: image identifier used
			response.imageIdentifierInUse(imageIdentifier);
		}

		return false;
	}

	@Override
	public boolean createImageFromUrl(final String imageIdentifier,
			final String imageUrl, final String metaData,
			final CreateResponse response) {
		if (!this.imageMetaDatabase.hasEntryWithIdentifier(imageIdentifier)) {
			final String hash = generateHash(imageIdentifier);
			final File tmpImageFile = this.getTemporaryFile(hash);

			JSONObject metaDataJSON = null;
			if (metaData != null) {
				try {
					metaDataJSON = (JSONObject) PARSER.parse(metaData);
				} catch (final ParseException e) {
					// error: meta data format invalid
					response.metaDataMalformed();
					return false;
				}
			}

			URL url = null;
			try {
				url = new URL(imageUrl);
			} catch (final MalformedURLException e) {
				// error: no valid URL
				response.imageUrlMalformed(imageUrl);
				return false;
			}

			// download and store original image
			MagickImage image = null;
			try {
				image = this.storeAndLoadImage(hash, url.openStream());
			} catch (final MagickException e) {
				// TODO: check what happens if an URL throws 404/is invalid
				// error: no image file
				response.imageUrlInvalid(imageUrl);
				tmpImageFile.delete();
				return false;
			} catch (final IOException e) {
				// internal server error: failed to store image(s)
				response.internalServerError();
				return false;
			}

			if (image != null) {
				// store basis version
				final File basisFile = this.getBasisFile(hash);
				try {
					storeCompressedImage(image, basisFile);
				} catch (final MagickException e) {
					// TODO: internal server error: compression/writing
					// failed
					response.internalServerError();
					return false;
				}

				// create image in database
				this.imageMetaDatabase.addDatabaseEntry(imageIdentifier,
						metaDataJSON);

				// register basis size
				try {
					final int width = (int) image.getDimension().getWidth();
					final int height = (int) image.getDimension().getHeight();
					this.imageMetaDatabase.registerImageSize(imageIdentifier,
							width, height, basisFile.getAbsolutePath());

					return true;

				} catch (final MagickException e) {
					// TODO: internal server error: failed to access image
					// dimension
					response.internalServerError();
					return false;
				}
			} else {
				// internal server error: hash collision
				response.internalServerError();
				System.err.println(ProtocolConstants.LogMessage.HASH_COLLISION);
			}
		} else {
			// error: image identifier in use
			response.imageIdentifierInUse(imageIdentifier);
		}

		return false;
	}

	@Override
	public ImageData readOriginalImage(final String imageIdentifier,
			final ReadResponse response) {
		final String metaData = this.imageMetaDatabase
				.getMetadata(imageIdentifier);

		if (metaData != null) {
			final String hash = generateHash(imageIdentifier);

			try {
				// return the original image
				return new ImageData(metaData, new FileInputStream(
						this.getOriginalFile(hash)));
			} catch (final FileNotFoundException e) {
				// internal server error: file not found
				response.internalServerError();
				e.printStackTrace();
			}
		} else {
			// error: no image with such identifier
			response.addImageNotFoundError();
		}

		return null;
	}

	@Override
	public ImageData readImage(final String imageIdentifier,
			final ReadResponse response) {
		final String metaData = this.imageMetaDatabase
				.getMetadata(imageIdentifier);

		if (metaData != null) {
			final String hash = generateHash(imageIdentifier);

			try {
				return new ImageData(metaData, new FileInputStream(
						this.getBasisFile(hash)));
			} catch (final FileNotFoundException e) {
				// internal server error: file not found
				response.internalServerError();
				e.printStackTrace();
			}
		} else {
			// error: no image with such identifier
			response.addImageNotFoundError();
		}

		return null;
	}

	@Override
	public ImageData readScaledImage(final String imageIdentifier,
			final int width, final int height, final ScalingType scalingType,
			final ReadResponse response) {
		final String metaData = this.imageMetaDatabase
				.getMetadata(imageIdentifier);

		if (metaData != null) {
			final String hash = generateHash(imageIdentifier);

			try {
				final File scaledImageFile = this.getScaledFile(hash, width,
						height);

				if (this.imageMetaDatabase.imageHasSizeRegistered(
						imageIdentifier, width, height)) {
					// read the scaled image
					return new ImageData(metaData, new FileInputStream(
							scaledImageFile));
				} else {
					// try to create the scaled version
					final String largerImagePath = this.imageMetaDatabase
							.getSmallestImagePath(imageIdentifier, width,
									height);

					if (largerImagePath != null) {
						MagickImage basicImage = null;
						try {
							basicImage = new MagickImage(new ImageInfo(
									largerImagePath));
						} catch (final MagickException e) {
							// TODO: internal server error: failed to load basis
							// image
							response.internalServerError();
							return null;
						}

						MagickImage image = null;
						try {
							image = scaleImage(basicImage, width, height,
									scalingType);
						} catch (final MagickException e) {
							// internal server error: scaling failed
							response.internalServerError();
							System.err
									.println(ProtocolConstants.LogMessage.SCALING_FAILURE);
							return null;
						}

						try {
							storeCompressedImage(image, scaledImageFile);
							this.imageMetaDatabase.registerImageSize(
									imageIdentifier, width, height,
									scaledImageFile.getAbsolutePath());
						} catch (final MagickException e) {
							// TODO: internal server error: compression/writing
							// failed
							response.internalServerError();
							return null;
						}

						return new ImageData(metaData, new FileInputStream(
								scaledImageFile));

					} else {
						response.addGeometryBiggerThanOriginalWarning();

						// read the basis image
						return new ImageData(metaData, new FileInputStream(
								this.getBasisFile(hash)));
					}
				}
			} catch (final FileNotFoundException e) {
				// internal server error: file not found
				response.internalServerError();
				e.printStackTrace();
			}
		} else {
			// error: no image with such identifier
			response.addImageNotFoundError();
		}

		return null;
	}

	@Override
	public boolean updateImageMetaData(final String imageIdentifier,
			final String metaData, final UpdateResponse response) {
		JSONObject metaDataJSON = null;
		try {
			metaDataJSON = (JSONObject) PARSER.parse(metaData);
		} catch (final ParseException e) {
			// error: meta data format invalid
			response.metaDataMalformed();
			return false;
		}

		final boolean successFlag = this.imageMetaDatabase.appendMetadata(
				imageIdentifier, metaDataJSON);
		if (!successFlag) {
			// error: no image with such identifier
			response.imageNotExisting(imageIdentifier);
		}

		return successFlag;
	}

	@Override
	public boolean deleteImage(final String imageIdentifier,
			final DeleteResponse response) {
		final String[] imagePaths = this.imageMetaDatabase
				.getRegisteredImagePaths(imageIdentifier);

		if (imagePaths != null) {
			// TODO: do we delete original images?
			// TODO: IF we do we have to register the path if we do not expect
			// creation and deletion to happen at the same day

			// delete all image versions registered
			File imageFile;
			for (String imagePath : imagePaths) {
				imageFile = new File(imagePath);
				imageFile.delete();
			}

			this.imageMetaDatabase.deleteDatabaseEntry(imageIdentifier);
			return true;
		} else {
			// error: no image with such identifier
			response.imageNotExisting(imageIdentifier);
		}

		return false;
	}

	/**
	 * generate the file handle to the original version of an image
	 * 
	 * @param hash
	 *            image identifier hash
	 * @return file handle to the original version of the image passed
	 */
	private File getOriginalFile(final String hash) {
		updateDateLabels();
		return new File(this.imageDirectory + "originals" + File.separator
				+ FORMATTED_YEAR + File.separator + FORMATTED_DAY
				+ File.separator + getRelativeDirectory(hash, 2), hash);
	}

	/**
	 * generate the file handle to the basis version of an image
	 * 
	 * @param hash
	 *            image identifier hash
	 * @return file handle to the basis version of the image passed
	 */
	private File getBasisFile(final String hash) {
		return new File(this.imageDirectory + getRelativeDirectory(hash, 3)
				+ File.separator + "basis", hash + ".jpg");
	}

	/**
	 * generate the file handle to the temporary version of an image
	 * 
	 * @param hash
	 *            image identifier hash
	 * @return file handle to the temporary version of the image passed
	 */
	private File getTemporaryFile(final String hash) {
		return new File(this.temporaryDirectory, hash);
	}

	/**
	 * generate the file handle to a scaled version of an image
	 * 
	 * @param hash
	 *            image identifier hash
	 * @param width
	 *            target width
	 * @param height
	 *            target height
	 * @return file handle to the scaled version of the image passed
	 */
	private File getScaledFile(final String hash, final int width,
			final int height) {
		return new File(this.imageDirectory + getRelativeDirectory(hash, 3)
				+ File.separator + String.valueOf(width) + "x"
				+ String.valueOf(height), hash + ".jpg");
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
		final File tmpFile = this.getTemporaryFile(hash);

		FileInputStream tmpInputStream = null;
		if (!tmpFile.exists()) {
			storeImage(imageStream, tmpFile);
			imageStream.close();

			try {
				final MagickImage originalImage = new MagickImage(
						new ImageInfo(tmpFile.getAbsolutePath()));

				// save the original image to the disk
				tmpInputStream = new FileInputStream(tmpFile);
				final File originalFile = this.getOriginalFile(hash);

				if (!originalFile.exists()) {
					storeImage(tmpInputStream, originalFile);
					return originalImage;
				}
			} finally {
				// close temporary input stream
				if (tmpInputStream != null) {
					tmpInputStream.close();
				}

				// delete temporary (image) file
				tmpFile.delete();
			}
		}

		// collision between original image files
		return null;
	}

	/**
	 * @return server running flag
	 */
	public boolean isRunning() {
		return this.running;
	}

	/**
	 * clear the server<br>
	 * <b>removes all images and meta data</b>
	 */
	public void clear() {
		deleteDirectoryContent(new File(this.imageDirectory), false);
		deleteDirectoryContent(new File(this.temporaryDirectory), false);
		this.imageMetaDatabase.clear();
	}

}