package de.metalcon.utils;

import java.math.BigInteger;
import java.nio.ByteBuffer;

/**
 * This class defines the 64 bit MUIDS and can generate, serialize and deserialize them 
 * 
 * This code is GPLv3
 */

/**
 * @author Rene Pickhardt
 * @author Jonas Kunze
 * 
 */
public class MUIDConverter {
	private final static char[] folderChars = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	private final static int RADIX = folderChars.length;
	private final static short MUID_LENGTH = 13;

	/**
	 * Generates a MUID containing the given information. The format of the MUID
	 * is following (in big endianess):
	 * 
	 * Bit 0 is always 1 to force constant length of the alphanumeric version
	 * 
	 * Bits 1-9 define the type
	 * 
	 * Bit 10 is always 0 to enforce the first two tokens in the alphanumeric
	 * version to be equal for each MUID with the same type
	 * 
	 * Bits 11-15 define the Source
	 * 
	 * Bits 16-47 define the timestamp
	 * 
	 * Bits 48-63 define the ID
	 * 
	 * @param type
	 *            The type of the MUID to be created
	 * @param source
	 *            The source of the creator of the MUID (the node running this
	 *            code)
	 * @param timestamp
	 *            The timestamp to be stored in the MUID (creation time)
	 * @param ID
	 *            The relative ID within the given timestamp, source and type.
	 * @return The MUID containing all the given information
	 */
	public static long generateMUID(final short type, final byte source,
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
		/* Highest 9 bits are type */
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

	/**
	 * Returns the type stored within the given MUID
	 * 
	 * @param muid
	 *            The MUID storing the type searched for
	 * @return The type within the given muid
	 */
	public static short getType(final long muid) {
		return (short) ((muid >>> (64 - 9 - 1)) & 511);
	}

	/**
	 * Returns the source that generated the given MUID
	 * 
	 * @param muid
	 *            The MUID storing the source searched for
	 * @return The source that created the given MUID
	 */
	public static byte getSource(final long muid) {
		return (byte) ((muid >>> (64 - 1 - 9 - 1 - 5)) & 31);
	}

	/**
	 * Returns the timestamp the given MUID has been created
	 * 
	 * @param muid
	 *            The MUID storing the timestamp searched for
	 * @return The timestamp the given MUID has been created at
	 */
	public static int getTimestamp(final long muid) {
		return (int) ((muid >>> (64 - 1 - 9 - 1 - 5 - 32)) & 0xFFFFFFFFL);
	}

	/**
	 * Returns the ID stored within the given MUID
	 * 
	 * @param muid
	 *            The MUID storing the ID searched for
	 * @return The ID within the given muid
	 */
	public static short getID(final long muid) {
		return (short) (muid & 0xFFFFL);
	}

	/**
	 * Parses the given MUID to an alphanumeric string
	 * 
	 * @param muid
	 *            The MUID to be parsed
	 * @return The alphanumeric string corresponding to the given MUID
	 */
	public static String serialize(final long muid) {
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
		byte[] bytes = ByteBuffer.allocate(8).putLong(muid).array();
		return new BigInteger(1, bytes).toString(RADIX);
	}

	/**
	 * Parses the given MUID in alphanumeric string format to it's corresponding
	 * long version
	 * 
	 * @param idString
	 *            The alphanumeric string describing the MUID to be parsed
	 * @return The MUID in it's long format
	 */
	public static long deserialize(final String idString) {
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
	 * Returns all characters a MUID path may consist of
	 * 
	 * @return All allowed characters
	 */
	public static char[] getAllowedFolderNames() {
		return folderChars;
	}

	/**
	 * 
	 * @return The number for characters an alphanumeric version of any MUID
	 *         consist of
	 */
	public static short getMUIDLength() {
		return MUID_LENGTH;
	}

	/**
	 * Returns the path corresponding to the given muid. The path consist of 3
	 * folders with one hex character as name (0-f). The letters are generated
	 * from 4 consecutive bits in the 32-bit hash of the MUID. The path will be
	 * in the format [0-9a-f]/[0-9a-f]/[0-9a-f]/
	 * 
	 * @param muid
	 *            The MUID to be taken for the path generation
	 * @return The path that should be used to store data coresponding to the
	 *         given MUID
	 */
	public static String getMUIDStoragePath(final long muid) {
		/*
		 * Split the muid into shorts and xor them
		 */
		short hash = (short) ((muid ^ (muid >>> 16)) ^ ((muid >>> 32) ^ (muid >>> 48)));
		char[] paths = new char[3];
		paths[0] = (char) ('a' + (hash & 15));
		paths[1] = (char) ('a' + ((hash >> 4) & 15));
		paths[2] = (char) ('a' + ((hash >> 8) & 15));

		return folderChars[(hash & 15)] + "/" + folderChars[((hash >> 4) & 15)]
				+ "/" + folderChars[((hash >> 8) & 15)] + "/";
	}
}