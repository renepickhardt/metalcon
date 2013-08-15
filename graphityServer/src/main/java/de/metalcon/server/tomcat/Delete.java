package de.metalcon.server.tomcat;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.metalcon.server.tomcat.NSSP.delete.DeleteFollowRequest;
import de.metalcon.server.tomcat.NSSP.delete.DeleteFollowResponse;
import de.metalcon.server.tomcat.NSSP.delete.DeleteRequest;
import de.metalcon.server.tomcat.NSSP.delete.DeleteResponse;
import de.metalcon.server.tomcat.NSSP.delete.DeleteUserRequest;
import de.metalcon.server.tomcat.NSSP.delete.DeleteUserResponse;
import de.metalcon.socialgraph.operations.ClientResponder;
import de.metalcon.socialgraph.operations.DeleteUser;
import de.metalcon.socialgraph.operations.RemoveFriendship;

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

		DeleteResponse deleteResponse = new DeleteResponse();
		final DeleteRequest deleteRequest = DeleteRequest.checkRequest(request,
				deleteResponse);

		boolean commandStacked = false;
		if (deleteRequest != null) {
			switch (deleteRequest.getType()) {

			// TODO: user delete requests/responses to instantiate commands

			// delete a user
			case USER:
				final DeleteUserResponse deleteUserResponse = new DeleteUserResponse();
				final DeleteUserRequest deleteUserRequest = DeleteUserRequest
						.checkRequest(request, deleteRequest,
								deleteUserResponse);
				deleteResponse = deleteUserResponse;

				if (deleteUserRequest != null) {
					// delete user
					final DeleteUser deleteUserCommand = new DeleteUser(this,
							responder, deleteUserRequest.getUser());
					this.commandQueue.add(deleteUserCommand);

					commandStacked = true;
				}
				break;

			// delete a follow edge
			case FOLLOW:
				final DeleteFollowResponse deleteFollowResponse = new DeleteFollowResponse();
				final DeleteFollowRequest deleteFollowRequest = DeleteFollowRequest
						.checkRequest(request, deleteRequest,
								deleteFollowResponse);
				deleteResponse = deleteFollowResponse;

				if (deleteFollowRequest != null) {
					// delete follow edge
					final RemoveFriendship deleteFollowCommand = new RemoveFriendship(
							this, responder, deleteFollowRequest.getUser(),
							deleteFollowRequest.getFollowed());
					this.commandQueue.add(deleteFollowCommand);

					commandStacked = true;
				}
				break;

			// delete status update
			default:
				// TODO
				break;

			}

			if (commandStacked) {
				try {
					this.workingQueue.take();
				} catch (final InterruptedException e) {
					System.err
							.println("request status queue failed due to delete request");
					e.printStackTrace();
				}
			}
		}

		responder.addLine(deleteResponse.toString());
		responder.finish();
	}
}