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
		long runs = (long) 1E6;

		Random rand = new Random();

		/*
		 * CPU warmup
		 */
		long start = System.nanoTime();
		for (long l = runs; l != 0; l--) {
			rand.nextLong();
		}

		start = System.nanoTime();
		for (long l = runs; l != 0; l--) {
			rand.nextLong();
		}
		long randLongTime = (System.nanoTime() - start) / runs;

		start = System.nanoTime();
		for (long l = runs; l != 0; l--) {
			rand.nextInt(1 << 9);
			rand.nextInt(1 << 5);
			rand.nextInt();
			rand.nextInt();
		}
		long rand4IntTime = (System.nanoTime() - start) / runs;

		start = System.nanoTime();
		for (long l = runs; l != 0; l--) {
			String s = MUIDConverter.serialize(rand.nextLong());
		}
		long time = (System.nanoTime() - start) / runs - randLongTime;
		System.out.println("Serialization: " + time + " ns");

		start = System.nanoTime();
		for (long l = runs; l != 0; l--) {
			MUIDConverter.getMUID((short) rand.nextInt(1 << 9),
					(byte) rand.nextInt(1 << 5), rand.nextInt(),
					(short) rand.nextInt());
		}
		long getMUIDTime = (System.nanoTime() - start) / runs;
		System.out.println("Generation: " + (getMUIDTime - rand4IntTime)
				+ " ns");

		start = System.nanoTime();
		for (long l = runs; l != 0; l--) {
			long uuid = MUIDConverter.getMUID((short) rand.nextInt(1 << 9),
					(byte) rand.nextInt(1 << 5), rand.nextInt(),
					(short) rand.nextInt());
			MUIDConverter.getType(uuid);
		}

		time = (System.nanoTime() - start) / runs;
		System.out.println("getType: " + (time - getMUIDTime) + " ns");
	}

	@Test
	public void smokeTest() {
		Random rand = new Random();
		for (int i = 0; i != 10000; i++) {
			short oType = (short) (rand.nextInt() & 511);
			byte oSource = (byte) (rand.nextInt() & 31);
			int oTimestamp = rand.nextInt();
			short oID = (short) rand.nextInt();

			long uuid = MUIDConverter.getMUID(oType, oSource, oTimestamp, oID);

			short type = MUIDConverter.getType(uuid);
			byte source = MUIDConverter.getSource(uuid);
			int timestamp = MUIDConverter.getTimestamp(uuid);
			short id = MUIDConverter.getID(uuid);

			if (type != oType) {
				fail("MUIDConverter.getType(" + uuid + ") returned " + type
						+ " instead of " + oType);
			}

			if (source != oSource) {
				fail("MUIDConverter.getSource(" + uuid + ") returned " + source
						+ " instead of " + oSource);
			}

			if (timestamp != oTimestamp) {
				fail("MUIDConverter.getTimestamp(" + uuid + ") returned "
						+ timestamp + " instead of " + oTimestamp);
			}

			if (id != oID) {
				fail("MUIDConverter.getID(" + uuid + ") returned " + id
						+ " instead of " + oID);
			}

			String s = MUIDConverter.serialize(uuid);
			if (s.length() != MUIDConverter.getMUIDLength()) {
				fail("UUID with wrong String length generated: " + uuid + "->"
						+ s);
			}

			long deserialized = MUIDConverter.deserialize(s);
			if (uuid != deserialized) {
				fail(uuid + " has been converted to " + s
						+ " but s is deserialized to " + deserialized);
			}
		}
	}
}
