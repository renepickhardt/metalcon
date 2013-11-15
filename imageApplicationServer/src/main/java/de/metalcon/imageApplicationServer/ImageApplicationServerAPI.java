package de.metalcon.imageApplicationServer;

import java.io.InputStream;

import de.metalcon.imageStorageServer.ImageStorageServerAPI;
import de.metalcon.imageStorageServer.ScalingType;
import de.metalcon.imageStorageServer.protocol.read.ReadResponse;

/**
 * java interface for the image application server API<br>
 * <br>
 * <b>warning</b>: includes methods that will not be accessible via HTTP
 * requests
 * 
 * @author sebschlicht
 * 
 */
public interface ImageApplicationServerAPI extends ImageStorageServerAPI {

	String readScaledMetaData(String[] imageIdentifiers, int width, int height,
			ScalingType scalingType, ReadResponse response);

	InputStream readThumbnails(String[] imageIdentifiers, int width,
			int height, ScalingType scalingType, ReadResponse response);

}