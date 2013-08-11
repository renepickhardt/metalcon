package de.metalcon.socialgraph.operations;

import org.neo4j.graphdb.Node;

import de.metalcon.server.exceptions.RequestFailedException;
import de.metalcon.socialgraph.SocialGraph;

/**
 * create command: friendship
 * 
 * @author Sebastian Schlicht
 * 
 */
public class CreateFriendship extends SocialGraphOperation {

	/**
	 * followed user
	 */
	private final Node followed;

	/**
	 * create a new create friendship command
	 * 
	 * @param responder
	 *            client responder
	 * @param timestamp
	 *            time stamp of the friendship
	 * @param following
	 *            following user
	 * @param followed
	 *            followed user
	 */
	public CreateFriendship(final ClientResponder responder,
			final long timestamp, final Node following, final Node followed) {
		super(responder, timestamp, following);
		this.followed = followed;
	}

	@Override
	protected boolean execute(final SocialGraph graph) {
		boolean success = false;

		try {
			graph.createFriendship(this.timestamp, this.user, this.followed);
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