package de.metalcon.imageServer;

public class ProtocolTestConstants {

	public final static String VALID_IMAGE_IDENTIFIER = "ii1";
	public final static String VALID_IMAGE_METADATA = "{\"key\":\"value\"}";
	public final static String INVALID_IMAGE_METADATA = "thisisnotjson";
	public final static String VALID_BOOLEAN_AUTOROTATE_TRUE = "1";
	public final static String VALID_BOOLEAN_AUTOROTATE_FALSE = "0";
	public final static String MALFORMED_BOOLEAN_AUTOROTATE = "wrong";
	public static final String VALID_CROPPING_LEFT_COORDINATE = "10";
	public static final String VALID_CROPPING_TOP_COORDINATE = "10";
	public static final String VALID_CROPPING_WIDTH_COORDINATE = "50";
	public static final String VALID_CROPPING_HEIGHT_COORDINATE = "50";
	public static final String INVALID_CROPPING_LEFT_COORDINATE = "-10";
	public static final String INVALID_CROPPING_TOP_COORDINATE = "-10";
	public static final String INVALID_CROPPING_WIDTH_COORDINATE = "-50";
	public static final String INVALID_CROPPING_HEIGHT_COORDINATE = "-50";
	public static final String MALFORMED_CROPPING_LEFT_COORDINATE = "NotANumber";
	public static final String MALFORMED_CROPPING_TOP_COORDINATE = "NotANumber";
	public static final String MALFORMED_CROPPING_WIDTH_COORDINATE = "NotANumber";
	public static final String MALFORMED_CROPPING_HEIGHT_COORDINATE = "NotANumber";
	public static final String MALFORMED_IDENTIFIER_LIST = "test,,test";
	public static final String VALID_SCALING_HEIGHT = "100";
	public static final String VALID_SCALING_WIDTH = "100";
	public static final String MALFORMED_SCALING_HEIGHT = "NotANumber";
	public static final String MALFORMED_SCALING_WIDTH = "NotANumber";
	public static final String VALID_IMAGE_IDENTIFIER_LIST = "ii1, ii2, ii3";
}
