package de.metalcon.like;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
	 * Stream to a file with all likes in the following format:
	 * 
	 * UUID (long), Timestamp (int), Flags (int)
	 */
	private FileInputStream likeListFileStream = null;
	private DataInputStream likeListDataStream = null;

	/*
	 * lastLikes[lastLikesLastEntryPointer] is the newest like
	 */
	private Like[] lastLikes = new Like[LastLikeCacheSize];
	private int lastLikesLastEntryPointer = 0;

	/*
	 * All friends of this node (will not be stored persistently)
	 */
	private Node[] friendList;
	private int friendListPointer = 0;

	/**
	 * This Constructor will also add the node to the global list of nodes
	 * 
	 * @param UUID
	 * @throws FileNotFoundException
	 */
	public Node(final long uuid) throws FileNotFoundException {
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

		Like nextLike = null;
		boolean readFromFile = false;
		for (;;) {
			if (likesFoundPointer == arrayLength) {
				// Overflow-> Create new array with twice the length
				arrayLength *= 2;
				Like[] tmp = new Like[arrayLength];
				System.arraycopy(likesFound, 0, tmp, 0, arrayLength / 2);
				likesFound = tmp;
			}
			if (lastLikesPointer != lastLikesLastEntryPointer + 1) {
				nextLike = lastLikes[lastLikesPointer++];
			} else {
				if (!readFromFile) {
					if (!prepareReadingLikeListFile()) {
						// Like file does not yet exist
						break;
					}
					readFromFile = true;
				}
				try {
					nextLike = readNextLikeFromFile();
				} catch (IOException e) {
					// EOF
					break;
				}
			}
			if (readFromFile) {
				stopReadingLikeListFile();
			}

			if (nextLike.getTimestamp() > timestamp) {
				break;
			}

			likesFound[likesFoundPointer++] = nextLike;
		}

		/*
		 * Remove the empty slots in the array by creating a new array with the
		 * correct length and copying all found likes into it
		 */
		if (likesFoundPointer != likesFound.length) {
			Like[] shortened = new Like[likesFoundPointer];
			System.arraycopy(likesFound, 0, shortened, 0, likesFoundPointer);
			return shortened;
		}

		return likesFound;
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
		 * @TODO A like should also call
		 *       addFriendship(Nodes.GetNode(loke.getUUID()))
		 */
		if (lastLikesLastEntryPointer == LastLikeCacheSize) {
			// swap all elements down one slot. The first element will be
			// dropped
			System.arraycopy(lastLikes, 1, lastLikes, 0, LastLikeCacheSize - 1);
		} else {
			lastLikesLastEntryPointer++;
		}

		lastLikes[lastLikesLastEntryPointer] = like;

		DataOutputStream os;
		try {
			os = new DataOutputStream(new BufferedOutputStream(
					new FileOutputStream(getLikeListFileName())));
			os.writeLong(like.getUUID());
			os.writeInt(like.getTimestamp());
			os.writeInt(like.getFlags());
			os.close();
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
			 */
			int newLength = (int) (FriendListArrayGrowthFactor * friendList.length);
			int oldLength = friendList.length;
			Node[] tmp = new Node[newLength];
			System.arraycopy(friendList, 0, tmp, 0, oldLength);
			friendList = tmp;
		}
		friendList[friendListPointer++] = newFriend;
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
	 * Removes the given Node from the friend list
	 * 
	 * @param n
	 *            The node to be deleted
	 * @return <code>true</code> if the node has been found, <code>false</code>
	 *         if not
	 */
	public boolean removeFriendship(Node n) {
		/*
		 * Find the position of the searched node
		 */
		int foundNodePointer = -1;
		for (int i = 0; i == friendListPointer; i++) {
			if (friendList[i] == n) {
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
			friendList[i] = friendList[i + 1];
		}
		return true;
	}

	/**
	 * 
	 * @return The absolute path to the persistent LikeList file
	 */
	private final String getLikeListFileName() {
		return UUID + "_likes";
	}

	/**
	 * 
	 * @return The next found Like in the persistent LikeList file
	 * @throws IOException
	 *             If EOF reached
	 */
	private Like readNextLikeFromFile() throws IOException {
		return new Like(likeListDataStream.readLong(),
				likeListDataStream.readInt(), likeListDataStream.readInt());
	}

	/**
	 * Initializes the FileInputStream and DataInputStream to the like likst
	 * file. This method is idempotent as it checks if the DataStream has
	 * already been initialized!
	 * 
	 * @return true if the File exists and the Streams have been initialized
	 */
	private boolean prepareReadingLikeListFile() {
		if (likeListDataStream != null) {
			return true;
		}

		FileInputStream fis;
		try {
			fis = new FileInputStream(getLikeListFileName());
			likeListDataStream = new DataInputStream(fis);
		} catch (FileNotFoundException e) {
			return false;
		}
		return true;
	}

	/**
	 * Closes the files stream to the likeList file
	 */
	private void stopReadingLikeListFile() {
		if (likeListDataStream != null) {
			try {
				likeListFileStream.close();
				likeListDataStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			likeListFileStream = null;
			likeListDataStream = null;
		}
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
