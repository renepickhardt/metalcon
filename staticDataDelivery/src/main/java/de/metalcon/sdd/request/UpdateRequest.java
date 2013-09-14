package de.metalcon.sdd.request;

import java.util.LinkedHashMap;
import java.util.Map;

import de.metalcon.sdd.server.Server;

public class UpdateRequest extends Request {
    
    public UpdateRequest(Server server) {
        super(server);
    }

    @Override
    public Map<String, Object> run() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        
        return result;
    }
    
    @Override
    public void exec() {
    }
    
}
