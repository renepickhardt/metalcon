package de.uniko.west.socialsensor.graphity.server.tomcat;

import java.io.IOException;
import java.util.Queue;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.uniko.west.socialsensor.graphity.server.exceptions.InvalidUserIdentifierException;
import de.uniko.west.socialsensor.graphity.server.exceptions.RequestFailedException;
import de.uniko.west.socialsensor.graphity.server.exceptions.read.InvalidItemNumberException;
import de.uniko.west.socialsensor.graphity.server.exceptions.read.InvalidRetrievalFlag;
import de.uniko.west.socialsensor.graphity.socialgraph.NeoUtils;
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
			long userId;
			try {
				userId = Helper.getLong(request, FormFields.USER_ID);
				if (!NeoUtils.isValidUserIdentifier(userId)) {
					throw new NumberFormatException();
				}
			} catch (final NumberFormatException e) {
				throw new InvalidUserIdentifierException(
						"user identifier has to be greater than zero.");
			}

			int numItems;
			try {
				numItems = Helper.getInt(request, FormFields.Read.NUM_ITEMS);
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
						FormFields.Read.OWN_UPDATES);
			} catch (final NumberFormatException e) {
				throw new InvalidRetrievalFlag("a number is excpected.");
			}

			// read status updates
			final ReadStatusUpdates readStatusUpdatesCommand = new ReadStatusUpdates(
					responder, System.currentTimeMillis(), userId, userId,
					numItems, ownUpdates);
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