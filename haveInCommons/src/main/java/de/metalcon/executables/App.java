package de.metalcon.executables;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import de.metalcon.haveInCommons.HaveInCommons;
import de.metalcon.haveInCommons.PersistentReadOptimized;
import de.metalcon.haveInCommons.SingleNodePreprocessorNeo4j;
import de.metalcon.like.NormalizedFlatFileLikeRetrieval;

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
	private static final String METALCON_USER_RAND_FILE = DATA_DIR
			+ "metalcon-user-random.csv";
	private static final String UB_SMALL_FILE = DATA_DIR + "ub-small.csv";
	private static final String WIKIPEDIA_FILE = "../data/";
	private static final String SMALL_FILE = "../data/";
	public static final int BATCH_SIZE = 10000;

	private static HaveInCommons graph;

	public static void main(String[] args) {

		// graph = new PersistentReadOptimized();
		graph = new NormalizedFlatFileLikeRetrieval("commonsDB");

		importGraph(graph, METALCON_FILE);

		Random rand = new Random();
		int commonsComputeCounter = 0;
		for (int i = 0; i < 1000; i++) {
			int commons = testInCommons(graph, rand.nextInt(10000),
					rand.nextInt(10000), false);
			if (commons > -1) {
				commonsComputeCounter++;
			}
		}

		testInCommons(graph, 1, 2, true);

		// TestSingleNodePreprocessor();
	}

	public static int testInCommons(HaveInCommons graph, long from, long to,
			boolean verbose) {
		long start = System.nanoTime();
		long[] commons = graph.getCommonNodes(from, to);
		long end = System.nanoTime();

		if (commons != null) {
			// if (commons.length > 10 || end-start>5001E3)
			System.out.println("getCommonNodes needed: " + (end - start) / 1000
					+ " microseconds for " + commons.length + " commons");
			if (commons.length > 0) {
				if (verbose) {
					System.out.println("Common length: " + commons.length);
					for (long s : commons) {
						System.out.println(s);
					}
				}
				return commons.length;
			} else {
				return 0;
			}
		} else {
			return -1;
		}

	}

	private static void importGraph(HaveInCommons graphImpl, String dataFile) {
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
			if (graphImpl instanceof PersistentReadOptimized) {
				((PersistentReadOptimized) graphImpl).flush();
			}

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		long end = System.nanoTime();
		System.out.println("Imported in: " + (int) ((end - start) / 1E9f)
				+ "sec");
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
