package de.metalcon.like;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;

import de.metalcon.utils.Serialization;

/**
 * This is the structure of the raw persistent common file content
 */
class CommonsFileRaw implements Serializable {
	private static final long serialVersionUID = -4126346298168246709L;

	public CommonsFileRaw() {
	}

	public int lastUpdateTS;
	public HashMap<Long, long[]> commonsMap;
}

/**
 * @author Jonas Kunze
 */
class Commons {
	/*
	 * FIXME: optimize those two values
	 */
	private static final int InitialCommonListLength = 4;
	private static final float CommonListGrowthFactor = 1.2f;

	private final Node node;
	private final String persistentFileName;
	private CommonsFileRaw raw = null;

	private boolean mayFreeMem = true;

	/**
	 * 
	 * @param persistentFileName
	 *            The path to the persistent commons file
	 */
	public Commons(final Node node, final String storageDir) {
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
		if (raw == null) {
			readFile();
		}
		long[] commons = raw.commonsMap.get(uuid);

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
	 * Reads the persistent commons file and casts the content to a struct:
	 * 
	 * int TS, HashMap<Long, long[]>
	 * 
	 * @param fileName
	 *            The path to the persistent commons file
	 */
	private void readFile() {
		raw = (CommonsFileRaw) Serialization
				.readObjectFromFile(persistentFileName);
		if (raw == null) {
			raw = new CommonsFileRaw();
			raw.lastUpdateTS = 0;
			raw.commonsMap = new HashMap<Long, long[]>();
		}
	}

	/**
	 * Serializes the given commonsMap and writes it into the file with the
	 * given path
	 * 
	 * @param commonsMap
	 *            The Map to be serialized
	 * @param fileName
	 *            The path to the persistent commons file
	 * @return <code>true</code> in case of success
	 */
	private synchronized boolean writeFile() {
		if (raw == null) {
			throw new RuntimeException(
					"Commons.writeToFile called even though raw is null");
		}

		// System.out.println(node.getUUID() + ":");
		// for (long key : raw.commonsMap.keySet()) {
		// System.out.println(key + ":\t");
		// for (long l : raw.commonsMap.get(key)) {
		// System.out.println("\t" + l);
		// }
		// }
		return Serialization.writeObjectToFile(raw, persistentFileName);
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
		raw = null;
	}

	/**
	 * Updates the commons of node and writes the data to disk
	 */
	public void update() {
		mayFreeMem = false;
		final int now = (int) (System.currentTimeMillis() / 1000l);

		for (long friendUUID : node.getFriends()) {
			updateFriend(NodeFactory.getNode(friendUUID), false);
		}

		raw.lastUpdateTS = now;
		writeFile();
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
			long[] commons = raw.commonsMap.get(like.getUUID());
			if (commons == null) {
				continue;
			}

			/*
			 * Remove the friend from the commons list of the liked entity
			 */
			raw.commonsMap.put(like.getUUID(),
					removeFromCommonsList(commons, friend.getUUID()));

			writeFile();
		}
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
		if (raw == null) {
			readFile();
		}

		mayFreeMem = false;

		int searchTS = ignoreTimestamp ? 0 : raw.lastUpdateTS;
		for (Like like : friend.getLikesFromTimeOn(searchTS)) {
			/*
			 * If the friend likes this node we would have a recursion->skip
			 */
			if (like.getUUID() == node.getUUID()) {
				continue;
			}

			/*
			 * Find the list of commons with the entity the friend liked
			 */
			long[] commons = raw.commonsMap.get(like.getUUID());
			if (commons == null) {
				commons = new long[InitialCommonListLength];
			}

			/*
			 * Add the friend to the commons list of the liked entity
			 * 
			 * TODO: separate likes, dislikes and neutral likes in 2 different
			 * maps and delete the entries if we find a neutral like
			 */
			commons = addIntoCommonsList(commons, friend.getUUID());
			raw.commonsMap.put(like.getUUID(), commons);
		}

		writeFile();
		mayFreeMem = true;
	}

	/**
	 * Seeks the first 0 element in the commons array and puts addUUID into that
	 * position. If the array is already full its length will be extended by a
	 * factor of CommonListGrowthFactor
	 * 
	 * @param commons
	 *            The array addUUID will be added to
	 * @param addUUID
	 *            The uuid to be added
	 * @return The commons array (length might have been extended)
	 */
	private long[] addIntoCommonsList(long[] commons, long addUUID) {
		int lastEmptyPointer = 0;

		/*
		 * Seek the last 0 element or return if addUUID is already in the array
		 * 
		 * If neither any 0-element nor addUUID have been found lastEmptyPointer
		 * will be commons.length after this loop
		 */
		while (lastEmptyPointer != commons.length) {
			long current = commons[lastEmptyPointer];
			if (current == addUUID) {
				return commons;
			}
			if (current == 0) {
				break;
			}
			lastEmptyPointer++;
		}

		/*
		 * No empty position found. Array has to be extended
		 */
		if (lastEmptyPointer == commons.length) {
			int newLength = (int) (CommonListGrowthFactor * commons.length + 1);
			int oldLength = commons.length;
			long[] tmp = new long[newLength];
			System.arraycopy(commons, 0, tmp, 0, oldLength);
			commons = tmp;
		}

		commons[lastEmptyPointer] = addUUID;
		return commons;
	}

	/**
	 * Removes the entry in the commons list with the value UUID
	 * 
	 * @param commons
	 *            The array UUID will be removed from
	 * @param UUID
	 *            The UUID to be removed
	 * @return The commons array
	 */
	private long[] removeFromCommonsList(long[] commons, long UUID) {

		/*
		 * Seek the element in commons with the value UUID and move all elements
		 * behind on to the left
		 */
		for (int i = 0; i < commons.length; i++) {
			if (commons[i] == UUID) {
				System.arraycopy(commons, i + 1, commons, i, commons.length - i);
				commons[commons.length - 1] = 0; // remove duplicate last
													// element
				return commons;
			}
		}

		return commons;
	}
}
