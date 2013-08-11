package de.metalcon.socialgraph.operations;

import org.neo4j.graphdb.Node;

import de.metalcon.server.exceptions.RequestFailedException;
import de.metalcon.server.statusupdates.StatusUpdate;
import de.metalcon.socialgraph.SocialGraph;

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
			final String nodeIdentifier = graph.createStatusUpdate(
					this.timestamp, this.user, this.content);

			// send status update node identifier
			this.responder.addLine(nodeIdentifier);
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