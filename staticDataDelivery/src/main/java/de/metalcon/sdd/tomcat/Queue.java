package de.metalcon.sdd.tomcat;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONValue;

import de.metalcon.sdd.Worker.QueueStatus;
import de.metalcon.sdd.queue.QueueAction;

public class Queue extends Servlet {

    private static final long serialVersionUID = 5835975500219833315L;

    @Override
    protected String run(HttpServletRequest request) {
        QueueStatus status = sdd.getQueueStatus();
        
        List<String> jsonQueue = new LinkedList<String>(); 
        for (QueueAction action : status.queue)
            jsonQueue.add(action.toString());
        
        List<String> jsonQueueDone = new LinkedList<String>();
        for (QueueAction action : status.queueDone)
            jsonQueueDone.add(action.toString());
        
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("queue",     jsonQueue);
        json.put("queueDone", jsonQueueDone);
        return JSONValue.toJSONString(json);
    }
    
}
