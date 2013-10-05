package de.metalcon.like;

import java.io.File;

/**
 * @author Jonas Kunze
 */
public class Node {
	private static final int LastLikeCacheSize = 10;

	private final long ID;
	private final File LikeListFile;
	private Like[] lastLikes = new Like[LastLikeCacheSize];
	private final File PersistentLikesFile;
	private int lastCacheUpdate;
	private Node[] friendList;

	public Node(final long ID) {
		this.ID = ID;

	}

	public Like[] getLikesFromTimeOn(final int timestamp) {
		return null;
	}

	public Like[] getDislikesFromTimeOn(final int timestamp) {
		return null;
	}

	public boolean addLike(long uuid, int timestamp) {
		return true;
	}

	public boolean removeLike(long uuid) {
		return true;
	}

	public boolean addFriendship(Node newFriend) {
		return true;
	}

	public boolean removeFriendship(Node n) {
		return true;
	}
}
