package de.metalcon.sdd.error;

import java.util.HashMap;
import java.util.Map;

public abstract class SddError extends RuntimeException {

    private static final long serialVersionUID = -4717969086691575163L;

    public abstract String getDescription();
    
    public abstract String getSuggestion();
    
    public Map<String, String> toJson() {
        Map<String, String> json = new HashMap<String, String>();
        json.put("type",    "error");
        json.put("id",      getClass().getName());
        json.put("desc",    getDescription());
        json.put("suggest", getSuggestion());
        return json;
    }

}