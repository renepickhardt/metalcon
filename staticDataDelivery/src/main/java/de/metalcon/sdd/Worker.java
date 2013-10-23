package de.metalcon.sdd;

import java.util.concurrent.BlockingQueue;

import de.metalcon.sdd.queue.QueueAction;
import de.metalcon.sddServer.error.SddError;

public class Worker<T> implements Runnable {
    
    private Thread thread;
    
    private boolean running;
    
    private boolean stopping;
    
    private boolean busy;
    
    private BlockingQueue<QueueAction<T>> queue;
    
    public Worker(BlockingQueue<QueueAction<T>> queue) {
        if (queue == null)
            throw new IllegalArgumentException("queue was null");
        
        running    = false;
        stopping   = false;
        busy       = false;
        this.queue = queue;
    }
    
    @Override
    public void run() {
        running = true;
        
        try {
            QueueAction<T> queueAction = null;
            while (!stopping) {
                try {
                    busy = false;
                    queueAction = queue.take();
                    busy = true;
                    queueAction.runQueueAction();
                } catch(SddError e) {
                    // A single error in a request does not terminate the
                    // server.
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
    
    public boolean isBusy() {
        return busy && queue.isEmpty();
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
