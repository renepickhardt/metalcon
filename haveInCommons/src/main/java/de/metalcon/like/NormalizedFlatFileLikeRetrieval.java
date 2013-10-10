package de.metalcon.like;

import java.io.File;
import java.io.IOException;

import de.metalcon.haveInCommons.HaveInCommons;

/**
 * @author Jonas Kunze
 */
public class NormalizedFlatFileLikeRetrieval implements HaveInCommons {
	private int edgeNum = 0;

	public NormalizedFlatFileLikeRetrieval(final String storageDir) {
		File f = new File(storageDir);
		if (!f.exists()) {
			throw new RuntimeException("Unable to initialize "
					+ this.getClass().getName()
					+ " because the storage directory does not exist: '"
					+ storageDir + "'");
		}
		NodeFactory.initialize(storageDir);
	}

	@Override
	public long[] getCommonNodes(long uuid1, long uuid2) {
		Node f = NodeFactory.getNode(uuid1);
		if (f == null) {
			// System.err.println("Unknown Node uuid: " + uuid1);
			return new long[0];
		}
		// f.freeMemory();

		return f.getCommonNodes(uuid2);
	}

	@Override
	public void putEdge(long from, long to) {
		try {
			Node f = NodeFactory.getNode(from);
			if (f == null) {
				f = NodeFactory.createNewNode(from);
			}

			Node t = NodeFactory.getNode(to);
			if (t == null) {
				t = NodeFactory.createNewNode(to);
			}

			f.addFriendship(t);
			f.addLike(new Like(t.getUUID(), edgeNum++, Like.FLAG_UPVOTE));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public boolean deleteEdge(long from, long to) {
		// TODO Auto-generated method stub
		return false;
	}
}
