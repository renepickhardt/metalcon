/**
 * 
 */
package de.metalcon.autocompleteServer.Helper;

/**
 * Status codes that are used in the Auto Suggest Transfer Protocol (ASTP)
 * @author Rene Pickhardt
 *
 */
public class StatusCodes {
	public static final String NUMITEMS_NOT_AN_INTEGER = "The numItems Parameter needs to be an Integer. I set it to: " + ProtocolConstants.MAX_NUMBER_OF_SUGGESTIONS;
	public static final String NUMITEMS_NOT_GIVEN = "You did not give the number of items I set it to: "+ ProtocolConstants.MAX_NUMBER_OF_SUGGESTIONS;
	public static final String NUMITEMS_OUT_OF_RANGE = "The rquested number of items is out of range. It has to be between 1 and " + ProtocolConstants.MAX_NUMBER_OF_SUGGESTIONS + " I set it to: "+ ProtocolConstants.MAX_NUMBER_OF_SUGGESTIONS;
	public static final String QUERYNAME_NOT_GIVEN = "No name given. A suggest item always needs a name. Please enter a name for your item.";
	public static final String INDEXNAME_NOT_GIVEN = "Missing index name. You need to specify which index the entry should be inserted into.";
	
	public static final String NO_INDEX_GIVEN = "You did not provide the index Field. I set it to the default index named: " + ProtocolConstants.DEFAULT_INDEX_NAME;
	public static final String INDEX_UNKNOWN = "The index that you want to query from is unknown to me. I used the default index named: " + ProtocolConstants.DEFAULT_INDEX_NAME;
	public static final String NO_INDEX_AVAILABLE = "The given index is not known to the server ADN / OR the default index is not up. Check if the server is correctly configured.";
	
	public static final String NO_TERM_GIVEN = "I cannot give you suggestions if you don't provide me a prefix";
}
