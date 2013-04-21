package de.uniko.west.socialsensor.graphity.server.tomcat;

/**
 * type of a creation request
 * 
 * @author Sebastian Schlicht
 * 
 */
public enum CreateType {

	/**
	 * create a new friendship between two users (unidirectional)
	 */
	FRIENDSHIP("friendship"),

	/**
	 * create a new status update for a specific user
	 */
	STATUS_UPDATE("status_update");

	/**
	 * create type identifier used as command parameter
	 */
	private final String identifier;

	/**
	 * create a new create type
	 * 
	 * @param identifier
	 *            create type identifier used as command parameter
	 */
	private CreateType(final String identifier) {
		this.identifier = identifier;
	}

	/**
	 * access the identifier to switch between types when handling requests
	 * 
	 * @return create type identifier used as command parameter
	 */
	public String getIdentifier() {
		return this.identifier;
	}

	public static CreateType GetCreateType(final String identifier) {
		if (FRIENDSHIP.getIdentifier().equals(identifier)) {
			return FRIENDSHIP;
		} else if (STATUS_UPDATE.getIdentifier().equals(identifier)) {
			return STATUS_UPDATE;
		} else {
			return null;
		}
	}

}