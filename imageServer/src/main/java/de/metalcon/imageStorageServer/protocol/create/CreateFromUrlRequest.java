package de.metalcon.imageStorageServer.protocol.create;

import java.net.MalformedURLException;
import java.net.URL;

import de.metalcon.imageStorageServer.protocol.ProtocolConstants;
import de.metalcon.utils.FormItemList;

public class CreateFromUrlRequest {

	@SuppressWarnings("unused")
	private final URL imageUrl;

	public CreateFromUrlRequest(final URL imageUrl) {
		this.imageUrl = imageUrl;
	}

	public static CreateFromUrlRequest checkRequest(
			final FormItemList formItemList, final CreateResponse response) {

		final URL imageUrl = checkUrl(formItemList, response);
		if (imageUrl != null) {
			return new CreateFromUrlRequest(imageUrl);
		}
		return null;
	}

	private static URL checkUrl(final FormItemList formItemList,
			final CreateResponse response) {
		String imageUrlString = null;
		try {
			imageUrlString = formItemList
					.getField(ProtocolConstants.Parameters.Create.URL);
			URL imageUrl = new URL(imageUrlString);
			return imageUrl;
		} catch (IllegalArgumentException e1) {
			response.imageIdentifierMissing();
		} catch (MalformedURLException e2) {
			response.imageUrlMalformed(imageUrlString);
		}
		return null;
	}

}
