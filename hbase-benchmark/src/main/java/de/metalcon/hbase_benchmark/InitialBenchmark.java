/**
 * @author Patrik Schmidt <patrik@visionhack.de>
 */

package de.metalcon.hbase_benchmark;

import java.lang.String;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.joda.time.DateTime;

public class InitialBenchmark {

	static HashMap<Long, String> mappedTitles = new HashMap<Long, String>();

	/**
	 * @param args
	 * 
	 *            Consider splitting up your input files or increasing the
	 *            memory of the jvm to 2-3 GiB
	 */
	public static void main(String[] args) {

		File links = new File("/home/pschmidt/hacking/rawdata/xaa");
		File titles = new File("/home/pschmidt/hacking/rawdata/wiki-titles.csv");
		File output = new File("/home/pschmidt/hacking/rawdata/output.tsv");
		ArrayList<Long> userIds = new ArrayList<Long>();
		
		init(output);

		try {
			mappedTitles = parseCSVtoHashmap(titles);
			userIds = userIdsToArray(links);

			Recommender recommender = initItembasedRecommender(links);
			//				 			  uid	, numOfRecommendations)
			doRecommendation(recommender, userIds, 10, output);
		} catch (IOException e) {
			// maybe file not found
			e.printStackTrace();
		} catch (TasteException te) {
			te.printStackTrace();
		}
	}

	private static void init(File file){
		DateTime date = new DateTime();
		
		try{
			if(!file.exists()){
				file.createNewFile();
				System.out.println("File created" + file.getAbsolutePath());
			}
			BufferedWriter br = new BufferedWriter(new FileWriter(file, true));
			
			System.out.println(file.getAbsolutePath());
			
			br.append("Created: " + date.toString() + "\n");
			br.append("itemid \t pagetitle \t recommendation1 \t recommendation2 \t recommendation3\t" +
					" recommendation4\t recommendation5\t recommendation6\t recommendation7\t" +
					" recommendation8\t recommendation9\t recommendation10" + "\n");
			br.flush();
			br.close();
		}
		catch (IOException e){
			System.out.println("Can execute: " + file.canExecute() + ", can write: " + file.canWrite());
			e.printStackTrace();
		}
	}
	
	/**
	 * Initialization of item-based recommender based on Loglikelihood
	 * similarity
	 * 
	 * @param file
	 *            input file with graph-like data (userid, itemid) in CSV format
	 * @return Recommender initialized recommender object
	 * @throws IOException
	 */
	private static Recommender initItembasedRecommender(File file)
			throws IOException {
		FileDataModel dataModel = new FileDataModel(file);
		ItemSimilarity similarity = new LogLikelihoodSimilarity(dataModel);

		ItemBasedRecommender recommender = new GenericItemBasedRecommender(
				dataModel, similarity);
		return recommender;
	}

	private static void doRecommendation(Recommender recommender,
			ArrayList<Long> userIds, int numRecommendations, File output)
			throws TasteException, IOException {
		List<RecommendedItem> recommendations;
		if (userIds.size() > 0) {
			
			//TODO: iterate over all ids
			for (long id = userIds.get(0); id < 100; id ++) {
				if(userIds.contains(id)){
				
					recommendations = recommender.recommend(id, numRecommendations);
					writeRecommendationsToFile(id, recommendations, true, output);
				}
					continue;
			}
		} else
			System.out.println("Bad graph data: Not enough userids");
	}

	/**
	 * 
	 * Puts all userIDs into Arraylist
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private static ArrayList<Long> userIdsToArray(File file)
			throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));

		ArrayList<Long> userIds = new ArrayList<Long>();

		String line = null;

		while ((line = br.readLine()) != null) {
			String[] tmp = (line.split(","));
			userIds.add((long) Integer.parseInt(tmp[0]));
		}
		br.close();
		return userIds;
	}

	/**
	 * 
	 * @param file
	 *            File containing key/value in csv format
	 * @return Map Hashmap containing key/value pairs
	 * @throws IOException
	 */
	private static HashMap<Long, String> parseCSVtoHashmap(File file)
			throws IOException {
		HashMap<Long, String> titleMap = new HashMap<Long, String>();

		BufferedReader br = new BufferedReader(new FileReader(file));

		String line = null;
		String[] element = null;

		try {
			while ((line = br.readLine()) != null) {
				element = line.split(",");
				if (element[0] != null && element[1] != null)
					titleMap.put(Long.parseLong(element[0]), element[1]);
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			br.close();
			System.out.println("Element[0]: " + element[0] + "\n");
			System.out.println("Int MAX_VALUE: " + Integer.MAX_VALUE);
			e.printStackTrace();
		}
		return titleMap;
	}

	private static String getTitleById(long key) {
		return mappedTitles.get(key);
	}

	private static void writeRecommendationsToFile(long itemId, List<RecommendedItem> recommendedItems, Boolean printToConsole, File file) {

		StringBuilder sb = new StringBuilder();
		
		sb.append(itemId + "\t" + getTitleById(itemId) + "\t");
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
			
		    System.out.println("RECOMMENDATION FOR ITEM ID: " + itemId + " --- Pagetitle:" + getTitleById(itemId)+ "\n");
			
		    for(RecommendedItem item: recommendedItems){
		    	
		    	System.out.println(item.toString());
		    	System.out.println(getTitleById(item.getItemID()));
		    	
		    	sb.append(getTitleById(item.getItemID()) + "\t");
		    }
		    sb.append("\n");
		    bw.append(sb.toString());
		    bw.flush();
		    bw.close();
		}

		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	private static void prettyPrint() {
		// TODO: Implement some pretty print routines
	}

}
