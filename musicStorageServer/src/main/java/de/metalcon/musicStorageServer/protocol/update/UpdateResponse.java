package de.metalcon.musicStorageServer.protocol.update;

import de.metalcon.musicStorageServer.protocol.ProtocolConstants;
import de.metalcon.musicStorageServer.protocol.Response;

/**
 * response object for update requests
 * 
 * @author sebschlicht
 * 
 */
public class UpdateResponse extends Response {

	/**
	 * add status message: music item identifier missing
	 */
	public void musicItemIdentifierMissing() {
		this.parameterMissing(
				ProtocolConstants.Parameter.Update.MUSIC_ITEM_IDENTIFIER,
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
				ProtocolConstants.StatusMessage.Update.MUSIC_ITEM_NOT_EXISTING,
				"There is no music item having the identifier \""
						+ musicItemIdentifier
						+ "\". Please provide an identifier used by an existing music item.");
	}

	/**
	 * add status message: meta data missing
	 */
	public void metaDataMissing() {
		this.parameterMissing(ProtocolConstants.Parameter.Update.META_DATA,
				"Please provide JSON formatted meta data to be updated for the music item.");
	}

	/**
	 * add status message: meta data malformed
	 */
	public void metaDataMalformed() {
		this.addStatusMessage(
				ProtocolConstants.StatusMessage.Update.META_DATA_MALFORMED,
				"Please provide JSON formatted meta data.");
	}

}