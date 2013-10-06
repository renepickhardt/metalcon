/**
 *
 */
package de.metalcon.like;

import de.metalcon.haveInCommons.HaveInCommons;

/**
 * @author Jonas Kunze
 */
public class NormalizedFlatFileLikeRetrieval implements HaveInCommons {
	private int edgeNum = 0;

	public NormalizedFlatFileLikeRetrieval() {

	}

	@Override
	public long[] getCommonNodes(long uuid1, long uuid2) {
		Node f = Node.createNode(uuid1);
		Node t = Node.createNode(uuid2);

		return f.getCommonNodes(uuid2);
	}

	@Override
	public void putEdge(long from, long to) {
		Node f = Node.createNode(from);
		Node t = Node.createNode(to);

		f.addFriendship(t);
		f.addLike(new Like(t.getUUID(), edgeNum++, Like.FLAG_UPVOTE));
	}

	@Override
	public boolean deleteEdge(long from, long to) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void flush() {
		// TODO Auto-generated method stub

	}
}
