package de.metalcon.middleware.util.request;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;

public class RequestManager {
    
    private ConcurrentMap<RequestTransaction, Set<Request>> requests;
    
    private ConcurrentMap<RequestTransaction, BlockingQueue<Object>> recieved;
    
    @Autowired
    @Qualifier("requestManager")
    private TaskExecutor taskExecutor;
    
    public RequestManager() {
        requests = new ConcurrentHashMap<RequestTransaction,
                                         Set<Request>>();
        recieved = new ConcurrentHashMap<RequestTransaction,
                                         BlockingQueue<Object>>();
    }

    public RequestTransaction startTransaction() {
        RequestTransaction tx = new RequestTransaction(this);
        requests.put(tx, Collections
                .newSetFromMap(new ConcurrentHashMap<Request, Boolean>()));
        recieved.put(tx, new LinkedBlockingQueue<Object>());
        return tx;
    }
    
    private void stopTransaction(RequestTransaction tx) {
        requests.remove(tx);
        recieved.remove(tx);
    }
    
    /* package */ void request(RequestTransaction tx, Request request) {
        requests.get(tx).add(request);
        taskExecutor.execute(new RequestRunner(tx, request));
    }
    
    /* package */ Object recieve(RequestTransaction tx) {
        Set<Request>          txRequests = requests.get(tx);
        BlockingQueue<Object> txRecieved = recieved.get(tx);
        
        if (txRecieved.isEmpty() && txRequests.isEmpty()) {
            stopTransaction(tx);
            return null;
        }
        
        try {
            return txRecieved.take();
        } catch (InterruptedException e) {
            // Cancelled
            return null;
        }
    }
    
    private class RequestRunner implements Runnable {
        
        private RequestTransaction tx;
        
        private Request request;

        private RequestRunner(RequestTransaction tx, Request request) {
            this.tx      = tx;
            this.request = request;
        }
        
        @Override
        public void run() {
            Object result = request.run();
            recieved.get(tx).add(result);
            requests.get(tx).remove(request);
            System.out.println(result.toString());
        }
        
    }

}
