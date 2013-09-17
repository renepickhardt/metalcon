package de.metalcon.musicStorageServer.converting;

import java.io.IOException;

public class Avconv {

	private static final String PROGRAM_NAME = "avconv";

	private String sourceFile;

	private String destinationFile;

	private int quality;

	private int bitrate;

	private OutputRateType outputRateType = null;

	private Process process;

	public Avconv(final String sourceFile, final String destinationFile) {
		this.sourceFile = sourceFile;
		this.destinationFile = destinationFile;
	}

	public String getSourceFile() {
		return this.sourceFile;
	}

	public String getDestinationFile() {
		return this.destinationFile;
	}

	public int getQuality() {
		return this.quality;
	}

	public void setQuality(final int quality) {
		this.quality = quality;
		this.outputRateType = OutputRateType.QUALITY;
	}

	public void setBitrate(final int bitrate) {
		this.bitrate = bitrate;
		this.outputRateType = OutputRateType.BITRATE;
	}

	public int getBitrate() {
		return this.bitrate;
	}

	public OutputRateType getOutputRateType() {
		return this.outputRateType;
	}

	public AvconvResponse execute() throws IOException {
		final String cmdCall = generateCommandLineCall(this);
		System.out.println(cmdCall);
		System.out.println();

		this.process = Runtime.getRuntime().exec(cmdCall);
		return AvconvResponse.readFromConsoleOutput(this.process
				.getErrorStream());
	}

	private static String generateCommandLineCall(final Avconv converterCommand) {
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