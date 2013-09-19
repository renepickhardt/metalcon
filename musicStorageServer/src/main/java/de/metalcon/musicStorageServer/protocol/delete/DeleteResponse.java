package de.metalcon.musicStorageServer.protocol.delete;

import de.metalcon.musicStorageServer.protocol.ProtocolConstants;
import de.metalcon.musicStorageServer.protocol.Response;

public class DeleteResponse extends Response {

	/**
	 * add status message: music item not existing
	 * 
	 * @param musicItemIdentifier
	 *            music item identifier passed
	 */
	public void musicItemNotExisting(final String musicItemIdentifier) {
		this.addStatusMessage(
				ProtocolConstants.StatusMessage.Delete.MUSIC_ITEM_NOT_EXISTING,
				"There is no music item having the identifier \""
						+ musicItemIdentifier
						+ "\". Please provide an identifier used by an existing music item.");
	}

}