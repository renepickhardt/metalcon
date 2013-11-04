package de.metalcon.tryout;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class Neo4j {

    public static void main(String[] args) throws IOException {
        Integer[] ia = new Integer[]{4,5,6};
        
        Path tmpDir = Files.createTempDirectory("neo4j");
        GraphDatabaseService graph = new GraphDatabaseFactory()
                .newEmbeddedDatabase(tmpDir.toString());
        try {
            Node n;
            
            Transaction tx = graph.beginTx();
            try {
                n = graph.createNode();
                n.setProperty("ia", ia);
                tx.success();
            } finally {
                tx.finish();
            }
            
            for (int i : (Integer[]) n.getProperty("ia"))
                System.out.print(i + ",");
            System.out.println();
        } finally {
            if (graph != null)
                graph.shutdown();
        }
    }
    
}
