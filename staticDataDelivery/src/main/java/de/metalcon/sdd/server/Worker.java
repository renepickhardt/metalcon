package de.metalcon.sdd.server;

import java.util.concurrent.BlockingQueue;

import de.metalcon.sdd.request.Request;

public class Worker implements Runnable {
    
    private Thread thread;
    
    private boolean running;
    
    private boolean stopping;
    
    private BlockingQueue<Request> queue;
    
    public Worker(BlockingQueue<Request> queue) {
        running = false;
        stopping = false;
        this.queue = queue;
    }
    
    @Override
    public void run() {
        running = true;
        
        try {
            Request request = null;
            while (!stopping) {
                request = queue.take();
                request.runQueueAction();
            }
        } catch (InterruptedException e) {
            // stopped by server
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
                    // TODO: handle this
                    throw new RuntimeException();
                }
    }

}
