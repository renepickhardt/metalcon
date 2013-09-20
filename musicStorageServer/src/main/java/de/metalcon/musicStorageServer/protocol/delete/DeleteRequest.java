package de.metalcon.musicStorageServer.protocol.delete;

import de.metalcon.musicStorageServer.protocol.ProtocolConstants;
import de.metalcon.utils.FormItemList;

/**
 * basic delete request object
 * 
 * @author sebschlicht
 * 
 */
public class DeleteRequest {

	/**
	 * music item identifier
	 */
	private final String musicItemIdentifier;

	/**
	 * create a new delete request object
	 * 
	 * @param musicItemIdentifier
	 *            music item identifier
	 */
	public DeleteRequest(final String musicItemIdentifier) {
		this.musicItemIdentifier = musicItemIdentifier;
	}

	/**
	 * @return music item identifier
	 */
	public String getMusicItemIdentifier() {
		return this.musicItemIdentifier;
	}

	/**
	 * check a delete request for validity concerning MSSP
	 * 
	 * @param request
	 *            form item list extracted from the request
	 * @param response
	 *            delete response object to add status messages to
	 * @return delete request object<br>
	 *         <b>null</b> if the request is invalid
	 */
	public static DeleteRequest checkRequest(final FormItemList request,
			final DeleteResponse response) {
		final String musicItemIdentifier = checkMusicItemIdentifier(request,
				response);
		if (musicItemIdentifier != null) {
			return new DeleteRequest(musicItemIdentifier);
		}

		return null;
	}

	/**
	 * check if the request contains a music item identifier
	 * 
	 * @param formItemList
	 *            form item list extracted
	 * @param response
	 *            delete response object
	 * @return music item identifier<br>
	 *         <b>null</b> if the music item identifier is missing
	 */
	protected static String checkMusicItemIdentifier(
			final FormItemList formItemList, final DeleteResponse response) {
		try {
			return formItemList
					.getField(ProtocolConstants.Parameter.Delete.MUSIC_ITEM_IDENTIFIER);
		} catch (final IllegalArgumentException e) {
			response.musicItemIdentifierMissing();
		}

		return null;
	}

}