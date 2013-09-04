package de.metalcon.imageServer.protocol.read;

import de.metalcon.imageServer.protocol.ProtocolConstants;
import de.metalcon.utils.FormItemList;

public class ReadRequest {

	public static void checkRequest(FormItemList formItemList,
			ReadResponse readResponse) {
		String imageIdentifier = checkImageIdentifier(formItemList,
				readResponse);

	}

	private static String checkImageIdentifier(FormItemList formItemList,
			ReadResponse response) {
		String imageIdentifier = null;
		try {
			imageIdentifier = formItemList
					.getField(ProtocolConstants.Parameters.Create.META_DATA);
		} catch (IllegalArgumentException e) {
			response.addNoImageIdentifierError(ProtocolConstants.StatusMessage.Read.IMAGE_IDENTIFIER_MISSING);
			return null;
		}
		return imageIdentifier;

	}

}