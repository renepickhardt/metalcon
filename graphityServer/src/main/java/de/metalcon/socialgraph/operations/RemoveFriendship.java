package de.metalcon.socialgraph.operations;

import org.neo4j.graphdb.Node;

import de.metalcon.server.exceptions.RequestFailedException;
import de.metalcon.socialgraph.SocialGraph;

/**
 * remove command: friendship
 * 
 * @author Sebastian Schlicht
 * 
 */
public class RemoveFriendship extends SocialGraphOperation {

	/**
	 * followed user
	 */
	private final Node followed;

	/**
	 * create a new delete follow edge command
	 * 
	 * @param responder
	 *            client responder
	 * @param following
	 *            following user
	 * @param followed
	 *            followed user
	 */
	public RemoveFriendship(final ClientResponder responder,
			final Node following, final Node followed) {
		super(responder, following);
		this.followed = followed;
	}

	@Override
	protected boolean execute(final SocialGraph graph) {
		boolean success = false;

		try {
			graph.removeFriendship(this.user, this.followed);
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