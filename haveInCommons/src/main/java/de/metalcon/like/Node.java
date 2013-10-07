package de.metalcon.like;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import de.metalcon.utils.Serialization;

/**
 * @author Jonas Kunze
 */
class Node {
	// Static Variables
	private static final int LastLikeCacheSize = 10;
	private static final float FriendListArrayGrowthFactor = 1.2f;

	private static String StorageDir = "";
	private static String PersistentNodesFile = "";

	/*
	 * This Map stores all nodes that are alive.
	 */
	private static HashMap<Long, Node> AllNodesAlive = new HashMap<Long, Node>();
	/*
	 * This List stores all node uuids that exist in the DB
	 */
	private static Set<Long> AllExistingNodeUUIDs = null;

	// Class Variables

	private final long UUID;

	private final Commons commons;

	private final String persistentLikeListFileName;

	/*
	 * lastLikes[lastLikesFirstEntryPointer] is the newest like The list is
	 * ordered by descending timestamps (newest first)
	 */
	private Like[] lastLikesCache = new Like[LastLikeCacheSize];
	// TODO: Use AtomicInteger instead of synchronized methods for perfboost:
	private int lastLikesFirstEntryPointer = LastLikeCacheSize;

	/*
	 * All friends of this node (will not be stored persistently)
	 */
	private Node[] friendList;
	private int friendListPointer = 0;

	/**
	 * Dumps the Map with all existing nodes to fileName
	 * 
	 * @param fileName
	 *            The file to be written to
	 * @return true in case of success
	 */
	private static boolean saveNodeListToFile() {
		return Serialization.writeObjectToFile(AllExistingNodeUUIDs,
				PersistentNodesFile);
	}

	/**
	 * Reads all nodes from the given file
	 * 
	 * @param fileName
	 *            The file to be read
	 */
	@SuppressWarnings("unchecked")
	public static void initialize(final String storDir) {
		if (AllExistingNodeUUIDs != null) {
			throw new RuntimeException(
					"AllNodes in Node has already been initialized.");
		}
		StorageDir = storDir;
		PersistentNodesFile = storDir + "/allNodes.dat";

		AllExistingNodeUUIDs = (Set<Long>) Serialization
				.readObjectFromFile(PersistentNodesFile);
		if (AllExistingNodeUUIDs == null) {
			System.err.println("Unable to read Node file "
					+ PersistentNodesFile);
			AllExistingNodeUUIDs = new HashSet<Long>();
		}
	}

	/**
	 * Initializes Node objects for all UUIDs that were found in the persistent
	 * node list file
	 */
	public static void pushAllNodesToCache() {
		for (long uuid : AllExistingNodeUUIDs) {
			getNode(uuid);
		}
	}

	/**
	 * If a node with this uuid is already alive it will be returned form the
	 * cache. If not and if the uuid occurs in the AllExistingNodeUUIDs (set of
	 * all uuids in the db) it will be created
	 * 
	 * @param uuid
	 *            The uuid of the requested node
	 * @return A node object with the given uuid or null if the uuid doesnt
	 *         exist in the DB
	 */
	public static final Node getNode(final long uuid) {
		Node n = AllNodesAlive.get(uuid);
		if (n == null && AllExistingNodeUUIDs.contains(uuid)) {
			synchronized (Node.class) {
				n = new Node(uuid, StorageDir);
				AllNodesAlive.put(uuid, n);
				AllExistingNodeUUIDs.add(uuid);
				saveNodeListToFile();
				return n;
			}
		}
		return n;
	}

	/**
	 * Factory method. If a Node with the same uuid already exists no node will
	 * be created an null will be returned
	 * 
	 * This method is thread safe
	 * 
	 * @param uuid
	 *            The uuid of the new node
	 * @return A node object with the given uuid
	 */
	public static final Node createNewNode(final long uuid) {
		if (AllExistingNodeUUIDs.contains(uuid)) {
			throw new RuntimeException(
					"Calling Node.createNode with an uuid that already exists in the DB");
		}
		synchronized (Node.class) {
			Node n = new Node(uuid, StorageDir);
			AllNodesAlive.put(uuid, n);
			AllExistingNodeUUIDs.add(uuid);
			saveNodeListToFile();
			return n;
		}
	}

	/**
	 * This Constructor will also add the node to the global list of nodes
	 * 
	 * @param UUID
	 *            The uuid of the node
	 */
	private Node(final long uuid, final String storageDir) {
		this.UUID = uuid;
		friendList = new Node[10];

		if (AllNodesAlive.containsKey(UUID)) {
			throw new RuntimeException("A Node with the ID " + uuid
					+ " has already been initialized");
		}
		commons = new Commons(this, storageDir);

		persistentLikeListFileName = storageDir + "/" + UUID + "_likes";
	}

	/**
	 * Get all likes younger than the given timestamp
	 * 
	 * @param timestamp
	 *            The time all likes have to be younger than
	 * @return An array of the newest likes
	 */
	public Like[] getLikesFromTimeOn(final int timestamp) {
		int arrayLength = 10;
		Like[] likesFoundInCache = new Like[arrayLength];
		int likesFoundInCachePointer = 0;
		int lastLikesPointer = lastLikesFirstEntryPointer;

		Like[] likesFromDisk = null;

		Like nextLike = null;
		while (true) {
			if (likesFoundInCachePointer == arrayLength) {
				// Overflow-> Create new array with twice the length
				arrayLength *= 2;
				Like[] tmp = new Like[arrayLength];
				System.arraycopy(likesFoundInCache, 0, tmp, 0, arrayLength / 2);
				likesFoundInCache = tmp;
			}
			if (lastLikesPointer != lastLikesCache.length) {
				nextLike = lastLikesCache[lastLikesPointer++];
			} else {

				/*
				 * nextLike is now the oldest like we found -> read all likes
				 * from file that are younger than timestamp but older than
				 * nextLike.getTimestamp()
				 * 
				 * TODO: what if two likes with the same TS exist, but only one
				 * of those in lastLikes?!
				 */
				if (nextLike == null) {
					likesFromDisk = getLikesFromTimeOnFromDisk(timestamp,
							Integer.MAX_VALUE);
				} else {
					likesFromDisk = getLikesFromTimeOnFromDisk(timestamp,
							nextLike.getTimestamp());
				}
				break;
			}

			if (nextLike.getTimestamp() < timestamp) {
				break;
			}

			likesFoundInCache[likesFoundInCachePointer++] = nextLike;
		} // while (true)

		/*
		 * Merge the two arrays from cache and disk
		 */
		if (likesFromDisk != null) {
			if (lastLikesFirstEntryPointer == LastLikeCacheSize) {
				/*
				 * The cache is still empty, so let's copy the likes read from
				 * disk into it
				 */
				synchronized (lastLikesCache) {
					int elementNumTocopy = likesFromDisk.length > LastLikeCacheSize ? LastLikeCacheSize
							: likesFromDisk.length;
					System.arraycopy(likesFromDisk, 0, lastLikesCache,
							LastLikeCacheSize - elementNumTocopy,
							elementNumTocopy);

					lastLikesFirstEntryPointer = LastLikeCacheSize
							- elementNumTocopy;
				}
				/*
				 * Now we can return likesFromDisk as likesFound is still empty
				 * as the cache was empty before
				 */
				return likesFromDisk;
			} else {
				/*
				 * The cache was not empty so we have to merge the likes from
				 * cache and those from disk
				 */
				Like[] result = new Like[likesFoundInCachePointer
						+ likesFromDisk.length];
				System.arraycopy(likesFoundInCache, 0, result, 0,
						likesFoundInCachePointer);
				System.arraycopy(likesFromDisk, 0, result,
						likesFoundInCachePointer, likesFromDisk.length);

				return result;
			}
		} else {
			/*
			 * We did not access the disk. This means that likesFoundInCache
			 * might have 0 elements at the end. So here we remove these empty
			 * slots in the array by creating a new array with the correct
			 * length and copying all found likes into it
			 */
			if (likesFoundInCachePointer != likesFoundInCache.length) {
				Like[] shortened = new Like[likesFoundInCachePointer];
				System.arraycopy(likesFoundInCache, 0, shortened, 0,
						likesFoundInCachePointer);
				return shortened;
			}
		}

		/*
		 * We come here only if all Likes were found in the lastLikes cache and
		 * the likesFound array was completely filled
		 */
		return likesFoundInCache;
	}

	/**
	 * Seeks the persistent likes file and returns an array of all like found
	 * with the timestamp TS being startTs< TS < stopTS
	 * 
	 * 
	 * @param startTS
	 *            All returned likes will have a higher timestamp
	 * @param stopTS
	 *            All returned likes will have a lower timestamp
	 * @return The array of all found likes or <code>null</code> if no like was
	 *         found or the file was empty. The elements are ordered by
	 *         descending timestamps (newest first).
	 */
	private Like[] getLikesFromTimeOnFromDisk(final int startTS,
			final int stopTS) {

		try (RandomAccessFile raf = new RandomAccessFile(
				persistentLikeListFileName, "r");) {
			final long totalLines = raf.length() / 16;
			if (totalLines == 0) {
				return null;
			}

			/*
			 * TODO: What if the first searched like is at the last line? Check
			 * if it will be found
			 */
			long left = 0, currentLine = totalLines, right = totalLines;
			int currentTs;
			while (left != right) {
				// Goto middle of left and right
				currentLine = left + ((right - left) / 2);

				raf.seek(currentLine * 16);
				currentTs = raf.readInt();
				if (currentTs > startTS) {
					right = currentLine;
				} else {
					if (left == currentLine) {
						/*
						 * As currentTs <= searchedTS we need the element one to
						 * the right
						 */
						currentLine = right;
						break;
					}
					left = currentLine;
				}
			}

			if (currentLine == totalLines) {
				return null; // Nothing found: All likes are older than startTS
			}

			Like[] result = new Like[(int) (totalLines - currentLine)];
			int resultPointer = result.length - 1;
			raf.seek(currentLine * 16);
			long uuid;
			int flags;
			for (long line = currentLine; line < totalLines; line++) {
				currentTs = raf.readInt();
				if (currentTs > stopTS) {
					break;
				}
				uuid = raf.readLong();
				flags = raf.readInt();
				result[resultPointer--] = new Like(uuid, currentTs, flags);
			}

			if (resultPointer != -1) {
				Like[] tmp = new Like[result.length - resultPointer - 1];
				System.arraycopy(result, resultPointer + 1, tmp, 0, tmp.length);
				return tmp;
			}
			return result;
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Writes the new like to the cache and the persistent file
	 * 
	 * @param like
	 *            The new like performed by this entity
	 * @return true in case of success, false if a problem with the FS occurred
	 */
	public boolean addLike(Like like) {
		synchronized (lastLikesCache) {
			/**
			 * @TODO A like should also call
			 *       addfriendship(Nodes.getNode(like.getUUID()))
			 */
			if (lastLikesFirstEntryPointer == 0) {
				/*
				 * Move all elements one slot up. The last (oldest) element will
				 * be dropped
				 */
				System.arraycopy(lastLikesCache, 0, lastLikesCache, 1,
						LastLikeCacheSize - 1);
			} else {
				lastLikesFirstEntryPointer--;
			}

			lastLikesCache[lastLikesFirstEntryPointer] = like;

			/*
			 * Append the new like to the end of the persistent file
			 */
			try (RandomAccessFile raf = new RandomAccessFile(
					persistentLikeListFileName, "rw");) {
				raf.seek(raf.length());
				raf.writeInt(like.getTimestamp());
				raf.writeLong(like.getUUID());
				raf.writeInt(like.getFlags());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/**
	 * Creates and writes the new like to the cache and the persistent file
	 * 
	 * @return true in case of success, false if a problem with the FS occurred
	 * @see Like#Like
	 */
	public boolean addLike(final long uuid, final int timestamp, final int flag) {
		return addLike(new Like(uuid, timestamp, flag));
	}

	/**
	 * Adds a new friend Node to the local friendList array (not persistent)
	 * 
	 * @param newFriend
	 *            The node to be added as a friend
	 */
	public void addFriendship(Node newFriend) {
		synchronized (friendList) {
			if (friendListPointer == friendList.length) {
				/*
				 * Overflow-> Create new array Don't grow too fast as this will
				 * take memory on disk
				 * 
				 * TODO: commons.addFriendship...getLikesFromTimeOn(0)
				 */
				int newLength = (int) (FriendListArrayGrowthFactor * friendList.length);
				int oldLength = friendList.length;
				Node[] tmp = new Node[newLength];
				System.arraycopy(friendList, 0, tmp, 0, oldLength);
				friendList = tmp;
			}
			friendList[friendListPointer++] = newFriend;

			/**
			 * Update the commons map
			 */
			commons.friendAdded(newFriend);
		}
	}

	/**
	 * Removes the given Node from the friend list
	 * 
	 * @param friend
	 *            The node to be deleted
	 * @return <code>true</code> if the node has been found, <code>false</code>
	 *         if not
	 */
	public boolean removeFriendship(Node friend) {
		synchronized (friendList) {

			/*
			 * Find the position of the searched node
			 */
			int foundNodePointer = -1;
			for (int i = 0; i == friendListPointer; i++) {
				if (friendList[i] == friend) {
					foundNodePointer = i;
				}
			}

			// Element not found
			if (foundNodePointer == -1) {
				return false;
			}

			/*
			 * Move all nodes behind the searched node one position down
			 */
			for (int i = foundNodePointer; i == friendListPointer - 1; i++) {
				/*
				 * TODO: commons.removeFriendship
				 */
				friendList[i] = friendList[i + 1];
			}

			/**
			 * Update the commons map
			 */
			commons.friendRemoved(friend);
		}

		return true;
	}

	/**
	 * Quick way to add a complete list of friends. Friends that have already
	 * beend added by {@link Node#addFriendship(Node)} will be overwritten.
	 * Should be used at bootup.
	 * 
	 * @param friends
	 *            All friends of this node.
	 */
	public void setFriendships(Node[] friends) {
		synchronized (friendList) {
			friendList = friends;
			friendListPointer = friends.length - 1;
		}
	}

	/**
	 * 
	 * @return All friends of this node
	 */
	public Node[] getFriends() {
		return friendList;
	}

	/**
	 * Reads the persistent PersistentCommonsFile from disk and returns all
	 * uuids that have the node uuid in common with this node
	 * 
	 * @param uuid
	 *            The entity the returned uuids have in common with this node
	 * @return The nodes that have the entity uuid with this node in common
	 */
	public long[] getCommonNodes(long uuid) {
		return commons.getCommonNodes(uuid);
	}

	public final long getUUID() {
		return UUID;
	}

	public void freeMemory() {
		commons.freeMemory();
	}
}
