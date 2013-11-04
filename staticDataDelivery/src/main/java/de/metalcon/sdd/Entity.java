package de.metalcon.sdd;

import java.util.Map;

import org.neo4j.graphdb.Node;

import de.metalcon.sdd.config.Config;
import de.metalcon.sdd.config.MetaEntity;
import de.metalcon.sdd.error.InconsitentTypeException;
import de.metalcon.sdd.error.InvalidTypeException;

public class Entity {
    
    private Config config;
    
    private long id;
    
    private String type;
    
    private MetaEntity metaEntity;
    
    private Node node;
    
    public Entity(Config config, long id, String type)
    throws InvalidTypeException {
        if (config == null)
            throw new IllegalArgumentException("config was null");
        if (type == null)
            throw new IllegalArgumentException("type was null");
        
        this.config = config;
        
        this.id = id;
        
        if (!config.isValidEntityType(type))
            throw new InvalidTypeException();
            
        this.type = type;
    }
    
    public Entity(Config config, Node node)
    throws InconsitentTypeException, InvalidTypeException {
        if (config == null)
            throw new IllegalArgumentException("config was null");
        if (node == null)
            throw new IllegalArgumentException("node was null");
        
        this.config = config;
        
        Long id = (Long) node.getProperty("id", null);
        if (id == null)
            throw new RuntimeException();
        this.id = id;
        
        type = (String) node.getProperty("type", null);
        if (type == null)
            throw new RuntimeException();
        if (!config.isValidEntityType(type))
            throw new InvalidTypeException();
        
        setNode(node);
    }
    
    public long getId() {
        return id;
    }
    
    public String getType() {
        if (type == null)
            throw new RuntimeException();
        return type;
    }
    
    public MetaEntity getMetaEntity()
    throws InvalidTypeException {
        if (metaEntity == null) {
            metaEntity = config.getEntity(getType());
            if (metaEntity == null)
                throw new InvalidTypeException();
        }
        return metaEntity;
    }
    
    public void setNode(Node node)
    throws InconsitentTypeException {
        if (node == null)
            throw new RuntimeException();
        if (!type.equals(node.getProperty("type", null)))
            throw new InconsitentTypeException();
        
        this.node = node;
    }
    
    public Node getNode() {
        if (node == null)
            throw new RuntimeException();
        return node;
    }
    
    public void setAttrs(Map<String, String> attrs) {
        for (Map.Entry<String, String> attr : attrs.entrySet()) {
            String attrName  = attr.getKey();
            String attrValue = attr.getValue();
            node.setProperty(attrName, attrValue);
        }
    }
    
}
