package de.metalcon.sdd.request;

import java.util.Map;

import de.metalcon.sdd.server.Server;

public abstract class Request {
    
    protected Server server;
    
    protected Map<String, String[]> params;

    public Request(Server server) {
        this.server = server;
    }
    
    public void setParams(Map<String, String[]> params) {
        this.params = params;
    }
    
    protected String getParam(String key) {
        String[] vals = params.get(key);
        if (vals != null) {
            String val = vals[0];
            if (val != null)
                return val;
        }
        
        // TODO: handle this
        throw new RuntimeException();
    }
    
    public abstract Map<String, Object> run();
    
    public abstract void exec();
    
}
