package de.uniko.west.socialsensor.graphity.socialgraph.algorithms;

import org.neo4j.graphdb.Node;

import de.uniko.west.socialsensor.graphity.socialgraph.NeoUtils;
import de.uniko.west.socialsensor.graphity.socialgraph.Properties;
import de.uniko.west.socialsensor.graphity.socialgraph.SocialGraphRelationshipType;

/**
 * user used within first social networking approach's reading process
 * 
 * @author Sebastian Schlicht
 * 
 */
public class StatusUpdateUser {

	/**
	 * last recent status update node of the user
	 */
	private Node nextStatusUpdateNode;

	/**
	 * current status update's time stamp
	 */
	private long statusUpdateTimestamp;

	/**
	 * create a new user for the reading process
	 * 
	 * @param user
	 *            user node represented
	 */
	public StatusUpdateUser(final Node user) {
		this.nextStatusUpdateNode = user;

		this.nextStatusUpdate();
	}

	/**
	 * check if another status update is available
	 * 
	 * @return true - if another status update is available<br>
	 *         false otherwise
	 */
	public boolean hasStatusUpdate() {
		return (this.nextStatusUpdateNode != null);
	}

	/**
	 * load another status update<br>
	 * (will cause an exception if called twice while none available)
	 */
	private void nextStatusUpdate() {
		this.nextStatusUpdateNode = NeoUtils.getNextSingleNode(
				this.nextStatusUpdateNode, SocialGraphRelationshipType.UPDATE);

		// load status update time stamp if existing
		if (this.nextStatusUpdateNode != null) {
			this.statusUpdateTimestamp = (long) this.nextStatusUpdateNode
					.getProperty(Properties.Timestamp);
		}
	}

	/**
	 * access the status update loaded<br>
	 * (calls will cause an exception if you did not successfully load one
	 * before)
	 * 
	 * @return status update loaded before
	 */
	public String getStatusUpdate() {
		final String statusUpdate = (String) this.nextStatusUpdateNode
				.getProperty(Properties.Content);
		this.nextStatusUpdate();
		return statusUpdate;
	}

	/**
	 * access the time stamp of the status update loaded before<br>
	 * (calls will cause an exception if you did not successfully load one
	 * before)
	 * 
	 * @return time stamp of the status update loaded before
	 */
	public long getStatusUpdateTimestamp() {
		return this.statusUpdateTimestamp;
	}

}