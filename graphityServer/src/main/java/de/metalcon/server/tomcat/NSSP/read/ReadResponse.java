package de.metalcon.server.tomcat.NSSP.read;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.metalcon.server.tomcat.NSSP.ProtocolConstants;
import de.metalcon.server.tomcat.NSSP.Response;

/**
 * response to read requests according to NSSP
 * 
 * @author sebschlicht
 * 
 */
public class ReadResponse extends Response {

	/**
	 * add status message: user identifier missing
	 */
	public void userIdentifierMissing() {
		this.parameterMissing(ProtocolConstants.Parameters.Read.USER_IDENTIFIER,
				"Please provide a user identifier matching to an existing user.");
	}

	/**
	 * add status message: poster identifier missing
	 */
	public void posterIdentifierMissing() {
		this.parameterMissing(ProtocolConstants.Parameters.Read.POSTER_IDENTIFIER,
				"Please provide a user identifier matching to an existing user.");
	}

	/**
	 * add status message: number of items missing
	 */
	public void numItemsMissing() {
		this.parameterMissing(
				ProtocolConstants.Parameters.Read.NUM_ITEMS,
				"Please provide a number greater than zero specifying how many news feed items you want to retrieve.");
	}

	/**
	 * add status message: retrieval flag missing
	 */
	public void ownUpdatesMissing() {
		this.parameterMissing(
				ProtocolConstants.Parameters.Read.OWN_UPDATES,
				"Please provide a number specifying what news feed items you want to retrieve. Pass \"0\" to retrieve items from or \"1\" to retrieve items for the user.");
	}

	/**
	 * add status message: user identifier invalid
	 * 
	 * @param userId
	 *            user identifier passed
	 */
	public void userIdentifierInvalid(final String userId) {
		this.addStatusMessage(ProtocolConstants.StatusCodes.Read.USER_NOT_EXISTING,
				"there is no user with the identifier \"" + userId
						+ "\". Please provide a valid user identifier.");
	}

	/**
	 * add status message: poster identifier invalid
	 * 
	 * @param posterId
	 *            poster identifier passed
	 */
	public void posterIdentifierInvalid(final String posterId) {
		this.addStatusMessage(ProtocolConstants.StatusCodes.Read.POSTER_NOT_EXISTING,
				"there is no user with the identifier \"" + posterId
						+ "\". Please provide a valid user identifier.");
	}

	/**
	 * add status message: number of items invalid
	 * 
	 * @param solution
	 *            detailed description and solution
	 */
	public void numItemsInvalid(final String solution) {
		this.addStatusMessage(ProtocolConstants.StatusCodes.Read.NUM_ITEMS_INVALID,
				solution);
	}

	/**
	 * add status message: retrieval flag
	 * 
	 * @param ownUpdates
	 *            retrieval flag passed
	 */
	public void ownUpdatesInvalid(final String ownUpdates) {
		this.addStatusMessage(
				ProtocolConstants.StatusCodes.Read.OWN_UPDATES_INVALID,
				"\""
						+ ownUpdates
						+ "\" is not a number. Please provide a number such as \"0\" to retrieve the items for the user or \"1\" to retrieve those from the user.");
	}

	/**
	 * add the news stream according to the Activitystrea.ms format
	 * 
	 * @param activities
	 *            news stream items
	 */
	@SuppressWarnings("unchecked")
	public void addActivityStream(final List<JSONObject> activities) {
		final JSONArray items = new JSONArray();
		for (JSONObject activity : activities) {
			items.add(activity);
		}
		this.json.put("items", items);
	}
}