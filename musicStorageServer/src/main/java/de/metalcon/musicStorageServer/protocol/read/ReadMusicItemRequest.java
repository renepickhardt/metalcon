package de.metalcon.musicStorageServer.protocol.read;

import de.metalcon.musicStorageServer.MusicItemVersion;
import de.metalcon.musicStorageServer.protocol.ProtocolConstants;
import de.metalcon.utils.FormItemList;

/**
 * basic read music item request object
 * 
 * @author sebschlicht
 * 
 */
public class ReadMusicItemRequest {

	/**
	 * music item identifier
	 */
	private final String musicItemIdentifier;

	/**
	 * music item version
	 */
	private final MusicItemVersion musicItemVersion;

	/**
	 * create a new read music item request object
	 * 
	 * @param musicItemIdentifier
	 *            music item identifier
	 * @param musicItemVersion
	 *            music item version
	 */
	public ReadMusicItemRequest(final String musicItemIdentifier,
			final MusicItemVersion musicItemVersion) {
		this.musicItemIdentifier = musicItemIdentifier;
		this.musicItemVersion = musicItemVersion;
	}

	/**
	 * @return music item identifier
	 */
	public String getMusicItemIdentifier() {
		return this.musicItemIdentifier;
	}

	/**
	 * @return music item version
	 */
	public MusicItemVersion getMusicItemVersion() {
		return this.musicItemVersion;
	}

	/**
	 * check a read music item request for validity concerning MSSP
	 * 
	 * @param request
	 *            form item list extracted from the request
	 * @param response
	 *            read response object to add status messages to
	 * @return read music item request object<br>
	 *         <b>null</b> if the request is invalid
	 */
	public static ReadMusicItemRequest checkRequest(final FormItemList request,
			final ReadResponse response) {
		final String musicItemIdentifier = checkMusicItemIdentifier(request,
				response);
		if (musicItemIdentifier != null) {
			final MusicItemVersion musicItemVersion = checkMusicItemVersion(
					request, response);
			if (musicItemVersion != null) {
				return new ReadMusicItemRequest(musicItemIdentifier,
						musicItemVersion);
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
	 *            read response object
	 * @return music item identifier<br>
	 *         <b>null</b> if the music item identifier is missing
	 */
	protected static String checkMusicItemIdentifier(
			final FormItemList formItemList, final ReadResponse response) {
		try {
			return formItemList
					.getField(ProtocolConstants.Parameter.Update.MUSIC_ITEM_IDENTIFIER);
		} catch (final IllegalArgumentException e) {
			response.musicItemIdentifierMissing();
		}

		return null;
	}

	/**
	 * check if the request contains a valid music item version
	 * 
	 * @param formItemList
	 *            form item list extracted
	 * @param response
	 *            read response object
	 * @return music item version<br>
	 *         <b>null</b> if the music item version is missing or invalid
	 */
	protected static MusicItemVersion checkMusicItemVersion(
			final FormItemList formItemList, final ReadResponse response) {
		try {
			final String musicItemVersionName = formItemList
					.getField(ProtocolConstants.Parameter.Read.MUSIC_ITEM_VERSION);
			final MusicItemVersion musicItemVersion = MusicItemVersion
					.getMusicItemVersion(musicItemVersionName);

			if (musicItemVersion != null) {
				return musicItemVersion;
			}

			response.musicItemVersionInvalid(musicItemVersionName);

		} catch (final IllegalArgumentException e) {
			response.musicItemVersionMissing();
		}

		return null;
	}

}