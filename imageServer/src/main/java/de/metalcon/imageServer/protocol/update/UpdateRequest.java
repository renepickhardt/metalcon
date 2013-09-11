package de.metalcon.imageServer.protocol.update;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.metalcon.imageServer.protocol.ProtocolConstants;
import de.metalcon.utils.FormItemList;

public class UpdateRequest {

	// TODO: JavaDoc

	private static final JSONParser PARSER = new JSONParser();

	private final String imageIdentifier;

	private final String metaData;

	public UpdateRequest(final String imageIdentifier, final String metaData) {
		this.imageIdentifier = imageIdentifier;
		this.metaData = metaData;
	}

	public String getImageIdentifier() {
		return this.imageIdentifier;
	}

	public String getMetaData() {
		return this.metaData;
	}

	public static UpdateRequest checkRequest(final FormItemList formItemList,
			final UpdateResponse response) {
		final String imageIdentifier = checkImageIdentifier(formItemList,
				response);
		if (imageIdentifier != null) {
			final String metaData = checkMetaData(formItemList, response);
			if (metaData != null) {
				return new UpdateRequest(imageIdentifier, metaData);
			}
		}

		return null;
	}

	protected static String checkImageIdentifier(
			final FormItemList formItemList, final UpdateResponse response) {
		try {
			return formItemList
					.getField(ProtocolConstants.Parameters.Update.IMAGE_IDENTIFIER);
		} catch (final IllegalArgumentException e) {
			response.imageIdentifierMissing();
		}

		return null;
	}

	protected static String checkMetaData(final FormItemList formItemList,
			final UpdateResponse response) {
		try {
			final String metaData = formItemList
					.getField(ProtocolConstants.Parameters.Update.META_DATA);
			try {
				PARSER.parse(metaData);
				return metaData;
			} catch (final ParseException e) {
				response.metaDataMalformed();
			}
		} catch (final IllegalArgumentException e) {
			response.metaDataMissing();
		}

		return null;
	}

}