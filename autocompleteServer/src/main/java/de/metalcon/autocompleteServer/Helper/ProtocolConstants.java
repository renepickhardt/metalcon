package de.metalcon.autocompleteServer.Helper;

/**
 * Constants that are used in the Auto Suggest Transfer Protocol (ASTP)
 * @author Rene Pickhardt
 *
 */
public class ProtocolConstants {
	public static final String QUERY_PARAMETER = "term";
	public static final  String INDEX_PARAMETER = "indexName";
	public static final  String NUM_ITEMS= "numItems";
	
	
	/**
	 * the default Number of Items which can be retrieved from the Suggest Server 
	 */
	public static final  Integer MAX_NUMBER_OF_SUGGESTIONS = 7;
}
