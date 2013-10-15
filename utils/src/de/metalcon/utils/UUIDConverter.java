package de.metalcon.utils;

/**
 * This class can serialize and deserialize 64 bit (8 Byte) uuids in alphanumerical strings
 * The class stores the 64 byte string but can also be initialized from a String
 * 
 * This code ist GPLv3
 */

/**
 * @author Rene Pickhardt
 * 
 */
public class UUIDConverter {
	private final static char[] tokens = { 'a', 'b', 'c', 'd', 'e', 'f', 'g',
			'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
			'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9' };
	private final static int MAX_TOKEN = 35;

	private final static int[] reverseTokens;
	static {
		reverseTokens = new int[256];
		for (int i = 0; i != 256; ++i) {
			reverseTokens[i] = -1;
		}
		for (int i = 0; i != MAX_TOKEN; ++i) {
			reverseTokens[(int) (tokens[i])] = i;
		}
	}

	public static String serialize(long uuid) {
		StringBuilder string = new StringBuilder(13);
		for (int i = 0; i != 13; ++i) {
			int rest = (int) (uuid % MAX_TOKEN);
			string.append(tokens[rest]);
			uuid = uuid / MAX_TOKEN;
		}
		return string.toString();
	}

	/**
	 * @param idString
	 * @return
	 */
	public static long deserialize(final String idString) {
		long tmp = 0;
		for (int i = idString.length() - 1; i >= 0; --i) {
			tmp *= MAX_TOKEN;
			char c = idString.charAt(i);
			if (reverseTokens[(int) c] == -1) {
				throw new NumberFormatException();
			}
			tmp += reverseTokens[(int) c];
		}
		return tmp;
	}
}