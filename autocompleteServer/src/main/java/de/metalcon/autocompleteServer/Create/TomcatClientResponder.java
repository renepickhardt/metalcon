package de.metalcon.autocompleteServer.Create;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

/**
 * client responder using Tomcat's response instance
 * 
 * @author Sebastian Schlicht
 * 
 */
public class TomcatClientResponder implements ClientResponder {

	/**
	 * Tomcat response instance to reach the client
	 */
	private final HttpServletResponse response;

	/**
	 * output stream for data sent to the client
	 */
	private final PrintWriter writer;

	/**
	 * create a new Tomcat client responder
	 * 
	 * @param response
	 *            Tomcat response instance to reach the client
	 * @throws IOException
	 */
	public TomcatClientResponder(final HttpServletResponse response)
			throws IOException {
		this.response = response;
		this.writer = response.getWriter();
	}

	public void addLine(final String line) {
		this.writer.println(line);
	}

	public void error(final String message) {
		// TODO: Error responder, which writes error messages to the respond
		// JSON.
	}

	public void finish() {
		// this.writer.close();
	}

}