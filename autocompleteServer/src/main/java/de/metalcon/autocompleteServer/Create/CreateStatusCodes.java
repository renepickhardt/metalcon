package de.metalcon.autocompleteServer.Create;

import de.metalcon.autocompleteServer.Helper.ProtocolConstants;

/**
 * Status codes that are used for the Create responses of the Auto Suggest
 * Transfer Protocol (ASTP)
 * 
 * @author Christian Schowalter
 * 
 */
public class CreateStatusCodes {

	public static final String QUERYNAME_NOT_GIVEN = "No search term given. A suggest item always needs a term. Please enter a search term for your item.";
	public static final String INDEXNAME_NOT_GIVEN = "Missing index name. You need to specify which index the entry should be inserted into.";
	public static final String REQUEST_MUST_BE_MULTIPART = "The HTTP request must be multipart/form-data. Your request has another content-type and can't be processed.";
	public static final String WEIGHT_NOT_GIVEN = "weight not given";
	public static final String WEIGHT_NOT_A_NUMBER = "weight is not a number and was discarded. Weight needs to be a positive integer.";
	public static final String SUGGESTION_KEY_NOT_GIVEN = "Suggestion key not given";
	public static final String NO_IMAGE = "No image inserted";
	public static final String IMAGE_GEOMETRY_TOO_BIG = "your image geometry exceeds the limit of "
			+ ProtocolConstants.IMAGE_HEIGHT
			+ "*"
			+ ProtocolConstants.IMAGE_WIDTH + " Pixels";
	public static final String IMAGE_FILE_TOO_LARGE = "your image file exceeds the size limit of"
			+ ProtocolConstants.MAX_IMAGE_FILE_LENGTH + " byte";
	public static final String KEY_TOO_LONG = "Your key length exceeds the limit of ";
	public static final String SUGGESTION_STRING_TOO_LONG = "Your suggestion string is longer than the limit of "
			+ ProtocolConstants.MAX_SUGGESTION_LENGTH;

}
