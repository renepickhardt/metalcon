package de.metalcon.sdd.request;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONValue;

import de.metalcon.common.JsonPrettyPrinter;
import de.metalcon.sdd.entity.Entity;
import de.metalcon.sdd.error.RequestMissingParamIdSddError;
import de.metalcon.sdd.error.RequestMissingParamTypeSddError;
import de.metalcon.sdd.server.Server;
import static de.metalcon.sdd.entity.EntityByType.newEntityByType;

public class UpdateRequest extends Request {
    
    String paramId;
    
    String paramType;
    
    public UpdateRequest(Server server) {
        super(server);
    }

    @Override
    protected Map<String, Object> runHttpAction() {
        Map<String, Object> result = new HashMap<String, Object>();
        
        paramId = getParam("id");
        if (paramId == null)
            throw new RequestMissingParamIdSddError();
        paramType = getParam("type");
        if (paramType == null)
            throw new RequestMissingParamTypeSddError();
        
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
        Entity entity = newEntityByType(paramType, server);
//        Entity entity = Entity.newEntityByType(id.getType(), server);
        entity.loadFromUpdateParams(params);;
        server.writeEntity(entity);
    }
    
    public static void main(String[] args) throws InterruptedException {
        Server s = new Server();
        s.start();
        
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("id",        new String[]{"ce0058ac39a33616"});
        params.put("type",      new String[]{"Band"});
        params.put("musicians", new String[]{""});
        UpdateRequest r = new UpdateRequest(s);
        r.setParams(params);
        String json = JSONValue.toJSONString(r.runHttp());
        json = JsonPrettyPrinter.prettyPrintJson(json);
        System.out.println(json);
        Thread.sleep(100);
        
        s.stop();
    }
    
}
