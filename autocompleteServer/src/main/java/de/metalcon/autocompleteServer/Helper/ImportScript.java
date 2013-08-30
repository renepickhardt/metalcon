/**
 * 
 */
package de.metalcon.autocompleteServer.Helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.codec.binary.Base64;

/**
 * @author Rene Pickhardt
 *
 */
public class ImportScript {

	
	public static void loadFilesToIndex(boolean parseBands, SuggestTree suggestTree, HashMap<String, String> imageIndex){
		//boolean parseBand = true;
		String fileName = null;
		String path = "/home/rpickhardt/data/metalconimages/";
		if (parseBands){
			fileName = path + "Band.csv";
		}
		else {
			fileName = path + "Album.csv";
		}
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(fileName));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		String line = null;
		int max = -1;
		try {
			while ((line = br.readLine())!=null){

				if (parseBands){
					parseBand(line.toLowerCase(), 1300, imageIndex, suggestTree);
				}else{
					parseAlbum(line.toLowerCase(), 560, imageIndex, suggestTree);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param line
	 * @param suggestTree 
	 * @param imageIndex 
	 * @param i
	 */
	private static void parseAlbum(String line, int max, HashMap<String, String> imageIndex, SuggestTree suggestTree) {
		String[] values = line.split("\t");
		if (values.length!=4) return;
		String name = values[0];
		String imageKey = values[1];
		String band = values[3];
		Integer score = Integer.parseInt(values[2])*10000/max;
		System.out.println("parsing record");
		buildHttpRequest(name, imageKey, name, score, imageIndex, suggestTree);
		buildHttpRequest(band + " " + name, imageKey, band + " " + name, score, imageIndex, suggestTree);
	}

	/**
	 * @param line
	 * @param suggestTree 
	 * @param imageIndex 
	 * @param i 
	 */
	private static void parseBand(String line, int max, HashMap<String, String> imageIndex, SuggestTree suggestTree) {
		String[] values = line.split("\t");
		if (values.length!=4) return;
		String name = values[0];
		String imageKey = values[1];
		String url = values[2];
		Integer score = Integer.parseInt(values[3])*10000/max;
		buildHttpRequest(name, imageKey, url, score, imageIndex, suggestTree);		
	}

	/**
	 * @param name
	 * @param imageKey
	 * @param url
	 * @param score
	 * @param suggestTree 
	 * @param imageIndex 
	 */
	private static void buildHttpRequest(String name, String imageKey,
			String key, int score, HashMap<String, String> imageIndex, SuggestTree suggestTree) {
		String path = "/home/rpickhardt/data/metalconimages/images/";
		File f = new File ( path + imageKey + "60.jpg");
		suggestTree.put(name, score, key);
		if (!f.exists()){
			System.out.println("cant finde image " + imageKey + " entitiy " + name);
			return;
		}

		if (f == null )return;
		FileInputStream fileReader = null;
		try {
			fileReader = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		byte[] cbuf = new byte[(int)f.length()];
		try {
			fileReader.read(cbuf, 0, (int) f.length());
			fileReader.close();
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		byte[] base64EncodedImage = Base64.encodeBase64(cbuf);
		String result = new String(base64EncodedImage);
		result = "data:image/jpg;base64," + result;
		imageIndex.put(key, result);
	}

}
