package de.uniko.west.socialsensor.graphity.socialgraph.operations;

import de.uniko.west.socialsensor.graphity.socialgraph.SocialGraph;

/**
 * remove command: status update
 * 
 * @author Sebastian Schlicht
 * 
 */
public class RemoveStatusUpdate extends SocialGraphOperation {

	/**
	 * status update identifier
	 */
	private final long statusUpdateId;

	/**
	 * create a new remove status update command
	 * 
	 * @param responder
	 *            client responder
	 * @param timestamp
	 *            time stamp of the status update removal
	 * @param posterId
	 *            posting user's identifier
	 * @param statusUpdateId
	 *            status update identifier
	 */
	public RemoveStatusUpdate(final ClientResponder responder,
			final long timestamp, final long posterId, final long statusUpdateId) {
		super(responder, timestamp, posterId);
		this.statusUpdateId = statusUpdateId;
	}

	@Override
	protected boolean execute(final SocialGraph graph) {
		final boolean success = graph.removeStatusUpdate(this.userId,
				this.statusUpdateId);

		if (success) {
			// TODO: send something? HTTP 200:OK sent anyway
			this.responder.finish();

			return true;
		} else {
			// TODO: send error code as the status update did not exist or is
			// not owned
			this.responder.error(404, "status update missing or not owned!");

			return false;
		}
	}

}