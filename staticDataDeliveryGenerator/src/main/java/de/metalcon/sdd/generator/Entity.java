package de.metalcon.sdd.generator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Entity {

    private String name;
    
    private List<EntityAttribute> attrs;
    
    private Map<Detail, EntityOutput> outputs;
    
    public Entity(String name) {
        this.name = name;
        attrs     = new LinkedList<EntityAttribute>();
        outputs   = new HashMap<Detail, EntityOutput>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void addAttribute(EntityAttribute attr) {
        attrs.add(attr);
    }
    
    public List<EntityAttribute> getAttributes() {
        return attrs;
    }
    
    public void addOutput(Detail detail, EntityOutput output) {
        outputs.put(detail, output);
    }
    
    public EntityOutput getOutput(Detail detail) {
        return outputs.get(detail);
    }
    
}
