package de.metalcon.sdd.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.metalcon.sdd.error.InvalidConfigException;

public abstract class Config {

    private String leveldbPath;
    
    private String neo4jPath;
    
    private Set<String> details;
    
    private Map<String, MetaEntity> entities;
    
    public Config() {
        leveldbPath = null;
        neo4jPath   = null;
        details     = new HashSet<String>();
        entities    = new HashMap<String, MetaEntity>();
    }
    
    public void setLeveldbPath(String leveldbPath) {
        this.leveldbPath = leveldbPath;
    }
    
    public String getLeveldbPath() {
        return leveldbPath;
    }
    
    public void setNeo4jPath(String neo4jPath) {
        this.neo4jPath = neo4jPath;
    }
    
    public String getNeo4jPath() {
        return neo4jPath;
    }
    
    public String getIdDelimeter() {
        // TODO: move into config file
        return ",";
    }
    
    public String getIdDetailDelimeter() {
        // TODO: move into config file
        return ":";
    }
    
    public Set<String> getDetails() {
        return Collections.unmodifiableSet(details);
    }
    
    public boolean isValidDetail(String detail) {
        return details.contains(detail);
    }
    
    public void addDetail(String detail) {
        details.add(detail);
    }
    
    public Set<String> getEntities() {
        return Collections.unmodifiableSet(entities.keySet());
    }
    
    /**
     * @return Returns the MetaEntity for type or Null if no entity with that
     *         type was configured.
     */
    public MetaEntity getEntity(String type) {
        return entities.get(type);
    }
    
    public boolean isValidEntity(String type) {
        return entities.containsKey(type);
    }
    
    public void addEntity(String type, MetaEntity entity) {
        entities.put(type, entity);
    }
    
    public void validate() throws InvalidConfigException {
        if (getLeveldbPath() == null)
            throw new InvalidConfigException("no Leveldb path");
        
        if (getNeo4jPath() == null)
            throw new InvalidConfigException("no Neo4j path");
        
        for (String type : getEntities()) {
            MetaEntity entity = getEntity(type);
            for (String attrName : entity.getAttrs()) {
                MetaType attrType = entity.getAttr(attrName);
                
                if (!attrType.isPrimitive() &&
                    !isValidEntity(attrType.getType()))
                    throw new InvalidConfigException("invalid attr type \"" + attrType.getType() + "\" for attr \"" + attrName + "\" on entity type \"" + type + "\"");
            }
            
            for (Map.Entry<String, MetaEntityOutput> output :
                    entity.output.entrySet()) {
                String           outputDetail = output.getKey();
                MetaEntityOutput outputObj    = output.getValue();
                
                if (!isValidDetail(outputDetail))
                    throw new InvalidConfigException("invalid output detail \"" + outputDetail + "\" on entity type \"" + type + "\"");
                
                for (Map.Entry<String, String> oattr :
                        outputObj.oattrs.entrySet()) {
                    String   attr     = oattr.getKey();
                    String   detail   = oattr.getValue();
                    MetaType attrType = entity.getAttr(attr);
                    
                    if (attrType == null)
                        throw new InvalidConfigException("oattr for invalid attr \"" + attr + "\" on entity type \"" + type + "\"");
                    
                    if (attrType.isPrimitive()) {
                        if (!detail.equals(""))
                            throw new InvalidConfigException("oattr for primitive attr \"" + attr + "\" has detail on entity type \"" + type + "\"");
                    } else if (!isValidDetail(detail))
                        throw new InvalidConfigException("oattr for non primitive attr \"" + attr + "\" has no detail on entity type \"" + type + "\"");
                }
            }
        }
    }
    
}
