package de.metalcon.socialgraph.operations;

import org.neo4j.graphdb.Node;

import de.metalcon.server.exceptions.RequestFailedException;
import de.metalcon.socialgraph.SocialGraph;

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
	 * @param poster
	 *            posting user
	 * @param statusUpdate
	 *            status update
	 */
	public RemoveStatusUpdate(final ClientResponder responder,
			final Node poster, final Node statusUpdate) {
		super(responder, poster);
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