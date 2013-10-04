package de.metalcon.sdd.config;

import java.util.HashMap;
import java.util.Map;

public class MetaEntity {
    
    public Map<String, MetaType> attrs;
    
    public Map<String, MetaEntityOutput> output;
    
    public MetaEntity() {
        attrs  = new HashMap<String, MetaType>();
        output = new HashMap<String, MetaEntityOutput>();
    }
    
}
