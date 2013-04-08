package de.uniko.west.socialsensor.graphity.socialgraph.algorithms;

import org.neo4j.graphdb.Node;

import de.uniko.west.socialsensor.graphity.socialgraph.NeoUtils;
import de.uniko.west.socialsensor.graphity.socialgraph.Properties;
import de.uniko.west.socialsensor.graphity.socialgraph.SocialGraphRelationshipType;
import de.uniko.west.socialsensor.graphity.socialgraph.StatusUpdate;
import de.uniko.west.socialsensor.graphity.socialgraph.StatusUpdateWrapper;

/**
 * user node used within Gravity reading process
 * 
 * @author Sebastian Schlicht
 * 
 */
public class GraphityUserNode {

	/**
	 * user the node is representing
	 */
	private final Node user;

	/**
	 * last recent status update of the user
	 */
	private Node nextStatusUpdate;

	/**
	 * current status update's time stamp
	 */
	private long statusUpdateTimestamp;

	/**
	 * create a new user node for the Graphity reading process
	 * 
	 * @param user
	 */
	public GraphityUserNode(final Node user) {
		this.user = user;
		this.nextStatusUpdate = user;
	}

	/**
	 * access the user being represented
	 * 
	 * @return user the node is representing
	 */
	public Node getUser() {
		return this.user;
	}

	/**
	 * load another status update
	 * 
	 * @return true - if another status update is existing<br>
	 *         false otherwise (another call will cause an exception!)
	 */
	public boolean hasStatusUpdate() {
		this.nextStatusUpdate = NeoUtils.getNextSingleNode(
				this.nextStatusUpdate, SocialGraphRelationshipType.UPDATE);

		if (this.nextStatusUpdate != null) {
			this.statusUpdateTimestamp = (long) this.nextStatusUpdate
					.getProperty(Properties.Timestamp);
			return true;
		}
		return false;
	}

	/**
	 * access the status update loaded<br>
	 * (calls will cause an exception if you did not successfully load one
	 * before)
	 * 
	 * @return status update loaded before
	 */
	private StatusUpdate getStatusUpdate() {
		return (StatusUpdate) this.nextStatusUpdate
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
		return new StatusUpdateWrapper(this.statusUpdateTimestamp,
				this.user.getId(), this.getStatusUpdate());
	}

}