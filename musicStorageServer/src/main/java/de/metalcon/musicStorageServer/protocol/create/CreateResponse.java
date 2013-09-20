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
	 * audio formats supported
	 */
	protected final String SUPPORTED_AUDIO_FORMATS = "MP3, OGG";

	/**
	 * add status message: music item identifier missing
	 */
	public void musicItemIdentifierMissing() {
		this.parameterMissing(
				ProtocolConstants.Parameter.Create.MUSIC_ITEM_IDENTIFIER,
				"Please provide a music item identifier that is not used by an existing music item.");
	}

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
	 * add status message: music item missing
	 */
	public void musicItemMissing() {
		this.parameterMissing(ProtocolConstants.Parameter.Create.MUSIC_ITEM,
				"Please provide an audio file having one of the following formats: "
						+ this.SUPPORTED_AUDIO_FORMATS);
	}

	/**
	 * add status message: music item stream invalid
	 */
	public void musicItemStreamInvalid() {
		this.addStatusMessage(
				ProtocolConstants.StatusMessage.Create.MUSIC_ITEM_STREAM_INVALID,
				"The stream passed is not a supported audio stream. Please provide one of the following audio formats: "
						+ this.SUPPORTED_AUDIO_FORMATS);
	}

	/**
	 * add status message: meta data missing
	 */
	public void metaDataMissing() {
		this.parameterMissing(
				ProtocolConstants.Parameter.Create.META_DATA,
				"Please provide JSON formatted meta data to be stored for the music item. VorbisComment fields will be written to the files directly. Leave the field blank if you do not want to store meta data at all.");
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