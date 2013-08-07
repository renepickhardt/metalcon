package de.uniko.west.socialsensor.graphity.server.tomcat;

import java.io.IOException;
import java.util.Queue;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.uniko.west.socialsensor.graphity.socialgraph.operations.ClientResponder;
import de.uniko.west.socialsensor.graphity.socialgraph.operations.ReadStatusUpdates;
import de.uniko.west.socialsensor.graphity.socialgraph.operations.SocialGraphOperation;

/**
 * Tomcat read operation handler
 * 
 * @author Sebastian Schlicht
 * 
 */
public class Read extends HttpServlet {

	/**
	 * command queue to stack commands created
	 */
	private Queue<SocialGraphOperation> commandQueue;

	@Override
	public void init(final ServletConfig config) throws ServletException {
		super.init(config);
		final ServletContext context = this.getServletContext();
		this.commandQueue = ((de.uniko.west.socialsensor.graphity.server.Server) context
				.getAttribute("server")).getCommandQueue();
	}

	@Override
	protected void doGet(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {
		// store response item for the server response creation
		final ClientResponder responder = new TomcatClientResponder(response);

		try {
			// TODO: OAuth, stop manual determining of user id
			final long userId = Helper.getLong(request, FormFields.USER_ID);

			final int numItems = Helper.getInt(request,
					FormFields.Read.NUM_ITEMS);
			if (numItems <= 0) {
				throw new IllegalArgumentException("parameter \""
						+ FormFields.Read.NUM_ITEMS
						+ "\" must be greater than zero!");
			}

			final boolean ownUpdates = Helper.getBool(request,
					FormFields.Read.OWN_UPDATES);

			// read status updates
			final ReadStatusUpdates readStatusUpdatesCommand = new ReadStatusUpdates(
					responder, System.currentTimeMillis(), userId, userId,
					numItems, ownUpdates);
			this.commandQueue.add(readStatusUpdatesCommand);
		} catch (final IllegalArgumentException e) {
			responder.error(500, e.getMessage());
		}
	}
}