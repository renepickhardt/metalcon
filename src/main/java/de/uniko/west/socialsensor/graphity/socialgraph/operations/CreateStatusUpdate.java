package de.uniko.west.socialsensor.graphity.socialgraph.operations;

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
	 * @param posterId
	 *            posting user's identifier
	 * @param content
	 *            status update content object
	 */
	public CreateStatusUpdate(final ClientResponder responder,
			final long timestamp, final long posterId,
			final StatusUpdate content) {
		super(responder, timestamp, posterId);
		this.content = content;
	}

	@Override
	protected boolean execute(final SocialGraph graph) {
		final long nodeIdentifier = graph.createStatusUpdate(this.timestamp,
				this.userId, this.content);

		if (nodeIdentifier != 0) {
			// send status update node identifier
			this.responder.addLine(String.valueOf(nodeIdentifier));
			this.responder.finish();

			return true;
		} else {
			// TODO: create own exceptions to catch
			// send error code
			this.responder.error(500,
					"thrown exceptions are not specified yet!");

			return false;
		}
	}

}