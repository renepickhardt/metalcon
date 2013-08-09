package de.uniko.west.socialsensor.graphity.socialgraph.operations;

import org.neo4j.graphdb.Node;

import de.uniko.west.socialsensor.graphity.server.exceptions.RequestFailedException;
import de.uniko.west.socialsensor.graphity.server.statusupdates.StatusUpdate;
import de.uniko.west.socialsensor.graphity.socialgraph.SocialGraph;

/**
 * create command: status update
 * 
 * @author Sebastian Schlicht
 * 
 */
public class CreateStatusUpdate extends SocialGraphOperation {

	/**
	 * status update content object
	 */
	private final StatusUpdate content;

	/**
	 * create a new create status update command
	 * 
	 * @param responder
	 *            client responder
	 * @param timestamp
	 *            time stamp of the status update
	 * @param poster
	 *            posting user
	 * @param content
	 *            status update content object
	 */
	public CreateStatusUpdate(final ClientResponder responder,
			final long timestamp, final Node poster, final StatusUpdate content) {
		super(responder, timestamp, poster);
		this.content = content;
	}

	@Override
	protected boolean execute(final SocialGraph graph) {
		boolean success = false;

		try {
			final long nodeIdentifier = graph.createStatusUpdate(
					this.timestamp, this.user, this.content);

			// send status update node identifier
			this.responder.addLine(String.valueOf(nodeIdentifier));
			this.responder.finish();

			success = true;
		} catch (final RequestFailedException e) {
			this.responder.addLine(e.getMessage());
			this.responder.addLine(e.getSalvationDescription());
		}

		this.responder.finish();
		return success;
	}

}