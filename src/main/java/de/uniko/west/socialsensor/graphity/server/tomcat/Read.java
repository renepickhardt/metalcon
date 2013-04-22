package de.uniko.west.socialsensor.graphity.server.tomcat;

import java.util.Queue;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
	 * generated serial version UID
	 */
	private static final long serialVersionUID = -2662159208880344207L;

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
			final HttpServletResponse response) {
		int numItems;
		try {
			numItems = Helper.getInt(request, "numItems");
			if (numItems <= 0) {
				throw new ClassCastException();
			}
		} catch (final IllegalArgumentException missing) {
			// send error - field missing
			Helper.sendErrorMessage(
					response,
					400,
					"the field \"numItems\" is missing!<br>please specify how many items should be retrieved.");
			return;
		} catch (final ClassCastException invalid) {
			// send error - field type invalid
			Helper.sendErrorMessage(
					response,
					400,
					"the field \"numItems\" is corrupted!<br>please provide a number greater than zero.");
			return;
		}

		long userId;
		try {
			userId = Helper.getLong(request, "userId");
		} catch (final IllegalArgumentException missing) {
			// send error - field missing
			Helper.sendErrorMessage(
					response,
					400,
					"the field \"userId\" is missing!<br>please specify the user you want to retrieve status updates from.");
			return;
		} catch (final ClassCastException invalid) {
			// send error - field type invalid
			Helper.sendErrorMessage(
					response,
					400,
					"the field \"userId\" is corrupted!<br>please provide a number greater than zero.");
			return;
		}

		boolean ownUpdates;
		try {
			final int tmp = Helper.getInt(request, "ownUpdates");
			if ((tmp == 0) || (tmp == 1)) {
				ownUpdates = (tmp == 1);
			} else {
				throw new ClassCastException();
			}
		} catch (final IllegalArgumentException missing) {
			// send error - field missing
			Helper.sendErrorMessage(
					response,
					400,
					"the field \"ownUpdates\" is missing!<br>please specify whether you want to retrieve an ego network or a single stream.");
			return;
		} catch (final ClassCastException invalid) {
			// send error - field type invalid
			Helper.sendErrorMessage(
					response,
					400,
					"the field \"ownUpdates\" is corrupted!<br>please type \"0\" to retrieve an ego network stream or \"1\" for a single stream only.");
			return;
		}

		// TODO: store response item for the server response creation
		final ReadStatusUpdates readStatusUpdatesCommand = new ReadStatusUpdates(
				System.currentTimeMillis(), userId, userId, numItems,
				ownUpdates);
		this.commandQueue.add(readStatusUpdatesCommand);
	}
}