package de.metalcon.musicStorageServer.converting;

import java.io.IOException;

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
	 * path to the source file
	 */
	private final String sourceFile;

	/**
	 * path to the destination file
	 */
	private final String destinationFile;

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
	 */
	public AvconvCommand(final String sourceFile, final String destinationFile) {
		this.sourceFile = sourceFile;
		this.destinationFile = destinationFile;
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
	 * execute this converter command
	 * 
	 * @return command execution response
	 * @throws IOException
	 *             if IO errors occurred within console communication
	 */
	public AvconvResponse execute() throws IOException {
		final String cmdCall = generateCommandLineCall(this);
		final Process process = Runtime.getRuntime().exec(cmdCall);
		return AvconvResponse.readFromConsoleOutput(process.getErrorStream());
	}

	/**
	 * generate a command line call
	 * 
	 * @param converterCommand
	 *            converter command to be represented
	 * @return command line call representing the converter command passed
	 */
	private static String generateCommandLineCall(
			final AvconvCommand converterCommand) {
		// program name
		final StringBuilder line = new StringBuilder(PROGRAM_NAME);

		// source file
		line.append(" -i ");
		line.append(converterCommand.getSourceFile());

		// output rate
		final OutputRateType outputRateType = converterCommand
				.getOutputRateType();
		if (outputRateType == null) {
			throw new IllegalArgumentException("no output rate type set!");
		} else if (outputRateType == OutputRateType.QUALITY) {
			line.append(" -aq ");
			line.append(converterCommand.getQuality());
		} else {
			line.append(" -ab ");
			line.append(converterCommand.getBitrate());
		}

		// audio encoder
		line.append(" -acodec libvorbis");

		// no video encoding
		line.append(" -vn");

		// destination file
		line.append(" ");
		line.append(converterCommand.getDestinationFile());

		return line.toString();
	}

}