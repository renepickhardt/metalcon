package de.metalcon.imageServer;

import java.io.InputStream;

public class ProtocolTestConstants {

	// TODO: insert usable constants
	// public static final String VALID_IMAGE = null;

	public static InputStream VALID_IMAGESTREAM;
	public static InputStream INVALID_IMAGESTREAM = null;

	public final static String VALID_IMAGE_METADATA = "{\"key\":\"value\"}";
	public final static String INVALID_IMAGE_METADATA = "thisisnotjson";
	// Usable constants below
	public final static String VALID_BOOLEAN_AUTOROTATE_TRUE = "1";
	public final static String VALID_BOOLEAN_AUTOROTATE_FALSE = "0";
	public final static String INVALID_BOOLEAN_AUTOROTATE = "wrong";
	public static final String VALID_CROPPING_LEFT_COORDINATE = "10";
	public static final String VALID_CROPPING_TOP_COORDINATE = "10";
	public static final String VALID_CROPPING_WIDTH_COORDINATE = "50";
	public static final String VALID_CROPPING_HEIGHT_COORDINATE = "50";
}
