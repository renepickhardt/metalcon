package de.metalcon.sdd.server;

import static de.metalcon.sdd.entity.EntityByType.newEntityByType;
import static org.fusesource.leveldbjni.JniDBFactory.asString;
import static org.fusesource.leveldbjni.JniDBFactory.bytes;
import static org.fusesource.leveldbjni.JniDBFactory.factory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.WriteBatch;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.tooling.GlobalGraphOperations;

import de.metalcon.common.Muid;
import de.metalcon.sdd.Detail;
import de.metalcon.sdd.IdDetail;
import de.metalcon.sdd.entity.Entity;
import de.metalcon.sdd.error.ServerDetailNoneSddError;
import de.metalcon.sdd.error.ServerLevelDbBatchCloseSddError;
import de.metalcon.sdd.error.ServerLevelDbCloseSddError;
import de.metalcon.sdd.error.ServerLevelDbInitializationSddError;
import de.metalcon.sdd.request.Request;

public class Server implements ServletContextListener {

    private static enum GraphRelTypes implements RelationshipType {
        DEPENDS
    }
    
    private ServerConfig config;
    
    private DB keyvalueDb;
    
    private GraphDatabaseService graphDb;
    
    Map<String, Node> muidIndex;
    
    private BlockingQueue<Request> queue;

    private Worker worker;
    
    public Server() {
        config = new ServerConfig();
    }
    
    public Server(ServerConfig config) {
        this.config = config;
    }
    
    public void start() {
        Options options = new Options();
        options.createIfMissing(true);
        try {
            keyvalueDb = factory.open(new File(config.getKvDbPath()), options);
        } catch (IOException e) {
            throw new ServerLevelDbInitializationSddError();
        }
        
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(
                config.getGraphDbPath());
        
        muidIndex = new HashMap<String, Node>();
        
        for (Node node : GlobalGraphOperations.at(graphDb).getAllNodes()) {
            if (node.hasProperty("muid"))
                muidIndex.put((String) node.getProperty("muid"), node);
        }
        
        queue = new LinkedBlockingQueue<Request>();
        worker = new Worker(queue);
        worker.start();
    }

    public void stop() {
        worker.stop();
        worker.waitForShutdown();
        
        graphDb.shutdown();
        
        try {
            keyvalueDb.close();
        } catch (IOException e) {
            throw new ServerLevelDbCloseSddError();
        }
    }
    
    public void waitUntilQueueEmpty() {
        try {
            while (!queue.isEmpty()) {
                System.out.println(queue.size());
                Thread.sleep(100);
            }
            Thread.sleep(1000);
        } catch(InterruptedException e) {
            // TODO: handle this
            throw new RuntimeException();
        }
    }
    
    public boolean addRequest(Request request) {
        return queue.offer(request);
    }

    /**
     * @return Returns null if entity does not exist in database.
     */
    public String readEntity(IdDetail idDetail) {
        if (idDetail.getDetail() == Detail.NONE)
            throw new ServerDetailNoneSddError(idDetail);
        
        return asString(keyvalueDb.get(bytes(idDetail.toString())));
    }
    
    public void writeEntity(Entity entity) {
        Muid id = entity.getId();
//        System.out.println("writeEntity(" + id.toString() + ")");
        
        updateDependingOn(entity);
        updateEntity(entity);
        updateDependencies(muidIndex.get(id.toString()));
    }

    private void updateDependingOn(Entity entity) {
        Muid id = entity.getId();
        Set<Muid> dependingOn = entity.getDependingOn();
        String idString = id.toString();
        
        Node entityNode = null;
            
        Transaction tx = graphDb.beginTx();
        try {
            Set<Muid> remainingDependingOn = null;
            
            if (!muidIndex.containsKey(idString)) {
                entityNode = graphDb.createNode();
                entityNode.setProperty("muid", idString);
                entityNode.setProperty("type", entity.getType());
                muidIndex.put(idString, entityNode);
                
                remainingDependingOn = dependingOn;
            } else {
                entityNode = muidIndex.get(idString);
                
                remainingDependingOn = new HashSet<Muid>();
                remainingDependingOn.addAll(dependingOn);
                
                for (Relationship rel : entityNode.getRelationships(
                        Direction.OUTGOING, GraphRelTypes.DEPENDS))
                    if (rel.getEndNode().hasProperty("muid")) {
                        if (!remainingDependingOn.remove((String)
                                rel.getEndNode().getProperty("muid")))
                            rel.delete();
                    } else
                        // TODO: handle this
                        throw new RuntimeException();
            }
            
            for (Muid dependId : remainingDependingOn) {
//                System.out.println("  dependingOn: " + dependId.toString());
                if (muidIndex.containsKey(dependId.toString()))
                    entityNode.createRelationshipTo(
                            muidIndex.get(dependId.toString()),
                            GraphRelTypes.DEPENDS);
                else
                    // TODO: handle this
                    throw new RuntimeException();
            }
            
            tx.success();
        } finally {
            tx.finish();
        }
    }
    
    private void updateEntity(Entity entity) {
        WriteBatch batch = keyvalueDb.createWriteBatch();
        try {
            for (Detail detail : Detail.values()) {
                if (detail == Detail.NONE)
                    continue;
                
                batch.put(
                        bytes(new IdDetail(entity.getId(), detail).toString()),
                        bytes(entity.getJson(detail)));
            }
            
            keyvalueDb.write(batch);
        } finally {
            try {
                batch.close();
            } catch (IOException e) {
                throw new ServerLevelDbBatchCloseSddError();
            }
        }
    }
    
    private void updateDependencies(Node entityNode) {
        for (Relationship rel : entityNode.getRelationships(
                Direction.INCOMING, GraphRelTypes.DEPENDS)) {
            Node dependencyNode = rel.getStartNode();
            Entity entity = newEntityByType(
                    (String) dependencyNode.getProperty("type"), this);
            entity.loadFromId(
                    new Muid((String) dependencyNode.getProperty("muid")));
            updateEntity(entity);
            updateDependencies(dependencyNode);
        }
    }
    
    public void deleteEntity(Muid id) {
        WriteBatch batch = keyvalueDb.createWriteBatch();
        try {
            for (Detail detail : Detail.values()) {
                if (detail == Detail.NONE)
                    continue;
                
                batch.delete(bytes(new IdDetail(id, detail).toString()));
            }
            
            keyvalueDb.write(batch);
        } finally {
            try {
                batch.close();
            } catch (IOException e) {
                throw new ServerLevelDbBatchCloseSddError();
            }
        }
    }
    
    @Override
    public void contextInitialized(ServletContextEvent arg) {
        ServletContext context = arg.getServletContext();
        context.setAttribute("server", this);

        start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg) {
        stop();
    }

}
