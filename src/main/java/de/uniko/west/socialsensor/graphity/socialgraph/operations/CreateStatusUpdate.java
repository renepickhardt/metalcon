package de.uniko.west.socialsensor.graphity.socialgraph.operations;

import de.uniko.west.socialsensor.graphity.socialgraph.SocialGraph;
import de.uniko.west.socialsensor.graphity.socialgraph.StatusUpdate;

/**
 * create command: status update
 * 
 * @author Sebastian Schlicht
 * 
 */
public class CreateStatusUpdate extends SocialGraphOperation {

	/**
	 * time stamp of the status update
	 */
	private final long timestamp;

	/**
	 * user identifier
	 */
	private final long userId;

	/**
	 * status update content object
	 */
	private final StatusUpdate content;

	/**
	 * create a new create status update command
	 * 
	 * @param timestamp
	 *            time stamp of the status update
	 * @param userId
	 *            user identifier
	 * @param content
	 *            status update content object
	 */
	public CreateStatusUpdate(final long timestamp, final long userId,
			final StatusUpdate content) {
		this.timestamp = timestamp;
		this.userId = userId;
		this.content = content;
	}

	@Override
	protected boolean execute(final SocialGraph graph) {
		return graph.createStatusUpdate(this.timestamp, this.userId, this.content);
	}

}