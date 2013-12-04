package de.metalcon.middleware.util.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;

public class RequestManager {
    
    @Autowired
    @Qualifier("requestManager")
    private TaskExecutor taskExecutor;
    
    public RequestTransaction startTransaction() {
        RequestTransaction tx = new RequestTransaction(this);
        return tx;
    }
    
    /* package */ void run(Runnable runnable) {
        taskExecutor.execute(runnable);
    }

}
