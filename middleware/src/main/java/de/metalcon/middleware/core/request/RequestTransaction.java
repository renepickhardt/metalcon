package de.metalcon.middleware.core.request;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class RequestTransaction {

    @Autowired
    private TaskExecutor taskExecutor;

    private Set<Request> requests;

    private BlockingQueue<Object> recieved;

    public RequestTransaction() {
        requests =
                Collections
                        .newSetFromMap(new ConcurrentHashMap<Request, Boolean>());
        recieved = new LinkedBlockingQueue<Object>();
    }

    public void request(Request request) {
        requests.add(request);
        taskExecutor.execute(new RequestRunner(request));
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

        private RequestRunner(
                Request request) {
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
