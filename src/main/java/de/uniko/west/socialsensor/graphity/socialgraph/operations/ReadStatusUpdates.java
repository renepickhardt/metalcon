package de.uniko.west.socialsensor.graphity.socialgraph.operations;

import java.util.List;

import de.uniko.west.socialsensor.graphity.socialgraph.SocialGraph;

/**
 * read command: status updates
 * 
 * @author Sebastian Schlicht
 * 
 */
public class ReadStatusUpdates extends SocialGraphOperation {

	/**
	 * identifier of the targeted stream's owner
	 */
	private final long posterId;

	/**
	 * number of items to be read
	 */
	private final int numItems;

	/**
	 * single stream flag
	 */
	private final boolean ownUpdates;

	/**
	 * create a new read status updates command
	 * 
	 * @param timestamp
	 *            time stamp of the access
	 * @param readerId
	 *            identifier of the reading user
	 * @param posterId
	 *            identifier of the targeted stream's owner
	 * @param numItems
	 *            number of items to be read
	 * @param ownUpdates
	 *            single stream flag
	 */
	public ReadStatusUpdates(final long timestamp, final long readerId,
			final long posterId, final int numItems, final boolean ownUpdates) {
		super(timestamp, readerId);
		this.posterId = posterId;
		this.numItems = numItems;
		this.ownUpdates = ownUpdates;
	}

	@Override
	protected boolean execute(final SocialGraph graph) {
		List<String> activities = graph.readStatusUpdates(this.posterId,
				this.userId, this.numItems, this.ownUpdates);

		if (activities != null) {
			// build Activity stream
			final String activityStream = getActivityStream(activities);

			// TODO: send stream to client

			return true;
		} else {
			// TODO: send error code
			return false;
		}
	}

	/**
	 * create the Activity stream containing the Activities specified
	 * 
	 * @param activities
	 *            Activities to be contained in the Activity stream
	 * @return Activity stream JSON<br>
	 *         (Activitystrea.ms)
	 */
	public static String getActivityStream(final List<String> activities) {
		final StringBuilder activityStream = new StringBuilder("{\"items\":[");

		final int numItems = activities.size();
		int i = 0;
		for (String activity : activities) {
			activityStream.append(activity);
			if (++i != numItems) {
				activityStream.append(",");
			}
		}

		activityStream.append("]}");
		return activityStream.toString();
	}

}