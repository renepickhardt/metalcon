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
	private static Random rand = new Random();

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

		/*
		 * Measure time to create random long
		 */
		start = System.nanoTime();
		for (long l = runs; l != 0; l--) {
			rand.nextLong();
		}
		long randLongTime = (System.nanoTime() - start) / runs;

		/*
		 * Measure time to create 4 random ints as we'll use them for
		 * MUIDConverter.getMUID
		 */
		start = System.nanoTime();
		for (long l = runs; l != 0; l--) {
			rand.nextInt(1 << 9);
			rand.nextInt(1 << 5);
			rand.nextInt();
			rand.nextInt();
		}
		long rand4IntTime = (System.nanoTime() - start) / runs;

		/*
		 * Serialization time
		 */
		start = System.nanoTime();
		for (long l = runs; l != 0; l--) {
			MUIDConverter.serialize(rand.nextLong());
		}
		long serializationTime = (System.nanoTime() - start) / runs
				- randLongTime;
		System.out.println("Serialization: " + serializationTime + " ns");

		/*
		 * Deserialization time
		 */
		start = System.nanoTime();
		for (long l = runs; l != 0; l--) {
			String s = MUIDConverter.serialize(rand.nextLong());
			MUIDConverter.deserialize(s);
		}
		long time = (System.nanoTime() - start) / runs - serializationTime;
		System.out.println("Deserialization: " + time + " ns");

		/*
		 * MUIDConverter.getMUID time
		 */
		start = System.nanoTime();
		for (long l = runs; l != 0; l--) {
			MUIDConverter.generateMUID((short) rand.nextInt(1 << 9),
					(byte) rand.nextInt(1 << 5), rand.nextInt(),
					(short) rand.nextInt());
		}
		long getMUIDTime = (System.nanoTime() - start) / runs;
		System.out.println("Generation: " + (getMUIDTime - rand4IntTime)
				+ " ns");

		/*
		 * MUIDConverter.getType time
		 */
		start = System.nanoTime();
		for (long l = runs; l != 0; l--) {
			long uuid = generateRandomMUID();
			MUIDConverter.getType(uuid);
		}

		time = (System.nanoTime() - start) / runs;
		System.out.println("getType: " + (time - getMUIDTime) + " ns");
	}

	/**
	 * Generates several random MUIDs and checks if the get methods like getType
	 * return the initially type used for the creation of the MUID
	 */
	@Test
	public void checkGetMethods() {
		Random rand = new Random();
		for (int i = 0; i != 10000; i++) {
			short oType = (short) (rand.nextInt() & 511);
			byte oSource = (byte) (rand.nextInt() & 31);
			int oTimestamp = rand.nextInt();
			short oID = (short) rand.nextInt();

			long uuid = MUIDConverter.generateMUID(oType, oSource, oTimestamp, oID);

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

	/**
	 * Generates several random MUIDs (long) and checks if the serialized
	 * version of the MUID get deserialized to the original long number
	 */
	@Test
	public void checkSerialization() {
		for (int i = 0; i != 10000; i++) {
			long uuid = generateRandomMUID();

			String alphanumeric = MUIDConverter.serialize(uuid);
			long deserialized = MUIDConverter.deserialize(alphanumeric);

			if (uuid != deserialized) {
				fail(uuid + " has been serialized to " + alphanumeric
						+ " and deserialized to " + deserialized);
			}
		}
	}

	@Test
	public void checkGetMUIDStoragePath() {
		for (int i = 1; i < 10000; i++) {
			long uuid = generateRandomMUID();
			String path = MUIDConverter.getMUIDStoragePath(uuid);
			if (path.length() != 6) {
				fail("Wrong MUID path length: " + path);
			}
			if (!path.matches("[0-9a-f]/[0-9a-f]/[0-9a-f]/")) {
				fail("Bad MUID path format: " + path);
			}
		}
	}

	/**
	 * Generates a new MUID with random values
	 * 
	 * @return The generated random MUID
	 */
	private long generateRandomMUID() {
		short oType = (short) (rand.nextInt() & 511);
		byte oSource = (byte) (rand.nextInt() & 31);
		int oTimestamp = rand.nextInt();
		short oID = (short) rand.nextInt();

		return MUIDConverter.generateMUID(oType, oSource, oTimestamp, oID);
	}
}
