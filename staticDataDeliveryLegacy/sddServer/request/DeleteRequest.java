package de.metalcon.sddServer.request;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONValue;

import de.metalcon.common.JsonPrettyPrinter;
import de.metalcon.common.Muid;
import de.metalcon.sdd.queue.DeleteQueueAction;
import de.metalcon.sddServer.server.Server;

public class DeleteRequest extends Request {
    
    public DeleteRequest(Server server) {
        super(server);
    }
    
    @Override
    protected Map<String, Object> runHttpAction() {
        Map<String, Object> result = new HashMap<String, Object>();
        
        String paramId = getParam("id");
        if (paramId == null)
            // TODO: handle this
            throw new RuntimeException();
        Muid id = new Muid(paramId);
        
        DeleteQueueAction action = new DeleteQueueAction(server, id);
        
        if (server.queueAction(action)) {
            // TODO: good response
            result.put("status", "worked");
        } else {
            // TODO: bad response
            result.put("status", "didnt work");
        }
        
        return result;
    }
    
    public static void main(String[] args) throws InterruptedException {
        Server s = new Server();
        s.start();
        
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("id", new String[]{"ec94771fed93cffc"});
        DeleteRequest r = new DeleteRequest(s);
        r.setParams(params);
        String json = JSONValue.toJSONString(r.runHttp());
        json = JsonPrettyPrinter.prettyPrintJson(json);
        System.out.println(json);
        
        s.waitUntilQueueEmpty();
        s.stop();
    }
    
}
