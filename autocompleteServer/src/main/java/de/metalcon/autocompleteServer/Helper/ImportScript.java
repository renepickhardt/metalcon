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
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.codec.binary.Base64;

/**
 * @author Rene Pickhardt
 * 
 */
public class ImportScript {

	public static void loadServerWiki(SuggestTree suggestTree,
			HashMap<String, String> imageIndex,HashMap<String, String> hitMap) {
		// articlswiththumbnail.tsv: ID \t Lemma \t image path
		// Pagerank: wiki-pr.tsv ID \t prvalue
		// path: /home/metalroot/
		// imagepath:
		// http://upload.wikimedia.org/wikipedia/commons/thumb/9/9e/Aki_Kaurism%C3%A4ki.jpg/92px-Aki_Kaurism%C3%A4ki.jpg

		BufferedReader br = null;

		String path = "/var/lib/datasets/rawdata/commons/wiki-pr.tsv";
		HashMap<Integer, Integer> prValues = new HashMap<Integer, Integer>();
		// HashMap<String, Integer> prTitles = new HashMap<String, Integer>();

		try {
			br = new BufferedReader(new FileReader(path));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				String[] values = line.split("\t");
				Integer id = Integer.parseInt(values[0]);
				Double pr = Double.parseDouble(values[1]);
				double d = pr * 1000. * 1000. * 10000.;
				prValues.put(id, (int) d);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		
		String imagePath = "/var/lib/datasets/rawdata/commons/IMAGES/";
		try {
			br = new BufferedReader(new FileReader(
					"/var/lib/datasets/rawdata/commons/articlswiththumbnail.tsv"));
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		line = null;
		try {
			while ((line = br.readLine()) != null) {
				String[] values = line.split("\t");
				if (values.length != 3)
					continue;

				String id = values[0];
				String title = values[1];
				String key = title.replace(" ", "_");
				String tmp = values[2];
				values = tmp.replace("http://upload.wikimedia.org/wikipedia/commons/thumb/", "").split("/");
				if (values.length!=4)
					continue;
				String image = values[0] + "/" + values[1] + "/" + values[3];

				File f = new File(imagePath + image);
				if (!f.exists()) {
					continue;
				}

				if (f == null)
					continue;
				FileInputStream fileReader = null;
				try {
					fileReader = new FileInputStream(f);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				byte[] cbuf = new byte[(int) f.length()];
				try {
					fileReader.read(cbuf, 0, (int) f.length());
					fileReader.close();
				} catch (IOException e1) {
					e1.printStackTrace();
					continue;
				}
				byte[] base64EncodedImage = Base64.encodeBase64(cbuf);
				String result = new String(base64EncodedImage);
				result = "data:image/jpg;base64," + result;
				imageIndex.put(key, result);
				
				title = title.replace("_", " ").toLowerCase();
				title = title.replace("-", " ");
				Integer pr = prValues.get(Integer.parseInt(id));
				if (pr != null && pr != null && key != null) {
					suggestTree.put(title, pr, key);
					hitMap.put(title,key);
					values = title.split(" ");
					if (values.length>1){
						generatePermutations(values, suggestTree, title, pr, key);
					}
					//TODO: if memory available also save more entries to the suggest tree
				}
				

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * @param w
	 * @param suggestTree
	 * @param title
	 * @param pr
	 * @param key
	 */
	private static void generatePermutations(String[] w,
			SuggestTree suggestTree, String title, Integer pr, String key) {
		if (w[w.length-1].startsWith("(")){
			w = Arrays.copyOf(w, w.length-1);
		}
		if (w.length==1)return;
		for (int i = 0 ; i< w.length; i++){
			suggestTree.put(w[i]+"###"+title, pr/2, key);
		}
		if (w.length==2){			
			suggestTree.put(w[1] + " " + w[0]+"###"+title, pr/2, key);
		}
		if (w.length==3){
			suggestTree.put(w[2] + " " + w[1] + " " + w[0]+"###"+title, pr/6, key);
			suggestTree.put(w[2] + " " + w[0] + " " + w[1]+"###"+title, pr/6, key);
			suggestTree.put(w[0] + " " + w[2] + " " + w[1]+"###"+title, pr/6, key);
			suggestTree.put(w[1] + " " + w[2] + " " + w[0]+"###"+title, pr/6, key);
			suggestTree.put(w[1] + " " + w[0] + " " + w[2]+"###"+title, pr/6, key);
		}
		if (w.length==4){
			suggestTree.put(w[3] + " " + w[2] + " " + w[1] + " " + w[0]+"###"+title, pr/24, key);
			suggestTree.put(w[3] + " " + w[2] + " " + w[0] + " " + w[1]+"###"+title, pr/24, key);
			suggestTree.put(w[3] + " " + w[0] + " " + w[2] + " " + w[1]+"###"+title, pr/24, key);
			suggestTree.put(w[3] + " " + w[0] + " " + w[1] + " " + w[2]+"###"+title, pr/24, key);
			suggestTree.put(w[3] + " " + w[1] + " " + w[2] + " " + w[0]+"###"+title, pr/24, key);
			suggestTree.put(w[3] + " " + w[1] + " " + w[0] + " " + w[2]+"###"+title, pr/24, key);

			suggestTree.put(w[2] + " " + w[3] + " " + w[1] + " " + w[0]+"###"+title, pr/24, key);
			suggestTree.put(w[2] + " " + w[3] + " " + w[0] + " " + w[1]+"###"+title, pr/24, key);
			suggestTree.put(w[2] + " " + w[0] + " " + w[3] + " " + w[1]+"###"+title, pr/24, key);
			suggestTree.put(w[2] + " " + w[0] + " " + w[1] + " " + w[3]+"###"+title, pr/24, key);
			suggestTree.put(w[2] + " " + w[1] + " " + w[3] + " " + w[0]+"###"+title, pr/24, key);
			suggestTree.put(w[2] + " " + w[1] + " " + w[0] + " " + w[3]+"###"+title, pr/24, key);

			suggestTree.put(w[1] + " " + w[2] + " " + w[3] + " " + w[0]+"###"+title, pr/24, key);
			suggestTree.put(w[1] + " " + w[2] + " " + w[0] + " " + w[3]+"###"+title, pr/24, key);
			suggestTree.put(w[1] + " " + w[0] + " " + w[2] + " " + w[3]+"###"+title, pr/24, key);
			suggestTree.put(w[1] + " " + w[0] + " " + w[3] + " " + w[2]+"###"+title, pr/24, key);
			suggestTree.put(w[1] + " " + w[3] + " " + w[2] + " " + w[0]+"###"+title, pr/24, key);
			suggestTree.put(w[1] + " " + w[3] + " " + w[0] + " " + w[2]+"###"+title, pr/24, key);
			
			suggestTree.put(w[0] + " " + w[2] + " " + w[1] + " " + w[3]+"###"+title, pr/24, key);
			suggestTree.put(w[0] + " " + w[2] + " " + w[3] + " " + w[1]+"###"+title, pr/24, key);
			suggestTree.put(w[0] + " " + w[3] + " " + w[2] + " " + w[1]+"###"+title, pr/24, key);
			suggestTree.put(w[0] + " " + w[3] + " " + w[1] + " " + w[2]+"###"+title, pr/24, key);
			suggestTree.put(w[0] + " " + w[1] + " " + w[3] + " " + w[2]+"###"+title, pr/24, key);			
		}
		
	}

	public static void loadWikipedia(SuggestTree suggestTree,
			HashMap<String, String> imageIndex) {
		BufferedReader br = null;

		/**
		 * load images
		 */
		String imagePath = "/var/lib/datasets/rawdata/wikicommons/";
		try {
			br = new BufferedReader(
					new FileReader(imagePath + "imageIndex.tsv"));
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				String[] values = line.split("\t");
				if (values.length != 3)
					continue;

				String id = values[0];
				String title = values[1];
				String image = values[2];

				File f = new File(imagePath + image);
				if (!f.exists()) {
					System.out.println("cant finde image " + image
							+ " entitiy " + title);
					continue;
				}

				if (f == null)
					continue;
				FileInputStream fileReader = null;
				try {
					fileReader = new FileInputStream(f);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				byte[] cbuf = new byte[(int) f.length()];
				try {
					fileReader.read(cbuf, 0, (int) f.length());
					fileReader.close();
				} catch (IOException e1) {
					e1.printStackTrace();
					continue;
				}
				byte[] base64EncodedImage = Base64.encodeBase64(cbuf);
				String result = new String(base64EncodedImage);
				result = "data:image/jpg;base64," + result;
				imageIndex.put(title.replace(" ", "_"), result);

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String path = "/var/lib/datasets/rawdata/wikipedia/de/personlizedsearch/wiki-pr.tsv";
		HashMap<Integer, Integer> prValues = new HashMap<Integer, Integer>();
		// HashMap<String, Integer> prTitles = new HashMap<String, Integer>();

		try {
			br = new BufferedReader(new FileReader(path));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		line = null;
		try {
			while ((line = br.readLine()) != null) {
				String[] values = line.split("\t");
				Integer id = Integer.parseInt(values[0]);
				Double pr = Double.parseDouble(values[1]);
				double d = pr * 1000. * 1000. * 10000.;
				prValues.put(id, (int) d);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		path = "/var/lib/datasets/rawdata/wikipedia/de/personlizedsearch/wiki-titles.tsv";
		br = null;
		try {
			br = new BufferedReader(new FileReader(path));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		line = null;
		try {
			while ((line = br.readLine()) != null) {
				String[] values = line.split("\t");
				if (values.length != 2)
					continue;
				Integer id = Integer.parseInt(values[0]);
				String title = values[1].replace("_", " ").toLowerCase();

				Integer pr = prValues.get(id);
				if (pr != null && imageIndex.containsKey(values[1])) {
					suggestTree.put(title, pr, values[1]);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void loadFilesToIndex(boolean parseBands,
			SuggestTree suggestTree, HashMap<String, String> imageIndex) {
		// boolean parseBand = true;
		String fileName = null;
		String path = "/home/rpickhardt/data/metalconimages/";
		if (parseBands) {
			fileName = path + "Band.csv";
		} else {
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
			while ((line = br.readLine()) != null) {

				if (parseBands) {
					parseBand(line.toLowerCase(), 1300, imageIndex, suggestTree);
				} else {
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
	private static void parseAlbum(String line, int max,
			HashMap<String, String> imageIndex, SuggestTree suggestTree) {
		String[] values = line.split("\t");
		if (values.length != 4)
			return;
		String name = values[0];
		String imageKey = values[1];
		String band = values[3];
		Integer score = Integer.parseInt(values[2]) * 10000 / max;
		System.out.println("parsing record");
		buildHttpRequest(name, imageKey, name, score, imageIndex, suggestTree);
		buildHttpRequest(band + " " + name, imageKey, band + " " + name, score,
				imageIndex, suggestTree);
	}

	/**
	 * @param line
	 * @param suggestTree
	 * @param imageIndex
	 * @param i
	 */
	private static void parseBand(String line, int max,
			HashMap<String, String> imageIndex, SuggestTree suggestTree) {
		String[] values = line.split("\t");
		if (values.length != 4)
			return;
		String name = values[0];
		String imageKey = values[1];
		String url = values[2];
		Integer score = Integer.parseInt(values[3]) * 10000 / max;
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
			String key, int score, HashMap<String, String> imageIndex,
			SuggestTree suggestTree) {
		String path = "/home/rpickhardt/data/metalconimages/images/";
		File f = new File(path + imageKey + "60.jpg");
		suggestTree.put(name, score, key);
		if (!f.exists()) {
			System.out.println("cant finde image " + imageKey + " entitiy "
					+ name);
			return;
		}

		if (f == null)
			return;
		FileInputStream fileReader = null;
		try {
			fileReader = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		byte[] cbuf = new byte[(int) f.length()];
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
