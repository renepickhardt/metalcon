package de.metalcon.sdd.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MetaEntity {
    
    private Map<String, MetaType> attrs;
    
    private Map<String, MetaEntityOutput> outputs;
    
    public MetaEntity() {
        attrs   = new HashMap<String, MetaType>();
        outputs = new HashMap<String, MetaEntityOutput>();
    }
    
    public Set<String> getAttrs() {
        return Collections.unmodifiableSet(attrs.keySet());
    }
    
    public MetaType getAttr(String name) {
        return attrs.get(name);
    }
    
    public boolean isValidAttr(String name) {
        return attrs.containsKey(name);
    }
    
    public void addAttr(String name, MetaType type) {
        attrs.put(name, type);
    }
    
    public Set<String> getOutputs() {
        return Collections.unmodifiableSet(outputs.keySet());
    }
    
    public MetaEntityOutput getOutput(String detail) {
        return outputs.get(detail);
    }
    
    public void addOutput(String detail, MetaEntityOutput output) {
        outputs.put(detail, output);
    }
    
}
