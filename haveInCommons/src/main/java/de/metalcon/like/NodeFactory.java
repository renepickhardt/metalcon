package de.metalcon.like;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

/**
 * @author Jonas Kunze
 */
class NodeFactory {
	private static String StorageDir = "";

	/*
	 * The file with all existing node uuids. The uuids are stored as
	 * concatenated longs where some might be 0 (fragmentation)
	 */
	private static String PersistentNodesFile = "";

	private static int NumberOfZeroUUIDsInFile = 0;

	/*
	 * This Map stores all nodes that are alive.
	 */
	private static HashMap<Long, Node> AllNodesAliveCache = new HashMap<Long, Node>();

	/*
	 * This Map stores all node uuids that exist in the DB as keys and the
	 * position in the persistent file as value
	 */
	private static RandomAccessFile persistentNodeFile = null;
	private static HashMap<Long, Long> AllExistingNodeUUIDs = null;
	private static HashMap<Long, Long> AllExistingNodeUUIDsReverse = null;

	/*
	 * Number of longs to jump through the persistent node file to access the
	 * last element
	 */
	private static long LastNodeUUIDPosition = -1;

	/**
	 * Dumps the Map with all existing nodes to fileName
	 * 
	 * @param fileName
	 *            The file to be written to
	 * @return true in case of success
	 */
	// private static boolean saveNodeListToFile(final String nodeFilePath) {
	// FileChannel file = raf.getChannel();
	// ByteBuffer buf = file.map(FileChannel.MapMode.READ_WRITE, 0,
	// 8 * AllExistingNodeUUIDs.size());
	// for (Long uuid : AllExistingNodeUUIDs.keySet()) {
	// buf.putLong(uuid);
	// }
	// return false;
	// }

	/**
	 * Reads all nodes from the given file
	 * 
	 * @param fileName
	 *            The file to be read
	 */
	@SuppressWarnings("unchecked")
	public static void initialize(final String storDir) {
		if (persistentNodeFile != null) {
			throw new RuntimeException(
					"NodeFactory has already been initialized.");
		}
		StorageDir = storDir;
		PersistentNodesFile = storDir + "/allNodes.dat";

		try {
			persistentNodeFile = new RandomAccessFile(PersistentNodesFile, "rw");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}

		try {
			readNodeFile(PersistentNodesFile);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * 
	 * @param nodeFilePath
	 * @return
	 * @throws IOException
	 */
	private static void readNodeFile(final String nodeFilePath)
			throws IOException {
		AllExistingNodeUUIDs = new HashMap<Long, Long>();
		AllExistingNodeUUIDsReverse = new HashMap<Long, Long>();

		final long numberOfLongs = (int) persistentNodeFile.length() / 8;
		LastNodeUUIDPosition = numberOfLongs;
		if (numberOfLongs == 0) {
			return;
		}

		NumberOfZeroUUIDsInFile = 0;
		for (long pos = 0; pos < numberOfLongs; pos++) {
			long uuid = persistentNodeFile.readLong();
			if (uuid == 0) {
				NumberOfZeroUUIDsInFile++;
			} else {
				AllExistingNodeUUIDs.put(uuid, pos);
				AllExistingNodeUUIDsReverse.put(pos, uuid);
			}
		}
		System.out.println("Finished reading "+nodeFilePath+":");
		System.out.println(numberOfLongs+" uuids read, "+NumberOfZeroUUIDsInFile+" were 0: "+NumberOfZeroUUIDsInFile/numberOfLongs*100+"% fragmentation.");
	}

	/**
	 * Initializes Node objects for all UUIDs that were found in the persistent
	 * node list file
	 */
	public static void pushAllNodesToCache() {
		for (long uuid : AllExistingNodeUUIDs.keySet()) {
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
		Node n = AllNodesAliveCache.get(uuid);
		if (n == null && AllExistingNodeUUIDs.containsKey(uuid)) {
			synchronized (NodeFactory.class) {
				n = new Node(uuid, StorageDir);
				AllNodesAliveCache.put(uuid, n);
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
	 * @throws IOException
	 */
	public static final Node createNewNode(final long uuid) throws IOException {
		if (AllExistingNodeUUIDs.containsKey(uuid)) {
			throw new RuntimeException(
					"Calling Node.createNode with an uuid that already exists in the DB");
		}
		synchronized (NodeFactory.class) {
			persistentNodeFile.seek(persistentNodeFile.length());
			persistentNodeFile.writeLong(uuid);

			Node n = new Node(uuid, StorageDir);
			AllNodesAliveCache.put(uuid, n);
			AllExistingNodeUUIDs.put(uuid, ++LastNodeUUIDPosition);
			AllExistingNodeUUIDsReverse.put(LastNodeUUIDPosition, uuid);

			// saveNodeListToFile(PersistentNodesFile);
			return n;
		}
	}

	static void removeNodeFromPersistentList(long nodeID) throws IOException {
		AllNodesAliveCache.remove(nodeID);
		Long positionInFile = AllExistingNodeUUIDs.get(nodeID);
		if (positionInFile != null) {
			AllExistingNodeUUIDs.remove(nodeID);
			AllExistingNodeUUIDsReverse.remove(positionInFile);

			persistentNodeFile.seek(positionInFile * 8);
			persistentNodeFile.writeLong(0);
		}
	}
}
