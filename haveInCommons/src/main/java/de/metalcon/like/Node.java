package de.metalcon.like;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

/**
 * @author Jonas Kunze
 */
public class Node {
	// Static Variables
	private static final int LastLikeCacheSize = 10;
	private static final float FriendListArrayGrowthFactor = 1.2f;

	private static HashMap<Long, Node> AllNodes = new HashMap<Long, Node>();

	// Class Variables

	private final long UUID;

	private final Commons commons;

	/*
	 * lastLikes[lastLikesFirstEntryPointer] is the newest like The list is
	 * ordered by descending timestamps (newest first)
	 */
	private Like[] lastLikes = new Like[LastLikeCacheSize];
	private int lastLikesFirstEntryPointer = LastLikeCacheSize;

	/*
	 * All friends of this node (will not be stored persistently)
	 */
	private Node[] friendList;
	private int friendListPointer = 0;

	/**
	 * Factory method. This will search for a node with the given uuid in a Map
	 * with all created nodes. If no node has been found a new instance is
	 * initialized.
	 * 
	 * This method is thread safe
	 * 
	 * @param uuid
	 *            The uuid of the new node
	 * @return A node object with the given uuid
	 */
	public static synchronized Node createNode(final long uuid) {
		Node n = AllNodes.get(uuid);
		if (n == null) {
			n = new Node(uuid);
		}
		return n;
	}

	/**
	 * This Constructor will also add the node to the global list of nodes
	 * 
	 * @param UUID
	 *            The uuid of the node
	 */
	private Node(final long uuid) {
		this.UUID = uuid;
		friendList = new Node[10];

		if (!AllNodes.containsKey(UUID)) {
			throw new RuntimeException("A Node with the ID " + uuid
					+ " has already been initialized");
		}
		AllNodes.put(UUID, this);

		commons = new Commons(this);
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
		Like[] likesFound = new Like[arrayLength];
		int likesFoundPointer = 0;
		int lastLikesPointer = 0;

		Like[] likesFromDisk = null;

		Like nextLike = null;
		while (true) {
			if (likesFoundPointer == arrayLength) {
				// Overflow-> Create new array with twice the length
				arrayLength *= 2;
				Like[] tmp = new Like[arrayLength];
				System.arraycopy(likesFound, 0, tmp, 0, arrayLength / 2);
				likesFound = tmp;
			}
			if (lastLikesPointer != lastLikesFirstEntryPointer + 1) {
				nextLike = lastLikes[lastLikesPointer++];
			} else {
				/*
				 * nextLike is now the oldest like we found -> read all likes
				 * from file that are younger than timestamp but older than
				 * nextLike.getTimestamp()
				 * 
				 * TODO: what if two likes with the same TS exist, but only one
				 * of those in lastLikes?!
				 */
				likesFromDisk = getLikesFromTimeOnFromDisk(timestamp,
						nextLike.getTimestamp());
				break;
			}

			if (nextLike.getTimestamp() > timestamp) {
				break;
			}

			likesFound[likesFoundPointer++] = nextLike;
		} // while (true)

		/*
		 * Merge the two arrays from cache and disk
		 */
		if (likesFromDisk != null) {
			Like[] result = new Like[likesFoundPointer + likesFromDisk.length];
			System.arraycopy(likesFound, 0, result, 0, likesFoundPointer);
			System.arraycopy(likesFound, likesFoundPointer, likesFromDisk, 0,
					likesFromDisk.length);
			return result;
		} else {
			/*
			 * Remove the empty slots in the array by creating a new array with
			 * the correct length and copying all found likes into it
			 */
			if (likesFoundPointer != likesFound.length) {
				Like[] shortened = new Like[likesFoundPointer];
				System.arraycopy(likesFound, 0, shortened, 0, likesFoundPointer);
				return shortened;
			}
		}

		/*
		 * We come here only if all Likes were found in the lastLikes cache and
		 * the likesFound array was completely filled
		 */
		return likesFound;
	}

	/**
	 * Seeks the persistent likes file and returns an array of all like found
	 * with the timestamp TS being startTs< TS < stopTS
	 * 
	 * @param startTS
	 *            All returned likes will have a higher timestamp
	 * @param stopTS
	 *            All returned likes will have a lower timestamp
	 * @return The array of all found likes or <code>null</code> if no like was
	 *         found or the file was empty
	 */
	private Like[] getLikesFromTimeOnFromDisk(final int startTS,
			final int stopTS) {

		try (RandomAccessFile raf = new RandomAccessFile(getLikeListFileName(),
				"r");) {
			final long totalLines = raf.length() / 16;
			if (totalLines == 0) {
				return null;
			}

			/*
			 * TODO: What if the first searched like is at the last line? Check
			 * if it will be found
			 */
			long left = 0, currentLine = totalLines, right = totalLines - 1;
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
			int resultPointer = 0;
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
				result[resultPointer++] = new Like(uuid, currentTs, flags);
			}

			if (resultPointer < result.length) {
				Like[] tmp = new Like[resultPointer];
				System.arraycopy(result, 0, tmp, 0, resultPointer);
				return tmp;
			}
			return result;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
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
		/**
		 * @TODO A like should also call addfp(Nodes.GetNode(like.getUUID()))
		 */
		if (lastLikesFirstEntryPointer == 0) {
			/*
			 * Move all elements one slot up. The last (oldest) element will be
			 * dropped
			 */
			System.arraycopy(lastLikes, 0, lastLikes, 1, LastLikeCacheSize - 1);
		} else {
			lastLikesFirstEntryPointer--;
		}

		lastLikes[lastLikesFirstEntryPointer] = like;

		/*
		 * Append the new like to the end of the persistent file
		 */
		try (RandomAccessFile raf = new RandomAccessFile(getLikeListFileName(),
				"rw");) {
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
		if (friendListPointer == friendList.length) {
			/*
			 * Overflow-> Create new array Don't grow too fast as this will take
			 * memory on disk
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

	/**
	 * Removes the given Node from the friend list
	 * 
	 * @param friend
	 *            The node to be deleted
	 * @return <code>true</code> if the node has been found, <code>false</code>
	 *         if not
	 */
	public boolean removeFriendship(Node friend) {
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
		friendList = friends;
		friendListPointer = friends.length - 1;
	}

	/**
	 * 
	 * @return All friends of this node
	 */
	public Node[] getFriends() {
		return friendList;
	}

	/**
	 * 
	 * @return The absolute path to the persistent LikeList file
	 */
	private final String getLikeListFileName() {
		return UUID + "_likes";
	}

	/**
	 * Returns the Node with the given uuid or null
	 * 
	 * @param uuid
	 *            The requested uuid
	 * @return The Node with the given uuid or null if it does not exist
	 */
	public static final Node GetNode(long uuid) {
		return AllNodes.get(uuid);
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
}
