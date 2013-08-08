package de.uniko.west.socialsensor.graphity.server.tomcat;

import java.io.IOException;
import java.util.Queue;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.uniko.west.socialsensor.graphity.server.Server;
import de.uniko.west.socialsensor.graphity.server.tomcat.delete.DeleteType;
import de.uniko.west.socialsensor.graphity.socialgraph.operations.ClientResponder;
import de.uniko.west.socialsensor.graphity.socialgraph.operations.RemoveFriendship;
import de.uniko.west.socialsensor.graphity.socialgraph.operations.RemoveStatusUpdate;
import de.uniko.west.socialsensor.graphity.socialgraph.operations.SocialGraphOperation;

/**
 * Tomcat delete operation handler
 * 
 * @author sebschlicht
 * 
 */
public class Delete extends HttpServlet {

	/**
	 * command queue to stack commands created
	 */
	private Queue<SocialGraphOperation> commandQueue;

	@Override
	public void init(final ServletConfig config) throws ServletException {
		super.init(config);
		final ServletContext context = this.getServletContext();
		final Server server = (Server) context.getAttribute("server");
		this.commandQueue = server.getCommandQueue();
	}

	@Override
	protected void doPost(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {
		// store response item for the server response creation
		final ClientResponder responder = new TomcatClientResponder(response);

		try {

			// TODO: OAuth, stop manual determining of user id
			final long userId = Helper.getLong(request, FormFields.USER_ID);

			// read essential form fields
			final long timestamp = System.currentTimeMillis();
			final DeleteType removalType = DeleteType.GetDeleteType(Helper
					.getString(request, FormFields.Delete.TYPE));

			if (removalType == DeleteType.FOLLOWSHIP) {
				// read followship specific fields
				final long followedId = Helper.getLong(request,
						FormFields.Delete.FOLLOWED);

				// remove followship
				final RemoveFriendship removeFriendshipCommand = new RemoveFriendship(
						responder, timestamp, userId, followedId);
				this.commandQueue.add(removeFriendshipCommand);
			} else {
				// read status update specific fields
				final long statusUpdateId = Helper.getLong(request,
						FormFields.Delete.STATUS_UPDATE_ID);

				// remove status update
				final RemoveStatusUpdate removeStatusUpdate = new RemoveStatusUpdate(
						responder, timestamp, userId, statusUpdateId);
				this.commandQueue.add(removeStatusUpdate);
			}

		} catch (final IllegalArgumentException e) {
			responder.error(500, e.getMessage());
		}
	}
}