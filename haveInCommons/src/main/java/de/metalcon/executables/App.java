package de.metalcon.executables;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import de.metalcon.haveInCommons.HaveInCommons;
import de.metalcon.haveInCommons.PersistentReadOptimized;
import de.metalcon.haveInCommons.SingleNodePreprocessorNeo4j;

/**
 * Hello world!
 * 
 */
public class App {

    private static final String DATA_DIR = "/home/hartmann/Desktop/MetalconDev/data/";
    private static final String METALCON_FILE  = DATA_DIR + "metalon-all-hashed.csv";
    private static final String WIKIPEDIA_FILE = "../data/";
    private static final String SMALL_FILE     = "../data/";
    public static final int BATCH_SIZE = 100000;

    private static HaveInCommons graph;

    public static void main(String[] args) {

        graph = new PersistentReadOptimized();

        importGraph(graph, METALCON_FILE);

        TestSingleNodePreprocessor();

    }

    private static void importGraph(HaveInCommons graphImpl, String dataFile) {
        long start = System.nanoTime();
        long last  = System.nanoTime();
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
                    graphImpl.putEdge(Long.parseLong(values[0]), Long.parseLong(values[1]));
                    edgeCount++;

                    graphImpl.putEdge(Long.parseLong(values[1]), Long.parseLong(values[0]));
                    edgeCount++;
                } else {
                    for (int i = 1; i < values.length; i++) {
                        graphImpl.putEdge(Long.parseLong(values[0]), Long.parseLong(values[i]));
                        edgeCount++;
                    }
                }
                nodeCount++;

                if (edgeCount % BATCH_SIZE == 0) {
                    // print Statistics
                    now = System.nanoTime();

                    System.out.println(
                        "#nodes=" + nodeCount + "\t" +
                        "#edges=" + edgeCount + "\t" +
                        "time total=" +  (long) ((now - start) / 1E9f) + "sec.\t" +
                        "batch time=" +  (long) ((now - last) / 1E6f)  + "msec."
                    );

                    System.err.println(
                            (int) (BATCH_SIZE * 2 * 1E9f / (now - last))  + " edges/sec");

                    last = now;
                }
            }
            graphImpl.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.nanoTime();
        System.out.println("Imported in: " + (int) ((end - start)*1E9f) + "sec");
    }

	private static void TestSingleNodePreprocessor() {
		System.out.println("SingleNodePreprocessorNeo4j:");

		SingleNodePreprocessorNeo4j processor = new SingleNodePreprocessorNeo4j("neo4j-metalconAll");
		
		processor.generateIndex(1);
		processor.print(1);
		
		for (int i = 1; i < 100; ++i) {
			long start = System.nanoTime();
			if (processor.generateIndex(i)) {
				long end = System.nanoTime();
				
				int max = processor.print(i);
				System.out.println((end - start) / 1E6f + " milliseconds, "+max+ " maximum elements");
			}
		}
	}

}
