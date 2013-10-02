package de.metalcon.sdd.server;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.tooling.GlobalGraphOperations;

import com.google.common.io.Files;

import de.metalcon.common.Muid;
import de.metalcon.sdd.Detail;
import de.metalcon.sdd.entity.Entity;

public class ServerTest {
    
    private class TestEntity extends Entity {
        
        Set<Muid> ids;
        
        public TestEntity(Server server, String id) {
            super(server);
            setId(new Muid(id));
            ids = new HashSet<Muid>();
        }
        
        public void addDependingOn(Muid id) {
            ids.add(id);
        }

        @Override
        public Set<Muid> getDependingOn() {
            return ids;
        }
        
        @Override
        public String getType() {
            return "TestEntity";
        }

        @Override
        public void loadFromJson(String json) {
        }

        @Override
        public void loadFromCreateParams(Map<String, String[]> params) {
        }

        @Override
        public void loadFromUpdateParams(Map<String, String[]> params) {
        }

        @Override
        protected void generateJson() {
        }
        
        @Override
        public String getJson(Detail detail) {
            return "";
        }
        
    }

    private Server server;
    
    private File kvDir;
    
    private File graphDir;
    
    @Before
    public void initialize() {
        System.out.println("--- initialize start");
        
        kvDir    = Files.createTempDir();
        graphDir = Files.createTempDir();
        
        checkGraph(graphDir.getAbsolutePath());
        
        ServerConfig serverConfig = new ServerConfig(
                kvDir.getAbsolutePath(),
                graphDir.getAbsolutePath());
        server = new Server(serverConfig);
        server.start();
        
        System.out.println("--- initialize end");
    }
    
    @After
    public void uninitialize() {
        System.out.println("--- uninitialize start");
        
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }
        server.stop();
        
        checkGraph(graphDir.getAbsolutePath());
        
        System.out.println("--- uninitialize end");
    }
    
    private void checkGraph(String graphDir) {
        GraphDatabaseService graphDb =
                new GraphDatabaseFactory().newEmbeddedDatabase(graphDir);
        for (Node node : GlobalGraphOperations.at(graphDb).getAllNodes())
            System.out.println(nodeToString(node));
        for (Relationship rel : GlobalGraphOperations.at(graphDb)
                .getAllRelationships())
            System.out.println(
                    nodeToString(rel.getStartNode()) +
                    "--" + rel.getType().toString() + "--> " +
                    nodeToString(rel.getEndNode()));
        graphDb.shutdown();
    }
    
    private String nodeToString(Node node) {
        String result = "Node (" + node.getId() + ") ";
        if (node.hasProperty("muid"))
            result += "muid=\"" + node.getProperty("muid") + "\" ";
        return result;
    }
    
    @Test
    public void testWriteEntity() {
        TestEntity a = new TestEntity(server, "ffda4ab022cb68c1");
        TestEntity b = new TestEntity(server, "a1ba4a0d2bb46d5a");
        TestEntity c = new TestEntity(server, "8b68561b79b609a9");
        b.addDependingOn(a.getId());
        c.addDependingOn(a.getId());
        server.writeEntity(a);
        server.writeEntity(b);
        server.writeEntity(c);
        c.addDependingOn(b.getId());
        server.writeEntity(c);
    }
    
}