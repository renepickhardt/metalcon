package de.uniko.west.socialsensor.graphity.server.tomcat.delete;

import de.uniko.west.socialsensor.graphity.server.exceptions.delete.InvalidDeleteTypeException;

/**
 * type of a delete request
 * 
 * @author sebschlicht
 * 
 */
public enum DeleteType {

	/**
	 * delete a user
	 */
	USER("user"),

	/**
	 * delete a follow edge from one user to another
	 */
	FOLLOW("follow"),

	/**
	 * delete a status update the user created
	 */
	STATUS_UPDATE("status_update");

	/**
	 * delete type identifier used as command parameter
	 */
	private final String identifier;

	/**
	 * create a new delete type
	 * 
	 * @param identifier
	 *            delete type identifier used as command parameter
	 */
	private DeleteType(final String identifier) {
		this.identifier = identifier;
	}

	/**
	 * access the identifier to switch between types when handling requests
	 * 
	 * @return delete type identifier used as command parameter
	 */
	public String getIdentifier() {
		return this.identifier;
	}

	/**
	 * get the delete type matching to the identifier passed
	 * 
	 * @param identifier
	 *            delete type identifier
	 * @return delete type
	 * @throws InvalidDeleteTypeException
	 *             if the identifier is invalid
	 */
	public static DeleteType GetDeleteType(final String identifier) {
		if (USER.getIdentifier().equals(identifier)) {
			return USER;
		} else if (FOLLOW.getIdentifier().equals(identifier)) {
			return FOLLOW;
		} else if (STATUS_UPDATE.getIdentifier().equals(identifier)) {
			return STATUS_UPDATE;
		}

		throw new InvalidDeleteTypeException("\"" + identifier
				+ "\" is not a valid delete type. valid values: "
				+ USER.getIdentifier() + ", " + FOLLOW.getIdentifier() + ", "
				+ STATUS_UPDATE.getIdentifier());
	}

}