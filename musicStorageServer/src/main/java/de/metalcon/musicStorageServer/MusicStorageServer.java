package de.metalcon.musicStorageServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.io.IOUtils;
import org.xiph.libogg.ogg_packet;
import org.xiph.libogg.ogg_page;
import org.xiph.libogg.ogg_stream_state;
import org.xiph.libvorbis.vorbis_block;
import org.xiph.libvorbis.vorbis_comment;
import org.xiph.libvorbis.vorbis_dsp_state;
import org.xiph.libvorbis.vorbis_info;
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

	private static final vorbis_dsp_state DSP_STATE = new vorbis_dsp_state();

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
	 * temporary directory for resampling
	 */
	private final String temporaryDirectory;

	/**
	 * minimum sample rate (in kbit/s)
	 */
	private final int sampleRateMin;

	/**
	 * sample rate targeted (in kbit/s)
	 */
	private final int sampleRateAverage;

	/**
	 * maximum sample rate (in kbit/s)
	 */
	private final int sampleRateMax;

	/**
	 * database for music item meta data
	 */
	private final MusicMetaDatabase musicMetaDatabase;

	/**
	 * server running flag
	 */
	private boolean running;

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
	 * 
	 * @param configPath
	 *            path to the configuration file
	 */
	public MusicStorageServer(final String configPath) {
		final MSSConfig config = new MSSConfig(configPath);
		MusicMetaDatabase musicMetaDatabase = null;
		this.musicDirectory = config.getMusicDirectory();
		this.temporaryDirectory = config.getTemporaryDirectory();
		this.sampleRateAverage = config.getSampleRateAverage();
		this.sampleRateMin = config.getSampleRateMin();
		this.sampleRateMax = config.getSampleRateMax();

		try {
			musicMetaDatabase = new MusicMetaDatabase(config.getDatabaseHost(),
					config.getDatabasePort(), config.getDatabaseName());
			this.running = true;
		} catch (final UnknownHostException e) {
			System.err
					.println("failed to connect to the mongoDB server at "
							+ config.getDatabaseHost() + ":"
							+ config.getDatabaseName());
			e.printStackTrace();
		}

		this.musicMetaDatabase = musicMetaDatabase;
	}

	private static void storeToFile(final InputStream inputStream,
			final File destinationFile) throws IOException {
		final File parentalDirectory = destinationFile.getParentFile();
		if (parentalDirectory != null) {
			parentalDirectory.mkdirs();
		}

		final OutputStream fileOutputStream = new FileOutputStream(
				destinationFile);
		IOUtils.copy(inputStream, fileOutputStream);
	}

	private boolean storeEncodedMusicItem(
			final InputStream musicItemInputStream, final File destinationFile)
			throws IOException {
		final File parentalDirectory = destinationFile.getParentFile();
		if (parentalDirectory != null) {
			parentalDirectory.mkdirs();
		}

		final vorbis_info vorbisInfo = new vorbis_info();
		final vorbisenc vorbisEncoder = new vorbisenc();
		final vorbis_dsp_state vorbisState = new vorbis_dsp_state();

		if (!vorbisEncoder.vorbis_encode_init(vorbisInfo, 2, 44100,
				(this.sampleRateMin != -1) ? (this.sampleRateMin * 1000) : -1,
				this.sampleRateAverage * 1000,
				(this.sampleRateMax != -1) ? (this.sampleRateMax * 1000) : -1)) {
			System.err.println("Failed to initialize vorbis encoder!");
			return false;
		}

		if (!vorbisState.vorbis_analysis_init(vorbisInfo)) {
			System.err.println("Failed to initialize vorbis DSP state!");
			return false;
		}

		final vorbis_comment vorbisComment = new vorbis_comment();
		final vorbis_block vorbisBlock = new vorbis_block(DSP_STATE);
		final ogg_stream_state oggStreamState = new ogg_stream_state();

		// create header
		final ogg_packet header = new ogg_packet();
		final ogg_packet header_comm = new ogg_packet();
		final ogg_packet header_code = new ogg_packet();
		vorbisState.vorbis_analysis_headerout(vorbisComment, header,
				header_comm, header_code);

		oggStreamState.ogg_stream_packetin(header);
		oggStreamState.ogg_stream_packetin(header_comm);
		oggStreamState.ogg_stream_packetin(header_code);

		final ogg_page oggPage = new ogg_page();
		final ogg_packet decodingPacket = new ogg_packet();

		final OutputStream fileOutputStream = new FileOutputStream(
				destinationFile);

		// write header
		while (oggStreamState.ogg_stream_flush(oggPage)) {
			fileOutputStream.write(oggPage.header, 0, oggPage.header_len);
			fileOutputStream.write(oggPage.body, 0, oggPage.body_len);
		}

		// TODO: encoding

		return true;
	}

	public boolean createMusicItem(final String musicItemIdentifier,
			final InputStream musicItemStream, final String metaData,
			final CreateResponse response) {
		if (!this.musicMetaDatabase.hasEntryWithIdentifier(musicItemIdentifier)) {
			final String hash = generateHash(musicItemIdentifier);
			final File tmpFile = this.getTemporaryFile(hash);

			try {
				if (!tmpFile.exists()) {
					storeToFile(musicItemStream, tmpFile);

					try {

						// save the original image to the disk
						final FileInputStream tmpInputStream = new FileInputStream(
								tmpFile);
						final File originalFile = this.getOriginalFile(hash);

						if (!originalFile.exists()) {
							storeToFile(tmpInputStream, originalFile);
						}
					} finally {
						// delete temporary (music) file
						tmpFile.delete();
					}
				} else {
					// TODO error: hash collision
				}
			} catch (final IOException e) {
				// TODO internal server error
			}
		} else {
			// TODO error: music item identifier in use
		}

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

	/**
	 * generate the file handle to the temporary version of a music item
	 * 
	 * @param hash
	 *            music item identifier hash
	 * @return file handle to the temporary version of the music item passed
	 */
	private File getTemporaryFile(final String hash) {
		return new File(this.temporaryDirectory, hash);
	}

	/**
	 * @return true - if the server is running<br>
	 *         false - otherwise
	 */
	public boolean isRunning() {
		return this.running;
	}

}