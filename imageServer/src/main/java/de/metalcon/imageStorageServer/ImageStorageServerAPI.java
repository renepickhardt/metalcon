package de.metalcon.imageStorageServer;

import java.io.InputStream;

import de.metalcon.imageStorageServer.protocol.create.CreateResponse;
import de.metalcon.imageStorageServer.protocol.delete.DeleteResponse;
import de.metalcon.imageStorageServer.protocol.read.ReadResponse;
import de.metalcon.imageStorageServer.protocol.update.UpdateResponse;

/**
 * java interface for the image storage server API
 * 
 * @author sebschlicht
 * 
 */
public interface ImageStorageServerAPI {

	/**
	 * create an image in its actual size
	 * 
	 * @param imageIdentifier
	 *            unused image identifier
	 * @param imageStream
	 *            input stream of the binary image
	 * @param metaData
	 *            optional meta data (JSON)
	 * @param autoRotate
	 *            enables the optional image auto rotation according to its EXIF
	 *            information if set to <b>true</b>
	 * @param response
	 *            create response object for status messages
	 * @return true - if the creation was successful<br>
	 * 
	 *         false - otherwise
	 */
	boolean createImage(String imageIdentifier, InputStream imageStream,
			String metaData, boolean autoRotate, CreateResponse response);

	/**
	 * create an image using only a certain frame of it
	 * 
	 * @param imageIdentifier
	 *            unused image identifier
	 * @param imageStream
	 *            input stream of the binary image
	 * @param metaData
	 *            optional meta data (JSON)
	 * @param croppingInformation
	 *            cropping information specifying the used image frame
	 * @param response
	 *            create response object for status messages
	 * @return true - if the creation was successful<br>
	 *         false - otherwise
	 */
	boolean createCroppedImage(String imageIdentifier, InputStream imageStream,
			String metaData, ImageFrame croppingInformation,
			CreateResponse response);

	/**
	 * create an image in its actual size using an URL
	 * 
	 * @param imageIdentifier
	 *            unused image identifier
	 * @param imageUrl
	 *            URL to the existing image
	 * @param metaData
	 *            optional meta data (JSON)
	 * @param response
	 *            create response object for status messages
	 * @return true - if the creation was successful<br>
	 *         false - otherwise
	 */
	boolean createImageFromUrl(String imageIdentifier, String imageUrl,
			String metaData, CreateResponse response);

	/**
	 * read the original image and its meta data
	 * 
	 * @param imageIdentifier
	 *            identifier of an existing image
	 * @param response
	 *            read response object for status messages
	 * @return stream and meta data of the original image<br>
	 *         <b>null</b> if there is no image with such identifier or internal
	 *         errors occurred
	 */
	ImageData readOriginalImage(String imageIdentifier, ReadResponse response);

	/**
	 * read the basis image<br>
	 * (compressed image including rotation and cropping)
	 * 
	 * @param imageIdentifier
	 *            identifier of an existing image
	 * @param response
	 *            read response object for status messages
	 * @return stream and meta data of the basis image<br>
	 *         <b>null</b> if there is no image with such identifier or internal
	 *         errors occurred
	 */
	ImageData readImage(String imageIdentifier, ReadResponse response);

	/**
	 * read a scaled version of the basis image
	 * 
	 * @param imageIdentifier
	 *            identifier of an existing image
	 * @param width
	 *            width of the new image frame
	 * @param height
	 *            height of the new image frame
	 * @param scalingType
	 *            specifies the way the scaling should happen
	 * @param response
	 *            read response object for status messages
	 * @return stream and meta data of the scaled basis image<br>
	 *         stream and meta data of the basis image if the size requested was
	 *         larger than the basis image<br>
	 *         <b>null</b> if there is no image with such identifier or internal
	 *         errors occurred
	 */
	ImageData readScaledImage(String imageIdentifier, int width, int height,
			ScalingType scalingType, ReadResponse response);

	/**
	 * update the meta data stored for an image
	 * 
	 * @param imageIdentifier
	 *            identifier of an existing image
	 * @param metaData
	 *            meta data that shall be appended (JSON)
	 * @param response
	 *            update response object for status messages
	 * @return true - if the update was successful<br>
	 *         false - if there is no image with such identifier
	 */
	boolean updateImageMetaData(String imageIdentifier, String metaData,
			UpdateResponse response);

	/**
	 * delete an image
	 * 
	 * @param imageIdentifier
	 *            identifier of an existing image
	 * @param response
	 *            delete response object for status messages
	 * @return true - if the deletion was successful<br>
	 *         false - if there is no image with such identifier
	 */
	boolean deleteImage(String imageIdentifier, DeleteResponse response);

}