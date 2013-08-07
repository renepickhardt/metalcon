package de.uniko.west.socialsensor.graphity.socialgraph.operations;

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
		boolean success = graph.removeFriendship(this.timestamp, this.userId,
				this.followedId);

		if (success) {
			// TODO: send something? HTTP 200:OK sent anyway
			this.responder.finish();

			return true;
		} else {
			// TODO: send error code as the friendship did not exist
			this.responder.error(404, "friendship not existing!");

			return false;
		}
	}

}
