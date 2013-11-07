package de.metalcon.sdd.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.metalcon.sdd.error.InvalidAttrNameException;

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
    
    public MetaType getAttr(String name) throws InvalidAttrNameException {
        MetaType attr = attrs.get(name);
        if (attr == null)
            throw new InvalidAttrNameException();
        return attr;
    }
    
    public boolean isValidAttr(String name) {
        return attrs.containsKey(name);
    }
    
    public void addAttr(String name, String type)
    throws InvalidAttrNameException {
        addAttr(name, new MetaType(type));
    }
    
    public void addAttr(String name, MetaType type)
    throws InvalidAttrNameException {
        if (name.equals("id") ||
            name.equals("type") ||
            name.startsWith("json-"))
            throw new InvalidAttrNameException();
        attrs.put(name, type);
    }
    
    public Set<String> getOutputs() {
        return Collections.unmodifiableSet(outputs.keySet());
    }
    
    public MetaEntityOutput getOutput(String detail) {
        MetaEntityOutput output = outputs.get(detail);
        if (output == null)
            throw new RuntimeException();
        return output;
    }
    
    public void addOutput(String detail, MetaEntityOutput output) {
        outputs.put(detail, output);
    }
    
    public boolean dependsOn(String type, Set<String> modifiedDetails) {
        for (MetaEntityOutput output : outputs.values())
            for (String oattrName : output.getOattrs()) {
                String   oattrDetail = output.getOattr(oattrName);
                MetaType oattrType   = attrs.get(oattrName);
                if (type.equals(oattrType.getType())
                        && modifiedDetails.contains(oattrDetail))
                    return true;
            }
        return false;
    }
    
}
