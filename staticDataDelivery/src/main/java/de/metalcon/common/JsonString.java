package de.metalcon.common;

import org.json.simple.JSONAware;

/**
 * Allows to add Strings, that already contain JSON-encoded data, to a
 * JSONObject without having to decode/reencode their contents.
 */
public class JsonString implements JSONAware {
    
    String string;
    
    public JsonString(String string) {
        this.string = string;
    }
    
    @Override
    public String toJSONString() {
        return string;
    }
    
}
