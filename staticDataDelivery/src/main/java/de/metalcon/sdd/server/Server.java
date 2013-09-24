package de.metalcon.sdd.server;

import static org.fusesource.leveldbjni.JniDBFactory.asString;
import static org.fusesource.leveldbjni.JniDBFactory.bytes;
import static org.fusesource.leveldbjni.JniDBFactory.factory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.WriteBatch;

import com.tinkerpop.blueprints.Index;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.neo4j.Neo4jGraph;

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

    final private static String leveldbPath = "/usr/share/sdd/leveldb";
    
    final private static String neo4jPath = "/usr/share/sdd/neo4j";

    private DB leveldb;
    
    private Neo4jGraph neo4j;
    
    private Index<Vertex> neo4jMuidIndex;
    
    private BlockingQueue<Request> queue;

    private Worker worker;
    
    public void start() {
        Options options = new Options();
        options.createIfMissing(true);
        try {
            leveldb = factory.open(new File(leveldbPath), options);
        } catch (IOException e) {
            throw new ServerLevelDbInitializationSddError();
        }
        
        neo4j = new Neo4jGraph(neo4jPath);
        neo4jMuidIndex = neo4j.createIndex("muidIndex", Vertex.class);
        
        queue = new LinkedBlockingQueue<Request>();
        worker = new Worker(queue);
        worker.start();
    }

    public void stop() {
        worker.stop();
        worker.waitForShutdown();
        
        neo4j.shutdown();
        
        try {
            leveldb.close();
        } catch (IOException e) {
            throw new ServerLevelDbCloseSddError();
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
        
        return asString(leveldb.get(bytes(idDetail.toString())));
    }
    
    public void writeEntity(Entity entity) {
        Muid id = entity.getId();
        
        Vertex entityVertex = null;
//        for (Vertex v : neo4j.getVertices("muid", id.toString())) {
//            entityVertex = v;
//            break;
//        }
        for (Vertex v : neo4jMuidIndex.query("muid", id.toString())) {
            entityVertex = v;
            break;
        }
        
        if (entityVertex == null) {
            // create
            System.out.println("create");
            neo4j.rollback();
            entityVertex = neo4j.addVertex(null);
            entityVertex.setProperty("muid", id.toString());
            neo4jMuidIndex.put("muid", id.toString(), entityVertex);
            neo4j.commit();
        } else {
            // update
            System.out.println("update");
        }
        
        WriteBatch batch = leveldb.createWriteBatch();
        try {
            for (Detail detail : Detail.values()) {
                if (detail == Detail.NONE)
                    continue;
                
                batch.put(bytes(new IdDetail(id, detail).toString()),
                          bytes(entity.getJson(detail)));
            }
            
            leveldb.write(batch);
        } finally {
            try {
                batch.close();
            } catch (IOException e) {
                throw new ServerLevelDbBatchCloseSddError();
            }
        }
    }
    
    public void deleteEntity(Muid id) {
        WriteBatch batch = leveldb.createWriteBatch();
        try {
            for (Detail detail : Detail.values()) {
                if (detail == Detail.NONE)
                    continue;
                
                batch.delete(bytes(new IdDetail(id, detail).toString()));
            }
            
            leveldb.write(batch);
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
