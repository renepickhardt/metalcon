package de.metalcon.imageServer.protocol.delete;

import de.metalcon.imageServer.protocol.ProtocolConstants;
import de.metalcon.utils.FormItemList;

public class DeleteRequest {

	// TODO: JavaDoc

	private final String imageIdentifier;

	public DeleteRequest(final String imageIdentifier) {
		this.imageIdentifier = imageIdentifier;
	}

	public String getImageIdentifier() {
		return this.imageIdentifier;
	}

	public static DeleteRequest checkRequest(final FormItemList formItemList,
			final DeleteResponse response) {
		final String imageIdentifier = checkImageIdentifier(formItemList,
				response);
		if (imageIdentifier != null) {
			return new DeleteRequest(imageIdentifier);
		}

		return null;
	}

	private static String checkImageIdentifier(final FormItemList formItemList,
			final DeleteResponse response) {
		try {
			return formItemList
					.getField(ProtocolConstants.Parameters.Delete.IMAGE_IDENTIFIER);
		} catch (final IllegalArgumentException e) {
			response.imageIdentifierMissing();
		}

		return null;

	}
}