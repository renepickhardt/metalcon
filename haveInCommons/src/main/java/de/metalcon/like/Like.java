package de.metalcon.like;

/**
 * @author Jonas Kunze
 */
public class Like {
	public static final int FLAG_UNLIKE = 1;

	private final int Timestamp;
	private final long UUID;
	private final int Flags;

	/**
	 * 
	 * @param Timestamp
	 *            The time this like has been performed
	 * @param UUID
	 *            The UUID of the liked entity
	 * @param Flags
	 *            An integer containing flags like unlike
	 */
	public Like(final int timestamp, final long UUID, final int flags) {
		this.Timestamp = timestamp;
		this.UUID = UUID;
		this.Flags = flags;
	}

	/**
	 * 
	 * @return Returns true if this
	 */
	public boolean isUnlike() {
		return (Flags & FLAG_UNLIKE) == FLAG_UNLIKE;
	}

	/**
	 * 
	 * @return The time this like has been performed
	 */
	public int getTimestamp() {
		return Timestamp;
	}

	/**
	 * 
	 * @return The UUID of the liked entity
	 */
	public long getUUID() {
		return UUID;
	}

	/**
	 * 
	 * @return An integer containing flags like unlike
	 */
	public int getFlags() {
		return Flags;
	}
}
