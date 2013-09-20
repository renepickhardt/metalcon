package de.metalcon.sdd.server;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import de.metalcon.common.Muid;
import de.metalcon.sdd.Detail;
import de.metalcon.sdd.IdDetail;
import de.metalcon.sdd.entity.Entity;
import de.metalcon.sdd.error.ServerDetailNoneSddError;
import de.metalcon.sdd.error.ServerLevelDbBatchCloseSddError;
import de.metalcon.sdd.error.ServerLevelDbCloseSddError;
import de.metalcon.sdd.error.ServerLevelDbInitializationSddError;
import de.metalcon.sdd.request.Request;

// LevelDB
import org.iq80.leveldb.*;
import static org.fusesource.leveldbjni.JniDBFactory.*;

public class Server implements ServletContextListener {

    final private static String dbPath = "/usr/share/sdd/leveldb";

    private DB db;
    
    private BlockingQueue<Request> queue;

    private Worker worker;
    
    public void start() {
        Options options = new Options();
        options.createIfMissing(true);
        try {
            db = factory.open(new File(dbPath), options);
        } catch (IOException e) {
            throw new ServerLevelDbInitializationSddError();
        }
        
        queue = new LinkedBlockingQueue<Request>();
        worker = new Worker(queue);
        worker.start();
    }

    public void stop() {
        worker.stop();
        worker.waitForShutdown();
        
        try {
            db.close();
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
        
        return asString(db.get(bytes(idDetail.toString())));
    }
    
    public void writeEntity(Entity entity) {
        Muid id = entity.getId();
        
        WriteBatch batch = db.createWriteBatch();
        try {
            for (Detail detail : Detail.values()) {
                if (detail == Detail.NONE)
                    continue;
                
                batch.put(bytes(new IdDetail(id, detail).toString()),
                          bytes(entity.getJson(detail)));
            }
            
            db.write(batch);
        } finally {
            try {
                batch.close();
            } catch (IOException e) {
                throw new ServerLevelDbBatchCloseSddError();
            }
        }
    }
    
    public void deleteEntity(Muid id) {
        WriteBatch batch = db.createWriteBatch();
        try {
            for (Detail detail : Detail.values()) {
                if (detail == Detail.NONE)
                    continue;
                
                batch.delete(bytes(new IdDetail(id, detail).toString()));
            }
            
            db.write(batch);
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
