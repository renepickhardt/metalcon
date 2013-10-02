package de.metalcon.common;

import org.json.simple.JSONAware;

/**
 * Allows to add Strings, that already contain JSON-encoded data, to a
 * JSONObject without having to decode/reencode their contents.
 */
public class JsonString implements JSONAware {
    
    private String string;
    
    public JsonString(String string) {
        if (string == null)
            // TODO: handle this
            throw new RuntimeException();
        
        this.string = string;
    }
    
    @Override
    public String toJSONString() {
        return string;
    }
    
    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        JsonString o = (JsonString) other;
        return string.equals(o.string);
    }
    
    @Override
    public int hashCode() {
        int hash = 92352;
        int mult = 719;
        
        hash = hash*mult + string.hashCode();
        
        return hash;
    }
    
}
