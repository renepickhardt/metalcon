package de.metalcon.imageServer.protocol.update;

import org.json.simple.JSONObject;

import de.metalcon.imageServer.protocol.ProtocolConstants;
import de.metalcon.utils.FormItemList;

public class UpdateRequest {

	public static void checkRequest(FormItemList formItemList,
			UpdateResponse response) {
		String imageIdentifier = checkImageIdentifier(formItemList, response);
		String metaData = checkMetaData(formItemList, response);
	}

	private static String checkMetaData(FormItemList formItemList,
			UpdateResponse response) {
		String metaData = null;
		try {
			metaData = formItemList
					.getField(ProtocolConstants.Parameters.Update.META_DATA);
		} catch (IllegalArgumentException e) {
			response.addNoMetadataError();
			return null;
		}
		try {
			JSONObject metaDataJson = new JSONObject();
			String[] jsonParts = metaData.split(":");
			metaDataJson.put(jsonParts[0], jsonParts[1]);
		} catch (ArrayIndexOutOfBoundsException e) {
			response.addMetadataMalformedError();
		}
		return metaData;
	}

	private static String checkImageIdentifier(FormItemList formItemList,
			UpdateResponse response) {
		String imageIdentifier = null;
		try {
			imageIdentifier = formItemList
					.getField(ProtocolConstants.Parameters.Update.IMAGE_IDENTIFIER);
		} catch (IllegalArgumentException e) {
			response.addNoImageIdentifierError();
			return null;
		}
		return imageIdentifier;

	}

}