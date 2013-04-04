package de.uniko.west.socialsensor.graphity.socialgraph;

/**
 * social graph operations for all graph implementations used
 * 
 * @author sebschlicht
 * 
 */
public interface SocialGraphOperations {

	/**
	 * add a status update to a user
	 * 
	 * @param timestamp
	 *            time stamp of the status update
	 * @param userID
	 *            user identifier
	 * @param content
	 *            status update content object
	 * @return true - status update has been added successfully<br>
	 *         false otherwise
	 */
	boolean addStatusUpdate(long timestamp, long userID, StatusUpdate content);
}
