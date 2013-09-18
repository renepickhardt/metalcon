package de.metalcon.musicStorageServer;

/**
 * music item version a request is according to
 * 
 * @author sebschlicht
 * 
 */
public enum MusicItemVersion {

	/**
	 * original file uploaded
	 */
	ORIGINAL,

	/**
	 * basis file having the maximum quality available in OGG
	 */
	BASIS,

	/**
	 * OGG file having a low bit rate used for streaming
	 */
	STREAM;

}