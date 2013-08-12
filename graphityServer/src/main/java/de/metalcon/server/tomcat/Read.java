package de.metalcon.server.tomcat;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.neo4j.graphdb.Node;

import de.metalcon.server.exceptions.RequestFailedException;
import de.metalcon.server.exceptions.read.InvalidItemNumberException;
import de.metalcon.server.exceptions.read.InvalidRetrievalFlag;
import de.metalcon.socialgraph.NeoUtils;
import de.metalcon.socialgraph.operations.ClientResponder;
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
		final ClientResponder responder = new TomcatClientResponder(response);

		try {
			// TODO: OAuth, stop manual determining of user id
			final String userId = Helper.getString(request, NSSProtocol.USER_ID);
			final Node user = NeoUtils.getUserNodeByIdentifier(this.graphDB,
					userId);

			final String posterId = Helper.getString(request,
					NSSProtocol.Read.POSTER_ID);
			final Node poster = NeoUtils.getUserNodeByIdentifier(this.graphDB,
					posterId);

			int numItems;
			try {
				numItems = Helper.getInt(request, NSSProtocol.Read.NUM_ITEMS);
				if (numItems <= 0) {
					throw new InvalidItemNumberException(
							"the number of items to retrieve must be greater than zero.");
				}
			} catch (final NumberFormatException e) {
				throw new InvalidItemNumberException("a number is expected.");
			}

			boolean ownUpdates;
			try {
				ownUpdates = Helper.getBool(request,
						NSSProtocol.Read.OWN_UPDATES);
			} catch (final NumberFormatException e) {
				throw new InvalidRetrievalFlag("a number is excpected.");
			}

			// read status updates
			final ReadStatusUpdates readStatusUpdatesCommand = new ReadStatusUpdates(
					responder, user, poster, numItems, ownUpdates);
			this.commandQueue.add(readStatusUpdatesCommand);
		} catch (final IllegalArgumentException e) {
			// a required form field is missing
			responder.error(500, e.getMessage());
			e.printStackTrace();
		} catch (final RequestFailedException e) {
			// the request contains errors
			responder.addLine(e.getMessage());
			responder.addLine(e.getSalvationDescription());
			responder.finish();
			e.printStackTrace();
		}
	}
}