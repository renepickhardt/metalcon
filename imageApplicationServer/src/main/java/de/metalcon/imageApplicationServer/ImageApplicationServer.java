package de.metalcon.imageApplicationServer;

import java.io.InputStream;

import org.json.simple.JSONObject;

import de.metalcon.imageStorageServer.ImageStorageServer;
import de.metalcon.imageStorageServer.ScalingType;
import de.metalcon.imageStorageServer.protocol.read.ReadResponse;

public class ImageApplicationServer extends ImageStorageServer implements
		ImageApplicationServerAPI {

	public ImageApplicationServer(final String configFile) {
		super(configFile);
	}

	@Override
	public String readScaledMetaData(final String[] imageIdentifiers,
			final int width, final int height, final ScalingType scalingType,
			final ReadResponse response) {
		final JSONObject scaledMetaData = new JSONObject();

		for (String imageIdentifier : imageIdentifiers) {
			// TODO: check if image exisiting
			if (false) {
				// TODO: load and add meta data
			} else {
				// TODO: add error message
				return null;
			}
		}

		return scaledMetaData.toJSONString();
	}

	@Override
	public InputStream readThumbnails(final String[] imageIdentifiers,
			final int width, final int height, final ScalingType scalingType,
			final ReadResponse response) {
		// TODO Auto-generated method stub
		return null;
	}

}