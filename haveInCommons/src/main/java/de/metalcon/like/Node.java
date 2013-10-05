package de.metalcon.like;

import java.io.File;

/**
 * @author Jonas Kunze
 */
public class Node {
	private final long ID;
	private final File LikeListFile;
	private long[] lastLikes;
	private final File PersistentLikesFile;
	private int lastCacheUpdate;
	private Node[] firendList;
}
