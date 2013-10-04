package de.metalcon.sdd.server;

import java.util.concurrent.BlockingQueue;

import de.metalcon.sdd.error.SddError;
import de.metalcon.sdd.server.queue.QueueAction;

public class Worker implements Runnable {
    
    private Thread thread;
    
    private boolean running;
    
    private boolean stopping;
    
    private BlockingQueue<QueueAction> queue;
    
    public Worker(BlockingQueue<QueueAction> queue) {
        running = false;
        stopping = false;
        this.queue = queue;
    }
    
    @Override
    public void run() {
        running = true;
        
        try {
            QueueAction queueAction = null;
            while (!stopping) {
                try {
                    queueAction = queue.take();
                    queueAction.runQueueAction();
                } catch(SddError e) {
                    // A single error in a request does not terminate the server.
                    // TODO: somehow log the error.
                    e.print();
                }
            }
        } catch (InterruptedException e) {
            // stopped by server
            // TODO: somehow store the queue until the server start up again.
        }
        
        running = false;
        stopping = false;
    }
    
    public void start() {
        thread = new Thread(this);
        thread.start();
    }
    
    public void stop() {
        if (running) {
            stopping = true;
            thread.interrupt();
        }
    }
    
    public void waitForShutdown() {
        if (stopping)
            while (running)
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    // stopped by server
                }
    }

}
