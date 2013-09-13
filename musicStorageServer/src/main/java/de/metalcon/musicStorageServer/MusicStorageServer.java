package de.metalcon.musicStorageServer;

import java.io.File;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.xiph.libvorbis.vorbisenc;

import de.metalcon.musicStorageServer.protocol.create.CreateResponse;
import de.metalcon.musicStorageServer.protocol.delete.DeleteResponse;
import de.metalcon.musicStorageServer.protocol.read.ReadResponse;
import de.metalcon.musicStorageServer.protocol.update.UpdateResponse;

public class MusicStorageServer implements MusicStorageServerAPI {

	/**
	 * date formatter
	 */
	private static final Format FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

	private static final vorbisenc ENCODER = new vorbisenc();

	/**
	 * year represented by the current formatted year
	 */
	private static int YEAR;

	/**
	 * day of the year represented by the current formatted day
	 */
	private static int DAY;

	/**
	 * formatted year
	 */
	private static String FORMATTED_YEAR;

	/**
	 * formatted day
	 */
	private static String FORMATTED_DAY;

	/**
	 * root directory for the music storage server
	 */
	private final String musicDirectory;

	/**
	 * database for music item meta data
	 */
	private final MusicMetaDatabase musicMetaDatabase;

	/**
	 * update the current date strings
	 */
	private static void updateDateLabels() {
		final Calendar calendar = Calendar.getInstance();
		final int day = calendar.get(Calendar.DAY_OF_YEAR);
		final int year = calendar.get(Calendar.YEAR);

		if ((day != DAY) || (year != YEAR)) {
			YEAR = year;
			DAY = day;

			FORMATTED_YEAR = String.valueOf(year);
			FORMATTED_DAY = FORMATTER.format(calendar.getTime());
		}
	}

	/**
	 * generate the has value for a key
	 * 
	 * @param key
	 *            object to generate the hash for
	 * @return hash for the key passed
	 */
	private static String generateHash(final String key) {
		return String.valueOf(key.hashCode());
	}

	/**
	 * generate the relative directory path for a music item
	 * 
	 * @param hash
	 *            music item identifier hash
	 * @param depth
	 *            number of sub directories
	 * @return relative directory path using hashes
	 */
	private static String getRelativeDirectory(final String hash,
			final int depth) {
		int pathLength = 0;
		for (int i = 0; i < depth; i++) {
			pathLength += i + 1;
		}

		final StringBuilder path = new StringBuilder(pathLength);
		for (int i = 0; i < depth; i++) {
			path.append(hash.substring(0, i + 1));
			if (i != (depth - 1)) {
				path.append(File.separator);
			}
		}

		return path.toString();
	}

	/**
	 * create a new music storage server
	 */
	public MusicStorageServer() {
		// TODO: use config file
		MusicMetaDatabase musicMetaDatabase = null;
		this.musicDirectory = "";

		try {
			musicMetaDatabase = new MusicMetaDatabase("", 0, "");
		} catch (final UnknownHostException e) {
			System.err.println("failed to connect to the mongoDB server at "
					+ "" + ":" + "");
			e.printStackTrace();
		}

		this.musicMetaDatabase = musicMetaDatabase;
	}

	public boolean createMusicItem(final String musicItemIdentifier,
			final InputStream musicItemStream, final String metaData,
			final CreateResponse response) {
		// TODO Auto-generated method stub

		return false;
	}

	public MusicData readMusicItem(final String musicItemIdentifier,
			final ReadResponse response) {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] readMusicItemMetaData(final String[] musicItemIdentifiers,
			final ReadResponse response) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean updateMetaData(final String musicItemIdentifier,
			final String metaData, final UpdateResponse response) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean deleteMusicItem(final String musicItemIdentifier,
			final DeleteResponse response) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * generate the file handle to the original version of a music item
	 * 
	 * @param hash
	 *            music item identifier hash
	 * @return file handle to the original version of the music item passed
	 */
	private File getOriginalFile(final String hash) {
		updateDateLabels();
		return new File(this.musicDirectory + "originals" + File.separator
				+ FORMATTED_YEAR + File.separator + FORMATTED_DAY
				+ File.separator + getRelativeDirectory(hash, 2), hash);
	}

	/**
	 * generate the file handle to the basis version of a music item
	 * 
	 * @param hash
	 *            music item identifier hash
	 * @return file handle to the basis version of the music item passed
	 */
	private File getBasisFile(final String hash) {
		return new File(this.musicDirectory + getRelativeDirectory(hash, 3)
				+ File.separator + "basis", hash + ".ogg");
	}

}