package de.metalcon.sdd.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MetaEntityOutput {

    private Map<String, String> oattrs;
    
    public MetaEntityOutput() {
        oattrs = new HashMap<String, String>();
    }
    
    public Set<String> getOattrs() {
        return Collections.unmodifiableSet(oattrs.keySet());
    }
    
    public String getOattr(String attr) {
        return oattrs.get(attr);
    }
    
    public void addOattr(String attr, String detail) {
        oattrs.put(attr, detail);
    }
    
}
