package de.metalcon.server.tomcat.create;

import de.metalcon.server.exceptions.create.InvalidCreateTypeException;

/**
 * type of a create request
 * 
 * @author Sebastian Schlicht
 * 
 */
public enum CreateType {

	/**
	 * create a new user
	 */
	USER("user"),

	/**
	 * create a new follow edge from one user to another
	 */
	FOLLOW("follow"),

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

	/**
	 * get the create type matching to the identifier passed
	 * 
	 * @param identifier
	 *            create type identifier
	 * @return create type
	 * @throws InvalidCreateTypeException
	 *             if the identifier is invalid
	 */
	public static CreateType GetCreateType(final String identifier) {
		if (USER.getIdentifier().equals(identifier)) {
			return USER;
		} else if (FOLLOW.getIdentifier().equals(identifier)) {
			return FOLLOW;
		} else if (STATUS_UPDATE.getIdentifier().equals(identifier)) {
			return STATUS_UPDATE;
		}

		throw new InvalidCreateTypeException("\"" + identifier
				+ "\" is not a valid create type. valid values: "
				+ USER.getIdentifier() + ", " + FOLLOW.getIdentifier() + ", "
				+ STATUS_UPDATE.getIdentifier());
	}

}