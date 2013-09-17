package de.metalcon.musicStorageServer.converting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * response class for converter command execution using avconv
 * 
 * @author sebschlicht
 * 
 */
public class AvconvResponse {

	/**
	 * success flag
	 */
	private boolean success;

	/**
	 * error message<br>
	 * set if the conversion failed only
	 */
	private String errorMessage;

	/**
	 * @return true - if the conversion succeeded<br>
	 *         false - otherwise
	 */
	public boolean succeeded() {
		return this.success;
	}

	/**
	 * set the success flag
	 * 
	 * @param success
	 *            success flag
	 */
	public void setSuccess(final boolean success) {
		this.success = success;
	}

	/**
	 * @return error message<br>
	 *         set if the conversion failed only
	 */
	public String getErrorMessage() {
		return this.errorMessage;
	}

	/**
	 * set an error message (sets the success flag to <b>false</b>)
	 * 
	 * @param errorMessage
	 *            error message to be set
	 */
	public void setErrorMessage(final String errorMessage) {
		this.errorMessage = errorMessage;
		this.success = false;
	}

	/**
	 * read an avconv converter execution command from the command line
	 * 
	 * @param inputStream
	 *            input stream of the command line
	 * @return converter command execution response read from the stream passed
	 * @throws IOException
	 *             if IO errors occurred while reading the stream
	 */
	public static AvconvResponse readFromConsoleOutput(
			final InputStream inputStream) throws IOException {
		final BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));
		String line;

		final AvconvResponse response = new AvconvResponse();
		while ((line = reader.readLine()) != null) {
			if (isFileNotSupportedMessage(line)) {
				response.setErrorMessage("file not supported");
				break;
			} else if (isSuccessMessage(line)) {
				response.setSuccess(true);
			} else {
				// line not interpreted
				System.out.println(line);
			}
		}

		return response;
	}

	private static boolean isFileNotSupportedMessage(final String message) {
		return message.endsWith(": Invalid data found when processing input");
	}

	private static boolean isSuccessMessage(final String message) {
		return message.startsWith("video:0kB audio:");
	}

}