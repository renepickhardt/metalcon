package de.uniko.west.socialsensor.graphity.server.tomcat;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.neo4j.graphdb.Node;

import de.uniko.west.socialsensor.graphity.server.exceptions.RequestFailedException;
import de.uniko.west.socialsensor.graphity.server.tomcat.delete.DeleteType;
import de.uniko.west.socialsensor.graphity.socialgraph.NeoUtils;
import de.uniko.west.socialsensor.graphity.socialgraph.operations.ClientResponder;
import de.uniko.west.socialsensor.graphity.socialgraph.operations.RemoveFriendship;
import de.uniko.west.socialsensor.graphity.socialgraph.operations.RemoveStatusUpdate;

/**
 * Tomcat delete operation handler
 * 
 * @author sebschlicht
 * 
 */
public class Delete extends GraphityHttpServlet {

	@Override
	protected void doPost(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {
		// store response item for the server response creation
		final ClientResponder responder = new TomcatClientResponder(response);

		try {

			// TODO: OAuth, stop manual determining of user id
			final String userId = Helper.getString(request, FormFields.USER_ID);
			final Node user = NeoUtils.getUserNodeByIdentifier(this.graphDB,
					userId);

			// read essential form fields
			final long timestamp = System.currentTimeMillis();
			final DeleteType removalType = DeleteType.GetDeleteType(Helper
					.getString(request, FormFields.Delete.TYPE));

			if (removalType == DeleteType.FOLLOW) {
				// read followship specific fields
				final String followedId = Helper.getString(request,
						FormFields.Delete.FOLLOWED);
				final Node followed = NeoUtils.getUserNodeByIdentifier(
						this.graphDB, followedId);

				// remove followship
				final RemoveFriendship removeFriendshipCommand = new RemoveFriendship(
						responder, timestamp, user, followed);
				this.commandQueue.add(removeFriendshipCommand);
			} else {
				// read status update specific fields
				final String statusUpdateId = Helper.getString(request,
						FormFields.Delete.STATUS_UPDATE_ID);
				final Node statusUpdate = NeoUtils
						.getStatusUpdateNodeByIdentifier(this.graphDB,
								statusUpdateId);

				// remove status update
				final RemoveStatusUpdate removeStatusUpdate = new RemoveStatusUpdate(
						responder, timestamp, user, statusUpdate);
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