package de.metalcon.musicStorageServer.converting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * command line wrapper for converting via avconv
 * 
 * @author sebschlicht
 * 
 */
public class AvconvCommand {

	/**
	 * program name used in command line
	 */
	private static final String PROGRAM_NAME = "avconv";

	/**
	 * meta data keys according to vorbis comment
	 */
	private static final String[] VORBIS_COMMENT_KEYS = { "title", "version",
			"album", "tracknumber", "artist", "performer", "copyright",
			"license", "organisation", "description", "genre", "date",
			"location", "contact", "isrc",

			/**
			 * NOT part of vorbis comment but of tag readers
			 */
			"comment" };

	/**
	 * path to the source file
	 */
	private final String sourceFile;

	/**
	 * path to the destination file
	 */
	private final String destinationFile;

	/**
	 * meta data to be written to the destination file
	 */
	private final Map<String, String> metaData;

	/**
	 * output quality relative to the source file
	 */
	private int quality;

	/**
	 * absolute output bit rate
	 */
	private int bitrate;

	/**
	 * output rate type used
	 */
	private OutputRateType outputRateType = null;

	/**
	 * create a new converter command
	 * 
	 * @param sourceFile
	 *            path to the source file
	 * @param destinationFile
	 *            path to the destination file
	 * @param metaData
	 *            meta data to be written to the destination file
	 */
	public AvconvCommand(final String sourceFile, final String destinationFile,
			final Map<String, String> metaData) {
		this.sourceFile = sourceFile;
		this.destinationFile = destinationFile;
		this.metaData = metaData;
	}

	/**
	 * @return path to the source file
	 */
	public String getSourceFile() {
		return this.sourceFile;
	}

	/**
	 * @return path to the destination file
	 */
	public String getDestinationFile() {
		return this.destinationFile;
	}

	/**
	 * @return output quality relative to the source file
	 */
	public int getQuality() {
		return this.quality;
	}

	/**
	 * use an output quality
	 * 
	 * @param quality
	 *            output quality relative to the source file
	 */
	public void setQuality(final int quality) {
		this.quality = quality;
		this.outputRateType = OutputRateType.QUALITY;
	}

	/**
	 * @return absolute output bit rate
	 */
	public int getBitrate() {
		return this.bitrate;
	}

	/**
	 * use an absolute bit rate
	 * 
	 * @param bitrate
	 *            absolute output bit rate
	 */
	public void setBitrate(final int bitrate) {
		this.bitrate = bitrate;
		this.outputRateType = OutputRateType.BITRATE;
	}

	/**
	 * @return output rate type used
	 */
	public OutputRateType getOutputRateType() {
		return this.outputRateType;
	}

	/**
	 * @return meta data to be written to the destination file
	 */
	public Map<String, String> getMetaData() {
		return this.metaData;
	}

	/**
	 * execute this converter command
	 * 
	 * @return command execution response
	 * @throws IOException
	 *             if IO errors occurred within console communication
	 */
	public AvconvResponse execute() throws IOException {
		final ProcessBuilder processBuilder = new ProcessBuilder(
				generateCommandLineCall(this));
		final Process process = processBuilder.start();
		return AvconvResponse.readFromConsoleOutput(process.getErrorStream());
	}

	/**
	 * extract the vorbis comment meta data from a map
	 * 
	 * @param metaData
	 *            map containing meta data of any format
	 * @return map containing meta data according to vorbis comment only
	 */
	private static Map<String, String> extractVorbisComment(
			final Map<String, String> metaData) {
		final Map<String, String> vorbisComment = new HashMap<String, String>();

		for (String key : VORBIS_COMMENT_KEYS) {
			final String value = metaData.get(key);
			if (value != null) {
				vorbisComment.put(key, value);
			}
		}

		return vorbisComment;
	}

	/**
	 * generate a command line call
	 * 
	 * @param converterCommand
	 *            converter command to be represented
	 * @return command line call representing the converter command passed
	 */
	private static List<String> generateCommandLineCall(
			final AvconvCommand converterCommand) {
		// extract meta data
		final Map<String, String> metaData = extractVorbisComment(converterCommand
				.getMetaData());

		final List<String> cmdCall = new ArrayList<String>();

		// program name
		cmdCall.add(PROGRAM_NAME);

		// source file
		cmdCall.add("-i");
		cmdCall.add(converterCommand.getSourceFile());

		// meta data
		for (String key : metaData.keySet()) {
			final String value = metaData.get(key);
			cmdCall.add("-metadata");
			cmdCall.add(key + "=" + value);
		}

		// output rate
		final OutputRateType outputRateType = converterCommand
				.getOutputRateType();
		if (outputRateType == null) {
			throw new IllegalArgumentException("no output rate type set!");
		} else if (outputRateType == OutputRateType.QUALITY) {
			cmdCall.add("-aq");
			cmdCall.add(String.valueOf(converterCommand.getQuality()));
		} else {
			cmdCall.add("-ab");
			cmdCall.add(String.valueOf(converterCommand.getBitrate()));
		}

		// audio encoder
		cmdCall.add("-acodec");
		cmdCall.add("libvorbis");

		// no video encoding
		cmdCall.add("-vn");

		// destination file
		cmdCall.add(converterCommand.getDestinationFile());

		return cmdCall;
	}
}