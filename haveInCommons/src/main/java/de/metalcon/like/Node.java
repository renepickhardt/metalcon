package de.metalcon.like;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Jonas Kunze
 */
public class Node {
	private static final int LastLikeCacheSize = 10;
	private static final int FriendListArrayBufferSize = 2;

	private final long ID;
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

	private final File PersistentCommonsFile;
	private int lastCacheUpdate;
	private Node[] friendList;

	public Node(final long ID) throws FileNotFoundException {
		this.ID = ID;
		PersistentCommonsFile = new File("mypath");
		friendList = new Node[FriendListArrayBufferSize];
	}

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

	public boolean addLike(Like like) {
		if (lastLikesLastEntryPointer == LastLikeCacheSize) {
			// swap all elements down one slot. The first element will be
			// dropped
			System.arraycopy(lastLikes, 1, lastLikes, 0, LastLikeCacheSize - 1);
		}
		lastLikes[LastLikeCacheSize] = like;

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

	public boolean addLike(final long uuid, final int timestamp, final int flag) {
		return addLike(new Like(uuid, timestamp, flag));
	}

	public boolean removeLike(final long uuid, final int timestamp, final int flag) {
		return true;
	}

	public boolean addFriendship(Node newFriend) {
		return true;
	}

	public boolean removeFriendship(Node n) {
		return true;
	}

	private final String getLikeListFileName() {
		return "data";
	}

	/**
	 * 
	 * @return A pair with the UUID and the TS of the next read like
	 * @throws IOException
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
}
