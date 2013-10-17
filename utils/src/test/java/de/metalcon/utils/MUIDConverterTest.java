/**
 * 
 */
package de.metalcon.utils;

import static org.junit.Assert.fail;

import java.util.Random;

import org.junit.Test;

/**
 * @author Jonas Kunze
 * 
 */
public class MUIDConverterTest {

	@Test
	public void performanceTest() {
		for (long i = 0; i < 15; i += 1) {
			MUID muid = new MUID((short) i, (byte) 2, 3, (short) 4);
			System.out.println(i + " : " + muid.toBinaryString() + " : "
					+ muid.toString());
		}

		System.exit(1);
		for (long i = -100; i < 100; i += 10) {
			String serialized = MUIDConverter.serialize(i);
			long deserialized = MUIDConverter.deserialize(serialized);
			System.out.println(i + " : " + serialized + " : " + deserialized
					+ " : " + serialized.length());
		}

		// for (long i = Long.MIN_VALUE; i < Long.MAX_VALUE; i += Long.MAX_VALUE
		// / 100) {
		// String serialized = UUIDConverter.serialize(i);
		// long deserialized = UUIDConverter.deserialize(serialized);
		// System.out.println(i + " : " + serialized + " : " +
		// deserialized+" : "+serialized.length());
		// }

		long runs = (long) 1E6;

		Random rand = new Random();
		long start = System.nanoTime();
		for (long l = runs; l != 0; l--) {
			String s = Long.toString(rand.nextLong(), 36);
		}
		long time = System.nanoTime() - start;
		System.out.println("Serialization: " + time / runs + " ns");

		// start = System.nanoTime();
		// for (long l = runs; l != 0; l--) {
		// String s = new BigInteger(130, random).toString(32);
		// }
		// long time = System.nanoTime() - start;
		// System.out.println("Serialization: " + time / runs + " ns");
	}

	/**
	 * Test method for {@link de.metalcon.utils.MUIDConverter#serialize(long)}.
	 */
	@Test
	public void smokeTest() {
		long runs = (long) 1E2;
		Random rand = new Random();
		for (long l = runs; l != 0; l--) {
			long value = rand.nextLong();
			String s = MUIDConverter.serialize(value);
			if (s.length() != MUIDConverter.getMUIDLength()) {
				fail("UUID with wrong String length generated: " + value + "->"
						+ s);
			}
			long deserialized = MUIDConverter.deserialize(s);
			if (value != deserialized) {
				fail(value + " has been converted to " + s
						+ " but s is deserialized to " + deserialized);
			}
		}
	}
}
