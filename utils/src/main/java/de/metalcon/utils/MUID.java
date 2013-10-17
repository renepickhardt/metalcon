package de.metalcon.utils;

/**
 * @author Jonas Kunze
 * 
 */
public class MUID {
	private final short type;
	private final byte source;
	private final int timestamp;
	private final short ID;

	private final long UUID;

	public MUID(final short type, final byte source, final int timestamp,
			final short ID) {
		this.type = type;
		this.source = source;
		this.timestamp = timestamp;
		this.ID = ID;
		this.UUID =
		/* First 10 bits are type */
		((long) type & 1023) << (64 - 10)
		/* Next bit is empty */
		/* Next 5 bits are source */
		| (source & 31) << (64 - 10 - 1 - 5);
	}

	@Override
	public String toString() {
		return MUIDConverter.serialize(UUID);
	}

	public String toBinaryString() {
		return Long.toBinaryString(UUID);
	}
}
