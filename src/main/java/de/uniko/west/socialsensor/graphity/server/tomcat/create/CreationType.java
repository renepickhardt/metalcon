package de.uniko.west.socialsensor.graphity.server.tomcat.create;

/**
 * type of a creation request
 * 
 * @author Sebastian Schlicht
 * 
 */
public enum CreationType {

	/**
	 * create a new friendship between two users (unidirectional)
	 */
	FRIENDSHIP("friendship"),

	/**
	 * create a new status update for a specific user
	 */
	STATUS_UPDATE("status_update");

	/**
	 * creation type identifier used as command parameter
	 */
	private final String identifier;

	/**
	 * create a new creation type
	 * 
	 * @param identifier
	 *            creation type identifier used as command parameter
	 */
	private CreationType(final String identifier) {
		this.identifier = identifier;
	}

	/**
	 * access the identifier to switch between types when handling requests
	 * 
	 * @return creation type identifier used as command parameter
	 */
	public String getIdentifier() {
		return this.identifier;
	}

	/**
	 * get the creation type matching to the identifier passed
	 * 
	 * @param identifier
	 *            creation type identifier
	 * @return creation type
	 * @throws IllegalArgumentException
	 *             if the identifier is invalid
	 */
	public static CreationType GetCreationType(final String identifier) {
		if (FRIENDSHIP.getIdentifier().equals(identifier)) {
			return FRIENDSHIP;
		} else if (STATUS_UPDATE.getIdentifier().equals(identifier)) {
			return STATUS_UPDATE;
		}

		throw new IllegalArgumentException("\"" + identifier
				+ "\" is not a valid creation type identifier!");
	}

}