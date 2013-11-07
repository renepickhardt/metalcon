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
import de.metalcon.sdd.error.InvalidAttrNameException;
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

public class Sdd implements Closeable {
    
    // TODO: implement delete
    // TODO: add validate data option
    // TODO: dont make index lookup for relation
    // TODO: create temporary index until transaction is finished
    // TODO: split up entity/relationship transactions
    // TODO: rel with nonexisting entity???
    // TODO: bug that attrs are only update after second reload?
    
    private Config config;
    
    private DB jsonDb;
    
    private WriteBatch jsonDbWriteBatch;
    
    private GraphDatabaseService entityGraph;
    
    private Map<Long, Node> entityGraphIdIndex;
    
    private Transaction entityGraphTransaction;
    
    private BlockingQueue<QueueAction> queue;
    
    private Worker worker;
    
    public Sdd(Config config)
    throws InvalidConfigException, IOException, InvalidAttrNameException {
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
        
        jsonDbWriteBatch = null;
        
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
        
        entityGraphTransaction = null;
        
        // create action queue
        queue = new PriorityBlockingQueue<QueueAction>();
        
        // startup worker thread
        worker = new Worker(this, queue);
        worker.start();
    }

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
    
    public boolean updateEntity(long id, String type, Map<String, String> attrs)
    throws InvalidTypeException, InvalidAttrException, InvalidAttrNameException {
        if (type == null)
            throw new IllegalArgumentException("type was null");
        if (attrs == null)
            throw new IllegalArgumentException("attrs was null");
        
        Entity entity = new Entity(config, id, type);
        
        Map<String, String>     primitives = new HashMap<String, String>(); 
        Map<String, Long>       entityRel  = new HashMap<String, Long>();
        Map<String, List<Long>> entityRels = new HashMap<String, List<Long>>();
        
        for (Map.Entry<String, String> attr: attrs.entrySet()) {
            String   attrName  = attr.getKey();
            String   attrValue = attr.getValue();
            MetaType attrType  = entity.getMetaEntity().getAttr(attrName);
            
            if (attrValue == null)
                throw new InvalidAttrException();
            
            if (attrType.isPrimitive())
                primitives.put(attrName, attrValue);
            else if (attrType.isArray()) {
                String[] relsStrings = attrValue.split(config.getIdDelimeter());
                List<Long> rels = new LinkedList<Long>();
                for (String relString : relsStrings)
                    rels.add(Long.parseLong(relString));
                entityRels.put(attrName, rels);
            } else
                entityRel.put(attrName, Long.parseLong(attrValue));
        }
        
        boolean worked = updateEntityAttrs(id, type, primitives);
        for (Map.Entry<String, Long> r : entityRel.entrySet()) {
            String attr = r.getKey();
            long   rel  = r.getValue();
            worked = worked && updateEntityRel(id, type, attr, rel);
        }
        for (Map.Entry<String, List<Long>> r : entityRels.entrySet()) {
            String     attr    = r.getKey();
            List<Long> relList = r.getValue();
            long[]     rels    = new long[relList.size()];
            
            int i = 0;
            for (long rel : relList)
                rels[i++] = rel;
            worked = worked && updateEntityRel(id, type, attr, rels);
        }
        return worked;
    }
    
    public boolean updateEntityAttrs(long id, String type,
                                     Map<String, String> attrs)
    throws InvalidTypeException, InvalidAttrException, InvalidAttrNameException {
        if (type == null)
            throw new IllegalArgumentException("type was null");
        if (attrs == null)
            throw new IllegalArgumentException("attrs was null");
        
        Entity entity = new Entity(config, id, type);
        
        for (Map.Entry<String, String> attr : attrs.entrySet()) {
            String   attrName  = attr.getKey();
            String   attrValue = attr.getValue();
            MetaType attrType  = entity.getMetaEntity().getAttr(attrName);
            
            if (!attrType.isPrimitive() || attrValue == null)
                throw new InvalidAttrException();
        }
        
        return queueAction(new UpdateGraphEntityQueueAction(this, entity, attrs));
    }
    
    public boolean updateEntityRel(long id, String type,
                                      String attr, long rel)
    throws InvalidTypeException, InvalidAttrException, InvalidAttrNameException {
        if (type == null)
            throw new IllegalArgumentException("type was null");
        if (attr == null)
            throw new IllegalArgumentException("attr was null");
        
        Entity entity = new Entity(config, id, type);
        
        MetaType attrType = entity.getMetaEntity().getAttr(attr);
        if (attrType.isPrimitive() || attrType.isArray())
            throw new InvalidAttrException();
        
        return queueAction(new UpdateGraphRelQueueAction(this, entity, attr, rel));
    }
  
    public boolean updateEntityRel(long id, String type,
                                      String attr, long[] rel)
    throws InvalidTypeException, InvalidAttrException, InvalidAttrNameException {
        if (type == null)
            throw new IllegalArgumentException("type was null");
        if (attr == null)
            throw new IllegalArgumentException("attr was null");
        
        Entity entity = new Entity(config, id, type);
        
        MetaType attrType = entity.getMetaEntity().getAttr(attr);
        if (attrType.isPrimitive() || !attrType.isArray() || rel == null)
            throw new InvalidAttrException();
        
        return queueAction(new UpdateGraphRelsQueueAction(this, entity, attr, rel));
    }
   
    public boolean deleteEntity(long id) {
        // TODO: queue
        return false;
    }
    
    public void waitUntilQueueEmpty() throws IOException {
        worker.waitUntilQueueEmpty();
    }
    
    public Worker.QueueStatus getQueueStatus() {
        return worker.getQueueState();
    }
    
    /* package */ void startEntityGraphTransaction() {
        entityGraphTransaction = entityGraph.beginTx();
    }
    
    /* package */ void endEntityGraphTransaction() {
        entityGraphTransaction.success();
        entityGraphTransaction.finish();
        entityGraphTransaction = null;
    }
    
    /* package */ void startJsonDbTransaction() {
        jsonDbWriteBatch = jsonDb.createWriteBatch();
    }
    
    /* package */ void endJsonDbTransaction() throws IOException {
        try {
            jsonDb.write(jsonDbWriteBatch);
            jsonDbWriteBatch.close();
        } catch (IOException e) {
            throw new IOException("Couldn't close WriteBatch", e);
        }
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
    
    private String buildJsonProperty(String detail) {
        return "json-" + detail;
    }
    
    public void actionUpdateGraphEntity(Entity entity,
                                        Map<String, String> attrs)
    throws InconsitentTypeException {
        Node node = entityGraphIdIndex.get(entity.getId());
        if (node == null) {
            node = entityGraph.createNode();
            node.setProperty("id",   entity.getId());
            node.setProperty("type", entity.getType());
            entityGraphIdIndex.put(entity.getId(), node);
        }
        entity.setNode(node);
        
        entity.setAttrs(attrs);
        
        queueAction(new UpdateJsonQueueAction(this, entity));
    }
    
    public void actionUpdateGraphRel(Entity entity, String attr, long rel)
    throws InconsitentTypeException, InvalidTypeException,
           InvalidReferenceException, InvalidAttrNameException {
        entity.setNode(entityGraphIdIndex.get(entity.getId()));
        
        boolean exists = false;
        for (Relationship referenceRel : entity.getNode().getRelationships(
                Direction.OUTGOING, DynamicRelationshipType.withName(attr))) {
            Entity reference = new Entity(config, referenceRel.getEndNode());
            if (reference.getId() == rel)
                exists = true;
            else
                referenceRel.delete();
        }
        
        if (!exists)
            generateReference(entity, attr, rel);
        
        queueAction(new UpdateJsonQueueAction(this, entity));
    }
    
    public void actionUpdateGraphRels(Entity entity, String attr, long[] rels)
    throws InvalidReferenceException, InconsitentTypeException,
           InvalidTypeException, InvalidAttrNameException {
        entity.setNode(entityGraphIdIndex.get(entity.getId()));
        
        Set<Long> remainingRels = new HashSet<Long>();
        for (long rel : rels)
            remainingRels.add(rel);
        
        for (Relationship referenceRel : entity.getNode().getRelationships(
                Direction.OUTGOING, DynamicRelationshipType.withName(attr))) {
            Entity reference = new Entity(config, referenceRel.getEndNode());
            
            if (remainingRels.contains(reference.getId()))
                remainingRels.remove(reference.getId());
            else
                referenceRel.delete();
        }
        
        for (long rel : remainingRels)
            generateReference(entity, attr, rel);
        
        queueAction(new UpdateJsonQueueAction(this, entity));
    }
    
    private void generateReference(Entity entity, String attr, long refrenceId)
    throws InvalidReferenceException, InvalidTypeException, InvalidAttrNameException, InconsitentTypeException {
        MetaType attrType = entity.getMetaEntity().getAttr(attr);
        
        Node referenceNode = entityGraphIdIndex.get(refrenceId);
        if (referenceNode == null)
            throw new InvalidReferenceException("reference was not found");
        
        Entity reference = new Entity(config, referenceNode);
        
        if (reference.getId() == entity.getId())
            throw new InvalidReferenceException("cant reference oneself");
        if (reference.getType() != attrType.getType())
            throw new InvalidReferenceException("reference was of invalid type");
        
        entity.getNode().createRelationshipTo(
                referenceNode, DynamicRelationshipType.withName(attr));
    }
    
    public void actionUpdateJson(Entity entity)
    throws IOException, InvalidTypeException, InvalidAttrNameException {
        Set<String> modifiedDetails = new HashSet<String>();
        
        for (String detail : config.getDetails()) {
            String json = generateJson(entity, detail);
            
            String oldjson = (String) entity.getNode().getProperty(
                    buildJsonProperty(detail), null);
            if (!json.equals(oldjson)) {
                modifiedDetails.add(detail);
                
                entity.getNode().setProperty(buildJsonProperty(detail), json);
                
                String idDetail = buildIdDetail(entity.getId(), detail);
                jsonDbWriteBatch.put(bytes(idDetail), bytes(json));
            }
        }
            
        queueAction(new UpdateReferencingQueueAction(this, entity,
                                                     modifiedDetails));
    }
    
    private String generateJson(Entity entity, String detail)
    throws InvalidTypeException, InvalidAttrNameException {
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
