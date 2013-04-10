package de.uniko.west.socialsensor.graphity.socialgraph.algorithms;

import org.neo4j.graphdb.Node;

import de.uniko.west.socialsensor.graphity.socialgraph.NeoUtils;
import de.uniko.west.socialsensor.graphity.socialgraph.Properties;
import de.uniko.west.socialsensor.graphity.socialgraph.SocialGraphRelationshipType;
import de.uniko.west.socialsensor.graphity.socialgraph.statusupdates.StatusUpdate;
import de.uniko.west.socialsensor.graphity.socialgraph.statusupdates.StatusUpdateWrapper;

/**
 * user used within Gravity reading process
 * 
 * @author Sebastian Schlicht
 * 
 */
public class GraphityUser {

	/**
	 * user node represented
	 */
	private final Node userNode;

	/**
	 * last recent status update node of the user
	 */
	private Node nextStatusUpdateNode;

	/**
	 * current status update's time stamp
	 */
	private long statusUpdateTimestamp;

	/**
	 * create a new user for the Graphity reading process
	 * 
	 * @param user
	 *            user node represented
	 */
	public GraphityUser(final Node user) {
		this.userNode = user;
		this.nextStatusUpdateNode = user;

		this.nextStatusUpdate();
	}

	/**
	 * access the user being represented
	 * 
	 * @return user the node is representing
	 */
	public Node getUser() {
		return this.userNode;
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
	private StatusUpdate getStatusUpdate() {
		return (StatusUpdate) this.nextStatusUpdateNode
				.getProperty(Properties.Content);
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

	/**
	 * generate a status update wrapper for the status update loaded before<br>
	 * (calls will cause an exception if you did not successfully load one
	 * before)
	 * 
	 * @return status update wrapper for the status update loaded before
	 */
	public StatusUpdateWrapper getStatusUpdateWrapper() {
		final StatusUpdateWrapper wrapper = new StatusUpdateWrapper(
				this.statusUpdateTimestamp, this.userNode.getId(),
				this.getStatusUpdate());
		this.nextStatusUpdate();
		return wrapper;
	}

}