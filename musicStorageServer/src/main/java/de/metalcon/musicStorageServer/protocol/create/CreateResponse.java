package de.metalcon.musicStorageServer.protocol.create;

import de.metalcon.musicStorageServer.protocol.ProtocolConstants;
import de.metalcon.musicStorageServer.protocol.Response;

/**
 * response object for create requests
 * 
 * @author sebschlicht
 * 
 */
public class CreateResponse extends Response {

	/**
	 * add status message: music item identifier in use
	 * 
	 * @param musicItemIdentifier
	 *            music item identifier passed
	 */
	public void musicItemIdentifierInUse(final String musicItemIdentifier) {
		this.addStatusMessage(
				ProtocolConstants.StatusMessage.Create.MUSIC_ITEM_IDENTIFIER_IN_USE,
				"there is already a music item with the identifier \""
						+ musicItemIdentifier
						+ "\" existing. Please provide an identifier that is not used yet.");
	}

	/**
	 * add status message: music item stream invalid
	 */
	public void musicItemStreamInvalid() {
		this.addStatusMessage(
				ProtocolConstants.StatusMessage.Create.MUSIC_ITEM_STREAM_INVALID,
				"The stream passed is not a supported audio stream. Please provide one of the following audio formats: MP3, OGG");
	}

	/**
	 * add status message: meta data malformed
	 */
	public void metaDataMalformed() {
		this.addStatusMessage(
				ProtocolConstants.StatusMessage.Create.META_DATA_MALFORMED,
				"Please provide JSON formatted meta data. VorbisComment fields will be written to the files directly. Leave the field blank if you do not want to store meta data at all.");
	}

}