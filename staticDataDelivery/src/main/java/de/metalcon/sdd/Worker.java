package de.metalcon.sdd;

import java.util.concurrent.BlockingQueue;

import de.metalcon.sdd.queue.QueueAction;

public class Worker implements Runnable {
    
    private Thread thread;
    
    private boolean running;
    
    private boolean stopping;
    
    private boolean busy;
    
    private BlockingQueue<QueueAction> queue;
    
    public Worker(BlockingQueue<QueueAction> queue) {
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
            QueueAction queueAction = null;
            while (!stopping) {
                try {
                    busy = false;
                    queueAction = queue.take();
                    busy = true;
                    queueAction.runQueueAction();
                } catch(InterruptedException e) {
                    throw e;
                } catch(Exception e) {
                    // An error in a request does not terminate the server.
                    // TODO: somehow log the error.
                    e.printStackTrace();
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
    
    public boolean isIdle() {
        return !busy && queue.isEmpty();
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
