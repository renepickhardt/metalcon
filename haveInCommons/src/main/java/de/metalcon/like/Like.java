package de.metalcon.like;

/**
 * @author Jonas Kunze
 */
public class Like {
	public static final int FLAG_REMOVE_VOTE = 1;
	public static final int FLAG_UPVOTE = 2;
	public static final int FLAG_DOWN_VOTE = 4;

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
	public Like(final long UUID, final int timestamp, final int flags) {
		this.Timestamp = timestamp;
		this.UUID = UUID;
		this.Flags = flags;

		int flagNum = 1 & flags + 1 & flags >> 1 + 1 & flags >> 2;
		assert (flagNum > 0);
	}

	/**
	 * 
	 * @return Returns true if this is an unlike vote
	 */
	public boolean isRemoveVote() {
		return (Flags & FLAG_REMOVE_VOTE) == FLAG_REMOVE_VOTE;
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
