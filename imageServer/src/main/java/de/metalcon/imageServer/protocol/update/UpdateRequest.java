package de.metalcon.imageServer.protocol.update;

import de.metalcon.imageServer.protocol.ProtocolConstants;
import de.metalcon.utils.FormItemList;

public class UpdateRequest {

	public static void checkRequest(FormItemList formItemList,
			UpdateResponse response) {
		String imageIdentifier = checkImageIdentifier(formItemList, response);
	}

	private static String checkImageIdentifier(FormItemList formItemList,
			UpdateResponse response) {
		String imageIdentifier = null;
		try {
			imageIdentifier = formItemList
					.getField(ProtocolConstants.Parameters.Read.IMAGE_IDENTIFIER);
		} catch (IllegalArgumentException e) {
			response.addNoImageIdentifierError();
			return null;
		}
		return imageIdentifier;

	}

}