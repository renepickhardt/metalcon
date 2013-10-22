package de.metalcon.like;

import java.io.IOException;
import java.util.HashMap;

import de.metalcon.storage.IPersistentMUIDSet;
import de.metalcon.storage.PersistentUUIDSet;

/**
 * @author Jonas Kunze
 */
public class NodeFactory {
	private static String StorageDir = "";

	/*
	 * The file with all existing node uuids. The uuids are stored as
	 * concatenated longs where some might be 0 (fragmentation)
	 */
	private static String PersistentNodesFile = "";
	private static IPersistentMUIDSet AllNodes = null;

	/*
	 * This Map stores all nodes that are alive.
	 */
	private static HashMap<Long, Node> AllNodesAliveCache = new HashMap<Long, Node>();

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
	public static void initialize(final String storDir) {
		if (AllNodes != null) {
			throw new RuntimeException(
					"NodeFactory has already been initialized.");
		}
		StorageDir = storDir;
		PersistentNodesFile = storDir + "/allNodes.dat";

		try {
			AllNodes = new PersistentUUIDSet(PersistentNodesFile);
			System.out.println("Finished reading " + PersistentNodesFile + ":");
			System.out.println(AllNodes.size() + " uuids read, "
					+ ((PersistentUUIDSet) AllNodes).getFragmentationRatio()
					* 100 + "% fragmentation.");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Initializes Node objects for all UUIDs that were found in the persistent
	 * node list file
	 */
	public static void pushAllNodesToCache() {
		for (long uuid : AllNodes) {
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
		if (n == null && AllNodes.contains(uuid)) {
			synchronized (NodeFactory.class) {
				n = new Node(uuid, StorageDir, false);
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
		if (AllNodes.contains(uuid)) {
			throw new RuntimeException(
					"Calling Node.createNode with an uuid that already exists in the DB");
		}
		synchronized (NodeFactory.class) {
			AllNodes.add(uuid);

			Node n = new Node(uuid, StorageDir, true);
			AllNodesAliveCache.put(uuid, n);
			return n;
		}
	}

	static void removeNodeFromPersistentList(long nodeID) throws IOException {
		AllNodesAliveCache.remove(nodeID);
		AllNodes.remove(nodeID);
	}

	public static long[] getAllNodeUUIDs() {
		return AllNodes.toArray();
	}
}
