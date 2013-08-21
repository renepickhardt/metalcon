package de.metalcon.server.tomcat;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

/**
 * client responder using Tomcat's response instance
 * 
 * @author Sebastian Schlicht
 * 
 */
public class TomcatClientResponder {

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
		response.setContentType("application/json");
		this.writer = response.getWriter();
	}

	/**
	 * add a line to the response
	 * 
	 * @param line
	 *            line to be added
	 */
	public void addLine(final String line) {
		this.writer.println(line);
	}

	/**
	 * abort the response with an error
	 * 
	 * @param errorCode
	 *            HTTP error code
	 * @param message
	 *            error message
	 */
	public void error(final int errorCode, final String message) {
		try {
			this.response.sendError(errorCode, message);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * finish the response by closing the stream
	 */
	public void finish() {
		this.writer.flush();
		this.writer.close();
	}

}