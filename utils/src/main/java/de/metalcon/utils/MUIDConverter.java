package de.metalcon.utils;

import java.math.BigInteger;
import java.nio.ByteBuffer;

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
public class MUIDConverter {
	private final static char[] tokens = { 'a', 'b', 'c', 'd', 'e', 'f', 'g',
			'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
			'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9' };
	private final static int RADIX = tokens.length;
	private final static short MUID_LENGTH = 13;

	private final static int[] reverseTokens;
	static {
		reverseTokens = new int[256];
		for (int i = 0; i != reverseTokens.length; ++i) {
			reverseTokens[i] = -1;
		}
		for (int i = 0; i != RADIX; ++i) {
			reverseTokens[(int) (tokens[i])] = i;
		}
	}

	public static final String serialize(long uuid) {
		// StringBuilder string = new StringBuilder(13);
		// for (int i = 0; i != MUID_LENGTH; ++i) {
		// int rest = (int) (uuid % RADIX);
		// string.append(tokens[rest]);
		// uuid = uuid / RADIX;
		// Long l;
		// }
		// return string.toString();

		// int bottomDigit = (int) (((uuid >>> 1) % (RADIX / 2)) << 1)
		// | ((int) uuid & 1);
		// long rest = (uuid >>> 1) / (RADIX / 2);
		// if (rest == 0) {
		// return serialize(bottomDigit);
		// }
		// return Long.toString(rest, RADIX) + serialize(bottomDigit);

		byte[] bytes = ByteBuffer.allocate(8).putLong(uuid).array();
		return new BigInteger(1, bytes).toString(RADIX);
	}

	/**
	 * @param idString
	 * @return
	 */
	public static final long deserialize(final String idString) {
		// if (idString.length() != MUID_LENGTH) {
		// throw new RuntimeException("Malformed MUID: " + idString);
		// }
		// long tmp = 0;
		// for (int i = idString.length() - 1; i != -1; --i) {
		// tmp *= RADIX;
		// char c = idString.charAt(i);
		// if (reverseTokens[(int) c] == -1) {
		// throw new NumberFormatException("Bad character in UUID: " + c);
		// }
		// tmp += reverseTokens[(int) c];
		// }
		// return tmp;
		return new BigInteger(idString, RADIX).longValue();
	}

	/**
	 * Returns all characters a metalcon UUID may consist of
	 * 
	 * @return All allowed characters
	 */
	public static char[] getAllowedTokens() {
		return tokens;
	}

	public static short getMUIDLength() {
		return MUID_LENGTH;
	}
}