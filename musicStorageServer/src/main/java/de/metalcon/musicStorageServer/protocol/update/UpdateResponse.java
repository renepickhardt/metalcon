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
	 * add status message: meta data malformed
	 */
	public void metaDataMalformed() {
		this.addStatusMessage(
				ProtocolConstants.StatusMessage.Update.META_DATA_MALFORMED,
				"Please provide JSON formatted meta data. VorbisComment fields will be written to the files directly. Leave the field blank if you do not want to store meta data at all.");
	}

}