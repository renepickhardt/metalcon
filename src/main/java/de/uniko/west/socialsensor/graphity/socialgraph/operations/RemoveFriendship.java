package de.uniko.west.socialsensor.graphity.socialgraph.operations;

import de.uniko.west.socialsensor.graphity.server.exceptions.RequestFailedException;
import de.uniko.west.socialsensor.graphity.socialgraph.SocialGraph;

/**
 * remove command: friendship
 * 
 * @author Sebastian Schlicht
 * 
 */
public class RemoveFriendship extends SocialGraphOperation {

	/**
	 * followed user's identifier
	 */
	private final long followedId;

	public RemoveFriendship(final ClientResponder responder,
			final long timestamp, final long followingId, final long followedId) {
		super(responder, timestamp, followingId);
		this.followedId = followedId;
	}

	@Override
	protected boolean execute(final SocialGraph graph) {
		boolean success = false;

		try {
			graph.removeFriendship(this.timestamp, this.userId, this.followedId);
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