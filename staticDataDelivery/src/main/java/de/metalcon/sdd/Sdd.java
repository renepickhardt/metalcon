package de.metalcon.sdd;

import static org.fusesource.leveldbjni.JniDBFactory.asString;
import static org.fusesource.leveldbjni.JniDBFactory.bytes;
import static org.fusesource.leveldbjni.JniDBFactory.factory;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.WriteBatch;
import org.json.simple.JSONValue;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.tooling.GlobalGraphOperations;

import de.metalcon.common.JsonString;
import de.metalcon.sdd.config.Config;
import de.metalcon.sdd.config.MetaEntity;
import de.metalcon.sdd.config.MetaEntityOutput;
import de.metalcon.sdd.config.MetaType;
import de.metalcon.sdd.error.InvalidAttrException;
import de.metalcon.sdd.error.InvalidConfigException;
import de.metalcon.sdd.error.InvalidDependencyException;
import de.metalcon.sdd.error.InvalidDetailException;
import de.metalcon.sdd.error.InvalidTypeException;
import de.metalcon.sdd.queue.QueueAction;
import de.metalcon.sdd.queue.UpdateDependenciesQueueAction;
import de.metalcon.sdd.queue.UpdateGraphQueueAction;
import de.metalcon.sdd.queue.UpdateJsonQueueAction;

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
     * @throws InvalidConfigException  TODO: doc
     * @throws IOException   TODO: doc
     */
    public Sdd(Config config) throws InvalidConfigException, IOException {
        if (config == null)
            throw new IllegalArgumentException("config was null");
        
        // config
        config.validate();
        this.config = config;
        
        // connect to jsonDb (LevelDB)
        Options options = new Options();
        options.createIfMissing(true);
        try {
            jsonDb = factory.open(new  File(config.getLeveldbPath()), options);
        } catch (IOException e) {
            throw new IOException("Couldn't connect to jsonDb (LevelDB)", e);
        }
        
        // connect to entityGraph (Neo4j)
        entityGraph = new GraphDatabaseFactory().
                        newEmbeddedDatabase(config.getNeo4jPath());
        if (entityGraph == null)
            throw new IOException("Couldn't connect to entityGraph (Neo4j)");
        // load index
        entityGraphIdIndex = new HashMap<T, Node>();
        for (Node node : GlobalGraphOperations.at(entityGraph).getAllNodes())
            if (node.hasProperty("sddId")) {
                @SuppressWarnings("unchecked")
                T id = (T) node.getProperty("sddID");
                entityGraphIdIndex.put(id, node);
            }
        
        // create action queue
        queue = new PriorityBlockingQueue<QueueAction<T>>();
        
        // startup worker thread
        worker = new Worker<T>(queue);
        worker.start();
    }

    /**
     * Uninitializes SDD's resources.
     * 
     * Call this when you are done.
     * @throws IOException  When an errors occurs while closing jsonDb
     *                      connection.
     */
    @Override
    public void close() throws IOException {
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
                throw new IOException("Couldn't close jsonDb (LevelDB)", e);
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
     * @throws InvalidAttrException  If attrs contained an attr not valid for
     *                               type.
     */
    public boolean updateEntity(T id, String type, Map<String, String> attrs)
        throws InvalidTypeException, InvalidAttrException {
        if (id == null)
            throw new IllegalArgumentException("id was null");
        if (type == null)
            throw new IllegalArgumentException("type was null");
        if (attrs == null)
            throw new IllegalArgumentException("attrs was null");
        
        // is type valid?
        if (!config.isValidEntity(type))
            throw new InvalidTypeException();
        
        // are attrs valid?
        MetaEntity metaEntity = config.getEntity(type);
        for (Map.Entry<String, String> attr : attrs.entrySet()) {
            String attrName  = attr.getKey();
            String attrValue = attr.getValue();
            
            if (!metaEntity.isValidAttr(attrName)
                    || attrName.equals("id")
                    || attrName.equals("type"))
                throw new InvalidAttrException();
            if (attrValue == null)
                throw new InvalidAttrException();
        }
        
        return queueAction(
                new UpdateGraphQueueAction<T>(this, id, type, attrs));
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
        
        // TODO: queue
        return false;
    }
    
    /**
     * Waits until all actions on the queue are finished.
     */
    public void waitUntilQueueEmpty() {
        // TODO: implement
        while (!worker.isIdle())
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // stopped
            }
    }
    
    public void actionUpdateGraph(T id, String type,
                                  Map<String, String> attrs)
            throws InvalidDependencyException {
        MetaEntity metaEntity = config.getEntity(type);
        
        Transaction tx = entityGraph.beginTx();
        try {
            boolean create = false;
            
            Node entity = entityGraphIdIndex.get(id);
            if (entity == null)
                create = true;
            
            if (create) {
                entity = entityGraph.createNode();
                entity.setProperty("id",   id);
                entity.setProperty("type", type);
            } else {
            }
            
            for (Map.Entry<String, String> attr : attrs.entrySet()) {
               String   attrName  = attr.getKey();
               String   attrValue = attr.getValue();
               MetaType attrType  = metaEntity.getAttr(attrName); 
               
               entity.setProperty(attrName, attrValue);
               
               if (!attrType.isPrimitive()) {
                   if (attrType.isArray())
                       for (String dependencyId :
                               attrValue.split(config.getIdDelimeter()))
                           generateDependsRel(entity, attrName, dependencyId);
                    else
                       generateDependsRel(entity, attrName, attrValue);
               }
            }
            
            tx.success();
            if (create)
                entityGraphIdIndex.put(id, entity);
        } finally {
            tx.finish();
        }
        
        queueAction(new UpdateJsonQueueAction<T>(this, id));
    }

    private void generateDependsRel(Node entity, String attr, String id)
            throws InvalidDependencyException {
        @SuppressWarnings("unchecked")
        T    dependencyId = (T) id;
        Node dependency   = entityGraphIdIndex.get(dependencyId);
        if (dependency == null)
            throw new InvalidDependencyException();

        entity.createRelationshipTo(dependency,
                                    DynamicRelationshipType.withName(attr));
    }
    
    public void actionUpdateJson(T id) throws IOException {
        WriteBatch batch = jsonDb.createWriteBatch();
        try {
            for (String detail : config.getDetails()) {
                String idDetail =
                        id.toString() + config.getIdDetailDelimeter() + detail;
                
                String json = generateJson(id, detail);
                if (json == null)
                    batch.delete(bytes(idDetail));
                else
                    batch.put(bytes(idDetail), bytes(json));
            }
            
            jsonDb.write(batch);
        } finally {
            try {
                batch.close();
            } catch (IOException e) {
                throw new IOException("Couldn't close WriteBatch", e);
            }
        }
        
        queueAction(new UpdateDependenciesQueueAction<T>(this, id));
    }
    
    private String generateJson(T id, String detail) {
        Node entity = entityGraphIdIndex.get(id);
        if (entity == null)
            throw new RuntimeException();
        
        String type = (String) entity.getProperty("type", null);
        if (type == null)
            throw new RuntimeException();
        
        MetaEntity metaEntity = config.getEntity(type);
        if (metaEntity == null)
            throw new RuntimeException();
        MetaEntityOutput metaOutput = metaEntity.getOutput(detail);
        if (metaOutput == null)
            return null;
        
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("id",   id.toString());
        json.put("type", type);
        
        for (String oattr : metaOutput.getOattrs()) {
            String   oattrDetail = metaOutput.getOattr(oattr);
            MetaType oattrType   = metaEntity.getAttr(oattr);
            
            String attrValue = (String) entity.getProperty(oattr, null);
            if (attrValue == null)
                json.put(oattr, null);
            else if (oattrType.isPrimitive())
                json.put(oattr, attrValue);
            else if (oattrType.isArray()) {
                List<JsonString> array = new LinkedList<JsonString>();
                for (String attrId : attrValue.split(config.getIdDelimeter())) {
                    array.add(getJson(attrId, oattrDetail));
                }
                json.put(oattr, array);
            } else
                json.put(oattr, getJson(attrValue, oattrDetail));
        }
        
        return JSONValue.toJSONString(json);
    }
    
    private JsonString getJson(String idString, String detail) {
        try {
            @SuppressWarnings("unchecked")
            T id = (T) idString;
            return new JsonString(readEntity(id, detail));
        } catch(InvalidDetailException e) {
            // TODO: this can't really happen, would still be better to log the error.
            return null;
        }
    }
    
    public void actionUpdateDependencies(T id) {
    }
    
    private boolean queueAction(QueueAction<T> action) {
        if (queue.contains(action))
            return true;
        else
            return queue.offer(action);
    }
    
}
