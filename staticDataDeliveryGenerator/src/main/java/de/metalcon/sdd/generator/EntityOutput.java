package de.metalcon.sdd.generator;

import java.util.HashMap;
import java.util.Map;

public class EntityOutput {
    
    private Map<EntityAttribute, Detail> oattrs;
    
    public EntityOutput() {
        oattrs = new HashMap<EntityAttribute, Detail>();
    }
    
    public void addOattrs(EntityAttribute attr, Detail detail) {
        oattrs.put(attr, detail);
    }
    
    public Map<EntityAttribute, Detail> getOattrs() {
        return oattrs;
    }
    
}
