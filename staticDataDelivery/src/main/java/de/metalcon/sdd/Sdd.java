package de.metalcon.sdd;

import static org.fusesource.leveldbjni.JniDBFactory.asString;
import static org.fusesource.leveldbjni.JniDBFactory.bytes;
import static org.fusesource.leveldbjni.JniDBFactory.factory;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.tooling.GlobalGraphOperations;

import de.metalcon.sdd.config.Config;
import de.metalcon.sdd.error.InvalidDetailException;
import de.metalcon.sdd.error.InvalidTypeException;
import de.metalcon.sdd.queue.DeleteQueueAction;
import de.metalcon.sdd.queue.QueueAction;
import de.metalcon.sdd.queue.UpdateQueueAction;

/**
 * TODO: doc
 * @param <T>  Type to be used for Entity-IDs. Must be a type that can directly
 *             be stored in Neo4j (Java primitive or string).
 */
public class Sdd<T> implements Closeable {
    
    private Config config;
    
    private DB jsonDb;
    
    private GraphDatabaseService entityGraph;
    
    private Map<T, Node> entityGraphIdIndex;
    
    private BlockingQueue<QueueAction<T>> queue;
    
    private Worker<T> worker;
    
    /**
     * TODO: doc
     * @param config  TODO: doc
     */
    public Sdd(Config config) {
        if (config == null)
            throw new IllegalArgumentException("config was null");
        
        // store config
        this.config = config;
        
        // connect to jsonDb (LevelDB)
        Options options = new Options();
        options.createIfMissing(true);
        try {
            jsonDb = factory.open(new  File(config.getLeveldbPath()), options);
        } catch (IOException e) {
            // TODO: handle this
            throw new RuntimeException("Couldn't connect to jsonDb (LevelDB)");
        }
        
        // connect to entityGraph (Neo4j)
        entityGraph = new GraphDatabaseFactory().
                        newEmbeddedDatabase(config.getNeo4jPath());
        // load index
        entityGraphIdIndex = new HashMap<T, Node>();
        for (Node node : GlobalGraphOperations.at(entityGraph).getAllNodes())
            if (node.hasProperty("sddId")) {
                @SuppressWarnings("unchecked")
                T id = (T) node.getProperty("sddID");
                entityGraphIdIndex.put(id, node);
            }
        
        // create action queue
        if (config.isPrioritizedQueue())
            queue = new PriorityBlockingQueue<QueueAction<T>>();
        else
            queue = new LinkedBlockingQueue<QueueAction<T>>();
        
        // startup worker thread
        worker = new Worker<T>(queue);
        worker.start();
    }

    /**
     * Uninitializes SDD's resources.
     * 
     * Call this when you are done.
     */
    @Override
    public void close() {
        // close worker thread
        if (worker != null) {
            worker.stop();
            worker.waitForShutdown();
        }
        
        // close entityGraph (Neo4j)
        if (entityGraph != null)
            entityGraph.shutdown();
        
        // close jsonDb (LevelDB)
        if (jsonDb != null)
            try {
                jsonDb.close();
            } catch(IOException e) {
                // TODO: handle this
                throw new RuntimeException("Couldn't close jsonDb (LevelDB)");
            }
    }
    
    /**
     * Reads the stored JSON for id/detail combination.
     * @param id      Entity-ID.
     * @param detail  String with valid detail specifier, as defined in
     *                config.
     * @return A string in JSON format with information stored for the entity. 
     *         Null if no entity with that ID exists.
     * @throws InvalidDetailException  If detail wasn't a valid detail string.
     */
    public String readEntity(T id, String detail)
            throws InvalidDetailException {
        if (id == null)
            throw new IllegalArgumentException("id was null");
        if (detail == null)
            throw new IllegalArgumentException("detail was null");
        
        // is valid detail?
        if (!config.isValidDetail(detail))
            throw new InvalidDetailException();
        
        // TODO: is idDetail as string a good idea?
        String idDetail = id.toString() +
                          config.getIdDetailDelimeter() +
                          detail;
        return asString(jsonDb.get(bytes(idDetail)));
    }
    
    /**
     * Updates an entity or creates a new one it it doesn't exist yet.
     * 
     * The action is not executed immediately but rather queued up and executed
     * at a later point.
     * @param id     Entity-ID. If no entity with this ID exists, a new one is
     *               created.
     * @param type   String with valid `type` specifier, as defined in config.
     * @param attrs  A map with the entities attributes.
     * @returns True if adding the action to the queue was possible,
     *          False otherwise.
     * @throws InvalidTypeException  If type wasn't a valid type string.
     */
    public boolean updateEntity(T id, String type, Map<String, String> attrs)
        throws InvalidTypeException {
        if (id == null)
            throw new IllegalArgumentException("id was null");
        if (type == null)
            throw new IllegalArgumentException("type was null");
        if (attrs == null)
            throw new IllegalArgumentException("attrs was null");
        
        // is type valid?
        if (!config.isValidEntity(type))
            throw new InvalidTypeException();
        
        // TODO: are attrs valid for type?
        
        return queueAction(new UpdateQueueAction<T>(this, id, type, attrs));
    }
    
    /**
     * Deletes an entity.
     * 
     * The action is not executed immediately but rather queued up and executed
     * at a later point.
     * @param id  Entity-ID.
     * @returns True if adding the action to the queue was possible,
     *          False otherwise.
     */
    public boolean deleteEntity(T id) {
        if (id == null)
            throw new IllegalArgumentException("id was null");
            
        return queueAction(new DeleteQueueAction<T>(this, id));
    }
    
    /**
     * Waits until all actions on the queue are finished.
     */
    public void waitUntilQueueEmpty() {
        // TODO: implement
        while (worker.isBusy())
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // stopped
            }
    }
    
    public void updateEntityImmediately(T id, String type,
                                        Map<String, String> attrs) {
        // TODO: implement
        // TODO: queue UpdateDependencyQueueAction()
    }
    
    public void deleteEntityImmediately(T id) {
        // TODO: implement
        // TODO: queue DeleteDependencyQueueAction()
    }
    
    private boolean queueAction(QueueAction<T> action) {
        if (queue.contains(action))
            return true;
        else
            return queue.offer(action);
    }
    
}
