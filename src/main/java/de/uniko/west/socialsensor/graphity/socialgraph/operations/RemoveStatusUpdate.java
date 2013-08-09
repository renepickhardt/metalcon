package de.uniko.west.socialsensor.graphity.socialgraph.operations;

import de.uniko.west.socialsensor.graphity.server.exceptions.RequestFailedException;
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
		boolean success = false;

		try {
			graph.removeStatusUpdate(this.userId, this.statusUpdateId);
			this.responder.addLine("ok");

			success = true;
		} catch (final RequestFailedException e) {
			this.responder.addLine(e.getMessage());
			this.responder.addLine(e.getSalvationDescription());
		}

		this.responder.finish();
		return success;
	}

}