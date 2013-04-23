package de.uniko.west.socialsensor.graphity.socialgraph.operations;

import de.uniko.west.socialsensor.graphity.socialgraph.SocialGraph;

/**
 * create command: friendship
 * 
 * @author Sebastian Schlicht
 * 
 */
public class CreateFriendship extends SocialGraphOperation {

	/**
	 * followed user's identifier
	 */
	private final long followedId;

	/**
	 * create a new create friendship command
	 * 
	 * // TODO: create own exceptions to catch // send error code
	 * this.responder.error(500, "thrown exceptions are not specified yet!");
	 * 
	 * @param timestamp
	 *            time stamp of the friendship
	 * @param followingId
	 *            following user's identifier
	 * @param followedId
	 *            followed user's identifier
	 */
	public CreateFriendship(final ClientResponder responder,
			final long timestamp, final long followingId, final long followedId) {
		super(responder, timestamp, followingId);
		this.followedId = followedId;
	}

	@Override
	protected boolean execute(SocialGraph graph) {
		boolean success = graph.createFriendship(this.timestamp, this.userId,
				this.followedId);

		if (success) {
			// TODO: send something? HTTP 200:OK sent anyway
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