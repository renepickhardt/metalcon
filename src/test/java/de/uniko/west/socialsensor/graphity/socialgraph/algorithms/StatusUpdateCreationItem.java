package de.uniko.west.socialsensor.graphity.socialgraph.algorithms;

import de.uniko.west.socialsensor.graphity.server.statusupdates.StatusUpdate;
import de.uniko.west.socialsensor.graphity.server.statusupdates.StatusUpdateManager;
import de.uniko.west.socialsensor.graphity.server.tomcat.create.FormItemDoubleUsageException;
import de.uniko.west.socialsensor.graphity.server.tomcat.create.FormItemList;

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

		final FormItemList values = new FormItemList();
		try {
			values.addField("message", String.valueOf(timestamp));
		} catch (final FormItemDoubleUsageException e) {
			e.printStackTrace();
		}

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