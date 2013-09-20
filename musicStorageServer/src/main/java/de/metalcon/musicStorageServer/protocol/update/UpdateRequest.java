package de.metalcon.musicStorageServer.protocol.update;

import org.json.simple.parser.JSONParser;

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

	private static String checkMusicItemIdentifier(
			final FormItemList formItemList, final UpdateResponse response) {
		// TODO Auto-generated method stub
		return null;
	}

	private static String checkMetaData(final FormItemList formItemList,
			final UpdateResponse response) {
		// TODO Auto-generated method stub
		return null;
	}

}