package de.metalcon.hbase_benchmark;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.hadoop.TasteHadoopUtils;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

import java.io.File;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

public class App {
	public static void main(String[] args) throws TasteException {

		File f = new File("/home/pschmidt/hacking/rawdata/xaa");
		File titles = new File("/home/pschmidt/hacking/rawdata/wiki-titles.csv");
		ArrayList<Integer> userId = new ArrayList<Integer>();

		FileReader fr;
		
		try {
			fr = new FileReader(titles);

			BufferedReader br = new BufferedReader(fr);

			String line = "";

			while ((line = br.readLine()) != null) {
				String[] item  = line.split(",");
				userId.add(Integer.parseInt(item[0]));
			}
			
			br.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		System.out.println("Importing Titles \n");

		if (f.exists())
			System.out.println("datei existiert");
		try {
			FileDataModel dataModel = new FileDataModel(f);
			ItemSimilarity itemSimilarity = new LogLikelihoodSimilarity(
					dataModel);
			ItemBasedRecommender recommender = new GenericItemBasedRecommender(
					dataModel, itemSimilarity);

			// recommender.recommend(userID, itemcount)
			for (int i = 1; i < 100; i++) {
				System.out.println("UserID: " + i);
				System.out.println("********************");
				List<RecommendedItem> recommendations = recommender.recommend(
						i, 10);

				for (RecommendedItem item : recommendations) {
					System.out.println(item);
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TasteException te) {
			System.out.println("TasteException");
			te.printStackTrace();
		}

	}
}
