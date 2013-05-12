package de.uniko.west.socialsensor.graphity.socialgraph;

/**
 * node property identifiers
 * 
 * @author Sebastian Schlicht
 * 
 */
public final class Properties {

	/**
	 * time stamp in milliseconds for status updates
	 */
	public static final String TIMESTAMP = "timestamp";

	/**
	 * content type identifier for status updates
	 */
	public static final String CONTENT_TYPE = "content_type";

	/**
	 * content object for status updates
	 */
	public static final String CONTENT = "content";

	/**
	 * time stamp in milliseconds for the last recent status update for users
	 */
	public static final String LAST_UPDATE = "last_update";

	/**
	 * status update template properties
	 * 
	 * @author Sebastian Schlicht
	 * 
	 */
	public static class Templates {

		/**
		 * template identifier
		 */
		public static final String IDENTIFIER = "identifier";

		/**
		 * template version
		 */
		public static final String VERSION = "version";

		/**
		 * template's java code
		 */
		public static final String CODE = "code";

	}

}