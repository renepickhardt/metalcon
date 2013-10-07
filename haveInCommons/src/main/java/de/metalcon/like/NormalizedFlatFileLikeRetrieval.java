package de.metalcon.like;

import java.io.File;

import de.metalcon.haveInCommons.HaveInCommons;

/**
 * @author Jonas Kunze
 */
public class NormalizedFlatFileLikeRetrieval implements HaveInCommons {
	private int edgeNum = 0;

	private final String storageDir;

	public NormalizedFlatFileLikeRetrieval(final String storageDir) {
		this.storageDir = storageDir;

		File f = new File(storageDir);
		if (!f.exists()) {
			throw new RuntimeException("Unable to initialize "
					+ this.getClass().getName()
					+ " becourse the storage directory does not exist: '"
					+ storageDir + "'");
		}

		Node.initialize(storageDir);
	}

	@Override
	public long[] getCommonNodes(long uuid1, long uuid2) {
		Node f = Node.getNode(uuid1);
		if (f == null) {
			// System.err.println("Unknown Node uuid: " + uuid1);
			return new long[0];
		}
		f.freeMemory();

		return f.getCommonNodes(uuid2);
	}

	@Override
	public void putEdge(long from, long to) {
		Node f = Node.getNode(from);
		if (f == null) {
			f = Node.createNewNode(from);
		}

		Node t = Node.getNode(to);
		if (t == null) {
			t = Node.createNewNode(to);
		}

		f.addFriendship(t);
		f.addLike(new Like(t.getUUID(), edgeNum++, Like.FLAG_UPVOTE));
	}

	@Override
	public boolean deleteEdge(long from, long to) {
		// TODO Auto-generated method stub
		return false;
	}
}
