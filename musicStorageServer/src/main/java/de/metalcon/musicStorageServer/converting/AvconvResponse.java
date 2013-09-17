package de.metalcon.musicStorageServer.converting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * response class for avconv converter calls
 * 
 * @author sebschlicht
 * 
 */
public class AvconvResponse {

	private boolean success;

	private String errorMessage;

	public boolean succeeded() {
		return this.success;
	}

	public void setSuccess(final boolean success) {
		this.success = success;
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}

	public void setErrorMessage(final String errorMessage) {
		this.errorMessage = errorMessage;
		this.success = false;
	}

	public static AvconvResponse readFromConsoleOutput(
			final InputStream inputStream) throws IOException {
		final BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));
		String line;

		final AvconvResponse response = new AvconvResponse();
		while ((line = reader.readLine()) != null) {
			if (line.endsWith(": could not find codec parameters")) {
				response.setErrorMessage("audio file malformed");
				break;
			} else if (line
					.equals("Output file #0 does not contain any stream")
					|| line.endsWith(": Invalid data found when processing input")) {
				response.setErrorMessage("file not supported");
				break;
			} else if (line.startsWith("video:0kB audio:")) {
				response.setSuccess(true);
			} else {
				System.out.println(line);
			}
		}

		return response;
	}

}