package de.metalcon.server.tomcat;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.neo4j.graphdb.Node;

import de.metalcon.server.exceptions.RequestFailedException;
import de.metalcon.server.tomcat.delete.DeleteType;
import de.metalcon.socialgraph.NeoUtils;
import de.metalcon.socialgraph.operations.ClientResponder;
import de.metalcon.socialgraph.operations.DeleteUser;
import de.metalcon.socialgraph.operations.RemoveFriendship;
import de.metalcon.socialgraph.operations.RemoveStatusUpdate;

/**
 * Tomcat delete operation handler
 * 
 * @author sebschlicht
 * 
 */
public class Delete extends GraphityHttpServlet {

	/**
	 * serialization information
	 */
	private static final long serialVersionUID = 3225499980113476273L;

	@Override
	protected void doPost(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {
		// store response item for the server response creation
		final ClientResponder responder = new TomcatClientResponder(response);

		try {
			// read essential form fields
			final DeleteType removalType = DeleteType.GetDeleteType(Helper
					.getString(request, NSSProtocol.Delete.TYPE));

			Node user = null;
			if (removalType != DeleteType.USER) {
				// TODO: OAuth, stop manual determining of user id
				final String userId = Helper.getString(request,
						NSSProtocol.USER_ID);
				user = NeoUtils.getUserNodeByIdentifier(this.graphDB, userId);
			}

			if (removalType == DeleteType.USER) {
				// read user specific fields
				final String userId = Helper.getString(request,
						NSSProtocol.Delete.USER_ID);
				user = NeoUtils.getUserNodeByIdentifier(this.graphDB, userId);

				// delete user
				final DeleteUser deleteUserCommand = new DeleteUser(responder,
						user);
				this.commandQueue.add(deleteUserCommand);
			} else if (removalType == DeleteType.FOLLOW) {
				// read followship specific fields
				final String followedId = Helper.getString(request,
						NSSProtocol.Delete.FOLLOWED);
				final Node followed = NeoUtils.getUserNodeByIdentifier(
						this.graphDB, followedId);

				// remove followship
				final RemoveFriendship removeFriendshipCommand = new RemoveFriendship(
						responder, user, followed);
				this.commandQueue.add(removeFriendshipCommand);
			} else {
				// read status update specific fields
				final String statusUpdateId = Helper.getString(request,
						NSSProtocol.Delete.STATUS_UPDATE_ID);
				final Node statusUpdate = NeoUtils
						.getStatusUpdateNodeByIdentifier(this.graphDB,
								statusUpdateId);

				// remove status update
				final RemoveStatusUpdate removeStatusUpdate = new RemoveStatusUpdate(
						responder, user, statusUpdate);
				this.commandQueue.add(removeStatusUpdate);
			}

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