package de.metalcon.sdd.request;

import java.util.Map;

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
    
    public abstract Map<String, Object> run();
    
    public abstract void exec();
    
}
