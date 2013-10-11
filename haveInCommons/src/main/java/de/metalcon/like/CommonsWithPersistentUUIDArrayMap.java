package de.metalcon.like;

import java.io.File;
import java.io.IOException;

import de.metalcon.utils.PersistentUUIDArrayMap;

/**
 * @author Jonas Kunze
 */
class CommonsWithPersistentUUIDArrayMap {
	private final Node node;
	private final String persistentFileName;
	// private LazyPersistentUUIDMap persistentcommonsMap = null;
	private PersistentUUIDArrayMap persistentcommonsMap = null;

	private boolean mayFreeMem = true;

	/**
	 * 
	 * @param persistentFileName
	 *            The path to the persistent commons file
	 */
	public CommonsWithPersistentUUIDArrayMap(final Node node,
			final String storageDir) {
		this.node = node;
		this.persistentFileName = storageDir + "/" + node.getUUID()
				+ "_commons";
	}

	/**
	 * Delete the corresponding file
	 */
	void delete() {
		File file = new File(persistentFileName);
		file.renameTo(new File(persistentFileName + ".deleted"));

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
		if (persistentcommonsMap == null) {
			readFile();
		}
		long[] commons = persistentcommonsMap.get(uuid);
		persistentcommonsMap.closeFileIfNecessary();

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
	 * Reads the persistent commons file
	 * 
	 */
	private void readFile() {
		try {
			persistentcommonsMap = new PersistentUUIDArrayMap(
					persistentFileName);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Frees the cached data. The next getCommonNodes call will therefore
	 * trigger a disc access
	 */
	public void freeMemory() {
		while (!mayFreeMem) { // Wait until all reads/writes have been performed
			try {
				Thread.sleep(1000); // sleep one second
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		persistentcommonsMap.closeFile();
		persistentcommonsMap = null;
	}

	/**
	 * Updates the commons of node and writes the data to disk
	 */
	public void update() {
		mayFreeMem = false;
		final int now = (int) (System.currentTimeMillis() / 1000l);

		for (long friendUUID : node.getFriends()) {
			if (friendUUID == node.getUUID()) {
				continue;
			}
			updateFriend(NodeFactory.getNode(friendUUID), false);
		}

		persistentcommonsMap.setUpdateTimeStamp(now);
		mayFreeMem = true;
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
			 * Find the list of commons with the entity the friend liked
			 */
			long[] commons = persistentcommonsMap.get(like.getUUID());
			if (commons == null) {
				continue;
			}

			/*
			 * Remove the friend from the commons list of the liked entity
			 */
			persistentcommonsMap.remove(like.getUUID(), friend.getUUID());
			// removeFromCommonsList(commons, friend.getUUID()));
		}
		persistentcommonsMap.closeFileIfNecessary();
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
	private void updateFriend(Node friend, boolean ignoreTimestamp) {
		if (persistentcommonsMap == null) {
			readFile();
		}

		mayFreeMem = false;

		int searchTS = ignoreTimestamp ? 0 : persistentcommonsMap
				.getLastUpdateTimeStamp();
		for (Like like : friend.getLikesFromTimeOn(searchTS)) {
			if (like.getUUID() == node.getUUID()) {
				continue;
			}
			persistentcommonsMap.append(like.getUUID(), friend.getUUID());
		}
		persistentcommonsMap.closeFileIfNecessary();
		mayFreeMem = true;
	}
}
