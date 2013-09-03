package de.metalcon.imageServer.protocol;

/**
 * constants used in the image server protocol
 * 
 * @author sebschlicht
 * 
 */
public class ProtocolConstants {

	/**
	 * JSON field name for the status message
	 */
	public static final String STATUS_MESSAGE = "statusMessage";

	/**
	 * JSON field name for the detailed description and the solution
	 */
	public static final String SOLUTION = "solution";

	public class StatusMessage {
		public class Create {
			// What's this for?
			public static final String IMAGE_IDENTIFIER = "imageIdentifier";

			public static final String IMAGE_IDENTIFIER_MISSING = "No Image Identifier given";
			public static final String IMAGESTREAM_MISSING = "No ImageStream found";
			public static final String IMAGE_METADATA_MISSING = "No meta data given";
			public static final String AUTOROTATE_FLAG_MISSING = "Autorotate flag not found";
			public static final String AUTOROTATE_FLAG_MALFORMED = "Autorotate flag malformed";
		}
	}

}