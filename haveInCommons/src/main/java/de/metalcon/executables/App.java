package de.metalcon.executables;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

import de.metalcon.haveInCommons.HaveInCommons;
import de.metalcon.haveInCommons.LuceneRead;
import de.metalcon.haveInCommons.RetrievalOptimized;
import de.metalcon.haveInCommons.RetrievalOptimizedLevelDB;
import de.metalcon.haveInCommons.RetrievalOptimizedSSDB;

/**
 * Hello world!
 * 
 */
public class App {
	private static void run(HaveInCommons r) {

		// PersistentReadOptimized r = new
		// PersistentReadOptimized("plaingraph");
		// r.putEdge("1", "2");
		// r.putEdge("2", "3");
		// r.putEdge("1", "4");
		// r.putEdge("4", "3");

		long start = System.currentTimeMillis();
		try {
			BufferedReader br = new BufferedReader(new FileReader("../data/ub.csv"));
			String line = "";
			int cnt = 0;
			while ((line = br.readLine()) != null) {
				String[] values = line.split("\t");
				if (values.length != 2)
					continue;
				r.putEdge(values[0], values[1]);
				r.putEdge(values[1], values[0]);
				cnt++;
//				if (cnt % 100 == 0) {
//					System.out.println(cnt + " edges added");
//				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		long end = System.currentTimeMillis();
		System.out.println("done indexing needed: " + (end - start) + " milliseconds");

		if (r instanceof LuceneRead) {
			start = System.currentTimeMillis();
			r.putFinished();
			end = System.currentTimeMillis();
			System.out.println("Indexing Lucene needed: " + (end - start) + " milliseconds");
		}

		start = System.nanoTime();
		Set<String> commons = r.getCommonNodes("1", "2");
		end = System.nanoTime();
		System.out.println("getCommonNodes needed: " + (end - start) / 1000 + " microseconds");

		if (commons != null && commons.size() > 0) {
			for (String s : commons) {
				System.out.println(s);
			}
		} else {
			System.out.println("Metallica and Ensiferum have nothing in common!");
		}

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

	public static void main(String[] args) {
		HaveInCommons r;
		System.out.println("Lucene:");
		r = new LuceneRead();
		run(r);

		System.out.println("RetrievalOptimized:");
		r = new RetrievalOptimized();
		run(r);

		System.out.println("RetrievalOptimizedSSDB:");
		r = new RetrievalOptimizedSSDB();
		run(r);

		System.out.println("RetrievalOptimizedLevelDB:");
		r = new RetrievalOptimizedLevelDB();
		run(r);

	}
}
