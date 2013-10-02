package de.metalcon.sdd;

import static org.fusesource.leveldbjni.JniDBFactory.factory;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import de.metalcon.common.Muid;

//import com.mongodb.BasicDBObject;
//import com.mongodb.DB;
//import com.mongodb.DBCollection;
//import com.mongodb.DBObject;
//import com.mongodb.MongoClient;

public class TryOut {

    public static void t1() throws UnknownHostException {
//        MongoClient  mongo   = new MongoClient("localhost", 27017);
//        DB           db      = mongo.getDB("staticdata");
//        DBCollection content = db.getCollection("content");
//        
//        DBObject lukas = new BasicDBObject();
//        lukas.put("_id", "9568b0efb6ca8e26:symbol");
//        lukas.put("firstname", "Lukas");
//        lukas.put("lastname",  "Schmelzeisen");
//        lukas.put("name",      "Lukas Schmelzeisen");
//        lukas.put("url",       "/person/Lukas+Schmelzeisen");
//        content.remove(new BasicDBObject("_id", "9568b0efb6ca8e26:symbol"));
//        content.insert(lukas);
//        DBObject ensiferum_genre = new BasicDBObject();
//        ensiferum_genre.put("_id", "d707470e91bb4e7:symbol");
//        ensiferum_genre.put("id", "d707470e91bb4e7");
//        ensiferum_genre.put("name", "Folk Metal");
//        ensiferum_genre.put("url", "/genre/Folk+Metal");
//        content.insert(ensiferum_genre);
//        DBObject ensiferum = new BasicDBObject();
//        ensiferum.put("_id", "2f364c13c0114e16:line");
//        ensiferum.put("id", "2f364c13c0114e16");
//        ensiferum.put("name", "Ensiferum");
//        ensiferum.put("url",  "/music/Ensiferum");
//        ensiferum.put("foundation", "1995");
//        ensiferum.put("genre", ensiferum_genre);
//        content.insert(ensiferum);
    }
    
    public static void t2() throws IOException {
        Options options = new Options();
        options.createIfMissing(true);
        DB db = factory.open(new File("/usr/share/sdd"), options);
        try {
//            Map<String, Object> lukas = new LinkedHashMap<String, Object>();
//            lukas.put("id", "9568b0efb6ca8e26");
//            lukas.put("firstname", "Lukas");
//            lukas.put("lastname", "Schmelzeisen");
//            lukas.put("name", "Lukas Schmelzeisen");
//            lukas.put("url", "/person/Lukas+Schmelzeisen");
//            db.put(bytes("9568b0efb6ca8e26:symbol"),
//                    bytes(JSONValue.toJSONString(lukas)));
//
//            Map<String, Object> ensiferum_genre = new LinkedHashMap<String, Object>();
//            ensiferum_genre.put("id", "d707470e91bb4e7a");
//            ensiferum_genre.put("name", "Folk Metal");
//            ensiferum_genre.put("url", "/genre/Folk+Metal");
//            db.put(bytes("d707470e91bb4e7a:symbol"),
//                    bytes(JSONValue.toJSONString(ensiferum_genre)));
//
//            Map<String, Object> ensiferum = new LinkedHashMap<String, Object>();
//            ensiferum.put("id", "2f364c13c0114e16");
//            ensiferum.put("name", "Ensiferum");
//            ensiferum.put("url", "/music/Ensiferum");
//            ensiferum.put("foundation", "1995");
//            ensiferum.put("genre", ensiferum_genre);
//            db.put(bytes("2f364c13c0114e16:line"),
//                    bytes(JSONValue.toJSONString(ensiferum)));
        } finally {
            db.close();
        }
    }
    
    public static void t3() {
//        Neo4jGraph neo4j = new Neo4jGraph("/usr/share/sdd/neo4j");
//        neo4j.createKeyIndex("muid", Vertex.class);
        
//        Vertex w = neo4j.addVertex(null);
//        w.setProperty("muid", "2f364c13c0114e16");
//        neo4j.commit();
        
//        for (Vertex v : neo4j.getVertices("muid", "2f364c13c0114e16")) {
//            System.out.println(v);
//        }
//        
//        neo4j.shutdown();
    }
    
    public static void t4() {
        GraphDatabaseService neo4j = new GraphDatabaseFactory()
                .newEmbeddedDatabase("/usr/share/sdd/neo4j");
        neo4j.shutdown();
    }
    
    @SuppressWarnings("deprecation")
    public static void t5() {
        Map<Muid, Node> muidIndex = new HashMap<Muid, Node>(10*1000*1000);
        
        GraphDatabaseService graphDb = new GraphDatabaseFactory()
                .newEmbeddedDatabase("/usr/share/sdd/neo4j");
        
//        Index<Node> index = neo4j.index().forNodes("muidIndex");
        
        long t = System.currentTimeMillis();
        
        Transaction tx = graphDb.beginTx();
        try {
            int i = 0;
            for (Node m : graphDb.getAllNodes()) {
//                m.delete();
//            }
            
            
//            for (int i = 0; i != 10 * 1000 * 1000; ++i) {
//                Node n = neo4j.createNode();
//                n.setProperty("muid", i);
                muidIndex.put(new Muid(""+i), m);
                
                if (i % 10000 == 0) {
                    System.out.print(i + ":");
                    System.out.println(Runtime.getRuntime().totalMemory());
                    
//                    tx.success();
//                    tx.finish();
//                    tx = neo4j.beginTx();
                }
                ++i;
            }
            
            tx.finish();
        } finally {
        }
        
        System.out.println(System.currentTimeMillis() - t);
        
        graphDb.shutdown();
    }
    
    public static void main(String[] args) throws IOException,
            UnknownHostException {
//        t1();
//        t2();
//        t3();
//        t4();
        t5();
    }
    
}
