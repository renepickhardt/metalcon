package de.uniko.west.socialsensor.graphity.socialgraph.algorithms;

import java.util.HashMap;
import java.util.Map;

import de.uniko.west.socialsensor.graphity.server.statusupdates.StatusUpdate;
import de.uniko.west.socialsensor.graphity.server.statusupdates.StatusUpdateManager;

/**
 * status update creation item
 * 
 * @author Sebastian Schlicht
 * 
 */
public class StatusUpdateCreationItem {

	/**
	 * status update creator identifier
	 */
	private final long userId;

	/**
	 * status update item
	 */
	private final StatusUpdate statusUpdate;

	/**
	 * create a new status update creation item
	 * 
	 * @param userId
	 *            status update creator identifier
	 * @param timestamp
	 *            status update time stamp
	 */
	public StatusUpdateCreationItem(final long userId, final long timestamp) {
		this.userId = userId;

		final Map<String, String[]> values = new HashMap<String, String[]>();
		values.put("message", new String[] { String.valueOf(timestamp) });

		this.statusUpdate = StatusUpdateManager.instantiateStatusUpdate(
				"PlainText", values);
	}

	/**
	 * access status update creator identifier
	 * 
	 * @return status update creator identifier
	 */
	public long getUserId() {
		return this.userId;
	}

	/**
	 * access status update item that is to be created
	 * 
	 * @return status update item
	 */
	public StatusUpdate getStatusUpdate() {
		return this.statusUpdate;
	}

}