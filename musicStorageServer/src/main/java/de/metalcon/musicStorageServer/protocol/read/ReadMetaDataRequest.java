package de.metalcon.musicStorageServer.protocol.read;

import de.metalcon.musicStorageServer.protocol.ProtocolConstants;
import de.metalcon.utils.FormItemList;

/**
 * basic read meta data request object
 * 
 * @author sebschlicht
 * 
 */
public class ReadMetaDataRequest {

	/**
	 * music item identifiers
	 */
	private final String[] musicItemIdentifiers;

	/**
	 * create a new read music item request object
	 * 
	 * @param musicItemIdentifiers
	 *            music item identifiers
	 */
	public ReadMetaDataRequest(final String[] musicItemIdentifiers) {
		this.musicItemIdentifiers = musicItemIdentifiers;
	}

	/**
	 * @return music item identifiers
	 */
	public String[] getMusicItemIdentifiers() {
		return this.musicItemIdentifiers;
	}

	/**
	 * check a read meta data request for validity concerning MSSP
	 * 
	 * @param request
	 *            form item list extracted from the request
	 * @param response
	 *            read response object to add status messages to
	 * @return read meta data request object<br>
	 *         <b>null</b> if the request is invalid
	 */
	public static ReadMetaDataRequest checkRequest(final FormItemList request,
			final ReadResponse response) {
		final String[] musicItemIdentifiers = checkMusicItemIdentifiers(
				request, response);
		if (musicItemIdentifiers != null) {
			return new ReadMetaDataRequest(musicItemIdentifiers);
		}

		return null;
	}

	/**
	 * check if the request contains music item identifiers
	 * 
	 * @param formItemList
	 *            form item list extracted
	 * @param response
	 *            read response object
	 * @return music item identifiers<br>
	 *         <b>null</b> if the music item identifiers are missing
	 */
	protected static String[] checkMusicItemIdentifiers(
			final FormItemList formItemList, final ReadResponse response) {
		try {
			return formItemList.getField(
					ProtocolConstants.Parameter.Read.MUSIC_ITEM_IDENTIFIERS)
					.split(",");
		} catch (final IllegalArgumentException e) {
			response.musicItemIdentifiersMissing();
		}

		return null;
	}
}