package de.metalcon.autocompleteServer.Helper;

/**
 * Status codes that are used for the Create responses of the Auto Suggest Transfer Protocol (ASTP)
 * @author Christian Schowalter
 *
 */
public class CreateStatusCodes {

	public static final String QUERYNAME_NOT_GIVEN = "No name given. A suggest item always needs a name. Please enter a name for your item.";
	public static final String INDEXNAME_NOT_GIVEN = "Missing index name. You need to specify which index the entry should be inserted into.";

}
