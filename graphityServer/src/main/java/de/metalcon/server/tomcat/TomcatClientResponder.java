package de.metalcon.server.tomcat;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import de.metalcon.socialgraph.operations.ClientResponder;

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

	@Override
	public void addLine(final String line) {
		this.writer.println(line);
	}

	@Override
	public void error(final int errorCode, final String message) {
		try {
			this.response.sendError(errorCode, message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void finish() {
		// this.writer.close();
	}

}