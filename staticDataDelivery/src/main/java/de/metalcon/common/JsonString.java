package de.metalcon.common;

import org.json.simple.JSONAware;

/**
 * Allows to add Strings, that already contain JSON-encoded data, to a
 * JSONObject without having to decode/reencode their contents.
 */
public class JsonString implements JSONAware {
    
    private String string;
    
    public JsonString(String string) {
        this.string = string;
    }
    
    @Override
    public String toJSONString() {
        return string;
    }
    
    @Override
    public boolean equals(Object other) {
        if (other == this)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;
        JsonString o = (JsonString) other;
        if (string == null)
            return o.string == null;
        else
            return string.equals(o.string);
    }
    
    @Override
    public int hashCode() {
        int hash = 92352;
        int mult = 719;
        
        if (string == null)
            hash = hash*mult;
        else
            hash = hash*mult + string.hashCode();
        
        return hash;
    }
    
}
