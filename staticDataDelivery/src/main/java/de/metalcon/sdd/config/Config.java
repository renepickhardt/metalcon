package de.metalcon.sdd.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Config {

    protected String leveldbPath;
    
    protected String neo4jPath;
    
    protected Set<String> details;
    
    protected Map<String, MetaEntity> entities;
    
    public Config() {
        leveldbPath = null;
        neo4jPath   = null;
        details     = new HashSet<String>();
        entities    = new HashMap<String, MetaEntity>();
    }
    
    public String getLeveldbPath() {
        return leveldbPath;
    }
    
    public String getNeo4jPath() {
        return neo4jPath;
    }
    
    public String getIdDetailDelimeter() {
        // TODO: move into config file
        return ":";
    }
    
    public boolean isPrioritizedQueue() {
        // TODO: move into config file
        return true;
    }
    
    public boolean isValidDetail(String detail) {
        return details.contains(detail);
    }
    
    public void addDetail(String detail) {
        details.add(detail);
    }
    
    public boolean isValidEntity(String type) {
        return entities.containsKey(type);
    }
    
}
