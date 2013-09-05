package de.metalcon.imageServer.protocol.delete;

import de.metalcon.imageServer.protocol.ProtocolConstants;
import de.metalcon.utils.FormItemList;

public class DeleteRequest {

	public static void checkRequest(FormItemList formItemList,
			DeleteResponse response) {
		String imageIdentifier = checkImageIdentifier(formItemList, response);
	}

	private static String checkImageIdentifier(FormItemList formItemList,
			DeleteResponse response) {
		String imageIdentifier = null;
		try {
			imageIdentifier = formItemList
					.getField(ProtocolConstants.Parameters.Delete.IMAGE_IDENTIFIER);
		} catch (IllegalArgumentException e) {
			response.addNoImageIdentifierError();
			return null;
		}
		return imageIdentifier;

	}
}