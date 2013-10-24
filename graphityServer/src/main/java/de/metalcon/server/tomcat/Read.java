package de.metalcon.server.tomcat;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.metalcon.server.tomcat.NSSP.read.ReadRequest;
import de.metalcon.server.tomcat.NSSP.read.ReadResponse;
import de.metalcon.socialgraph.operations.ReadStatusUpdates;

/**
 * Tomcat read operation handler
 * 
 * @author Sebastian Schlicht
 * 
 */
public class Read extends GraphityHttpServlet {

	/**
	 * serialization information
	 */
	private static final long serialVersionUID = 3210720308557065909L;

	@Override
	protected void doGet(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {
		// store response item for the server response creation
		response.setHeader("Access-Control-Allow-Origin",
				this.config.getHeaderAccessControl());
		final TomcatClientResponder responder = new TomcatClientResponder(
				response);

		final ReadResponse readResponse = new ReadResponse();
		final ReadRequest readRequestObject = ReadRequest.checkRequest(request,
				readResponse);

		if (readRequestObject != null) {
			// read status updates
			final ReadStatusUpdates readStatusUpdatesCommand = new ReadStatusUpdates(
					this, readResponse, readRequestObject);
			this.commandQueue.add(readStatusUpdatesCommand);

			try {
				this.workingQueue.take();
			} catch (final InterruptedException e) {
				System.err
						.println("request status queue failed due to read request");
				e.printStackTrace();
			}
		}

		responder.addLine(readResponse.toString());
		responder.finish();
	}
}