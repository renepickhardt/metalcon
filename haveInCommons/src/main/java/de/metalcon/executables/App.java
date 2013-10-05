package de.metalcon.executables;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import de.metalcon.haveInCommons.HaveInCommons;
import de.metalcon.haveInCommons.LuceneRead;
import de.metalcon.haveInCommons.PersistentReadOptimized;
import de.metalcon.haveInCommons.SingleNodePreprocessorNeo4j;

/**
 * Hello world!
 * 
 */
public class App {
	private static final String DataFile = "../data/metalcon-all-hashed.csv";

	private static void run(HaveInCommons r) {
		long start = System.currentTimeMillis();
		try {
			// BufferedReader br = new BufferedReader(new
			// FileReader("/var/lib/datasets/rawdata/wikipedia/de/personlizedsearch/wiki-links.tsv"));
			BufferedReader br = new BufferedReader(new FileReader(DataFile));
			String line = "";
			long cnt = 0;
			long nodecnt = 0;
			while ((line = br.readLine()) != null) {
				String[] values = line.split("\\s");
				if (values.length < 2) {
					continue;
				} else if (values.length == 2) {
					r.putEdge(Long.parseLong(values[0]),
							Long.parseLong(values[1]));
					cnt++;

//					r.putEdge(Long.parseLong(values[1]),
//							Long.parseLong(values[0]));
//					cnt++;
				} else {
					for (int i = 1; i < values.length; i++) {
						r.putEdge(Long.parseLong(values[0]),
								Long.parseLong(values[i]));
						cnt++;

					}
				}
				nodecnt++;
				if (nodecnt % 500 == 0) {
					long tmp = System.currentTimeMillis();
					System.out.println(nodecnt + " nodes processed \t\t" + cnt
							+ " edges added \t time since start "
							+ (tmp - start));
					System.err.println(cnt * 1000 / (tmp - start)
							+ " edges / sec");
					// testInCommons(r,"1", "2");
				}
			}
			r.flush();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		long end = System.currentTimeMillis();
		System.out.println("done indexing needed: " + (end - start)
				+ " milliseconds");

		if (r instanceof LuceneRead) {
			start = System.currentTimeMillis();
			// r.putFinished();
			end = System.currentTimeMillis();
			System.out.println("Indexing Lucene needed: " + (end - start)
					+ " milliseconds");
		}

		// testInCommons(r, "1", "2");

		// try {
		// BufferedReader bufferRead = new BufferedReader(new
		// InputStreamReader(System.in));
		// String s = "";
		// while (s != null && !s.equals("q")) {
		// System.out.println("Please enter two band names separeted by ';':");
		// s = bufferRead.readLine();
		// start = System.nanoTime();
		// String[] values = s.split(";");
		// if (values.length != 2)
		// continue;
		// Set<String> set = r.getCommonNodes(values[0], values[1]);
		// if (set != null) {
		// end = System.nanoTime();
		// for (String res : set) {
		// System.out.println(res);
		// }
		// int x = set.size();
		// System.out.println((end - start) / 1000 +
		// " micro seconds for lookup of " + x + "  elements");
		// } else {
		// System.out.println("They have nothing in common!");
		// }
		// }
		// } catch (Exception e) {
		// }
	}

	public static void testInCommons(HaveInCommons r, long from, long to) {
		long start = System.nanoTime();
		long[] commons = r.getCommonNodes(from, to);
		long end = System.nanoTime();
		// System.out.println("getCommonNodes needed: " + (end - start) / 1000 +
		// " microseconds");
		//
		// if (commons != null && commons.length > 0) {
		// System.out.println(commons.length);
		// for (long s : commons) {
		// System.out.println(s);
		// }
		// } else {
		// System.out.println("Metallica and Ensiferum have nothing in common!");
		// }

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
				System.out.println((end - start) / 1E6f + " milliseconds, "+max+ " maximum elements");
			}
		}
	}

	public static void main(String[] args) {
		HaveInCommons r;
		// System.out.println("Lucene:");
		// r = new LuceneRead();
		// run(r);
		//
		// System.out.println("RetrievalOptimized:");
		// r = new RetrievalOptimized();
		// run(r);
		//
		// System.out.println("RetrievalOptimizedSSDB:");
		// r = new RetrievalOptimizedSSDB();
		// run(r);

		// System.out.println("RetrievalOptimizedLevelDB:");
		// r = new RetrievalOptimizedLevelDB();
		// run(r);

//		System.out.println("Neo4j:");
//		r = new PersistentReadOptimized();
//		run(r);

		// System.out.println("JUNG Graph in memory:");
		// r = new NormailzedRetrieval();
		// run(r);

		// testInCommons(r, 2, 3);
		// System.exit(0);
		// System.out.println("RetrievalOptimizedLevelDB:");
		// r = new NormailzedRetrieval();
		// run(r);

		 TestSingleNodePreprocessor();

		//
		// int count = 0;
		// long start = System.nanoTime();
		// for (int i = 99500; i < 100000; i++) {
		// for (int j = 99500; j < 100000; j++) {
		// if (i == j)
		// continue;
		// try {
		// testInCommons(r, i, j);
		// } catch (Exception e) {
		// continue;
		// }
		// count++;
		// if (count % 10 == 0) {
		// System.out.println("\t\t" + count);
		// }
		// }
		// }
		// long end = System.nanoTime();
		// System.out.println((end - start) / (1000 * 1000) + " milliseconds");
		// System.out.println((end - start) / (1000 * count)
		// + " microsec per operation");

	}
}
