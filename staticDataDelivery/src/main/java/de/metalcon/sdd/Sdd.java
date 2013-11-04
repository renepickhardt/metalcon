package de.metalcon.sdd;

import static org.fusesource.leveldbjni.JniDBFactory.asString;
import static org.fusesource.leveldbjni.JniDBFactory.bytes;
import static org.fusesource.leveldbjni.JniDBFactory.factory;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import de.metalcon.sdd.error.InconsitentTypeException;
import de.metalcon.sdd.error.InvalidAttrException;
import de.metalcon.sdd.error.InvalidConfigException;
import de.metalcon.sdd.error.InvalidReferenceException;
import de.metalcon.sdd.error.InvalidDetailException;
import de.metalcon.sdd.error.InvalidTypeException;
import de.metalcon.sdd.queue.QueueAction;
import de.metalcon.sdd.queue.UpdateGraphRelQueueAction;
import de.metalcon.sdd.queue.UpdateGraphRelsQueueAction;
import de.metalcon.sdd.queue.UpdateReferencingQueueAction;
import de.metalcon.sdd.queue.UpdateGraphEntityQueueAction;
import de.metalcon.sdd.queue.UpdateJsonQueueAction;

/**
 * TODO: doc
 */
public class Sdd implements Closeable {
    
    private Config config;
    
    private DB jsonDb;
    
    private GraphDatabaseService entityGraph;
    
    private Map<Long, Node> entityGraphIdIndex;
    
    private BlockingQueue<QueueAction> queue;
    
    private Worker worker;
    
    /**
     * TODO: doc
     * @param config  TODO: doc
     * @throws InvalidConfigException  TODO: doc
     * @throws IOException   TODO: doc
     */
    public Sdd(Config config)
    throws InvalidConfigException, IOException {
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
        entityGraphIdIndex = new HashMap<Long, Node>();
        for (Node node : GlobalGraphOperations.at(entityGraph).getAllNodes())
            if (node.hasProperty("id")) {
                Long id = (Long) node.getProperty("id");
                entityGraphIdIndex.put(id, node);
            }
        
        // create action queue
        queue = new PriorityBlockingQueue<QueueAction>();
        
        // startup worker thread
        worker = new Worker(queue);
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
    public void close()
    throws IOException {
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
    public String readEntity(long id, String detail)
    throws InvalidDetailException {
        if (detail == null)
            throw new IllegalArgumentException("detail was null");
        
        // is valid detail?
        if (!config.isValidDetail(detail))
            throw new InvalidDetailException();
        
        // TODO: is idDetail as string a good idea?
        String idDetail = buildIdDetail(id, detail);
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
    public boolean updateEntity(long id, String type, Map<String, String> attrs)
    throws InvalidTypeException, InvalidAttrException {
        if (type == null)
            throw new IllegalArgumentException("type was null");
        if (attrs == null)
            throw new IllegalArgumentException("attrs was null");
        
        Entity entity = new Entity(config, id, type);
        
        for (Map.Entry<String, String> attr : attrs.entrySet()) {
            String   attrName  = attr.getKey();
            String   attrValue = attr.getValue();
            MetaType attrType  = entity.getMetaEntity().getAttr(attrName);
            
            if (attrType == null
                    || !attrType.isPrimitive()
                    || attrValue == null)
                throw new InvalidAttrException();
        }
        
        return queueAction(new UpdateGraphEntityQueueAction(this, entity, attrs));
    }
    
    public boolean updateRelationship(long id, String type,
                                      String attr, long rel)
    throws InvalidTypeException, InvalidAttrException {
        if (type == null)
            throw new IllegalArgumentException("type was null");
        if (attr == null)
            throw new IllegalArgumentException("attr was null");
        
        Entity entity = new Entity(config, id, type);
        
        MetaType attrType = entity.getMetaEntity().getAttr(attr);
        if (attrType == null
                || attrType.isPrimitive()
                || attrType.isArray())
            throw new InvalidAttrException();
        
        return queueAction(new UpdateGraphRelQueueAction(this, entity, attr, rel));
    }
  
    public boolean updateRelationship(long id, String type,
                                      String attr, long[] rel)
    throws InvalidTypeException, InvalidAttrException {
        if (type == null)
            throw new IllegalArgumentException("type was null");
        if (attr == null)
            throw new IllegalArgumentException("attr was null");
        
        Entity entity = new Entity(config, id, type);
        
        MetaType attrType = entity.getMetaEntity().getAttr(attr);
        if (attrType == null
                || attrType.isPrimitive()
                || !attrType.isArray()
                || rel == null)
            throw new InvalidAttrException();
        
        return queueAction(new UpdateGraphRelsQueueAction(this, entity, attr, rel));
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
    public boolean deleteEntity(long id) {
        // TODO: queue
        return false;
    }
    
    private boolean queueAction(QueueAction action) {
        if (queue.contains(action))
            return true;
        else
            return queue.offer(action);
    }
    
    private String buildIdDetail(long id, String detail) {
        return id + config.getIdDetailDelimeter() + detail;
    }
    
    /**
     * Waits until all actions on the queue are finished.
     */
    public void waitUntilQueueEmpty() {
        while (!worker.isIdle())
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // stopped
            }
    }
    
//    public void actionUpdateGraph(Entity entity, Map<String, Object> attrs)
//    throws InvalidReferenceException, InconsitentTypeException,
//           InvalidTypeException {
//        // TODO: check if old attrs are considered
//        
//        // TODO: check if attr = id
//    
//        Transaction tx = entityGraph.beginTx();
//        try {
//            boolean create = false;
//            
//            Node entityNode = entityGraphIdIndex.get(entity.getId());
//            if (entityNode == null) {
//                create = true;
//                entityNode = entityGraph.createNode();
//                entityNode.setProperty("id",   entity.getId());
//                entityNode.setProperty("type", entity.getType());
//            }
//            entity.setNode(entityNode);
//                
//            for (Map.Entry<String, Object> attr : attrs.entrySet()) {
//                String attrName  = attr.getKey();
//                Object attrValue = attr.getValue();
//                entityNode.setProperty(attrName, attrValue);
//            }
//                
//            if (!create)
//                for (Relationship referenceRel :
//                        entityNode.getRelationships(Direction.OUTGOING)) {
//                    String attrName  = referenceRel.getType().name();
//                    Object attrValue = attrs.get(attrName);
//                    if (attrValue == null)
//                        continue;
//                    MetaType attrType = entity.getMetaEntity().getAttr(
//                            attrName);
//                    
//                    Node reference   = referenceRel.getEndNode();
//                    long referenceId = (Long) reference.getProperty("id");
//                    
//                    if (attrType.isPrimitive())
//                        // TODO: this shouldn't happen
//                        throw new RuntimeException();
//                    else if (attrType.isArray()) {
//                        // TODO: optimize
//                        long[] attrIds = (long[]) attrValue;
//                        boolean found = false;
//                        for (long attrId : attrIds)
//                            if (attrId == referenceId) {
//                                found = true;
//                                break;
//                            }
//                        if (!found)
//                            referenceRel.delete();
//                    } else {
//                        long attrId = (long) attrValue;
//                        if (attrId != referenceId)
//                            referenceRel.delete();
//                    }
//                }
//            
//            for (Map.Entry<String, Object> attr : attrs.entrySet()) {
//                String attrName  = attr.getKey();
//                Object attrValue = attr.getValue();
//                if (attrValue == null)
//                    continue;
//                MetaType attrType = entity.getMetaEntity().getAttr(attrName);
//                
//                if (!attrType.isPrimitive()) {
//                    if (attrType.isArray()) {
//                        long[] attrIds = (long[]) attrValue;
//                        for (long attrId : attrIds)
//                            generateReference(entityNode, attrName, attrId);
//                    } else {
//                        long attrId = (long) attrValue;
//                        generateReference(entityNode, attrName, attrId);
//                    }
//                }
//            }
//                
//            tx.success();
//            if (create)
//                entityGraphIdIndex.put(entity.getId(), entityNode);
//        
//            queueAction(new UpdateJsonQueueAction(this, entity));
//        } finally {
//            tx.finish();
//        }
//    }

    public void actionUpdateGraphEntity(Entity entity,
                                        Map<String, String> attrs)
    throws InconsitentTypeException {
        Transaction tx = entityGraph.beginTx();
        try {
            boolean create = false;
            
            Node node = entityGraphIdIndex.get(entity.getId());
            if (node == null) {
                create = true;
                node = entityGraph.createNode();
                node.setProperty("id",   entity.getId());
                node.setProperty("type", entity.getType());
            }
            entity.setNode(node);
            
            entity.setAttrs(attrs);
            
            tx.success();
            if (create)
                entityGraphIdIndex.put(entity.getId(), node);
            
            queueAction(new UpdateJsonQueueAction(this, entity));
        } finally {
            tx.finish();
        }
    }
    
    public void actionUpdateGraphRel(Entity entity, String attr, long rel)
    throws InconsitentTypeException, InvalidTypeException,
           InvalidReferenceException {
        Transaction tx = entityGraph.beginTx();
        try {
            entity.setNode(entityGraphIdIndex.get(entity.getId()));
            
            boolean exists = false;
            for (Relationship referenceRel : entity.getNode().getRelationships(
                    Direction.OUTGOING, DynamicRelationshipType.withName(attr)))
            {
                Entity reference = new Entity(config,
                                              referenceRel.getEndNode());
                if (reference.getId() == rel)
                    exists = true;
                else
                    referenceRel.delete();
            }
            
            if (!exists)
                generateReference(entity.getNode(), attr, rel);
            
            tx.success();
            
            queueAction(new UpdateJsonQueueAction(this, entity));
        } finally {
            tx.finish();
        }
    }
    
    public void actionUpdateGraphRels(Entity entity, String attr, long[] rels)
    throws InvalidReferenceException, InconsitentTypeException,
           InvalidTypeException {
        Transaction tx = entityGraph.beginTx();
        try {
            entity.setNode(entityGraphIdIndex.get(entity.getId()));
            
            Set<Long> remainingRels = new HashSet<Long>();
            for (long rel : rels)
                remainingRels.add(rel);
            
            for (Relationship referenceRel : entity.getNode().getRelationships(
                    Direction.OUTGOING, DynamicRelationshipType.withName(attr)))
            {
                Entity reference = new Entity(config,
                                              referenceRel.getEndNode());
                
                if (remainingRels.contains(reference.getId()))
                    remainingRels.remove(reference.getId());
                else
                    referenceRel.delete();
            }
            
            for (long rel : remainingRels)
                generateReference(entity.getNode(), attr, rel);
            
            tx.success();
            
            queueAction(new UpdateJsonQueueAction(this, entity));
        } finally {
            tx.finish();
        }
    }
    
    private void generateReference(Node entity, String attr, long refrenceId)
    throws InvalidReferenceException {
        Node reference = entityGraphIdIndex.get(refrenceId);
        if (reference == null)
            throw new InvalidReferenceException();

        entity.createRelationshipTo(reference,
                                    DynamicRelationshipType.withName(attr));
    }
    
    public void actionUpdateJson(Entity entity)
    throws IOException, InvalidTypeException {
        Set<String> modifiedDetails = new HashSet<String>();
        
        Transaction tx    = entityGraph.beginTx();
        WriteBatch  batch = jsonDb.createWriteBatch();
        try {
            for (String detail : config.getDetails()) {
                String json = generateJson(entity, detail);
                
                String oldjson = (String) entity.getNode().getProperty(
                        "json-" + detail, null);
                if (!json.equals(oldjson)) {
                    modifiedDetails.add(detail);
                    
                    entity.getNode().setProperty("json-" + detail, json);
                    
                    String idDetail = buildIdDetail(entity.getId(), detail);
                    batch.put(bytes(idDetail), bytes(json));
                }
            }
            
            tx.success();
            jsonDb.write(batch);
        } finally {
            tx.finish();
            try {
                batch.close();
            } catch (IOException e) {
                throw new IOException("Couldn't close WriteBatch", e);
            }
        }
        
        queueAction(new UpdateReferencingQueueAction(this, entity,
                                                      modifiedDetails));
    }
    
    private String generateJson(Entity entity, String detail)
    throws InvalidTypeException {
        MetaEntity metaEntity = entity.getMetaEntity();
        if (metaEntity == null)
            throw new RuntimeException();
        MetaEntityOutput metaOutput = metaEntity.getOutput(detail);
        
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("id",   entity.getId());
        json.put("type", entity.getType());
        
        if (metaOutput != null)
            for (String attr : metaOutput.getOattrs()) {
                String   attrDetail = metaOutput.getOattr(attr);
                MetaType attrType   = metaEntity.getAttr(attr);
                
                if (attrType.isPrimitive()) {
                    String attrValue = (String) entity.getNode().getProperty(
                            attr, null);
                    if (attrValue == null)
                        json.put(attr, null);
                    else
                        json.put(attr, attrValue.toString());
                } else {
                    Iterable<Relationship> referenceRels =
                            entity.getNode().getRelationships(
                                    Direction.OUTGOING,
                                    DynamicRelationshipType.withName(attr));
                    
                    if (attrType.isArray()) {
                        List<JsonString> referenceJsons =
                                new LinkedList<JsonString>();
                        for (Relationship referenceRel : referenceRels) {
                            Node reference = referenceRel.getEndNode();
                            referenceJsons.add(new JsonString((String)
                                    reference.getProperty("json-" + attrDetail,
                                                           null)));
                        }
                        if (referenceJsons.isEmpty())
                            json.put(attr, null);
                        else
                            json.put(attr, referenceJsons);
                    } else {
                        if (referenceRels.iterator().hasNext()) {
                            Relationship referenceRel =
                                    referenceRels.iterator().next();
                            Node reference = referenceRel.getEndNode();
                            json.put(attr, new JsonString((String)
                                    reference.getProperty("json-" + attrDetail,
                                                           null)));
                        } else
                            json.put(attr, null);
                    }
                }
            }
        
        return JSONValue.toJSONString(json);
    }
    
    public void actionUpdateReferencing(Entity entity,
                                         Set<String> modifiedDetails)
    throws InconsitentTypeException, InvalidTypeException {
        for (Relationship referencingRel :
                entity.getNode().getRelationships(Direction.INCOMING)) {
            Entity referencing = new Entity(config,
                                            referencingRel.getStartNode());
            
            if (referencing.getMetaEntity().dependsOn(entity.getType(),
                                                      modifiedDetails))
                queueAction(new UpdateJsonQueueAction(this, referencing));
        }
    }
    
}
