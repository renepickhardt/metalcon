package de.metalcon.musicStorageServer;

import java.io.InputStream;

/**
 * music data wrapper holding the music item stream and the meta data stored
 * 
 * @author sebschlicht
 * 
 */
public class MusicData {

	/**
	 * stream of the music item
	 */
	private final InputStream musicItemStream;

	/**
	 * meta data stored for the music item
	 */
	private final String metaData;

	/**
	 * create a new music data wrapper
	 * 
	 * @param musicItemStream
	 *            stream of the music item
	 * @param metaData
	 *            meta data stored for the music item
	 */
	public MusicData(final InputStream musicItemStream, final String metaData) {
		this.musicItemStream = musicItemStream;
		this.metaData = metaData;
	}

	/**
	 * @return stream of the music item
	 */
	public InputStream getMusicItemStream() {
		return this.musicItemStream;
	}

	/**
	 * @return meta data stored for the music item
	 */
	public String getMetaData() {
		return this.metaData;
	}

}