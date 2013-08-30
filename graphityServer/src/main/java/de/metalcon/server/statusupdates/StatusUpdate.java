package de.metalcon.server.statusupdates;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import de.metalcon.socialgraph.User;

/**
 * basic status update class
 * 
 * @author sebschlicht
 * 
 */
public abstract class StatusUpdate {

	/**
	 * activitystrea.ms date formatter
	 */
	private static final SimpleDateFormat dateFormatter = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

	/**
	 * status update identifier
	 */
	protected String id;

	/**
	 * status update type identifier
	 */
	protected final String type;

	/**
	 * status update publishing time stamp
	 */
	protected long timestamp;

	/**
	 * status update creator
	 */
	protected User creator;

	/**
	 * create a new basic status update
	 * 
	 * @param type
	 *            status update type identifier
	 */
	public StatusUpdate(final String type) {
		this.type = type;
	}

	/**
	 * get status update identifier
	 * 
	 * @return status update identifier
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * set status update identifier
	 * 
	 * @param id
	 *            status update identifier
	 */
	public void setId(final String id) {
		this.id = id;
	}

	/**
	 * get status update type
	 * 
	 * @return status update type identifier
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * get status update publishing time stamp
	 * 
	 * @return status update publishing time stamp
	 */
	public long getTimestamp() {
		return this.timestamp;
	}

	/**
	 * set status update publishing time stamp for JSON parsing
	 * 
	 * @param timestamp
	 *            status update publishing time stamp
	 */
	public void setTimestamp(final long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * set status update creator for JSON parsing
	 * 
	 * @param creator
	 *            status update creator
	 */
	public void setCreator(final User creator) {
		this.creator = creator;
	}

	/**
	 * parse the status update to an Activity JSON String
	 * 
	 * @return Activity JSON String representing this status update<br>
	 *         (Activitystrea.ms)
	 */
	@SuppressWarnings("unchecked")
	public JSONObject toJSONObject() {
		final JSONObject activity = new JSONObject();

		// parse time stamp
		activity.put("published",
				dateFormatter.format(new Date(this.timestamp)));

		// TODO: check performance
		// parse user
		// final Map<String, Object> user = this.creator.toActorJSON();
		// activity.put("actor", user);

		// parse verb:read
		activity.put("verb", "read");

		// parse object
		final Map<String, Object> object = this.toObjectJSON();
		activity.put("object", object);

		return activity;
	}

	/**
	 * parse the status update to an Object JSON map
	 * 
	 * @return Object JSON map representing this status update<br>
	 *         (Activitystrea.ms)
	 */
	protected Map<String, Object> toObjectJSON() {
		final Map<String, Object> objectJSON = new LinkedHashMap<String, Object>();
		// TODO: select correct object type - usage of own types allowed?
		objectJSON.put("objectType", "article");
		objectJSON.put("type", this.type);
		objectJSON.put("id", this.id);

		return objectJSON;
	}

}