package de.metalcon.middleware.util.request;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class RequestTransaction {

    private RequestManager requestManager;
    
    private Set<Request> requests;
    
    private BlockingQueue<Object> recieved;
    
    /* package */ RequestTransaction(RequestManager requestManager) {
        this.requestManager = requestManager;
        requests = Collections
                .newSetFromMap(new ConcurrentHashMap<Request, Boolean>());
        recieved = new LinkedBlockingQueue<Object>();
    }

    public void request(Request request) {
        requests.add(request);
        requestManager.run(new RequestRunner(request));
    }
    
    public Object recieve() {
        if (requests.isEmpty() && recieved.isEmpty())
            return null;
        
        try {
            return recieved.take();
        } catch (InterruptedException e) {
            // Interrupted.
            return null;
        }
    }
    
    private class RequestRunner implements Runnable {
        
        private Request request;

        private RequestRunner(Request request) {
            this.request = request;
        }
        
        @Override
        public void run() {
            Object result = request.run();
            recieved.add(result);
            requests.remove(request);
        }
        
    }

}
