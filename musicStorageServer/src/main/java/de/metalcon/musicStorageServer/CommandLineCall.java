package de.metalcon.musicStorageServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandLineCall {

	private final Process process;

	private final BufferedReader consoleOutput;

	public CommandLineCall(final String programName, final String[] arguments)
			throws IOException {
		final StringBuilder cmd = new StringBuilder(programName);
		for (String argument : arguments) {
			cmd.append(" ");
			cmd.append(argument);
		}

		this.process = Runtime.getRuntime().exec(cmd.toString());
		this.consoleOutput = new BufferedReader(new InputStreamReader(
				this.process.getErrorStream()));
	}

	/**
	 * waits for the command line process called to terminate
	 * 
	 * @return program exit code<br>
	 *         -1 if interrupted while waiting
	 */
	public int waitFor() {
		try {
			return this.process.waitFor();
		} catch (final InterruptedException e) {
			return -1;
		}
	}

	/**
	 * @return output of the console<br>
	 *         <b>null</b> if IO error occurred
	 */
	public String getConsoleOutput() {
		final StringBuilder output = new StringBuilder();
		String line;

		try {
			while ((line = this.consoleOutput.readLine()) != null) {
				output.append(line);
				output.append("\n");
			}
		} catch (final IOException e) {
			return null;
		}

		return output.toString();
	}

}