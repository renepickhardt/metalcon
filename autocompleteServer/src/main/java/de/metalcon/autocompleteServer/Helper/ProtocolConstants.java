package de.metalcon.autocompleteServer.Helper;

/**
 * Constants that are used in the Auto Suggest Transfer Protocol (ASTP)
 * 
 * @author Rene Pickhardt
 * 
 */
public class ProtocolConstants {
	public static final String QUERY_PARAMETER = "term";
	public static final String INDEX_PARAMETER = "indexName";
	public static final String NUM_ITEMS = "numItems";

	/**
	 * form parameter names for the create request of the ASTP
	 */
	public static final String SUGGESTION_WEIGHT = null;
	public static final String SUGGESTION_KEY = null;
	public static final String SUGGESTION_STRING = "term";
	public static final int SUGGESTION_LENGTH = 80;
	public static final String IMAGE = "image";

	/**
	 * the default Number of Items which can be retrieved from the Suggest
	 * Server
	 */
	public static final Integer MAX_NUMBER_OF_SUGGESTIONS = 7;
	public static final String DEFAULT_INDEX_NAME = "generalindex";

}
