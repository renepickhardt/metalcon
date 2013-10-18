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
	private final static char[] tokens = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
			'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
			'x', 'y', 'z' };
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

	public static long getMUID(final short type, final byte source,
			final int timestamp, final short ID) {
		if (type >= (1 << 9)) {
			throw new RuntimeException("Type may not be larger or equal to "
					+ (1 << 9));
		}
		if (source >= (1 << 5)) {
			throw new RuntimeException("Source may not be larger or equal to "
					+ (1 << 5));
		}

		return
		/* Highest bit is 1 for constant length */
		1l << (64 - 1)
		/* Highest 10 bits are type */
		| (((long) type & 511) << (64 - 9 - 1))
		/*
		 * Next bit is empty so that the first two alphanumerics only depend on
		 * type and not also source
		 */
		/* Next 5 bits are source */
		| (((long) source & 31) << (64 - 1 - 9 - 1 - 5))
		/* Next 4 bytes are TS */
		| (((long) timestamp & 0xFFFFFFFFL) << (64 - 1 - 9 - 1 - 5 - 32))
		/* Next 2 bytes are ID */
		| (short) ID & 0xFFFFL;
	}

	public static final short getType(final long uuid) {
		return (short) ((uuid >>> (64 - 10)) & 511);
	}

	public static final byte getSource(final long uuid) {
		return (byte) ((uuid >>> (64 - 1 - 9 - 1 - 5)) & 31);
	}

	public static final int getTimestamp(final long uuid) {
		return (int) ((uuid >>> (64 - 1 - 9 - 1 - 5 - 32)) & 0xFFFFFFFFL);
	}

	public static final short getID(final long uuid) {
		return (short) (uuid & 0xFFFFL);
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

		/*
		 * Do not use Long.toString to interpret the uuid as unsigned long
		 */
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