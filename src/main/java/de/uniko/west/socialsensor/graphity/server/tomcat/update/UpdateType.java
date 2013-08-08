package de.uniko.west.socialsensor.graphity.server.tomcat.update;

/**
 * type of an update request
 * 
 * @author sebschlicht
 * 
 */
public enum UpdateType {

	/**
	 * update an user
	 */
	USER("user"),

	/**
	 * update a status update the user created
	 */
	STATUS_UPDATE("status_update");

	/**
	 * update type identifier used as command parameter
	 */
	private final String identifier;

	/**
	 * create a new update type
	 * 
	 * @param identifier
	 *            update type identifier used as command parameter
	 */
	private UpdateType(final String identifier) {
		this.identifier = identifier;
	}

	/**
	 * access the identifier to switch between types when handling requests
	 * 
	 * @return update type identifier used as command parameter
	 */
	public String getIdentifier() {
		return this.identifier;
	}

	/**
	 * get the update type matching to the identifier passed
	 * 
	 * @param identifier
	 *            update type identifier
	 * @return update type
	 * @throws IllegalArgumentException
	 *             if the identifier is invalid
	 */
	public static UpdateType GetUpdateType(final String identifier) {
		if (USER.getIdentifier().equals(identifier)) {
			return USER;
		} else if (STATUS_UPDATE.getIdentifier().equals(identifier)) {
			return STATUS_UPDATE;
		}

		throw new IllegalArgumentException("\"" + identifier
				+ "\" is not a valid update type identifier!");
	}

}