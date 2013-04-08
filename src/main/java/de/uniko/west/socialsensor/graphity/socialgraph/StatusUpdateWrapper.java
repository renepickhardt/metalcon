package de.uniko.west.socialsensor.graphity.socialgraph;

/**
 * wrapper for status updates and their creators
 * 
 * @author Sebastian Schlicht
 * 
 */
public class StatusUpdateWrapper {

	/**
	 * time stamp of the status update creation
	 */
	private final long timestamp;

	/**
	 * status update creator identifier
	 */
	private final long userId;

	/**
	 * status update object
	 */
	private final StatusUpdate statusUpdate;

	/**
	 * create a new status update wrapper
	 * 
	 * @param timestamp
	 *            time stamp of the status update creation
	 * @param userId
	 *            status update creator identifier
	 * @param statusUpdateNode
	 *            status update node
	 */
	public StatusUpdateWrapper(final long timestamp, final long userId,
			final StatusUpdate statusUpdate) {
		this.timestamp = timestamp;
		this.userId = userId;
		this.statusUpdate = statusUpdate;
	}

}