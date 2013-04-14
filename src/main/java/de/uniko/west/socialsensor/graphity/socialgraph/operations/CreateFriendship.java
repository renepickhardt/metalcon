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
	 * @param timestamp
	 *            time stamp of the friendship
	 * @param followingId
	 *            following user's identifier
	 * @param followedId
	 *            followed user's identifier
	 */
	public CreateFriendship(final long timestamp, final long followingId,
			final long followedId) {
		super(timestamp, followingId);
		this.followedId = followedId;
	}

	@Override
	protected boolean execute(SocialGraph graph) {
		// TODO: send error code (may be zero)
		return graph.createFriendship(this.timestamp, this.userId,
				this.followedId);
	}

}