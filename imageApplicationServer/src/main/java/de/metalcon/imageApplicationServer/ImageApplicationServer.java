package de.metalcon.imageApplicationServer;

import java.io.InputStream;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import de.metalcon.imageStorageServer.ImageStorageServer;
import de.metalcon.imageStorageServer.ScalingType;
import de.metalcon.imageStorageServer.protocol.read.ReadResponse;

public class ImageApplicationServer extends ImageStorageServer implements
		ImageApplicationServerAPI {

	public ImageApplicationServer(final String configFile) {
		super(configFile);
	}

	@Override
	@SuppressWarnings("unchecked")
	public String readScaledMetaData(final String[] imageIdentifiers,
			final int width, final int height, final ScalingType scalingType,
			final ReadResponse response) {
		final JSONObject scaledMetaData = new JSONObject();
		final JSONArray images = new JSONArray();

		JSONObject image;
		for (String imageIdentifier : imageIdentifiers) {
			String metaData = this.imageMetaDatabase
					.getMetadata(imageIdentifier);
			if (metaData != null) {
				try {
					image = (JSONObject) PARSER.parse(metaData);
					images.add(image);
				} catch (final ParseException e) {
					// TODO: internal server error
					return null;
				}
			} else {
				// error: no image with such identifier
				response.addImageNotFoundError();
				return null;
			}
		}

		scaledMetaData.put(ImageApplicationMetaData.IMAGES, images);
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