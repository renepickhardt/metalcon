package de.metalcon.executables;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import de.metalcon.haveInCommons.HaveInCommons;
import de.metalcon.haveInCommons.PersistentReadOptimized;
import de.metalcon.haveInCommons.SingleNodePreprocessorNeo4j;
import de.metalcon.like.Node;
import de.metalcon.like.NodeFactory;
import de.metalcon.like.NormalizedLikeRetrieval;
import de.metalcon.like.PersistentLikeHistory;
import de.metalcon.storage.LevelDBHandler;
import de.metalcon.storage.PersistentUUIDSetLevelDB;

/**
 * Hello world!
 * 
 */
public class App {

	private static final String DATA_DIR = "../data/";
	private static final String METALCON_FILE = DATA_DIR
			+ "metalcon-all-hashed.csv";
	private static final String METALCON_USER_FILE = DATA_DIR
			+ "metalcon-user.csv";
	private static final String METALCON_NONUSER_FILE = DATA_DIR
			+ "metalcon-nonUser.csv";
	private static final String METALCON_NONUSER_RAND_FILE = DATA_DIR
			+ "metalcon-nonUser-random.csv";
	private static final String METALCON_USER_SMALL_FILE = DATA_DIR
			+ "metalcon-user-small.csv";
	private static final String UB_SMALL_FILE = DATA_DIR + "ub-small.csv";
	private static final String POWER_LAW_USER = DATA_DIR
			+ "powerlaw-11023-2.5-100-200.csv";
	private static final String POWER_LAW_USER_SMALL = DATA_DIR
			+ "powerlaw-5000-2-1-200.csv";
	private static final String POWER_LAW_USER_SMALL2 = DATA_DIR
			+ "powerlaw-5000-2.5-10-200.csv";
	private static final String POWER_LAW_USER_SMALL4 = DATA_DIR
			+ "powerlaw-5000-2.5-100-200.csv";

	public static final int BATCH_SIZE = 100000;

	private static HaveInCommons graph;

	public static void main(String[] args) {
		try {
			PersistentLikeHistory.initialize("/dev/shm/commonsDB/likesDB");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		graph = new NormalizedLikeRetrieval("/dev/shm/commonsDB");
		LevelDBHandler.initialize("/dev/shm/commonsDB/levelDB");
		PersistentUUIDSetLevelDB.initialize();

		long time = 0;
		/*
		 * Import all users
		 */
		time = importGraph(graph, METALCON_USER_FILE);
		long nodeNum = NodeFactory.getAllNodeUUIDs().length;
		System.out.println("Importing " + nodeNum + " users took "
				+ (int) (time / 1E9f) + " s (" + time / 1000 / nodeNum
				+ " µs per node)");

		/*
		 * Import Albums and Bands
		 */
		time = importGraph(graph, METALCON_NONUSER_RAND_FILE);
		long newNodes = NodeFactory.getAllNodeUUIDs().length - nodeNum;
		System.out
				.println("Importing nonUser nodes took " + (int) (time / 1E9f)
						+ " s (" + newNodes + " new nodes added)");

		/*
		 * Update all nodes
		 */
		time = updateAllNodes();
		System.out.println("Updating " + NodeFactory.getAllNodeUUIDs().length
				+ " nodes took " + (int) (time / 1E9f) + " s (" + time / 1000
				/ NodeFactory.getAllNodeUUIDs().length + " µs per node)");

		/*
		 * Process some getInCommon calls
		 */
		final long[] uuids = NodeFactory.getAllNodeUUIDs();

		Random rand = new Random();
		int runs = 100000, found = 0;
		long start = System.nanoTime();
		for (int i = 0; i < runs; i++) {
			int commons = testInCommons(graph,
					uuids[rand.nextInt(uuids.length)],
					uuids[rand.nextInt(uuids.length)], false);
			if (commons > 0) {
				found++;
			}
		}
		time = System.nanoTime() - start;
		System.out.println("Generating commons for " + runs
				+ " node pairs took " + (int) (time / 1E6f) + " ms finding "
				+ found + " non empty lists (" + time / 1000 / runs
				+ " µs per node)");

		testInCommons(graph, 1, 2, true);
	}

	public static long updateAllNodes() {
		long start = System.nanoTime();
		for (long uuid : NodeFactory.getAllNodeUUIDs()) {
			Node n = NodeFactory.getNode(uuid);
			n.updateCommons();
		}
		return System.nanoTime() - start;
	}

	public static int testInCommons(HaveInCommons graph, long from, long to,
			boolean verbose) {
		long start = System.nanoTime();
		long[] commons = graph.getCommonNodes(from, to);
		long end = System.nanoTime();

		if (commons != null) {
			if (verbose) {
				System.out.println("getCommonNodes needed: " + (end - start)
						/ 1000 + " microseconds for " + commons.length
						+ " commons");
			}
			if (commons.length > 0) {
				if (!verbose) {
					System.out.println("getCommonNodes needed: "
							+ (end - start) / 1000 + " microseconds for "
							+ commons.length + " commons");
				}
				if (verbose) {
					System.out.println("Common length: " + commons.length);
					for (long s : commons) {
						System.out.println(s);
					}
				}
				return commons.length;
			} else {
				if (verbose) {
					System.out.println(from + " and " + to
							+ " have nothing in common");
				}
				return 0;
			}
		} else {
			if (verbose) {
				System.out.println(from + " and " + to
						+ " have nothing in common");
			}
			return -1;
		}

	}

	private static long importGraph(HaveInCommons graphImpl, String dataFile) {
		long start = System.nanoTime();
		long last = System.nanoTime();
		long now;

		try {
			BufferedReader br = new BufferedReader(new FileReader(dataFile));
			String line = "";
			long edgeCount = 0;
			long nodeCount = 0;
			while ((line = br.readLine()) != null) {
				String[] values = line.split("\\s");
				if (values.length < 2) {
					continue;
				} else if (values.length == 2) {
					graphImpl.putEdge(Long.parseLong(values[0]),
							Long.parseLong(values[1]));
					edgeCount++;

					// graphImpl.putEdge(Long.parseLong(values[1]),
					// Long.parseLong(values[0]));
					// edgeCount++;
				} else {
					for (int i = 1; i < values.length; i++) {
						graphImpl.putEdge(Long.parseLong(values[0]),
								Long.parseLong(values[i]));
						edgeCount++;
					}
				}
				nodeCount++;

				if (edgeCount % BATCH_SIZE == 0) {
					// print Statistics
					now = System.nanoTime();

					System.out.println("#nodes=" + nodeCount + "\t" + "#edges="
							+ edgeCount + "\t" + "time total="
							+ (long) ((now - start) / 1E9f) + "sec.\t"
							+ "batch time=" + (long) ((now - last) / 1E6f)
							+ "msec.");

					System.err
							.println((int) (BATCH_SIZE * 2 * 1E9f / (now - last))
									+ " edges/sec");

					last = now;
				}
			}

			now = System.nanoTime();
			System.out.println("#nodes=" + nodeCount + "\t" + "#edges="
					+ edgeCount + "\t" + "time total="
					+ (long) ((now - start) / 1E9f) + "sec.\t" + "batch time="
					+ (long) ((now - last) / 1E6f) + "msec.");

			if (graphImpl instanceof PersistentReadOptimized) {
				((PersistentReadOptimized) graphImpl).flush();
			}

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return System.nanoTime() - start;
	}

	private static void TestSingleNodePreprocessor() {
		System.out.println("SingleNodePreprocessorNeo4j:");

		SingleNodePreprocessorNeo4j processor = new SingleNodePreprocessorNeo4j(
				"neo4j-metalconAll");

		processor.generateIndex(1);
		processor.print(1);

		for (int i = 1; i < 100; ++i) {
			long start = System.nanoTime();
			if (processor.generateIndex(i)) {
				long end = System.nanoTime();

				int max = processor.print(i);
				System.out.println((end - start) / 1E6f + " milliseconds, "
						+ max + " maximum elements");
			}
		}
	}

}
