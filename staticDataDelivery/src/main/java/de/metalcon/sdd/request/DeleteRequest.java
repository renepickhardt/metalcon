package de.metalcon.sdd.request;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONValue;

import de.metalcon.common.JsonPrettyPrinter;
import de.metalcon.common.Muid;
import de.metalcon.sdd.error.RequestMissingParamIdSddError;
import de.metalcon.sdd.server.Server;

public class DeleteRequest extends Request {
    
    String paramId;
    
    public DeleteRequest(Server server) {
        super(server);
    }
    
    @Override
    protected Map<String, Object> runHttpAction() {
        Map<String, Object> result = new HashMap<String, Object>();
        
        paramId = getParam("id");
        if (paramId == null)
            throw new RequestMissingParamIdSddError();
        
        if (server.addRequest(this)) {
            // TODO: good response
            result.put("status", "worked");
        } else {
            // TODO: bad response
            result.put("status", "didnt work");
        }
        
        return result;
    }
    
    @Override
    public void runQueueAction() {
        server.deleteEntity(new Muid(paramId));
    }
    
    public static void main(String[] args) throws InterruptedException {
        Server s = new Server();
        s.start();
        
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("id", new String[]{"ce0058ac39a33616"});
        DeleteRequest r = new DeleteRequest(s);
        r.setParams(params);
        String json = JSONValue.toJSONString(r.runHttp());
        json = JsonPrettyPrinter.prettyPrintJson(json);
        System.out.println(json);
        Thread.sleep(100);
        
        s.stop();
    }
    
}
