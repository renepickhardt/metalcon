package de.metalcon.sdd.request;

import java.util.HashMap;
import java.util.Map;

import de.metalcon.sdd.error.SddError;
import de.metalcon.sdd.server.Server;
import de.metalcon.sdd.tomcat.Servlet;

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
        return getParam(key, false);
    }
    
    protected String getParam(String key, boolean optional) {
        return Servlet.getParam(params, key, optional);
    }
    
    public Map<String, Object> runHttp() {
        try {
            return runHttpAction();
        } catch(SddError e) {
            e.print();
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("error", e.toJson());
            return result;
        }
    }
    
    protected abstract Map<String, Object> runHttpAction();
    
    public abstract void runQueueAction();
    
}
