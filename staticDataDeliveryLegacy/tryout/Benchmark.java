package de.metalcon.tryout;
import static org.fusesource.leveldbjni.JniDBFactory.asString;
import static org.fusesource.leveldbjni.JniDBFactory.bytes;
import static org.fusesource.leveldbjni.JniDBFactory.factory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.tooling.GlobalGraphOperations;

import de.metalcon.common.Muid;
import de.metalcon.sdd.config.FileConfig;


public class Benchmark {

    private static FileConfig config;
    
    private static DB jsonDb;
    
    private static GraphDatabaseService entityGraph;
    
    private static Map<Muid, Node> entityGraphMuidIndex;
    
    public static void main(String[] args) {
        System.out.println("--------------------");
        System.out.println("Loading:");
      
        System.out.println("Loading config...");
        config = new FileConfig();
        
        System.out.println("Loading jsonDb (LevelDB)...");
        Options options = new Options();
        options.createIfMissing(true);
        try {
            jsonDb = factory.open(new File(config.getLeveldbPath()),
                                  options);
        } catch(IOException e) {
            throw new RuntimeException();
        }
        
        System.out.println("Loading entityGraph (Neo4j)...");
        entityGraph = new GraphDatabaseFactory().newEmbeddedDatabase(config
                .getNeo4jPath());
        entityGraphMuidIndex = new HashMap<Muid, Node>();
        for (Node node : GlobalGraphOperations.at(entityGraph).getAllNodes())
            if (node.hasProperty("muid"))
                entityGraphMuidIndex.put(
                        new Muid((String) node.getProperty("muid")), node);
        
        int count = 0;
        long t = System.currentTimeMillis();
        for (int i = 0; i != 100; ++i) {
            for (Muid key : entityGraphMuidIndex.keySet()) {
                String a;
                if ((Math.random() + 0.5) > 1)
                    a = asString(jsonDb.get(bytes(key.toString() + ":all")));
                else
                    a = asString(jsonDb.get(bytes(key.toString() + ":title")));
                ++count;
                if (count % 10000 == 0)
                    System.out.println(a);
            }
            System.out.println(i);
        } 
        long tt = System.currentTimeMillis() - t;
        System.out.println(count);
        System.out.println(count / tt);
    }
}
