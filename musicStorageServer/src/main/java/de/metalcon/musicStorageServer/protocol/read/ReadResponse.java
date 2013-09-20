package de.metalcon.musicStorageServer.protocol.read;

import de.metalcon.musicStorageServer.protocol.ProtocolConstants;
import de.metalcon.musicStorageServer.protocol.Response;

public class ReadResponse extends Response {

	protected static final String MUSIC_ITEM_VERSIONS_ALLOWED = "\"original\" to retrieve the original music file uploaded, "
			+ "\"basis\" to retrieve the compressed version having the best quality besides the original file or "
			+ "\"stream\" to retrieve the version for streaming having a low(er) bit rate";

	/**
	 * add status message: music item identifier missing
	 */
	public void musicItemIdentifierMissing() {
		this.parameterMissing(
				ProtocolConstants.Parameter.Read.MUSIC_ITEM_IDENTIFIER,
				"Please provide a music item identifier that is used by an existing music item.");
	}

	/**
	 * add status message: music item not existing
	 * 
	 * @param musicItemIdentifier
	 *            music item identifier passed
	 */
	public void musicItemNotExisting(final String musicItemIdentifier) {
		this.addStatusMessage(
				ProtocolConstants.StatusMessage.Read.MUSIC_ITEM_NOT_EXISTING,
				"There is no music item having the identifier \""
						+ musicItemIdentifier
						+ "\". Please provide an identifier used by an existing music item.");
	}

	/**
	 * add status message: music item version missing
	 */
	public void musicItemVersionMissing() {
		this.parameterMissing(
				ProtocolConstants.Parameter.Read.MUSIC_ITEM_VERSION,
				"Please provide the version of the music item requested. Please pass "
						+ MUSIC_ITEM_VERSIONS_ALLOWED + ".");
	}

	/**
	 * add status message: music item version invalid
	 * 
	 * @param musicItemVersionName
	 *            music item version passed
	 */
	public void musicItemVersionInvalid(final String musicItemVersionName) {
		this.addStatusMessage(
				ProtocolConstants.StatusMessage.Read.MUSIC_ITEM_VERSION_INVALID,
				"\"" + musicItemVersionName
						+ "\" is not a valid music item version! Please pass "
						+ MUSIC_ITEM_VERSIONS_ALLOWED + ".");
	}

	/**
	 * add status message: music item identifiers missing
	 */
	public void musicItemIdentifiersMissing() {
		this.parameterMissing(
				ProtocolConstants.Parameter.Read.MUSIC_ITEM_IDENTIFIERS,
				"Please provide a comma separated list of identifiers you want to retrieve meta data for.");
	}

}