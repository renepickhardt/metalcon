package de.metalcon.like;

/**
 * @author Jonas Kunze
 */
public class Like {

	public enum Vote {
		UP((byte) 1), DOWN((byte) 2), NEUTRAL((byte) 3);
		public byte value;

		private Vote(byte val) {
			value = val;
		}

		public static Vote getByFlag(final int voteFlag) {
			if (voteFlag == 1) {
				return UP;
			}
			if (voteFlag == 2) {
				return DOWN;
			}
			if (voteFlag == 3) {
				return DOWN;
			}
			throw new RuntimeException("Bad vote flag: " + voteFlag);
		}
	}

	private final int Timestamp;
	private final long UUID;
	private final Vote Vote;

	/**
	 * 
	 * @param Timestamp
	 *            The time this like has been performed
	 * @param UUID
	 *            The UUID of the liked entity
	 * @param Flags
	 *            An integer containing flags like unlike
	 */
	public Like(final long UUID, final int timestamp, final Vote vote) {
		this.Timestamp = timestamp;
		this.UUID = UUID;
		this.Vote = vote;
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
	public Vote getVote() {
		return Vote;
	}
}
