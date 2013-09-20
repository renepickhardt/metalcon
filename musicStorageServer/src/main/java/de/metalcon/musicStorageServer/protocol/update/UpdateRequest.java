package de.metalcon.musicStorageServer.protocol.update;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.metalcon.musicStorageServer.protocol.ProtocolConstants;
import de.metalcon.utils.FormItemList;

/**
 * basic update request object
 * 
 * @author sebschlicht
 * 
 */
public class UpdateRequest {

	/**
	 * JSON parser
	 */
	protected static final JSONParser PARSER = new JSONParser();

	/**
	 * music item identifier
	 */
	private final String musicItemIdentifier;

	/**
	 * meta data to be stored
	 */
	private final String metaData;

	/**
	 * create a new update request object
	 * 
	 * @param musicItemIdentifier
	 *            music item identifier
	 * @param metaData
	 *            meta data to be stored
	 */
	public UpdateRequest(final String musicItemIdentifier, final String metaData) {
		this.musicItemIdentifier = musicItemIdentifier;
		this.metaData = metaData;
	}

	/**
	 * @return music item identifier
	 */
	public String getMusicItemIdentifier() {
		return this.musicItemIdentifier;
	}

	/**
	 * @return meta data to be stored
	 */
	public String getMetaData() {
		return this.metaData;
	}

	/**
	 * check a update request for validity concerning MSSP
	 * 
	 * @param request
	 *            form item list extracted from the request
	 * @param response
	 *            update response object to add status messages to
	 * @return update request object<br>
	 *         <b>null</b> if the request is invalid
	 */
	public static UpdateRequest checkRequest(final FormItemList request,
			final UpdateResponse response) {
		final String musicItemIdentifier = checkMusicItemIdentifier(request,
				response);
		if (musicItemIdentifier != null) {
			final String metaData = checkMetaData(request, response);
			if (metaData != null) {
				return new UpdateRequest(musicItemIdentifier, metaData);
			}
		}

		return null;
	}

	/**
	 * check if the request contains a music item identifier
	 * 
	 * @param formItemList
	 *            form item list extracted
	 * @param response
	 *            update response object
	 * @return music item identifier<br>
	 *         <b>null</b> if the music item identifier is missing
	 */
	protected static String checkMusicItemIdentifier(
			final FormItemList formItemList, final UpdateResponse response) {
		try {
			return formItemList
					.getField(ProtocolConstants.Parameter.Update.MUSIC_ITEM_IDENTIFIER);
		} catch (final IllegalArgumentException e) {
			response.musicItemIdentifierMissing();
		}

		return null;
	}

	/**
	 * check if the request contains valid meta data
	 * 
	 * @param formItemList
	 *            form item list extracted
	 * @param response
	 *            update response object
	 * @return valid meta data<br>
	 *         <b>null</b> if the meta data is missing or malformed
	 */
	protected static String checkMetaData(final FormItemList formItemList,
			final UpdateResponse response) {
		try {
			final String metaData = formItemList
					.getField(ProtocolConstants.Parameter.Update.META_DATA);
			PARSER.parse(metaData);
			return metaData;
		} catch (final IllegalArgumentException e) {
			response.metaDataMissing();
		} catch (final ParseException e) {
			response.metaDataMalformed();
		}

		return null;
	}

}