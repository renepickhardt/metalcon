package de.metalcon.autocompleteServer.Create;

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
	// TODO: replace "null" with better human readable Strings.
	public static final String REQUEST_MUST_BE_MULTIPART = "request must be multipart";
	public static final String WEIGHT_NOT_GIVEN = "weight not given";
	public static final String WEIGHT_NOT_A_NUMBER = "weight is NaN";
	public static final String SUGGESTION_KEY_NOT_GIVEN = "Suggestion key not given";
	public static final String NO_IMAGE = "No image inserted";
	public static final String IMAGE_GEOMETRY_TOO_BIG = "your image is too big";
	public static final String IMAGE_FILE_TOO_LARGE = "your image file is too big";
	public static final String KEY_TOO_LONG = "key too long";
	public static final String SUGGESTION_STRING_TOO_LONG = "suggestion string too long";

}
