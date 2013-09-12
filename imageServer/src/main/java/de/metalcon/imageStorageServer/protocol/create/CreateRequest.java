package de.metalcon.imageStorageServer.protocol.create;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.metalcon.imageStorageServer.protocol.ProtocolConstants;
import de.metalcon.utils.FormFile;
import de.metalcon.utils.FormItemList;

public class CreateRequest {

	// TODO: JavaDoc

	private static final JSONParser PARSER = new JSONParser();

	private final String imageIdentifier;

	private final FormFile imageStream;

	private final String metaData;

	private final boolean autoRotateFlag;

	public CreateRequest(final String imageIdentifier,
			final FormFile imageStream, final String metaData,
			final boolean autoRotateFlag) {
		this.imageIdentifier = imageIdentifier;
		this.imageStream = imageStream;
		this.metaData = metaData;
		this.autoRotateFlag = autoRotateFlag;
	}

	public String getImageIdentifier() {
		return this.imageIdentifier;
	}

	public FormFile getImageStream() {
		return this.imageStream;
	}

	public String getMetaData() {
		return this.metaData;
	}

	public boolean isAutoRotateFlag() {
		return this.autoRotateFlag;
	}

	public static CreateRequest checkRequest(final FormItemList formItemList,
			final CreateResponse response) {

		final String imageIdentifier = checkImageIdentifier(formItemList,
				response);
		if (imageIdentifier != null) {
			final FormFile imageStream = checkImageStream(formItemList,
					response);
			if (imageStream != null) {
				final String metaData = checkMetaData(formItemList, response);
				final Boolean autoRotateFlag = checkAutoRotateFlag(
						formItemList, response);
				if (autoRotateFlag != null) {
					return new CreateRequest(imageIdentifier, imageStream,
							metaData, autoRotateFlag);
				}
			}
		}

		return null;
	}

	protected static String checkImageIdentifier(
			final FormItemList formItemList, final CreateResponse response) {
		try {
			return formItemList
					.getField(ProtocolConstants.Parameters.Create.IMAGE_IDENTIFIER);
		} catch (final IllegalArgumentException e) {
			response.imageIdentifierMissing();
		}

		return null;
	}

	protected static FormFile checkImageStream(final FormItemList formItemList,
			final CreateResponse response) {
		try {
			return formItemList
					.getFile(ProtocolConstants.Parameters.Create.IMAGESTREAM);
		} catch (final IllegalArgumentException e) {
			response.imageStreamMissing();
		}

		return null;
	}

	protected static Boolean checkAutoRotateFlag(
			final FormItemList formItemList, final CreateResponse response) {
		try {
			final String autoRotateFlag = formItemList
					.getField(ProtocolConstants.Parameters.Create.AUTOROTATE_FLAG);
			try {
				return (Integer.parseInt(autoRotateFlag) != 0);
			} catch (final NumberFormatException e) {
				response.autoRotateFlagMalformed(autoRotateFlag);
			}
		} catch (final IllegalArgumentException e) {
			response.autoRotateFlagMissing();
		}

		return null;
	}

	protected static String checkMetaData(final FormItemList formItemList,
			final CreateResponse response) {
		try {
			final String metaData = formItemList
					.getField(ProtocolConstants.Parameters.Create.META_DATA);
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