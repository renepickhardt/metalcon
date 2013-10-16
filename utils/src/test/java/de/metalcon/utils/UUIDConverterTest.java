/**
 * 
 */
package de.metalcon.utils;

import static org.junit.Assert.fail;

import java.util.Random;

import org.junit.Test;

/**
 * @author kunzejo
 * 
 */
public class UUIDConverterTest {

	@Test
	public void performanceTest() {
		for (long i = -100; i < 100; i += 10) {
			String serialized = UUIDConverter.serialize(i);
			long deserialized = UUIDConverter.deserialize(serialized);
			System.out.println(i + " : " + serialized + " : " + deserialized+" : "+serialized.length());
		}
		
//		for (long i = Long.MIN_VALUE; i < Long.MAX_VALUE; i += Long.MAX_VALUE / 100) {
//			String serialized = UUIDConverter.serialize(i);
//			long deserialized = UUIDConverter.deserialize(serialized);
//			System.out.println(i + " : " + serialized + " : " + deserialized+" : "+serialized.length());
//		}

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
	 * Test method for {@link de.metalcon.utils.UUIDConverter#serialize(long)}.
	 */
	@Test
	public void smokeTest() {
		long runs = (long) 1E2;
		Random rand = new Random();
		for (long l = runs; l != 0; l--) {
			long value = rand.nextLong();
			String s = UUIDConverter.serialize(value);
			if (s.length() != UUIDConverter.getMUIDLength()) {
				fail("UUID with wrong String length generated: " + value + "->"
						+ s);
			}
			long deserialized = UUIDConverter.deserialize(s);
			if (value != deserialized) {
				fail(value + " has been converted to " + s
						+ " but s is deserialized to " + deserialized);
			}
		}
	}
}
