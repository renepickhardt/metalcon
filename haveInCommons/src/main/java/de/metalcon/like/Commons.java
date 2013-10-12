package de.metalcon.like;

import java.io.IOException;

import de.metalcon.utils.IPersistentUUIDArrayMap;
import de.metalcon.utils.LazyPersistentUUIDMap;
import de.metalcon.utils.PersistentUUIDArrayMap;
import de.metalcon.utils.PersistentUUIDArrayMapLevelDB;
import de.metalcon.utils.PersistentUUIDArrayMapRedis;

/**
 * @author Jonas Kunze
 */
class Commons {
	private final Node node;

	private IPersistentUUIDArrayMap persistentcommonsMap = null;

	/**
	 * 
	 * @param persistentFileName
	 *            The path to the persistent commons file
	 */
	public Commons(final Node node, final String storageDir, Class<?> c) {
		this.node = node;
		if (c == PersistentUUIDArrayMapRedis.class) {
			persistentcommonsMap = new PersistentUUIDArrayMapRedis(""
					+ node.getUUID());
		} else if (c == LazyPersistentUUIDMap.class) {
			persistentcommonsMap = LazyPersistentUUIDMap
					.getPersistentUUIDMap(storageDir + "/" + node.getUUID()
							+ "_commons");
		} else if (c == PersistentUUIDArrayMap.class) {
			try {
				persistentcommonsMap = new PersistentUUIDArrayMap(storageDir
						+ "/" + node.getUUID() + "_commons");
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		} else if (c == PersistentUUIDArrayMapLevelDB.class) {
			persistentcommonsMap = PersistentUUIDArrayMapLevelDB.generateMap(
					node.getUUID(), storageDir + "/levelDB");
		}
	}

	/**
	 * Remove all entries in the DB
	 */
	public void delete() {
		persistentcommonsMap.removeAll();
	}

	/**
	 * Reads the persistent PersistentCommonsFile from disk and returns all
	 * uuids that have the node uuid in common with this node
	 * 
	 * @param uuid
	 *            The entity the returned uuids have in common with this node
	 * @return The uuids of the nodes that have the entity uuid with the owner
	 *         of this Commons in common. The last uuids in the list may be 0
	 */
	public long[] getCommonNodes(long uuid) {
		long[] commons = persistentcommonsMap.get(uuid);

		if (commons != null) {
			/*
			 * Remove the trailing zeros
			 */
			int firstZero = -1;
			while (firstZero != commons.length - 1) {
				if (commons[++firstZero] == 0) {
					long[] tmp = new long[firstZero];
					System.arraycopy(commons, 0, tmp, 0, firstZero);
					return tmp;
				}
			}
		}
		return commons;
	}

	/**
	 * Frees the cached data. The next getCommonNodes call will therefore
	 * trigger a disc access
	 */
	public void freeMemory() {
	}

	/**
	 * Updates the commons of node and writes the data to disk
	 */
	public void update() {
		final int now = (int) (System.currentTimeMillis() / 1000l);

		for (long friendUUID : node.getFriends()) {
			updateFriend(NodeFactory.getNode(friendUUID), false);
		}

		for (long inNodeID : node.getInNodes()) {
			Node n = NodeFactory.getNode(inNodeID);
			n.getCommons().updateFriend(n, false);
		}

		persistentcommonsMap.setUpdateTimeStamp(now);
		persistentcommonsMap.save();
	}

	/**
	 * Adds the friend to all entities in the commonsMap liked by the friend
	 * node.
	 * 
	 * @param friend
	 *            The friend to be added to the commonsMap
	 */
	public void friendAdded(Node friend) {
		updateFriend(friend, true);
	}

	/**
	 * Removes the friend from all entities in the commonsMap liked by the
	 * friend node.
	 * 
	 * @param friend
	 *            The friend to be removed from the commonsMap
	 */
	public void friendRemoved(Node friend) {
		for (Like like : friend.getLikesFromTimeOn(0)) {
			/*
			 * Remove the friend from the commons list of the liked entity
			 */
			persistentcommonsMap.remove(like.getUUID(), friend.getUUID());
		}
		persistentcommonsMap.removeKey(friend.getUUID());
		persistentcommonsMap.save();
	}

	/**
	 * Adds the friend to all entities in the commonsMap liked by the friend
	 * node. If ignoreTimstamp is set to false only the entities liked by friend
	 * from the last update of this commons till now will be considered.
	 * 
	 * If a new friend is added to node you should call this method with this
	 * friend and ignoreTimestamp=false to add the friend to all entities liked
	 * by him
	 * 
	 * @param friend
	 *            The friend to be added to the HashMap
	 * @param ignoreTimestamp
	 *            If false only the entities liked by friend from the last
	 *            update of this commons till now will be considered.
	 */
	public void updateFriend(Node friend, boolean ignoreTimestamp) {
		int searchTS = ignoreTimestamp ? 0 : persistentcommonsMap
				.getLastUpdateTimeStamp();
		for (Like like : friend.getLikesFromTimeOn(searchTS)) {
			if (like.getUUID() == node.getUUID()) {
				continue;
			}
			persistentcommonsMap.append(like.getUUID(), friend.getUUID());
		}
		persistentcommonsMap.save();
	}
}
