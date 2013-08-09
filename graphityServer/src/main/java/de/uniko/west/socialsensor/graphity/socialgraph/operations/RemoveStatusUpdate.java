package de.uniko.west.socialsensor.graphity.socialgraph.operations;

import org.neo4j.graphdb.Node;

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
	 * status update
	 */
	private final Node statusUpdate;

	/**
	 * create a new remove status update command
	 * 
	 * @param responder
	 *            client responder
	 * @param timestamp
	 *            time stamp of the status update removal
	 * @param poster
	 *            posting user
	 * @param statusUpdate
	 *            status update
	 */
	public RemoveStatusUpdate(final ClientResponder responder,
			final long timestamp, final Node poster, final Node statusUpdate) {
		super(responder, timestamp, poster);
		this.statusUpdate = statusUpdate;
	}

	@Override
	protected boolean execute(final SocialGraph graph) {
		boolean success = false;

		try {
			graph.removeStatusUpdate(this.user, this.statusUpdate);
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