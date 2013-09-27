package de.metalcon.imageServer.protocol;

public class ProtocolTestConstants {

	public static final String VALID_IMAGE_METADATA = "{\"key\":\"value\"}";
	public static final String MALFORMED_IMAGE_METADATA = "thisisnotjson";
	public static final String VALID_SCALING_HEIGHT = "100";
	public static final String VALID_SCALING_WIDTH = "100";
	public static final String INVALID_SCALING_HEIGHT = "-100";
	public static final String INVALID_SCALING_WIDTH = "-100";
	public static final String MALFORMED_SCALING_VALUE = "NotANumber";
}