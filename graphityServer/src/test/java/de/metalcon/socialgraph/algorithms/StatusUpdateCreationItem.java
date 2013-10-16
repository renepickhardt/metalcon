package de.metalcon.socialgraph.algorithms;

import org.neo4j.graphdb.Node;

import de.metalcon.server.exceptions.StatusUpdateInstantiationFailedException;
import de.metalcon.server.statusupdates.StatusUpdate;
import de.metalcon.server.statusupdates.StatusUpdateManager;
import de.metalcon.utils.FormItemList;

/**
 * status update creation item
 * 
 * @author Sebastian Schlicht
 * 
 */
public class StatusUpdateCreationItem {

	/**
	 * status update creator
	 */
	private final Node user;

	/**
	 * status update item
	 */
	private final StatusUpdate statusUpdate;

	/**
	 * create a new status update creation item
	 * 
	 * @param user
	 *            status update creator
	 * @param timestamp
	 *            status update time stamp
	 * @throws StatusUpdateInstantiationFailedException
	 *             if the status update could not be instantiated
	 */
	public StatusUpdateCreationItem(final Node user, final long timestamp)
			throws StatusUpdateInstantiationFailedException {
		this.user = user;

		final FormItemList values = new FormItemList();
		values.addField("message", String.valueOf(timestamp));

		this.statusUpdate = StatusUpdateManager.instantiateStatusUpdate(
				"Plain", values);
	}

	/**
	 * access status update creator
	 * 
	 * @return status update creator
	 */
	public Node getUser() {
		return this.user;
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