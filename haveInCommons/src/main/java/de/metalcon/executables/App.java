package de.metalcon.executables;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;

import de.metalcon.haveInCommons.PersistentReadOptimized;
import de.metalcon.haveInCommons.RetrievalOptimized;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
//    	RetrievalOptimized r = new RetrievalOptimized();
    	PersistentReadOptimized r = new PersistentReadOptimized("plaingraph");
//    	r.putEdge("1", "2");
//    	r.putEdge("2", "3");
//    	r.putEdge("1", "4");
//    	r.putEdge("4", "3");

    	
    	System.out.println("go");
		long start = System.currentTimeMillis();
    	try {
			BufferedReader br = new BufferedReader(new FileReader("../data/ub.csv"));
			String line = "";
			int cnt = 0;
			while ((line = br.readLine())!= null){
				String[] values = line.split("\t");
				if (values.length!=2) continue;
				r.putEdge(values[0], values[1]);
//				r.putEdge(values[1], values[0]);
				cnt++;
				if (cnt %100 == 0){
					System.out.println(cnt + " edges added");
				}
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
    	for (String s:r.getCommonNodes("Metallica", "Ensiferum")){
    		System.out.println(s);
    	}
//    	long start, end; 
    	try {
    	BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
	    String s = "";
	    while (!s.equals("q")){
	    	s = bufferRead.readLine();
	    	start = System.nanoTime();
	    	String[] values = s.split(";");
	    	if (values.length!=2)continue;
	    	Set<String> set = r.getCommonNodes(values[0], values[1]);
	    	end = System.nanoTime();
	    	for (String res:set){
	    		System.out.println(res);
	    	}
	    	int x = set.size();
	    	System.out.println((end - start) + " nano seconds for lookup of " + x + "  elements");
	    }
    	}catch(Exception e){}
    }
}
