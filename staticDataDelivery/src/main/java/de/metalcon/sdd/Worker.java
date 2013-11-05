package de.metalcon.sdd;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import de.metalcon.sdd.queue.QueueAction;
import de.metalcon.sdd.queue.QueueActionType;

public class Worker implements Runnable {
    
    public static final int graphTransactionCount = 20;
    
    public static final int jsonTransactionCount = 20;
    
    private Sdd sdd;
    
    private Thread thread;
    
    private boolean running;
    
    private boolean stopping;
    
    private boolean busy;
    
    private QueueActionType state;
    
    private int count;
    
    private BlockingQueue<QueueAction> queue;
    
    public Worker(Sdd sdd, BlockingQueue<QueueAction> queue) {
        if (queue == null)
            throw new IllegalArgumentException("queue was null");
        
        this.sdd   = sdd;
        running    = false;
        stopping   = false;
        busy       = false;
        state      = null;
        count      = 0;
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
                    
                    QueueActionType newState = queueAction.getType();
                    if (state != newState) {
                        onStateEnd();
                        state = newState;
                        count = 0;
                    }
                    
                    onStateUpdate();
                    
                    queueAction.runQueueAction();
                    ++count;
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
        
        try {
            onStateEnd();
        } catch (IOException e) {
            throw new RuntimeException();
        }
        
        running  = false;
        stopping = false;
    }

    private void onStateUpdate() throws IOException {
        switch (state) {
            case updateGraphQueueAction:
                if (count == 0)
                    onStateStart();
                else if (count % graphTransactionCount == 0) {
                    onStateEnd();
                    onStateStart();
                }
                break;
            case updateJsonQueueAction:
                if (count == 0)
                    onStateStart();
                else if (count % jsonTransactionCount == 0) {
                    onStateEnd();
                    onStateStart();
                }
                break;
            default:
                throw new RuntimeException();
        }
    }
    
    private void onStateStart() throws IOException {
        switch (state) {
            case updateGraphQueueAction:
                sdd.startEntityGraphTransaction();
                break;
            case updateJsonQueueAction:
                sdd.startJsonDbTransaction();
                sdd.startEntityGraphTransaction();
                break;
        }
    }
    
    private void onStateEnd() throws IOException {
        if (state != null)
            switch (state) {
                case updateGraphQueueAction:
                    sdd.endEntityGraphTransaction();
                    break;
                case updateJsonQueueAction:
                    sdd.endEntityGraphTransaction();
                    sdd.endJsonDbTransaction();
                    break;
            }
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
    
    public void waitUntilQueueEmpty() throws IOException {
        while (!isIdle())
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // stopped
            }
        
        onStateEnd();
        state = null;
        count = 0;
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
