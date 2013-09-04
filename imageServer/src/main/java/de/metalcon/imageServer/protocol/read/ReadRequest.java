package de.metalcon.imageServer.protocol.read;

import de.metalcon.imageServer.protocol.ProtocolConstants;
import de.metalcon.utils.FormItemList;

public class ReadRequest {

	public static void checkRequest(FormItemList formItemList,
			ReadResponse readResponse) {
		String imageIdentifier = checkImageIdentifier(formItemList,
				readResponse);
		Boolean originalImageFlag = checkOriginalImageFlag(formItemList,
				readResponse);
	}

	private static Boolean checkOriginalImageFlag(FormItemList formItemList,
			ReadResponse response) {
		String originalImageFlagString = null;
		try {
			// FIXME: Field is null but should contain flag
			System.out.println(formItemList
					.getField(ProtocolConstants.Parameters.Read.ORIGINAL_FLAG));
			originalImageFlagString = formItemList
					.getField(ProtocolConstants.Parameters.Read.ORIGINAL_FLAG);
		} catch (IllegalArgumentException e) {
			System.out.println("ouch");
			response.addNoOriginalFlagError();
			return null;
		}
		try {
			Integer originalImageFlagInteger = Integer
					.parseInt(originalImageFlagString);
			if (originalImageFlagInteger == 0) {
				return false;
			}
			return true;
		} catch (NumberFormatException e) {
			response.addOriginalImageFlagMalformedError();
			return null;
		}
	}

	private static String checkImageIdentifier(FormItemList formItemList,
			ReadResponse response) {
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