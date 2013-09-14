package de.metalcon.sdd.request;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONValue;

import de.metalcon.common.JsonPrettyPrinter;
import de.metalcon.common.Muid;
import de.metalcon.sdd.Detail;
import de.metalcon.sdd.IdDetail;
import de.metalcon.sdd.server.Server;

public class DeleteRequest extends Request {
    
    public DeleteRequest(Server server) {
        super(server);
    }
    
    @Override
    public Map<String, Object> run() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        
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
    public void exec() {
        Muid id = new Muid(getParam("id"));
        
        for (Detail detail : Detail.values()) {
            if (detail == Detail.NONE)
                continue;
            server.deleteEntity(new IdDetail(id, detail));
        }
        server.commitWriteBatch();
    }
    
    public static void main(String[] args) throws InterruptedException {
        Server s = new Server();
        s.start();
        
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("id", new String[]{"11233033e2b36cff"});
        DeleteRequest r = new DeleteRequest(s);
        r.setParams(params);
        String json = JSONValue.toJSONString(r.run());
        json = JsonPrettyPrinter.prettyPrintJson(json);
        System.out.println(json);
        Thread.sleep(100);
        
        s.stop();
    }
    
}
