package de.uniko.west.socialsensor.graphity.socialgraph.operations;

import de.uniko.west.socialsensor.graphity.server.exceptions.RequestFailedException;
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
	protected boolean execute(final SocialGraph graph) {
		boolean success = false;

		try {
			graph.createFriendship(this.timestamp, this.userId, this.followedId);
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