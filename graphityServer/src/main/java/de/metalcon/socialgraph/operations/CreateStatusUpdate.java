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
	 * time stamp of the operation
	 */
	private final long timestamp;

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
		super(responder, poster);
		this.timestamp = timestamp;
		this.content = content;
	}

	@Override
	protected boolean execute(final SocialGraph graph) {
		boolean success = false;

		try {
			graph.createStatusUpdate(this.timestamp, this.user, this.content);
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