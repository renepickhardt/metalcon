package de.metalcon.imageStorageServer.protocol.read;

import de.metalcon.imageStorageServer.protocol.ProtocolConstants;
import de.metalcon.utils.FormItemList;

public class ReadRequest {

	private final String imageIdentifier;

	public ReadRequest(final String imageIdentifier) {
		this.imageIdentifier = imageIdentifier;
	}

	public String getImageIdentifier() {
		return this.imageIdentifier;
	}

	public static ReadRequest checkRequest(final FormItemList formItemList,
			final ReadResponse response) {
		final String imageIdentifier = checkImageIdentifier(formItemList,
				response);
		if (imageIdentifier != null) {
			return new ReadRequest(imageIdentifier);
		}
		return null;
	}

	private static String checkImageIdentifier(final FormItemList formItemList,
			final ReadResponse response) {
		try {
			return formItemList
					.getField(ProtocolConstants.Parameters.Read.IMAGE_IDENTIFIER);
		} catch (final IllegalArgumentException e) {
			response.addNoImageIdentifierError();
		}

		return null;

	}

}