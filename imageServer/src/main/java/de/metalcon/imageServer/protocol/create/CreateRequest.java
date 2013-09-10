package de.metalcon.imageServer.protocol.create;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.metalcon.imageServer.protocol.ProtocolConstants;
import de.metalcon.utils.FormFile;
import de.metalcon.utils.FormItemList;

public class CreateRequest {

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

	public static CreateRequest checkRequest(FormItemList formItemList,
			CreateResponse response) {

		final String imageIdentifier = checkImageIdentifier(formItemList,
				response);
		if (imageIdentifier != null) {
			final FormFile imageStream = checkImageStream(formItemList,
					response);
			if (imageStream != null) {
				final String metaData = checkMetaData(formItemList, response);
				if (metaData != null) {
					final Boolean autoRotateFlag = checkAutoRotateFlag(
							formItemList, response);
					if (autoRotateFlag != null) {
						return new CreateRequest(imageIdentifier, imageStream,
								metaData, autoRotateFlag);
					}
				}
			}
		}

		return null;
	}

	private static FormFile checkImageStream(FormItemList formItemList,
			CreateResponse response) {

		FormFile imageStream = null;
		try {
			imageStream = formItemList
					.getFile(ProtocolConstants.Parameters.Create.IMAGESTREAM);
		} catch (IllegalArgumentException e) {

			response.addNoImageStreamError();
			return imageStream;
		}
		return imageStream;
	}

	private static Boolean checkAutoRotateFlag(FormItemList formItemList,
			CreateResponse response) {

		String autoRotateFlag = null;
		try {
			autoRotateFlag = formItemList
					.getField(ProtocolConstants.Parameters.Create.AUTOROTATE_FLAG);
		} catch (IllegalArgumentException e) {

			response.addAutoRotateFlagMissingError(ProtocolConstants.StatusMessage.Create.AUTOROTATE_FLAG_MISSING);
			return null;
		}
		try {
			Integer autoRotateInteger = Integer.parseInt(autoRotateFlag);
			if (autoRotateInteger == 0) {
				return false;
			}
			return true;
		} catch (NumberFormatException e) {
			response.addAutoRotateFlagMalformedError();
			return null;
		}
	}

	private static String checkMetaData(FormItemList formItemList,
			CreateResponse response) {
		String metaData = null;
		try {
			metaData = formItemList
					.getField(ProtocolConstants.Parameters.Create.META_DATA);
		} catch (IllegalArgumentException e) {
			response.addNoMetadataError();
			return null;
		}
		try {
			JSONParser jsonparser = new JSONParser();
			jsonparser.parse(metaData);
		} catch (ParseException e) {
			// System.out.println(metaData + "broken");
			response.addMetadataMalformedError();
		}
		return metaData;
	}

	private static String checkImageIdentifier(FormItemList formItemList,
			CreateResponse response) {
		String imageIdentifier = null;
		try {
			imageIdentifier = formItemList
					.getField(ProtocolConstants.Parameters.Create.IMAGE_IDENTIFIER);
		} catch (IllegalArgumentException e) {
			response.addNoIdentifierError();
			return null;
		}
		return imageIdentifier;
	}

}