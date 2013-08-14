package de.metalcon.autocompleteServer.Create;

/**
 * Status codes that are used for the Create responses of the Auto Suggest
 * Transfer Protocol (ASTP)
 * 
 * @author Christian Schowalter
 * 
 */
public class CreateStatusCodes {

	public static final String QUERYNAME_NOT_GIVEN = "No name given. A suggest item always needs a name. Please enter a name for your item.";
	public static final String INDEXNAME_NOT_GIVEN = "Missing index name. You need to specify which index the entry should be inserted into.";
	// TODO: replace "null" with human readable Strings.
	public static final String REQUEST_MUST_BE_MULTIPART = null;
	public static final String WEIGHT_NOT_GIVEN = null;
	public static final String WEIGHT_NOT_A_NUMBER = null;
	public static final String SUGGESTION_KEY_NOT_GIVEN = null;
	public static final String NO_IMAGE = null;
	public static final String KEY_TOO_LONG = null;
	public static final String SUGGESTION_STRING_TOO_LONG = null;

}
