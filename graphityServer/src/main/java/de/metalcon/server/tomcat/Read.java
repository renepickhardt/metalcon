package de.metalcon.server.tomcat;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.metalcon.server.tomcat.NSSP.read.ReadRequest;
import de.metalcon.server.tomcat.NSSP.read.ReadResponse;
import de.metalcon.socialgraph.operations.ClientResponder;
import de.metalcon.socialgraph.operations.ReadStatusUpdates;

/**
 * Tomcat read operation handler
 * 
 * @author Sebastian Schlicht
 * 
 */
public class Read extends GraphityHttpServlet {

	@Override
	protected void doGet(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {
		// store response item for the server response creation
		final ClientResponder responder = new TomcatClientResponder(response);

		final ReadResponse readResponse = new ReadResponse();
		final ReadRequest readRequestObject = ReadRequest.checkRequest(request,
				readResponse);

		// DEBUG
		responder.addLine("stacked commands: " + this.commandQueue.size());

		if (readRequestObject != null) {
			// read status updates
			final ReadStatusUpdates readStatusUpdatesCommand = new ReadStatusUpdates(
					this, responder, readRequestObject.getUser(),
					readRequestObject.getPoster(),
					readRequestObject.getNumItems(),
					readRequestObject.getOwnUpdates());
			this.commandQueue.add(readStatusUpdatesCommand);

			try {
				this.workingQueue.take();
				responder.finish();
			} catch (final InterruptedException e) {
				System.err.println("request status queue failed");
				e.printStackTrace();
			}
		}
	}
}