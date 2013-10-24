package de.metalcon.like;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import de.metalcon.haveInCommons.HaveInCommons;
import de.metalcon.like.Like.Vote;

/**
 * @author Jonas Kunze
 */
public class NormalizedLikeRetrieval implements HaveInCommons {
	private int edgeNum = 0;

	public NormalizedLikeRetrieval(final String storageDir) {
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
	public long[] getCommonNodes(final long uuid1, final long uuid2) {
		Node f = NodeFactory.getNode(uuid1);
		if (f == null) {
			// System.err.println("Unknown Node uuid: " + uuid1);
			return new long[0];
		}
		// f.freeMemory();

		return f.getCommonNodes(uuid2);
	}

	@Override
	public void putEdge(final long from, final long to, final Vote vote) {
		try {
			Node f = NodeFactory.getNode(from);
			if (f == null) {
				f = NodeFactory.createNewNode(from);
			}

			Node t = NodeFactory.getNode(to);
			if (t == null) {
				t = NodeFactory.createNewNode(to);
			}

			/*
			 * TODO: Here we need to pass the current timestamp instead of
			 * edgeNum++
			 */
			f.addLike(new Like(t.getUUID(), edgeNum++, vote));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Delete the friendship between from and to
	 */
	@Override
	public void deleteEdge(final long from, final long to) {
		final Node f = NodeFactory.getNode(from);
		if (f == null) {
			return;
		}

		Node t = NodeFactory.getNode(to);
		if (t == null) {
			return;
		}

		f.removeFriendship(t);
	}

	/**
	 * Returns a list of MUIDs of nodes liking the node with the MUID 'nodeMUID'
	 * or null if the requested node does not exist
	 * 
	 * @param nodeMUID
	 *            The requested node
	 * @return The list of nodes liking the node with the given MUID
	 */
	public long[] getLikedInNodes(final long nodeMUID) {
		final Node n = NodeFactory.getNode(nodeMUID);
		if (n == null) {
			return null;
		}
		return n.getLikeInNodes();
	}

	/**
	 * Returns a list of MUIDs of nodes liked by the node with the MUID
	 * 'nodeMUID' or null if the requested node does not exist
	 * 
	 * @param nodeMUID
	 *            The requested node
	 * @return The list of nodes liked by the node with the given MUID
	 */
	public long[] getLikedOutNodes(final long nodeMUID) {
		final Node n = NodeFactory.getNode(nodeMUID);
		if (n == null) {
			return null;
		}
		return n.getOutNodes(Vote.UP);
	}

	/**
	 * Returns a list of MUIDs of nodes disliking the node with the MUID
	 * 'nodeMUID' or null if the requested node does not exist
	 * 
	 * @param nodeMUID
	 *            The requested node
	 * @return The list of nodes disliking the node with the given MUID
	 */
	public long[] getDislikedInNodes(final long nodeMUID) {
		final Node n = NodeFactory.getNode(nodeMUID);
		if (n == null) {
			return null;
		}
		return n.getDislikeInNodes();
	}

	/**
	 * Returns a list of MUIDs of nodes disliked by the node with the MUID
	 * 'nodeMUID' or null if the requested node does not exist
	 * 
	 * @param nodeMUID
	 *            The requested node
	 * @return The list of nodes liked by the node with the given MUID
	 */
	public long[] getDislikedOutNodes(final long nodeMUID) {
		final Node n = NodeFactory.getNode(nodeMUID);
		if (n == null) {
			return null;
		}
		return n.getOutNodes(Vote.DOWN);
	}

	/**
	 * Returns a list of MUIDs of nodes liked by nodes liked by the node
	 * associated with the given nodeMUID
	 * 
	 * @param nodeMUID
	 *            The requested node
	 * @return The list of nodes liked by any node liked by the node with the
	 *         given MUID
	 */
	public long[] getLikedLikes(final long nodeMUID) {
		final Node n = NodeFactory.getNode(nodeMUID);
		if (n == null) {
			return null;
		}

		HashSet<Long> likedLikedNodes = new HashSet<Long>();

		/*
		 * Iterate through all nodes liked by n
		 */
		for (long likedMUID : n.getOutNodes(Vote.UP)) {
			if (likedMUID == 0) {
				break;
			}

			/*
			 * Iterate through all nodes liked by likedNode and add those liked
			 * nodes to the set
			 */
			final Node likedNode = NodeFactory.getNode(likedMUID);
			for (long likedlikedMUID : likedNode.getOutNodes(Vote.UP)) {
				if (likedlikedMUID == 0) {
					break;
				}
				likedLikedNodes.add(likedlikedMUID);
			}
		}
		long[] result = new long[likedLikedNodes.size()];
		int i = 0;
		for (long l : likedLikedNodes) {
			result[i++] = l;
		}
		return result;
	}
}
