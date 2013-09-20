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
	ORIGINAL("original"),

	/**
	 * basis file having the maximum quality available in OGG
	 */
	BASIS("basis"),

	/**
	 * OGG file having a low bit rate used for streaming
	 */
	STREAM("stream");

	/**
	 * human readable name
	 */
	private final String name;

	/**
	 * create a new music item version
	 * 
	 * @param name
	 *            human readable name
	 */
	private MusicItemVersion(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}

	/**
	 * get a music item version by its name
	 * 
	 * @param name
	 *            name of the music item
	 * @return music item version with the name passed<br>
	 *         <b>null</b> if there is no music item version having such name
	 */
	public static MusicItemVersion getMusicItemVersion(final String name) {
		if (ORIGINAL.toString().equals(name)) {
			return ORIGINAL;
		} else if (BASIS.toString().equals(name)) {
			return BASIS;
		} else if (STREAM.toString().equals(name)) {
			return STREAM;
		}

		return null;
	}

}