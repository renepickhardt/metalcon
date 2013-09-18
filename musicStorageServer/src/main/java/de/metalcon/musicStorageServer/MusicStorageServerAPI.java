package de.metalcon.musicStorageServer;

import java.io.InputStream;

import de.metalcon.musicStorageServer.protocol.create.CreateResponse;
import de.metalcon.musicStorageServer.protocol.delete.DeleteResponse;
import de.metalcon.musicStorageServer.protocol.read.ReadResponse;
import de.metalcon.musicStorageServer.protocol.update.UpdateResponse;

/**
 * API for the embedded music storage server
 * 
 * @author sebschlicht
 * 
 */
public interface MusicStorageServerAPI {

	/**
	 * create a music item
	 * 
	 * @param musicItemIdentifier
	 *            identifier of the new music item
	 * @param musicItemStream
	 *            stream of the music item
	 * @param metaData
	 *            additional optional meta data
	 * @param response
	 *            create response object
	 * @return true - if the music item has been created successfully<br>
	 *         false - otherwise
	 */
	boolean createMusicItem(String musicItemIdentifier,
			InputStream musicItemStream, String metaData,
			CreateResponse response);

	/**
	 * read a music item and its meta data stored
	 * 
	 * @param musicItemIdentifier
	 *            identifier of the music item
	 * @param version
	 *            music item version requested to read
	 * @param response
	 *            read response object
	 * @return music data encapsulating the music item stream and its meta data<br>
	 *         <b>null</b> if the identifier passed is invalid
	 */
	MusicData readMusicItem(String musicItemIdentifier,
			MusicItemVersion version, ReadResponse response);

	/**
	 * read the meta data for any number of music items
	 * 
	 * @param musicItemIdentifiers
	 *            list containing the identifiers of all music item the meta
	 *            data shall be retrieved of
	 * @param response
	 *            read response object
	 * @return list of meta data for the music items sorted like the identifier
	 *         list passed
	 */
	String[] readMusicItemMetaData(String[] musicItemIdentifiers,
			ReadResponse response);

	/**
	 * update the meta data for a music item
	 * 
	 * @param musicItemIdentifier
	 *            identifier of the music item
	 * @param metaData
	 *            meta data that shall be updated/appended
	 * @param response
	 *            update response object
	 * @return true - if the meta data has been updated/appended successfully<br>
	 *         false - otherwise
	 */
	boolean updateMetaData(final String musicItemIdentifier,
			final String metaData, final UpdateResponse response);

	/**
	 * delete a music item
	 * 
	 * @param musicItemIdentifier
	 *            identifier of the music item
	 * @return true - if the music item has been deleted successfully<br>
	 *         false - if the identifier passed is invalid
	 */
	boolean deleteMusicItem(String musicItemIdentifier, DeleteResponse response);

}